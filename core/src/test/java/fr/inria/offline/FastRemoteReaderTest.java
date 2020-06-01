package fr.inria.offline;

import com.google.common.collect.BiMap;
import fr.inria.align.treediff.FastRemoteReader;
import fr.inria.yajta.api.MalformedTrackingClassException;
import fr.inria.yajta.api.SimpleTracerTest;
import fr.inria.yajta.api.loggerimplem.TestFastLogger;
import fr.inria.yajta.processor.loggers.FastRemoteLogger;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class FastRemoteReaderTest {

	@Test
	public void testProduceRemoteTraceThenReadTrace() throws MalformedTrackingClassException, InterruptedException {
		File tmpTrace = new File("tmpTrace");
		if(tmpTrace.exists()) tmpTrace.delete();
		tmpTrace.mkdir();
		//new File(tmpTrace, "trace").mkdir();
		//Initialization
		File classDir = new File(SimpleTracerTest.class.getClassLoader().getResource("classes-remote-branch").getPath());

		FastRemoteLogger logger = FastRemoteLogger.getInstance();
		logger.traceBranch = true;
		logger.setLogFile(tmpTrace);



		InstrumentationBuilder builder = new InstrumentationBuilder(classDir, FastRemoteLogger.class);
		//InstrumentationBuilder builder = new InstrumentationBuilder(classDir, Logger.class);

		//Instrument bytecode of class in classDir
		builder.instrument();

		//Run the instrumented code from fr.inria.hellobranch.AppBranch()
		builder.setEntryPoint("fr.inria.hellobranchr.AppBranch", "main", String[].class);
		builder.runInstrumented((Object) new String[]{"Input"});

		//Send en signal, as the jvm will not stop, we need to end capture
		logger.flush();

		TestFastLogger.traceBranch = true;
		TestFastLogger.getInstance().logs.clear();
		FastRemoteReader reader = new FastRemoteReader(TestFastLogger.getInstance(),tmpTrace);

		//Reconstitute logs
		reader.read();

		//Check that the logs collected are consistent with what was expected
		List<TestFastLogger.Log> logs = TestFastLogger.getInstance().logs;
		BiMap<Integer, String> dico = TestFastLogger.getInstance().getDictionary().inverse();


		//contract: Every method and each branch is indeed logged (in and out)
		//assertTrue(logs.size() == 97);
		assertTrue(logs.size() == 160);

		//contract: Every method logged in is also logged out
		/*assertEquals(
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.IN && !l.isBranch(dico)).count(),
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.OUT).count()
		);*/
		assertEquals(
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.IN).count(),
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.OUT).count()
		);

		//contract: every branch entered is logged
		assertTrue(logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.IN && l.isBranch(dico)).count() == 63);

		builder.close();

	}

	@Test
	public void testReadTrace() throws InterruptedException {
		File traceDir = new File(FastRemoteReaderTest.class.getClassLoader().getResource("remote").getPath());
		TestFastLogger.traceBranch = true;
		TestFastLogger.getInstance().logs.clear();
		FastRemoteReader reader = new FastRemoteReader(TestFastLogger.getInstance(),traceDir);

		//Reconstitute logs
		reader.read();

		//Check that the logs collected are consistent with what was expected
		List<TestFastLogger.Log> logs = TestFastLogger.getInstance().logs;
		BiMap<Integer, String> dico = TestFastLogger.getInstance().getDictionary().inverse();


		//contract: Every method and each branch is indeed logged (in and out)
		assertTrue(logs.size() == 97);

		//contract: Every method logged in is also logged out
		assertEquals(
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.IN && !l.isBranch(dico)).count(),
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.OUT).count()
		);

		//contract: every branch entered is logged
		assertTrue(logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.IN && l.isBranch(dico)).count() == 63);

	}

}
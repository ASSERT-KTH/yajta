package se.kth.castor.offline;

import com.google.common.collect.BiMap;
import org.junit.Ignore;
import org.junit.Test;
import se.kth.castor.align.treediff.FastRemoteReader;
import se.kth.castor.yajta.api.MalformedTrackingClassException;
import se.kth.castor.yajta.api.SimpleTracerTest;
import se.kth.castor.yajta.api.loggerimplem.TestFastLogger;
import se.kth.castor.yajta.processor.loggers.FastRemoteLogger;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FastRemoteReaderTest {

	@Test
	public void testProduceRemoteTraceThenReadTrace() throws MalformedTrackingClassException, InterruptedException {
		File tmpTrace = new File("FastRemoteReaderTest-tmpTrace");
		if(tmpTrace.exists()) tmpTrace.delete();
		tmpTrace.mkdir();

		//Initialization
		System.out.println("Reading bytecode from dir: " + SimpleTracerTest.class.getClassLoader().getResource("classes-remote-branch").getPath());
		File classDir = new File(FastRemoteReaderTest.class.getClassLoader().getResource("classes-remote-branch").getPath());

		FastRemoteLogger logger = FastRemoteLogger.getInstance();
		logger.purge();
		logger.traceBranch = true;
		logger.setLogFile(tmpTrace);



		InstrumentationBuilder builder = new InstrumentationBuilder(classDir, FastRemoteLogger.class);

		//Instrument bytecode of class in classDir
		builder.instrument();

		//Run the instrumented code from fr.inria.hellobranch.AppBranch()
		builder.setEntryPoint("fr.inria.hellobranchr.AppBranch", "main", String[].class);
		builder.runInstrumented((Object) new String[]{"Input"});

		//Send en signal, as the jvm will not stop, we need to end capture
		logger.flush();

		TestFastLogger.traceBranch = true;
		TestFastLogger.getInstance().logs.clear();
		TestFastLogger.getInstance().getDictionary().clear();

		FastRemoteReader reader = new FastRemoteReader(TestFastLogger.getInstance(),tmpTrace);

		//Reconstitute logs
		reader.read();

		//Check that the logs collected are consistent with what was expected
		List<TestFastLogger.Log> logs = TestFastLogger.getInstance().logs;
		BiMap<Integer, String> dico = TestFastLogger.getInstance().getDictionary().inverse();

		/*for(Map.Entry<String, Integer> entry: logger.getDico().entrySet()) {
			System.out.println("Dico sent: " + entry.getKey() + ": " + entry.getValue());
		}
		for(Map.Entry<String, Integer> entry: TestFastLogger.getInstance().getDictionary().entrySet()) {
			System.out.println("Dico read: " + entry.getKey() + ": " + entry.getValue());
		}*/

		//contract: the original dictionary and the dictionary read must contains the same elements
		assertEquals(logger.getDico().size(), TestFastLogger.getInstance().getDictionary().size());
		for(Map.Entry<String, Integer> entry: logger.getDico().entrySet()) {
			assertTrue(TestFastLogger.getInstance().getDictionary().containsKey(entry.getKey()));
			assertEquals(entry.getValue(), TestFastLogger.getInstance().getDictionary().get(entry.getKey()));
		}

		/*for(TestFastLogger.Log log: logs) {
			System.out.println((log.type == TestFastLogger.LOGTYPE.IN ? "IN" : "OUT" ) + "-> " + log.getElementName(dico));
		}*/


		//contract: Every method and each branch is indeed logged (in and out)
		assertEquals(160, logs.size());

		//contract: Every method logged in is also logged out
		assertEquals(
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.IN).count(),
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.OUT).count()
		);

		assertEquals(
				7,
				logs.stream().filter(l ->
						l.type == TestFastLogger.LOGTYPE.IN &&
								l.getElementName(dico).startsWith("fr.inria.hellobranchr.AppBranch.myIf(")
				).count()
		);

		assertEquals(
				28,
				logs.stream().filter(l ->
						l.type == TestFastLogger.LOGTYPE.IN &&
								l.getElementName(dico).startsWith("fr.inria.hellobranchr.AppBranch.myIfElse(")
				).count()
		);

		assertEquals(
				11,
				logs.stream().filter(l ->
						l.type == TestFastLogger.LOGTYPE.IN &&
								l.getElementName(dico).startsWith("fr.inria.hellobranchr.AppBranch.mySwitch(")
				).count()
		);

		assertEquals(
				8,
				logs.stream().filter(l ->
						l.type == TestFastLogger.LOGTYPE.IN &&
								l.getElementName(dico).startsWith("fr.inria.hellobranchr.AppBranch.myFor(")
				).count()
		);

		assertEquals(
				8,
				logs.stream().filter(l ->
						l.type == TestFastLogger.LOGTYPE.IN &&
								l.getElementName(dico).startsWith("fr.inria.hellobranchr.AppBranch.myWhile(")
				).count()
		);

		assertEquals(
				5,
				logs.stream().filter(l ->
						l.type == TestFastLogger.LOGTYPE.IN &&
								l.getElementName(dico).startsWith("fr.inria.hellobranchr.AppBranch.myDoWhile(")
				).count()
		);

		assertEquals(
				8,
				logs.stream().filter(l ->
						l.type == TestFastLogger.LOGTYPE.IN &&
								l.getElementName(dico).startsWith("fr.inria.hellobranchr.AppBranch.myForeach(")
				).count()
		);

		assertEquals(
				3,
				logs.stream().filter(l ->
						l.type == TestFastLogger.LOGTYPE.IN &&
								l.getElementName(dico).startsWith("fr.inria.hellobranchr.AppBranch.myTry(")
				).count()
		);


		//contract: every branch entered is logged
		assertEquals(63, logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.IN && l.isBranch(dico)).count());

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
package fr.inria.yajta.api;

import com.google.common.collect.BiMap;
import fr.inria.offline.InstrumentationBuilder;
import fr.inria.yajta.api.loggerimplem.TestFastLogger;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FastTracerTest {

	@Test
	public void testProbesInsertion() throws MalformedTrackingClassException {
		File classDir = new File(SimpleTracerTest.class.getClassLoader().getResource("classes-fast").getPath());
		TestFastLogger.traceBranch = false;
		TestFastLogger.getInstance().logs.clear();
		InstrumentationBuilder builder = new InstrumentationBuilder(classDir, TestFastLogger.class);
		builder.instrument();
		builder.setEntryPoint("fr.inria.helloworldf.App", "main", String[].class);
		builder.runInstrumented((Object) new String[]{""});

		List<TestFastLogger.Log> logs = TestFastLogger.getInstance().logs;
		//Every method is indeed logged (in and out)
		assertTrue(logs.size() == 36);
		//Every method logged in is also logged out
		assertEquals(logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.IN).count(),
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.OUT).count()
		);

		BiMap<Integer, String> dico = TestFastLogger.getInstance().getDico().inverse();

		//First method logged is "main", "fr.inria.helloworld.App", "main(java.lang.String[])"
		assertEquals("fr.inria.helloworldf.App.main(java.lang.String[])", logs.get(0).getElementName(dico));
		builder.close();
	}



	@Test
	public void testBranchProbesInsertion() throws MalformedTrackingClassException {
		//Initialization
		File classDir = new File(SimpleTracerTest.class.getClassLoader().getResource("classes-fast-branch").getPath());
		TestFastLogger.traceBranch = true;
		TestFastLogger.getInstance().logs.clear();
		InstrumentationBuilder builder = new InstrumentationBuilder(classDir, TestFastLogger.class);
		//InstrumentationBuilder builder = new InstrumentationBuilder(classDir, Logger.class);

		//Instrument bytecode of class in classDir
		builder.instrument();

		//Run the instrumented code from fr.inria.hellobranch.AppBranch()
		builder.setEntryPoint("fr.inria.hellobranchf.AppBranch", "main", String[].class);
		builder.runInstrumented((Object) new String[]{"Input"});

		//Check that the logs collected are consistent with what was expected
		List<TestFastLogger.Log> logs = TestFastLogger.getInstance().logs;
		BiMap<Integer, String> dico = TestFastLogger.getInstance().getDico().inverse();


		//contract: Every method and each branch is indeed logged (in and out)
		assertTrue(logs.size() == 97);

		//contract: Every method logged in is also logged out
		assertEquals(
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.IN && !l.isBranch(dico)).count(),
				logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.OUT).count()
		);

		//contract: every branch entered is logged
		assertTrue(logs.stream().filter(l -> l.type == TestFastLogger.LOGTYPE.IN && l.isBranch(dico)).count() == 63);

		builder.close();
	}
}

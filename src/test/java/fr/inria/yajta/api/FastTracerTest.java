package fr.inria.yajta.api;

import fr.inria.offline.InstrumentationBuilder;
import fr.inria.yajta.api.loggerimplem.TestLogger;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FastTracerTest {

	@Test
	public void testProbesInsertion() throws MalformedTrackingClassException {
		/*File classDir = new File(SimpleTracerTest.class.getClassLoader().getResource("classes").getPath());
		InstrumentationBuilder builder = new InstrumentationBuilder(classDir, TestLogger.class);
		builder.instrument();
		builder.setEntryPoint("fr.inria.helloworld.App", "main", String[].class);
		builder.runInstrumented((Object) new String[]{""});

		List<TestLogger.Log> logs = TestLogger.getInstance().log;
		//Every method is indeed logged (in and out)
		assertTrue(logs.size() == 22);
		//Every method logged in is also logged out
		assertEquals(logs.stream().filter(l -> l.type == TestLogger.LOGTYPE.IN).count(),
				logs.stream().filter(l -> l.type == TestLogger.LOGTYPE.OUT).count()
		);
		//First method logged is "main", "fr.inria.helloworld.App", "main(java.lang.String[])"
		assertEquals(logs.get(0).thread,"main");
		assertEquals(logs.get(0).clazz,"fr.inria.helloworld.App");
		assertEquals(logs.get(0).method,"main(java.lang.String[])");
		builder.close();*/
	}
}

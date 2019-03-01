package fr.inria.yajta.api;

import fr.inria.offline.InstrumentationBuilder;
import fr.inria.yajta.api.loggerimplem.TestFastLogger;

import java.io.File;
import java.util.List;

public class TestLoggerUtils {
	public static InstrumentationBuilder instrumentAndRun(String classDirPath, String entryClass, String entryMethod, List<TestFastLogger.Log> logsToFill) throws MalformedTrackingClassException {
		TestFastLogger.getInstance().logs.clear();

		File classDir = new File(SimpleTracerTest.class.getClassLoader().getResource(classDirPath).getPath());
		InstrumentationBuilder builder = new InstrumentationBuilder(classDir, TestFastLogger.class);
		builder.instrument();
		builder.setEntryPoint(entryClass, entryMethod, String[].class);
		builder.runInstrumented((Object) new String[]{""});

		logsToFill.addAll(TestFastLogger.getInstance().logs);
		return builder;
	}
}

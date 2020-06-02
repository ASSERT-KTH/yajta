package se.kth.castor.yajta.api;

import se.kth.castor.offline.InstrumentationBuilder;
import se.kth.castor.yajta.api.loggerimplem.TestFastLogger;

import java.io.File;
import java.util.List;

public class TestLoggerUtils {
	public static InstrumentationBuilder instrumentAndRun(
			String classDirPath,
			String entryClass,
			String entryMethod,
			List<TestFastLogger.Log> logsToFill
	) throws MalformedTrackingClassException {

		TestFastLogger.getInstance().logs.clear();

		File classDir = new File(TestLoggerUtils.class.getClassLoader().getResource(classDirPath).getPath());
		InstrumentationBuilder builder = new InstrumentationBuilder(classDir, TestFastLogger.class);
		builder.instrument();
		builder.setEntryPoint(entryClass, entryMethod, String[].class);
		builder.runInstrumented((Object) new String[]{""});

		logsToFill.addAll(TestFastLogger.getInstance().logs);
		return builder;
	}
}

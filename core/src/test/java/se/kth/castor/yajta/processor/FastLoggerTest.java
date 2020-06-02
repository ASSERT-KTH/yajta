package se.kth.castor.yajta.processor;

import se.kth.castor.yajta.processor.loggers.FastLogger;
import org.junit.Test;

import java.io.File;

public class FastLoggerTest {

	@Test
	public void testLog() {
		FastLogger logger = FastLogger.getInstance();
		logger.setLogFile(new File("tmp.json"));


		logger.stepIn(0, logger.register("cl", "m"));
		logger.stepIn(0, logger.register("cl", "m11"));
		logger.stepIn(0, logger.register("cl", "m21"));
		logger.stepOut(0);
		logger.stepOut(0);

		logger.stepIn(1, logger.register("cl", "m"));
		logger.stepIn(1, logger.register("cl", "m11"));
		logger.stepIn(1, logger.register("cl", "m21"));
		logger.stepOut(1);
		logger.stepOut(1);


		logger.stepIn(1, logger.register("cl", "m21"));
		logger.stepIn(1, logger.register("cl", "m22"));
		logger.stepOut(1);
		logger.stepOut(1);
		logger.stepOut(1);

		logger.stepIn(0, logger.register("cl", "m21"));
		logger.stepIn(0, logger.register("cl", "m22"));
		logger.stepOut(0);
		logger.stepOut(0);
		logger.stepOut(0);
		logger.flush();
		System.out.println("Done");
	}

}
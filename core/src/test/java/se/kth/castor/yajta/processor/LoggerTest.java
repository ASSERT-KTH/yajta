package se.kth.castor.yajta.processor;

import se.kth.castor.yajta.api.MalformedTrackingClassException;
import se.kth.castor.yajta.api.SimpleTracer;
import se.kth.castor.yajta.processor.loggers.Logger;
import org.junit.Test;

public class LoggerTest {
    @Test
    public void testLoggerImplem() throws MalformedTrackingClassException {
        SimpleTracer tracer = new SimpleTracer(null);
        tracer.setTrackingClass(Logger.class);
    }
}
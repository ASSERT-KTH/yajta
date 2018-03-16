package fr.inria.yajta.processor;

import fr.inria.yajta.api.MalformedTrackingClassException;
import fr.inria.yajta.api.SimpleTracer;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoggerTest {
    @Test
    public void testLoggerImplem() throws MalformedTrackingClassException {
        SimpleTracer tracer = new SimpleTracer(null);
        tracer.setTrackingClass(Logger.class);
    }
}
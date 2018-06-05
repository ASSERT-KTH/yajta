package fr.inria.yajta.api;


import fr.inria.offline.InstrumentationBuilder;
import fr.inria.yajta.Agent;
import fr.inria.yajta.Tracer;
import fr.inria.yajta.Utils;
import fr.inria.yajta.api.loggerimplem.IncompleteLogger1;
import fr.inria.yajta.api.loggerimplem.IncompleteLogger2;
import fr.inria.yajta.api.loggerimplem.IncompleteLogger3;
import fr.inria.yajta.api.loggerimplem.IncompleteValueLogger1;
import fr.inria.yajta.api.loggerimplem.IncompleteValueLogger2;
import fr.inria.yajta.api.loggerimplem.IncompleteValueLogger3;
import fr.inria.yajta.api.loggerimplem.TestBranchLogger;
import fr.inria.yajta.api.loggerimplem.TestLogger;
import fr.inria.yajta.api.loggerimplem.TestValueLogger;
import fr.inria.yajta.processor.Logger;
import fr.inria.yajta.processor.LoggerTest;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.junit.Assert.*;

public class SimpleTracerTest {

    @Test
    public void testsetTrackingClass() {
        SimpleTracer tracer = new SimpleTracer(null);
        try {
            tracer.setTrackingClass(IncompleteLogger1.class);
            fail("Setting incorrect tracking implementation should raise an exception.");
        } catch (MalformedTrackingClassException e) { }
        try {
            tracer.setTrackingClass(IncompleteLogger2.class);
            fail("Setting incorrect tracking implementation should raise an exception.");
        } catch (MalformedTrackingClassException e) { }
        try {
            tracer.setTrackingClass(IncompleteLogger3.class);
        } catch (MalformedTrackingClassException e) {
            fail("Setting a correct tracking implementation should not raise an exception.");
            e.printStackTrace();
        }
        try {
            tracer.setTrackingClass(TestLogger.class);
        } catch (MalformedTrackingClassException e) {
            fail("Setting a correct tracking implementation should not raise an exception.");
            e.printStackTrace();
        }
        assertFalse(tracer.logValue);
        assertEquals(tracer.loggerInstance, "fr.inria.yajta.api.loggerimplem.TestLogger.getInstance()");
    }

    @Test
    public void testsetValueTrackingClass() {
        SimpleTracer tracer = new SimpleTracer(null);
        try {
            tracer.setValueTrackingClass(IncompleteValueLogger1.class);
            fail("Setting incorrect tracking implementation should raise an exception.");
        } catch (MalformedTrackingClassException e) { }
        try {
            tracer.setValueTrackingClass(IncompleteValueLogger2.class);
            fail("Setting incorrect tracking implementation should raise an exception.");
        } catch (MalformedTrackingClassException e) { }
        try {
            tracer.setValueTrackingClass(IncompleteValueLogger3.class);
        } catch (MalformedTrackingClassException e) {
            fail("Setting a correct tracking implementation should not raise an exception.");
            e.printStackTrace();
        }
        try {
            tracer.setValueTrackingClass(TestValueLogger.class);
        } catch (MalformedTrackingClassException e) {
            fail("Setting a correct tracking implementation should not raise an exception.");
            e.printStackTrace();
        }
        assertTrue(tracer.logValue);
        assertEquals(tracer.loggerInstance, "fr.inria.yajta.api.loggerimplem.TestValueLogger.getInstance()");
    }

    @Test
    public void testProbesInsertion() throws MalformedTrackingClassException {
        File classDir = new File(SimpleTracerTest.class.getClassLoader().getResource("classes").getPath());
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
        builder.close();
    }

    @Test
    public void testValueProbesInsertion() throws MalformedTrackingClassException {
        //Initialization
        File classDir = new File(SimpleTracerTest.class.getClassLoader().getResource("classes-with-value").getPath());
        InstrumentationBuilder builder = new InstrumentationBuilder(classDir, TestValueLogger.class);

        //Instrument bytecode of class in classDir
        builder.instrument();

        //Run the instrumented code from fr.inria.hellovalue.AppValue.main()
        builder.setEntryPoint("fr.inria.hellovalue.AppValue", "main", String[].class);
        builder.runInstrumented((Object) new String[]{"Input"});

        //Check that the logs collected are consistent with what was expected
        List<TestValueLogger.Log> logs = TestValueLogger.getInstance().log;
        //contract: Every method is indeed logged (in and out)
        assertTrue(logs.size() == 22);
        //contract: Every method logged in is also logged out
        assertEquals(
                logs.stream().filter(l -> l.type == TestValueLogger.LOGTYPE.IN).count(),
                logs.stream().filter(l -> l.type == TestValueLogger.LOGTYPE.OUT).count()
        );
        //contract: First method logged is "main", "fr.inria.helloworld.App", "main(java.lang.String[])"
        assertEquals(logs.get(0).thread,"main");
        assertEquals(logs.get(0).clazz,"fr.inria.hellovalue.AppValue");
        assertEquals(logs.get(0).method,"main(java.lang.String[])");

        //contract: First method has been called with one String parameter which value is "Input"
        assertEquals(logs.get(0).parameter.length,1);
        String[] p = (String[]) logs.get(0).parameter[0];
        assertEquals(p[0],"Input");

        //contract: a method that returns a non-primitive type is indeed logged
        assertEquals(logs.get(16).returnValue,"Hello");
        //contract: a method that returns a primitive type is indeed logged
        assertEquals(logs.get(14).returnValue,false);
        //contract: a method that returns a primitive array type is indeed logged
        assertEquals(((boolean[])logs.get(18).returnValue).length,2);
        assertEquals(((boolean[]) logs.get(18).returnValue)[0],true);
        //contract: a method that returns a non-primitive type is indeed logged
        assertEquals(logs.get(16).returnValue,"Hello");
        //contract: a method that returns a non-primitive array type is indeed logged
        assertEquals(((String[])logs.get(20).returnValue).length,1);
        assertEquals(((String[]) logs.get(20).returnValue)[0],"Hello");
        //contract: Last method to end (first to be called) return void"
        assertEquals(logs.get(21).returnValue,null);

        builder.close();
    }

    @Test
    public void testBranchProbesInsertion() throws MalformedTrackingClassException {
        //Initialization
        File classDir = new File(SimpleTracerTest.class.getClassLoader().getResource("classes-with-branch").getPath());
        InstrumentationBuilder builder = new InstrumentationBuilder(classDir, TestBranchLogger.class);
        //InstrumentationBuilder builder = new InstrumentationBuilder(classDir, Logger.class);

        //Instrument bytecode of class in classDir
        builder.instrument();

        //Run the instrumented code from fr.inria.hellovalue.App.main()
        builder.setEntryPoint("fr.inria.hellobranch.AppBranch", "main", String[].class);
        builder.runInstrumented((Object) new String[]{"Input"});

        //Check that the logs collected are consistent with what was expected
        List<TestBranchLogger.Log> logs = TestBranchLogger.getInstance().log;


        //contract: Every method and each branch is indeed logged (in and out)
        //assertTrue(logs.size() == 97);
        //assertTrue(logs.size() == 160);
        //assertTrue(logs.size() == 196);
        //contract: Every method logged in is also logged out
        assertEquals(
                logs.stream().filter(l -> l.type == TestBranchLogger.LOGTYPE.IN).count(),
                logs.stream().filter(l -> l.type == TestBranchLogger.LOGTYPE.OUT).count()
        );
        //contract: Every branch logged in is also logged out
        /*assertEquals(
                logs.stream().filter(l -> l.type == TestBranchLogger.LOGTYPE.BIN).count(),
                logs.stream().filter(l -> l.type == TestBranchLogger.LOGTYPE.BOUT).count()
        );*/

        builder.close();
    }

}
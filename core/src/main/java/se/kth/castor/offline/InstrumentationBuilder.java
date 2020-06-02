package se.kth.castor.offline;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import se.kth.castor.yajta.TracerI;
import se.kth.castor.yajta.Utils;
import se.kth.castor.yajta.api.ClassList;
import se.kth.castor.yajta.api.FastTracer;
import se.kth.castor.yajta.api.FastTracking;
import se.kth.castor.yajta.api.MalformedTrackingClassException;
import se.kth.castor.yajta.api.SimpleTracer;
import se.kth.castor.yajta.api.Tracking;
import se.kth.castor.yajta.api.ValueTracking;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class InstrumentationBuilder {
    File classDir;
    File outputDir;
    boolean tmpOutput = false;
    Class loggerClass;
    ClassList list;
    //SimpleTracer tracer;
    TracerI tracer;

    /** trackingClass must implement Tracking or ValueTracking */
    public InstrumentationBuilder (File classDir, File outputDir, ClassList filter, Class trackingClass, boolean directLoggerCall) throws MalformedTrackingClassException {
        System.out.println("[yajta] use " + trackingClass.getName());
        this.classDir = classDir;
        this.outputDir = outputDir;
        this.list = filter;
        this.loggerClass = trackingClass;
        if (implementsInterface(trackingClass, Tracking.class)) {
            System.out.println("[yajta] use SimpleTracer with " + trackingClass.getName());
            tracer = new SimpleTracer(list, directLoggerCall ? (trackingClass.getName() + ".getInstance()") : null);
            tracer.setTrackingClass(trackingClass);
        } else if (implementsInterface(trackingClass, ValueTracking.class)) {
            tracer = new SimpleTracer(list, directLoggerCall ? (trackingClass.getName() + ".getInstance()") : null);
            tracer.setValueTrackingClass(trackingClass);
        } else if (implementsInterface(trackingClass, FastTracking.class)) {
            tracer = new FastTracer(list, directLoggerCall ? (trackingClass.getName() + ".getInstance()") : null);
            tracer.setFastTrackingClass(trackingClass);
        } else {
            throw new MalformedTrackingClassException("Tracking class must implements either Tracking, BranchTracking, FastTracking or ValueTracking");
        }
        if(outputDir == null) {
            tmpOutput = true;
            this.outputDir = Utils.getATmpDir();
        }
    }

    /** trackingClass must implement Tracking or ValueTracking */
    public InstrumentationBuilder (File classDir, File outputDir, ClassList filter, Class trackingClass) throws MalformedTrackingClassException {
        this(classDir,outputDir,filter,trackingClass, false);
    }

    /** trackingClass must implement Tracking, BranchTracking or ValueTracking */
    public InstrumentationBuilder (File classDir, Class trackingClass) throws MalformedTrackingClassException {
        this(classDir,null,null,trackingClass);
    }

    /** trackingClass must implement Tracking, BranchTracking or ValueTracking */
    public InstrumentationBuilder (File classDir, ClassList filter, Class trackingClass) throws MalformedTrackingClassException {
        this(classDir,null,filter,trackingClass);
    }

    /** trackingClass must implement Tracking, BranchTracking or ValueTracking */
    public InstrumentationBuilder (File classDir, File outputDir, Class trackingClass) throws MalformedTrackingClassException {
        this(classDir,outputDir,null,trackingClass);
    }

    public String[] filter(String[]classes , ClassList filter) {
        if(filter == null) return classes;
        return Arrays.asList(classes).stream().filter(c -> filter.isToBeProcessed(c)).toArray(size -> new String[size]);
    }


    /** instrument all classes (unless a class filter us given) from the given input directory and writes the instrumented classed to disk */
    public void instrument() throws MalformedTrackingClassException {
        System.out.println("[yajta] instrument");
        try {
            /*if(implementsInterface(loggerClass,Tracking.class)) {
                tracer.setTrackingClass(loggerClass);
            } else if (implementsInterface(loggerClass,ValueTracking.class)) {
                tracer.setValueTrackingClass(loggerClass);
            } else {
                throw new MalformedTrackingClassException("Tracking class must implements either Tracking, BranchTracking, FastTracking or ValueTracking");
            }*/
            ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(InstrumentationBuilder.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            pool.appendClassPath(loggerClass.getProtectionDomain().getCodeSource().getLocation().getPath());
            pool.appendClassPath(classDir.getAbsolutePath());
            String[] classNames = filter(Utils.listClassesAsArray(classDir), list);
            CtClass[] classToTransform = pool.get(classNames);

            for(CtClass cl: classToTransform) {
                try {
                    System.out.println("[yajta] instrument class " + cl.getName());
                    tracer.doClass(cl,cl.getName());
                    cl.writeFile(outputDir.getAbsolutePath());
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                    throw new MalformedTrackingClassException("Incorrect probe insertion: " + e.getMessage());
                } catch (IOException e) {
                    throw new MalformedTrackingClassException("ClassFile not found?");
                }
            }

        } catch (NotFoundException e ) {
            e.printStackTrace();
            throw new MalformedTrackingClassException("e: " + e.getMessage());
        }
    }


    public boolean implementsInterface(Class cl, Class interf) {
        for(Class c : cl.getInterfaces()) {
            if(c.equals(interf)) {
                return true;
            }
        }
        return false;
    }

    //EntryPoint
    String className;
    String methodName;
    Class<?>[] paramtersType;
    public void setEntryPoint(String className, String methodName, Class<?>... paramtersType) {
        this.className = className;
        this.methodName = methodName;
        this.paramtersType = paramtersType;
    }

    public void runInstrumented(Object... paramters) throws MalformedTrackingClassException {
        try {

            URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{outputDir.toURI().toURL()});
            Class appClass = urlClassLoader.loadClass(className);
            Method method;
            method = appClass.getMethod(methodName, paramtersType);
            method.invoke(null, paramters);

        } catch (MalformedURLException |
                ClassNotFoundException |
                NoSuchMethodException |
                InvocationTargetException |
                IllegalAccessException e ) {
            throw new MalformedTrackingClassException("Run instrumented code failed");
        }
    }

    public void close() {
        if(tmpOutput && outputDir.exists()) {
            outputDir.delete();
        }
    }

    @Override
    public void finalize() throws Throwable {
        if(tmpOutput && outputDir.exists()) {
            outputDir.delete();
        }
        super.finalize();
    }
}

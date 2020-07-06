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
import se.kth.castor.yajta.processor.util.MyList;

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
    TracerI tracer;
    MyList<String> classpathElements = new MyList<>();

    /**
     * trackingClass must implement Tracking or ValueTracking
     * @param classDir path bytecode to be transformed
     * @param outputDir path to directory where to write transformed bytecode
     * @param filter policy to determine which class to transform
     * @param trackingClass logging class. Call to this class will be inserted.
     * @param directLoggerCall if true, an instance of the tracking class will be obtain through getInstance
     * @throws MalformedTrackingClassException if the tracking class does not implement a valid interface
     */
    public InstrumentationBuilder (File classDir, File outputDir, ClassList filter, Class trackingClass, boolean directLoggerCall) throws MalformedTrackingClassException {
        System.out.println("[yajta] use " + trackingClass.getName());
        this.classDir = classDir;
        this.outputDir = outputDir;
        this.list = filter;
        this.loggerClass = trackingClass;
        if (implementsInterface(trackingClass, Tracking.class)) {
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

    /**
     * trackingClass must implement Tracking or ValueTracking
     * @param classDir path bytecode to be transformed
     * @param outputDir path to directory where to write transformed bytecode
     * @param filter policy to determine which class to transform
     * @param trackingClass logging class. Call to this class will be inserted.
     * @throws MalformedTrackingClassException if the tracking class does not implement a valid interface
     */
    public InstrumentationBuilder (File classDir, File outputDir, ClassList filter, Class trackingClass) throws MalformedTrackingClassException {
        this(classDir,outputDir,filter,trackingClass, false);
    }

    /**
     * trackingClass must implement Tracking, BranchTracking or ValueTracking
     * @param classDir path bytecode to be transformed
     * @param trackingClass logging class. Call to this class will be inserted.
     * @throws MalformedTrackingClassException if the tracking class does not implement a valid interface
     */
    public InstrumentationBuilder (File classDir, Class trackingClass) throws MalformedTrackingClassException {
        this(classDir,null,null,trackingClass);
    }

    /**
     * trackingClass must implement Tracking, BranchTracking or ValueTracking
     *
     */
    /**
     * trackingClass must implement Tracking or ValueTracking
     * @param classDir path bytecode to be transformed
     * @param filter policy to determine which class to transform
     * @param trackingClass logging class. Call to this class will be inserted.
     * @throws MalformedTrackingClassException if the tracking class does not implement a valid interface
     */
    public InstrumentationBuilder (File classDir, ClassList filter, Class trackingClass) throws MalformedTrackingClassException {
        this(classDir,null,filter,trackingClass);
    }

    /**
     * trackingClass must implement Tracking, BranchTracking or ValueTracking
     * @param classDir path bytecode to be transformed
     * @param outputDir path to directory where to write transformed bytecode
     * @param trackingClass logging class. Call to this class will be inserted.
     * @throws MalformedTrackingClassException if the tracking class does not implement a valid interface
     */
    public InstrumentationBuilder (File classDir, File outputDir, Class trackingClass) throws MalformedTrackingClassException {
        this(classDir,outputDir,null,trackingClass);
    }


    public void addClassPathElement(String classPathElement) {
        classpathElements.add(classPathElement);
    }



    private String[] filter(String[]classes , ClassList filter) {
        if(filter == null) return classes;
        return Arrays.asList(classes).stream().filter(c -> filter.isToBeProcessed(c)).toArray(size -> new String[size]);
    }


    /**
     * Instruments all classes (unless a class filter us given) from the given input directory and writes the instrumented classed to disk.
     *
     * @throws MalformedTrackingClassException when the path to a bytecode file cannot be determined or if the probe call cannot be compiled.
     */
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
            pool.appendClassPath(classDir.getAbsolutePath());
            for(String cpel: classpathElements) {
                pool.appendClassPath(cpel);
            }

            //FIXME This is a terrible idea to conflate the classpath available when instrumenting and when executing. This should be separated...  For shame!
            pool.appendClassPath(loggerClass.getProtectionDomain().getCodeSource().getLocation().getPath());

            String[] classNames = filter(Utils.listClassesAsArray(classDir), list);
            CtClass[] classToTransform = pool.get(classNames);

            for(CtClass cl: classToTransform) {
                try {
                    System.out.println("[yajta] instrument class " + cl.getName());
                    tracer.doClass(cl,cl.getName());
                    cl.writeFile(outputDir.getAbsolutePath());
                } catch (CannotCompileException e) {
                    System.err.println("Incorrect probe insertion: " + e.getMessage() +", skiping class " + cl.getName());
                    e.printStackTrace();
                    //throw new MalformedTrackingClassException("Incorrect probe insertion: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("ClassFile not found: " + e.getMessage() +", skiping class " + cl.getName());
                    //throw new MalformedTrackingClassException("ClassFile not found?");
                }
            }

        } catch (NotFoundException e ) {
            e.printStackTrace();
            throw new MalformedTrackingClassException("e: " + e.getMessage());
        }
    }


    private boolean implementsInterface(Class cl, Class interf) {
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

    /**
     * Set the entry point to be used with runInstrumented
     * @param className fully qualified name of the class
     * @param methodName method name
     * @param paramtersType types of arguments
     */
    public void setEntryPoint(String className, String methodName, Class<?>... paramtersType) {
        this.className = className;
        this.methodName = methodName;
        this.paramtersType = paramtersType;
    }

    /**
     * Run the instrumented bytecode with the previously set entry point.
     * @param parameters Actual values of the arguments
     * @throws MalformedTrackingClassException if something went wrong.
     */
    public void runInstrumented(Object... parameters) throws MalformedTrackingClassException {
        try {

            URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{outputDir.toURI().toURL()});
            Class appClass = urlClassLoader.loadClass(className);
            Method method;
            method = appClass.getMethod(methodName, paramtersType);
            method.invoke(null, parameters);

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

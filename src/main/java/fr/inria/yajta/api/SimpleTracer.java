package fr.inria.yajta.api;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.Modifier;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;

public class SimpleTracer implements ClassFileTransformer {

    public boolean verbose = false;
    public boolean strictIncludes = false;
    ClassList cl;

    String loggerInstance;
    boolean logValue = false;

    public static SimpleTracer getDefault(String packageToTrace) {
        return new SimpleTracer(ClassList.getDefault(packageToTrace));
    }

    public SimpleTracer (ClassList cl) {
        new SimpleTracer(cl, "fr.inria.yajta.Agent.getInstance()", false);
    }

    public SimpleTracer (ClassList cl, String loggerInstance) {
        new SimpleTracer(cl, loggerInstance, false);
    }

    public SimpleTracer (ClassList cl, boolean logValue) {
        new SimpleTracer(cl, "fr.inria.yajta.Agent.getInstance()", logValue);
    }

    public void setTrackingClass(Class<? extends Tracking> trackingClass) throws MalformedTrackingClassException {
        if(trackingClass.isAnonymousClass()) {
            throw new MalformedTrackingClassException("Class " + trackingClass.getName() + " should not be anonymous.)");
        }
        try {
            Method m = trackingClass.getDeclaredMethod("getInstance");
            if(!java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                throw new MalformedTrackingClassException("Method " + trackingClass.getName() + ".getInstance() is not static");
            }
            loggerInstance = trackingClass.getName() + ".getInstance()";
            logValue = false;
        } catch (NoSuchMethodException e) {
            throw new MalformedTrackingClassException("Class " + trackingClass.getName() + " does not have a static method getInstance()");
        }
    }

    public void setValueTrackingClass(Class<? extends ValueTracking> trackingClass) throws MalformedTrackingClassException {
        if(trackingClass.isAnonymousClass()) {
            throw new MalformedTrackingClassException("Class " + trackingClass.getName() + " should not be anonymous.)");
        }
        try {
            Method m = trackingClass.getDeclaredMethod("getInstance");
            if(!java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                throw new MalformedTrackingClassException("Method " + trackingClass.getName() + ".getInstance() is not static");
            }
            loggerInstance = trackingClass.getName() + ".getInstance()";
            logValue = true;
        } catch (NoSuchMethodException e) {
            throw new MalformedTrackingClassException("Class " + trackingClass.getName() + " does not have a static method getInstance()");
        }
    }

    public SimpleTracer (ClassList cl, String loggerInstance, boolean logValue) {
        this.cl = cl;
        this.loggerInstance = loggerInstance;
        this.logValue = logValue;
    }

    public byte[] transform( final ClassLoader loader, final String className, final Class clazz,
                             final java.security.ProtectionDomain domain, final byte[] bytes ) {
        if(verbose) System.out.println("className: " + className + " -> " + cl.isToBeProcessed(className));
        if( cl.isToBeProcessed(className) ) {
            return doClass( className, clazz, bytes );
        } else {
            return bytes;
        }
    }

    public byte[] doClass( final String name, final Class clazz, byte[] b ) {
        ClassPool pool = ClassPool.getDefault();
        CtClass cl = null;
        try {
            cl = pool.makeClass( new java.io.ByteArrayInputStream( b ) );
            if( cl.isInterface() == false ) {

                doClass(cl,name);

                b = cl.toBytecode();

                if(verbose) System.err.println( "-> Instrument  " + name);
            }
        } catch( Exception e ) {
            if(verbose) System.err.println( "Could not instrument  " + name + ",  exception : " + e.getMessage() );
        } finally {

            if( cl != null ) {
                cl.detach();
            }
        }

        return b;
    }

    public void doClass(CtClass cl, String name) throws NotFoundException, CannotCompileException {
        CtBehavior[] methods = cl.getDeclaredBehaviors();

        for( int i = 0; i < methods.length; i++ ) {

            if( methods[i].isEmpty() == false ) {
                doMethod( methods[i] , name);
            }
        }
    }

    private void doMethod( final CtBehavior method , String className) throws NotFoundException, CannotCompileException {
        doMethod(method,className,false,null);
    }

    private void doMethod( final CtBehavior method , String className, boolean isIsotope, String isotope) throws NotFoundException, CannotCompileException {
        //System.out.println("\t\tMethod: " + method.getLongName() + " -> " + !Modifier.isNative(method.getModifiers()));
        if(!Modifier.isNative(method.getModifiers())) {
            if(verbose) System.err.println("[Vanilla] " + className + " " + method.getName());
            String params = "(";
            boolean first = true;
            for (CtClass c : method.getParameterTypes()) {
                if (first) first = false;
                else params += ", ";
                params += c.getName();
            }
            params += ")";

            String parameterValues = "";
            String returnValue = "";
            if(logValue) {
                parameterValues = ", $args";
                if(method instanceof CtMethod) {
                    CtMethod m = (CtMethod) method;
                    if(m.getReturnType() instanceof CtPrimitiveType
                            && !(m.getReturnType().getName().equals("void"))) {
                        returnValue = ", new " + ((CtPrimitiveType) m.getReturnType()).getWrapperName() +"($_)";
                    } else {
                        returnValue = ", $_";
                    }
                } else {
                    returnValue = ", $_";
                }
            }

            /*System.err.println(loggerInstance + ".stepIn(Thread.currentThread().getName(),\""
                    + className.replace("/", ".") + "\", \""
                    + method.getName() + params + "\""
                    + parameterValues
                    + ");");*/
            method.insertBefore(loggerInstance + ".stepIn(Thread.currentThread().getName(),\""
                    + className.replace("/", ".") + "\", \""
                    + method.getName() + params + "\""
                    + parameterValues
                    + ");");
            method.insertAfter(loggerInstance + ".stepOut(Thread.currentThread().getName()"
                    + returnValue
                    +");");

        } else {
            if(verbose) System.err.println("Method: " + className.replace("/", ".") + "." + method.getName() + " is native");
        }
    }
}

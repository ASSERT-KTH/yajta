package fr.inria.yajta.api;

import fr.inria.yajta.Tracer;
import fr.inria.yajta.TracerI;
import fr.inria.yajta.Utils;
import fr.inria.yajta.api.ClassList;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.analysis.ControlFlow;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;

public class BranchTracer implements TracerI {

    public boolean verbose = false;
    public boolean strictIncludes = false;
    ClassList cl;

    String loggerInstance;
    boolean logValue = false;

    public BranchTracer (ClassList cl) {
        new Tracer(cl,new String[0]);
    }

    public BranchTracer (ClassList cl, String isotopes[]) {
        this.cl = cl;
        ISOTOPES = isotopes;
    }

    String[] ISOTOPES;

    public byte[] transform( final ClassLoader loader, final String className, final Class clazz,
                             final java.security.ProtectionDomain domain, final byte[] bytes ) {
        if(verbose) System.out.println("className: " + className + " -> " + cl.isToBeProcessed(className));
        if( Utils.startWith(className, ISOTOPES) ) return doClass( className, clazz, bytes, true);
        if( cl.isToBeProcessed(className) ) {
            return doClass( className, clazz, bytes );
        } else {
            return bytes;
        }
    }

    @Override
    public void doClass(CtClass cl, String name) throws NotFoundException, CannotCompileException {
        CtBehavior[] methods = cl.getDeclaredBehaviors();

        for( int i = 0; i < methods.length; i++ ) {

            if( methods[i].isEmpty() == false ) {
                doMethod( methods[i] , name);
            }
        }
    }

    public byte[] doClass( final String name, final Class clazz, byte[] b ) {
        return doClass(name,clazz,b,false);
    }
    public byte[] doClass( final String name, final Class clazz, byte[] b, boolean isIsotope ) {
        ClassPool pool = ClassPool.getDefault();
        CtClass cl = null;
        try {
            cl = pool.makeClass( new java.io.ByteArrayInputStream( b ) );
            if( cl.isInterface() == false ) {

                CtBehavior[] methods = cl.getDeclaredBehaviors();

                for( int i = 0; i < methods.length; i++ ) {

                    if( methods[i].isEmpty() == false ) {
                        doMethod( methods[i] , name);
                    }
                }

                b = cl.toBytecode();

                if(verbose) System.err.println( "-> Instrument  " + name);
            }
        } catch( Exception e ) {
            System.err.println( "Could not instrument  " + name + ",  exception : " + e.getMessage() );
            e.printStackTrace(System.err);
        } finally {

            if( cl != null ) {
                cl.detach();
            }
        }

        return b;
    }



    private Bytecode getBytecode(String print, CtClass cc) throws CompileError {
        Javac jv = new Javac(cc);
        jv.compileStmnt(print);
        if(verbose) System.err.println("compile: " + print);
        return jv.getBytecode();
    }

    private void doMethod( final CtBehavior method , String className) throws NotFoundException, CannotCompileException {
        //System.out.println("\t\tMethod: " + method.getLongName() + " -> " + !Modifier.isNative(method.getModifiers()));
        if(verbose) System.err.println("Method: " + className.replace("/", ".") + "." + method.getName());
        if(!Modifier.isNative(method.getModifiers())) {
            if(verbose) System.err.println("Method: " + className.replace("/", ".") + "." + method.getName() + " is not native");
            String params = "(";
            boolean first = true;
            for (CtClass c : method.getParameterTypes()) {
                if (first) first = false;
                else params += ", ";
                params += c.getName();
            }
            params += ")";

            String loggerBegin = "System.err.println(\"[" + className + "] " + method.getName() + params + " block ";
            String loggerEnd = "\");";
            if(method instanceof CtMethod) {
                if(verbose) System.err.println("Method: " + className.replace("/", ".") + "." + method.getName() + " is processed");
                try {
                    ControlFlow controlFlow = new ControlFlow((CtMethod)method);
                    MethodInfo info = method.getMethodInfo();
                    CodeIterator iterator = info.getCodeAttribute().iterator();
                    ControlFlow.Block[] blocks = controlFlow.basicBlocks();
                    /*for(int i = 0; i < blocks.length; i++) {
                        //if(i == 3) {
                        System.err.println("Block: " + i + ", pos: " + blocks[i].position());
                    }*/
                    int offset = 0;
                    for(int i = 0; i < blocks.length; i++) {
                        if(verbose) System.err.println("Transform Block: " + i + ", pos: " + (blocks[i].position() + offset));
                        byte[] bytes = getBytecode(loggerBegin + i + loggerEnd, method.getDeclaringClass()).get();
                        iterator.insertAt(blocks[i].position() + offset, bytes);
                        offset += bytes.length;
                    }
                } catch (BadBytecode badBytecode) {
                    badBytecode.printStackTrace();
                } catch (CompileError compileError) {
                    compileError.printStackTrace();
                }
            } else {
                method.insertBefore(loggerBegin + "M begin" + loggerEnd);
                method.insertAfter(loggerBegin + "M end" + loggerEnd);

            }

            //method.insertBefore(pprefix + "fr.inria.yajta.Agent.getTrackingInstance().stepIn(Thread.currentThread().getName(),\"" + className.replace("/", ".") + "\", \"" + method.getName() + params + "\");" + ppostfix);
            //method.insertAfter(pprefix + "fr.inria.yajta.Agent.getTrackingInstance().stepOut(Thread.currentThread().getName());" + ppostfix);

        } else {
            if(verbose) System.err.println("Method: " + className.replace("/", ".") + "." + method.getName() + " is native");
        }
    }

    @Override
    public void setTrackingClass(Class<? extends Tracking> trackingClass) throws MalformedTrackingClassException {
        throw new MalformedTrackingClassException("BranchTracer only accept Classes implementing BranchTracking.");
    }

    @Override
    public void setValueTrackingClass(Class<? extends ValueTracking> trackingClass) throws MalformedTrackingClassException {
        throw new MalformedTrackingClassException("BranchTracer only accept Classes implementing BranchTracking.");
    }

    /*@Override
    public void setBranchTrackingClass(Class<? extends BranchTracking> trackingClass) throws MalformedTrackingClassException {
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
    }*/
}
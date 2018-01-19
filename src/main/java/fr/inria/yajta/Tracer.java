package fr.inria.yajta;

import javassist.*;
import javassist.Modifier;
import javassist.expr.ExprEditor;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.*;

import static javassist.CtClass.voidType;

public class Tracer implements ClassFileTransformer {

    boolean verbose = false;
    boolean strictIncludes = false;

    public Tracer (String[] includes, String excludes[]) {
        new Tracer(includes,excludes,new String[0]);
    }

    public Tracer (String[] includes, String excludes[], String isotopes[]) {
        INCLUDES = includes;
        DEFAULT_EXCLUDES = excludes;
        ISOTOPES = isotopes;
    }

    String[] DEFAULT_EXCLUDES;
    String[] ISOTOPES;

    String[] INCLUDES;

    public byte[] transform( final ClassLoader loader, final String className, final Class clazz,
                             final java.security.ProtectionDomain domain, final byte[] bytes ) {

        for( String isotope : ISOTOPES ) {

            if( className.startsWith( isotope ) ) {
                return doClass( className, clazz, bytes, true);
            }
        }

        for( String include : INCLUDES ) {

            if( className.startsWith( include ) ) {
                return doClass( className, clazz, bytes );
            }
        }

        for( int i = 0; i < DEFAULT_EXCLUDES.length; i++ ) {

            if( className.startsWith( DEFAULT_EXCLUDES[i] ) ) {
                return bytes;
            }
        }

        if(!strictIncludes) return doClass( className, clazz, bytes );
        else return bytes;
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

                /*for( int i = 0; i < methods.length; i++ ) {
                    if(Modifier.isNative(methods[i].getModifiers()) && !methods[i].getName().startsWith("wrapped__native__method__")) {
                        System.err.println( "Class  " + name + ", m : " + methods[i].getName() );
                        CtMethod m = (CtMethod) methods[i];
                        String mName = m.getName();
                        m.setName("wrapped__native__method__" + m.getName());
                        String body = "{";
                        if(!m.getReturnType().getName().equals("java.lang.void")) {
                            body += "return ";
                        }
                        body += m.getName() + "($$);}";
                        System.err.println( "1");

                        CtMethod newM = CtNewMethod.make(
                                m.getModifiers() & (~java.lang.reflect.Modifier.NATIVE),
                                m.getReturnType(),
                                mName,
                                m.getParameterTypes(),
                                m.getExceptionTypes(),
                                body,
                                cl
                        );
                        System.err.println( "2");
                        cl.addMethod(newM);
                        System.err.println( "3");

                    }
                }
                methods = cl.getDeclaredBehaviors();*/

                for( int i = 0; i < methods.length; i++ ) {

                    if( methods[i].isEmpty() == false ) {
                        if(isIsotope)
                            doMethod( methods[i] , name, isIsotope, "fr.inria.singleusagedemo.collections.MyMap");
                        else
                            doMethod( methods[i] , name);
                    }
                }

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

    private void doMethod( final CtBehavior method , String className) throws NotFoundException, CannotCompileException {
        doMethod(method,className,false,null);
    }

    private void doMethod( final CtBehavior method , String className, boolean isIsotope, String isotope) throws NotFoundException, CannotCompileException {

        if(!Modifier.isNative(method.getModifiers())) {
            String pprefix = "", ppostfix = "";
            if(isIsotope && !Modifier.isStatic(method.getModifiers())) {
                if(verbose) System.err.println("[Isotope] " + className + " " + method.getName());
                pprefix = "if(getClass().getName().equalsIgnoreCase(\"" + isotope + "\")) {";
                ppostfix = "}";
            } else if(isIsotope) {
                pprefix = "if(false) {";
                ppostfix = "}";
            } else {
                if(verbose) System.err.println("[Vanilla] " + className + " " + method.getName());
            }
            String params = "(";
            boolean first = true;
            for (CtClass c : method.getParameterTypes()) {
                if (first) first = false;
                else params += ", ";
                params += c.getName();
            }
            params += ")";

            //method.insertAfter( "System.err.println( $_ );");

            method.insertBefore(pprefix + "fr.inria.yajta.Agent.getTrackingInstance().stepIn(Thread.currentThread().getName(),\"" + className.replace("/", ".") + "." + method.getName() + params + "\");" + ppostfix);
            method.insertAfter(pprefix + "fr.inria.yajta.Agent.getTrackingInstance().stepOut(Thread.currentThread().getName());" + ppostfix);

        } else {
            if(verbose) System.err.println("Method: " + className.replace("/", ".") + "." + method.getName() + " is native");
        }
    }
}
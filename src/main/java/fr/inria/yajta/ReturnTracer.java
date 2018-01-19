package fr.inria.yajta;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;

/**
 * Created by nharrand on 27/07/17.
 */
public class ReturnTracer implements ClassFileTransformer {

    boolean verbose = true;
    boolean strictIncludes = false;

    public ReturnTracer (String[] includes, String excludes[]) {
        new Tracer(includes,excludes,new String[0]);
    }

    public ReturnTracer (String[] includes, String excludes[], String isotopes[]) {
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

            if( className.startsWith( include ) && !className.endsWith("Test") ) {
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

                CtMethod[]  methods = cl.getMethods();

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

    private void doMethod( final CtMethod method , String className) throws NotFoundException, CannotCompileException {
        doMethod(method,className,false,null);
    }

    private void doMethod( final CtMethod method , String className, boolean isIsotope, String isotope) throws NotFoundException, CannotCompileException {

        if(!Modifier.isNative(method.getModifiers())) {
            String params = "(";
            boolean first = true;
            for (CtClass c : method.getParameterTypes()) {
                if (first) first = false;
                else params += ", ";
                params += c.getName();
            }
            params += ")";

            //method.insertAfter( "System.err.println( $_ );");

            method.insertAfter("fr.inria.yajta.Agent.getTrackingInstance().trace(Thread.currentThread().getName(),\"" + className.replace("/", ".") + "." + method.getName() + params + "\", $_);");

        } else {
            if(verbose) System.err.println("Method: " + className.replace("/", ".") + "." + method.getName() + " is native");
        }
    }
}

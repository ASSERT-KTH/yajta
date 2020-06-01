package fr.inria.yajta;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;

import java.lang.instrument.ClassFileTransformer;
import java.util.Arrays;

public class JunitDeterministicator implements ClassFileTransformer {
    CtClass fixMethodOrder;
    CtClass methodSorters;
    ClassPool cp;

    public JunitDeterministicator() {
        cp = ClassPool.getDefault();
        try {
            fixMethodOrder = cp.get("org.junit.FixMethodOrder");
            methodSorters = cp.get("org.junit.runners.MethodSorters");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public byte[] transform( final ClassLoader loader, final String className, final Class clazz,
                             final java.security.ProtectionDomain domain, final byte[] bytes ) {

        ClassPool pool = ClassPool.getDefault();
        CtClass cl = null;
        byte[] b = Arrays.copyOfRange(bytes,0,bytes.length);
        try {
            cl = pool.makeClass( new java.io.ByteArrayInputStream( b ) );
            if( cl.isInterface() == false ) {



                b = cl.toBytecode();
            }
        } catch( Exception e ) {
        } finally {

            if( cl != null ) {
                cl.detach();
            }
        }

        return b;
        //return bytes;
    }

    public boolean isTestClass(String className) {
        return className.endsWith("Test");
    }
}

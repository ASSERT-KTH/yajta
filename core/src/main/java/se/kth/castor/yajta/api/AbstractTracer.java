package se.kth.castor.yajta.api;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;

import java.net.URL;

public abstract class AbstractTracer {

	public boolean verbose = false;
	ClassList cl;
	ClassPool pool = ClassPool.getDefault();

	public byte[] transform( final ClassLoader loader, final String className, final Class clazz,
	                         final java.security.ProtectionDomain domain, final byte[] bytes ) {
		//if(verbose) System.out.println("className: " + className + " ? ");
		URL classURL = loader.getResource(className + ".class");
		String classFilePath = classURL == null ? null : classURL.getFile().replace("file:","");
		if(classFilePath == null || !cl.isInJars(classFilePath)) return bytes;
		if(verbose) System.out.println("className: " + className + " -> " + cl.isToBeProcessed(className));
		if( cl.isToBeProcessed(className) ) {
			return doClass( className, clazz, bytes );
		} else {
			return bytes;
		}
	}

	public byte[] doClass( final String name, final Class clazz, byte[] b ) {
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

	abstract void doMethod( final CtBehavior method , String className, boolean isIsotope, String isotope) throws NotFoundException, CannotCompileException;
}

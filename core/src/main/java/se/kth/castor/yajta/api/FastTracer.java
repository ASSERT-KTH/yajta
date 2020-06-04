package se.kth.castor.yajta.api;


import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.analysis.ControlFlow;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import se.kth.castor.yajta.TracerI;
import se.kth.castor.yajta.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

public class FastTracer extends AbstractTracer implements TracerI {

	public boolean strictIncludes = false;

	String loggerInstance;
	FastTracking realLoggerInstance;
	boolean logValue = false;
	boolean logBranch = false;


	public FastTracer (ClassList cl) {
		this.cl = cl;
		this.loggerInstance = "se.kth.castor.yajta.Agent.getInstance()";
		this.logValue = false;
	}

	public FastTracer (ClassList cl, String loggerInstance) {
		this.cl = cl;
		this.loggerInstance = loggerInstance;
		this.logValue = false;
	}

	public FastTracer (ClassList cl, boolean logValue) {
		this.cl = cl;
		this.loggerInstance = "se.kth.castor.yajta.Agent.getInstance()";
		this.logValue = logValue;
	}

	public FastTracer (ClassList cl, String loggerInstance, boolean logValue) {
		this.cl = cl;
		this.loggerInstance = loggerInstance;
		this.logValue = logValue;
	}


	public boolean implementsInterface(Class cl, Class interf) {
		for(Class c : cl.getInterfaces()) {
			if(c.equals(interf)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setTrackingClass(Class<? extends Tracking> trackingClass) throws MalformedTrackingClassException {
		throw new UnsupportedOperationException("FastTracer only supports TracingInstance that implements FastTracking");
	}

	@Override
	public void setValueTrackingClass(Class<? extends ValueTracking> trackingClass) throws MalformedTrackingClassException {
		throw new UnsupportedOperationException("FastTracer only supports TracingInstance that implements FastTracking");
	}

	@Override
	public void setFastTrackingClass(Class<? extends FastTracking> trackingClass) throws MalformedTrackingClassException {
		if(verbose) System.err.println( "[yajta] setTrackingClass " + trackingClass.getName());
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
			FastTracking instance = (FastTracking) trackingClass.getMethod("getInstance", new Class[]{}).invoke(null,new Object[]{});
			realLoggerInstance = instance;
			if(instance.traceBranch()) {
				if(verbose) System.err.println( "[yajta] set Branch tracking on.");
				logBranch = true;
			}
		} catch (NoSuchMethodException e) {
			throw new MalformedTrackingClassException("Class " + trackingClass.getName() + " does not have a static method getInstance()");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void setTrackingClass(Class<? extends FastTracking> trackingClass, FastTracking realLoggerInstance) throws MalformedTrackingClassException {
		if(verbose) System.err.println( "[yajta] setTrackingClass " + trackingClass.getName());
		logValue = false;
		logBranch = realLoggerInstance.traceBranch();
		loggerInstance = "se.kth.castor.yajta.Agent.fastTrackingInstance";
		this.realLoggerInstance = realLoggerInstance;
	}

	private Bytecode getBytecode(String print, CtClass cc) throws CompileError {
		Javac jv = new Javac(cc);
		jv.compileStmnt(print);
		if(verbose) System.err.println("compile: " + print);
		return jv.getBytecode();
	}

	private void printBlock(CodeIterator iterator, int begin, int end, int no) {
		System.out.println(" -- Block "+ no +":");
		for(int i = begin; i < end; i++) {
			//if(iterator.byteAt(i) != 0)
			System.out.println(Mnemonic.OPCODE[iterator.byteAt(i)]);
		}
		System.out.println(" -- ");
	}

	private void printDiff(CodeAttribute ca, int debut, int insertSize, int segmentSize) {
		System.out.println(" --- RAW --- ");

		for(int i = 0; i < ca.getCode().length; i++) {
			if(i >= debut && i < debut + insertSize) {
				System.out.print("++");
			}
			if(i >= debut + insertSize && i < debut + segmentSize) {
				System.out.print("|");
			}

			//if(ca.getCode()[i] != 0) {
			System.out.println(Utils.getOpcode(ca.getCode()[i] & 0xff));
			//}
		}
	}

	private boolean isBlockEmpty(CodeIterator iterator, int begin, int end) {
		for(int i = begin; i < end; i++) {
			if(iterator.byteAt(i) != 0) return false;
		}
		return true;
	}

	protected void doMethod( final CtBehavior method , String className, boolean isIsotope, String isotope) throws NotFoundException, CannotCompileException {
		if(!Modifier.isNative(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())) {
			if(verbose) System.err.println("[Vanilla] " + className + " " + method.getName());
			try {
				/*String params = "(";
				boolean first = true;
				for (CtClass c : method.getParameterTypes()) {
					if (first) first = false;
					else params += ", ";
					params += c.getName();
				}
				params += ")";*/
				//int tracePointId = realLoggerInstance.register(className.replace("/", "."), method.getName() + params);
				int tracePointId = realLoggerInstance.register(className.replace("/", "."), method.getName() + method.getSignature());

				// !!!! This only work because the inserted call for branch logging does not have more arguments than the method logging one.
				if(logBranch && method instanceof CtMethod) {
					try {
						ControlFlow controlFlow = new ControlFlow((CtMethod)method);
						MethodInfo info = method.getMethodInfo();
						CodeAttribute ca = info.getCodeAttribute();
						CodeIterator iterator = ca.iterator();
						ControlFlow.Block[] blocks = controlFlow.basicBlocks();
						String branchIn = loggerInstance + ".stepIn(Thread.currentThread().getId(),";
						String branchInEnd = ");";
						String branchOut = loggerInstance + ".stepOut(Thread.currentThread().getId());";
						int offset = 0;
	                    /*if(method.getName().equals("myIfElse")) {
	                        System.out.println(" --- RAW --- ");

	                        for(int i = 0; i < ca.getCode().length; i++) {
	                            System.out.println(Mnemonic.OPCODE[(int) ca.getCode()[i] & 0xff]);
	                        }

	                        System.out.println(" --- Avant --- ");
	                        for(int i = 0; i < blocks.length; i++) {
	                            printBlock(iterator, blocks[i].position(), blocks[i].position() + blocks[i].length(), i);
	                        }
	                        System.out.println(" --- Après --- ");
	                        System.out.println("------- total size: "+ca.getCode().length+ ", added 0");
	                    }*/
						for(int i = 0; i < blocks.length; i++) {
							//int tracePointBlockId = realLoggerInstance.register(className.replace("/", "."), method.getName() + params, ""+i);
							int tracePointBlockId = realLoggerInstance.register(className.replace("/", "."), method.getName() + method.getSignature(), ""+i);
							//for(int i = 1; i < blocks.length-1; i++) {
							int sizeBefore = ca.getCode().length;
							//String inser = branchIn + i + branchInEnd;
							String inser = branchIn + tracePointBlockId + branchInEnd;
							inser += branchOut;
	                        /*if(i == 0) {
	                            inser = loggerInstance + ".stepIn(Thread.currentThread().getName(),\""
	                                    + className.replace("/", ".") + "\", \""
	                                    + method.getName() + params + "\""
	                                    + parameterValues
	                                    + ");\n" + inser;
	                        }*/
							byte[] bytes = getBytecode(inser, method.getDeclaringClass()).get();
							iterator.insertAt(blocks[i].position() + offset, bytes);
							int old_off = offset;
							//offset += bytes.length;
							int sizeAfter = ca.getCode().length;

							offset += (sizeAfter - sizeBefore); //insertAt may insert more than bytes.length bytes... For some reasons...

	                        /*if(method.getName().equals("myIfElse")) {
	                            System.out.println("------- total size: " + ca.getCode().length
	                                    + ", added " + (sizeAfter - sizeBefore)
	                                    + ", from " + (blocks[i].position() + old_off)
	                                    + ", to " + ((sizeAfter - sizeBefore) + blocks[i].length()));
	                            //printBlock(iterator, blocks[i].position() + old_off, blocks[i].position() + offset + blocks[i].length(), i);
	                            printDiff(ca, blocks[i].position() + old_off, (sizeAfter - sizeBefore), (sizeAfter - sizeBefore) + blocks[i].length());
	                        }*/

							//Branch out
	                        /*sizeBefore = ca.getCode().length;
	                        bytes = getBytecode(branchOut, method.getDeclaringClass()).get();
	                        //iterator.insertAt(blocks[i].position() + offset + blocks[i].length(), bytes);
	                        iterator.append(bytes);
	                        sizeAfter = ca.getCode().length;
	                        offset += (sizeAfter - sizeBefore);*/
						}
						ca.computeMaxStack();
						//if(ms < 4) ca.setMaxStack(4);
					} catch (BadBytecode badBytecode) {
						badBytecode.printStackTrace();
					} catch (CompileError compileError) {
						compileError.printStackTrace();
					}
				} else {
				}


				method.insertBefore(loggerInstance + ".stepIn(Thread.currentThread().getId(), "
						+ tracePointId
						+ ");");
				method.insertAfter(loggerInstance + ".stepOut(Thread.currentThread().getId());");
			} catch (CannotCompileException e) {
				e.printStackTrace();
				System.err.println("[Yajta] Cannot insert probe in " + method.getDeclaringClass().getName() + " " + method.getName() + method.getSignature() + ", skipping method");
			}

		} else {
			if(verbose) System.err.println("Method: " + className.replace("/", ".") + "." + method.getName() + " is native");
		}
	}
}

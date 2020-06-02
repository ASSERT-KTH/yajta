package se.kth.castor.yajta.api;

import java.io.File;

/**
 * This interface can be extended by the user to create new logging structure.
 * se.kth.castor.yajta.api.FastTracer can insert calls in targeted bytecode to
 * stepIn and stepOut. The required id will be registered first through register.
 */
public interface FastTracking {

	//Offline instrumentation assume that a method
	//public static FastTracking getInstance()
	//exists

	/**
	 * @param clazz Class fully qualified name
	 * @param method method name plus formal parameters
	 * @param branch branch id
	 * @return ann unique id for the triplet (clazz, method, branch)
	 */
	int register(String clazz, String method, String branch);

	/**
	 * @param clazz Class fully qualified name
	 * @param method method name plus formal parameters
	 * @return ann unique id for the pair (clazz, method)
	 */
	int register(String clazz, String method);

	/**
	 * @param thread the thread id
	 * @param id id of the method or block being entered
	 */
	void stepIn(long thread, int id);

	/**
	 * @param thread the thread id exiting a method/block
	 * Note that it is up to the logger to keep track which block is being exited
	 */
	void stepOut(long thread);


	/**
	 * @return is the logger keep track of branches
	 * By default should be false as branch tracking is more expensive
	 */
	boolean traceBranch();

	// OPTIONAL PART (can be empty, not called by default by the framework)

	/**
	 * @param log File in which to write traces
	 */
	void setLogFile(File log);


	/**
	 * Write traces in log file
	 */
	void flush();
}

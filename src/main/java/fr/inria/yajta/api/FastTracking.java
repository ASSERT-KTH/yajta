package fr.inria.yajta.api;

import java.io.File;

public interface FastTracking {
	//getInstance()

	int register(String clazz, String method, String branch);
	int register(String clazz, String method);

	void stepIn(long thread, int id);
	void stepOut(long thread);


	boolean traceBranch();

	// OPTIONAL PART (can be empty, not called by default by the framework)
	void setLogFile(File log);
	void flush();
}

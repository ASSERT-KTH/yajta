package se.kth.castor.yajta.api;

import java.io.File;

/**
 * Interface for tracking classes
 */
public interface Tracking {

    // REQUIRED PART
    /*
     * Must contain a static method getInstance() with no parameter
     */

    /**
     * A call to this method can be inserted at each method entry
     * @param thread the thread id
     * @param clazz fully qualified name of the class being traced
     * @param method method name and signature
     */
    void stepIn(String thread, String clazz, String method);

    /**
     * A call to this method can be inserted at methods exit (always called, even in the presence of multiple returns, finally blocks and thrown exceptions)
     * @param thread the thread id
     */
    void stepOut(String thread);
    
    // OPTIONAL PART (can be empty, not called by default by the framework)

    /**
     * Set output file if relevant
     * @param log log file
     */
    void setLogFile(File log);

    /**
     * Typically the implementation of this method should write to output the traces.
     */
    void flush();
}

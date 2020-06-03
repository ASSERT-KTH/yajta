package se.kth.castor.yajta.api;

import java.io.File;

public interface ValueTracking {

    // REQUIRED PART
    /*
     * Must contain a static method getInstance() with no parameter
     */


    /**
     * A call to this method can be inserted at each method entry
     * @param thread the thread id
     * @param clazz fully qualified name of the class being traced
     * @param method method name and signature
     * @param parameter The acual references to the arguments of the method being traced.
     */
    void stepIn(String thread, String clazz, String method, Object[] parameter);

    /**
     * A call to this method can be inserted at methods exit (always called, even in the presence of multiple returns, finally blocks and thrown exceptions)
     * @param thread the thread id
     * @param returnValue reference to the value being returned by the method
     */
    void stepOut(String thread, Object returnValue);

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

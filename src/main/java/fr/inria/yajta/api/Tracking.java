package fr.inria.yajta.api;

import java.io.File;

/**
 * Interface for tracking classes
 */
public interface Tracking {

    // REQUIRED PART
    /*
     * Must contain a static method getInstance() with no parameter
     */

    /** called at each method entry */
    void stepIn(String thread, String clazz, String method);
    /** called at each method exit (always called, even in the presence of multiple returns, finally blocks and thrown exceptions) */
    void stepOut(String thread);
    
    // OPTIONAL PART (can be empty, not called by default by the framework)
    void setLogFile(File log);
    void flush();
}

package fr.inria.yajta.api;

import java.io.File;

/**
 * Interface for tracking classes
 */
public interface Tracking {

    // REQUIRED PART
    /*
     * Must contain a static method getInstance
     */

    void stepIn(String thread, String clazz, String method);
    void stepOut(String thread);
    
    // OPTIONAL PART (can be empty, not called by default by the framework)
    void setLogFile(File log);
    void flush();
}

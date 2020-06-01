package fr.inria.yajta.api;

import java.io.File;

public interface ValueTracking {

    // REQUIRED PART
    /*
     * Must contain a static method getInstance() with no parameter
     */

    void setLogFile(File log);

    /** called at each method entry */
    void stepIn(String thread, String clazz, String method, Object[] parameter);
    /** called at each method exit (always called, even in the presence of multiple returns, finally blocks and thrown exceptions) */
    void stepOut(String thread, Object returnValue);

    void flush();
}

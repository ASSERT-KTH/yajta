package fr.inria.yajta.api;

import java.io.File;

/**
 * Created by nharrand on 11/07/17.
 */
public interface Tracking {

    /*
     * Must contain a static method getInstance
     */

    void setLogFile(File log);
    void stepIn(String thread, String clazz, String method);
    void stepOut(String thread);
    void flush();
}

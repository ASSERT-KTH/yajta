package fr.inria.yajta.api;

import java.io.File;

public interface ValueTracking {

    void setLogFile(File log);
    void stepIn(String thread, String clazz, String method, Object[] parameter);
    void stepOut(String thread, Object returnValue);
    void flush();
}

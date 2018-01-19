package fr.inria.yajta.processor;

/**
 * Created by nharrand on 11/07/17.
 */
public interface Tracking {
    void stepIn(String thread, String method);
    void stepOut(String thread);
    void flush();

    void trace(String thread, String method, Object returnValue);
    void trace(String thread, String method, boolean returnValue);
    void trace(String thread, String method, byte returnValue);
    void trace(String thread, String method, int returnValue);
    void trace(String thread, String method, long returnValue);
    void trace(String thread, String method, float returnValue);
    void trace(String thread, String method, double returnValue);
    void trace(String thread, String method, short returnValue);
}

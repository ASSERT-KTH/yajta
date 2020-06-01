package fr.inria.yajta.api.loggerimplem;

import fr.inria.yajta.api.ValueTracking;

import java.io.File;

public class IncompleteValueLogger3 implements ValueTracking {
    public static IncompleteValueLogger3 getInstance() {return null;}

    @Override
    public void setLogFile(File log) {

    }

    @Override
    public void stepIn(String thread, String clazz, String method, Object[] parameter) {

    }

    @Override
    public void stepOut(String thread, Object returnValue) {

    }

    @Override
    public void flush() {

    }
}

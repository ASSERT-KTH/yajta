package se.kth.castor.yajta.api.loggerimplem;

import se.kth.castor.yajta.api.ValueTracking;
import java.io.File;

public class IncompleteValueLogger1 implements ValueTracking {

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


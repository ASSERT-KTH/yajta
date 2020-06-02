package se.kth.castor.yajta.api.loggerimplem;


import se.kth.castor.yajta.api.Tracking;

import java.io.File;

public class IncompleteLogger2 implements Tracking {
    public IncompleteLogger2 getInstance() {return null;}

    @Override
    public void setLogFile(File log) {

    }

    @Override
    public void stepIn(String thread, String clazz, String method) {

    }

    @Override
    public void stepOut(String thread) {

    }

    @Override
    public void flush() {

    }
}

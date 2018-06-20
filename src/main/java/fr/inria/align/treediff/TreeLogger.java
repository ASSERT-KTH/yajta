package fr.inria.align.treediff;

import fr.inria.yajta.api.BranchTracking;
import fr.inria.yajta.api.Tracking;
import fr.inria.yajta.api.ValueTracking;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TreeLogger implements Tracking, BranchTracking, ValueTracking {

    Map<String,TreeStore> threadTreeStore;
    File log;

    @Override
    public void stepIn(String thread, String clazz, String method) {

    }

    @Override
    public void stepOut(String thread) {

    }

    public TreeLogger() {
        threadTreeStore = new HashMap<>();
    }

    //If used outside of agent
    static TreeLogger instance ;
    public static TreeLogger getInstance() {
        if(instance == null) instance = new TreeLogger();
        return instance;
    }

    @Override
    public void setLogFile(File log) {
        this.log = log;
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

    @Override
    public void branchIn(String thread, String branch) {

    }

    @Override
    public void branchOut(String thread) {

    }
}

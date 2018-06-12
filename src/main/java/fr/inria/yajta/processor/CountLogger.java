package fr.inria.yajta.processor;

import fr.inria.yajta.Agent;
import fr.inria.yajta.api.BranchTracking;
import fr.inria.yajta.api.Tracking;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CountLogger implements Tracking, BranchTracking {
    public File log;
    BufferedWriter bufferedWriter;
    int nodes;
    int branches;

    public CountLogger() {
        nodes = 0;
        branches = 0;
    }

    //If used outside of agent
    static CountLogger instance ;
    public static CountLogger getInstance() {
        if(instance == null) instance = new CountLogger();
        return instance;
    }

    @Override
    public void setLogFile(File log) {
        this.log = log;
    }

    public synchronized void stepIn(String thread, String clazz, String method) {
        nodes++;
    }

    public synchronized void stepOut(String thread) {
    }

    @Override
    public void branchIn(String thread, String branch) {
        branches++;
    }

    @Override
    public void branchOut(String thread) {
    }

    public void flush() {
        if(log == null) {
            int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
            log = new File("log" + i + ".json");
        }
        try {
            if(log.exists()) log.delete();
            log.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(log, true));
            bufferedWriter.append("{\"name\":\"Threads\", " +
                    "\"yajta-version\": \"" + Agent.yajtaVersionUID + "\", " +
                    "\"serialization-version\": " + TreeNode.serialVersionUID + ", " +
                    "\"nodes\": " + nodes + ", " +
                    "\"branches\": " + branches +
                    "}");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
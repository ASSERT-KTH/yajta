package se.kth.castor.yajta.processor.loggers;

import se.kth.castor.yajta.Agent;
import se.kth.castor.yajta.api.BranchTracking;
import se.kth.castor.yajta.api.Tracking;
import se.kth.castor.yajta.processor.TreeNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class LinearLogger implements Tracking, BranchTracking {
    public File log;
    private boolean verbose = true;
    BufferedWriter bufferedWriter;
    //MyList<String> logs = new MyList<>();
    int max;
    String[] logs;
    int i;

    public LinearLogger() {
        max = 10;
        logs = new String[max];
        i = 0;
    }

    private void add(String str) {
        if(i == max) {
            //String[] n = Arrays.copyOfRange(logs,0,max);
            //logs = new String[2*max];
            logs = Arrays.copyOf(logs, 2*max);
            max = 2 * max;
            //logs = n;
        }
        logs[i] = str;
        i++;
    }

    static LinearLogger instance ;
    public static LinearLogger getInstance() {
        if(instance == null) {
            instance = new LinearLogger();
        }
        return instance;
    }

    @Override
    public void branchIn(String thread, String branch) {
        add(branch);
    }

    @Override
    public void branchOut(String thread) {

    }

    @Override
    public void stepIn(String thread, String clazz, String method) {
        if(verbose) System.err.println("[yajta] stepIn");
        add(clazz + "." + method);
        if(verbose) System.err.println("[yajta] stepIn");
    }

    @Override
    public void stepOut(String thread) {

    }

    @Override
    public void setLogFile(File log) {
        this.log = log;
    }

    @Override
    public void flush() {
        if(verbose) System.err.println("[yajta] flush");
        if(log == null) {
            int k = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
            log = new File("log" + k + ".json");
        }
        try {
            if(log.exists()) log.delete();
            log.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(log, true));
            if(verbose) System.err.println("{\"name\":\"Threads\", " +
                    "\"yajta-version\": \"" + Agent.yajtaVersionUID + "\", " +
                    "\"serialization-version\": " + TreeNode.serialVersionUID + ", " +
                    "\"children\":[");
            bufferedWriter.append("{\"name\":\"Threads\", " +
                    "\"yajta-version\": \"" + Agent.yajtaVersionUID + "\", " +
                    "\"serialization-version\": " + TreeNode.serialVersionUID + ", " +
                    "\"children\":[");
            boolean isFirst = true;
            for(int j = 0; j < i; j++) {
                if(verbose) System.err.println("entry: " + logs[j]);
                if (isFirst) isFirst = false;
                else bufferedWriter.append(",");
                bufferedWriter.append("\"" + logs[j] + "\"");
            }
            if(verbose) System.err.println("]}");
            bufferedWriter.append("]}");
            bufferedWriter.flush();
            if(verbose) System.err.println("[yajta] flush done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

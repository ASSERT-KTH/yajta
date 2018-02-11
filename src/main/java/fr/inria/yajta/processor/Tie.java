package fr.inria.yajta.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nharrand on 12/07/17.
 */
public class Tie implements Tracking {
    public File log;
    BufferedWriter bufferedWriter;

    Map<String, Set<String>> threadLogs = new HashMap<>();

    public synchronized void stepIn(String thread, String clazz, String method) {
        Set<String> entry = threadLogs.get(thread);
        if(entry == null) {
            entry = new HashSet<>();
        }
        entry.add(clazz + "." + method);
        threadLogs.put(thread,entry);
    }

    public synchronized void stepOut(String thread) {
    }

    public void flush() {
        if(log == null) {
            int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
            log = new File("log" + i);
        }
        try {
            if(log.exists()) log.delete();
            log.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(log, true));
            boolean isFirst = true;
            bufferedWriter.append("{\"report\":[");
            for(Map.Entry<String, Set<String>> e: threadLogs.entrySet()) {
                if(isFirst) isFirst = false;
                else bufferedWriter.append(",");
                bufferedWriter.append("{\"thread\":\"" + e.getKey() + "\", \"methods\":[");
                boolean f = true;
                for(String m: e.getValue()) {
                    if(f) f = false;
                    else bufferedWriter.append(",");
                    bufferedWriter.append("\"" + m + "\"");
                }
                bufferedWriter.append("]}");
            }
            bufferedWriter.append("]}");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trace(String thread, String clazz, String method, Object returnValue) {

    }

    @Override
    public void trace(String thread, String clazz, String method, boolean returnValue) {

    }

    @Override
    public void trace(String thread, String clazz, String method, byte returnValue) {

    }

    @Override
    public void trace(String thread, String clazz, String method, int returnValue) {

    }

    @Override
    public void trace(String thread, String clazz, String method, long returnValue) {

    }

    @Override
    public void trace(String thread, String clazz, String method, float returnValue) {

    }

    @Override
    public void trace(String thread, String clazz, String method, double returnValue) {

    }

    @Override
    public void trace(String thread, String clazz, String method, short returnValue) {

    }

}
package fr.inria.yajta.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by nharrand on 19/04/17.
 */
public class Logger implements Tracking {
    public File log;
    public boolean tree = true;
    BufferedWriter bufferedWriter;

    Map<String, Map.Entry<TreeNode, TreeNode>> threadLogs = new HashMap<>();

    public synchronized void stepIn(String thread, String method) {
        Map.Entry<TreeNode, TreeNode> entry = threadLogs.get(thread);
        if(entry == null) {
            TreeNode cur = new TreeNode();
            cur.method = thread;
            entry = new HashMap.SimpleEntry<>(cur,cur.addChild(method));
            threadLogs.put(thread, entry);
        } else {
            entry.setValue(entry.getValue().addChild(method));
            threadLogs.put(thread,entry);
        }
    }

    public synchronized void stepOut(String thread) {
        Map.Entry<TreeNode, TreeNode> entry = threadLogs.get(thread);
        if(entry != null) {
            if(entry.getValue() != null) entry.setValue(entry.getValue().parent);
        }
    }

    public void flush() {
        if(log == null) {
            int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
            if(tree) log = new File("log" + i + ".json");
            else log = new File("log" + i);
        }
        try {
            if(log.exists()) log.delete();
            log.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(log, true));
            if(tree) bufferedWriter.append("{\"name\":\"Threads\", \"children\":[");
            boolean isFirst = true;
            for(Map.Entry<String, Map.Entry<TreeNode, TreeNode>> e: threadLogs.entrySet()) {
                if (isFirst) isFirst = false;
                else if(tree) bufferedWriter.append(",");
                e.getValue().getKey().print(bufferedWriter, tree);
            }
            if(tree) bufferedWriter.append("]}");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trace(String thread, String method, Object returnValue) {

    }

    @Override
    public void trace(String thread, String method, boolean returnValue) {

    }

    @Override
    public void trace(String thread, String method, byte returnValue) {

    }

    @Override
    public void trace(String thread, String method, int returnValue) {

    }

    @Override
    public void trace(String thread, String method, long returnValue) {

    }

    @Override
    public void trace(String thread, String method, float returnValue) {

    }

    @Override
    public void trace(String thread, String method, double returnValue) {

    }

    @Override
    public void trace(String thread, String method, short returnValue) {

    }

}

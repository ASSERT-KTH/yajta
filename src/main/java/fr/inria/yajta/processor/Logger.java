package fr.inria.yajta.processor;

import fr.inria.yajta.processor.util.MyEntry;
import fr.inria.yajta.processor.util.MyMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by nharrand on 19/04/17.
 */
public class Logger implements Tracking {
    public File log;
    public boolean tree = true;
    BufferedWriter bufferedWriter;

    MyMap<String, MyEntry<TreeNode, TreeNode>> threadLogs = new MyMap<>();

    public synchronized void stepIn(String thread, String clazz, String method) {
        MyEntry<TreeNode, TreeNode> entry = threadLogs.get(thread);
        if(entry == null) {
            TreeNode cur = new TreeNode();
            cur.method = thread;
            entry = new MyEntry<>(cur,cur.addChild(clazz, method));
            threadLogs.put(thread, entry);
        } else {
            entry.setValue(entry.getValue().addChild(clazz, method));
            threadLogs.put(thread,entry);
        }
    }

    public synchronized void stepOut(String thread) {
        MyEntry<TreeNode, TreeNode> entry = threadLogs.get(thread);
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
            for(MyEntry<String, MyEntry<TreeNode, TreeNode>> e: threadLogs.entryList()) {
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

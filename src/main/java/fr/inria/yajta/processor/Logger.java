package fr.inria.yajta.processor;

import fr.inria.yajta.Agent;
import fr.inria.yajta.api.BranchTracking;
import fr.inria.yajta.api.Tracking;
import fr.inria.yajta.processor.util.MyEntry;
import fr.inria.yajta.processor.util.MyMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by nharrand on 19/04/17.
 */
public class Logger implements Tracking, BranchTracking {
    boolean verbose = true;
    public File log;
    public boolean tree = true;
    BufferedWriter bufferedWriter;

    MyMap<String, MyEntry<TreeNode, TreeNode>> threadLogs = new MyMap<>();

    //If used outside of agent
    static Logger instance = new Logger();
    public static Logger getInstance() {
        return instance;
    }

    @Override
    public void setLogFile(File log) {
        this.log = log;
    }

    public synchronized void stepIn(String thread, String clazz, String method) {
        if(verbose) System.err.println("[yajta][" + thread + "] stepIn " + method);
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
        if(verbose) System.err.println("[yajta][" + thread + "] stepOut");
        MyEntry<TreeNode, TreeNode> entry = threadLogs.get(thread);
        if(entry != null) {
            if(entry.getValue() != null) entry.setValue(entry.getValue().parent);
        }
    }

    @Override
    public void branchIn(String thread, String branch) {
        if(verbose) System.err.println("[yajta][" + thread + "] branchIn " + branch);
        MyEntry<TreeNode, TreeNode> entry = threadLogs.get(thread);
        if(entry != null) {
            if(entry.getValue() != null) {
                String branchName = "@" + branch;
                String clazz = "@branch";
                entry.getValue().addChild(clazz, branchName);
                //stepIn(thread, clazz, branchName);
            }
        }
    }

    @Override
    public void branchOut(String thread) {
        //if(verbose) System.err.println("[yajta][" + thread + "] branchOut");
        //stepOut(thread);
    }

    public void flush() {
        if(verbose) System.err.println("[yajta] flush");
        if(log == null) {
            int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
            if(tree) log = new File("log" + i + ".json");
            else log = new File("log" + i);
        }
        try {
            if(log.exists()) log.delete();
            log.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(log, true));
            if(verbose) System.err.println("{\"name\":\"Threads\", " +
                    "\"yajta-version\": \"" + Agent.yajtaVersionUID + "\", " +
                    "\"serialization-version\": " + TreeNode.serialVersionUID + ", " +
                    "\"children\":[");
            if(tree) bufferedWriter.append("{\"name\":\"Threads\", " +
                    "\"yajta-version\": \"" + Agent.yajtaVersionUID + "\", " +
                    "\"serialization-version\": " + TreeNode.serialVersionUID + ", " +
                    "\"children\":[");
            boolean isFirst = true;
            for(MyEntry<String, MyEntry<TreeNode, TreeNode>> e: threadLogs.entryList()) {
                if(verbose) System.err.println("entry: " + e.getKey());
                if (isFirst) isFirst = false;
                else if(tree) bufferedWriter.append(",");
                e.getValue().getKey().print(bufferedWriter, tree);
            }
            if(verbose) System.err.println("]}");
            if(tree) bufferedWriter.append("]}");
            bufferedWriter.flush();
            if(verbose) System.err.println("[yajta] flush done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

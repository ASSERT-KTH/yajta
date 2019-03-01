package fr.inria.yajta.processor.loggers;

import fr.inria.yajta.Agent;
import fr.inria.yajta.api.ValueTrace;
import fr.inria.yajta.api.ValueTracking;
import fr.inria.yajta.processor.TreeNode;
import fr.inria.yajta.processor.util.MyEntry;
import fr.inria.yajta.processor.util.MyMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ValueLogger implements ValueTracking {

    private MyMap<String, MyEntry<ValueTrace, ValueTrace>> threadLogs;
    public File log;

    public ValueLogger() {
        threadLogs = new MyMap<>();
    }

    //If used outside of agent
    static ValueLogger instance ;

    public static ValueLogger getInstance() {
        if(instance == null) instance = new ValueLogger();
        return instance;
    }

    @Override
    public void setLogFile(File log) {
        this.log = log;
    }

    @Override
    public void stepIn(String thread, String clazz, String method, Object[] parameter) {
        MyEntry<ValueTrace, ValueTrace> entry = threadLogs.get(thread);
        if(entry == null) {
            ValueTrace cur = ValueTrace.getThreadDeclaration(thread);
            entry = new MyEntry<>(cur,cur.addChild(clazz,method,parameter));
            threadLogs.put(thread, entry);
        } else {
            entry.setValue(entry.getValue().addChild(clazz,method,parameter));
            threadLogs.put(thread,entry);
        }

    }

    @Override
    public void stepOut(String thread, Object returnValue) {
        MyEntry<ValueTrace, ValueTrace> entry = threadLogs.get(thread);
        if(entry != null) {
            if(entry.getValue() != null) {
                entry.getValue().setReturnValue(returnValue);
                entry.setValue(entry.getValue().getParent());
            }
        }
    }

    @Override
    public void flush() {

        if(log == null) {
            int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
            log = new File("log" + i + ".json");
        }
        try {
            if(log.exists()) log.delete();
            log.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(log, true));
            bufferedWriter.append("{\"name\":\"Threads\", " +
                    "\"yajta-version\": \"" + Agent.yajtaVersionUID + "\", " +
                    "\"serialization-version\": " + TreeNode.serialVersionUID + ", " +
                    "\"children\":[");
            boolean isFirst = true;
            for(MyEntry<String, MyEntry<ValueTrace, ValueTrace>> e: threadLogs.entryList()) {
                if (isFirst) isFirst = false;
                else bufferedWriter.append(",");
                e.getValue().getKey().print(bufferedWriter);
            }
            bufferedWriter.append("]}");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

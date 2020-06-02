package se.kth.castor.yajta.processor.loggers;

import se.kth.castor.yajta.api.Tracking;
import se.kth.castor.yajta.processor.util.MyEntry;
import se.kth.castor.yajta.processor.util.MyMap;
import se.kth.castor.yajta.processor.util.MySet;
import se.kth.castor.yajta.processor.util.MyEntry;
import se.kth.castor.yajta.processor.util.MyMap;
import se.kth.castor.yajta.processor.util.MySet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by nharrand on 12/07/17.
 */
public class Tie implements Tracking {
    public File log;
    BufferedWriter bufferedWriter;

    MyMap<String, MySet<String>> threadLogs = new MyMap<>();

    @Override
    public void setLogFile(File log) {
        this.log = log;
    }

    public synchronized void stepIn(String thread, String clazz, String method) {
        MySet<String> entry = threadLogs.get(thread);
        if(entry == null) {
            entry = new MySet<>();
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
            for(MyEntry<String, MySet<String>> e: threadLogs.entryList()) {
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

}
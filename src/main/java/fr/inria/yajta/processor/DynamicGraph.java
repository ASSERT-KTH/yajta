package fr.inria.yajta.processor;

import fr.inria.yajta.api.Tracking;
import fr.inria.yajta.processor.util.MyMap;
import fr.inria.yajta.processor.util.MyStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class DynamicGraph implements Tracking {

    MyMap<String, MyMap<String, MyMap<String, Integer>>> matrix = new MyMap<>(); //Thread -> Caller -> Callee -> #called
    MyMap<String, MyStack<String>> previous = new MyMap<>();

    public File log;

    @Override
    public void setLogFile(File log) {
        this.log = log;
    }

    @Override
    public void stepIn(String thread, String clazz, String method) {
        MyStack<String> threadPrevious = previous.get(thread);
        String callee = clazz + "." + method;
        if(threadPrevious == null) threadPrevious = new MyStack<>();
        else {
            String caller = threadPrevious.peek();
            if(caller != null) {
                MyMap<String, MyMap<String, Integer>> threadMatrix = matrix.get(thread);
                if(threadMatrix == null) threadMatrix = new MyMap<>();
                MyMap<String, Integer> callerMatrix = threadMatrix.get(caller);
                if(callerMatrix == null) callerMatrix = new MyMap<>();
                Integer numberCalled = callerMatrix.get(callee);
                if(numberCalled == null) numberCalled = 0;
                numberCalled++;
                callerMatrix.put(callee,numberCalled);
                threadMatrix.put(caller,callerMatrix);
                matrix.put(thread,threadMatrix);
            }
        }
        threadPrevious.push(callee);
        previous.put(thread,threadPrevious);
    }

    @Override
    public void stepOut(String thread) {
        if(previous.get(thread) == null) {
            System.err.println("[ERROR] step out of an unknown thread");
        } else {
            if(previous.get(thread).isEmpty()) {
                System.err.println("[ERROR] step out of a thread with an empty call stack");
            } else {
                previous.get(thread).pop();
            }
        }
    }

    BufferedWriter bufferedWriter;

    @Override
    public void flush() {
        if(log == null) {
            int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
            log = new File("log" + i + ".json");
        }
        try {
            if(log.exists()) log.delete();
            log.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(log, true));
            bufferedWriter.append("[");

            boolean isFirst = true;
            /*MyList threads = matrix.keyList();
            for(int i = 0; i < threads.size(); i++) {
                String thread = (String) threads.get(i);*/
            for(String thread: matrix.keyList()) {
                if (isFirst) isFirst = false;
                else bufferedWriter.append(",");

                bufferedWriter.append("{ \"thread\": \"" + thread + "\", \"callgraph\":[");

                MyMap<String, MyMap<String, Integer>> threadMatrix = matrix.get(thread);
                boolean isFirst2 = true;
                /*MyList callers = threadMatrix.keyList();
                for(int j = 0; j < callers.size(); j++) {
                    String caller = (String) callers.get(j);*/
                for(String caller: threadMatrix.keyList()) {
                //for(String caller: threadMatrix.keySet()) {
                    if (isFirst2) isFirst2 = false;
                    else bufferedWriter.append(",");
                    bufferedWriter.append("{ \"caller\": \"" + caller + "\", \"called\":[");

                    MyMap<String, Integer> callerMatrix = threadMatrix.get(caller);
                    boolean isFirst3 = true;
                    /*MyList callees = callerMatrix.keyList();
                    for(int k = 0; k < callees.size(); k++) {
                        String callee = (String) callees.get(k);*/
                    for(String callee: callerMatrix.keyList()) {
                    //for(String callee: callerMatrix.keySet()) {
                        if (isFirst3) isFirst3 = false;
                        else bufferedWriter.append(",");
                        bufferedWriter.append("{ \"callee\": \"" + callee + "\", \"nb\": " + callerMatrix.get(callee));

                        bufferedWriter.append("}");
                    }

                    bufferedWriter.append("]}");
                }

                bufferedWriter.append("]}");

            }

            bufferedWriter.append("]");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

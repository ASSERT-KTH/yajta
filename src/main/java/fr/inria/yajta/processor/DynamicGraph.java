package fr.inria.yajta.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class DynamicGraph implements Tracking {

    Map<String, Map<String, Map<String, Integer>>> matrix = new HashMap<>(); //Thread -> Caller -> Callee -> #called
    Map<String, Stack<String>> previous = new HashMap<>();


    public File log;

    @Override
    public void stepIn(String thread, String clazz, String method) {
        Stack<String> threadPrevious = previous.get(thread);
        String callee = clazz + "." + method;
        if(threadPrevious == null) threadPrevious = new Stack<>();
        else {
            String caller = threadPrevious.peek();
            if(caller != null) {
                Map<String, Map<String, Integer>> threadMatrix = matrix.get(thread);
                if(threadMatrix == null) threadMatrix = new HashMap<>();
                Map<String, Integer> callerMatrix = threadMatrix.get(caller);
                if(callerMatrix == null) callerMatrix = new HashMap<>();
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
            for(String thread: matrix.keySet()) {
                if (isFirst) isFirst = false;
                else bufferedWriter.append(",");

                bufferedWriter.append("{ \"thread\": \"" + thread + "\", \"callgraph\":[");

                Map<String, Map<String, Integer>> threadMatrix = matrix.get(thread);
                boolean isFirst2 = true;
                for(String caller: threadMatrix.keySet()) {
                    if (isFirst2) isFirst2 = false;
                    else bufferedWriter.append(",");
                    bufferedWriter.append("{ \"caller\": \"" + caller + "\", \"called\":[");

                    Map<String, Integer> callerMatrix = threadMatrix.get(caller);
                    boolean isFirst3 = true;
                    for(String callee: callerMatrix.keySet()) {
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

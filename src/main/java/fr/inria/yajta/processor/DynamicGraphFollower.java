package fr.inria.yajta.processor;

import fr.inria.yajta.FileHelper;
import fr.inria.yajta.api.Tracking;
import fr.inria.yajta.processor.util.MyMap;
import fr.inria.yajta.processor.util.MyStack;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by nharrand on 11/07/17.
 */
public class DynamicGraphFollower implements Tracking {

    MyMap<String, MyMap<String, MyMap<String, Integer>>> matrix = new MyMap<>(); //Thread -> Caller -> Callee -> #called
    MyMap<String, MyStack<String>> previous = new MyMap<>();
    MyMap<String, Boolean> threadOfftrack = new MyMap<>();

    @Override
    public void setLogFile(File log) {

    }

    public synchronized void stepIn(String thread, String clazz, String method) {
        if(!threadOfftrack.containsKey(thread) || threadOfftrack.get(thread)) return;
        MyStack<String> threadPrevious = previous.get(thread);
        String callee = clazz + "." + method;
        if(threadPrevious == null) threadPrevious = new MyStack<>();
        else {
            String caller = threadPrevious.peek();
            if(caller != null) {
                MyMap<String, MyMap<String, Integer>> threadMatrix = matrix.get(thread);
                if(threadMatrix == null) {
                    offTrack(thread, caller, "unknown thread");
                    return;
                }
                MyMap<String, Integer> callerMatrix = threadMatrix.get(caller);
                if(callerMatrix == null) {
                    offTrack(thread, caller, "unknown caller");
                    return;
                }
                Integer numberCalled = callerMatrix.get(callee);
                if(numberCalled == null)  {
                    offTrack(thread, caller, "unknown callee: " + method);
                    return;
                }
                if(numberCalled == 0) {
                    offTrack(thread, caller, "additional call to " + method);
                    return;
                }
                numberCalled--;
                callerMatrix.put(callee,numberCalled);
                threadMatrix.put(caller,callerMatrix);
                matrix.put(thread,threadMatrix);
            }
        }
        threadPrevious.push(callee);
        previous.put(thread,threadPrevious);
    }

    public synchronized void stepOut(String thread) {
        if(!threadOfftrack.containsKey(thread) || threadOfftrack.get(thread)) return;
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

    public void load(File trace) {
        JSONArray threads = FileHelper.readArrayFromFile(trace);

        try {
            //
            for(int i = 0; i < threads.length(); i++) {
                MyMap<String, MyMap<String, Integer>> callgraphMap = new MyMap<>();
                JSONObject thread = threads.getJSONObject(i);
                String threadName = thread.getString("thread");
                JSONArray callgraph = thread.getJSONArray("callgraph");
                for(int j = 0; j < callgraph.length(); j++) {
                    MyMap<String, Integer> callerMap = new MyMap<>();
                    JSONObject caller = callgraph.getJSONObject(j);
                    String callerName = caller.getString("caller");
                    JSONArray calleds = caller.getJSONArray("called");
                    for(int k = 0; k < calleds.length(); k++) {
                        JSONObject callee = calleds.getJSONObject(j);
                        String calleeName = callee.getString("callee");
                        Integer nb = callee.getInt("nb");
                        callerMap.put(calleeName,nb);
                    }
                    callgraphMap.put(callerName,callerMap);
                }
                matrix.put(threadName,callgraphMap);
                threadOfftrack.put(threadName,false);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void offTrack(String thread, String method, String cur) {
        System.err.println("[OFF TRACK] <" + method + ">: " + cur + "");
        threadOfftrack.put(thread,true);

    }

    public void flush() {
        for(String thread: matrix.keyList()) {
            MyMap<String, MyMap<String, Integer>> callgraph = matrix.get(thread);
            for(String caller: callgraph.keyList()) {
                MyMap<String, Integer> callees = callgraph.get(caller);
                for(String callee: callees.keyList()) {
                    if(callees.get(callee) > 0) {
                        offTrack(thread, caller, "missing call to " + callee);
                        return;
                    }
                }
            }
        }

    }
}

package fr.inria.yajta.processor;

import fr.inria.yajta.FileHelper;
import fr.inria.yajta.processor.util.MyMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by nharrand on 11/07/17.
 */
public class Follower implements Tracking {

    MyMap<String, TreeNode> threadLogs = new MyMap<>();
    MyMap<String, Boolean> threadOfftrack = new MyMap<>();

    public synchronized void stepIn(String thread, String clazz, String method) {
        if(!threadOfftrack.containsKey(thread) || threadOfftrack.get(thread)) return;
        //System.err.println("[" + thread + "] " + method + "{");
        TreeNode cur = threadLogs.get(thread);
        if(cur == null) offTrack(thread,clazz + "." + method, "NO CHILD");
        else {
            if(cur.hasNext()) {
                cur = cur.next();
                if((cur.method.compareTo(method) != 0) && (cur.clazz.compareTo(clazz) ==0)) {
                    offTrack(thread, clazz + "." + method, cur.clazz + "." + cur.method);
                } else {
                    threadLogs.put(thread,cur);
                }
            } else offTrack(thread, clazz + "." + method, "DONE");
        }
    }

    public synchronized void stepOut(String thread) {
        //System.err.println("[" + thread + "] }");
    }

    public void load(File trace) {
        JSONObject o = FileHelper.readFromFile(trace);

        try {
            JSONArray threads = o.getJSONArray("children");
            for(int i = 0; i < threads.length(); i++) {
                TreeNode t = new TreeNode(threads.getJSONObject(i));
                threadLogs.put(t.method,t);
                threadOfftrack.put(t.method,false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void offTrack(String thread, String method, String cur) {
        System.err.println("[OFF TRACK] <" + method + "> instad of <" + cur + ">");
        threadOfftrack.put(thread,true);

    }

    public void flush() {

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

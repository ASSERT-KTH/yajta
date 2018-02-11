package fr.inria.yajta.processor;


import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nharrand on 27/07/17.
 */
public class ReturnLogger implements Tracking {
    public File log;
    BufferedWriter bufferedWriter;

    Map<String, Map<String, List<Serializable>>> logs = new ConcurrentHashMap<>();

    public void trace(String thread, String clazz, String method, Object returnValue) {
        if( returnValue instanceof java.io.Serializable ) {
            Map<String, List<Serializable>> threadLogs;
            if(!logs.containsKey(thread)) threadLogs = new ConcurrentHashMap<>();
            else threadLogs = logs.get(thread);

            List<Serializable> values;
            if(!threadLogs.containsKey(clazz + "." + method)) values = new ArrayList<>();
            else values = threadLogs.get(clazz + "." + method);

            values.add((Serializable) returnValue);
            threadLogs.put(clazz + "." + method,values);
            logs.put(thread,threadLogs);
        }
    }

    @Override
    public void trace(String thread, String clazz, String method, boolean returnValue) {
        trace(thread,clazz,method, new Boolean(returnValue));
    }

    @Override
    public void trace(String thread, String clazz, String method, byte returnValue) {
        trace(thread,clazz,method, new Byte(returnValue));
    }

    @Override
    public void trace(String thread, String clazz, String method, int returnValue) {
        trace(thread,clazz,method, new Integer(returnValue));
    }

    @Override
    public void trace(String thread, String clazz, String method, long returnValue) {
        trace(thread,clazz,method, new Long(returnValue));
    }

    @Override
    public void trace(String thread, String clazz, String method, float returnValue) {
        trace(thread,clazz,method, new Float(returnValue));
    }

    @Override
    public void trace(String thread, String clazz, String method, double returnValue) {
        trace(thread,clazz,method, new Double(returnValue));
    }

    @Override
    public void trace(String thread, String clazz, String method, short returnValue) {
        trace(thread,clazz,method, new Short(returnValue));
    }

    @Override
    public void stepIn(String thread, String clazz, String method) {

    }

    @Override
    public void stepOut(String thread) {

    }

    public void flush() {
        if(log == null) {
            int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
            //log = new File("log" + i + ".json");
            log = new File("log" + i + ".ser");
        }
        try {
            if(log.exists()) log.delete();
            log.createNewFile();
            /*bufferedWriter = new BufferedWriter(new FileWriter(log, true));
            bufferedWriter.append("{\"name\":\"Threads\", \"children\":[\n");
            Set<Map.Entry<String, Map<String, List<Serializable>>>> s = logs.entrySet();
            boolean isFirst0 = true;
            for(Map.Entry<String, Map<String, List<Serializable>>> t: s) {
                if(isFirst0) isFirst0 = false;
                else bufferedWriter.append(",");
                bufferedWriter.append("{\"thread\":\"" + t.getKey() + "\", \"methods\":[\n");
                boolean isFirst1 = true;
                for(Map.Entry<String, List<Serializable>> m: t.getValue().entrySet()) {
                    if(isFirst1) isFirst1 = false;
                    else bufferedWriter.append(",");
                    bufferedWriter.append("{\"method\":\"" + m.getKey() + "\", \"values\":[\n");
                    boolean isFirst2 = true;
                    for(Serializable v: m.getValue()) {
                        if(isFirst2) isFirst2 = false;
                        else bufferedWriter.append(",");

                        bufferedWriter.append("\"" +
                                java.util.Base64.getEncoder().encodeToString(serialize(v))
                                + "\"");
                    }
                    bufferedWriter.append("\n]}");
                }
                bufferedWriter.append("\n]}");
            }
            bufferedWriter.append("\n]}");
            bufferedWriter.flush();*/
            FileOutputStream fileOut = new FileOutputStream(log);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(logs);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] serialize(Serializable o) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(baos);
            os.writeObject(o);
            os.flush();
            os.close();
            return baos.toByteArray();
        } catch (NotSerializableException e) {}
        return new byte[0];
    }
}

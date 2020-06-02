package se.kth.castor.yajta.api.loggerimplem;

import se.kth.castor.yajta.api.Tracking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestLogger implements Tracking {
    static TestLogger instance;

    public List<Log> logs = new ArrayList<>();

    public static TestLogger getInstance() {
        if(instance == null) {
            instance = new TestLogger();
            // This logger is not meant to be used outside of tests, therefor,
            // logs will never be written down in a file, so
            // there is no needs to register a shutdown hook but it would look like this:

            /*
             * instance.setLogFile(new File("my-traces.json"));
             * Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {
             *   public void run() {
             *      TestFastLogger.getInstance().flush();
             *   }
             * });
            */
        }
        return instance;
    }

    @Override
    public void setLogFile(File log) {
        //This logger is not meant to be used outside of tests, therefor, logs will never be written down in a file
    }

    @Override
    public void stepIn(String thread, String clazz, String method) {
        logs.add(new Log(thread, clazz, method));
    }

    @Override
    public void stepOut(String thread) {
        logs.add(new Log(thread));
    }

    @Override
    public void flush() {
        //This logger is not meant to be used outside of tests, therefor, logs will never be written down in a file
    }

    public enum LOGTYPE {IN,OUT}
    public class Log {
        public LOGTYPE type;
        public String thread;
        public String clazz;
        public String method;
        public Log(String thread, String clazz, String method) {
            this.thread = thread;
            this.clazz = clazz;
            this.method = method;
            type = LOGTYPE.IN;
        }
        public Log(String thread) {
            this.thread = thread;
            type = LOGTYPE.OUT;
        }
    }
}

package fr.inria.yajta.api.loggerimplem;

import fr.inria.yajta.api.BranchTracking;
import fr.inria.yajta.api.Tracking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestBranchLogger implements Tracking,BranchTracking {
    static TestBranchLogger instance;

    public List<Log> log = new ArrayList<>();

    public static TestBranchLogger getInstance() {
        if(instance == null) {
            instance = new TestBranchLogger();
            // This logger is not meant to be used outside of tests, therefor,
            // logs will never be written down in a file, so
            // there is no needs to register a shutdown hook but it would look like this:

            /*
             * instance.setLogFile(new File("my-traces.json"));
             * Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {
             *   public void run() {
             *      TestLogger.getInstance().flush();
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
        log.add(new Log(thread, clazz, method));
    }

    @Override
    public void stepOut(String thread) {
        log.add(new Log(thread));
    }

    @Override
    public void branchIn(String thread, String branch) {
        log.add(new Log(thread, branch));
    }

    @Override
    public void branchOut(String thread) {
        log.add(new Log(thread, 0));
    }

    @Override
    public void flush() {
        //This logger is not meant to be used outside of tests, therefor, logs will never be written down in a file
    }

    public enum LOGTYPE {IN, OUT, BIN, BOUT}
    public class Log {
        public LOGTYPE type;
        public String thread;
        public String clazz;
        public String method;
        public String branch;
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
        public Log(String thread, String branch) {
            //System.out.println("["+thread+"] " + branch);
            this.thread = thread;
            this.branch = branch;
            type = LOGTYPE.BIN;
        }
        public Log(String thread, int moche) {
            //System.out.println("["+thread+"] branch out");
            this.thread = thread;
            type = LOGTYPE.BOUT;
        }
    }
}

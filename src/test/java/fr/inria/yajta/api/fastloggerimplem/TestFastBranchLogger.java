package fr.inria.yajta.api.fastloggerimplem;

import fr.inria.yajta.api.AbstractFastTracking;
import fr.inria.yajta.api.BranchTracking;
import fr.inria.yajta.api.Tracking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestFastBranchLogger extends AbstractFastTracking {
    static TestFastBranchLogger instance;

    private List<Log> log = new ArrayList<>();

    public static TestFastBranchLogger getInstance() {
        if(instance == null) {
            instance = new TestFastBranchLogger();
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
    public void stepIn(long thread, int id) {
        log.add(new Log(thread, id));
    }

    @Override
    public void stepOut(long thread) {
        log.add(new Log(thread));
    }

    @Override
    public void flush() {
        //This logger is not meant to be used outside of tests, therefor, logs will never be written down in a file
    }

    public enum LOGTYPE {IN, OUT}
    public class Log {
        public LOGTYPE type;
        public long thread;
        public int id;
        public Log(long thread, int id) {
            this.thread = thread;
            this.id = id;
            type = LOGTYPE.IN;
        }
        public Log(long thread) {
            this.thread = thread;
            type = LOGTYPE.OUT;
        }
    }
}

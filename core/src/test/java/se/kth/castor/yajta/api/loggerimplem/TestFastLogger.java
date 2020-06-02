package se.kth.castor.yajta.api.loggerimplem;

import com.google.common.collect.BiMap;
import se.kth.castor.yajta.api.AbstractFastTracking;
import se.kth.castor.yajta.api.FastTracking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestFastLogger extends AbstractFastTracking implements FastTracking {
    public static boolean traceBranch = false;
    static TestFastLogger instance;

    public List<Log> logs = new ArrayList<>();

    public static TestFastLogger getInstance() {
        if(instance == null) {
            instance = new TestFastLogger();
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
    public void stepIn(long thread, int id) {
        logs.add(new Log(thread, id));
    }

    @Override
    public void stepOut(long thread) {
        logs.add(new Log(thread));
    }

    @Override
    public boolean traceBranch() {
        return traceBranch;
    }

    @Override
    public void flush() {
        //This logger is not meant to be used outside of tests, therefor, logs will never be written down in a file
    }

    public enum LOGTYPE {IN,OUT}
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

        public boolean isBranch(BiMap<Integer, String> dico) {
            return dico.get(id).contains("#");
        }

        public String getElementName(BiMap<Integer, String> dico) {
            return dico.get(id);
        }
    }
}

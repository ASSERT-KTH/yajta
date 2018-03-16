package fr.inria.yajta.api.loggerimplem;

import fr.inria.yajta.api.Tracking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestLogger implements Tracking {
    static TestLogger instance = new TestLogger();

    public List<Log> log = new ArrayList<>();

    public static TestLogger getInstance() {
        return instance;
    }

    @Override
    public void setLogFile(File log) {

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
    public void flush() {

    }

    public enum LOGTYPE {IN,OUT;}
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

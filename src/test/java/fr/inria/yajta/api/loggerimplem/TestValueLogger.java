package fr.inria.yajta.api.loggerimplem;

import fr.inria.yajta.api.ValueTracking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestValueLogger implements ValueTracking {
    static TestValueLogger instance = new TestValueLogger();

    public List<Log> log = new ArrayList<>();

    public static TestValueLogger getInstance() {
        return instance;
    }
    @Override
    public void setLogFile(File log) {

    }

    @Override
    public void stepIn(String thread, String clazz, String method, Object[] parameter) {
        log.add(new Log(thread,clazz,method,parameter));
    }

    @Override
    public void stepOut(String thread, Object returnValue) {
        log.add(new Log(thread,returnValue));
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
        public Object[] parameter;
        public Object returnValue;
        public Log(String thread, String clazz, String method, Object[] parameter) {
            this.thread = thread;
            this.clazz = clazz;
            this.method = method;
            this.parameter = parameter;
            type = LOGTYPE.IN;
        }
        public Log(String thread, Object returnValue) {
            this.thread = thread;
            this.returnValue = returnValue;
            type = LOGTYPE.OUT;
        }
    }
}

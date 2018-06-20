package fr.inria.yajta.processor;

import fr.inria.yajta.api.BranchTracking;
import fr.inria.yajta.api.Tracking;
import fr.inria.yajta.api.ValueTracking;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import java.io.File;

public class RemoteLogger implements Tracking, BranchTracking, ValueTracking {
    public static File defaultLogFile;
    public File log;
    ChronicleQueue queue;
    final ExcerptAppender appender;

    public RemoteLogger() {
        queue = SingleChronicleQueueBuilder.binary(defaultLogFile.getAbsolutePath() + "/trace").build();
        appender = queue.acquireAppender();
    }

    static RemoteLogger instance ;
    public static RemoteLogger getInstance() {
        if(instance == null) {
            instance = new RemoteLogger();
        }
        return instance;
    }

    @Override
    public void branchIn(String thread, String branch) {
        appender.writeDocument(w -> w.write("trace").marshallable(
                m -> m.write("type").text("branchIn")
                        .write("thread").text(thread)
                        .write("branch").text(branch)
        ));
    }

    @Override
    public void branchOut(String thread) {
        appender.writeDocument(w -> w.write("trace").marshallable(
                m -> m.write("type").text("branchOut")
                        .write("thread").text(thread)
        ));
    }

    @Override
    public void stepIn(String thread, String clazz, String method) {
        appender.writeDocument(w -> w.write("trace").marshallable(
                m -> m.write("type").text("stepIn")
                        .write("thread").text(thread)
                        .write("clazz").text(clazz)
                        .write("method").text(method)
        ));
    }

    @Override
    public void stepOut(String thread) {
        appender.writeDocument(w -> w.write("trace").marshallable(
                m -> m.write("type").text("stepOut")
                        .write("thread").text(thread)
        ));
    }

    @Override
    public void setLogFile(File log) {
        this.log = log;
    }

    @Override
    public void stepIn(String thread, String clazz, String method, Object[] parameter) {

    }

    @Override
    public void stepOut(String thread, Object returnValue) {

    }

    @Override
    public void flush() {
        appender.writeDocument(w -> w.write("trace").marshallable(
                m -> m.write("type").text("end")
        ));
    }
}
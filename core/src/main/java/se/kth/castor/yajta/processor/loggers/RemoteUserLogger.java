package se.kth.castor.yajta.processor.loggers;

import se.kth.castor.yajta.api.Tracking;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import java.io.File;

public class RemoteUserLogger implements Tracking {
	public static File defaultLogFile = new File("traceDir");
	public File log;
	ChronicleQueue queue;
	final ExcerptAppender appender;

	public RemoteUserLogger() {
		if(!defaultLogFile.exists()) {
			defaultLogFile.mkdirs();
		}
		System.err.println("[RemoteUserLogger] trace will be at " + defaultLogFile.getAbsolutePath() + "/trace");
		queue = SingleChronicleQueueBuilder.binary(defaultLogFile.getAbsolutePath() + "/trace").build();
		appender = queue.acquireAppender();
	}

	static RemoteUserLogger instance ;
	public static RemoteUserLogger getInstance() {
		if(instance == null) {
			System.err.println("[yajta] Creating logging instance.");
			instance = new RemoteUserLogger();
		}
		return instance;
	}

	/*
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
	}*/

	@Override
	public void stepIn(String thread, String clazz, String method) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		String caller = (stackTrace.length > 3)  ? (stackTrace[3].getClassName() + "." + stackTrace[3].getMethodName()) : "";

		appender.writeDocument(
			w -> w.write("trace")
				.marshallable(
					m -> m.write("type").text("stepIn")
						.write("thread").text(thread)
						.write("clazz").text(clazz)
						.write("method").text(method)
						.write("caller").text(caller)
				)
		);
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
	public void flush() {
		appender.writeDocument(w -> w.write("trace").marshallable(
				m -> m.write("type").text("end")
		));
	}
}
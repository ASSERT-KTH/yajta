package se.kth.castor.yajta.processor.loggers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import se.kth.castor.yajta.api.FastTracking;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import java.io.File;

public class FastRemoteLogger implements FastTracking {
	private int elementCount = 1;

	protected BiMap<String, Integer> dico = HashBiMap.create();
	public BiMap<String, Integer> getDico() {
		return dico;
	}

	public boolean traceBranch = false;

	@Override
	public boolean traceBranch() {
		return traceBranch;
	}

	public File log;

	@Override
	public synchronized void setLogFile(File log) {
		this.log = log;
		queue = SingleChronicleQueueBuilder.binary(this.log.getAbsolutePath() + "/trace").build();
		appender = queue.acquireAppender();

	}

	public static File defaultLogFile = new File("tmpTrace");
	ChronicleQueue queue;
	ExcerptAppender appender;

	public FastRemoteLogger() {
		this(null);
	}

	public FastRemoteLogger(File log) {
		if(log == null) this.log = defaultLogFile;
		else this.log = log;
		queue = SingleChronicleQueueBuilder.binary(this.log.getAbsolutePath() + "/trace").build();
		appender = queue.acquireAppender();
	}

	@Override
	public synchronized int register(String clazz, String method, String branch) {
		String id = clazz + "." + method + "#" + branch;
		int r;
		if(dico.containsKey(id)) r = dico.get(id);
		else {
			dico.put(id,elementCount);
			r = elementCount;
			appender.writeDocument(w -> w.write("trace").marshallable(
				m -> m.write("type").text("regB")
					.write("clazz").text(clazz)
					.write("method").text(method)
					.write("branch").text(branch)
					.write("id").fixedInt32(elementCount)
			));
			elementCount++;
		}
		return r;
	}

	@Override
	public synchronized int register(String clazz, String method) {
		String id = clazz + "." + method;
		int r;
		if(dico.containsKey(id)) r = dico.get(id);
		else {
			dico.put(id,elementCount);
			r = elementCount;
			appender.writeDocument(w -> w.write("trace").marshallable(
				m -> m.write("type").text("reg")
					.write("clazz").text(clazz)
					.write("method").text(method)
					.write("id").fixedInt32(elementCount)
			));
			elementCount++;
		}
		return r;
	}

	//If used outside of agent
	static FastRemoteLogger instance ;
	public synchronized static FastRemoteLogger getInstance() {
		if(instance == null) instance = new FastRemoteLogger();
		return instance;
	}

	@Override
	public synchronized void stepIn(long thread, int id) {
		appender.writeDocument(w -> w.write("trace").marshallable(
			m -> m.write("type").text("in")
				.write("thread").fixedInt64(thread)
				.write("id").fixedInt32(id)
		));
	}

	@Override
	public synchronized void stepOut(long thread) {
		appender.writeDocument(w -> w.write("trace").marshallable(
			m -> m.write("type").text("out")
				.write("thread").fixedInt64(thread)
		));
	}

	@Override
	public synchronized void flush() {
		appender.writeDocument(w -> w.write("trace").marshallable(
			m -> m.write("type").text("end")
		));
	}
}

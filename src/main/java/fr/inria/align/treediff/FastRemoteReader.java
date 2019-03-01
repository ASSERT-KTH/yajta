package fr.inria.align.treediff;

import fr.inria.yajta.api.FastTracking;
import fr.inria.yajta.processor.loggers.FastLogger;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import java.io.File;

public class FastRemoteReader {

	/*public static void main(String arg[]) throws InterruptedException {
		FastRemoteReader r = new FastRemoteReader();
		r.logger = FastLogger.getInstance();
		File dd = new File("/home/nharrand/Documents/helloworld");
		r.dir = new File(dd,"d2");
		r.logger.setLogFile(new File("out.json"));
		r.read();
		r.logger.flush();
	}*/

	public FastRemoteReader(FastTracking logger, File inTraceDir) {
		this.logger = logger;
		this.dir= inTraceDir;
	}


	FastTracking logger;
	File dir;
	private boolean done = false;
	public void read() throws InterruptedException {

		try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir.getAbsolutePath() + "/trace").build()) {
			final ExcerptTailer tailer = queue.createTailer();
			while(!done) {
				tailer.readDocument(w -> {
					w.read("trace").marshallable(
							m -> {
								String type = m.read("type").text();
								//System.err.println("t:" + type);
								switch (type) {
									case "in":
										logger.stepIn(m.read("thread").int64(),
												m.read("id").int32());
										break;
									case "out":
										logger.stepOut(m.read("thread").int64());

										break;
									case "reg":
										logger.register(m.read("clazz").text(),
												m.read("method").text());
										break;
									case "regB":
										logger.register(m.read("clazz").text(),
												m.read("method").text(),
												m.read("branch").text());
										break;
									case "end":
										end();
										break;
									default:

										break;
								}
							});
				});
				Thread.sleep(10);
			}

		}
	}

	public void end() {
		done = true;
	}
}

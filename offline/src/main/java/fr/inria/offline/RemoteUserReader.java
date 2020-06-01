package fr.inria.offline;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import fr.inria.yajta.Utils;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RemoteUserReader {

	@Parameter(names = {"--help", "-h"}, help = true, description = "Display this message.")
	private boolean help;
	@Parameter(names = {"--input-dir", "-i"}, description = "Directory containing bytecode to instrument")
	private String inTraceDir;
	@Parameter(names = {"--output", "-o"}, description = "Output file")
	private String output = "./";
	@Parameter(names = {"--finalize", "-f"}, description = "Add an \"end\" message to the trace. Default: false")
	private boolean finalize = false;

	public static void printUsage(JCommander jcom) {
		jcom.usage();
	}

	public static void main(String args[]) throws InterruptedException {
		RemoteUserReader r = new RemoteUserReader();
		JCommander jcom = new JCommander(r,args);
		if(r.help || r.inTraceDir == null) {
			printUsage(jcom);
		} else {
			r.init();
			r.read();

			File outputFile = new File(r.output);
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile, true))) {
				bufferedWriter.append(r.toJSONString());
				bufferedWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//System.err.println("[RemoteUserReader] Done.");
		}
	}

	public RemoteUserReader() {
	}

	public RemoteUserReader(File inTraceDir) {
		this.dir= inTraceDir;
	}

	public void init() {
		dir = new File(inTraceDir);
	}

	protected Map<String,Map<String,Integer>> usages = new HashMap<>();

	public String toJSONString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean isFirst = true;
		for(String p: usages.keySet()) {
			if(isFirst) {
				isFirst = false;
			} else {
				sb.append(",");
			}
			sb.append("\"" + p + "\"");
			sb.append(":");
			sb.append("{");
			Map<String, Integer> membersUsages = usages.get(p);
			boolean isFirst2 = true;
			for(String m : membersUsages.keySet()) {
				if(isFirst2) {
					isFirst2 = false;
				} else {
					sb.append(",");
				}
				sb.append("\"" + m + "\"");
				sb.append(":");
				sb.append(membersUsages.get(m));
			}
			sb.append("}");
		}
		sb.append("}");
		return sb.toString();
	}

	public void add(String called, String caller) {
		Map<String, Integer> us = usages.computeIfAbsent(called, s -> new HashMap<>());
		Integer i = us.computeIfAbsent(caller, s -> 0);
		i = new Integer(i.intValue() + 1);
		us.put(caller,i);
		usages.put(called, us);
	}

	File dir;
	private boolean done = false;

	public void read() throws InterruptedException {

		try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir.getAbsolutePath() + "/trace").build()) {
			if(finalize) {
				final ExcerptAppender appender = queue.acquireAppender();
				appender.writeDocument(w -> w.write("trace").marshallable(
						m -> m.write("type").text("end")
				));
			}
			final ExcerptTailer tailer = queue.createTailer();
			while(!done) {
				tailer.readDocument(
					w -> {
						w.read("trace").marshallable(
							m -> {
								String type = m.read("type").text();
								String thread, clazz, method, branch, caller;
								//System.err.println("t:" + type);
								switch (type) {
									case "stepIn":
										thread = m.read("thread").text();
										clazz = m.read("clazz").text();
										method = m.read("method").text();
										caller = m.read("caller").text();
										add(clazz + "." + method, caller);
										//System.err.println("-> in " + thread + ", " + clazz + ", " + method + ", " + caller);
										break;
									case "stepOut":
										thread = m.read("thread").text();
										//System.err.println("-> out " + thread);

										break;
									case "branchIn":
										thread = m.read("thread").text();
										branch = m.read("branch").text();
										//System.err.println("-> branchIn " + thread + "," + branch);
										break;
									case "branchOut":
										thread = m.read("thread").text();
										//System.err.println("-> branchOut " + thread);
										break;
									case "stepInV":

										break;
									case "stepOutV":

										break;
									case "end":
										end();
										break;
									default:

										break;
								}
							}
						);
					}
				);
				Thread.sleep(10);
			}

		}
	}

	public void end() {
		done = true;
	}
}

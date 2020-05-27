package fr.inria.align.treediff;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.inria.offline.Instrumenter;
import fr.inria.yajta.api.FastTracking;
import fr.inria.yajta.processor.loggers.Logger;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RemoteUserReader {

	@Parameter(names = {"--help", "-h"}, help = true, description = "Display this message.")
	private boolean help;
	@Parameter(names = {"--input-dir", "-i"}, description = "Directory containing bytecode to instrument")
	private String inTraceDir;
	@Parameter(names = {"--output-dir", "-o"}, description = "Directory in which to output instrumented bytecode. Default: inst-classes")
	private String outputDir = "./";

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

			System.out.println(r.toJSONString());
			System.err.println("[RemoteUserReader] Done.");
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
		i = i++;
		us.put(caller,i);
	}

	File dir;
	private boolean done = false;

	public void read() throws InterruptedException {

		try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir.getAbsolutePath() + "/trace").build()) {
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
										System.err.println("-> in " + thread + ", " + clazz + ", " + method + ", " + caller);
										break;
									case "stepOut":
										thread = m.read("thread").text();
										System.err.println("-> out " + thread);

										break;
									case "branchIn":
										thread = m.read("thread").text();
										branch = m.read("branch").text();
										System.err.println("-> branchIn " + thread + "," + branch);
										break;
									case "branchOut":
										thread = m.read("thread").text();
										System.err.println("-> branchOut " + thread);
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

package se.kth.castor.yajta.processor.loggers;

import com.google.common.collect.BiMap;
import se.kth.castor.yajta.Agent;
import se.kth.castor.yajta.api.AbstractFastTracking;
import se.kth.castor.yajta.api.FastTracking;
import se.kth.castor.yajta.processor.IdTreeNode;
import se.kth.castor.yajta.processor.TreeNode;
import se.kth.castor.yajta.processor.util.MyEntry;
import se.kth.castor.yajta.processor.util.MyMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FastLogger extends AbstractFastTracking implements FastTracking {
	boolean traceBranches;

	public boolean tree = true;
	BufferedWriter bufferedWriter;
	int nodes;
	int branches;

	//TODO make it parametric
	int maxNodes = 20000000;

	private boolean stop = false;

	protected MyMap<Long, MyEntry<IdTreeNode, IdTreeNode>> threadLogs; // thread -> Root, Actual

	public void clear() {
		threadLogs = new MyMap<>();
		nodes = 0;
		branches = 0;
	}

	public FastLogger() {
		this(false);
	}

	public FastLogger(boolean traceBranches) {
		clear();
		this.traceBranches = traceBranches;
	}

	//If used outside of agent
	static FastLogger instance ;
	public static FastLogger getInstance() {
		if(instance == null) instance = new FastLogger();
		return instance;
	}

	@Override
	public synchronized void stepIn(long thread, int id) {
		if(!stop && (nodes < maxNodes)) {
			nodes++;
			MyEntry<IdTreeNode, IdTreeNode> entry = threadLogs.get(thread);
			if (entry == null) {
				IdTreeNode cur = new IdTreeNode();
				cur.id = 0;
				entry = new MyEntry<>(cur, cur.addChild(id));
				threadLogs.put(thread, entry);
			} else {
				entry.setValue(entry.getValue().addChild(id));
				threadLogs.put(thread, entry);
			}
		}
	}

	@Override
	public synchronized void stepOut(long thread) {
		if(!stop && (nodes < maxNodes)) {
			MyEntry<IdTreeNode, IdTreeNode> entry = threadLogs.get(thread);
			if(entry != null) {
				if(entry.getValue() != null) entry.setValue(entry.getValue().parent);
			} else {
				System.err.println("out");
			}
			threadLogs.put(thread, entry);
		}
	}

	@Override
	public boolean traceBranch() {
		return traceBranches;
	}

	public void flush() {
		stop = true;
		if(log == null) {
			int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
			if(tree) log = new File("log" + i + ".json");
			else log = new File("log" + i);
		}
		writeJSON(log);
		//writeCompact(log);
	}

	public void writeJSON(File out) {
		try {
			BiMap<Integer, String> rdico = dictionary.inverse();
			IdTreeNode.dico = i -> rdico.get(i);

			if(log.exists()) log.delete();
			log.createNewFile();
			bufferedWriter = new BufferedWriter(new FileWriter(log, true));
			if(tree) bufferedWriter.append("{\"name\":\"Threads\", " +
					"\"yajta-version\": \"" + Agent.yajtaVersionUID + "\", " +
					"\"serialization-version\": " + TreeNode.serialVersionUID + ", " +
					"\"nodes\": " + nodes + ", " +
					"\"branches\": " + branches + ", " +
					"\"threads\": " + threadLogs.entryList().size() + ", " +
					"\"children\":[\n");
			boolean isFirst = true;
			for(MyEntry<Long, MyEntry<IdTreeNode, IdTreeNode>> e: threadLogs.entryList()) {
				if (isFirst) isFirst = false;
				else if(tree) bufferedWriter.append(",");
				//e.getValue().getKey().print(bufferedWriter, tree);
				e.getValue().getKey().print(bufferedWriter, tree);
				bufferedWriter.append("\n");
			}
			if(tree) bufferedWriter.append("]}");
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeCompact(File out) {
		try {
			BiMap<Integer, String> rdico = dictionary.inverse();
			IdTreeNode.dico = i -> rdico.get(i);

			if(log.exists()) log.delete();
			log.createNewFile();
			bufferedWriter = new BufferedWriter(new FileWriter(log, true));
			for(Map.Entry<Integer,String> entry : rdico.entrySet()) {
				bufferedWriter.append(entry.getKey() + ":" + entry.getValue() + "\n");
			}
			boolean isFirst = true;
			for(MyEntry<Long, MyEntry<IdTreeNode, IdTreeNode>> e: threadLogs.entryList()) {
				bufferedWriter.append("_" + e.getKey() + "\n");
				writeTreeNode(e.getValue().getKey(), bufferedWriter);
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void writeTreeNode(IdTreeNode n, BufferedWriter bufferedWriter) throws IOException {
		bufferedWriter.append(n.id + "\n");
		if(n.children != null) {
			for (IdTreeNode c : n.children) {
				writeTreeNode(c, bufferedWriter);
			}
		}
		bufferedWriter.append("-");
	}


	public MyMap<Long, IdTreeNode> exportLogs() {
		MyMap<Long, IdTreeNode> res = new MyMap<>();
		for(MyEntry<Long, MyEntry<IdTreeNode, IdTreeNode>> e: threadLogs.entryList()) {
			res.put(e.getKey(),e.getValue().getKey());
		}
		return res;
	}
}

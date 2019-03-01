package fr.inria.yajta.processor.loggers;

import com.google.common.collect.BiMap;
import fr.inria.yajta.Agent;
import fr.inria.yajta.api.AbstractFastTracking;
import fr.inria.yajta.api.FastTracking;
import fr.inria.yajta.processor.IdTreeNode;
import fr.inria.yajta.processor.TreeNode;
import fr.inria.yajta.processor.util.MyEntry;
import fr.inria.yajta.processor.util.MyMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FastLogger extends AbstractFastTracking implements FastTracking {
	boolean traceBranches;

	public boolean tree = true;
	BufferedWriter bufferedWriter;
	int nodes;
	int branches;

	private boolean stop = false;

	private MyMap<Long, MyEntry<IdTreeNode, IdTreeNode>> threadLogs; // thread -> Root, Actual

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
		if(!stop) {
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
		if(!stop) {
			MyEntry<IdTreeNode, IdTreeNode> entry = threadLogs.get(thread);
			if(entry != null) {
				if(entry.getValue() != null) entry.setValue(entry.getValue().parent);
			} else {
				System.err.println("out");
			}
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
		try {
			BiMap<Integer, String> rdico = dico.inverse();
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
}

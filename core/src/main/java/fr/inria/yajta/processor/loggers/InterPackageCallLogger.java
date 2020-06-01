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
import java.util.Map;

public class InterPackageCallLogger extends AbstractFastTracking implements FastTracking {
	boolean traceBranches;

	public boolean tree = true;
	BufferedWriter bufferedWriter;
	int nodes;
	int branches;

	//TODO make it paramatric
	int maxNodes = 20000000;

	private boolean stop = false;

	protected MyMap<Long, MyEntry<IdTreeNode, IdTreeNode>> threadLogs; // thread -> Root, Actual

	public void clear() {
		threadLogs = new MyMap<>();
		nodes = 0;
		branches = 0;
	}

	public InterPackageCallLogger() {
		this(false);
	}

	public InterPackageCallLogger(boolean traceBranches) {
		clear();
		this.traceBranches = traceBranches;
	}

	//If used outside of agent
	static FastLogger instance;

	public static FastLogger getInstance() {
		if (instance == null) instance = new FastLogger();
		return instance;
	}

	@Override
	public synchronized void stepIn(long thread, int id) {
		if (!stop && (nodes < maxNodes)) {
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
		if (!stop && (nodes < maxNodes)) {
			MyEntry<IdTreeNode, IdTreeNode> entry = threadLogs.get(thread);
			if (entry != null) {
				if (entry.getValue() != null) entry.setValue(entry.getValue().parent);
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
		if (log == null) {
			int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
			if (tree) log = new File("log" + i + ".json");
			else log = new File("log" + i);
		}
		writeJSON(log);
		//writeCompact(log);
	}

	public void writeJSON(File out) {
		try {
			BiMap<Integer, String> rdico = dictionary.inverse();
			IdTreeNode.dico = i -> rdico.get(i);

			if (log.exists()) log.delete();
			log.createNewFile();
			bufferedWriter = new BufferedWriter(new FileWriter(log, true));
			bufferedWriter.append("packageParent,classParent,packageCalled,classCalled\n");
			for (MyEntry<Long, MyEntry<IdTreeNode, IdTreeNode>> e : threadLogs.entryList()) {

				printNode(bufferedWriter, e.getValue().getKey());

			}
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printNode(BufferedWriter b, IdTreeNode n) throws IOException {
		String name = IdTreeNode.dico.apply(n.id);
		if(name != null) {
			name = name.substring(0, name.lastIndexOf("("));
			String fqn = name.substring(0, name.lastIndexOf("."));
			String packageCalled = fqn.substring(0, fqn.lastIndexOf("."));
			String classCalled = fqn.substring(fqn.lastIndexOf(".") + 1);
			String parent = "", packageParent = "", parentClass = "";
			if (n.parent != null) {
				parent = IdTreeNode.dico.apply(n.parent.id);
				if(parent != null) {
					parent = parent.substring(0, parent.lastIndexOf("("));
					String fqnParent = parent.substring(0, parent.lastIndexOf("."));
					packageParent = fqnParent.substring(0, fqnParent.lastIndexOf("."));
					parentClass = fqnParent.substring(fqnParent.lastIndexOf(".") + 1);
				}
			}
			b.append(packageParent + ",");
			b.append(parentClass + ",");
			b.append(packageCalled + ",");
			b.append(classCalled + "\n");
		}

		if(n.children != null) {
			for(IdTreeNode c :n.children) {
				printNode(b,c);
			}
		}
	}

}
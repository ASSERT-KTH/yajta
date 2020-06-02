package se.kth.castor.yajta.processor.loggers;

import com.google.common.collect.BiMap;
import se.kth.castor.yajta.api.AbstractFastTracking;
import se.kth.castor.yajta.api.FastTracking;
import se.kth.castor.yajta.processor.IdTreeNode;
import se.kth.castor.yajta.processor.util.MyMap;

import java.io.File;

public class FastFollower extends AbstractFastTracking implements FastTracking {

	MyMap<Long, IdTreeNode> threadLogs = new MyMap<>();
	MyMap<Long, Boolean> threadOfftrack = new MyMap<>();

	@Override
	public void stepIn(long thread, int id) {
		if(!threadOfftrack.containsKey(thread) || threadOfftrack.get(thread)) return;
		//System.err.println("[" + thread + "] " + method + "{");
		IdTreeNode cur = threadLogs.get(thread);
		if(cur == null) offTrack(thread,id, 0);
		else {
			if(cur.hasNext()) {
				cur = cur.next();
				if(cur.id != id) {
					offTrack(thread, id, cur.id);
				} else {
					threadLogs.put(thread,cur);
				}
			} else offTrack(thread, id, 0);
		}

	}

	@Override
	public void stepOut(long thread) {

	}

	@Override
	public boolean traceBranch() {
		return false;
	}

	@Override
	public void setLogFile(File log) {

	}

	public void load(MyMap<Long, IdTreeNode> threadLogs, MyMap<Long, Boolean> threadOfftrack, BiMap<String,Integer> dico) {
		this.threadLogs = threadLogs;
		this.threadOfftrack = threadOfftrack;
		this.dictionary = dico;
	}

	public void offTrack(long thread, int method, int cur) {
		BiMap<Integer, String> d = dictionary.inverse();
		System.err.println("[OFF TRACK] <" + d.get(method) + "> instead of <" + d.get(cur) + ">");
		threadOfftrack.put(thread,true);

	}

	public void flush() {

	}
}
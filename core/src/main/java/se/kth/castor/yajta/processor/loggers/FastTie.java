package se.kth.castor.yajta.processor.loggers;

import se.kth.castor.yajta.api.AbstractFastTracking;
import se.kth.castor.yajta.api.FastTracking;
import se.kth.castor.yajta.processor.util.MyEntry;
import se.kth.castor.yajta.processor.util.MySet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FastTie extends AbstractFastTracking implements FastTracking {
	Set<Integer> visited = new TreeSet<>();

	static FastTie instance;
	public static FastTie getInstance() {
		if(instance == null) {
			instance = new FastTie();
		}
		return instance;
	}

	public void printDictionary() {
		for(Map.Entry<String, Integer> e : dictionary.entrySet()) {
			System.err.println("M: " + e.getKey() + " -> " + e.getValue());
		}
	}

	@Override
	public void stepIn(long thread, int id) {
		//System.err.println("[FastTie] step in " + id);
		if(visited.contains(id)) return;
		visited.add(id);
		//System.out.println(dictionary.inverse().get(id) + " is new.");
		/*ClassLoader cl = FastTie.class.getClassLoader();
		try {
			System.out.println("path: " + (String) cl.loadClass("spoon.test.main.MainTest").getField("curPath").get(null));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}*/

	}

	@Override
	public void stepOut(long thread) {

	}

	@Override
	public boolean traceBranch() {
		return false;
	}

	@Override
	public void flush() {
		if(log == null) {
			int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
			log = new File("log" + i);
		}
		try {
			if(log.exists()) log.delete();
			log.createNewFile();

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(log, true));
			bufferedWriter.append("{\"report\":[");
			bufferedWriter.append("{\"thread\":\"all\", \"methods\":[");
			boolean f = true;
			for(Integer m: visited) {
				if(f) f = false;
				else bufferedWriter.append(",");
				bufferedWriter.append("\"" + dictionary.inverse().get(m) + "\"");
			}
			bufferedWriter.append("]}");
			bufferedWriter.append("]}");
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}

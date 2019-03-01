package fr.inria.yajta.processor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.inria.yajta.api.AbstractFastTracking;
import fr.inria.yajta.api.FastTracking;

import java.io.File;
import java.net.URLClassLoader;
import java.util.*;

public class FastTie extends AbstractFastTracking implements FastTracking {
	//Set<Integer> visited = new HashSet<>();
	Set<Integer> visited = new TreeSet<>();

	static FastTie instance;
	public static FastTie getInstance() {
		if(instance == null) {
			instance = new FastTie();
		}
		return instance;
	}

	public void printDico() {
		for(Map.Entry<String, Integer> e :dico.entrySet()) {
			System.err.println("M: " + e.getKey() + " -> " + e.getValue());
		}
	}

	@Override
	public void stepIn(long thread, int id) {
		//System.err.println("[FastTie] step in " + id);
		if(visited.contains(id)) return;
		visited.add(id);
		System.out.println(dico.inverse().get(id) + " is new.");
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
	public void flush() {

	}


}

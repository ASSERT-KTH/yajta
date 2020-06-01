package fr.inria.yajta.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.File;

public abstract class AbstractFastTracking implements FastTracking {
	private int elementCount = 1;

	protected BiMap<String, Integer> dictionary = HashBiMap.create();
	public BiMap<String, Integer> getDictionary() {
		return dictionary;
	}

	public File log = new File("default-yajta-log");

	@Override
	public void setLogFile(File log) {
		this.log = log;
	}

	@Override
	public int register(String clazz, String method, String branch) {
		String id = clazz + "." + method + "#" + branch;
		int r;
		if(dictionary.containsKey(id)) r = dictionary.get(id);
		else {
			dictionary.put(id,elementCount);
			r = elementCount;
			elementCount++;
		}
		return r;
	}

	@Override
	public int register(String clazz, String method) {
		String id = clazz + "." + method;
		int r;
		if(dictionary.containsKey(id)) r = dictionary.get(id);
		else {
			dictionary.put(id,elementCount);
			r = elementCount;
			elementCount++;
		}
		return r;
	}

	/**
	 * @param thread the id of the thread beeing logged
	 * @param id the id of the method/branch beeing logged
	 *
	 *  stepIn is called when Method/Branch is entered.
	 *  It is up to the logger to store it in its data structure.
	 */
	@Override
	public abstract void stepIn(long thread, int id);

	/**
	 * @param thread the id of the thread beeing logged
	 *
	 *  stepOut is called when Method/Branch is exited.
	 *  It is up to the logger to keep track of which
	 *  Method/Branch is exited.
	 */
	@Override
	public abstract void stepOut(long thread);

	/**
	 * The JVM is exiting. It is up to the logger to output
	 * the logs in whatever format it is fit. The filed log contains
	 * the file in which to write.
	 */
	@Override
	public abstract void flush();

}

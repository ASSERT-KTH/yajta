package fr.inria.yajta.processor.loggers;

import fr.inria.yajta.api.Tracking;
import fr.inria.yajta.processor.util.MyMap;
import fr.inria.yajta.processor.util.MySet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MethodCoverageLogger implements Tracking {

	MyMap<String,MySet<String>> observedClasses = new MyMap<>();
	File log;


	//If used outside of agent
	static MethodCoverageLogger instance ;
	public static MethodCoverageLogger getInstance() {
		if(instance == null) {

			int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
			System.err.println("[Yajta] create logging instance");
			instance = new MethodCoverageLogger();
			instance.setLogFile(new File("yajta_coverage-" + i + ".json"));

			Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {
				public void run() {
					getInstance().flush();
				}
			});
		}
		return instance;
	}

	@Override
	public void stepIn(String thread, String clazz, String method) {
		if(!observedClasses.containsKey(clazz)) {
			observedClasses.put(clazz,new MySet<>());
		}
		observedClasses.get(clazz).add(method);
	}

	@Override
	public void stepOut(String thread) {

	}


	@Override
	public synchronized void setLogFile(File log) {
		this.log = log;
	}

	@Override
	public synchronized void flush() {
		System.err.println("[Yajta] flush");
		try {
			//if(log.exists()) log.delete();
			if(!log.exists()) log.createNewFile();
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(log, true));
			bufferedWriter.append("{\n");
			boolean isFirst = true;
			for(String clazz: observedClasses.keyList()) {
				if (isFirst) {
					isFirst = false;
				} else {
					bufferedWriter.append(",\n");
				}
				bufferedWriter.append("\"" + clazz + "\": [");
				boolean isFirst2 = true;
				for(String method: observedClasses.get(clazz)) {
					if (isFirst2) {
						isFirst2 = false;
					} else {
						bufferedWriter.append(",");
					}
					bufferedWriter.append("\"" + method + "\"");
				}
				bufferedWriter.append("]");
			}
			bufferedWriter.append("\n}");
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

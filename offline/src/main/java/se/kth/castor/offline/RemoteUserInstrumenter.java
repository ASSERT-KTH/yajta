package se.kth.castor.offline;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import se.kth.castor.yajta.api.ClassList;
import se.kth.castor.yajta.api.MalformedTrackingClassException;
import se.kth.castor.yajta.processor.loggers.FastLogger;
import se.kth.castor.yajta.processor.loggers.RemoteLogger;
import se.kth.castor.yajta.processor.loggers.RemoteUserLogger;
import se.kth.castor.yajta.processor.loggers.RemoteUserLogger;

import java.io.File;

public class RemoteUserInstrumenter {

	@Parameter(names = {"--help", "-h"}, help = true, description = "Display this message.")
	private boolean help;
	@Parameter(names = {"--in-class-dir", "-i"}, description = "Directory containing bytecode to instrument")
	private String classDir;
	@Parameter(names = {"--out-class-dir", "-o"}, description = "Directory in which to output instrumented bytecode. Default: inst-classes")
	private String traceDir = "./";


	public static void printUsage(JCommander jcom) {
		jcom.usage();
	}

	public static void main(String args[]) throws MalformedTrackingClassException {
		RemoteUserInstrumenter r = new RemoteUserInstrumenter();
		JCommander jcom = new JCommander(r,args);
		if(r.help || r.classDir == null) {
			printUsage(jcom);
		} else {
			File iDir = new File(r.classDir);
			File oDir = new File(r.traceDir);
			ClassList cl = new ClassList(new String[0],new String[0],null,false);
			InstrumentationBuilder ib = new InstrumentationBuilder(iDir,oDir, cl,RemoteUserLogger.class, true);
			//InstrumentationBuilder ib = new InstrumentationBuilder(iDir,oDir,cl,RemoteLogger.class, true);
			ib.instrument();
			System.out.println("Instrumentation Done.");
		}
	}
}

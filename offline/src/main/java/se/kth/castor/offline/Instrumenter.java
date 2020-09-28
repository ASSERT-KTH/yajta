package se.kth.castor.offline;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import se.kth.castor.yajta.Agent;
import se.kth.castor.yajta.Tracer;
import se.kth.castor.yajta.Utils;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;

public class Instrumenter {

    @Parameter(names = {"--help", "-h"}, help = true, description = "Display this message.")
    private boolean help;
    @Parameter(names = {"--classes-dir", "-c"}, description = "Directory containing bytecode to instrument")
    private String classDirPath;
    @Parameter(names = {"--output-dir", "-o"}, description = "Directory in which to output instrumented bytecode. Default: inst-classes")
    private String outputDirPath = "inst-classes";

    public static void printUsage(JCommander jcom) {
        jcom.usage();
    }


    public static void main(String[] args){
        Instrumenter instrumenter = new Instrumenter();
        JCommander jcom = new JCommander(instrumenter,args);

        if(instrumenter.help || instrumenter.classDirPath == null) {
            printUsage(jcom);
        } else {
            File classDir = new File(instrumenter.classDirPath);
            File outputDir = new File(instrumenter.outputDirPath);
            if(!classDir.exists() || !classDir.isDirectory()){
                System.out.println("Incorrect parameter -c / --classes-dir, expect a valid directory, found \"" + instrumenter.classDirPath + "\".");
                printUsage(jcom);
                return;
            }
            System.out.println("[se.kth.castor.offline.Instrumenter] classDir: " + classDir.getPath());
            System.out.println("[se.kth.castor.offline.Instrumenter] classDir: " + outputDir.getPath());

            ClassPool pool = ClassPool.getDefault();
            try {
                pool.appendClassPath(Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath());
                pool.appendClassPath(classDir.getAbsolutePath());
                String[] classNames = Utils.listClassesAsArray(classDir);
                CtClass[] classToTransform = pool.get(classNames);

                Tracer tracer = new Tracer(null);
                for(CtClass cl: classToTransform) {
                    try {
                        tracer.doClass(cl,cl.getName(),false);
                        cl.writeFile(outputDir.getAbsolutePath());
                        System.out.println("[se.kth.castor.offline.Instrumenter] instrument: " + cl.getName());
                    } catch (CannotCompileException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("[se.kth.castor.offline.Instrumenter] Done");
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }

    }



}

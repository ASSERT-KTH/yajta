package fr.inria.offline;

import fr.inria.yajta.Agent;
import fr.inria.yajta.Tracer;
import fr.inria.yajta.Utils;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Instrumenter {

    public static void main(String[] args){
        if(args.length < 2) {
            System.err.println("Usage: fr.inria.offline.Instrumenter classDir outputDir");
            return;
        }
        //File classDir = new File("/home/nharrand/Documents/helloworld/target/classes");
        File classDir = new File(args[0]);
        File outputDir = new File(args[1]);
        System.out.println("[fr.inria.offline.Instrumenter] classDir: " + classDir.getPath());
        System.out.println("[fr.inria.offline.Instrumenter] classDir: " + outputDir.getPath());
        //Agent.class.getClassLoader().get
        ClassPool pool = ClassPool.getDefault();
        try {
            pool.appendClassPath(Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            pool.appendClassPath(classDir.getAbsolutePath());
            //pool.appendClassPath("/home/nharrand/Documents/yajta/target/classes");
            String[] classNames = Utils.listClassesAsArray(classDir);
            CtClass[] classToTransform = pool.get(classNames);

            Tracer tracer = new Tracer(null);
            for(CtClass cl: classToTransform) {
                try {
                    tracer.doClass(cl,cl.getName(),false);
                    cl.writeFile(outputDir.getAbsolutePath());
                    System.out.println("[fr.inria.offline.Instrumenter] instrument: " + cl.getName());
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("[fr.inria.offline.Instrumenter] Done");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }



}

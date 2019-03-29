package fr.inria.yajta;


import fr.inria.yajta.api.*;
import fr.inria.yajta.processor.loggers.CountLogger;
import fr.inria.yajta.processor.loggers.DynamicGraph;
import fr.inria.yajta.processor.loggers.DynamicGraphFollower;
import fr.inria.yajta.processor.loggers.FastLogger;
import fr.inria.yajta.processor.loggers.FastTie;
import fr.inria.yajta.processor.loggers.Follower;
import fr.inria.yajta.processor.loggers.Logger;
import fr.inria.yajta.processor.loggers.RemoteLogger;
import fr.inria.yajta.processor.loggers.Tie;
import fr.inria.yajta.processor.loggers.ValueLogger;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;


public class Agent {
    //Initialized from pom (pom > project.properties > Yajta
    public static String yajtaVersionUID;
    static boolean verbose = true;

    static String[] INCLUDES = new String[] {};
    static String[] ISOTOPES = new String[] {};
    static String[] EXCLUDES = new String[] {};

    static Tracking trackingInstance;
    static ValueTracking valueTrackingInstance;
    public static FastTracking fastTrackingInstance;

    public static Tracking getTrackingInstance() {
        if(trackingInstance == null) {
            offlineInit();
        }
        return trackingInstance;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.err.println("[Yajta] Begin with '" + agentArgs + "'");
        Args a = new Args();
        a.parseArgs(agentArgs);

        if(a.follow != null) {
            if(verbose) System.err.println("[Yajta] Follower selected");
            Follower f = new Follower();
            f.load(a.follow);
            trackingInstance = f;
        } else if(a.mfollow != null) {
            if(verbose) System.err.println("[Yajta] DynamicGraphFollower selected");
                DynamicGraphFollower f = new DynamicGraphFollower();
                f.load(a.mfollow);
                trackingInstance = f;
        } else if(a.print.equalsIgnoreCase("tie")) {
            if(verbose) System.err.println("[Yajta] Tie selected");
            Tie t = new Tie();
            if(a.output != null)
                t.log = a.output;
            trackingInstance = t;
        } else if(a.print.equalsIgnoreCase("fasttie")) {
            if(verbose) System.err.println("[Yajta] FastTie selected");
            FastTie t = new FastTie();
            if(a.output != null)
                t.log = a.output;
            fastTrackingInstance = t;
        } else if(a.print.equalsIgnoreCase("fasttree")) {
            if(verbose) System.err.println("[Yajta] FastTie selected");
            FastLogger t = new FastLogger();
            if(a.output != null)
                t.log = a.output;
            fastTrackingInstance = t;
        } else if(a.print.equalsIgnoreCase("matrix")) {
            if(verbose) System.err.println("[Yajta] DynamicGraph selected");
            DynamicGraph t = new DynamicGraph();
            if(a.output != null)
                t.log = a.output;
            trackingInstance = t;
        } else if(a.print.equalsIgnoreCase("values")) {
            if(verbose) System.err.println("[Yajta] ValueLogger selected");
            ValueLogger t = ValueLogger.getInstance();
            if(a.output != null)
                t.log = a.output;
            valueTrackingInstance = t;
        } else if(a.print.equalsIgnoreCase("branch")) {
            if(verbose) System.err.println("[Yajta] Logger selected");
            Logger t = Logger.getInstance();
            t.tree = true;
            if(a.output != null)
                t.log = a.output;
            trackingInstance = t;
        } else if(a.print.equalsIgnoreCase("remote")) {
            if(verbose) System.err.println("[Yajta] RemoteLogger selected");
            System.err.println("[yajta] remote");
            if(a.output != null) {
                RemoteLogger.defaultLogFile = a.output;
            }
            RemoteLogger t = RemoteLogger.getInstance();
            /*if(a.output != null)
                t.log = a.output;*/
            trackingInstance = t;
        } else if(a.print.equalsIgnoreCase("count")) {
            if(verbose) System.err.println("[Yajta] CountLogger selected");
            CountLogger t = CountLogger.getInstance();
            if(a.output != null)
                t.log = a.output;
            trackingInstance = t;
        } else {
            if(verbose) System.err.println("[Yajta] Logger selected");
            Logger l = Logger.getInstance();
            if(a.output != null)
                l.log = a.output;
            if(!a.print.equalsIgnoreCase("tree")) l.tree = false;
            trackingInstance = l;
        }

        final ClassFileTransformer transformer;

        INCLUDES = a.INCLUDES;
        EXCLUDES = a.EXCLUDES;
        ISOTOPES = a.ISOTOPES;

        if(a.print.equalsIgnoreCase("values")) {
            if(verbose) System.err.println("[Yajta] SimpleTracer selected");
            //transformer = new ReturnTracer(Utils.format(a.INCLUDES), Utils.format(a.EXCLUDES), Utils.format(a.ISOTOPES));
            //if (a.strictIncludes) ((ReturnTracer)transformer).strictIncludes = true;
            transformer = new SimpleTracer(a.cl);
            if (a.strictIncludes) ((SimpleTracer)transformer).strictIncludes = true;
            try {
                ((SimpleTracer)transformer).setValueTrackingClass(valueTrackingInstance.getClass());
            } catch (MalformedTrackingClassException e) {
                e.printStackTrace();
            }
        } else if(a.print.equalsIgnoreCase("fasttie")
                || a.print.equalsIgnoreCase("fasttree")) {
            if(verbose) System.err.println("[Yajta] FastTracer selected");
            transformer = new FastTracer(a.cl);
            if (a.strictIncludes) ((FastTracer)transformer).strictIncludes = true;
            try {
                ((FastTracer)transformer).setTrackingClass(fastTrackingInstance.getClass(),fastTrackingInstance);
            } catch (MalformedTrackingClassException e) {
                e.printStackTrace();
            }
        } else if(a.print.equalsIgnoreCase("branch")
                || a.print.equalsIgnoreCase("count")
                || a.print.equalsIgnoreCase("remote")) {
            if(verbose) System.err.println("[Yajta] SimpleTracer selected");
            transformer = new SimpleTracer(a.cl);
            if (a.strictIncludes) ((SimpleTracer)transformer).strictIncludes = true;
            try {
                ((SimpleTracer)transformer).setTrackingClass(trackingInstance.getClass());
            } catch (MalformedTrackingClassException e) {
                e.printStackTrace();
            }
        } else if(a.includeFile == null) {
            if(verbose) System.err.println("[Yajta] Tracer selected");
            transformer = new Tracer(a.cl, Utils.format(a.ISOTOPES));
            if (a.strictIncludes) ((Tracer)transformer).strictIncludes = true;
        } else {
            if(verbose) System.err.println("[Yajta] SpecializedTracer selected");
            transformer = new SpecializedTracer(a.includeFile);
        }

        inst.addTransformer(transformer, true);

        if (inst.isNativeMethodPrefixSupported()) {
            inst.setNativeMethodPrefix(transformer, "wrapped_native_method_");
        }

        Class loadedClasses[] = inst.getAllLoadedClasses();
        /*for(int i = 0; i < loadedClasses.length; i++) {
            System.err.println("cl: " + loadedClasses[i]);
            System.err.println("cl name: " + loadedClasses[i].getName());
        }
        Object oFilteredLoadedClasses[] = Arrays.stream(loadedClasses)
                .filter(el -> el != null)
                .filter(el -> el.getName() != null)
                .filter(el -> !el.getName().startsWith("fr.inria.yajta"))
                .filter(el -> !el.getName().startsWith("[Lfr.inria.yajta"))
                .toArray();
        Class filteredLoadedClasses[] = new Class[oFilteredLoadedClasses.length];
        for(int i = 0; i < oFilteredLoadedClasses.length; i++) {
            filteredLoadedClasses[i] = (Class) oFilteredLoadedClasses[i];
        }
        Class BSloadedClasses[] = inst.getInitiatedClasses(ClassLoader.getSystemClassLoader().getParent());
        //System.err.println("BSloadedClasses size: " + BSloadedClasses.length);
        //System.err.println("filteredLoadedClasses size: " + filteredLoadedClasses.length);
        retransform(BSloadedClasses, inst, a);
        retransform(filteredLoadedClasses, inst, a);*/
        if (!a.strictJar) {
            retransform(loadedClasses, inst, a);
        }

        Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {
            public void run() {
                if(trackingInstance != null) {
                    getTrackingInstance().flush();
                } else if (valueTrackingInstance != null) {
                    valueTrackingInstance.flush();
                } else if (fastTrackingInstance != null) {
                    fastTrackingInstance.flush();
                } else {
                    System.err.println("[Premain] No tracking instance found.");
                }
            }
        });
        System.err.println("[Premain] Done");

    }

    public static void offlineInit() {
        //If used offline, the Agent premain will never be called
        //Then trackingInstance must be initialized
        //And the shutdown hook too
        Logger l =  new Logger();
        trackingInstance = l;
        trackingInstance.setLogFile(new File("yajta-trace.json"));

        Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {
            public void run() {
                getTrackingInstance().flush();
            }
        });
    }

    public static void retransform(Class[] classes, Instrumentation inst, Args a) {
        for(int i = classes.length-1; i >= 0; i--) {
            String className = classes[i].getName().replace(".", "/");
            if(className.startsWith("[L")) {
                className = className.substring(2);
            }
            //System.err.println("Loaded class " + className + " -> " + a.cl.isToBeProcessed(className));
            if(a.cl.isToBeProcessed(className)) {
                try {
                    //System.err.println(classes[i].getName());
                    inst.retransformClasses(classes[i]);

                } catch (UnmodifiableClassException e) {
                    System.err.println("[ERROR] " + className);
                }
            }
            if(Utils.startWith(className, Utils.format(a.ISOTOPES)) && !Utils.startWith(className, Utils.format(a.EXCLUDES))) {
                try {
                    inst.retransformClasses(classes[i]);
                } catch (UnmodifiableClassException e) {
                    System.err.println("[ERROR] (isotope) " + className);
                }
            }
        }
    }
}

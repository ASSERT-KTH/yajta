package fr.inria.yajta;


import fr.inria.yajta.processor.*;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;


public class Agent {
    static String[] INCLUDES = new String[] {};
    static String[] ISOTOPES = new String[] {};
    static String[] EXCLUDES = new String[] {};

    static Tracking trackingInstance;

    public static Tracking getTrackingInstance() {
        return trackingInstance;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.err.println("[Premain] Begin '" + agentArgs + "'");
        Args a = new Args();
        a.parseArgs(agentArgs);

        if(a.follow != null) {
            Follower f = new Follower();
            f.load(a.follow);
            trackingInstance = f;
        } else if(a.print.equalsIgnoreCase("tie")) {
            Tie t = new Tie();
            if(a.output != null)
                t.log = a.output;
            trackingInstance = t;
        } else if(a.print.equalsIgnoreCase("values")) {
            ReturnLogger t = new ReturnLogger();
            if(a.output != null)
                t.log = a.output;
            trackingInstance = t;
        } else {
            Logger l =  new Logger();
            if(a.output != null)
                l.log = a.output;
            if(!a.print.equalsIgnoreCase("tree")) l.tree = false;
            trackingInstance = l;
        }
        if(a.print.equalsIgnoreCase("values")) {
            final ReturnTracer transformer = new ReturnTracer(Utils.format(a.INCLUDES), Utils.format(a.EXCLUDES), Utils.format(a.ISOTOPES));

            if (a.strictIncludes) transformer.strictIncludes = true;

            INCLUDES = a.INCLUDES;
            EXCLUDES = a.EXCLUDES;
            ISOTOPES = a.ISOTOPES;
            inst.addTransformer(transformer, true);
            if (inst.isNativeMethodPrefixSupported()) {
                inst.setNativeMethodPrefix(transformer, "wrapped_native_method_");
            }

        } else if(a.includeFile == null) {
            final Tracer transformer = new Tracer(a.cl, Utils.format(a.ISOTOPES));

            if (a.strictIncludes) transformer.strictIncludes = true;

            INCLUDES = a.INCLUDES;
            EXCLUDES = a.EXCLUDES;
            ISOTOPES = a.ISOTOPES;
            inst.addTransformer(transformer, true);
            if (inst.isNativeMethodPrefixSupported()) {
                inst.setNativeMethodPrefix(transformer, "wrapped_native_method_");
            }
        } else {
            final SpecializedTracer transformer = new SpecializedTracer(a.includeFile);

            inst.addTransformer(transformer, true);
            if (inst.isNativeMethodPrefixSupported()) {
                inst.setNativeMethodPrefix(transformer, "wrapped_native_method_");
            }
        }
        Class loadedClasses[] = inst.getAllLoadedClasses();

        //System.err.println("isRedefineClassesSupported: " + inst.isRedefineClassesSupported());

        for(int i = loadedClasses.length-1; i >= 0; i--) {
            if(loadedClasses[i].getName().startsWith("[L")) {
                String className = loadedClasses[i].getName().substring(2).replace(".","/");

                if(a.cl.isToBeProcessed(className)) {
                    try {
                        //System.err.println(cl[i].getName());
                        inst.retransformClasses(loadedClasses[i]);

                    } catch (UnmodifiableClassException e) {
                        System.err.println("[ERROR] " + className);
                    }
                }
                if(Utils.startWith(className, Utils.format(a.ISOTOPES)) && !Utils.startWith(className, Utils.format(a.EXCLUDES))) {
                    try {
                        inst.retransformClasses(loadedClasses[i]);
                    } catch (UnmodifiableClassException e) {
                        System.err.println("[ERROR] (isotope) " + className);
                    }
                }
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                getTrackingInstance().flush();
            }
        });
        System.err.println("[Premain] Done");


    }


}

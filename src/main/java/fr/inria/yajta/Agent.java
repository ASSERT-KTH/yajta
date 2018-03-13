package fr.inria.yajta;


import fr.inria.yajta.processor.*;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.Properties;


public class Agent {
    //Initialized from pom (pom > project.properties > Yajta
    public static String yajtaVersionUID;

    static String[] INCLUDES = new String[] {};
    static String[] ISOTOPES = new String[] {};
    static String[] EXCLUDES = new String[] {};

    static Tracking trackingInstance;

    public static Tracking getTrackingInstance() {
        return trackingInstance;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.err.println("[Yajta] Begin with '" + agentArgs + "'");
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
        } else if(a.print.equalsIgnoreCase("matrix")) {
            DynamicGraph t = new DynamicGraph();
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
        } else if(a.print.equalsIgnoreCase("branch")) {
            final BranchTracer transformer = new BranchTracer(a.cl, Utils.format(a.ISOTOPES));

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
        Object oFilteredLoadedClasses[] = Arrays.stream(loadedClasses)
                .filter(el -> !el.getName().startsWith("fr.inria.yajta")
                && !el.getName().startsWith("[Lfr.inria.yajta"))
                .toArray();
        Class filteredLoadedClasses[] = new Class[oFilteredLoadedClasses.length];
        for(int i = 0; i < oFilteredLoadedClasses.length; i++) {
            filteredLoadedClasses[i] = (Class) oFilteredLoadedClasses[i];
        }
        Class BSloadedClasses[] = inst.getInitiatedClasses(ClassLoader.getSystemClassLoader().getParent());
        //System.err.println("BSloadedClasses size: " + BSloadedClasses.length);
        //System.err.println("filteredLoadedClasses size: " + filteredLoadedClasses.length);
        retransform(BSloadedClasses, inst, a);
        retransform(filteredLoadedClasses, inst, a);

        Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {
            public void run() {
                getTrackingInstance().flush();
            }
        });
        System.err.println("[Premain] Done");

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
                    //System.err.println(loadedClasses[i].getName());
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
            //}
        }
    }

    public static void main(String[] args) {
        System.out.println("This class is supposed to be used as an agent and has no real main.");
        final Properties properties = new Properties();
        try {
            properties.load(Agent.class.getClassLoader().getResourceAsStream("/project.properties"));
            //properties.load(Agent.class.getResourceAsStream("project.properties"));
            yajtaVersionUID = properties.getProperty("project.version");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

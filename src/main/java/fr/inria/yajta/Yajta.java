package fr.inria.yajta;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.JarURLConnection;
import java.util.Properties;

public class Yajta {
    public static void premain(String agentArgs, Instrumentation inst) {
        /*
         * This method is a small initializer,
         * it access resources from the application class loader
         * and then call the real premain agent but from bootstrap classloader.
         * This couldn't be done directly in Agent.premain as Agent's class
         * initializer would be called first loading the classes
         * related to Agent static fields in the application classloader.
         */
        final Properties properties = new Properties();
        JarURLConnection connection = null;
        String yajtaVersionUID = null;
        boolean runFromBootstrapClassLoader = agentArgs.contains("from-bootstrap-classloader");
        try {
            properties.load(Yajta.class.getClassLoader().getResourceAsStream("project.properties"));
            yajtaVersionUID = properties.getProperty("project.version");
            connection = (JarURLConnection) Yajta.class.getResource("Yajta.class").openConnection();
            if(runFromBootstrapClassLoader) {
                inst.appendToBootstrapClassLoaderSearch(connection.getJarFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Agent.yajtaVersionUID = yajtaVersionUID;
        Agent.premain(agentArgs, inst);
    }
}

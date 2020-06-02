package se.kth.castor.yajta;

import javassist.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nharrand on 12/07/17.
 */
public class SpecializedTracer implements ClassFileTransformer {

    boolean verbose = false;
    public SpecializedTracer (File includeFile) {
        //{"classes":[{"class":"myorg.myclass", "methods":["mymethod"]}]}
        JSONObject includeJson = FileHelper.readFromFile(includeFile);
        try {
            JSONArray classes = includeJson.getJSONArray("classes");
            for(int i = 0; i < classes.length(); i++) {
                Set<String> ms = new HashSet<>();
                String cl = classes.getJSONObject(i).getString("class");
                JSONArray methods = classes.getJSONObject(i).getJSONArray("methods");
                for(int j = 0; j < methods.length(); j++) ms.add(methods.getString(j));
                methodToTrace.put(cl,ms);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Map<String, Set<String>> methodToTrace = new HashMap<>();

    public byte[] transform( final ClassLoader loader, final String className, final Class clazz,
                             final java.security.ProtectionDomain domain, final byte[] bytes ) {

        if(methodToTrace.containsKey(className.replace("/", "."))) return doClass( className, clazz, bytes );
        else return bytes;
    }

    public byte[] doClass( final String name, final Class clazz, byte[] b ) {
        return doClass(name,clazz,b,false);
    }
    public byte[] doClass( final String name, final Class clazz, byte[] b, boolean isIsotope ) {
        ClassPool pool = ClassPool.getDefault();
        CtClass cl = null;
        try {
            cl = pool.makeClass( new java.io.ByteArrayInputStream( b ) );
            if( cl.isInterface() == false ) {

                CtBehavior[] methods = cl.getDeclaredBehaviors();

                for( int i = 0; i < methods.length; i++ ) {

                    if( methods[i].isEmpty() == false ) {
                        if(isIsotope)
                            doMethod( methods[i] , name, isIsotope, "se.kth.castor.singleusagedemo.collections.MyMap");
                        else
                            doMethod( methods[i] , name);
                    }
                }

                b = cl.toBytecode();

                if(verbose) System.err.println( "-> Instrument  " + name);
            }
        } catch( Exception e ) {
            if(verbose) System.err.println( "Could not instrument  " + name + ",  exception : " + e.getMessage() );
        } finally {

            if( cl != null ) {
                cl.detach();
            }
        }

        return b;
    }

    private void doMethod( final CtBehavior method , String className) throws NotFoundException, CannotCompileException {
        doMethod(method,className,false,null);
    }

    private void doMethod( final CtBehavior method , String className, boolean isIsotope, String isotope) throws NotFoundException, CannotCompileException {

        if(!Modifier.isNative(method.getModifiers())) {
            String pprefix = "", ppostfix = "";
            if(isIsotope && !Modifier.isStatic(method.getModifiers())) {
                if(verbose) System.err.println("[Isotope] " + className + " " + method.getName());
                pprefix = "if(getClass().getName().equalsIgnoreCase(\"" + isotope + "\")) {";
                ppostfix = "}";
            } else if(isIsotope) {
                pprefix = "if(false) {";
                ppostfix = "}";
            } else {
                if(verbose) System.err.println("[Vanilla] " + className + " " + method.getName());
            }
            if(verbose) System.err.println("1");
            /*
            String params = "(";
            boolean first = true;
            for (CtClass c : method.getParameterTypes()) {
                if (first) first = false;
                else params += ", ";
                params += c.getName();
            }
            params += ")";
            */
            String params = method.getSignature();
            //String methodName = className.replace("/", ".") + "." + method.getName() + params;
            if(methodToTrace.get(className.replace("/", ".")).contains(method.getName() + params)) {
                method.insertBefore(pprefix + "se.kth.castor.yajta.Agent.getTrackingInstance().stepIn(Thread.currentThread().getName(),\"" + className.replace("/", ".") + "\", \""+ method.getName() + params + "\");" + ppostfix);
                method.insertAfter(pprefix + "se.kth.castor.yajta.Agent.getTrackingInstance().stepOut(Thread.currentThread().getName());" + ppostfix);
            }
        } else {
            if(verbose) System.err.println("Method: " + className.replace("/", ".") + "." + method.getName() + " is native");
        }
    }
}
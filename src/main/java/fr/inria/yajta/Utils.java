package fr.inria.yajta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    public static boolean startWith(String str, String[] ins) {
        for( String in : ins ) {
            if( str.startsWith(in) )
                return true;
        }
        return false;
    }

    public static String[] format(String[] ar) {
        if(ar != null) {
            String[] res = new String[ar.length];
            if(ar.length > 0) {
                for(int i = 0; i < ar.length; i++) res[i] = ar[i].replace(".","/");
            }
            return res;
        } else {
            return null;
        }
    }

    public static String[] listClassesAsArray(File f) {
        Object[] classNamesO = listClasses(f).toArray();
        String[] classNames = new String[classNamesO.length];
        for(int i = 0; i < classNamesO.length; i++) classNames[i] = (String) classNamesO[i];
        return classNames;
    }

    public static List<String> listClasses(File f) {
        return listClassesExecutor(f,f);
    }

    public static List<String> listClassesExecutor(File f, File root) {
        List<String> res = new ArrayList<>();
        if(f.isDirectory()) {
            for(File c: f.listFiles()) {
                res.addAll(listClassesExecutor(c, root));
            }
        } else if (f.getName().endsWith(".class")) {
            res.add(f.getPath().split("\\.class")[0].substring(root.getPath().length()+1).replace("/","."));
        }
        return res;
    }

    public static File getATmpDir() {
        int i = (int) Math.floor(Math.random() * (double) Integer.MAX_VALUE);
        File tmp = new File(System.getProperty("java.io.tmpdir"));
        File dir = new File(tmp,"tmp" + i);
        dir.mkdir();
        return dir;
    }

}

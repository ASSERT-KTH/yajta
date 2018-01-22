package fr.inria.yajta;

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

}

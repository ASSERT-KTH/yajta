package fr.inria.yajta;

import java.io.File;

/**
 * Created by nharrand on 02/05/17.
 */


public class Args {
    public String[] INCLUDES, EXCLUDES, ISOTOPES;
    public File follow = null;
    public File includeFile = null;
    public File output = null;
    public boolean strictIncludes = false;
    public String print = "tree";
    //includes=|excludes=
    public void parseArgs(String args) {
        if(args == null || args.equals("")) {
            INCLUDES = new String[0];
            ISOTOPES = new String[0];
            EXCLUDES = new String[1];
            EXCLUDES[0] = "fr.inria.yajta";
        } else {
            String ar[];
            if(args.contains("|")) {
                ar = args.split("\\|");
            } else {
                ar = new String[1];
                ar[0] = args;
            }
            EXCLUDES = new String[1];
            INCLUDES = new String[0];
            ISOTOPES = new String[0];
            strictIncludes = false;
            print = "tree";
            for(String p : ar) {
                parseArg(p);
            }
        }
    }

    public void parseArg(String p) {
        //System.err.println("p: " + p);
        if(p.startsWith("strict-includes")) {
            parseStrictIncludes(p);
        } else if (p.startsWith("print=")) {
            parsePrint(p);
        } else if (p.startsWith("includes=")) {
            parseIncludes(p);
        } else if (p.startsWith("includeFile=")) {
            parseIncludeFile(p);
        } else if (p.startsWith("excludes=")) {
            parseExcludes(p);
        } else if (p.startsWith("isotopes=")) {
            parseIsotopes(p);
        } else if (p.startsWith("follow=")) {
            parseFollow(p);
        } else if (p.startsWith("output=")) {
            parseOutput(p);
        }
    }

    private void parseIncludeFile(String p) {
        includeFile = new File(p.split("includeFile=")[1]);
    }

    public void parseIsotopes(String p) {
        ISOTOPES = parseArray(p.split("isotopes=")[1]);
    }

    public void parseIncludes(String p) {
        INCLUDES = parseArray(p.split("includes=")[1]);
    }
    public void parseExcludes(String p) {
        EXCLUDES = parseArray(p.split("excludes=")[1]);
    }
    public void parseStrictIncludes(String p) {
        strictIncludes = true;
    }

    public void parseFollow(String p) {
        follow = new File(p.split("follow=")[1]);
        if(!follow.exists()) System.err.println("Unvalid file for follow argument");
    }

    public void parseOutput(String p) {
        output = new File(p.split("output=")[1]);
    }

    public void parsePrint(String p) {
        if(p.compareTo("print=list") == 0) {
            print = "list";
        } else if(p.compareTo("print=tie") == 0) {
            print = "tie";
        } else if(p.compareTo("print=values") == 0) {
            print = "values";
        }
    }

    public String[] parseArray(String li) {
        if(li.contains(","))
            return li.split(",");
        else {
            String res[] = new String[1];
            res[0] = li;
            return res;
        }
    }

    public void printUsage(String args) {
        System.err.println("Incorrect agent arguments. Argument must belong to the following list (and be separated by |)");
        System.err.println("\t\t- includes=org.package(,org.package2)* Default: Empty");
        System.err.println("\t\t- excludes=org.package(,org.package2)* Default: fr.inria.yajta");
        System.err.println("\t\t- isotopes=org.package(,org.package2)* Default:Empty");
        System.err.println("\t\t- print=(list,tree,tie) Default: tree");
        System.err.println("\t\t- strict-includes Default: false");
        System.err.println("\t\t- follow=File Default: null");
        System.err.println("\t\t- output=File Default: null");
        System.err.println("Found: \"" + args + "\"");
    }
}

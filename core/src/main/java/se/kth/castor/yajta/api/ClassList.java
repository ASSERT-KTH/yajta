package se.kth.castor.yajta.api;


import se.kth.castor.yajta.processor.util.MyEntry;
import se.kth.castor.yajta.processor.util.MyMap;

import java.net.URL;
import java.util.Arrays;

/**
 * ClassList implements alternative inclusion exclusion filter
 * For exemple if a ClassList cl includes a, a/a/a and excludes a/a with strict includes
 * cl.isToBeProcessed("a") will be true
 * cl.isToBeProcessed("a/b") will be true
 * cl.isToBeProcessed("a/a/a") will be true
 * cl.isToBeProcessed("a/a/a/b") will be true
 * but
 * cl.isToBeProcessed("b") will be false
 * cl.isToBeProcessed("a/a") will be false
 * cl.isToBeProcessed("a/a/b") will be false
 */
public class ClassList {
    String yajtaJar;


    public static ClassList getDefault(String packageToTrace) {
        return new ClassList(new String[]{packageToTrace}, null, null, true);
    }
    String[] JARS;

    public boolean isInJars(String classFilePath) {
        if(classFilePath.startsWith(yajtaJar)) return false;
        if(JARS == null) return true;
        for(String jar: JARS) {
            if(classFilePath.startsWith(jar)) return true;
        }
        return false;
    }

    PackTree rootTree;

    public ClassList(String[] includes, String[] excludes, String[] isotopes, boolean strictIncludes) {
        this(includes,excludes,isotopes,null,strictIncludes);

    }

    public ClassList(String[] includes, String[] excludes, String[] isotopes, String[] jars, boolean strictIncludes) {
        rootTree = new PackTree(!strictIncludes);
        if(includes != null) {
            for(String in : includes) {
                rootTree.add(split(in,'/'),true, !strictIncludes);
            }
        }
        if(excludes != null) {
            for(String ex : excludes) {
                rootTree.add(split(ex,'/'),false, !strictIncludes);
            }
        }
        rootTree.add(new String[]{"se","kth","castor","yajta"},false, !strictIncludes);
        this.JARS = jars;

        ClassLoader loader = ClassList.class.getClassLoader();
        URL jarURL = loader.getResource("se/kth/castor/yajta/api/ClassList.class");
        yajtaJar = jarURL.getPath()
                .replace("se/kth/castor/yajta/api/ClassList.class","")
                .replace("file:", "");

    }

    public boolean isToBeProcessed(String className) {
        if (className == null) return false;
        if (className.length() == 2 && className.charAt(0) == '[') return false;
        return rootTree.get(split(className,'/'));
    }

    public void print() {
        System.err.println("ClassList:\n" + rootTree.print());
    }


    class PackTree {
        boolean toInclude;
        PackTree(boolean toInclude) {
            this.toInclude = toInclude;
        }

        MyMap<String,PackTree> children = new MyMap<>();

        public void add(String[] path, boolean toInclude, boolean def) {
            if(path == null) return;
            if(path.length == 0) return;

            if(children.containsKey(path[0])) {
                if(path.length > 1) {
                    children.get(path[0]).add(Arrays.copyOfRange(path, 1, path.length), toInclude, children.get(path[0]).toInclude);
                } else {
                    children.get(path[0]).toInclude = toInclude;
                }
            } else {
                PackTree c;
                if(path.length == 1) {
                    c = new PackTree(toInclude);
                } else {
                    c = new PackTree(def);
                    c.add(Arrays.copyOfRange(path, 1, path.length), toInclude, this.toInclude);
                }
                children.put(path[0], c);
            }
        }

        public boolean get(String[] className) {
            if(className == null) return false;
            if(className.length == 0) return toInclude;
            if(children.containsKey(className[0])) {
                return children.get(className[0]).get(Arrays.copyOfRange(className, 1, className.length));
            } else {
                return toInclude;
            }
        }

        public String printNTabs(int n) {
            String res = "";
            for(int i = 0; i < n; i++) {
                res += "\t";
            }
            return res;
        }
        public String print() {
            return print(0);
        }

        public String print(int tab) {
            String res = "";
            for(MyEntry<String, PackTree> entry : children.entryList()) {
                res += printNTabs(tab) + entry.getKey() + " " + entry.getValue().toInclude + "\n";
                res += entry.getValue().print(tab+1);
            }
            return res;
        }

    }

    public static String[] split(String str, char delim) {
        int size = 1;
        for(char c: str.toCharArray()) {
            if(c == delim) size++;
        }
        String[] res = new String[size];
        int j = 0;
        int last = 0;
        for(int i = 0; i < str.length(); i++) {
            if(str.charAt(i) == delim) {
                res[j] = str.substring(last,i);
                j++;
                last = i+1;
            }
        }
        res[j] = str.substring(last);
        return res;
    }
}

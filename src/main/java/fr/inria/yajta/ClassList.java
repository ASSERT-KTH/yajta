package fr.inria.yajta;

import java.util.*;

public class ClassList {
    PackTree rootTree;

    public ClassList(String[] includes, String[] excludes, String[] isotopes, boolean strictIncludes) {
        rootTree = new PackTree(!strictIncludes);
        if(includes != null) {
            for(String in : includes) {
                rootTree.add(in.split("/"),true, !strictIncludes);
            }
        }
        if(excludes != null) {
            for(String ex : excludes) {
                rootTree.add(ex.split("/"),false, !strictIncludes);
            }
        }
        rootTree.add(new String[]{"fr","inria","yajta"},false, !strictIncludes);
    }

    public boolean isToBeProcessed(String className) {
        if (className == null) return false;
        return rootTree.get(className.split("/"));
    }


    class PackTree {
        boolean toInclude;
        PackTree(boolean toInclude) {
            this.toInclude = toInclude;
        }

        Map<String,PackTree> children = new HashMap<>();

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

    }
}

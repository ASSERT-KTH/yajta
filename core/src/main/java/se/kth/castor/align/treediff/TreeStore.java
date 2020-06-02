package se.kth.castor.align.treediff;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TreeStore {

    Map<String, Set<Tree>> existingTrees;
    Set<Tree> newTrees;

    public TreeStore() {
        existingTrees = new HashMap<>();
        newTrees = new HashSet<>();
    }

    //Pre: All children from t are already registered.
    public boolean addTree(Tree t) throws TreeNotFoundException {
        Set<Tree> entry;
        boolean isNew = false;
        if (!existingTrees.keySet().contains(t.getNodeName())) {
            isNew = true;
            entry = new HashSet<>();
            existingTrees.put(t.getNodeName(),entry);
        }
        entry = existingTrees.get(t.getNodeName());
        if(getTree(t) == null) isNew = true;

        if(isNew) {
            Tree n = t.copy();
            for(Tree c: t.getChildren()) {
                n.addChild(getTree(c));
            }
            //Update newest?
        }
        return isNew;
    }

    private Tree getTree(Tree t) {
        if (t == null) return null;
        Set<Tree> entry = existingTrees.get(t.getNodeName());
        if (entry == null) return null;
        for(Tree o : entry) {
            if(t.equals(o)) return o;
        }
        return null;
    }

    public class TreeNotFoundException extends Exception {
        public TreeNotFoundException(String e) {
            super(e);
        }
    }
}

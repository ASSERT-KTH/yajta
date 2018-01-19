package fr.inria.yajta.processor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nharrand on 11/07/17.
 */
public class TreeNode {
    protected String method;
    protected List<TreeNode> children;
    protected TreeNode parent;

    public TreeNode() {}

    public TreeNode addChild(String m) {
        TreeNode t = new TreeNode();
        t.method = m;
        t.parent = this;
        if(children == null) children = new ArrayList<>();
        children.add(t);
        return t;
    }

    public void print(BufferedWriter b) throws IOException {
        print(b, true);
    }

    public void print(BufferedWriter b, boolean tree) throws IOException {
        if(tree) b.append("{\"name\":\"" + method + "\", \"children\":[");
        else b.append(method + "\n");
        if(children != null) {
            boolean isFirst = true;
            for (TreeNode t : children) {
                if (isFirst) isFirst = false;
                else if(tree) b.append(",");
                t.print(b, tree);
            }
        }
        if(tree) b.append("]}");
    }

    public boolean hasNext() {
        return (children != null && (c < children.size())) || (parent != null && parent.hasNext());
    }

    public TreeNode next() {
        if(hasNext()) {
            if(children != null && (c < children.size())) {
                return children.get(c++);
            } else {
                return parent.next();
            }
        } else {
            return null;
        }
    }

    int c = 0;

    public void reset() {
        c = 0;
        for(TreeNode n : children) n.reset();
    }

    public TreeNode(JSONObject o) throws JSONException {
        method = o.getString("name");
        JSONArray jsonChildren = o.getJSONArray("children");
        if(jsonChildren.length() > 0) children = new ArrayList<>();
        for(int i = 0; i < jsonChildren.length(); i++) {
            TreeNode c = new TreeNode(jsonChildren.getJSONObject(i));
            c.parent = this;
            children.add(c);
        }
    }
}

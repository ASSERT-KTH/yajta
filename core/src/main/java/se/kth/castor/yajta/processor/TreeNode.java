package se.kth.castor.yajta.processor;

import java.io.BufferedWriter;
import java.io.IOException;

import se.kth.castor.yajta.processor.util.MyList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nharrand on 11/07/17.
 */
public class TreeNode {

    public static final long serialVersionUID = 0L;

    public String clazz;
    public String method;
    public MyList<TreeNode> children;
    public TreeNode parent;

    public TreeNode() {}

    public TreeNode addChild(String c, String m) {
        TreeNode t = new TreeNode();
        t.method = m;
        t.clazz = c;
        t.parent = this;
        if(children == null) children = new MyList<>();
        children.add(t);
        return t;
    }

    public void print(BufferedWriter b) throws IOException {
        print(b, true);
    }

    public void print(BufferedWriter b, boolean tree) throws IOException {
        if(tree) b.append("{\"class\":\"" + clazz + "\", \"method\":\"" + method + "\", \"children\":[");
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

    public void alternativePrint(BufferedWriter b, boolean tree) throws IOException {
        String src;
        if(clazz == null) src = null;
        else {
            if (clazz.startsWith("javafx")) src = "javafx";
            else if (clazz.startsWith("java")
                    || clazz.startsWith("sun")
                    || clazz.startsWith("com.sun")
                    ) src = "stdlib";
            else if (clazz.startsWith("Main")
                    || clazz.startsWith("Controllers")
                    || clazz.startsWith("Models")
                    || clazz.startsWith("Styling")
                    || clazz.startsWith("Views")
                    ) src = "App";
            else src = "other";
        }

        if(tree) b.append("{\"source\": \"" + src + "\", \"name\":\"" + clazz + "." + method + "\", \"children\":[\n");
        else b.append(method + "\n");
        if(children != null) {
            boolean isFirst = true;
            for (TreeNode t : children) {
                if (isFirst) isFirst = false;
                else if(tree) b.append(",");
                t.alternativePrint(b, tree);
            }
        }
        if(tree) b.append("\n]}");
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
        method = o.getString("method");
        clazz = o.getString("class");
        JSONArray jsonChildren = o.getJSONArray("children");
        if(jsonChildren.length() > 0) children = new MyList<>();
        for(int i = 0; i < jsonChildren.length(); i++) {
            TreeNode c = new TreeNode(jsonChildren.getJSONObject(i));
            c.parent = this;
            children.add(c);
        }
    }
}

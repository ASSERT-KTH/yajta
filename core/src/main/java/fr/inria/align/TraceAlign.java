package fr.inria.align;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.Generators;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import fr.inria.yajta.FileHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TraceAlign {

    private final static TreeContext treeContext = new TreeContext();

    public static void main( String[] args ) {
        if(args.length < 2) {
            System.out.println("Usage: fr.inria.align.TraceAlign TraceFile1 TraceFile2");
        }
        System.out.println("TraceAlign");
        File t1 = new File(args[0]);
        File t2 = new File(args[1]);
        Run.initGenerators();
        ITree tree1 = loadTrace(t2);
        ITree tree2 = loadTrace(t2);


        final MappingStore mappingsComp = new MappingStore();

        final Matcher m = new CompositeMatchers.ClassicGumtree(tree1, tree2, mappingsComp);
        //Matcher m = Matchers.getInstance().getMatcher(tree1, tree2); // retrieve the default matcher
        m.match();
        //m.getMappings();
        ActionGenerator g = new ActionGenerator(tree1, tree1, m.getMappings());
        g.generate();
        List<Action> actions = g.getActions();
        /*try {
            Run.initGenerators();
            ITree src = Generators.getInstance().getTree(args[0]).getRoot();
            ITree dst = Generators.getInstance().getTree(args[1]).getRoot();
            Matcher m = Matchers.getInstance().getMatcher(src, dst); // retrieve the default matcher
            m.match();
            ActionGenerator g = new ActionGenerator(src, dst, m.getMappings());
            g.generate();
            List<Action> actions = g.getActions(); // return the actions
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        System.out.println("Done");
    }

    public static ITree loadTrace(File trace) {
        ITree root = treeContext.createTree(-1, "root", "root");
        JSONObject o = FileHelper.readFromFile(trace);

        try {
            JSONArray threads = o.getJSONArray("children");
            for(int i = 0; i < threads.length(); i++) {
                ITree t = fromJSON(threads.getJSONObject(i));
                root.addChild(t);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static ITree fromJSON(JSONObject o) throws JSONException {
        String method = o.getString("method");
        String clazz = o.getString("class");
        JSONArray jsonChildren = o.getJSONArray("children");
        ITree tree = treeContext.createTree(42, clazz + method, "child");
        //ITree tree = new Tree(42,clazz + method);
        for(int i = 0; i < jsonChildren.length(); i++) {
            ITree c = fromJSON(jsonChildren.getJSONObject(i));
            tree.addChild(c);
        }
        return tree;
    }


}

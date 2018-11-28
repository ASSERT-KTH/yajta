package fr.inria.yajta.processor;

import fr.inria.yajta.processor.util.MyList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.function.Function;

public class IdTreeNode {
		public static Function<Integer, String> dico = i -> i.toString();

		public static final long serialVersionUID = 0L;

		public int id;
		public MyList<IdTreeNode> children;
		public IdTreeNode parent;

		public IdTreeNode() {}

		public IdTreeNode addChild(int id) {
			IdTreeNode t = new IdTreeNode();
			t.id = id;
			t.parent = this;
			if(children == null) children = new MyList<>();
			children.add(t);
			return t;
		}

		public void print(BufferedWriter b) throws IOException {
			print(b, true);
		}

		public void print(BufferedWriter b, boolean tree) throws IOException {
			String name = dico.apply(id);
			String src;
			if(name == null) src = null;
			else {
				if (name.startsWith("javafx")) src = "javafx";
				else if (name.startsWith("java")
						|| name.startsWith("sun")
						|| name.startsWith("com.sun")
						) src = "stdlib";
				else if (name.startsWith("Main")
						|| name.startsWith("Controllers")
						|| name.startsWith("Models")
						|| name.startsWith("Styling")
						|| name.startsWith("Views")
						) src = "App";
				else src = "other";
			}

			if(tree) b.append("{\"source\": \"" + src + "\", \"name\":\"" + name + "\", \"children\":[\n");
			else b.append(name + "\n");
			if(children != null) {
				boolean isFirst = true;
				for (IdTreeNode t : children) {
					if (isFirst) isFirst = false;
					else if(tree) b.append(",");
					t.print(b, tree);
				}
			}
			if(tree) b.append("\n]}");
		}

		public boolean hasNext() {
			return (children != null && (c < children.size())) || (parent != null && parent.hasNext());
		}

		public IdTreeNode next() {
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
			for(IdTreeNode n : children) n.reset();
		}
}
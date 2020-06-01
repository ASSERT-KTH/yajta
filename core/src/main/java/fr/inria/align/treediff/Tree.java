package fr.inria.align.treediff;

import java.util.List;

public interface Tree {
    String getNodeName();
    boolean equals(Tree other);
    List<Tree> getChildren();

    Tree copy();//Copy node only, without copying children

    void addChild(Tree tree);
}

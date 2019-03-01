package fr.inria.helloworldf;




import java.util.*;

/**
 * Created by nharrand on 04/10/17.
 */
public class Col {
    List<String> list;
    Set<String> set;
    Map<String, String> map;
    Collection<String> collection;
    Queue<String> queue;

    public Col () {
        list = new ArrayList<>();
        set = new HashSet<>();
        map = new HashMap<>();
        collection = new TreeSet<>();
        queue = new ArrayDeque<>();
    }



    void insert(Collection<String> c) {
        c.add("aaa");
        c.add("bbb");
        c.add("ccc");
        c.add("aaa");
    }

    void test() {
        insert(list);
        insert(set);
        insert(collection);
        insert(queue);
        map.put("aaa", "aaa");
        map.put("bbb", "bbb");
        map.put("ccc", "ccc");
        assert (list.size() == 4);
        assert (set.size() == 3);
        assert (queue.size() == 4);
        assert ((collection.size() == 4) || (collection.size() == 3));
        assert (map.size() == 3);
    }
}

package se.kth.castor.yajta.processor.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class MySetTest {

    @Test
    public void add() throws Exception {
        MySet<String> set = new MySet<>();
        assertTrue(set.size() == 0);
        set.add("A");
        assertTrue(set.size() == 1);
        assertTrue(set.contains("A"));
        set.add("B");
        assertTrue(set.size() == 2);
        assertTrue(set.contains("B"));
        set.add("A");
        assertTrue(set.size() == 2);
        assertTrue(set.contains("A"));
        assertFalse(set.contains("C"));
        assertFalse(set.contains("D"));
        assertFalse(set.contains(null));

    }

    @Test
    public void iterator() throws Exception {
        MySet<String> set = new MySet<>();
        assertTrue(!set.iterator().hasNext());
        List<String> values = new ArrayList<>();
        values.add("A");
        values.add("C");
        values.add("B");
        values.add("F");
        values.add("E");
        for (int i = 0; i < values.size(); i++) {
            set.add(values.get(i));
        }
        Iterator<String> it = set.iterator();
        for (int i = 0; i < values.size(); i++) {
            assertTrue(it.hasNext());
            String val = it.next();
            assertTrue(val,values.contains(val));
        }
        assertTrue(!it.hasNext());
    }

}
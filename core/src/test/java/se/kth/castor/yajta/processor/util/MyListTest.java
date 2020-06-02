package se.kth.castor.yajta.processor.util;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class MyListTest {

    @Test
    public void add() throws Exception {
        MyList<String> list = new MyList<>();
        assertTrue(list.size() == 0);
        list.add("A");
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).equals("A"));
        list.add("B");
        assertTrue(list.size() == 2);
        assertTrue(list.get(1).equals("B"));
        list.add("A");
        assertTrue(list.size() == 3);
        assertTrue(list.get(2).equals("A"));

    }

    @Test
    public void iterator() throws Exception {
        String[] values = new String[5];
        MyList<String> list = new MyList<>();
        assertTrue(!list.iterator().hasNext());
        values[0] = "A";
        values[1] = "B";
        values[2] = "C";
        values[3] = "B";
        values[4] = "A";
        for (int i = 0; i < values.length; i++) {
            list.add(values[i]);
        }
        Iterator<String> it = list.iterator();
        for (int i = 0; i < values.length; i++) {
            assertTrue(it.hasNext());
            assertEquals(it.next(),values[i]);
        }
        assertTrue(!it.hasNext());
    }

}
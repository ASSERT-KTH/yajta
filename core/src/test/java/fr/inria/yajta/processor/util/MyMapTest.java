package fr.inria.yajta.processor.util;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class MyMapTest {

    @Test
    public void put() throws Exception {
        MyMap<String,String> map = new MyMap<>();
        assertTrue(map.size() == 0);
        map.put("A","AA");
        assertTrue(map.size() == 1);
        assertTrue(map.get("A").equals("AA"));
        map.put("B","BB");
        assertTrue(map.size() == 2);
        assertTrue(map.get("B").equals("BB"));
        map.put("A","AB");
        assertTrue(map.size() == 2);
        assertTrue(map.get("A").equals("AB"));
        map.remove("A");
        assertTrue(map.size() == 1);
        assertTrue(map.get("A") == null);

    }

    @Test
    public void lists() throws Exception {
        String[] values = new String[5];
        MyMap<String,String> map = new MyMap<>();
        assertTrue(map.keyList().size() == 0);
        assertTrue(map.valueList().size() == 0);
        assertTrue(map.entryList().size() == 0);
        values[0] = "A";
        values[1] = "B";
        values[2] = "C";
        values[3] = "Z";
        values[4] = "G";
        for (int i = 0; i < values.length; i++) {
            map.put(values[i],values[i] + values[i]);
        }
        MyList<String> keys = map.keyList();
        MyList<String> vals = map.valueList();
        MyList<MyEntry<String, String>> entries = map.entryList();
        for (int i = 0; i < values.length; i++) {
            assertEquals(keys.get(i),values[i]);
            assertEquals(vals.get(i),values[i] + values[i]);
            assertEquals(entries.get(i).getKey(),values[i]);
            assertEquals(entries.get(i).getValue(),values[i] + values[i]);
        }
    }

}
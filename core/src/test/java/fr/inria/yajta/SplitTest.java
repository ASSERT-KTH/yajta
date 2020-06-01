package fr.inria.yajta;

import fr.inria.yajta.api.ClassList;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class SplitTest {
    @Test
    public void testSplit() {
        String res[] = ClassList.split("", '/');
        assertArrayEquals(res, new String[]{""});
        res = ClassList.split("aaaa", '/');
        assertArrayEquals(res, new String[]{"aaaa"});
        res = ClassList.split("aaaa/bbb", '/');
        assertArrayEquals(res, new String[]{"aaaa", "bbb"});
        res = ClassList.split("aaaa/bbb/ccc", '/');
        assertArrayEquals(res, new String[]{"aaaa","bbb","ccc"});
        res = ClassList.split("/bbb/ccc", '/');
        assertArrayEquals(res, new String[]{"","bbb","ccc"});
        res = ClassList.split("a/bbb/ccc", '/');
        assertArrayEquals(res, new String[]{"a","bbb","ccc"});
        res = ClassList.split("a/b/ccc", '/');
        assertArrayEquals(res, new String[]{"a","b","ccc"});
        res = ClassList.split("a//ccc", '/');
        assertArrayEquals(res, new String[]{"a","","ccc"});
        res = ClassList.split("a/b/", '/');
        assertArrayEquals(res, new String[]{"a","b",""});
    }
}

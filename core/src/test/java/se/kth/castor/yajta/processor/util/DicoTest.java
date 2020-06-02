package se.kth.castor.yajta.processor.util;

import org.junit.Test;

public class DicoTest {
    @Test
    public void testHash() {
        String str = "bonjour";
        int h = str.hashCode();
        System.out.println("str: " + str + ", h: " + h);
        str += " world";
        h = str.hashCode();
        System.out.println("str: " + str + ", h: " + h);
        String str2 = "bonjour world";
        int h2 = str2.hashCode();
        System.out.println("str2: " + str2 + ", h2: " + h2);
    }

}
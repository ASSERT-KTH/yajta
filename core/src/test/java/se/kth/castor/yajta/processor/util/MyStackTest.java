package se.kth.castor.yajta.processor.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyStackTest {

    @Test
    public void push() throws Exception {
        MyStack<String> stack = new MyStack<>();
        assertTrue(stack.size() == 0);
        stack.push("A");
        assertTrue(stack.size() == 1);
        assertFalse(stack.isEmpty());
        assertTrue(stack.peek().equals("A"));
        stack.push("B");
        assertTrue(stack.size() == 2);
        assertTrue(stack.peek().equals("B"));
        stack.push("C");
        assertTrue(stack.size() == 3);
        assertTrue(stack.peek().equals("C"));
        assertTrue(stack.pop().equals("C"));
        assertTrue(stack.size() == 2);
        assertTrue(stack.pop().equals("B"));
        assertTrue(stack.size() == 1);
        assertFalse(stack.isEmpty());
        assertTrue(stack.pop().equals("A"));
        assertTrue(stack.size() == 0);
        assertTrue(stack.isEmpty());

    }

}
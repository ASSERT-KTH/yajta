package fr.inria.helloworld;

/**
 * Created by nharrand on 19/04/17.
 */
public class A {
    class B {
        public int i = 1;
    }
    class C extends B {
        public int i = 2;
    }
    class D extends C {
        public int i = 3;
    }

    public <T extends B> void m( T i) {
        System.out.println("T extends B : " + i.i);
    }

    public <T extends C> void m( T i) {
        System.out.println("T extends C : " + i.i);
    }

    public void test () {
        B b = new B();
        System.out.println("B:");
        m(b);
        C c = new C();
        System.out.println("C:");
        m(c);
        D d = new D();
        System.out.println("D:");
        m(d);

    }
}

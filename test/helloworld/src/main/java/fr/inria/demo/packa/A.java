package fr.inria.demo.packa;

import fr.inria.demo.packb.B;

public class A {
	static int myStaticInt = 3;

	public static void main(String[] args) {
		m(new String[]{"Alfred Russel", "Charles"});
	}

	public static void m(String[] names) {
		B b = new B();
		System.out.println(b.sayHello());
		for(String name: names) {
			System.out.println(b.sayHello(name));
		}
	}
}

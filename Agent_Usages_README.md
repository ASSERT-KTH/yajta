# Agent usage exemples

All the following exemples are run on the following classes:

`fr.inria.demo.packa.A`:

```java
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
```


`fr.inria.demo.packb.B`:

```java
package fr.inria.demo.packb;

public class B {
	public String sayHello() {
		return "Hello";
	}

	public String sayHello(String name) {
		return "Hello " + name;
	}
}
```

## list

```bash
java -javaagent:core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar="strict-includes|print=list|includes=fr.inria.demo|output=samples/list.json -cp test/helloworld/target/classes fr.inria.demo.packa.A"
```

See output [here](samples/list)

## fasttree

```bash
java -javaagent:core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar="strict-includes|print=fasttree|includes=fr.inria.demo|output=samples/fasttree.json -cp test/helloworld/target/classes fr.inria.demo.packa.A"
```

See output [here](samples/fasttree.json)

## values

```bash
java -javaagent:core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar="strict-includes|print=values|includes=fr.inria.demo|output=samples/values.json -cp test/helloworld/target/classes fr.inria.demo.packa.A"
```

See output [here](samples/values.json)

## count

```bash
java -javaagent:core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar="strict-includes|print=count|includes=fr.inria.demo|output=samples/count.json -cp test/helloworld/target/classes fr.inria.demo.packa.A"
```

See output [here](samples/count.json)

## matrixclass

```bash
java -javaagent:core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar="strict-includes|print=matrixclass|includes=fr.inria.demo|output=samples/matrixclass.json -cp test/helloworld/target/classes fr.inria.demo.packa.A"
```

See output [here](samples/matrixclass)

## matrix

```bash
java -javaagent:core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar="strict-includes|print=matrix|includes=fr.inria.demo|output=samples/matrix.json -cp test/helloworld/target/classes fr.inria.demo.packa.A"
```

See output [here](samples/matrix.json)

## branch

```bash
java -javaagent:core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar="strict-includes|print=branch|includes=fr.inria.demo|output=samples/branch.json -cp test/helloworld/target/classes fr.inria.demo.packa.A"
```

See output [here](samples/branch.json)


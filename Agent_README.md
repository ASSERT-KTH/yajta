## Tracing agent

Java agent tracing methods calls.

Argument must belong to the following list (and be separated by |)
 * includes=org.package(,org.package2)* Default: Empty
 * excludes=org.package(,org.package2)* Default: fr.inria.yajta (always added to excludes)
 * isotopes=org.package(,org.package2)* Default:Empty
 * print=(list,tree,tie,matrix,branch) Default: tree
 * strict-includes Default: false
 * follow=File Default: null
 * output=File Default: log[random_number].json
 * from-bootstrap-classloader Default: false

Usage:
```
java -javaagent:path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar="Args" myApp 
```

Ex:
```
java javaagent:path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar="strict-includes|includes=org.myorg.myapp|excludes=fr.inria.yalta|output=oupout.json" -cp myJar.jar org.myorg.myapp.AppMainClass
```

Note that not excluding java,javax,sun,sunw might still result into carshes

Note that in order to instrument classes loaded by the bootstrap class loader you need to add the agent jar to its classpath.
```
java -Xbootclasspath/p:path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar -javaagent...
```
## Known Limitations

Does not log native methods yet.

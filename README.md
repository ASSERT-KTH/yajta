# yajta (Yet Another Java Tracing Agent)

Java agent tracing methods calls.

Argument must belong to the following list (and be separated by |)
	- includes=org.package(,org.package2)* Default: Empty
	- excludes=org.package(,org.package2)* Default: fr.inria.yajta (always added to excludes)
	- isotopes=org.package(,org.package2)* Default:Empty
	- print=(list,tree,tie) Default: tree
	- strict-includes Default: false
	- follow=File Default: null
	- output=File Default: null

Usage:
```
java -javaagent:path/to/yajta/target/yajta-1.0-SNAPSHOT-jar-with-dependencies.jar="Args" myApp 
```

Ex:
```
java javaagent:path/to/yajta/target/yajta-1.0-SNAPSHOT-jar-with-dependencies.jar="strict-includes|includes=org.myorg.myapp|excludes=fr.inria.yalta|output=oupout.json" -cp myJar.jar org.myorg.myapp.AppMainClass
```

Note that not excluding java,javax,sun,sunw might still result into carshes

Note that in order to instrument classes loaded by the bootstrap class loader you need to add the agent jar to its classpath.
```
java -Xbootclasspath/p:path/to/yajta/target/yajta-1.0-SNAPSHOT-jar-with-dependencies.jar -javaagent...
```

## Check that an App follows the same execution path than a previous one

Ex:
```
java javaagent:path/to/yajta/target/yajta-1.0-SNAPSHOT-jar-with-dependencies.jar="strict-includes|includes=org.myorg.myapp|excludes=fr.inria.yalta|follow=oupout.json" -cp myJar.jar org.myorg.myapp.AppMainClass
```

## Known Limitations

Does not log native methods yet.

# |-o-| tie |-o-| (Test Impact Explorer)


## Run test with traces

For each test (Trace only org.MyApp):
```
mvn -Dtest=MyTest -DargLine="-javaagent:path/to/yajta/target/yajta-1.0-SNAPSHOT-jar-with-dependencies.jar=\"strict-includes|print=tie|includes=org.myApp\"" test > testLog/MyTest
```

For each test (Trace (almost) everything):
```
mvn -Dtest=MyTest -DargLine="-javaagent:path/to/yajta/target/yajta-1.0-SNAPSHOT-jar-with-dependencies.jar=\"print=tie|excludes=java,javax,sun,sunw\"" test > testLog/MyTest
```

## Organize the output as a map of methods / set of test

```
java -cp path/to/yajta/target/yajta-1.0-SNAPSHOT-jar-with-dependencies.jar fr.inria.tie.Report -i testLog -o methodsImpact.json
```


# yajta (Yet Another Java Tracing Agent)

yajta is a extensible library for byte code probe insertion. Its built on top of javassist. It allow to build tracing agent but is not limited to this task. Probe insertion can be done both offline and at load time. So far, probe can be inserted only at the begining and the end of methods. Hopefully it will soon support insertion at any branching point.

Further more several tools that make us of that library are provided:

 * An agent that allow probe insertion at run time (See [Agent_README](Agent_README.md))
 * An agent that check that an execution follow a previous trace (See [Follower_README](Follower_README.md))
 * A tool that generate a test impact report (A json file containing for each method of the explored project the list of test the call it.) (See [TIE_README](TIE_README.md))
 * A way to insert probe offline (See [API_README](API_README.md))




## Tracing agent

Java agent tracing methods calls.

Argument must belong to the following list (and be separated by |)
 * includes=org.package(,org.package2)* Default: Empty
 * excludes=org.package(,org.package2)* Default: fr.inria.yajta (always added to excludes)
 * isotopes=org.package(,org.package2)* Default:Empty
 * print=(list,tree,tie,matrix) Default: tree
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

## Check that an App follows the same execution path than a previous one

Ex:
```
java javaagent:path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar="strict-includes|includes=org.myorg.myapp|excludes=fr.inria.yalta|follow=oupout.json" -cp myJar.jar org.myorg.myapp.AppMainClass
```

## Known Limitations

Does not log native methods yet.

# |-o-| tie |-o-| (Test Impact Explorer)

Simply run 

```bash
./tie.sh /path/to/yajta-jar org.MyPackage

```

## Manual run

### Run test with traces

For each test (Trace only org.MyApp):
```
mvn -Dtest=MyTest -DargLine="-javaagent:path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar=\"strict-includes|print=tie|includes=org.myApp\"" test > testLog/MyTest
```

For each test (Trace (almost) everything):
```
mvn -Dtest=MyTest -DargLine="-javaagent:path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar=\"print=tie|excludes=java,javax,sun,sunw\"" test > testLog/MyTest
```

### Organize the output as a map of methods / set of test

```
java -cp path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar fr.inria.tie.Report -i testLog -o methodsImpact.json
```

# Offline instrumentation

Will insert default probe to all classes in classDir and write the transformed class in output dir.
When run, the instrumented class will produce a json trace in a file called `yajta-trace.json` in the working directory.

Note that in order to run them you must add the yajta jar to your classpath (as the probe call classes from yajta).

```
#Instrument
java -cp path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar fr.inria.offline.Instrumenter classDir outputDir

#Run
java -cp outputDir:path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar org.myApp
```

# API

```Java
    //File classDir is the directory that contains byte code to be instrumented
    //You should implement in a class the interface Tracking (here TestLogger does)
    //Other constructors exist if you want to add filters to the class to be instrumented 
    //and/or specify the output for the transformed bytecode (by default a temporary directory is created)
    InstrumentationBuilder builder = new InstrumentationBuilder(classDir, TestLogger.class);
    //Note that the tracking class must also contain a static method getInstance() that returns an instance of the logger.
    //If bytecode is to be instrumented offline and run after the stop of the jvm, this getInstance() method should also register a shutdown hook that will call flush()
    //flush is supposed to contain whatever processing is supposed to be done after all logs are collected.

    //See fr.inria.yajta.api.loggerimplem.TestLogger as example.

    //Apply the instrumentation
    builder.instrument();
    //Optionally run the instrumented classes
    builder.setEntryPoint("fr.inria.helloworld.App", "main", String[].class);
    builder.runInstrumented((Object) new String[]{""});
```

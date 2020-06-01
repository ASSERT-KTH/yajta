## Coverage

### Build

```bash
	mvn install
```

### Usage

To instrument classes, run:

```bash
	java -cp /path/to/yajta/lean/target/yajta-lean-2.0.0-jar-with-dependencies.jar(:additional-class-path-elements) fr.inria.offline.CoverageInstrumenter -i target/classes -o target/instrumented
	mv target/classes target/original-classes
	mv target/instrumented target/classes
```
Note that the instrumenter may need to add the complete class path of the applications. (Can be computed through `mvn dependency:build-classpath`.)

### Run instrumented

When running instrumented classes, `/path/to/yajta/lean/target/yajta-lean-2.0.0-jar-with-dependencies.jar` need to be added to the classpath.

It can be done by editing the pom file

```xml
<dependency>
	<groupId>fr.inria</groupId>
	<artifactId>yajta</artifactId>
	<version>2.0.0</version>
	<scope>system</scope>
	<systemPath>/path/to/yajta/lean/target/yajta-lean-2.0.0-jar-with-dependencies.jar</systemPath>
</dependency>
```

### Reading the results

The execution of the instrumented code should lead to the creation of files `yajta_coverage-******.json` structured as a map that associate to fully qualified name of classes an array of methods name and signature.
Exemple:

```json
{"org.mypackage.MyClass":["myMethod([ILjava/util/List;)V"]}

```



# Follower

## Check that an App follows the same execution path than a previous one

Ex:
```
java javaagent:path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar="strict-includes|includes=org.myorg.myapp|excludes=fr.inria.yalta|follow=oupout.json" -cp myJar.jar org.myorg.myapp.AppMainClass
```

Argument must belong to the following list (and be separated by |)
 * includes=org.package(,org.package2)* Default: Empty
 * excludes=org.package(,org.package2)* Default: fr.inria.yajta (always added to excludes)
 * isotopes=org.package(,org.package2)* Default:Empty
 * print=(list,tree,tie,matrix) Default: tree
 * strict-includes Default: false
 * follow=File Default: null
 * output=File Default: log[random_number].json
 * from-bootstrap-classloader Default: false


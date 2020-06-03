## Tracing agent

Java agent tracing methods calls.

Argument must belong to the following list (and be separated by |)
 * includes=org.package(,org.package2)* Default: Empty
 * jars=/path/to/my/jar Default: Empty (trace only classes inside this jar list)
 * excludes=org.package(,org.package2)* Default: se.kth.castor.yajta (always added to excludes)
 * isotopes=org.package(,org.package2)* Default:Empty Deprecated
 * print=(list,tie,values,matrixclass,matrix,branch,count,remote,fasttie,fasttree,fastremote,userlogger) Default: tree
 * strict-includes Default: false
 * follow=File Default: null
 * output=File Default: log[random_number].json
 * from-bootstrap-classloader Default: false

Usage:
```
java -javaagent:path/to/yajta/core/target/yajta-core-2.0.0-jar-with-dependencies.jar="Args" myApp 
```

Ex:
```
java -javaagent:path/to/yajta/core/target/yajta-core-2.0.0-jar-with-dependencies.jar="strict-includes|includes=org.myorg.myapp|excludes=se.kth.castor.yalta|output=oupout.json" -cp myJar.jar org.myorg.myapp.AppMainClass
```

Note that not excluding java,javax,sun,sunw might still result into carshes

Note that in order to instrument classes loaded by the bootstrap class loader you need to add the agent jar to its classpath.
```
java -Xbootclasspath/p:path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar -javaagent...
```

## Tracing Method calls, parameters value and return value

Assuming a jar conating the following class:
```Java
package fr.inria.testentry;

public class AppValue {
    public static int getI(int i) {
        return i+1;
    }
    public static void main( String[] args ) {
        System.out.println("hello world! " + getI(3));
    }
}
```

Running this command:
```
java -javaagent:/path/to/yajta.jar="strict-includes|includes=fr.inria.testentry|print=values|output=values.json" -cp myJar.jar fr.inria.testentry.AppValue
```

should create a json file named `values.json` containing:
```JSON
{
  "name": "Threads",
  "yajta-version": "2.0.0",
  "serialization-version": 0,
  "children": [
    {
      "class": "Thread",
      "method": "main",
      "parameters": [],
      "returnValue": "null",
      "children": [
        {
          "class": "fr.inria.testentry.AppValue",
          "method": "main([Ljava/lang/String;)V",
          "parameters": [
            "rO0ABXVyABNbTGphdmEubGFuZy5TdHJpbmc7rdJW5+kde0cCAAB4cAAAAAA="
          ],
          "returnValue": "null",
          "children": [
            {
              "class": "fr.inria.testentry.AppValue",
              "method": "getI(I)I",
              "parameters": [
                "rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAD"
              ],
              "returnValue": "rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAE",
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```
Values are encoded in Base64.

## Known Limitations

 * Does not log native methods yet.
 * Traces are written in the output file by a shutdown hook. Thus, anything that stops the jvm without executing these hooks (Internal error, SIGKILL) will result in not writting traces.


## More exemples

See [Agent_Usages_README.md](Agent_Usages_README.md).
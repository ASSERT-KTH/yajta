# |-o-| tie |-o-| (Test Impact Explorer)

For a maven project for which test are in src/test, simply run 

```bash
./tie.sh /path/to/yajta-jar org.MyPackage

```

## Manual run

### Run test with traces

To run it manually, run for each test the agent with the following option (Trace only org.MyApp):
```
mvn -Dtest=MyTest -DargLine="-javaagent:path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar=\"strict-includes|print=tie|includes=org.myApp\"" test > log/MyTest
```

it will generate a trace for each test in the `log` folder.


### Organize the output as a map of methods / set of test

Then, run
```
java -cp path/to/yajta/target/yajta-2.0.0-jar-with-dependencies.jar fr.inria.tie.Report -i testLog -o tie-report.json
```
to generate the report.



# Follower

## Check that an App follows the same execution path than a previous one

Ex:
```bash
#Collect original trace
java javaagent:path/to/yajta/core/target/yajta-core-2.0.0-jar-with-dependencies.jar="strict-includes|includes=org.myorg.myapp|excludes=se.kth.castor.yalta|print=tree|output=oupout.json" -cp myJar.jar org.myorg.myapp.AppMainClass

#Check that the second execution follows the same trace
java javaagent:path/to/yajta/core/target/yajta-core-2.0.0-jar-with-dependencies.jar="strict-includes|includes=org.myorg.myapp|excludes=se.kth.castor.yalta|follow=oupout.json" -cp myJar.jar org.myorg.myapp.AppMainClass
```




#!/bin/bash

JARTOTEST=$1
AGENTPATH="/home/nharrand/Documents/yajta/target/yajta-1.0-SNAPSHOT-jar-with-dependencies.jar"
TMP="classes.tmp"


for cl in `cat $TMP`
do
	java -javaagent:$AGENTPATH="excludes=se.kth.castor,sun,java|includes=$cl|print=tree|output=output.json" -jar $JARTOTEST > /dev/null 2>&1
	status=$?
    if [ $status -ne 0 ]; then
        echo "!! $cl"
	else
		echo "OK $cl"
    fi
done



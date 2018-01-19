#!/bin/bash

pathToJarAgent=$1
tmpDir="log"
packages=$2
#pathToJarAgent="../../projet_art_et_numerique/whistle-bits/code-analyser/yajta/target/yajta-1.0-SNAPSHOT-jar-with-dependencies.jar"
#packages="org.apache.commons.lang3"

listTests() {
	#1 test dir
	find $1 -name "*Test.java" | rev | cut -d '/' -f1 | rev | cut -d '.' -f1
}

traceTest() {
	#1 test name
	mvn -o -Dtest=$1 -DargLine="-javaagent:$pathToJarAgent=\"strict-includes|includes=$packages|excludes=fr.inria.yalta|print=tie|output=$tmpDir/$1\"" test
}

mkdir $tmpDir
for t in `listTests src/test`
do
	traceTest $t
done

java -cp $pathToJarAgent fr.inria.tie.Report -i $tmpDir -o "tie-report.json"
#genReport


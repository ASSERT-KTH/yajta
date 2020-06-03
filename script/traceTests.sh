#!/bin/bash

rootDir=`pwd`
pathToJarAgent=$(echo "$0" | sed 's|script/generate_samples.sh|core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar|' | sed "s|\.|$PWD|")
tmpDir="$rootDir/traces"
packages=$1
testSrcDir="src/test"


listTests() {
	#1 test dir
	find $1 -name "*Test.java" | rev | cut -d '/' -f1 | rev | cut -d '.' -f1
}

traceTest() {
	#1 test name
	mvn -o -Dtest=$1 -DfailIfNoTests=false -DargLine="-javaagent:$pathToJarAgent=\"strict-includes|includes=$packages|excludes=se.kth.castor.yalta|print=tree|output=$tmpDir/$1\"" test
}

mkdir $tmpDir
for t in `listTests $testSrcDir`
do
	traceTest $t
done



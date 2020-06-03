#!/bin/bash

rootDir=`pwd`
pathToJarAgent=$(echo "$0" | sed 's|script/generate_samples.sh|core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar|' | sed "s|\.|$PWD|")
tmpDir="$rootDir/branchTraces"
logDir="$rootDir/branchTraces/log"
packages=$1
testSrcDir="src/test"


listTests() {
	#1 test dir
	find $1 -name "*Test.java" | rev | cut -d '/' -f1 | rev | cut -d '.' -f1
}

traceTest() {
	#1 test name
	mvn -o -Dtest=$1 -DfailIfNoTests=false -DargLine="-Xmx4G -javaagent:$pathToJarAgent=\"strict-includes|includes=$packages|print=branch|output=$tmpDir/$1.json\"" test > $logDir/$1.log 2>&1
}

mkdir $tmpDir
mkdir $logDir
for t in `listTests $testSrcDir`
do
	traceTest $t
done



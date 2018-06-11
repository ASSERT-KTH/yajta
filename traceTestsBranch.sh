#!/bin/bash

rootDir=`pwd`
pathToJarAgent=$1
tmpDir="$rootDir/branchTraces"
logDir="$rootDir/branchTraces/log"
packages=$2
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



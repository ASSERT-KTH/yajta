#!/bin/bash

rootDir=`pwd`
pathToJarAgent=$1
tmpDir="$rootDir/log"
excude=""
instrument=$(find . -type d -path '**/target/classes' | sed "s|\.|${PWD}|" | paste -sd "," -)

listTests() {
	#1 test dir
	#find . -type f -path '**/target/**/testCompile/**/inputFiles.lst' | xargs cat | rev | cut -d '/' -f1 | rev | cut -d '.' -f1 | sort | uniq
	find   -path '**/src/test/java/**/*.java' | rev | cut -d '/' -f1 | rev | cut -d '.' -f1
}

traceTest() {
	#1 test name
	mvn -o -Dtest=$1 -DfailIfNoTests=false -DargLine="-javaagent:$pathToJarAgent=\"strict-jar|jars=$instrument|exculdes=se.kth.castor.yalta$exclude|print=fasttie|output=$tmpDir/$1\"" test
}

mkdir $tmpDir
for t in `listTests`
do
	traceTest $t
done

java -cp $pathToJarAgent fr.inria.tie.Report -i $tmpDir -o "tie-report.json"
#genReport


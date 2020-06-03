#!/bin/bash

rootDir=`pwd`
pathToJarAgent=$(echo "$0" | sed 's|script/generate_samples.sh|core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar|' | sed "s|\.|$PWD|")
tmpDir="$rootDir/trace"
excude=""
instrument=$(find . -type d -path '**/target/classes' | sed "s|\.|${PWD}|" | paste -sd "," -)

listTests() {
	#1 test dir
	#find . -type f -path '**/target/**/testCompile/**/inputFiles.lst' | xargs cat | rev | cut -d '/' -f1 | rev | cut -d '.' -f1 | sort | uniq
	find   -path '**/src/test/java/**/*.java' | rev | cut -d '/' -f1 | rev | cut -d '.' -f1
}

traceTest() {
	#1 test name
	mvn -o -Dtest=$1 -DfailIfNoTests=false -DargLine="-javaagent:$pathToJarAgent=\"strict-jar|jars=$instrument|exculdes=se.kth.castor.yalta$exclude|print=fasttree|output=$tmpDir/$1\"" test
}

mkdir $tmpDir
for t in `listTests`
do
	traceTest $t
done



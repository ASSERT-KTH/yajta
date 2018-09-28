#!/bin/bash

rootDir=`pwd`
pathToJarAgent=$1
tmpDir="$rootDir/log"
packages=$2
testSrcDir="src/test"
#testSrcDir="org.eclipse.jgit.test/tst org.eclipse.jgit.http.test/tst org.eclipse.jgit.pgm.test/tst"
excude=""
#excude=",org.eclipse.jgit.ant"
#exclude="$exclude,org.eclipse.jgit.archive"
#exclude="$exclude,org.eclipse.jgit.console"
#exclude="$exclude,org.eclipse.jgit.http"
#exclude="$exclude,org.eclipse.jgit.java7"
#exclude="$exclude,org.eclipse.jgit.junit"
#exclude="$exclude,org.eclipse.jgit.packaging"
#exclude="$exclude,org.eclipse.jgit.pgm"
#exclude="$exclude,org.eclipse.jgit.test"
#exclude="$exclude,org.eclipse.jgit.ui"
#pathToJarAgent="../../projet_art_et_numerique/whistle-bits/code-analyser/yajta/target/yajta-1.0-SNAPSHOT-jar-with-dependencies.jar"
#packages="org.apache.commons.lang3"

listTests() {
	#1 test dir
	find  $testSrcDir -name "*.java" | rev | cut -d '/' -f1 | rev | cut -d '.' -f1
}

traceTest() {
	#1 test name
	#mvn -o -Dtest=$1 -DfailIfNoTests=false -DargLineBis="-javaagent:$pathToJarAgent=\"strict-includes|includes=$packages|excludes=fr.inria.yalta$exclude|print=tie|output=$tmpDir/$1\"" test
	#echo "mvn -o -Dtest=$1 -DfailIfNoTests=false -DargLine=\"-javaagent:$pathToJarAgent=\\\"strict-includes|includes=$packages|excludes=fr.inria.yalta$exclude|print=tie|output=$tmpDir/$1\\\"\" test"
	mvn -o -Dtest=$1 -DfailIfNoTests=false -DargLine="-javaagent:$pathToJarAgent=\"strict-includes|includes=$packages|excludes=fr.inria.yalta$exclude|print=tie|output=$tmpDir/$1\"" test
}

mkdir $tmpDir
for t in `listTests`
do
	traceTest $t
done

java -cp $pathToJarAgent fr.inria.tie.Report -i $tmpDir -o "tie-report.json"
#genReport


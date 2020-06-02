#!/bin/bash

rootDir=`pwd`
pathToJarAgent=$1
tmpDir="$rootDir/traces"
packages=$2
testSrcDir="src/test"
#testSrcDir=$3
#testSrcDir="org.eclipse.jgit.test/tst org.eclipse.jgit.http.test/tst org.eclipse.jgit.pgm.test/tst"

#exclude=$4
exclude=""
#exclude=",org.eclipse.jgit.ant"
#exclude="$exclude,org.eclipse.jgit.archive"
#exclude="$exclude,org.eclipse.jgit.console"
#exclude="$exclude,org.eclipse.jgit.http"
#exclude="$exclude,org.eclipse.jgit.java7"
#exclude="$exclude,org.eclipse.jgit.junit"
#exclude="$exclude,org.eclipse.jgit.packaging"
#exclude="$exclude,org.eclipse.jgit.pgm"
#exclude="$exclude,org.eclipse.jgit.test"
#exclude="$exclude,org.eclipse.jgit.ui"


listTests() {
	#1 test dir
	find $1 -name "*.java" | rev | cut -d '/' -f1 | rev | cut -d '.' -f1
}

traceTest() {
	#1 test name
	mvn -o -Dtest=$1 -DfailIfNoTests=false -DargLine="-javaagent:$pathToJarAgent=\"strict-includes|includes=$packages|excludes=se.kth.castor.yalta$exclude|print=matrix|output=$tmpDir/$1\"" test
}

mkdir $tmpDir
for t in `listTests $testSrcDir`
do
	traceTest $t
done



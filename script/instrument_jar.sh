#!/bin/bash


JAR_PATH=$(echo "$0" | sed 's|script/instrument_jar.sh|offline/target/yajta-offline-2.0.3-SNAPSHOT-jar-with-dependencies.jar|')

#echo "jar path: $JAR_PATH"
if [[ ! $# -eq 2 ]] ; then
    echo 'usage: script input.jar outputDir'
    exit 1
fi

#TMP dir clean up
TMP=$(echo "$2/yajta-tmp" | sed 's|//|/|g')
if [ -d $TMP ] ; then
	echo "removed $2/yajta-tmp"
	rm -rf $TMP
fi
mkdir $TMP

echo "--- Instrumenting jar $1 ---"
java -cp $JAR_PATH se.kth.castor.offline.RemoteUserInstrumenter -i $1 -o $TMP -y

JAR_NAME=$(echo $1 | rev | cut -d '/' -f1 | rev)
mv $TMP/$JAR_NAME $2

if [ -d $TMP ] ; then
	echo "removed $2/yajta-tmp"
	rm -rf $TMP
fi


#java -cp /home/nharrand/Documents/yajta/offline/target/yajta-offline-2.0.3-SNAPSHOT-jar-with-dependencies.jar se.kth.castor.offline.RemoteUserReader -i yajta-traceDir -o toto.json -f


echo "--- Done - Output jar is in $TMP ---"
echo ""

echo "Run your application with the generated jar in the classpath. Temporary traces should appear in a directory yajta-traceDir"
echo ""
echo "Run the following to generate json traces:"
echo "java -cp $JAR_PATH se.kth.castor.offline.RemoteUserReader -i yajta-traceDir -o trace.json -f"


#uncompress jar to output
#instrument
#add yajta lean
#recompress

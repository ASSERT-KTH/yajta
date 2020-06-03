#!/bin/bash

JAR_PATH=$(echo "$0" | sed 's|script/generate_samples.sh|core/target/yajta-core-2.0.2-SNAPSHOT-jar-with-dependencies.jar|'| sed "s|\.|$PWD|")



SAMPLES="samples"
if [[ ! -d $SAMPLES ]] ; then
    mkdir $SAMPLES
fi
README="Agent_Usages_README.md"
if [[ -f $README ]] ; then
    rm $README
fi

touch $README
echo "# Agent usage exemples" > $README
echo "" >> $README
echo "All the following exemples are run on the following classes:" >> $README
echo "" >> $README

OPTIONS="list fasttree values count matrixclass matrix branch"
CLASS_PATH=$(echo "$0" | sed 's|script/generate_samples.sh|test/helloworld/target/classes|'| sed "s|\.|$PWD|")
SRC_CLASS_A=$(echo "$0" | sed 's|script/generate_samples.sh|test/helloworld/src/main/java/fr/inria/demo/packa/A.java|'| sed "s|\.|$PWD|")
SRC_CLASS_B=$(echo "$0" | sed 's|script/generate_samples.sh|test/helloworld/src/main/java/fr/inria/demo/packb/B.java|'| sed "s|\.|$PWD|")
FQN="fr.inria.demo.packa.A"
FQNB="fr.inria.demo.packb.B"

echo "\`$FQN\`:" >> $README
echo "" >> $README
echo "\`\`\`java" >> $README
cat $README $SRC_CLASS_A >> $README
echo "\`\`\`" >> $README
echo "" >> $README
echo "" >> $README
echo "\`$FQNB\`:" >> $README
echo "" >> $README
echo "\`\`\`java" >> $README
cat $README $SRC_CLASS_B >> $README
echo "\`\`\`" >> $README
echo "" >> $README

for opt in $OPTIONS
do
	OUTPUT_FILE="$SAMPLES/$opt.json"
	echo " ----- Generate sample for $opt ----- "
	echo "## $opt" >> $README
	echo "" >> $README
	echo "\`\`\`bash" >> $README
	echo "Run: java -javaagent:$JAR_PATH=\"strict-includes|print=$opt|includes=fr.inria.demo|output=$OUTPUT_FILE" -cp $CLASS_PATH $FQN\" >> $README
	echo "\`\`\`" >> $README
	echo "" >> $README
	#echo "Run: java -javaagent:$JAR_PATH=\"strict-includes|print=$opt|includes=fr.inria.demo|output=$OUTPUT_FILE" -cp $CLASS_PATH $FQN\"
	java -javaagent:$JAR_PATH="strict-includes|print=$opt|includes=fr.inria.demo|output=$OUTPUT_FILE" -cp $CLASS_PATH $FQN
	
	echo "See output [here]($OUTPUT_FILE)" >> $README
	echo "" >> $README
done

mv $SAMPLES/matrixclass.json $SAMPLES/matrixclass
mv $SAMPLES/list.json $SAMPLES/list

sed -i 's|See output \[here\](samples/list.json)|See output [here](samples/list)|' $README
sed -i 's|See output \[here\](samples/matrixclass.json)|See output [here](samples/matrixclass)|' $README

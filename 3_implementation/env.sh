#!/bin/sh

echo "You should set environmental variable JDK_HOME or JAVA_HOME"

ANT_HOME=./tools/ant

if [ "$HUDUP_OLD_PATH" == "" ]
then
	HUDUP_OLD_PATH=$PATH
fi

PATH=.:$JDK_HOME/bin:$JAVA_HOME/bin:$ANT_HOME/bin:$HUDUP_OLD_PATH

echo PATH=$PATH

CLASSPATH=./hudup-core.jar:./hudup.jar:./sim.jar:./hudup-runtime-lib.jar:./sim-runtime-lib.jar:./bin:./lib/*:./working/lib/*:$EXTRA_CLASSPATH

echo CLASSPATH=$CLASSPATH

JAVA_CMD="java -cp '$CLASSPATH'"

JAVAW_CMD="java -cp '$CLASSPATH'"

EXTRA_CLASSPATH=./hudup-server.jar

. env.sh

eval $JAVA_CMD net.hudup.server.ext.EvaluatorCPList

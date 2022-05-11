cd ../..
EXTRA_CLASSPATH=./hudup-evaluator.jar
. env.sh
eval $JAVA_CMD net.hudup.server.ext.EvaluatorCP
cd tools/cp

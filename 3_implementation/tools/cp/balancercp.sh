cd ../..
EXTRA_CLASSPATH=./hudup-listener.jar
. env.sh
eval $JAVA_CMD net.hudup.listener.ui.BalancerCP
cd tools/cp

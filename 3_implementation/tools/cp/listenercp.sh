cd ../..
EXTRA_CLASSPATH=./hudup-listener.jar
. env.sh
eval $JAVA_CMD net.hudup.listener.ui.ListenerCP
cd tools/cp


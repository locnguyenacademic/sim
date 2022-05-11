EXTRA_CLASSPATH=./hudup-listener.jar

. env.sh

if [ "$1" == "service" ]
then
	eval $JAVAW_CMD net.hudup.Balancer &
else
	eval $JAVA_CMD net.hudup.Balancer
fi


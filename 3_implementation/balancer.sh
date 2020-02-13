./env.sh

if [ "$1" == "service" ]
then
	$JAVAW_CMD net.hudup.Balancer &
else
	$JAVA_CMD net.hudup.Balancer
fi


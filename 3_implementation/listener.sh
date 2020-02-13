./env.sh

if [ "$1" == "service" ]
then
	$JAVAW_CMD net.hudup.Listener &
else
	$JAVA_CMD net.hudup.Listener
fi
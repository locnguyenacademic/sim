./env.sh

if [ "$1" == "service" ]
then
	$JAVAW_CMD net.hudup.Server $1 $2 $3 $4 &
else
	$JAVA_CMD net.hudup.Server $1 $2 $3 $4
fi


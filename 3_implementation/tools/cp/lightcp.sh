cd ../..
. env.sh

if [ "$1" == "console" ]
then
	eval $JAVA_CMD net.hudup.core.client.LightRemoteServerCP console
else
	eval $JAVA_CMD net.hudup.core.client.LightRemoteServerCP
fi

cd tools/cp

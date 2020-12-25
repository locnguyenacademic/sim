. env.sh

if [ "$1" == "console" ]
then
	eval $JAVA_CMD net.hudup.core.client.RemoteServerCP console
else
	eval $JAVA_CMD net.hudup.core.client.RemoteServerCP
fi

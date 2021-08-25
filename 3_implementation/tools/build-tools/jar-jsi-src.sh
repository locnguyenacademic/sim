cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib-src=$1 jar-jsi-src
else
	./build.sh jar-jsi-src
fi
cd tools/build-tools

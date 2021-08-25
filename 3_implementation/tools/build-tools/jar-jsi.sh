cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-jsi
else
	./build.sh jar-jsi
fi
cd tools/build-tools

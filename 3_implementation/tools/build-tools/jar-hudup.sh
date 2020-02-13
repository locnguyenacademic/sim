cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-hudup
else
	./build.sh jar-hudup
fi
cd tools/build-tools

cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-ea
else
	./build.sh jar-ea
fi
cd tools/build-tools

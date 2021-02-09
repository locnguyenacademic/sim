cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-ml
else
	./build.sh jar-ml
fi
cd tools/build-tools

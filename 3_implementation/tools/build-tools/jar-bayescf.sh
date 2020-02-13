cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-bayescf
else
	./build.sh jar-bayescf
fi
cd tools/build-tools

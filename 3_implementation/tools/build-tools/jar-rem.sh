cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-rem
else
	./build.sh jar-rem
fi
cd tools/build-tools

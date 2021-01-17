cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-pso
else
	./build.sh jar-pso
fi
cd tools/build-tools

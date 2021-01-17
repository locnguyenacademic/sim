cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-hmm
else
	./build.sh jar-hmm
fi
cd tools/build-tools

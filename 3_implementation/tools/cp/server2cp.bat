@echo off

cd ..\..
set EXTRA_CLASSPATH=./hudup-server.jar
call .\env.bat
%JAVA_CMD% net.hudup.server.ext2.ExtendedServer2CP
cd tools\cp

@echo on

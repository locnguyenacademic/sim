@echo off

cd ..\..
set EXTRA_CLASSPATH=./hudup-server.jar
call .\env.bat
%JAVA_CMD% net.hudup.server.ext.ExtendedServerCP
cd tools\cp

@echo on

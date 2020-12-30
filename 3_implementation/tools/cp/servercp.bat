@echo off

cd ..\..
call .\env.bat
%JAVA_CMD% net.hudup.server.ext.ExtendedServerCP
cd tools\cp

@echo on

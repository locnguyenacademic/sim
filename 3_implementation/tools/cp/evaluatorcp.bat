@echo off

cd ..\..
call .\env.bat
%JAVA_CMD% net.hudup.server.ext.EvaluatorCP
cd tools\cp

@echo on

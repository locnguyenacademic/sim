@echo off

cd ..\..
set EXTRA_CLASSPATH=./hudup-evaluator.jar
call .\env.bat
%JAVA_CMD% net.hudup.server.ext.EvaluatorCP
cd tools\cp

@echo on

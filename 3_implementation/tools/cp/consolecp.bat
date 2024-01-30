@echo off

cd ..\..
call .\env.bat
%JAVA_CMD% net.hudup.core.logistic.console.ConsoleCP
cd tools\cp

@echo on

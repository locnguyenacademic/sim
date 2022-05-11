@echo off

cd ..\..
set EXTRA_CLASSPATH=./hudup-listener.jar
call .\env.bat
%JAVA_CMD% net.hudup.listener.ui.BalancerCP
cd tools\cp

@echo on

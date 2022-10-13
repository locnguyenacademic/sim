@echo off

cd ..\..
call .\env.bat
%JAVA_CMD% net.hudup.core.data.ui.DatasetPoolsManager
cd tools\cp

@echo on

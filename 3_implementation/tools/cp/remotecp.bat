cd ..\..
call .\env.bat

@echo off

if "%1" == "console" goto console
goto normal

:console
%JAVA_CMD% net.hudup.core.client.RemoteServerCP console
goto end

:normal
%JAVA_CMD% net.hudup.core.client.RemoteServerCP
goto end

:end
cd tools\cp

@echo on

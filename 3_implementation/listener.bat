set EXTRA_CLASSPATH=./hudup-listener.jar

call .\env.bat

@echo off

if "%1" == "service" goto service
goto normal

:service
start %JAVAW_CMD% net.hudup.Listener
goto end

:normal
%JAVA_CMD% net.hudup.Listener
goto end

:end

@echo on

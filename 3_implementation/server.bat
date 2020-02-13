call .\env.bat

@echo off

if "%1" == "service" goto service
goto normal

:service
start %JAVAW_CMD% -Xmx1g net.hudup.Server %1 %2 %3 %4
goto end

:normal
%JAVA_CMD% -Xmx1g net.hudup.Server %1 %2 %3 %4
goto end

:end
@echo on


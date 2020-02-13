@echo off

echo "You should set environmental variable JDK_HOME or JAVA_HOME"

set ANT_HOME=.\tools\ant
echo ANT_HOME = %ANT_HOME%

if "%HUDUP_OLD_PATH%" == "" set HUDUP_OLD_PATH=%PATH%

set PATH=.;%JDK_HOME%\bin;%JAVA_HOME%\bin;%ANT_HOME%\bin;%HUDUP_OLD_PATH%

echo PATH=%PATH%

set CLASSPATH=./hudup.jar;./hudup-server.jar;./hudup-evaluator.jar;./hudup-listener.jar;./hudup-toolkit.jar;./hudup-core.jar;./hudup-runtime-lib.jar;./bin;./lib/*;./sim.jar;./sim-bayescf.jar;./sim-hudup.jar;./sim-rem.jar;./sim-runtime-lib.jar

echo CLASSPATH=%CLASSPATH%

set JAVA_CMD=java -cp %CLASSPATH%

set JAVAW_CMD=javaw -cp %CLASSPATH%

@echo on

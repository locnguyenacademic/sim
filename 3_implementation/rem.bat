set EXTRA_CLASSPATH=./rem.jar;./sim-rem.jar

call .\env.bat

%JAVA_CMD% net.rem.regression.evaluate.RegressionEvaluator
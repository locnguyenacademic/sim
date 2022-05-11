set EXTRA_CLASSPATH=./hudup-evaluator.jar

call .\env.bat

%JAVA_CMD% net.hudup.Evaluator %1 %2 %3 %4
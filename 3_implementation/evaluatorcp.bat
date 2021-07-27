@echo off

call .\env.bat
%JAVA_CMD% net.hudup.server.ext.EvaluatorCPList

@echo on

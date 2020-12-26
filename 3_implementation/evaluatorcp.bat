@echo off

call .\env.bat
%JAVA_CMD% net.hudup.server.ext.EvaluatorCP

@echo on

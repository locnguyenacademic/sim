@echo off

call .\env.bat
%JAVA_CMD% net.hudup.core.client.RemoteServerCP

@echo on

echo off

REM Must have bank package built
echo ... building bank package
if not exist ..\bank\bank cd ..\bank & build & cd ..\atmcs-i

call setEnv.bat

set serverclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar

if not exist atmcs goto :IDL

if "%1"=="client" goto CLIENT
if "%1"=="server" goto SERVER

:IDL
call %VISIG_HOME%\bin\idl2java -no_comments -no_examples atmcs.idl

:SERVER
javac -classpath "..\bank;%serverclasspath%;%CLASSPATH%" Server.java
if "%1"=="server" goto :EOF

:CLIENT
javac -classpath %serverclasspath%;%CLASSPATH% Client.java


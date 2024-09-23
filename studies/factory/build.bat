echo off
call setEnv.bat

set serverclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar

if not exist factory goto :IDL

if "%1"=="client" goto CLIENT
if "%1"=="server" goto SERVER

:IDL
call %VISIG_HOME%\bin\idl2java -no_comments -no_examples factory.idl

:SERVER
javac -classpath %serverclasspath%;%CLASSPATH% Server.java

:CLIENT
javac -classpath %serverclasspath%;%CLASSPATH% Client.java


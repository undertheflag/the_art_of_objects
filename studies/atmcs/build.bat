echo off

call setEnv.bat

if NOT DEFINED PSE_HOME echo Environment PSE_HOME not set & goto :EOF

set PSE_CLASSPATH=%PSE_HOME%\pro.jar;%PSE_HOME%\tools.jar

set serverclasspath=%PSE_CLASSPATH%;%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar

if not exist atmcs goto :IDL

if "%1"=="client" goto CLIENT
if "%1"=="server" goto SERVER

:IDL
call %VISIG_HOME%\bin\idl2java -no_comments -no_examples atmcs.idl

:SERVER
if exist ..\atmcs-p\atmcs-p.jar goto SERVER_IF
echo ... building the persistent layer
cd ..\atmcs-p
call build
cd ..\atmcs
echo ... now back to the interface layer

:SERVER_IF
javac -classpath "..\atmcs-p\atmcs-p.jar;..\bank;%serverclasspath%;%CLASSPATH%" Server.java
if "%1"=="server" goto :EOF

:CLIENT
javac -classpath %serverclasspath%;%CLASSPATH% Client.java


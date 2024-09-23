echo off
call setEnv.bat

set ORB_CLASSPATH=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar

set LOCAL_CLASSPATH=..\;..\classes\regex.jar;..\classes\jsdk.jar;%ORB_CLASSPATH%

if "%1"=="idl" goto IDL
if not exist atmcs goto :IDL

goto SERVLET

:IDL
call %VISIG_HOME%\bin\idl2java -no_comments -no_examples -no_tie atmcs.idl
cd atmcs
javac -classpath "%LOCAL_CLASSPATH%;%CLASSPATH%" *.java 
cd ..

REM put the atmcs class files into a jar file (to be used by ibank)
jar -c0f atmcs-i.jar atmcs\*.class

:SERVLET
javac -classpath "%LOCAL_CLASSPATH%;%CLASSPATH%" *.java 


set LOCAL_CLASSPATH=

echo off

call setEnv.bat

if NOT DEFINED VISIG_LIB echo Environment VISIG_LIB not set & goto :EOF

echo Use 'build' to build both IDL and port.
echo Use 'build port' to build the port only.

set portclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar

if "%1"=="port" goto port

:IDL
call idl2java -no_comments port.idl
javac -classpath %portclasspath%;%CLASSPATH% port\*.java

:port
rem Force to build IDL layer
if NOT exist "port\ImagePortIF.class" goto IDL

rem Compile and output to .\(pcakge_name)\
javac -classpath "..;%portclasspath%;%CLASSPATH%" -d . *.java

rem Make a jar file for the port package
jar -c0f port.jar port\*.class

if "%1"=="port" goto :EOF

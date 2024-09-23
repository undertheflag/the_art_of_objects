echo off
call setEnv.bat

set serverclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar

if not exist pdm goto :IDL

if "%1"=="client" goto CLIENT
if "%1"=="server" goto SERVER
if "%1"=="applet" goto APPLET

if NOT "%1"=="idl" goto SERVER
:IDL
call %VISIG_HOME%\bin\idl2java -no_comments -no_examples pdm.idl
javac -classpath %serverclasspath%;%CLASSPATH% pdm\*.java

REM put the atmcs class files into a jar file
jar -c0f pdm.jar pdm\*.class

:SERVER
javac -classpath "%serverclasspath%;%CLASSPATH%" Server.java
if "%1"=="server" goto :EOF

:CLIENT
javac -classpath %serverclasspath%;%CLASSPATH% Client.java
if "%1"=="client" goto :EOF

:APPLET
if NOT DEFINED NETSCAPE_PROGRAM echo Environment NETSCAPE_PROGRAM not set & goto :EOF
javac -classpath %NETSCAPE_PROGRAM%\java\classes\java40.jar;%serverclasspath%;%CLASSPATH% PdmApplet.java
set NETSCAPE_PROGRAM=


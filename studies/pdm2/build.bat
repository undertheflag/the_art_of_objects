echo off
call setEnv.bat

set serverclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar

if not exist pdm goto :IDL

if "%1"=="client" goto CLIENT
if "%1"=="server" goto SERVER
if "%1"=="persistent" goto SERVER

if NOT "%1"=="idl" goto SERVER

:IDL
call %VISIG_HOME%\bin\idl2java -no_comments -no_examples pdm.idl
javac -classpath %serverclasspath%;%CLASSPATH% pdm\*.java

REM put the atmcs class files into a jar file
jar -c0f pdm.jar pdm\*.class


:SERVER
REM persistent layer 
rem compile & postprocess the class files.

rem You must set the environment variable PSE_HOME if
rem PSEPROJ is installed in other paths.

set PSE_HOME=C:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=D:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=E:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=F:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=G:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=

if NOT DEFINED PSE_HOME echo Environment PSE_HOME not set & goto :EOF

set OLD_CLASSPATH=%CLASSPATH%
set PSE_CLASSPATH=%PSE_HOME%\pro.jar;%PSE_HOME%\tools.jar
set CLASSPATH=%PSE_CLASSPATH%;%CLASSPATH%

javac -classpath "%serverclasspath%;%CLASSPATH%" Catalog.java

call %PSE_HOME%\bin\osjcfp -dest . -inplace @cfpargs

set CLASSPATH=%OLD_CLASSPATH%
set OLD_CLASSPATH=

if "%1"=="persistent" goto :EOF

REM interface layer 
javac -classpath "%serverclasspath%;%PSE_CLASSPATH%;%CLASSPATH%" Server.java
if "%1"=="server" goto :EOF

:CLIENT
javac -classpath %serverclasspath%;%CLASSPATH% Client.java
if "%1"=="client" goto :EOF


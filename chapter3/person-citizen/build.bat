@echo off
rem Compile and postprocess the class files.

echo This sample code required PSE Pro for Java (see README.TXT).
echo You must set the environment variable PSE_HOME if
echo PSEPROJ is installed non-standard paths.

if exist c:\odi\pseproj\bin set PSE_HOME=c:\odi\pseproj
if exist d:\odi\pseproj\bin set PSE_HOME=d:\odi\pseproj
if exist e:\odi\pseproj\bin set PSE_HOME=e:\odi\pseproj
if exist f:\odi\pseproj\bin set PSE_HOME=f:\odi\pseproj

if NOT DEFINED PSE_HOME echo Environment PSE_HOME not set & goto :EOF

set OLD_CLASSPATH=%CLASSPATH%
set PSE_CLASSPATH=%PSE_HOME%\pro.jar;%PSE_HOME%\tools.jar
set CLASSPATH=%PSE_CLASSPATH%;%CLASSPATH%

javac -d . *.java

call %PSE_HOME%\bin\osjcfp -dest . -inplace @cfpargs

set CLASSPATH=%OLD_CLASSPATH%
set OLD_CLASSPATH=

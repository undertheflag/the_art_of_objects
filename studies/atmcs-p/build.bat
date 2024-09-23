@echo off
rem Compile and postprocess the class files.

rem You must set the environment variable PSE_HOME if
rem PSEPROJ is installed in other paths.

set PSE_HOME=C:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=D:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=E:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=F:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=G:\odi\pseproj

if NOT DEFINED PSE_HOME echo Environment PSE_HOME not set & goto :EOF

set OLD_CLASSPATH=%CLASSPATH%
set PSE_CLASSPATH=%PSE_HOME%\pro.jar;%PSE_HOME%\tools.jar
set CLASSPATH=%PSE_CLASSPATH%;%CLASSPATH%

javac -d . *.java

call %PSE_HOME%\bin\osjcfp -dest atmcs -inplace @cfpargs

REM put the class files into a jar file (to be used be the full atmcs)
jar -c0f atmcs-p.jar atmcs\*.class

set CLASSPATH=%OLD_CLASSPATH%
set OLD_CLASSPATH=


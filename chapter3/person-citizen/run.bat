@echo off
if NOT DEFINED PSE_HOME echo Environment PSE_HOME not set & goto :EOF

set OLD_CLASSPATH=%CLASSPATH%
set PSE_CLASSPATH=%PSE_HOME%\pro.jar;%PSE_HOME%\tools.jar
set CLASSPATH=%PSE_CLASSPATH%;%CLASSPATH%

java DBRoot %1 %2

set CLASSPATH=%OLD_CLASSPATH%
set OLD_CLASSPATH=
@echo on


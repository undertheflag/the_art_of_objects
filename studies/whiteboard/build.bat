@echo off
echo Building the Shared Whiteboard package
echo Use "build all" to build both port and whiteboard.
echo Use "build clean" to do a clean build for all.

set PORT_LIB=port\port.jar

REM  Jump over to local if port has been built
if "%1"=="clean" goto CLEAN
if "%1"=="all" goto PORT
if exist %PORT_LIB% goto LOCAL

:CLEAN
  echo ...cleaning class files
  del /S /Q *.class

:PORT
  echo ...building the port package
  cd port
  call build.bat port
  cd ..
  echo ...back to original directory
  set PORT_LIB=port\port.jar

:LOCAL
  set OLD_CLASSPATH=%CLASSPATH%
  set LOCAL_CLASSPATH=%PORT_LIB%
  set CLASSPATH=%LOCAL_CLASSPATH%;%CLASSPATH%

  javac -classpath "%CLASSPATH%" jni/ImagePort.java wb/Main.java

  set CLASSPATH=%OLD_CLASSPATH%
  set OLD_CLASSPATH=

:END
  set PORT_LIB=

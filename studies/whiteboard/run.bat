@echo on

REM  Add the jni dir to path for DLL loading
set OLD_PATH=%PATH%
set PATH=.\jni;%PATH%

call port\setEnv.bat

if not defined VISIG_LIB echo ...running as standalone whiteboard

if defined VISIG_LIB set portclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar;%CLASSPATH%

if defined VISIG_LIB set VBROKER_FLAG=-Dorg.omg.CORBA.ORBClass=com.visigenic.vbroker.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=com.visigenic.vbroker.orb.ORB

java -classpath "port\port.jar;%portclasspath%" %VBROKER_FLAG% -DcomputerName=%COMPUTERNAME% -Ddebug=1 wb.Main mandrill.jpg %1 %2

set PATH=%OLD_PATH%
set OLD_PATH=
set VISIG_LIB=

@echo on


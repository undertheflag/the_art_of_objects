@echo on
echo Testing ImagePort

REM  Add the jni dir to path for DLL loading
set OLD_PATH=%PATH%
set PATH=..\jni;%PATH%

call setEnv.bat

set portclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar;%CLASSPATH%

java -classpath "port.jar;..\;%portclasspath%" -Dorg.omg.CORBA.ORBClass=com.visigenic.vbroker.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=com.visigenic.vbroker.orb.ORB -DcomputerName=%COMPUTERNAME% -Ddebug=1 port.ImagePort %1 %2

set PATH=%OLD_PATH%
set OLD_PATH=

@echo on


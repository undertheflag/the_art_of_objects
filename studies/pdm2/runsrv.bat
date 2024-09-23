echo off
call setEnv.bat

if NOT DEFINED PSE_HOME echo Environment PSE_HOME not set & goto :EOF
set PSE_CLASSPATH=%PSE_HOME%\pro.jar;%PSE_HOME%\tools.jar

set serverclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar;%PSE_CLASSPATH%;%CLASSPATH%

java -classpath %serverclasspath% -Dorg.omg.CORBA.ORBClass=com.visigenic.vbroker.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=com.visigenic.vbroker.orb.ORB Server %1 %2
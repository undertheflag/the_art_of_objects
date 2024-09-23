call setEnv.bat

if NOT DEFINED NETSCAPE_PROGRAM echo Environment NETSCAPE_PROGRAM not set & goto :EOF
set serverclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar;%CLASSPATH%

java -classpath "%NETSCAPE_PROGRAM%\java\classes\java40.jar;%serverclasspath%"  -Dorg.omg.CORBA.ORBClass=com.visigenic.vbroker.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=com.visigenic.vbroker.orb.ORB PdmApplet %1 %2

set NETSCAPE_PROGRAM=

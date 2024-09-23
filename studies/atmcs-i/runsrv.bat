call setEnv.bat

set serverclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar;%CLASSPATH%

java -classpath "%serverclasspath%;..\bank;..\atmcs-p" -Dorg.omg.CORBA.ORBClass=com.visigenic.vbroker.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=com.visigenic.vbroker.orb.ORB Server %1 %2
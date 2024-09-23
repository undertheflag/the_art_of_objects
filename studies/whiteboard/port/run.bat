call setEnv.bat

set portclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar;%CLASSPATH%

java -classpath "%portclasspath%" -Dorg.omg.CORBA.ORBClass=com.visigenic.vbroker.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=com.visigenic.vbroker.orb.ORB -DcomputerName=%COMPUTERNAME% -Ddebug=1 port.MessagePort %1 %2

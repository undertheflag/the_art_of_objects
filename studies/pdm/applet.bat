call setEnv.bat

set serverclasspath=%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar;%CLASSPATH%

appletviewer Pdm.htm %1 %2

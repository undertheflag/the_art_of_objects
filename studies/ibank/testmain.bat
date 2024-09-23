call setEnv.bat

REM This can be run from either this or its parent directory

set serverclasspath=..\;..\classes\jsdk.jar;%VISIG_LIB%\vbjorb.jar;%VISIG_LIB%\vbjapp.jar;%VISIG_LIB%\vbjtools.jar;%VISIG_LIB%\vbjcosnm.jar;%CLASSPATH%

java -classpath "%serverclasspath%" ibank.Main %1 %2

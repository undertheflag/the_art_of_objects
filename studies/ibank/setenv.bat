REM for JDK
set JAVA_HOME=C:\jdk
if not exist %JAVA_HOME% set JAVA_HOME=D:\jdk
if not exist %JAVA_HOME% set JAVA_HOME=E:\jdk
if not exist %JAVA_HOME% set JAVA_HOME=F:\jdk
if not exist %JAVA_HOME% set JAVA_HOME=G:\jdk

REM set the environment for visibroker
set VISIG_HOME=C:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=D:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=E:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=F:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=

if exist %VISIG_HOME% set VISIG_LIB=%VISIG_HOME%\lib

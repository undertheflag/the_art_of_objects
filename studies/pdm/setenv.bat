REM set the environment for visibroker
set VISIG_HOME=C:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=D:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=E:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=F:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=G:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=

if exist %VISIG_HOME% set VISIG_LIB=%VISIG_HOME%\lib

set NETSCAPE_PROGRAM=E:\netscape47\Program
if not exist %NETSCAPE_PROGRAM% set NETSCAPE_PROGRAM=C:\Progra~1\Netscape\Communicator\Program


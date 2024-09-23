REM set the environment for visibroker
set VISIG_HOME=C:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=D:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=E:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=F:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=G:\vbroker
if not exist %VISIG_HOME% set VISIG_HOME=

if exist %VISIG_HOME% set VISIG_LIB=%VISIG_HOME%\lib

rem You must set the environment variable PSE_HOME if
rem PSEPROJ is installed in other paths.

set PSE_HOME=C:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=D:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=E:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=F:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=G:\odi\pseproj
if not exist %PSE_HOME% set PSE_HOME=


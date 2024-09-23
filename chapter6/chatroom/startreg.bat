echo off
echo ... Starting rmi registry
echo     You may close this window afterwards ...

REM     The following note is from JDK documentation.

REM     Note: Before you start the rmiregistry, you must make sure that the
REM     shell or window in which you will run the registry, either has no
REM     CLASSPATH set or has a CLASSPATH that does not include the path to any
REM     classes that you want downloaded to your client, including the stubs for
REM     your remote object implementation classes. 

REM     If you start the rmiregistry, and it can find your stub classes in its
REM     CLASSPATH, it will ignore the server's java.rmi.server.codebase
REM     property, and as a result, your client(s) will not be able to download
REM     the stub code for your remote object. 

set CLASSPATH= & rmiregistry


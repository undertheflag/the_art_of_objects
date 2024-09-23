REM Be sure to run the rmi registry.
REM This will close the console after return.
start cmd /C startReg

REM Must specify codebase and policy here.

REM Codebase must end with "/".
REM if code base is incorrect, will see:
REM    ClassNotFoundException: ChatRoomImpl_Stub

REM set the current working dir
FOR /F %%i IN ('cd') DO set pwd=%%i

java -Djava.rmi.server.codebase=file:///%pwd%/ -Djava.security.policy=policy ChatRoomImpl %1 %2 %3



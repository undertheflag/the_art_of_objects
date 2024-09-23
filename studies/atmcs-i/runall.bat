echo off
REM run all bank and atmcs servers and clients

REM bank server and client with test data
cd ..\bank

start runsrv
echo Please wait for the bank server to start up.
pause

rem run the client for bank then close the window
start cmd /c client %COMPUTERNAME% test1.txt

cd ..\atmcs-i

pause
start runsrv

pause
client

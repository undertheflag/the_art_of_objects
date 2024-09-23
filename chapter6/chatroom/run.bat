echo off
echo ... Testing the Chat Room

REM First run a test
java ChatRoomImpl test %1

REM This will close the console after return.
start cmd /C runsrv

REM Pause 2 seconds before starting client so that the server should have
REM started by then.
java Pause 2

client John localhost test1.txt


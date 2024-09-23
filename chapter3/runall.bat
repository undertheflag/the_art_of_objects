REM   Run tests in all sub directories
FOR /F %%i IN ('dir /ad /b') DO cd %%i & del /Q test.od? & run test.odb test1.txt & cd .. 

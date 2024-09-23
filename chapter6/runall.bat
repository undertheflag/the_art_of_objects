REM   Run tests in all sub directories
FOR /F %%i IN ('dir /ad /b') DO if exist %%i\run.bat cd %%i & run & cd .. 

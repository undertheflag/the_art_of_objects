REM   Build all sub directories
FOR /F %%i IN ('dir /ad /b') DO if exist %%i\buildall.bat cd %%i & buildall & cd .. 

build

REM   Build all sub directories
FOR /F %%i IN ('dir /ad /b') DO if exist %%i\build.bat cd %%i & build & cd .. 

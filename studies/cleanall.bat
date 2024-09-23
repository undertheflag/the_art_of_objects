REM   Clean all sub directories
FOR /F %%i IN ('dir /ad /b') DO if exist %%i\clean.bat cd %%i & clean & cd .. 

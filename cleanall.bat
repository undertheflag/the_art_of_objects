REM   Clean all sub directories
FOR /F %%i IN ('dir /ad /b') DO if exist %%i\cleanall.bat cd %%i & cleanall & cd .. 

REM   Clean docs
rmdir /S /Q docs

del *.bak /S /Q

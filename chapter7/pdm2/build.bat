echo off
call setEnv.bat

call %VISIG_HOME%\bin\idl2java -no_comments -no_tie -no_stub -no_skel -no_bind -no_examples -no_toString pdm.idl

del /Q pdm\*Holder.java pdm\*Helper.java

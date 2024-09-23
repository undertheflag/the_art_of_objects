echo off
call setEnv.bat

call %VISIG_HOME%\bin\idl2java -no_comments -no_tie -no_stub -no_skel -no_bind -no_examples -no_toString factory.idl

del /Q factory\*Holder.java factory\*Helper.java

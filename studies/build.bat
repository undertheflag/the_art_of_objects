echo off

call ibank\setEnv.bat

javac -classpath "classes\jsdk.jar;%CLASSPATH%" *.java

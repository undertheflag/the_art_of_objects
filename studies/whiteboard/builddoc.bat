REM build the HTML documents

if NOT exist docs\html mkdir docs\html
javadoc -private -d docs\html wb graph jni widget math port
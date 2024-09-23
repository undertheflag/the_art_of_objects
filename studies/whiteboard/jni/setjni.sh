#!/bin/csh -f

if (!($?JAVAWB)) then
  echo "The environment JAVAWB has not been defined!"
else
  setenv LD_LIBRARY_PATH "$JAVAWB/jni":"$LD_LIBRARY_PATH"
  setenv PATH "$PATH":$JAVAWB/jni
endif

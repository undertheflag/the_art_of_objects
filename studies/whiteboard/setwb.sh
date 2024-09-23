#!/bin/csh -f

# the root for Java WB  - redefine this for your system!
setenv JAVAWB /whiteboard

# home of JDK - redefine this for your system!
setenv JDK_HOME /export/home/java

if (!($?JAVAWB)) then
  echo "The environment JAVAWB has not been defined!"
else
  setenv CLASSPATH "$CLASSPATH":$JAVAWB
endif

if (!($?JDK_HOME)) then
  echo "The environment JDK_HOME has not been defined!"
else
endif

# set env for others
source ../jni/setjni.sh
#source ../nitf/setnitf.sh


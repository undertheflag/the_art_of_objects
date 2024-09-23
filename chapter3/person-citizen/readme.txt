This sample code required an installation of PSE Pro for 
Java, which is a persistent storage engine from Excelon Inc. 
(formerly Object Design Inc.)

In PSE Pro, a file ("cfpargs") lists the persistent enabled
classes.  A post processor is used to process those class
files and make them persistent capable.


You must set the environment variable PSE_HOME to point to
the installed path of PSE Pro.

Information about PSE Pro can be found in: ..\studies\studies.htm

See ..\Person-Hobby or  ..\university for examples that do 
not require PSE Pro.

---------------

To build the sample:
	build

To run with transient objects:
	run

To run with persistent objects:
	run test.odb
This will generate three database files: test.odb, test.odf, test.odt

To run with persistent objects and input command file:
	run test.odb test1.txt


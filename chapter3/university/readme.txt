This example uses the persistent object manager class POManager.
Persistent enabled classes are marked by "implements
java.io.Serializable".  Java can then serialize the objects and
save them to a file.

The example in ..\Person-Citizen uses PSE Pro, which uses a
separate file ("cfpargs") to list the persistent enabled classes.

---------------

To build the sample:
	build

To run with transient objects:
	run

To run with persistent objects:
	run test.odb
This will generate one database file: test.odb

To run with persistent objects and input command file:
	run test.odb test1.txt


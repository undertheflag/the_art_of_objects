
		 Shared Whiteboard Version 1.0 README file

-------------------------------------------------------

IMPORTANT: Shared Whiteboard runs on JDK 1.2 or above !!

-------------------------------------------------------

Building Shared Whiteboard from wb10.zip on Windows NT

- unzip wb10.zip to a directory, say C:\whiteboard

- cd to that directory and do:
	build

- To run, type:
	run [port_name]

  where port_name is the name of this whiteboard.

==================================================================

Java Whiteboard directory structure

olympic.cgm     a CGM file containing the olympic logo
mandrill.jpg    an image file for testing
rocket.ntf      an NITF file containing a rocket clipping and the olympic logo
rocket-mosaic.ntf  an NITF file containing a rocket and the olympic logo
setwb.sh	Unix shell script for setting environment
whiteboard.lis  List of all files in the package

........ docs/	Documents

wb.ppt		Some object design diagrams
wb.txt		Tracking document

........ wb/

Main.java  - Main class containing the graphic user interface 
  	     components of the Whiteboard window.
InfoDialog.java - A dialog with a text area, an input field, and a button.

........ images/

*.gif      - Images for buttons

....... widget/

ImageLabel.java  - Base class for a Canvas with an image 
ImageButton.java - Image buttons used in Main
DrawCanvas.java  - The major Canvas for drawing and displaying images and markups
LineChooser.java - Used by Main
FontChooser.java - Used by Main
ColorChooser.java- Used by Main

...... graph/ - graphic lib and tools

Glyph.java      Defines basic glyphs (line, rectangle, etc) and their operations
G2dTool.java    2D drawing tools for circle, line, etc.
TextLine.java   a line of text

Cgm.java	CGM driver
CgmTool.java    CGM library tool

...... math/

SpecialFunction.java  used by TextLine.java  

........ port/  Utility for sharing whiteboard contents with others

port.idl		Interface definition for the port package.  Two interfaces
			are defined: MessagePortIF and ImagePortIF.
MessagePort.java	The controller object for the message port,
			which is a superclass of ImagePort
MessagePortImpl.java	The interface implementation class for the message
			port
ImagePort.java		The controller object for the image port.  It 
			uses the jni/ImagePort to convert image to other
			native formats.
ImagePortImpl.java	The interface implementation class for the image port

build.bat     		A batch file for building the package on Windows
run.bat			For running the message port as a stand-alone application
TestImagePort.bat	For testing the image port via a terminal

....... jni/    Java Native Interface

ImagePort.java	Contains the image data structure and declare native calls
ImagePort.cpp   Contains the native methods that exchange data structure with
		jni/ImagePort.java
jni_ImagePort.h Generated from jni/ImagePort.java by javah

ImagePort.mdp	Project file for MS visual C++
ImagePort.mak	Makefile for generating libImagePort.so

Nitf.cpp	A class for NITF file in/out including cgm
Tiff.cpp	An image class with Tiff-like metadata
tiff.h		used by Tiff.cpp

ImagePort.dll   Dynamic link libraries for NT
libImagePort.so Dynamic link libraries for Unix (not included)

NITFSjpegcmp    Executables for compressing and decompress jpeg files.
NITFSjpegdcmp   They are called by nitf/unpackimg.c

...... nitf/	(not included in this version)

nitf.zip   	Nitf service lib

...... jpeg/	(not included in this version)

jpeg.zip   	JPEG lib

-------------------------------------------------------

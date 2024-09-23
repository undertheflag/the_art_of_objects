# makefile for ImagePort
# 
# the lib ${NITFDIR}/libnitf.a should have been built by now
#
# root directory of package
ROOTDIR = ..
#
# libs (*.h, *.so, *.a) under ROOTDIR
TIFFDIR	= ${ROOTDIR}/tiff
NITFDIR	= ${ROOTDIR}/nitf
CGMDIR	= ${ROOTDIR}/cgm
#
JAVAC	= javac
JAVAH	= javah
JAVADIR = ${JDK_HOME}/include
SOLJAVADIR = ${JDK_HOME}/include/solaris
#
AR	= /usr/ccs/bin/ar
AROPTS	= crs
#
# Compiler options
CC	= gcc
CCC	= g++
#
#COPTS	= -g 
# this builds the symbol tables
COPTS	= -g -fpic -G
#
OPTIMIZER=-O
#OPTIMIZER=
#
# Include paths
IPATH	= -I. -I${NITFDIR} -I${JAVADIR} -I${TIFFDIR} -I${CGMDIR}/include -I${SOLJAVADIR}
CFLAGS	=  ${COPTS} ${OPTIMIZER} ${IPATH}
#
# Link libraries ( -lxxx => libxxx.a )
LIBNITF = -L${NITFDIR} -lnitf
LIBTIFF	= -L${TIFFDIR} -ltiff
LIBCGM	= -L${CGMDIR}/bin -lcgm

# All libs
LIBS	= ${LIBNITF} ${LIBTIFF} -lm

# target name
TARGET1	= libImagePort.so
TARGET2	= libGlyphPort.so
TARGET8	= Nitf
TARGET9	= Tiff

TARGETS	= ${TARGET1} ${TARGET2} ${TARGET9}

OBJ1	= ImagePort.o Tiff.o Nitf.o
OBJ2	= GlyphPort.o Glyph.o
OBJS	= ${OBJ1} ${OBJ2} 

all:	${TARGET1}
	echo "All targets successfully built."

# $^ includes all dependents
${TARGET1}: ${OBJ1} ${NITFDIR}/libnitf.a
	$(JAVAH) ImagePort
	$(CCC) ${CFLAGS} ${OBJ1} ${NITFDIR}/libnitf.a -o $@

${TARGET2}: ${OBJ2} 
	$(AR) $(AROPTS) tem.a ${OBJ2} 
	$(CCC) ${CFLAGS} tem.a ${LIBCGM} -o $@

${TARGET8}: ${TARGET8}.o Tiff.o ${NITFDIR}/libnitf.a
	$(CCC) ${IPATH} ${TARGET8}.o Tiff.o ${NITFDIR}/libnitf.a ${LIBS} -o $@

${TARGET9}: ${TARGET9}.o ${NITFDIR}/libnitf.a
	$(CCC) ${IPATH} ${TARGET9}.o ${NITFDIR}/libnitf.a ${LIBS} -o $@

slim:
	rm -f ${OBJS} core

clean:
	rm -f ${OBJS} core
	rm -f ${TARGETS} core

# ------------------------------------------------------------------
# Generic dependence and rules 
# ------------------------------------------------------------------
# $* gives target name without suffix
%.h:	%.class
	$(JAVAH) $*

%.o:	%.cpp
	$(CCC) $(CFLAGS) -c $< -o $@

# compile from first dependent and libs to target
%: 	%.o
	${CC} ${CFLAGS} $< ${LIBS} -o $@

%.o:	%.c
	$(CC) $(CFLAGS) -c $< -o $@

.f.o:	$(FC) $(FFLAGS) -c $< -o $@
  
# ------------------------------------------------------------------

//*************************************************************************
/*
 * ImagePort.java - Java Native Interface to various image library functions. 
 *  (compatible with JDK 1.1 JNI specifications)
 *
 *   This file should be under $CLASSPATH/jni
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package jni;

/**
 * A final class containing all native methods to image libraries.
 * The idea is to define all image data as primitive field variables
 * in this class, so that the native code can access them easily.
 *
 * The data transfer methods are encapsulated in the upper layer of
 * the object hierachy.  As an example:
 * <PRE>
 *             sendTo    -------------  read (native code)
 * DrawCanvas <--------> | ImagePort | <------------------> File
 *             getFrom   -------------  write
 *
 * Steps of calling:
 * 1. ImagePort imgp = new ImagePort();
 * 2. DrawCanvas.sendTo(imgp);
 *    imgp.write(file);  
 * or
 *    imgp.read(file);
 *    DrawCanvas.getFrom(imgp);
 * </PRE>
 * The type of native calls is determined by the extension of file.
 *
 * @see wb.Main
 */
public final class ImagePort {

  // Fields for interfacing with native codes

  /** Base image in ARGB */
  public int width;
  public int height;
  public int[] pixels;  // pixel = ARGB buffer

  /** An array of byte buffers for CGM */
  public byte[][] cgm;

  // Private fields
  Object o;    // the kind of object being exchanged

  static { 
    // this will load XXX.dll or libXXX.so, depending on native system
    System.loadLibrary("ImagePort");  
  }

  /** Constructor  */
  public ImagePort (Object o) {
    this.o = o;
  }

  // ............................................ Native methods
  /** 
   * Read an image file.  
   * @param file filename.  Its extension determines the file type and
   *     	the calls used in ImagePort.c
   * @return 0 normal; -1 error;
   */
  public native int read (String file);

  /** 
   * Write an image file.  
   * @param file filename.  Its extension determines the file type and
   *        	   the calls used in ImagePort.c
   * @return 0 normal; -1 error;
   */
  public native int write (String file);

}

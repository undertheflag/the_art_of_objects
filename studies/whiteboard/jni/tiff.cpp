//*************************************************************************
/*
 * Tiff.cpp - A Class for NITF file in/out.
 *
 *  This file should be under $CLASSPATH/jni
 *
 *  Description: 
 *    An image class with Tiff-like metadata.  Many imagery operations
 *    are encapsulated within.  Examples are binary operations posdiff
 *    and insert.  Supported output file formats are: Tiff,
 *    Nitf.
 *
 *    The incorporation of Tiff in/out functions can be controlled
 *    separately by TIFF_IO.  When it is not defined, link to libtiff.a
 *    is not needed.  Similar procedure works for Nitf.
 *
 *    This file can be used as a header file as follows:
 *      #ifndef HEADER_ONLY
 *      #define HEADER_ONLY
 *      #include "Tiff.cpp"
 *      #undef HEADER_ONLY
 *      #endif
 *
 *  Revision History:
 *    12-19-96 YTLau  created
 *    4-1-97   YTLau  separate tiff i/o functions with directive
 *    5/14/97  YTLau  Version 1.0 delivery.  It has nitf i/o functions.
 *    5/20/97  YTLau  Major enhancement for insert().  Image size is 
 *                    changed dynamically when the insert is out of 
 *                    image boundary.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

#include <iostream.h>          

// "C" stuff
extern "C" {
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "tiff.h"     // Tiff headers for type definitions
                      // like int32 etc.
}

#define TIFF_IO       // turns on Tiff in/out library
#define NITF_IO       // turns on Nitf in/out library

#define SHOWINFO 1    // flag for showing detail info
#define MSG_LEN 80    // length of message string
#define UINT32_MAX 456976

#define TIFF_ERR -1   // error flag

/* --------- begin Tiff & related classes -----------------------*/

//------------------------------------------------------------------

#ifndef TIFF_HPP   //  start header
/** A collection of Tiff-like tags for images.
  Used as a service class to Tiff objects only!
  Note: short = uint16
 */
class TiffTag
{         
  // grant access of private data to friends
public:
  int x, y;                       // upper-left corner location in pixel space
  uint32 imagewidth, imagelength;  // number of pixels and rows
  uint16 planarconfig;         // planar config. flag
  uint16 samplesperpixel;      // for planar config. = separate 
  uint16 bitspersample;
  uint16 compression;
  uint16 photometric;          // Photometric Interpretation
  uint16 orientation;

  uint32 stripoffsets;
  uint32 rowsperstrip;
  uint32 stripbytecounts;

  float xresolution;
  float yresolution;
  uint16 resolutionunit;

  TiffTag(void);
  void show(void);
};
#endif   // end header

#ifndef HEADER_ONLY   //  method implementation

TiffTag::TiffTag(void)    // default constructor as initializer
{
  // Default for greyscale images
  x = y = 0;
  imagewidth = 1;
  imagelength = 1; 
  planarconfig = PLANARCONFIG_CONTIG;
  samplesperpixel = 3;
  bitspersample = 8;
  compression = 1;
  photometric = PHOTOMETRIC_RGB;
  orientation = 1;
  stripoffsets = 8;
  rowsperstrip = imagelength;
  stripbytecounts = 1;
  xresolution = 1200.0F;
  yresolution = 1200.0F;
  resolutionunit = 2;
}

void TiffTag::show(void)
{
  cout << "\tupper-left corner location = " << x << ", " << y << '\n';
  cout << "\timagewidth * imagelength = " 
       << imagewidth << " * " << imagelength << '\n';
  cout << "\tplanarconfig = " << planarconfig << '\n';
  cout << "\tsamplesperpixel = " << samplesperpixel << '\n';
  cout << "\tbitspersample = " << bitspersample << '\n';
  cout << "\tcompression = " << compression << '\n';
  cout << "\tphotometric = " << photometric << '\n';
  cout << "\torientation = " << orientation << '\n';
  cout << "\tstripoffsets = " << stripoffsets << '\n';
  cout << "\trowsperstrip = " << rowsperstrip << '\n';

  cout << "\txresolution = " << xresolution << '\n';
  cout << "\tyresolution = " << yresolution << '\n';
  cout << "\tresolutionunit = " << resolutionunit << '\n';

}

#endif      //  method implementation ends

#ifndef TIFF_HPP   //  start header
/** An image class with Tiff-like tags */
class Tiff {  
protected:
  unsigned char **buf;      /* buffer: pointer to 2D array containing 
                               entire image data (for color: RGBRGB...) */
  int32 scanlinesize;       /* byte size of each scan line */

  TiffTag t;                // object for image file tags 

  // things related to y, z buffers
  uint8 **y;                // pointer to luminance (Y) array 
  uint8 **z;                // pointer to z array (for analysis)

  // yz buffer state : {no yz buffers created, old contents in buffers, buffers up-to-date}
  enum {NO_YZ, OLD_YZ, OK_YZ} yzstate;
  void setyz(void);          // create and set yz buffers
  void renewyz(void);        // renew yz buffers
  void killyz(void);         // kill yz buffers
  void killyz(uint32 startrow);  // kill yz buffers from startrow

  void choplength(uint32 startrow); // chop image memory from startrow

  int fullscan(Tiff&, int32 position[2]);
  int radarscan(Tiff&, int32 position[2]);

  virtual int read(char *filename);         // Read file and construct object.
    // Note that read is protected since it is meant to be
    // called by an constructor (this or derived classes).

#ifdef TIFF_IO
  int readtif(char *filename);      // read tif file, allocate memory
  int writetif(char *filename);     // write tif file
#else
  int readtif(char *filename)      // dummy
    { error( "Not implemented", "Tiff::readtif"); return -1; }
  int writetif(char *filename)      // dummy
    { error( "Not implemented", "Tiff::writetif"); return -1; }
#endif

#ifdef NITF_IO
  virtual int readnitf(char *filename);    // read nitf file, allocate memory
  virtual int writenitf(char *filename);   // write image to nitf file
#else
  virtual int readnitf(char *filename)       // dummy
    { error( "Not implemented", "Tiff::readnitf"); return -1; }
  virtual int writenitf(char *filename)      // dummy
    { error( "Not implemented", "Tiff::writenitf"); return -1; }
#endif

public:
  Tiff(int width=1, int height=1);  // first constructor
  Tiff(char *filename);             // second constructor
  Tiff(int width, int height, const Tiff&);  // third constructor
  Tiff(const Tiff&);                // copy constructor

  ~Tiff();                          // destructor

  Tiff& operator=(const Tiff&);     // assignment operator
                                    // can convert rgb to b/w !

  Tiff& operator=(const unsigned char *p) // assignment operator
    { this->setBuf(p);  return *this; }

  Tiff& operator^=(const Tiff& b)         // *this xor b
    { this->xor(b);  return *this; }
  
  // Field data access
  int32 scansize(void);           // get scanlinesize
  int getWidth(void) { return (int)t.imagewidth; }  
  int getHeight(void) { return (int)t.imagelength; }
  int getSize(void) { return (int)(t.imagelength*t.imagewidth); }

  int setBuf(const unsigned char *p); // set RGB buffer with byte array
  int setBuf(const int pixels[]);     // set RGB buffer with pixels
  int getBuf(int pixels[]);           // get RGB buffer to pixels
  int getBuf(unsigned char *rgb);     // get RGB buffer to contiguous rgb buffer

  // Unary operations on *this 
  int getbgcolor(uint8 bgcolor[3]); // get background color
  int cropbg(void);                 // crop background frame

  // Binary operations between *this and Tiff&
  int insert(const Tiff&, int32 offset[2], int clip=0);  // insert to *this
  void posdiff(Tiff&, int32 position[3]);      // find difference in position
  int xor(const Tiff& b);                      // xor with b

  int write(char *filename);        // write to file

  // These are public and new Tiff(w,h) must be called first.
  int writergb(char *filename);     // write rgb file
  int readrgb(char *filename);      // read rgb file

  virtual void error(char *message, char *function);  // show error

};
#endif   // end header

#ifndef HEADER_ONLY   // method implementation starts
// ----------------------------------------------------------
// RGB percentage for conversion to luminance
  #define PERCENT(x) (((x)*255)/100)
  #define RED   PERCENT(28)     /* 28% */
  #define GREEN PERCENT(59)     /* 59% */
  #define BLUE  PERCENT(11)     /* 11% */

/** First constructor: creates space for a Tiff image object

   Memory is allocated to hold a width*height image.
   The buffer is initialized to zero.
*/
Tiff::Tiff(int width, int height) 
{
  uint32 j;
  uint32 s[2] = { 0, 0 };

  // then sets the desired ones
  t.imagelength = height;
  t.imagewidth = width;
  t.rowsperstrip = height;

  // computes the correct size
  scanlinesize = scansize();

#if SHOWINFO == 2
   cout << "Tiff 1st constructor:\n";
   t.show();
#endif

  /* If  unsigned char ** buf = new unsigned char *[imagelength]; is used,
     then destructor will core dump since no private:buf is allocated!
     */
  buf = new unsigned char * [t.imagelength];
  for (j=0; j<t.imagelength; j++) {
    buf[j] = new unsigned char[scanlinesize];
    memset (buf[j], 0, scanlinesize);
  }

  setyz();

#if SHOWINFO == 2
  cout << "Tiff 1st constructor: scanlinesize = " << scanlinesize << "\n";
#endif

}


// -------------------------------------------------------------
/** Second (read) constructor: reads a file into an image object.
  The type of image file is determined by the suffix.
*/
Tiff::Tiff(char *filename)
{
  read(filename);
}

// --------------------------------------------------------
/** Third constructor: creates space for a Tiff object

   Memory is allocated to hold a width*height image, which
   has the same property as b.  The buffer is initialized 
   to zero.
*/
Tiff::Tiff(int width, int height, const Tiff& b) 
{
  uint32 j;
  uint32 s[2] = { 0, 0 };

  if (&b != NULL) {
    t = b.t;       // copy tag
  }

  // then sets the desired ones
  t.imagelength = height;
  t.imagewidth = width;
  t.rowsperstrip = height;

  // computes the correct size
  scanlinesize = scansize();

#if SHOWINFO == 2
   cout << "Tiff constructor:\n";
   t.show();
#endif

  buf = new unsigned char * [t.imagelength];
  for (j=0; j<t.imagelength; j++) {
    buf[j] = new unsigned char[scanlinesize];
    memset (buf[j], 0, scanlinesize);
  }

  setyz();

#if SHOWINFO == 2
  cout << "Tiff constructor: scanlinesize = " << scanlinesize << "\n";
#endif

}


// -----------------------------------------------------------------------
/** Copy constructor: creates a Tiff image object from an existing one
*/
Tiff::Tiff(const Tiff& b)
{
  uint32 j;

  scanlinesize = b.scanlinesize;

  t = b.t;       // copy tag

  buf = new unsigned char * [t.imagelength];

  if (t.planarconfig == PLANARCONFIG_CONTIG) {
    for (j=0; j<t.imagelength; j++) {
      buf[j] = new unsigned char[scanlinesize];
      memcpy(buf[j], b.buf[j], scanlinesize);
    }

  } else {
    error("Unsupported planarconfig value", "Tiff copy constructor");
    return;
  }

  setyz();

#if SHOWINFO == 2
  cout << "Tiff copy constructor: scanlinesize = " << scanlinesize << '\n';
#endif

}

// ----------------------------------------------------------------
/** Assignment operator = copy a Tiff image object to an existing one.

     Both rgb and y buffers are copied for color images.
     Will not create new resources nor alter image property 
     of *this.  Will check for size before copying.

     If the source is color and *this is b/w, will copy the
     luminance.
*/
Tiff& Tiff::operator=(const Tiff& b)
{
  uint32 j;

  if (buf == b.buf) return *this;  // do nothing
 
  if ( t.imagelength < b.t.imagelength || 
       t.imagewidth < b.t.imagewidth ) {
    error("Not enough space for assignment","Tiff::=");
    return *this;
  }

  if (t.planarconfig == PLANARCONFIG_CONTIG) {

    if(t.photometric == b.t.photometric) {
      for (j=0; j<b.t.imagelength; j++) {
        memcpy(buf[j], b.buf[j], b.scanlinesize);
        memcpy(y[j], b.y[j], b.t.imagewidth);
      }

    } else if (t.photometric == PHOTOMETRIC_MINISBLACK &&
               b.t.photometric == PHOTOMETRIC_RGB ) {
      // convert from rgb to luminance
      for (j=0; j<b.t.imagelength; j++)
        memcpy(buf[j], b.y[j], b.t.imagewidth);

    } else {

      error("Unsupported photometric conversion", "Tiff::=");
      return *this;
      }
        
  } else {
    error("Unsupported planarconfig value", "Tiff::=");
    return *this;
  }

#if SHOWINFO >= 2
  cout << "Tiff = operator: scanlinesize = " << scanlinesize << '\n';
#endif

  return *this;
}

// ----------------------------------------------------------------
/** Destructor: destroys a Tiff image object
*/
Tiff::~Tiff()
{
  uint32 j;
  for (j=0; j < t.imagelength; j++) delete [] buf[j];
  delete [] buf;
  killyz();
}

/** Chop off allocated memory from start row.  Reset imagelength
  to startrow. */
void Tiff::choplength(uint32 startrow) {
  uint32 j;
  for (j=startrow; j < t.imagelength; j++) delete [] buf[j];
  killyz(startrow);

  t.imagelength = startrow;
}

// ----------------------------------------------------------------
/** setBuf = copy values from a contiguous byte area to buf.
    Tags should have been set.  Buffer y is renewed.
*/
int Tiff::setBuf(const unsigned char *p)
{
  int32 i,j;

  if (t.planarconfig == PLANARCONFIG_CONTIG) {

    for (i=j=0; j<(int32)t.imagelength; i += scanlinesize,  j++) {
      memcpy(buf[j], p+i, scanlinesize);
    }
        
  } else {
    error("Unsupported planarconfig value", "Tiff::=");
    return -1;
  }

#if SHOWINFO >= 2
  cout << "Tiff::setBuf: scanlinesize = " << scanlinesize << '\n';
#endif

  renewyz();
  return 0;
}

// --------------------------------------------------------------------------
/** set RGB buffer values from an int pixel array (which must be 
    contiguous in memory).  Each pixel should be 4 bytes (ARGB).
    Tags should have been set.  Buffer y is renewed.
    */
int Tiff::setBuf(const int pixels[])
{
  int32 i,j;
  int p;

#if SHOWINFO >= 2
  cout << "Tiff::setBuf: scanlinesize = " << scanlinesize << '\n';
#endif

  int32 k=0; unsigned char *c;
  if (t.planarconfig == PLANARCONFIG_CONTIG) {

    for (j=0; j < (int32)t.imagelength; j++) {
      c = buf[j];
      for (i=0; i < (int32)t.imagewidth; i++) {
        p = pixels[k];
        *c = (unsigned char)( p >> 16 ); c++;
        *c = (unsigned char)( p >> 8 ); c++;
        *c = (unsigned char)( p ); c++;
        k++;
      }
    }

  } else {
    error("Unsupported planarconfig value", "Tiff::setBuf");
    return -1;
  }

#if SHOWINFO >= 2
  cout << "Tiff::setBuf: scanlinesize = " << scanlinesize << '\n';
#endif

  renewyz();
  return 0;
}


// ----------------------------------------------------------------
/** get RGB buffer values and send to the pixel array (which must be 
    contiguous in memory).  Each pixel should be 4 bytes (ARGB).
    */
int Tiff::getBuf(int pixels[])
{
  int32 i,j;
  int p;

#if SHOWINFO >= 2
  cout << "Tiff::getBuf: scanlinesize = " << scanlinesize << '\n';
#endif

  int32 k=0;  unsigned char *c;
  if (t.planarconfig == PLANARCONFIG_CONTIG) {

    // Transform rgb to 16 bit pixels
    // 0xFF000000 gives a nontransparent alpha value
    int r,g,b;
    for (j=0; j < (int32)t.imagelength; j++) {
      c = buf[j];
      for (i=0; i < (int32)t.imagewidth; i++) {
        r = *c++; g = *c++; b = *c++;
        pixels[k] = 0xFF000000 | ( r << 16 ) | ( g << 8 ) | ( b );
        k++;
      }
    }

/* Use memcpy to stuff 3 rgb bytes to pixel - not working
  int pix;
  unsigned char *c;
  c = rgb;
  for (i=0; i < len; i++) {
    if ( memcpy(&pix, c, 3) != &pix ) {
      printf ("ImagePort.c: readRGB memcpy error\n");
      exit(-1);
    }
    pixels[i] = 0xFF000000 | ( pix >> 8 );
    c += 3;
  }
 */


  } else {
    error("Unsupported planarconfig value", "Tiff::getBuf");
    return -1;
  }

#if SHOWINFO >= 2
  cout << "Tiff::getBuf: scanlinesize = " << scanlinesize << '\n';
#endif

  return 0;
}

// ----------------------------------------------------------------
/** copy buf to a contiguous rgb buffer.
    Tags should have been set.
*/

int Tiff::getBuf(unsigned char *rgb)
{
  int32 i,j;

  if (t.planarconfig == PLANARCONFIG_CONTIG) {
    // do mem copy line by line
    for (i=j=0; j<(int32)t.imagelength; i += scanlinesize,  j++) {
      memcpy(rgb + i, buf[j], scanlinesize);
    }
        
  } else {
    error("Unsupported planarconfig value", "Tiff::getBuf");
    return -1;

  }

#if SHOWINFO >= 2
  cout << "Tiff::getBuf: scanlinesize = " << scanlinesize << '\n';
#endif

  return 0;
}

// ----------------------- Driver for file i/o --------------------------------

/** Read an image object from a file and construct the Tiff object. */
int Tiff::read(char *filename)
{
  if ( filename[0]=='\0' ) {   // simply returns 
    error("Null filename", "Tiff::read");
    return -1;
  }

  // search for . from end
  int i;
  for (i=strlen(filename); i>0; i--)
    if ( filename[i] == '.' ) break;
  char *suf = filename + i + 1;

  if (!strcmp(suf, "rgb")) {
    error("Constructor with raw rgb file not allowed", "Tiff::read");
    return 0;
  }

  if (!strcmp(suf, "tif")) {
    if ( readtif(filename) < 0 ) {
      error("Error from readtif", "Tiff::read");
      return 0;
    }
  } else if (!strcmp(suf, "ntf") || !strcmp(suf, "nitf")) {
    if ( readnitf(filename) < 0 ) {
      error("Error from readnitf", "Tiff::read");
      return 0;
    }
  } else {
    error("Unidentified format suffix", "Tiff::read");
  }
  return 0;
}

/** Writes an image object to a file.

   Tiff is the default form.  Other forms are identified by the
   filename suffix.
*/
int Tiff::write(char *filename)
{
  // search for . from end
  int i;
  for (i=strlen(filename); i>0; i--)
    if ( filename[i] == '.' ) break;
  char *suf = filename + i + 1;

  if (!strcmp(suf, "rgb")) return writergb(filename);
  if (!strcmp(suf, "ntf")) return writenitf(filename);
  if (!strcmp(suf, "nitf")) return writenitf(filename);

  if ( strcmp(suf, "tif")) {
    error("Unidentified format suffix", "Tiff::write");
  }
  return writetif(filename);

}

// ------------------- methods using Tiff in/out library ------------------------
#ifdef TIFF_IO

extern "C" {
#include "tiffio.h"   // Tiff in/out library
}

/** writes an image object to a tiff file.
*/
int Tiff::writetif(char *filename)
{
  TIFF *tif = TIFFOpen(filename, "w");       // file handle

  if (tif == NULL) {
    char msg[MSG_LEN] = "Can't open ";
    strcat (msg, filename);
    error(msg, "Tiff::writetif");
    return (-2);
  }

  uint32 row;

  // set image tags
  TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, t.imagewidth);
  TIFFSetField(tif, TIFFTAG_IMAGELENGTH, t.imagelength);
  TIFFSetField(tif, TIFFTAG_PLANARCONFIG, t.planarconfig);
  TIFFSetField(tif, TIFFTAG_SAMPLESPERPIXEL, t.samplesperpixel);
  TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, t.bitspersample);
  TIFFSetField(tif, TIFFTAG_COMPRESSION, t.compression);
  TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, t.photometric);
  TIFFSetField(tif, TIFFTAG_ORIENTATION, t.orientation);
  TIFFSetField(tif, TIFFTAG_STRIPOFFSETS, t.stripoffsets);
  TIFFSetField(tif, TIFFTAG_ROWSPERSTRIP, t.rowsperstrip);
  TIFFSetField(tif, TIFFTAG_XRESOLUTION, t.xresolution);
  TIFFSetField(tif, TIFFTAG_YRESOLUTION, t.yresolution);
  TIFFSetField(tif, TIFFTAG_RESOLUTIONUNIT, t.resolutionunit);

  if (t.planarconfig == PLANARCONFIG_CONTIG) {
    for (row = 0; row < t.imagelength; row++)
      TIFFWriteScanline(tif, buf[row], row);

  } else if (t.planarconfig == PLANARCONFIG_SEPARATE) {
    uint16 s;
      
    for (s = 0; s < t.samplesperpixel; s++)
      for (row = 0; row < t.imagelength; row++)
        TIFFWriteScanline(tif, buf[row], row, s);
    
  } else {
    error("Wrong planarconfig value", "Tiff::writetif");
    return (-1);
  }

  TIFFClose(tif);
  return 0;
}

/** reads an image object from a tiff file.  Also allocates memory.
  Return 0 normal; -1 error;
*/
int Tiff::readtif(char *filename)
{
  TIFF *tif = TIFFOpen(filename, "r");       // file handle

  if (tif == NULL) {
    char msg[MSG_LEN] = "Can't open ";
    strcat(msg, filename);
    error(msg, "Tiff::readtif");
    exit(1);
  }

  // get image tags
  TIFFGetField(tif, TIFFTAG_IMAGEWIDTH, &t.imagewidth);
  TIFFGetField(tif, TIFFTAG_IMAGELENGTH, &t.imagelength);
  TIFFGetField(tif, TIFFTAG_PLANARCONFIG, &t.planarconfig);
  TIFFGetField(tif, TIFFTAG_SAMPLESPERPIXEL, &t.samplesperpixel);
  TIFFGetField(tif, TIFFTAG_BITSPERSAMPLE, &t.bitspersample);
  TIFFGetField(tif, TIFFTAG_COMPRESSION, &t.compression);
  TIFFGetField(tif, TIFFTAG_PHOTOMETRIC, &t.photometric);
  TIFFGetField(tif, TIFFTAG_ORIENTATION, &t.orientation);
  TIFFGetField(tif, TIFFTAG_STRIPOFFSETS, &t.stripoffsets);
  TIFFGetField(tif, TIFFTAG_ROWSPERSTRIP, &t.rowsperstrip);
  TIFFGetField(tif, TIFFTAG_XRESOLUTION, &t.xresolution);
  TIFFGetField(tif, TIFFTAG_YRESOLUTION, &t.yresolution);
  TIFFGetField(tif, TIFFTAG_RESOLUTIONUNIT, &t.resolutionunit);

  scanlinesize = TIFFScanlineSize(tif);
  
  // allocate buffer
  uint32 row;
  buf = new unsigned char * [t.imagelength];
  for (row = 0; row < t.imagelength; row++)
    buf[row] = new unsigned char[scanlinesize];

#if SHOWINFO == 2
  cout << filename << ":\n";
  t.show();
#endif
  
  if (t.planarconfig == PLANARCONFIG_CONTIG) {
    for (row = 0; row < t.imagelength; row++)
      if (TIFFReadScanline(tif, buf[row], row) == TIFF_ERR) {
        error("TIFFReadScanline call", "Tiff:readtif");
        return -1;
      }

  } else if (t.planarconfig == PLANARCONFIG_SEPARATE) {
    uint16 s;
    for (s = 0; s < t.samplesperpixel; s++)
      for (row = 0; row < t.imagelength; row++)
        if (TIFFReadScanline(tif, buf[row], row, s) == TIFF_ERR) {
          error("TIFFReadScanline call", "Tiff:readtif");
          return -1;
        }
    
  } else {
    error("Wrong planarconfig value", "Tiff:readtif");
    return -1;
  }
  
  TIFFClose(tif);
  setyz();
  return 0;

}

#endif
// ------------------- end of methods using Tiff in/out library ----------

// --------------------starts methods for raw rgb file --------------------
/** Writes an image object to a raw rgb file
*/
int Tiff::writergb(char *filename)
{
  FILE *fp = fopen(filename, "wb");       // file handle

  if (fp == NULL) {
    char msg[MSG_LEN] = "Can't open ";
    strcat (msg, filename);
    error(msg, "Tiff::writergb");
    return (-2);
  }

  uint32 row;

  if (t.planarconfig == PLANARCONFIG_CONTIG) {
    for (row = 0; row < t.imagelength; row++)
      fwrite(buf[row], scanlinesize, 1, fp);

  } else {
    error("Unspported planarconfig option", "Tiff::writergb");
    return (-1);
  }

  fclose(fp);
  return 0;
}

/** reads an image object from a raw rgb file
*/
int Tiff::readrgb(char *filename)
{
  if ( t.imagelength<=1 || t.imagewidth<=1 ) {
    error("Width-Height not set", "Tiff::readrgb");
    return -1;
  }

  FILE *fp = fopen(filename, "rb");       // file handle

  if (fp == NULL) {
    char msg[MSG_LEN] = "Can't open ";
    strcat (msg, filename);
    error(msg, "Tiff::readrgb");
    return (-2);
  }

  uint32 row;
  for (row = 0; row < t.imagelength; row++)
    fread(buf[row], scanlinesize, 1, fp);

  fclose(fp);
  return 0;
}
// -------------------- ends methods for raw rgb file --------------------

// --------------------starts methods for nitf file --------------------
#ifdef NITF_IO

// #define TEST_READNITF        

extern "C" {
#include "nitfserv.h"
#include "nsdefault.h"
#include "CRRN.h"
}

/** reads a nitf file.  Also allocates memory.
  Return 0 normal; -1 error;
*/
int Tiff::readnitf(char *filename)
{
   char *method = "Tiff::readnitf";

   NS_MSGDBS *msgdb = NS_open_msgdbs();    /* initialize the msg db */
   ns_unpack(filename, msgdb);     /* Unpacks an NITF file into database */

   NS_DHNDL *data_hdl = NS_dhndl_alloc();
   NS_SHDR4IMG *img_hdr = NS_image_alloc();
   NS_SHDR4SYM *sym_hdr = NS_sym_alloc();
   NS_NLE *nle = (NS_NLE *) malloc(sizeof(NS_NLE));

   // Get an image
   if ( NS_firstNLE("IM", nle, msgdb) == FAILURE ) {
     error("NS_firstNLE", method);
     return (-1);
   }

   ns_dbs2img(img_hdr, data_hdl, nle->dlvl, msgdb); /* get image header */

   t.imagewidth = (long) ns_get_long((char *)img_hdr->source.source20.ncols,8);
   t.imagelength = (long) ns_get_long((char *)img_hdr->source.source20.nrows,8);
   t.x = ns_get_int((char *)img_hdr->iloc,5);
   t.y = ns_get_int((char *)&img_hdr->iloc[5],5);

   char irep[9];
   memcpy(irep, img_hdr->source.source20.irep, 8);
   irep[8]='\0';
   //   printf("image representation = %s\n", irep);
   if (strncmp(irep, "RGB", 3) != 0) error("Unsupported image representation", method);

   int abpp = ns_get_int((char *)img_hdr->source.source20.abpp, 2);
   //   printf ("actual bits per pixel per band = %d\n", abpp);
   if (abpp != 8) error("Unsupported bits per pixel", method);

   char ic[3];
   memcpy(ic, img_hdr->ic, 2);
   ic[2]='\0';
   // printf("image compression = %s\n", ic);
   if (strcmp(ic, "NC") != 0) error("Unsupported image compression", method);

   // computes the correct size
   scanlinesize = scansize();
   
#ifdef TEST_READNITF    
   cout << "Tiff::readnitf - " << "\n";
   t.show();
   cout << "scanlinesize = " << scanlinesize << "\n";
   cout << "date byte size = " << data_hdl->datasize << "\n";
#endif

   // allocate buffer
   uint32 i,j;
   buf = new unsigned char * [t.imagelength];
   for (j = 0; j < t.imagelength; j++)
     buf[j] = new unsigned char[scanlinesize];

   // do mem copy line by line
   unsigned char *c = (unsigned char *) data_hdl->data;
   for (i=j=0; j < t.imagelength; i += scanlinesize,  j++) {
     memcpy(buf[j], c + i, scanlinesize);
   }

   setyz();

   /* free memory */
   free(nle);
   NS_image_free(img_hdr);
   NS_sym_free(sym_hdr);
   NS_dhndl_free(data_hdl);

   /* Clean up dbs and temp files */
   NS_close_msgdbs ( msgdb );

   return 0;
}

#define NSERROR_OPEN_DB         -1      /* error opening message database */
#define NSERROR_HDR             -2      /* error allocating a message header */
#define NSERROR_SHDR            -3      /* error allocating image subheader */
#define NSERROR_SAVEIMG         -4      /* error saving image to database */
#define NSERROR_COMPRESS        -5      /* error compressing the image */
#define NSERROR_GENFILE         -6      /* could not generate NITF file from database */

/** Writes an image object to a NITF file.  See the define's
 for return code. 
*/
int Tiff::writenitf(char *filename) {
  char *method = "writenitf";

  /* Nitf properties */
  char *compression = (char *) 0;
  int level = 1;
  int len3 = t.imagelength * t.imagewidth * 3;

  unsigned char *rgb = (unsigned char *) malloc(len3);
  getBuf( rgb );

  int height = t.imagelength;
  int width = t.imagewidth;

  // starts sending rgb to nitf 
  NS_MSGDBS *msgdb;
  NS_HDR4MSG *hdr;
  NS_SHDR4IMG *img_hdr;
  NS_DHNDL data_handle;
  char ldt[11];
  char rows[9];
  char cols[9];
  int ret_code = 0;
  int rc;

  msgdb = NS_open_msgdbs();     /* initialize the msg db */
  if ( msgdb == (NS_MSGDBS *)0) return (NSERROR_OPEN_DB);

  hdr = NS_hdr_alloc(); /* allocates message header */
  if ( hdr == (NS_HDR4MSG *)0 ) return (NSERROR_HDR);

  NS_SetA_MSG_numi ( hdr, "1" );  /* set # of images in msg */
  ns_hdr2dbs ( hdr, msgdb );    /* store the header in the db */

  img_hdr = NS_image_alloc();     /* Create the image header */
  if ( img_hdr == (NS_SHDR4IMG *)0 ) return ( NSERROR_SHDR );

  /* set the data length */
  sprintf ( ldt, "%10.10d", len3 );     
  NS_SetA_IMG_ldt ( img_hdr, ldt );

  /* Must set nppbh/nppbv, or the image may be chopped to 512 x 512! */
  sprintf ( cols, "%8.8d", width );             
  NS_SetA_IMG_ncols ( img_hdr, cols );   /* set width */
  memcpy ( img_hdr->nppbh, &cols[4], 4); /* number of pixels/block horizontally */

  sprintf ( rows, "%8.8d", height );            
  NS_SetA_IMG_nrows ( img_hdr, rows );    /* set height */
  memcpy ( img_hdr->nppbv, &rows[4], 4);        /* number of pixels/block vertically */

  NS_SetA_IMG_irep ( img_hdr, "RGB     " );  /* set image representation */
  memcpy ( img_hdr->imode, "P", 1 );
  memcpy ( img_hdr->nbpp, "08", 2);     
  memcpy ( img_hdr->source.source20.abpp,"08",2);

  NS_SetA_IMG_nbands ( img_hdr, "3" );  /* set image bands */
  img_hdr->bandinfo[0] = NS_band_alloc();
  memcpy ( img_hdr->bandinfo[0]->bandinfo.bi20.irepband, "R ", 2 );
  img_hdr->bandinfo[1] = NS_band_alloc();
  memcpy ( img_hdr->bandinfo[1]->bandinfo.bi20.irepband, "G ", 2 );
  img_hdr->bandinfo[2] = NS_band_alloc();
  memcpy ( img_hdr->bandinfo[2]->bandinfo.bi20.irepband, "B ", 2 );

  memcpy(img_hdr->iid,"Tiff Class",10);

  /* This setting reads the data and writes it to the database.  rgb
     has priority.  If it is not null, then filename is set to null. */
  data_handle.data = (void *)rgb;
  data_handle.filename = NULL;
  data_handle.datasize = len3;
  data_handle.masked = 0;

  rc = ns_img2dbs ( img_hdr, &data_handle, msgdb );  /* save the image data */
  if ( rc != 0 ) return ( NSERROR_SAVEIMG );

  /*  Compress the data and generate an NITF file       */
  if ( compression != NULL ) {
          rc = NS_Compress_All ( msgdb, compression, level );  /* compress the image */
          ret_code = NSERROR_COMPRESS;
  }

  rc = ns_pack ( filename, msgdb );   /* generate an NITF file from database */
  if ( rc != 0 ) ret_code = NSERROR_GENFILE;

  /* Clean up */
  NS_hdr_free ( hdr );
  NS_close_msgdbs ( msgdb );

  printf ( "Tiff::writenitf: return code =%d \n", rc );
  return rc;
}
#endif
// -------------------- ends methods for nitf file --------------------


/* macro to return the smaller/larger one of two values */
#define MIN2(x, y)  (((x) < (y)) ? (x) : (y))
#define MAX2(x, y)  (((x) > (y)) ? (x) : (y))

// --------------------------------------------------------------------
/** Inserts an image object to the current one.  

  If the image space is not large enough, the image is automatically
  expanded.  Memory is handled dynamically and the size of the image
  is changed accordingly.

 Input:  p          reference to an image to be inserted
         offset[2]  (row,col) offset of upperleft corner of p.
         clip       >0 turns on edge clipping when copying.
                    The touching edge of p with *this (with a number 
                    of "clip" pixels) will not be copied to *this.

 Output: updated image object 

 Return: 0         normal
         -1        errenous info in *this object
         -2        bad memory copy
*/
int Tiff::insert(const Tiff& p, int32 offset[2], int clip)
{
  uint32 i;  // row count for inserting image p
  uint32 j;  // row count for *this image

  int32 sx, sy;
  int sx1;
  int32 linewid;
  static int32 offsetx, offsety;  // values saved for next call

  // set flag for yz buffers
  yzstate = OLD_YZ;

  sx = offset[0];  sy = offset[1];

  // determines clipped rows and columns based on original offest
  int lclip=0, rclip=0, tclip=0, bclip=0;

  if (clip > 0) {
    if ( offset[0] == offsetx ) {  // no relative shift from last time
      lclip = rclip = 0;
    } else if ( offset[0] > offsetx ) {
      lclip = MIN2(clip, p.t.imagewidth/10);  rclip = 0;
    } else {
      rclip = MIN2(clip, p.t.imagewidth/10);  lclip = 0;
    }
    if ( offset[1] == offsety ) {
      tclip = bclip = 0;
    } else if ( offset[1] > offsety ) {
      tclip = MIN2(clip, p.t.imagelength/10);  bclip = 0;
    } else {
      bclip = MIN2(clip, p.t.imagelength/10);  tclip = 0;
    }

#if SHOWINFO >= 2
  printf(" clip = %d %d %d %d\n", lclip, rclip, tclip, bclip);
#endif

  }

    // variables for expansion
    int32 delx, dely;
    int32 sxe, sye;
    uint32 newwidth, newlength, newscansize;
    unsigned char ** newbuf;

  int xflag = 0;  // indicates whether expansion is needed
  if ( sx < 0 ) {
    xflag = -1;
    delx = -sx;
  } else if ( p.t.imagewidth + sx  > t.imagewidth ) {
    xflag = 1;
    delx = p.t.imagewidth + sx - t.imagewidth;
  }

  int yflag = 0;  // indicates whether expansion is needed
  if ( sy < 0 ) {
    yflag = -1;
    dely = -sy;
  } else if ( p.t.imagelength + sy > t.imagelength ) {
    yflag = 1;
    dely = p.t.imagelength + sy - t.imagelength;
  }

  // Expand image memory
  if ( xflag != 0 || yflag != 0 ) {

    error("Not enough space ... will expand memory", "Tiff::insert");

    // set up new buffer
    if (xflag != 0) delx = MAX2( (int32)( t.imagewidth * 0.3 ), 2*delx);
    else delx =0;
    if (yflag != 0) dely = MAX2( (int32)( t.imagelength * 0.3), 2*dely);
    else dely =0;
    newwidth = t.imagewidth + delx;
    newlength = t.imagelength + dely;
    newscansize = newwidth * t.samplesperpixel;
    newbuf = new unsigned char * [newlength];
    for (j=0; j<newlength; j++) {
      newbuf[j] = new unsigned char[newscansize];
      memset (newbuf[j], 0, newscansize);
    }

    // copy buf to newbuf
    if (xflag >=0 ) sxe = 0;
    else sxe = delx;
    if (yflag >=0 ) sye = 0;
    else sye = dely;

    // update shift parameters
    sx = sxe + offset[0];  sy = sye + offset[1];

    if (t.planarconfig == PLANARCONFIG_CONTIG) {

      // byte shift in x.  Works for RGBRGB...
      sxe *= t.samplesperpixel;
      for (i=0, j=sye; i<t.imagelength; i++, j++) {
        if ( memcpy( &newbuf[j][sxe], &buf[i][0], scanlinesize )
             != &newbuf[j][sxe] ) {
        error("Bad memcpy", "Tiff::insert / expansion");  return (-2);
        }
      }

    } else {
      error("Unsupported planarconfig value", "Tiff::insert");
      return (-1);
    }

    // delete old buf
    {
      for (j=0; j < t.imagelength; j++) delete [] buf[j];
      delete [] buf;
      killyz();
    }

    // set tags to new image and let buf point to newbuf
    {
      t.imagewidth = newwidth;
      t.imagelength = newlength;
      t.rowsperstrip = newlength;
      scanlinesize = scansize();

      buf = newbuf;
      for (j=0; j < t.imagelength; j++) {
        buf[j] = newbuf[j];
      }
      newbuf = NULL;

      setyz();
    }

    offset[0] = sx;        offset[1] = sy;

    printf("...image expanded to: %d x %d\n", newwidth, newlength);

#if SHOWINFO >= 2
    cout << "Expanded image:\n";
    t.show();
#endif

  }
  // ...end Expand image memory

  if (t.planarconfig == PLANARCONFIG_CONTIG) {

    // copy from p.buf to buf with some edges clipped 

    // byte shift in x.  Works for RGBRGB...
    sx = p.t.samplesperpixel * (sx + lclip);
    sx1 = p.t.samplesperpixel * lclip;
    linewid = p.scanlinesize - p.t.samplesperpixel * (lclip + rclip);

    for (i=tclip, j=sy+tclip; i<p.t.imagelength - bclip; i++, j++) {
      if ( memcpy( &buf[j][sx], &p.buf[i][sx1], linewid )
           != &buf[j][sx] ) {
      error("Bad memcpy", "Tiff::insert");  return (-2);
      }
    }

  } else {
    error("Unsupported planarconfig value", "Tiff::insert");
    return (-1);
  }

  // save for next entry
  offsetx = offset[0];
  offsety = offset[1];

  return 0;
}

// MAD function for a NxN block stencil
// For use by posdiff
//
// i0, j0 is the start pixel. (sx,sy) the shift.
#define NBLOCK 16    // for NxN blcok
#define MAD0   400   // target mad value for matching
#define MAD(sx, sy) \
    for (mad=0, j=j0; j<j0+NBLOCK; j++) \
      for (i=i0; i<i0+NBLOCK; i++) \
        mad += abs( y[j][i] - b.y[j-(sy)][i-(sx)] ); \
    count++;
// end MAD function definition


// --------------------------------------------------------------------
/** Find the difference in position (b relative to *this)

   Usage:   a.posdiff(Tiff& b, int32 s[3]);

   Input:   b      the image to be positioned with *this as
                   the reference
            s      s[0], s[1] are the initial guess for the shifts
                   (unit = pixel)
   Output:  s      s[0], s[1] are the x, y shifts

                   s[3] is the angle of rotation 
                   (not implemented yet)
   Algorithm:

     The relative position is found by a stencil matching procedure.
     That is, by minimizing the mean absolute difference (MAD) between a 
     sampling stencil of the luminance arrays.  Note that the stencil 
     moves in -s[i] direction on the reference image a.  Thus the
     minus sign for sx and sy inside the stencil calculation.

*/
void Tiff::posdiff(Tiff& b, int32 s[3])
{
  uint32 i0, j0, i, j;
  int32 ic, jc;
  uint32 mad;       // mean absolute difference
  uint32 mad0, mads[3][3];
  int32 sxx, syy;

  fullscan(b, s);
  // radarscan(b, s);

  /* Sparse search to find rough direction of motion 
  sxx = i0 / 4;
  syy = j0 / 4;

  for (jc=0; jc<3; jc++) {
    for (ic=0; ic<3; ic++) {
      MAD((ic-1)*sxx, (jc-1)*syy);
      mads[jc][ic] = mad;
      cout << " mad = " << mad;
    }
  }
  */

}


// ------------------------------------------------------------------------
/** Full scan for min MAD within an area.

   The scan is within (-ICMAX, ICMAX) of the shift s[0].
   Likewise for j.  If a satisfactory MAD is reached, the 
   scan stops.

   Input:   b      the image to be positioned with *this as
                   the reference
            s      s[0], s[1] are the initial guess for the shifts
                   (unit = pixel)

   Output:  s      s[0], s[1] are the optimized x, y shifts

   Return:  0      normal
            -1     index out of bound

*/
int Tiff::fullscan(Tiff& b, int32 s[2]) 
{
  #define ICMAX 10      // defines half the scan range in pixel unit
  #define JCMAX 10
  #define TEST_FULLSCAN 1    // turns on data output for MAD

  uint32 i0, j0, i, j;
  int32 ic, jc;
  uint32 mad;    // mean absolute difference (MAD) between the image pixels
  uint32 mad0;
  int32 sx, sy, sxx, syy;
  int count=0;   // counter for calls to MAD

  i0 = t.imagewidth * 2 / 5;     // upper left corner of block or stencil
  j0 = t.imagelength * 2 / 5;

  mad0 = UINT32_MAX;

  // sum of absolute gradient sum in stencil
  {
    static const int MINABSGRAD = 3 * NBLOCK * NBLOCK;  // an empirical number
    uint32 absgrad = 0;
    for (j=j0; j<j0+NBLOCK; j++)
      for (i=i0; i<i0+NBLOCK; i++)
        absgrad += abs( y[j][i] - y[j][i-1] ) + abs( y[j][i] - y[j-1][i] );
    printf("absgrad = %d\n", absgrad);
    if (absgrad > 0 && absgrad < MINABSGRAD) 
      error(">>> stencil maybe in featureless area", "Tiff::fullscan");
  }

#if TEST_FULLSCAN >= 2
  cout << "Tiff::fullscan -\n";
  for (ic=-ICMAX; ic<ICMAX; ic++) cout << ic << ' ';
  cout << '\n';
  for (jc=JCMAX-1; jc>=-ICMAX; jc--) printf("%d ",jc);
  cout << '\n';
#endif

  // check index range first     
  if (j0-abs(s[1])-JCMAX <0 || j0+NBLOCK+abs(s[1])+JCMAX > t.imagelength ||
      i0-abs(s[0])-ICMAX <0 || i0+NBLOCK+abs(s[0])+ICMAX > t.imagewidth ) {
    error("Index out of bound", "Tiff::fullscan");
    return (-1);
  }

  for (jc=-JCMAX; jc<JCMAX; jc++) {
    sy = s[1] + jc;
    for (ic=-ICMAX; ic<ICMAX; ic++) {
      sx = s[0] + ic;

      // Calculation of MAD for a block stencil of nxn pixels

      MAD(sx,sy);

      /* --------------------------------------------
         A sparse block with alternating pixels.  
         Produces more jumpy mad contours.  No good.
      mad=0;
      for (j=j0; j<j0+NBLOCK; j++)
        for (i= i0 + j % 2; i<i0+NBLOCK; i+=2)
          mad += abs( y[j][i] - b.y[j-sy][i-sx] );
         -------------------------------------------- */

#if TEST_FULLSCAN >= 2
      printf("%5d ", mad);
#endif

      if (mad < mad0) {   // full search of min mad
        mad0 = mad;
        sxx = sx;  syy = sy;
        if ( mad0 <= MAD0 ) goto done_full;
      }

    }
#if TEST_FULLSCAN >= 2
    cout << '\n';
#endif
  }

done_full:
  s[0] = sxx;  s[1] = syy;

#if TEST_FULLSCAN >= 1
  cout << "min_mad =" << mad0 << '\n';
  printf("function_count = %d\n", count);
  printf("sx,sy = %d %d\n", s[0], s[1]);
#endif

  return (0);
}


// --------------------------------------------------------------------
/** Does radar scan for min MAD from the guess values.

   The scan is within [-NMAX, +NMAX] of the shift s[0], s[1].

   Input:   b      the image to be positioned with *this as
                   the reference
            s      s[0], s[1] are the initial guess for the shifts
                   (unit = pixel)

   Output:  s      s[0], s[1] are the optimized x, y shifts

   Return:  0      normal
            -1     index out of bound

   Algorithm:   The scan order is shown below.
                 , , , , .
                 * 4 3 2 .
                 * 5 + 1 .
                 * 6 7 8 .
                 * - - - -=>
                If a satisfactory MAD is reached, the scan stops.

*/
int Tiff::radarscan(Tiff& b, int32 s[2]) 
{
  #define TEST_RADARSCAN 1   // turns on info output
  #define NMAX 10            // defines half the scan range in pixel unit
  #define MAD1(sx,sy) \
     MAD((sx),(sy)); \
     if (mad < mad0) { \
        mad0 = mad; sxx = (sx);  syy = (sy); \
        if ( mad0 <= MAD0 ) goto done_radar; \
     }

  uint32 i0, j0, i, j;
  int32 ic, jc;
  uint32 mad;    // mean absolute difference (MAD) between the image pixels
  uint32 mad0;
  int32 sx, sy, sxx, syy;
  int count=0;   // counter for calls to MAD
  int k,n;

  i0 = t.imagewidth / 2;     // upper left corner of block or stencil
  j0 = t.imagelength / 2;

  mad0 = UINT32_MAX;

  // check index range first     
  if (j0-abs(s[1])-NMAX <0 || j0+NBLOCK+abs(s[1])+NMAX > t.imagelength ||
      i0-abs(s[0])-NMAX <0 || i0+NBLOCK+abs(s[0])+NMAX > t.imagewidth ) {
    error("Index out of bound", "Tiff::radarscan");
    return (-1);
  }

  sx = s[0];
  sy = s[1];

  for (n=2; n<=2*NMAX; n+=2) {

  sx++;  sy--;  // go to lower right corner
  for (k=0; k<n; k++) {
    sy++;
    MAD1(sx,sy);
  }

  for (k=0; k<n; k++) {
    sx--;
    MAD1(sx,sy);
  }

  for (k=0; k<n; k++) {
    sy--;
    MAD1(sx,sy);
  }

  for (k=0; k<n; k++) {
    sx++;
    MAD1(sx,sy);
  }

  }

done_radar:
  s[0] = sxx;  s[1] = syy;

#if TEST_RADARSCAN >= 1
  cout << "min_mad =" << mad0 << '\n';
  printf("function_count = %d\n", count);
  printf("sx,sy = %d %d\n", s[0], s[1]);
#endif

  return (0);
}

// ------------------------------------------------------------------
/** xor *this with b.  The yz buffers are renewed too.
  return     0 normal
            -1 b does not have the same size as *this.
*/
int Tiff::xor(const Tiff& b) {
  if ( t.imagelength != b.t.imagelength || t.imagelength != b.t.imagelength ) {
    error("dimension not matching", "Tiff::xor");
    return -1;
  }
  
  int i, j, i1;
  for (j=0; j<t.imagelength; j++) {
    for (i=0, i1=0; i < t.imagewidth; i++) {
      buf[j][i1] ^= b.buf[j][i1];  i1++;
      buf[j][i1] ^= b.buf[j][i1];  i1++;
      buf[j][i1] ^= b.buf[j][i1];  i1++;
    }
  }

  renewyz();
  return 0;
}

// ------------------------------------------------------------------
/** get background color

  A full line of identical luminance on the edge determines 
  the background color.

  return     0 normal
            -1 no background found

*/
int Tiff::getbgcolor(uint8 bgcolor[3])
{
  uint32 i,j, ii, jj;
  uint8 yy;

  bgcolor[0] = bgcolor[1] = bgcolor[2] = 0;

  if( yzstate == OLD_YZ ) renewyz();

  j = 0;
  ii = 0; jj = j;
  yy=y[jj][ii];
  for (i=1; i < t.imagewidth; i++)
    if (yy != y[j][i]) goto break1;
  goto found;

break1:
  j = t.imagelength - 1; 
  ii = 0; jj = j;
  yy=y[jj][ii];
  for (i=1; i < t.imagewidth; i++)
    if (yy != y[j][i]) goto break2;
  goto found;

break2:
  i = 0;
  ii = i; jj = 0;
  yy=y[jj][ii];
  for (j=1; j < t.imagelength; j++)
    if (yy != y[j][i]) goto break3;
  goto found;

break3:
  i = t.imagewidth - 1;
  ii = i; jj = 0;
  yy=y[jj][ii];
  for (j=1; j < t.imagelength; j++)
    if (yy != y[j][i]) goto break4;
  goto found;

found:
  switch(t.photometric) {
  case PHOTOMETRIC_MINISBLACK:  // greyscale
    bgcolor[0] = y[jj][ii];
    bgcolor[1] = y[jj][ii];
    bgcolor[2] = y[jj][ii];
    break;

  case PHOTOMETRIC_RGB:         // rgb
    ii *= 3;
    bgcolor[0] = buf[jj][ii];
    bgcolor[1] = buf[jj][ii+1];
    bgcolor[2] = buf[jj][ii+2];
  }
  return 0;

break4:
  return -1;
}

// ------------------------------------------------------------------
/** crops out background on the edge

 Return: 0         normal
         -1        errenous info in *this object
         -3        bad memory copy
*/
int Tiff::cropbg(void)
{
  uint8 bc[3];
  uint32 i,j;
  uint8 yy;
  int tcrop,bcrop,lcrop,rcrop;

  int ret = getbgcolor(bc);
#if SHOWINFO >= 2
  printf ("found = %d; bgcolor = %u %u %u\n", ret, bc[0],bc[1],bc[2]);
#endif
  if (ret) return 0;

  // use luminance to determine the crop range
  yy = ( RED * bc[0] + GREEN * bc[0] + BLUE * bc[0] ) >> 8;

  for (j=0; j < t.imagelength; j++)
    for (i=0; i < t.imagewidth; i++)
      if (yy != y[j][i]) goto break1;
break1:
  tcrop = j;

  for (j=t.imagelength-1; j > 0; j--)
    for (i=0; i < t.imagewidth; i++)
      if (yy != y[j][i]) goto break2;
break2:
  bcrop = j;

  for (i=0; i < t.imagewidth; i++)
    for (j=0; j < t.imagelength; j++)
      if (yy != y[j][i]) goto break3;
break3:
  lcrop = i;

  for (i=t.imagewidth-1; i > 0; i--)
    for (j=0; j < t.imagelength; j++)
      if (yy != y[j][i]) goto break4;
break4:
  rcrop = i;

#if SHOWINFO >= 2
  printf("crop = %d %d %d %d\n",tcrop,bcrop,lcrop,rcrop);
#endif

  uint32 newwidth = MAX2(1, (rcrop - lcrop));
  uint32 newlength = MAX2(1, (bcrop - tcrop));

  choplength(newlength);

  // set dimensions
  t.imagewidth = newwidth;
  t.imagelength = newlength;
  scanlinesize = scansize();

  printf("Tiff::cropbg: image cropped to %d x %d\n", newwidth, newlength);

  if (t.planarconfig == PLANARCONFIG_CONTIG) {

    // move memory in buf

    // byte shift in x.  Works for RGBRGB...
    uint32 sx = t.samplesperpixel * (lcrop + 1);

    for (i=tcrop+1, j=0; i<bcrop; i++, j++) {
      if ( memmove( &buf[j][0], &buf[i][sx], scanlinesize )
           != &buf[j][0] ) {
      error("Bad memcpy", "Tiff::cropbg");  return (-3);
      }
    }

  } else {
    error("Unsupported planarconfig value", "Tiff::cropbg");
    return (-1);
  }

  return 0;
}

// ------------------------------------------------------------------
/** set up y & z buffers for image processing

   The y buffer contains luminance (Y).  For b/w images,
   it is the same as buf.  For rgb, its value is computed.

   The z buffer is used for image analysis.
*/
void Tiff::setyz(void) {
  switch(t.photometric) {
  case PHOTOMETRIC_MINISBLACK:  // greyscale
    y = buf;
    break;

  case PHOTOMETRIC_RGB:         // rgb ==> luminace
    uint32 i, j, i1;

    y = new uint8 * [t.imagelength];

    /* Timing study for 100 loops -
       integer operation: 3.17u 0.41s
        y[j][i] = ( RED * buf[j][i1] 
                    + GREEN * buf[j][i1+1] + BLUE * buf[j][i1+2] ) >> 8;

       double operation: 4.19u
        y[j][i] = (uint8) ( 0.28 * (double)buf[j][i1] 
          + 0.59 * (double)buf[j][i1+1] + 0.11 * (double)buf[j][i1+2] );

       double operation: 4.04u
        y[j][i] =  ( 0.28 * buf[j][i1] + 0.59 * buf[j][i1+1] + 0.11 * buf[j][i1+2] );

    for (int k=0; k<100; k++) {
       */

    for (j=0; j<t.imagelength; j++) {
      y[j] = new uint8 [t.imagewidth];
      for (i=0, i1=0; i < t.imagewidth; i++,i1+=3) {
        y[j][i] = ( RED * buf[j][i1] 
          + GREEN * buf[j][i1+1] + BLUE * buf[j][i1+2] ) >> 8;
      }
    }
    break;

  default:
    error("Unsupported photometric option", "Tiff::setyz");
  }

  yzstate = OK_YZ;

}


// --------------------------------------------------------------
/** renew y & z buffers from buf
*/
void Tiff::renewyz(void)
{
  switch(t.photometric) {
  case PHOTOMETRIC_MINISBLACK:  // greyscale
    y = buf;
    break;

  case PHOTOMETRIC_RGB:         // rgb ==> luminace
    uint32 i, j, i1;

    for (j=0; j<t.imagelength; j++) {
      for (i=0, i1=0; i < t.imagewidth; i++,i1+=3) {
        y[j][i] = ( RED * buf[j][i1] 
          + GREEN * buf[j][i1+1] + BLUE * buf[j][i1+2] ) >> 8;
      }
    }
    break;

  default:
    error("Unsupported photometric option", "Tiff::renewyz");
  }

  yzstate = OK_YZ;

}


// ------------------------------------------------------------
/** kills y & z buffers set in setyz, set pointer to NULL
    This is called by destructor.
*/
void Tiff::killyz(void) {
  if ( yzstate == NO_YZ ) {
    error("No yz buffers created", "Tiff::killyz");
    return;
  }

  switch(t.photometric) {
  case PHOTOMETRIC_MINISBLACK:  // greyscale
    break;

  case PHOTOMETRIC_RGB:         // rgb ==> luminace
    uint32 j;
    for (j=0; j < t.imagelength; j++) delete [] y[j];
    delete [] y;
    break;

  default:
    error("Unsupported photometric option", "Tiff::killyz");
  }
  
  y = NULL;
  yzstate = NO_YZ;

}

/** Kills y & z buffers from start row.
    This is called by choplength.
*/
void Tiff::killyz(uint32 startrow) {
  if ( yzstate == NO_YZ ) {
    error("No yz buffers created", "Tiff::killyz");
    return;
  }

  switch(t.photometric) {
  case PHOTOMETRIC_MINISBLACK:  // greyscale
    break;

  case PHOTOMETRIC_RGB:         // rgb ==> luminace
    uint32 j;
    for (j=startrow; j < t.imagelength; j++) delete [] y[j];
    break;

  default:
    error("Unsupported photometric option", "Tiff::killyz");
  }
  
  yzstate = OK_YZ;

}

// ----------------------------------------------------------
/** Returns scanlinesize from Tags
*/
int32 Tiff::scansize(void)
{
  int32 sl = t.bitspersample * t.imagewidth;
  if (t.planarconfig == PLANARCONFIG_CONTIG)
    sl *= t.samplesperpixel;
  return ( (sl + 7)/8 );
}

// ----------------------------------------------------------
/** Shows where error occurs
*/
void Tiff::error(char *message, char *function)
{
  cerr << function << ": " << message << "!\n";
}

#endif      //  method implementation ends

/* --------- end Tiff class --------------------------------*/

//#define TIFF_TEST
#ifdef TIFF_TEST
void main(int argc, char* argv[]){

  if (argc < 2)
    {
      printf("Usage: %s image_file.\n",argv[0]);
      exit (1);
    }
 
  /*
  Tiff a(320,240);
  a.readrgb(argv[1]);
  a.write("tem1.tif");
  */

  /* A loop test */
  Tiff *a = new Tiff(argv[1]);
  a->write("tem1.tif");

  /*
  Tiff b("tem1.ntf");
  b.write("tem1.tif");
 */

  /*
  Tiff a(argv[1]);
  Tiff b(argv[2]);
  a ^= b;
  a->write("tem1.tif");
  */

  cout << "image written to tem1\n";

  delete a;
}
#endif

#ifdef HEADER_ONLY   // set flag if used as header
#ifndef TIFF_HPP
#define TIFF_HPP
#endif
#endif

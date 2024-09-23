//*************************************************************************
/*
 * Nitf.cpp - A Class for NITF file in/out.
 *
 *  This file should be under $CLASSPATH/jni
 *
 *  This file can be used as a header file as follows:
 *      #ifndef HEADER_ONLY
 *      #define HEADER_ONLY
 *      #include "Nitf.cpp"
 *      #undef HEADER_ONLY
 *      #endif
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

// include this only if HEADER_ONLY not defined
#ifndef HEADER_ONLY
#define HEADER_ONLY
#include "Tiff.cpp"
#undef HEADER_ONLY
#endif

#define NITF_DEBUG  // shows debug messages

/** A class for reading & writing NITF files.

  The Nitf class encapsulates the base image, arrays of inset
  images and cgm buffers.  (Note that inset images are not
  implemented yet.)  The main methods are read (constructor) and
  write, which use the API in the core NITF service library
  (NS_XXXXX functions).  The data structures, like image header,
  symbol header, etc. are identical to those used in the NITF
  service library.

  Example of usage:

  First, include this file into your C++ file as a header as follows:
      #ifndef HEADER_ONLY
      #define HEADER_ONLY
      #include "Nitf.cpp"
      #undef HEADER_ONLY
      #endif

  Then you may create the Nitf object and call its methods.

  -- reading a nitf file

  Nitf a((char *)file ); // Creates a Nitf object by reading in the file
  width = a.getWidth();     
  height = a.getHeight();

  // get the buffer data to pixels, which must be allocated 
  // before this call
  a.getBuf( (int *)pixels );

  a.getnumCGM();          // get number of CGM buffers
  // allocate memory for cgmlen, and *cgm
  a.getCGMlen(cgmlen);
  // allocate memory for cgm
  a.getCGM(cgm, cgmlen);  // get CGM buffers.  

  // The pixels can be sent to Java and display as an image.

  // The CGM buffers will be sent to Java and interpreted using 
  // the Java CGM class library.

 -- writing a nitf file

  Nitf a(width, height);     // Creates a Nitf object
  a.setBuf( (int *)pixels ); // set pixels for base image
  a.setnumCGM(numCGM);       // set number of CGM
  a.setCGM(cgm, cgmlen);     // set CGM buffers and their lengths
  a.write( (char *)file );   // write to file.
          // Note that write() is inherited from Tiff.cpp

  Note: Nitf is a subclass of Tiff.cpp.  The base image of the NITF
  file is the parent Tiff image itself.

*/
class Nitf : public Tiff {
protected:
  Tiff *IM;             // an array of inset images
  int numIM;            // number of inset images (base image excluded)

  char **cgm;  // an array of cgm buffers
  int *cgmlen;          // length of each CGM buffer
  int numCGM;           // number of CGM's
  int numSY;            /* number of symbols.  It should be >= numCGM (usually equal).
			   Memory for cgm is allocated according to numSY. */

  int numTX;            // number of text areas.  Not used

  // The following overwrite those in Tiff
  int readnitf(char *filename);  /* read nitf file, allocate memory
                                    for the Tiff object and cgm buffers */

public:
  Nitf(int width=1, int height=1, int numCGM=0, int numIM=0);  // First constructor 
      //  with base image dimension etc. as argument.

  Nitf(char *filename);   // read constructor (from a file)

  Nitf(int width, int height, const Tiff& b)  // Third constructor.
    : Tiff(width, height, b) { }              // create a new Nitf with the same property as b.

  Nitf(const Tiff& b)              // copy constructor
    : Tiff(b) { }

  ~Nitf();          // destructor 

  void setnumCGM(int numCGM)      // set numCGM.  Should be called before setCGM.
    { this->numCGM = numCGM; }
  int setCGM(char **cgm, int *cgmlen);  // create and set the cgm buffers (it allocates memory).

  int getnumCGM()              // get numCGM.  Should be called before getCGMlen.
    { return this->numCGM; }
  int getCGMlen(int *cgmlen);  // get the array of cgmlen.  Should be called before getCGM.
  int getCGM(char **cgm, int *cgmlen);  // get the cgm buffers.

  void writecgm(char *filename, int n);  // write cgm[n] to a file

  // The following overwrite those of Tiff
  int writenitf(char *filename);   // write nitf file

  virtual void error(char *message, char *function);  // show error

};


#ifndef HEADER_ONLY   // method implementation starts
// ----------------------------------------------------------

/** first constructor.  Allocate some memory.  Note that
  the memory for cgm[i] is allocated by setCGM.
 */
Nitf::Nitf(int width, int height, int nCGM, int nIM) 
    : Tiff(width, height) { 
  int i;

  numSY = numCGM = nCGM;
  numIM = nIM;

  cgm = new char * [numSY];  // creates the array of buffers
  cgmlen = new int [numSY];           // creates the array of buffer lengths
  for (i=0; i<numSY; i++) {           // set them to null
    cgm[i] = NULL; 
    cgmlen[i] = 0; 
  }
  
}

/** read constructor */
Nitf::Nitf(char *filename) { 
  numCGM=0;
  numIM=0;
  read(filename); // read from Tiff class
}

/** Destructor: destroys a Nitf object */
Nitf::~Nitf() {
  // delete array of cgm buffers
  // note: numSY >= numCGM
  for (int j=0; j < numSY; j++) {
    if (cgm[j] != NULL) delete [] cgm[j];
  }
  if (numSY > 0) {
    delete [] cgm;
    delete [] cgmlen;
  }
}

/** create and set the cgm buffers.  Simply return if numCGM==0. */
int Nitf::setCGM(char **cgm, int *cgmlen) {
  int i;
  
  for (i=0; i<numCGM; i++) {           // set them to null
    this->cgm[i] = new char[cgmlen[i]];
    if (cgm[i] == NULL || this->cgm[i] == NULL) {
      error("null cgm[i]", "setCGM");
      return -1;
    }
    memcpy(this->cgm[i], cgm[i], cgmlen[i]);
    this->cgmlen[i] = cgmlen[i]; 
  }
  return 0;
}

/** get the cgm length array. */
int Nitf::getCGMlen(int *cgmlen) {
  int i;
  for (i=0; i<numCGM; i++) {
    cgmlen[i] = this->cgmlen[i]; 
  }
  return 0;
}

/** get the cgm buffers.  Should be called after cgm and cgmlen 
 have been allocated memory. */
int Nitf::getCGM(char **cgm, int *cgmlen) {
  int i;

  for (i=0; i<numCGM; i++) {
    if (this->cgm[i] == NULL) {
      error("null cgm[i]", "getCGM");
      return -1;
    }
    memcpy(cgm[i], this->cgm[i], this->cgmlen[i]);
    cgmlen[i] = this->cgmlen[i]; 
  }
  return 0;
}

/** write cgm[n] to file */
void Nitf::writecgm(char *filename, int n) {
  if (n>=numCGM) {
    error("n out of range", "writecgm");
    return;
  }

  FILE *fp = fopen(filename, "wb");       // file handle

  if (fp == NULL) { 
    error("cannot open file", "Nitf::writecgm");
    return;
  }
  fwrite(cgm[n], cgmlen[n], 1, fp);
  fclose(fp);

}

// -------------------- NITF I/O ----------------------------
extern "C" {
#include "nitfserv.h"
#include "nsdefault.h"
#include "CRRN.h"
}

/** reads a nitf file.  Also allocates memory.
  Return 0 normal; -1 error;
*/
int Nitf::readnitf(char *filename)
{
   int n;
   char *method = "readnitf";

   NS_MSGDBS *msgdb = NS_open_msgdbs();    /* initialize the msg db */
   ns_unpack(filename, msgdb);     /* Unpacks an NITF file into database */

   NS_DHNDL *data_hdl = NS_dhndl_alloc();
   NS_SHDR4IMG *img_hdr = NS_image_alloc();
   NS_SHDR4SYM *sym_hdr = NS_sym_alloc();
   NS_NLE *nle = (NS_NLE *) malloc(sizeof(NS_NLE));

   /* for symbol */
   char stype[2];	/* symbol type:                             */
                        /*              B - bit-mapped               */
                        /*              C - computer graphics metafile */
              	        /*              O - object                     */
   char	encryp[2];	/* encryption: 0=not encrypteds */
			/* 1=encrypted - currently undefined */
   char sid[11], sname[21], scolor[2];   /* id, name and color */
   int ccwrot;                           /* symbol ccw rotation (0-359) */

#ifdef NITF_DEBUG
   printf("Nitf::readnitf starting.......\n");
#endif

   //...................... count the number of stuff
   numIM = -1;        // don't count the base image
   numSY = numTX = 0;
   int more = NS_firstNLE("AL", nle, msgdb);
   while (more == SUCCESS)
   {
      switch (nle->type[0]) { 
      case 'I':
	numIM++;  break;

      case 'S':
	numSY++;  break;

      case 'T':
	numTX++;  break;

      default:
	break;
      }
      more = NS_nextNLE(nle, msgdb);
   }

   if (numIM < 0) {
     error("no base image", method);
   } else {
     printf("one base image\n");
   }

#ifdef NITF_DEBUG
   printf("number of inset images = %d\n", (numIM > 0) ? numIM : 0);
   printf("number of symbols = %d\n", numSY);
   printf("number of text elements = %d\n", numTX);
#endif


   //............  Get the images, the first one being the base image
   if (numIM >= 0) {

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
   if (abpp != 8) error("Unsupported bits per pixel", method);
#ifdef NITF_DEBUG
   printf ("actual bits per pixel per band = %d\n", abpp);
#endif

   char ic[3];
   memcpy(ic, img_hdr->ic, 2);
   ic[2]='\0';
   if (strcmp(ic, "NC") != 0) error("Unsupported image compression", method);
#ifdef NITF_DEBUG
   printf("image compression = %s\n", ic);
#endif

   // computes the correct size
   scanlinesize = scansize();
   
#ifdef NITF_DEBUG
   cout << "Nitf::readnitf - " << "\n";
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

   }   // ... end of   if (numIM >= 0) 

   //...................... get the symbols (CGM)
   {
     // note numSY >= numCGM
     cgm = new char * [numSY];  // creates the array of buffers
     cgmlen = new int [numSY];           // creates the array of buffer lengths

     NS_firstNLE("SY", nle, msgdb);
     data_hdl->filename = NULL;
     numCGM = 0;
     for (n=0; n<numSY; n++) {
	ns_dbs2sym(sym_hdr, data_hdl, nle->dlvl, msgdb);

	memcpy(stype, sym_hdr->stype, 1);
	stype[1]='\0';

	// do not include non CGM symbols
	if (stype[0] != 'C') {
	  error("not a cgm symbol", method);
	  continue;
	}

	memcpy(encryp, sym_hdr->encryp, 1);
	encryp[1]='\0';

	memcpy(sid, sym_hdr->sid, 10);
	sid[10]='\0';

	memcpy(sname, sym_hdr->sname, 20);
	sname[20]='\0';

#ifdef NITF_DEBUG
	printf ("type = %s (C=CGM)\n", stype);
	printf ("encryption = %s\n", encryp);
	printf ("ID = %s\n", sid);
	printf ("name = %s\n", sname);
	printf ("data size = %d\n", data_hdl->datasize );
	printf ("data address = %x\n", data_hdl->data);
#endif

	cgm[numCGM] = new char[data_hdl->datasize];
	memcpy(cgm[numCGM], data_hdl->data, data_hdl->datasize);
	cgmlen[numCGM] = data_hdl->datasize;

	NS_nextNLE(nle, msgdb);
	numCGM++;
     }
   }

#ifdef NITF_DEBUG
   printf("numCGM = %d\n", numCGM);
#endif


   /* free memory */
   free(nle);
   NS_image_free(img_hdr);
   NS_sym_free(sym_hdr);
   NS_dhndl_free(data_hdl);

   /* Clean up dbs and temp files */
   NS_close_msgdbs ( msgdb );

   return 0;
}

#define NSERROR_OPEN_DB		-1	/* error opening message database */
#define NSERROR_HDR		-2	/* error allocating a message header */
#define NSERROR_SHDR		-3	/* error allocating image subheader */
#define NSERROR_SAVING		-4	/* error saving to database */
#define NSERROR_COMPRESS	-5	/* error compressing the image */
#define NSERROR_GENFILE		-6	/* could not generate NITF file from database */

/** Writes an image object to a NITF file.  See the define's
 for return code. 
*/
int Nitf::writenitf(char *filename) {
  char *method = "writenitf";

  /* Nitf properties */
  char *compression = (char *) 0;
  int level = 1;
  int len3 = t.imagelength * t.imagewidth * 3;

  unsigned char *rgb = new unsigned char[len3];
  getBuf( rgb );         // get rgb buffer from Tiff object

  int height = t.imagelength;
  int width = t.imagewidth;

#ifdef NITF_DEBUG
   printf("Nitf::writenitf starting.......\n");
#endif

  //........... starts writing base image to nitf (derived fom rgb2nitf)
  NS_DHNDL data_handle;
  NS_MSGDBS *msgdb;
  NS_HDR4MSG *hdr;
  NS_SHDR4IMG *img_hdr;
  char ldt[11];
  char rows[9];
  char cols[9];
  int ret_code = 0;
  int rc;

  msgdb = NS_open_msgdbs();	/* initialize the msg db */
  if ( msgdb == (NS_MSGDBS *)0) return (NSERROR_OPEN_DB);

  hdr = NS_hdr_alloc();	/* allocates message header */
  if ( hdr == (NS_HDR4MSG *)0 ) return (NSERROR_HDR);

  NS_SetA_MSG_numi ( hdr, "1" );  /* set # of images in msg */
  ns_hdr2dbs ( hdr, msgdb );	/* store the header in the db */

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
  memcpy ( img_hdr->nppbv, &rows[4], 4);	/* number of pixels/block vertically */

  NS_SetA_IMG_irep ( img_hdr, "RGB     " );  /* set image representation */
  memcpy ( img_hdr->imode, "P", 1 );
  memcpy ( img_hdr->nbpp, "08", 2);	
  memcpy ( img_hdr->source.source20.abpp,"08",2);

  NS_SetA_IMG_nbands ( img_hdr, "3" );	/* set image bands */
  img_hdr->bandinfo[0] = NS_band_alloc();
  memcpy ( img_hdr->bandinfo[0]->bandinfo.bi20.irepband, "R ", 2 );
  img_hdr->bandinfo[1] = NS_band_alloc();
  memcpy ( img_hdr->bandinfo[1]->bandinfo.bi20.irepband, "G ", 2 );
  img_hdr->bandinfo[2] = NS_band_alloc();
  memcpy ( img_hdr->bandinfo[2]->bandinfo.bi20.irepband, "B ", 2 );

  memcpy(img_hdr->iid,"Nitf Class",10);

  /* This setting reads the data and writes it to the database.  rgb
     has priority.  If it is not null, then filename is set to null. */
  data_handle.data = (void *)rgb;
  data_handle.filename = NULL;
  data_handle.datasize = len3;
  data_handle.masked = 0;

  rc = ns_img2dbs ( img_hdr, &data_handle, msgdb );  /* save the image data */
  if ( rc != 0 ) return ( NSERROR_SAVING );

  //....................... end of writing base image

  //................... start writing cgm
  if (numCGM > 0) {
    NS_SHDR4SYM *sym_hdr = NS_sym_alloc();

#ifdef NITF_DEBUG
    printf("numCGM = %d\n", numCGM);
    printf("cgmlen[0] = %d\n", cgmlen[0]);
#endif

    /* set the data length */
    sprintf ( ldt, "%6.6d", cgmlen[0] );	
    memcpy(sym_hdr->ldt, ldt, 6);       /* Data length in ASCII (0-999999) */

    memcpy(sym_hdr->stype, "C", 1);     /* C - computer graphics metafile */
    memcpy(sym_hdr->encryp, "0", 1);    /* encryption: 0=not encrypteds */
					/* 1=encrypted - currently undefined */
    memcpy(sym_hdr->sid, "Whiteboard", 10); /* symbol id (ASCII) */
    memcpy(sym_hdr->sname, "Whiteboard NITF-CGM  ", 20);

    memcpy(sym_hdr->dlvl, "003", 3);    /* display level     (1-999) */
    memcpy(sym_hdr->alvl, "000", 3);    /* attacment level   (1-999) */

    memcpy(sym_hdr->nbpp, "0", 1);   /* 0 for CGM, 1 for Object */

    /* This setting reads the data and writes it to the database. */
    data_handle.data = (void *)cgm[0];
    data_handle.filename = NULL;       // not reading from file
    data_handle.datasize = cgmlen[0];
    data_handle.masked = 0;

    rc = ns_sym2dbs ( sym_hdr, &data_handle, msgdb );  /* save the data */
    if ( rc != 0 ) return ( NSERROR_SAVING );

    NS_sym_free(sym_hdr);

  }
  //................... end writing cgm

  /*  Compress the data and generate an NITF file	*/
  if ( compression != NULL ) {
    rc = NS_Compress_All ( msgdb, compression, level );  /* compress the image */
    ret_code = NSERROR_COMPRESS;
  }

  rc = ns_pack ( filename, msgdb );	/* generate an NITF file from the database */
  if ( rc != 0 ) ret_code = NSERROR_GENFILE;

  /* Clean up */
  NS_hdr_free ( hdr );
  NS_close_msgdbs ( msgdb );

  delete rgb;

#ifdef NITF_DEBUG
  printf ( "Nitf::writenitf: return code =%d \n", rc );
#endif

  return rc;
}

// ----------------------------------------------------------
/** Shows where error occurs */
void Nitf::error(char *message, char *function) {
  printf ("Nitf::%s: %s\n", function, message);
}

#endif      //  method implementation ends

/* --------- end class --------------------------------*/


//#define NITF_TEST
#ifdef NITF_TEST
void main(int argc, char* argv[]){

  if (argc < 2)
    {
      printf("Usage: %s image_file\n",argv[0]);
      exit (1);
    }

  /* A loop test */
  Nitf a(argv[1]);

  int buflen[1] = { 4 };

  // This is not equivalent to what follows!
  //  char buf[1][4] = { {1, 2, 3 ,4} };

  /* create a fake cgm and add it to nitf
  char **buf;
  buf = new char * [1];
  buf[0] = new char [4];
  for (int i=0; i<4; i++) buf[0][i] = i;

  a.setCGM((char **)buf, (int *)buflen, 1);
  */

  a.write("tem1.ntf");
  a.writecgm("tem1.cgm", 0);


  cout << "written to tem1\n";

}
#endif

#ifdef HEADER_ONLY   // set flag if used as header
#ifndef NITF_HPP
#define NITF_HPP
#endif
#endif

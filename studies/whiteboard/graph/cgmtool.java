//*************************************************************************
/*
 * CgmTool.java -     An class encapsulating all CGM
 *   (Computer Graphics Metafile) op-codes and command call methods.
 *
 *
 *   This file should be under $CLASSPATH/graph
 *   To run:   java wb.Main
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package graph;

// standard java class libraries
import java.io.*;
import java.awt.*;
import java.lang.*;

/** A class encapsulating all CGM op-codes and command call methods.
<PRE>
  This implementation follows the standard MIL-STD-2301 (CGM
  implementation standard for the National Imagery Transmission
  Format, or NITF-CGM June 1993), which is a subset of the ISO CGM
  standard.  In particular, all integers are signed 16-bit, with Big
  Endian convention (high bits 15-8 <=> high order byte).

  Although the CGM standard does not have bias toward the organization
  of the command calls.  We feel that it is best to use a
  object-oriented approach, i.e., each graphic primitive (which we
  call glyphs) should encapsulate all of its own attributes (or define
  its attributes before calling the primitive command).  This will be
  crucial in a collaborative environment, in which the glyphs are
  transimitted to peers over network.

  Sequence of calling ([] indicates optional commands):
  All NITF-CGM compliant commands are listed here.

    BegMF
                   state = METAFILE_HEAD
      MFVersion        
      MFElemList
      MFDesc
      [FontList]

      BegPic
                   state = PICTURE_HEAD
        VDCExt
        ColrMode
	[EdgeWidthMode]
	[LineWidthMode]

        BegPicBody 
                   state = PICTURE_BODY

	[LineWidth, LineType, LineColr] - attributes
	[Line/EllipArc/CircArcCntr] - primitives

	[FillColr, IntStyle, EdgeVis, EdgeWidth, EdgeType, EdgeColr] - attributes
	[Polygon, Ellipse, EllipArcClose, Rect, Circle, ArcCtrClose] - primitives

	[TextColr, CharHeight, TextFontIndex, CharOri] - attributes
	[Text] - primitives

	Note: commands to write primitives and attributes should appear
	before EndPic.  For glyphs, Glyph.toCGM can be called.  Try to
	use object oriented style for each graphic primitive (what we
	call glyph).  So each glyph should come with all of its own
	attributes.  

      EndPic 
                   state = PICTURE_CLOSED
    EndMF
                   state = METAFILE_CLOSED

  All integer input parameters are int, except for pixel coordinates (x,y), 
  which are in short. 
</PRE>
*/
public class CgmTool {

  /** A short array large enough to hold short form parameters.
   (EllipArcClose has 11 parameters.) */
  private short[] p = new short[11];

  /** The max of font index (= number of fonts in font list) */
  private int MaxFontIndex;

  /** Indicates which one of the four VDCExt Cases. */
  private int VDCExtCase;

  /** state variable */
  private byte state = NULL;

  /* states */
  public static final byte NULL             = 0; /** Null state - idle */
  public static final byte METAFILE_HEAD    = 1;
  public static final byte PICTURE_HEAD     = 2;
  public static final byte PICTURE_BODY     = 3;
  public static final byte PICTURE_CLOSED   = 4;
  public static final byte METAFILE_CLOSED  = 5;

  // ...................... define cgm op-codes
  // For binary encoding, each 16-bit op-code consists of 
  // class (4 bits), ID (7 bits), and length (5 bits).

  /* metafile class masks */
  public static final short DELIMETER     = 0x0000;
  public static final short MF_DESCRIPTOR = 0x1000;
  public static final short PIC_DESCRIPTOR= 0x2000;
  public static final short CONTROL       = 0x3000;
  public static final short PRIMITIVE     = 0x4000;
  public static final short ATTRIBUTE     = 0x5000;

  /* metafile Class | ID masks (use clear text name with an underscore) */

  /* Class 0: DELIMETER */
  public static final short BegMF_     = 0x0020; /* ID 1 */
  public static final short EndMF_     = 0x0040; /* ID 2 */
  public static final short BegPic_    = 0x0060; /* ID 3 */
  public static final short BegPicBody_= 0x0080; /* ID 4 */
  public static final short EndPic_    = 0x00A0; /* ID 5 */

  /* Class 1: MF_DESCRIPTOR */
  public static final short MFVersion_  = MF_DESCRIPTOR | 0x0020; /* ID 1 */
  public static final short MFDesc_     = MF_DESCRIPTOR | 0x0040; /* ID 2 */
  public static final short VDCType_    = MF_DESCRIPTOR | 0x0060; /* ID 3 */
  public static final short IntegerPrec_= MF_DESCRIPTOR | 0x0080; /* ID 4 */
  public static final short RealPrec_   = MF_DESCRIPTOR | 0x00A0; /* ID 5 */
  public static final short IndexPrec_  = MF_DESCRIPTOR | 0x00C0; /* ID 6 */
  public static final short ColrPrec_      = MF_DESCRIPTOR | 0x00E0; /* ID 7 */
  public static final short ColrIndexPrec_ = MF_DESCRIPTOR | 0x0100; /* ID 8 */
  public static final short MaxColrIndex_  = MF_DESCRIPTOR | 0x0120; /* ID 9 */
  public static final short ColrValueExt_  = MF_DESCRIPTOR | 0x0140; /* ID 10 */
  public static final short MFElemList_    = MF_DESCRIPTOR | 0x0160; /* ID 11 */
  public static final short BegMFDefaults_ = MF_DESCRIPTOR | 0x0180; /* ID 12 */
  public static final short FontList_      = MF_DESCRIPTOR | 0x01A0; /* ID 13 */

  public static final short EndMFDefaults_ = MF_DESCRIPTOR | 0x0000; /* No command for this since
								      * BegMFDefaults contains length. */

  /* Class 2: Picture_Descriptor */
  public static final short ScaleMode_     = PIC_DESCRIPTOR | 0x0020; /* ID 1 */
  public static final short ColrMode_      = PIC_DESCRIPTOR | 0x0040; /* ID 2 */
  public static final short LineWidthMode_ = PIC_DESCRIPTOR | 0x0060; /* ID 3 */
  public static final short MarkerSizeMode_= PIC_DESCRIPTOR | 0x0080; /* ID 4 */
  public static final short EdgeWidthMode_ = PIC_DESCRIPTOR | 0x00A0; /* ID 5 */
  public static final short VDCExt_        = PIC_DESCRIPTOR | 0x00C0; /* ID 6 */
  public static final short BackColr_      = PIC_DESCRIPTOR | 0x00E0; /* ID 7 */

  /* Class 4: PRIMITIVE */
  public static final short Line_      = PRIMITIVE | 0x0020; /* ID 1 */
  public static final short DisjtLine_ = PRIMITIVE | 0x0040; /* ID 2 */
  public static final short Marker_    = PRIMITIVE | 0x0060; /* ID 3 */
  public static final short Text_      = PRIMITIVE | 0x0080; /* ID 4 */
  public static final short RestrText_ = PRIMITIVE | 0x00A0; /* ID 5 */
  public static final short ApndText_  = PRIMITIVE | 0x00C0; /* ID 6 */
  public static final short Polygon_   = PRIMITIVE | 0x00E0; /* ID 7 */
  public static final short PolygonSet_= PRIMITIVE | 0x0100; /* ID 8 */

  public static final short Rect_	   = PRIMITIVE | 0x0160; /* ID 11 */
  public static final short Circle_        = PRIMITIVE | 0x0180; /* ID 12 */
  public static final short Arc3Pt_        = PRIMITIVE | 0x01A0; /* ID 13 */
  public static final short Arc3PtClose_   = PRIMITIVE | 0x01C0; /* ID 14 */
  public static final short ArcCtr_        = PRIMITIVE | 0x01E0; /* ID 15 */
  public static final short ArcCtrClose_   = PRIMITIVE | 0x0200; /* ID 16 */
  public static final short Ellipse_       = PRIMITIVE | 0x0220; /* ID 17 */
  public static final short EllipArc_      = PRIMITIVE | 0x0240; /* ID 18 */
  public static final short EllipArcClose_ = PRIMITIVE | 0x0260; /* ID 19 */
  public static final short ArcCtrRev_     = PRIMITIVE | 0x0280; /* ID 20 */

  /* Class 5: ATTRIBUTE for primitives */

  public static final short LineIndex_ = ATTRIBUTE | 0x0020; /* ID 1 */
  public static final short LineType_  = ATTRIBUTE | 0x0040; /* ID 2 */
  public static final short LineWidth_ = ATTRIBUTE | 0x0060; /* ID 3 */
  public static final short LineColr_  = ATTRIBUTE | 0x0080; /* ID 4 */
  public static final short MarkerIndex_   = ATTRIBUTE | 0x00A0; /* ID 5 */
  public static final short MarkerType_    = ATTRIBUTE | 0x00C0; /* ID 6 */
  public static final short MarkerSize_    = ATTRIBUTE | 0x00E0; /* ID 7 */
  public static final short MarkerColr_    = ATTRIBUTE | 0x0100; /* ID 8 */
  public static final short TextIndex_     = ATTRIBUTE | 0x0120; /* ID 9 */
  public static final short TextFontIndex_ = ATTRIBUTE | 0x0140; /* ID 10 */

  public static final short TextColr_   = ATTRIBUTE | 0x01C0; /* ID 14 */
  public static final short CharHeight_ = ATTRIBUTE | 0x01E0; /* ID 15 */
  public static final short CharOri_    = ATTRIBUTE | 0x0200; /* ID 16 */

  public static final short IntStyle_   = ATTRIBUTE | 0x02C0; /* ID 22 */
  public static final short FillColr_   = ATTRIBUTE | 0x02E0; /* ID 23 */
  public static final short HatchIndex_ = ATTRIBUTE | 0x0300; /* ID 24 */
  public static final short PatIndex_   = ATTRIBUTE | 0x0320; /* ID 25 */
  public static final short EdgeIndex_  = ATTRIBUTE | 0x0340; /* ID 26 */
  public static final short EdgeType_   = ATTRIBUTE | 0x0360; /* ID 27 */
  public static final short EdgeWidth_  = ATTRIBUTE | 0x0380; /* ID 28 */
  public static final short EdgeColr_   = ATTRIBUTE | 0x03A0; /* ID 29 */
  public static final short EdgeVis_    = ATTRIBUTE | 0x03C0; /* ID 30 */

  // Length code for long form
  public static final short LONGFORM = 0x001F;

  // Styles
  public static final int SOLID = 1;  // for interior/edge/line
  public static final int DASHED= 2;  // for edge/line
  public static final int EMPTY = 4;  // for interior/edge/line

  /** Fonts allowed by NITF_CGM.  Note that they may not be the same as in Java.
   *  For example, TimesRoman-Plain <=> TIMES_ROMAN.  We use string matching to
   *  identify the font index.
   */ 
  public static final String[] NITF_CGM_fontlist = {
    "TIMES_ROMAN",
    "TIMES_ITALIC",
    "TIMES_BOLD",
    "TIMES_BOLD_ITALIC",
    "HELVETICA",
    "HELVETICA_OBLIQUE",
    "HELVETICA_BOLD",
    "HELVETICA_BOLD_OBLIQUE",
    "COURIER",
    "COURIER_BOLD",
    "COURIER_ITALIC",
    "COURIER_BOLD_ITALIC",
    "HERSHEY/CARTOGRAPHIC_ROMAN",
    "HERSHEY/CARTOGRAPHIC_GREEK",
    "HERSHEY/SIMPLEX_ROMAN",
    "HERSHEY/SIMPLEX_GREEK",
    "HERSHEY/SIMPLEX_SCRIPT",
    "HERSHEY/COMPLEX_ROMAN",
    "HERSHEY/COMPLEX_GREEK",
    "HERSHEY/COMPLEX_SCRIPT",
    "HERSHEY/COMPLEX_ITALIC",
    "HERSHEY/COMPLEX_CYRILLIC",
    "HERSHEY/DUPLEX_ROMAN",
    "HERSHEY/TRIPLEX_ROMAN",
    "HERSHEY/TRIPLEX_ITALIC",
    "HERSHEY/GOTHIC_GERMAN",
    "HERSHEY/GOTHIC_ENGLISH",
    "HERSHEY/GOTHIC_ITALIAN"
  };

  /** The order of the styles should be the same as that in fontlist */
  private static final int[] styleValues = 
    {Font.PLAIN, Font.ITALIC, Font.BOLD, Font.ITALIC|Font.BOLD};

  // ...................... end defining cgm op-codes

  /** A convenient output stream for various data formats */
  private DataOutputStream o;

  /** Default constructor (not allowed) */
  private CgmTool () {
    error("Default constructor not allowed", "CgmTool");
  }

  /** Constructor with an OutputStream for writing out cgm codes */
  public CgmTool (OutputStream _o) {
    o = new DataOutputStream(_o);
  }

  //---------------- public methods to write cgm commands ----------

  // ................................. Class 0 & 1 methods
  // Methods are listed in the order they would be called.

  /** Begin CGM metafile  */
  public void BegMF(String MF_name) {
    writeCommand(BegMF_, MF_name);
    state = METAFILE_HEAD;  // now we are in MF header
  }

  /** Version of CGM metafile.  Only version 1 is allowed for this
   implementation. */
  public void MFVersion(int version) {
    if (state != METAFILE_HEAD) 
      error("call invoked in wrong state", "MFVersion");
    if (version != 1) {
      version = 1;
      error("version set to 1", "MFVersion");
    }
    writeCommand(MFVersion_, (short)version);
  }

  /** Metafile description */
  public void MFDesc(String MF_description) {
    if (state != METAFILE_HEAD) 
      error("call invoked in wrong state", "MFDesc");
    writeCommand(MFDesc_, MF_description);
  }

  /* Array of font names */
  public void FontList(String[] fontlist) {
    if (state != METAFILE_HEAD) 
      error("call invoked in wrong state", "FontList");
    MaxFontIndex = fontlist.length;
    writeCommand(FontList_, fontlist);
  }

  /** Begin picture */
  public void BegPic(String picture_name) {
    writeCommand(BegPic_, picture_name);
    state = PICTURE_HEAD;  // now we are in picture header
  }

  /** Begin picture body */
  public void BegPicBody() {
    try {
      o.writeShort( BegPicBody_ | 0x0000 );  // no argument
    } catch (IOException e) {
      System.err.println(e);
    }
    state = PICTURE_BODY;  // now we are in picture body
  }

  // Commands to write primitives and attributes should appear before EndPic.
  // For glyphs, Glyph.toCGM can be called.

  /** End picture */
  public void EndPic() {
    try {
      o.writeShort( EndPic_ | 0x0000 );  // no argument
    } catch (IOException e) {
      System.err.println(e);
    }
    state = PICTURE_CLOSED;
  }

  /** End Metafile */
  public void EndMF() {
    try {
      o.writeShort( EndMF_ | 0x0000 );  // no argument
      o.close();
    } catch (IOException e) {
      System.err.println(e);
    }
    state = METAFILE_CLOSED;
  }

  /* Metafile element list (NITF-CGM implementation - drawing + control set) */
  public void MFElemList() {
    if (state != METAFILE_HEAD) 
      error("call invoked in wrong state", "MFElemList");
    p[0] = 1; p[1] = -1; p[2] = 1;
    writeCommand(MFElemList_, p, 3);
  }


  // ................................. Class 2 methods

  /** Line width specification mode.
    Enumerated values: absolute, scaled (default), fractional, mm
    NITF-CGM uses absolute (0, width specified in source pixel values).
    */
  public void LineWidthMode(int mode) {
    if (state != PICTURE_HEAD) 
      error("call invoked in wrong state", "LineWidthMode");
    writeCommand(LineWidthMode_, (short)mode);
  }

  /** Marker size specification mode.
    Enumerated values: absolute, scaled (default), fractional, mm
    */
  public void MarkerSizeMode(int mode) {
    if (state != PICTURE_HEAD) 
      error("call invoked in wrong state", "MarkerSizeMode");
    writeCommand(MarkerSizeMode_, (short)mode);
  }

  /** Color selection mode.
    Enumerated values: indexed (0 default), direct (1).
    NITF-CGM uses direct (1).
    */
  public void ColrMode(int mode) {
    if (state != PICTURE_HEAD) 
      error("call invoked in wrong state", "ColrMode");
    writeCommand(ColrMode_, (short)mode);
  }

  /** Edge width specification mode for filled-area primitives.
    Enumerated values: absolute, scaled (default), fractional, mm.
    NITF-CGM uses absolute (0, width specified in source pixel values).
    */
  public void EdgeWidthMode(int mode) {
    if (state != PICTURE_HEAD) 
      error("call invoked in wrong state", "EdgeWidthMode");
    writeCommand(EdgeWidthMode_, (short)mode);
  }

  /** VDC (virtual device coordinates) extent specification:
      x1 - x coordinate for lower left corner of VDC space
      y1 - y coordinate for lower left corner of VDC space
      x2 - x coordinate for upper right corner of VDC space
      y2 - y coordinate for upper right corner of VDC space

      Four possible cases:
      (1) x1 < x2 && y1 < y2 - x increasing right and y increasing up
      (2) x1 < x2 && y1 > y2 - x increasing right and y increasing down
      (3) x1 > x2 && y1 < y2 - x increasing left and y increasing up
      (4) x1 > x2 && y1 > y2 - x increasing left and y increasing down

      VDCExt(0, 0, 640, 480); gives the normal (x,y) coordinates.
      VDCExt(0, 480, 640, 0); gives pixel coordinates with y pointing downward.

  */
  public void VDCExt(int x1, int y1, int x2, int y2) {
    if (state != PICTURE_HEAD) 
      error("call invoked in wrong state", "VDCExt");
    p[0] = (short)x1; p[1] = (short)y1; p[2] = (short)x2; p[3] = (short)y2;
    if (x1 < x2 && y1 < y2) VDCExtCase = 1;
    if (x1 < x2 && y1 > y2) VDCExtCase = 2;
    if (x1 > x2 && y1 < y2) VDCExtCase = 3;
    if (x1 > x2 && y1 > y2) VDCExtCase = 4;
    writeCommand(VDCExt_, p, 4);
  }

  // ................................. Class 4 methods

  /** A string of text at (x,y). */
  public void Text(short x, short y, String s) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "Text");
    writeCommand(Text_, x, y, (short)1, s);
  }

  /** A Polygon.  Point 0 and Point N should be joined. */
  public void Polygon(short[] x, short[] y) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "Polygon");
    if ( x.length != y.length) 
      error("arrays x, y have different lengths", "Polygon");
    writeCommand(Polygon_, x, y);
  }

  /** A Line/PolyLine. */
  public void Line(short[] x, short[] y) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "Line");
    if ( x.length != y.length) 
      error("arrays x, y have different lengths", "Line");
    writeCommand(Line_, x, y);
  }

  /** Ellipse at arbitrary orientation.
      cen_x - center x
      cen_y - center y
      con1_x - conjugate axis 1 end point x
      con1_y - conjugate axis 1 end point y
      con2_x - conjugate axis 2 end point x
      con2_y - conjugate axis 2 end point y
      */
  public void Ellipse(short cen_x, short cen_y, 
		      short con1_x, short con1_y, short con2_x, short con2_y) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "Ellipse");
    p[0] = cen_x;   p[1] = cen_y; 
    p[2] = con1_x;  p[3] = con1_y; 
    p[4] = con2_x;  p[5] = con2_y; 
    writeCommand(Ellipse_, p, 6);
  }

  /** Ellipse with Point 0 and Point 1 being the corners of a bounding rectangle.
    A Point = ( X[i], Y[i]).
    */
  public void Ellipse(short X[], short Y[]) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "Ellipse");
    if (X.length != 2) error("only first two elements are used", "Ellipse");
    p[0] = (short)( 0.5 * (X[0]+X[1]) );   // center of ellipse
    p[1] = (short)( 0.5 * (Y[0]+Y[1]) );
    p[2] = (short)( Math.abs(X[0] - p[0]) + p[0]);  
    p[3] = p[1];
    p[4] = p[0];
    p[5] = (short)( Math.abs(Y[0] - p[1]) + p[1]); 
    writeCommand(Ellipse_, p, 6);
  }

  // Close_type for elliptical arc close
  public static final int PIE = 0;
  public static final int CHORD = 1;

  /** Ellipse arc with closed ends
      cen_x - center x
      cen_y - center y
      con1_x - conjugate axis 1 end point x
      con1_y - conjugate axis 1 end point y
      con2_x - conjugate axis 2 end point x
      con2_y - conjugate axis 2 end point y
      start_x  - The x coordinate of a point on the start vector
      start_y  - The y coordinate of a point on the start vector
      end_x    - The x coordinate of a point on the end vector   
      end_x    - The y coordinate of a point on the end vector   
      close_type - The close mechanism to use 
         PIE = 0, two rays from center to start and end
	 CHORD = 1, a line connecting start and end
      */
  public void EllipArcClose(short cen_x, short cen_y, 
		      short con1_x, short con1_y, short con2_x, short con2_y,
		      short start_x, short start_y, short end_x, short end_y,
		      short close_type) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "EllipArcClose");
    p[0] = cen_x;   p[1] = cen_y; 
    p[2] = con1_x;  p[3] = con1_y; 
    p[4] = con2_x;  p[5] = con2_y; 
    p[6] = start_x; p[7] = start_y; 
    p[8] = end_x;   p[9] = end_y; 
    p[10] = close_type;
    writeCommand(EllipArcClose_, p, 11);
  }

  /*
  ** Ellipse arc without closed ends
  */

  public void EllipArc ( short cen_x, short cen_y, short con1_x,
		             short con1_y, short con2_x, short con2_y,
		             short start_x, short start_y, short end_x,
                         short end_y) {

    if ( state != PICTURE_BODY ) 
        error ( "call invoked in wrong state", "EllipArc" );

    p[0] = cen_x;   p[1] = cen_y; 
    p[2] = con1_x;  p[3] = con1_y; 
    p[4] = con2_x;  p[5] = con2_y; 
    p[6] = start_x; p[7] = start_y; 
    p[8] = end_x;   p[9] = end_y; 

    writeCommand ( EllipArc_, p, 10 );
  }

  /** Rectangle with Point 0 and Point 1 being the corners.  A Point = (X[i], Y[i]).
    */
  public void Rect(short X[], short Y[]) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "Rect");
    if (X.length != 2) error("only first two elements are used", "Rect");
    p[0] = X[0];  p[1] = Y[0];
    p[2] = X[1];  p[3] = Y[1];
    writeCommand(Rect_, p, 4);
  }

  /** Circle centered at (x,y) with radius r.  */
  public void Circle(short x, short y, short r) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "Circle");
    p[0] = x;  p[1] = y;  p[2] = r;
    writeCommand(Circle_, p, 3);
  }

  /*
  ** Circle Arc centered at (x,y) with radius r.  See 
  ** Ellipse Arc Close for similar definition.
  */

  public void ArcCtr(short x, short y, 
			short start_x, short start_y, 
			short end_x, short end_y, short r) {

    if (state != PICTURE_BODY) 
       error("call invoked in wrong state", "ArcCtr");

//    p[0] = x;       p[1] = y;       p[2] = r;
//    p[3] = start_x; p[4] = start_y; 
//    p[5] = end_x;   p[6] = end_y; 
    p[0] = x;       p[1] = y;       
    p[2] = start_x; p[3] = start_y;
    p[4] = end_x;   p[5] = end_y;
    p[6] = r;

    writeCommand(ArcCtr_, p, 7);
  }

  /** Circle Arc Center Close centered at (x,y) with radius r.  See 
    Ellipse Arc Close for similar definition. */
  public void ArcCtrClose(short x, short y, 
			short start_x, short start_y, 
			short end_x, short end_y, short r, short close_type) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "ArcCtrClose");
//    p[0] = x;       p[1] = y;       p[2] = r;
//    p[3] = start_x; p[4] = start_y; 
//    p[5] = end_x;   p[6] = end_y; 
//    p[7] = close_type;
    p[0] = x;       p[1] = y;       
    p[2] = start_x; p[3] = start_y;
    p[4] = end_x;   p[5] = end_y;
    p[6] = r;       p[7] = close_type;
    writeCommand(ArcCtrClose_, p, 8);
  }

  // ................................. Class 5 methods

  /** Line color in RGB */
  public void LineColr(byte red, byte green, byte blue) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "LineColr");
    writeColor(LineColr_, red, green, blue);
  }

  /** Line color in integer (ARGB) */
  public void LineColr(int icolor) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "LineColr");
    writeColor(LineColr_, icolor);
  }

  /** Marker color in RGB */
  public void MarkerColr(byte red, byte green, byte blue) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "MarkerColr");
    writeColor(MarkerColr_, red, green, blue);
  }

  /** Marker color in integer (ARGB) */
  public void MarkerColr(int icolor) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "MarkerColr");
    writeColor(MarkerColr_, icolor);
  }

  /** Text color in RGB */
  public void TextColr(byte red, byte green, byte blue) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "TextColr");
    writeColor(TextColr_, red, green, blue);
  }

  /** Text color in integer (ARGB) */
  public void TextColr(int icolor) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "TextColr");
    writeColor(TextColr_, icolor);
  }

  /** Fill color in RGB */
  public void FillColr(byte red, byte green, byte blue) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "FillColr");
    writeColor(FillColr_, red, green, blue);
  }

  /** Fill color in integer (ARGB) */
  public void FillColr(int icolor) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "FillColr");
    writeColor(FillColr_, icolor);
  }

  /** Edge color in RGB */
  public void EdgeColr(byte red, byte green, byte blue) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "EdgeColr");
    writeColor(EdgeColr_, red, green, blue);
  }

  /** Edge color in integer (ARGB) */
  public void EdgeColr(int icolor) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "EdgeColr");
    writeColor(EdgeColr_, icolor);
  }

  /** Character orientation:
     x1 - x component of character up vector   
     y1 - y component of character up vector  
     x2 - x component of character base vector
     y2 - y compenent of character base vector
     */
  public void CharOri(int x1, int y1, int x2, int y2) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "CharOri");
    p[0] =(short) x1; p[1] = (short)y1; p[2] = (short)x2; p[3] = (short)y2; 
    writeCommand(CharOri_, p, 4);
  }

  /** Character orientation for NITF-CGM.
     It allows four cases, corresponding to the four VDC Extent cases:
     (1) x1=0 y1= 1, x2= 1, y2=0
     (2) x1=0 y1=-1, x2= 1, y2=0
     (3) x1=0 y1= 1, x2=-1, y2=0
     (4) x1=0 y1=-1, x2=-1, y2=0
     */
  public void CharOri() {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "CharOri");
    p[0] = 0;  p[3] = 0;
    switch(VDCExtCase) {
    case 1:
      p[1] = 1; p[2] = 1;
      break;
    case 2:
      p[1] =-1; p[2] = 1;
      break;
    case 3:
      p[1] = 1; p[2] =-1;
      break;
    case 4:
      p[1] =-1; p[2] =-1;
      break;
    default:
    }
    writeCommand(CharOri_, p, 4);
  }

  /** Character height in pixels (from baseline to capline) */
  public void CharHeight(int h) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "CharHeight");
    writeCommand(CharHeight_, (short)h);
  }

  /** Interior style.
    Enumerated values: SOLID (1), EMPTY (4)
    */
  public void IntStyle(int style) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "IntStyle");
    if (style != SOLID && style != EMPTY) 
      error("Invalid interior style", "IntStyle");
    writeCommand(IntStyle_, (short)style);
  }

  /** Edge visibility state.  0 is off, none zero is on.
   NITF-CGM specifies on state. */
  public void EdgeVis(int i) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "EdgeVis");
    if ( i != 0 ) i = 1;
    writeCommand(EdgeVis_, (short)i);
  }

  /** Edge Type.  Enumerated values: SOLID (1), DASHED (2)
    */
  public void EdgeType(int type) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "EdgeType");
    if (type != SOLID && type != DASHED) 
      error("Invalid interior type", "EdgeType");
    writeCommand(EdgeType_, (short)type);
  }

  /** Marker Type.  Enumerated values: SOLID (1), DASHED (2)
    */
  public void MarkerType(int type) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "MarkerType");
    if (type != SOLID && type != DASHED) 
      error("Invalid interior type", "MarkerType");
    writeCommand(MarkerType_, (short)type);
  }

  /** Line Type.  Enumerated values: SOLID (1), DASHED (2)
    */
  public void LineType(int type) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "LineType");
    if (type != SOLID && type != DASHED) 
      error("Invalid interior type", "LineType");
    writeCommand(LineType_, (short)type);
  }

  /** Edge width in pixels. */
  public void EdgeWidth(int w) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "EdgeWidth");
    writeCommand(EdgeWidth_, (short)w);
  }

  /** Line width in pixels. */
  public void LineWidth(int w) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "LineWidth");
    writeCommand(LineWidth_, (short)w);
  }

  /** Marker size in pixels. */
  public void MarkerSize(int h) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "MarkerSize");
    writeCommand(MarkerSize_, (short)h);
  }

  /** Set Text Font Index (1 to MaxFontIndex) */
  public void TextFontIndex(int index) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "TextFontIndex");
    // check whether index is in range.
    if ( index < 1 || index > MaxFontIndex) error("Font index out of range",
						  "TextFontIndex");
    writeCommand(TextFontIndex_, (short)index);
  }

  /** Set Text Font Index by matching the font name and font style. */
  public void TextFontIndex(String font, int style) {
    if (state != PICTURE_BODY) 
      error("call invoked in wrong state", "TextFontIndex");

    int i, index;

    // go thru the list to find the font name by matching the first 5 char
    for (i=0; i<NITF_CGM_fontlist.length; i++) {
      if ( NITF_CGM_fontlist[i].regionMatches(true, 0, font, 0, 5) ) break;
    }
    index = i;

    // find style index
    for (i=0; i<styleValues.length; i++) {
      if ( style == styleValues[i] ) break;
    }
    index += i + 1;   // start with 1 
    writeCommand(TextFontIndex_, (short)index);
  }


  //---------------- private generic methods to write cgm commands --

  /** write a command with one string parameter.  Applicable opcodes
   * are: BegMF_, MFDesc_, BegPic_,
   *
   * Both short and long forms were tested. - YTLau
   *
   * The command header for the long form:
   * - first 16 bits (command header)
   *  element class is in bits 15-12
   *  element id is in bits 11-5
   *  parameter list length is in bits 4-0 = 31
   * - second 16 bits (command parameters)
   *  bit 15 = 0 for last partition; = 1 for continued partitions
   *  bit 14-0 param list length
   *
   * We only implement one partition (number of octets < 32767 (max_short)).
   */
  private void writeCommand(short opcode, String s) {
    int len;
    int slen = s.length();     // string length
    boolean even = slen % 2 == 0;

    if (even) {
      len = slen + 1;            // len must be odd to meet the 16 bit boundary
    } else {
      len = slen;
    }
    byte[] b = s.getBytes();  // extracts the bytes

    try {
      // write the command.  len+1 = number of octets (8-bit)
      if (len < 30) {            // len+1 must be < LONGFORM
	o.writeShort( opcode | (len+1) );  // 2 bytes
	o.write((byte)slen);               // 1 byte
	o.write(b);                        // slen bytes
	if (even) o.write(0x0);            // 1 padding byte

      } else if ( len < 32766 ) {          //  long form of command
	o.writeShort( opcode | LONGFORM );
	o.writeShort(len+1);               // 2 bytes, sign bit = 0
	o.write((byte)slen);               // 1 byte
	o.write(b);                        // slen bytes
	if (even) o.write(0x0);            // 1 padding byte

      } else {
	error("len over max_short; multiple partitions not implemented",
	      "writeCommand");
      }

    } catch (IOException e) {
      System.err.println(e);
    }

  }

  /** write a command with an array of strings.  Applicable opcodes:
   * FontList_,
   *
   * Both short and long forms were tested. - YTLau
   *
   */
  private void writeCommand(short opcode, String[] sa) {
    int salen = sa.length;       // number of strings
    int slen[] = new int[salen]; // length of each string (<= 256)
    int tlen = 0;                // total length = sum of slen + salen

    for (int i=0; i<salen; i++) {
      slen[i] = sa[i].length();
      tlen += slen[i];
      if (slen[i] > 0xFF) error("String length too long (> 0xFF)", 
				"writeCommand");
    }
    tlen += salen;

    boolean odd = tlen % 2 != 0;
    if (odd) tlen++;         // tlen must be even to meet the 16 bit boundary

    try {
      // write the command.  tlen = number of octets (8-bit)
      if (tlen < 31) {                     // tlen must be < LONGFORM
	o.writeShort( opcode | tlen );     // 2 bytes

      } else if ( tlen < 32766 ) {          //  long form of command
	o.writeShort( opcode | LONGFORM );
	o.writeShort(tlen);                 // 2 bytes, sign bit = 0

      } else {
	error("len over max_short; multiple partitions not implemented",
	      "writeCommand");
      }

      byte[] b;
      for (int i=0; i<salen; i++) {
	b = sa[i].getBytes();              // extracts the bytes
 	o.write((byte)slen[i]);            // 1 byte
	o.write(b);                        // slen bytes
      }
      if (odd) o.write(0x0);               // 1 padding byte

    } catch (IOException e) {
      System.err.println(e);
    }

  }

  /** write a command with 3 short and 1 string parameters.  
   * Applicable opcodes are: Text_,
   */
  private void writeCommand(short opcode, short x, short y, short f, String s) {
    int len;
    int slen = s.length();     // string length
    boolean even = slen % 2 == 0;

    if (even) {
      len = slen + 1 + 6;    // len must be odd to meet the 16 bit boundary
    } else {
      len = slen + 6;        // the 6 is for the three shorts
    }
    byte[] b = s.getBytes();  // extracts the bytes

    try {
      // write the command.  len+1 = number of octets (8-bit)
      if (len < 30) {            // len+1 must be < LONGFORM
	o.writeShort( opcode | (len+1) );  // 2 bytes

      } else if ( len < 32766 ) {          //  long form of command
	o.writeShort( opcode | LONGFORM );
	o.writeShort(len+1);               // 2 bytes, sign bit = 0

      } else {
	error("len over max_short; multiple partitions not implemented",
	      "writeCommand");
      }

      o.writeShort(x);                // 2 bytes
      o.writeShort(y);                // 2 bytes
      o.writeShort(f);                // 2 bytes
      o.write((byte)slen);            // 1 byte
      o.write(b);                     // slen bytes
      if (even) o.write(0x0);         // 1 padding byte

    } catch (IOException e) {
      System.err.println(e);
    }

  }

  /** write a command with one short parameter.  Applicable opcodes
   * are: MFVersion_,
   */
  private void writeCommand(short opcode, short p) {
    try {
      o.writeShort( opcode | 0x0002 );  // 2 bytes
      o.writeShort(p);                  // 2 bytes
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  /** write a command with an array of short parameters.  
   * This is used for short form encoding only.  Applicable opcodes
   * are: CharOri_, VDCExt_,
   */
  private void writeCommand(short opcode, short[] p, int len) {
    if ( len >= LONGFORM ) error("over LONGFORM", "writeCommand");
    try {
      o.writeShort( opcode | (2 * len) );           // 2 bytes
      for (int i=0; i<len; i++) o.writeShort(p[i]); // 2*len bytes
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  /** write a command with an array of short parameters.  Short
   * or long form will be selected automatically.  Applicable opcodes
   * are: Polygon_, Line_,
   */
  private void writeCommand(short opcode, short[] x, short[] y) {
    int len4 = x.length * 4;

    try {
      if (len4 < 31) {                  // 4*len must be < LONGFORM
	o.writeShort( opcode | len4 );  // 2 bytes
	
      } else if ( len4 < 32766 ) {          //  long form of command
	o.writeShort( opcode | LONGFORM );
	o.writeShort(len4);                 // 2 bytes, sign bit = 0
      }

      for (int i=0; i<x.length; i++) {
	o.writeShort(x[i]);
	o.writeShort(y[i]);
      }

    } catch (IOException e) {
      System.err.println(e);
    }
  }

  /** Write color in RGB */
  private void writeColor(short opcode, byte red, byte green, byte blue) {
    try {
      o.writeShort( opcode | 0x0003 );
      o.write(red);
      o.write(green);
      o.write(blue);
      o.write(0x0);  // padding
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  /** Write color in integer (ARGB) */
  private void writeColor(short opcode, int icolor) {
    try {
      o.writeShort( opcode | 0x0003 );
      o.writeInt( icolor << 8 );  // left shift to the right place
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  //--------------------------------------------------------------

  //--------------------------------------------------------------
  void error(String msg, String src) {
    System.err.println( "CgmTool." + src + ": " + msg );
  }

}

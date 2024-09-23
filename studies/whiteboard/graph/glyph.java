//*************************************************************************
/*
 * Glyph.java - Define basic glyphs (line, rectangle, circle, oval) 
 *   and their methods.
 *
 *   This file should be under $CLASSPATH/graph
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package graph;

import java.awt.*;
import java.lang.Math;

// local class libraries
import graph.CgmTool;  // define cgm op-codes and methods

/** Defines some basic glyph types (line, rectangle, circle, oval).
 *
 * Design philosophy
 *
 * There are three types of glyphs:
 *   Drawable - those drawn by mouse motion
 *   Text - text labels
 *   Image - inset images
 *
 * One could put them into three classes, which are subclasses of a
 * parent glyph class.  However, we put all of them in the same glyph
 * class here because we may need to have glyphs that include multiple
 * elements (like the north arrow).  Thus we use a type parameter to
 * signify the glyph type.
 *
 * This class can be extended to include more complicated glyphs, like
 * curves, polygons.
 */
public class Glyph extends Object implements Cloneable {

  /** Graphic context */
  private Graphics g;

  /** Type of glyphs */
  protected byte type;

  /** Pixel coordinates defining the glyph.  
   * Should have length 2 for basic glyphs.
   *
   * X[2], Y[2] represent the bounding points, piont 0 and point 1.
   * LINE - the two end points.
   * RECT - Point 0 and 1 is the bounding vertices of the rectangle.
   * CIRCLE - X[0], Y[0] is the center.  The distance between point 0 and 1 is the radius.
   * OVAL - Point 0 and 1 is the bounding vertices of a rectangle containing the ellipse.
   * TEXT - Point 0 is the upper left corner of the text box.
   * CIRCULAR ARC - X[0], Y[0] is the center.  The distance between point 0 and 1 
   *                is the radius.  X[2] is the start angle. Y[2] is the delta angle.
   *
   * If only X[], Y[] are declared.  Then only the memory location is
   * preserved for different instances of Glyph.  That is, the value
   * of X, Y will be those from latest constructor.  Thus X = new
   * short[2]; etc have to be called in the constructor.  
   */
  protected short X[], Y[];

  /** Rectangle bounding the glyph.  The point r.x, r.y is upperleft corner.
   */
  protected Rectangle r = new Rectangle();

  /** Color of the glyph */
  protected Color color;

  /** integer representation of color (ARGB) */
  int icolor;

  /** Pixel line width */
  protected int lineWidth;

  /** A line of text (with parsing capability) */
  protected TextLine text;

  /** Text string of text (for GlyphPort) */
  String textstr;

  /** Font information of text (for GlyphPort) */
  String font;

  /** 1=plain, 2=italic; 3= */
  int fontstyle;
  int fontsize;

  /** A key for tree searching (not implemented) */
  protected int key;

  private int radius;  // used by CIRCLE only

  private int start_angle; // used by CIRCULAR ARC
  private int delta_angle; // used by CIRCULAR ARC

  private int close_style;

  // enumeration types for glyphs
  public static final byte LINE                 = 1;
  public static final byte RECT                 = 2;
  public static final byte CIRCLE               = 3;
  public static final byte OVAL                 = 4;
  public static final byte TEXT                 = 5;
  public static final byte POLYLINE             = 6;
  public static final byte POLYGON              = 7;
  public static final byte CROSSRECT            = 8;
  public static final byte CIRCULAR_ARC         = 9;
  public static final byte CIRCULAR_ARC_CLOSE   = 10;
  public static final byte ELLIPTICAL_ARC       = 11;
  public static final byte ELLIPTICAL_ARC_CLOSE = 12;
  public static final byte ALL                  = 13;
  public static final byte typeMAX = ALL - 1;

  /** Dummy Constructor */
  public Glyph() {
  }

  /** Clone Constructor */
  public Object clone() throws CloneNotSupportedException {
    Glyph gl = (Glyph) super.clone();
    int len = X.length;
    gl.X = new short[len];   System.arraycopy(X, 0, gl.X, 0, len);
    gl.Y = new short[len];   System.arraycopy(Y, 0, gl.Y, 0, len);
    gl.color = color;
    gl.r = new Rectangle(r.x, r.y, r.width, r.height);

    // No need to clone other primitive attributes since 
    // they are cloned by super.clone()

    return (gl);
  }
 
  /* 
  **  Constructor for a basic glyph.  It sets the bounding box and other
  **  properties.  Note that it still works when type = TEXT. 
  */

  public Glyph ( byte type, short x[], short y[], int lineWidth,
		     Color color, int key ) {
    short max_x, min_x, max_y, min_y;

    // Validate the glyph type.

    if ( type > typeMAX )
       System.err.println ( "Glyph: invalid input type!" );

    this.type = type;

    // Save the array of points

    X = new short[x.length];   System.arraycopy ( x, 0, X, 0, x.length );
    Y = new short[y.length];   System.arraycopy ( y, 0, Y, 0, y.length );

    // Save the line width

    this.lineWidth = lineWidth;
    if ( this.lineWidth == 0 ) this.lineWidth = 1;

    // Save the color.  icolor is an integer form of RGB (ARGB)

    this.color = color;
    this.icolor = color.getRGB();

    this.key = key;

    // For polylines and polygons, the bounding rectangle values
    // need to be based on the minimum/maximum X/Y values

    if ( ( type == POLYLINE ) || ( type == POLYGON ) ) {

       // Initialize the maximum and minimum values to the first point

       min_x = max_x = X[0];
       min_y = max_y = Y[0];

       // Find the minimum and maximum X and Y values

       for ( int i = 1; i < X.length; i++ ) {
         if ( X[i] > max_x ) max_x = X[i];
         if ( X[i] < min_x ) min_x = X[i];
         if ( Y[i] > max_y ) max_y = Y[i];
         if ( Y[i] < min_y ) min_y = Y[i];
       }

       // Set the bounding rectangle upper left coordinate,
       // width, and height based on these values

       r.x = min_x;  r.width = max_x - min_x;
       r.y = max_y;  r.height = max_y - min_y;
    }

    // For all other elements, set bounding rectangle 
    // based on the first two endpoints given

    else {
      if ( x[1] > x[0] ) {
        r.x = x[0];  r.width = (x[1] - x[0]);
      } else {
        r.x = x[1];  r.width = (x[0] - x[1]);
      }
      if ( y[1] > y[0] ) {
        r.y = y[0];  r.height = (y[1] - y[0]);
      } else {
        r.y = y[1];  r.height = (y[0] - y[1]);
      }
    }

    if ( ( type == CIRCLE ) || ( type == CIRCULAR_ARC ) || 
         ( type == CIRCULAR_ARC_CLOSE ) ) {
      radius = (int) Math.sqrt(r.width * r.width + r.height * r.height);
      r.x = X[0]-radius; r.y = Y[0]-radius;
      r.width = 2 * radius;   // for bounding box
      r.height = r.width;
    }

    // If this is a circular arc, then save the start and delta angles

    if ( ( type == CIRCULAR_ARC )   || ( type == CIRCULAR_ARC_CLOSE ) ||
         ( type == ELLIPTICAL_ARC ) || 
         ( type == ELLIPTICAL_ARC_CLOSE ) ) {
      start_angle = X[2];
      delta_angle = Y[2];
    }
  }

  /** Constructor for a text glyph */
  public Glyph (String s, Font f, short x[], short y[], 
		     Color color, int key) {

    this(TEXT, x, y, 0, color, key);

    /* Set the fields for GlyphPort */
    this.textstr = s;
    this.font = f.getName();
    this.fontstyle = f.getStyle();
    this.fontsize = f.getSize();

    text = new TextLine(s, f, color, -TextLine.LEFT);

    // parse it so width, height can be set.  
    // But g == null here, so it won't work!
    // text.parseText(g);   
   
  }

  /** Constructor for a filled elements */

  public Glyph ( byte type, short x[], short y[], int lineWidth,
		     Color color, int closeStyle, int key ) {
 
     // Fill in most of the Glyph data using the normal constructor
     this ( type, x, y, lineWidth, color, key );

     // Fill in the specialty data (i.e. the close type )
     this.close_style = closeStyle;
  }

  /** 
   * Set the attributes of a glyph 
   */
  public void setAttributes(Color color, int lineWidth, Font f) {
    this.color = color;
    this.icolor = color.getRGB();
    this.lineWidth = lineWidth;
    if (type == TEXT) {
      this.font = f.getName();
      this.fontstyle = f.getStyle();
      this.fontsize = f.getSize();
      text = new TextLine(textstr, f, color, -TextLine.LEFT);
    }
  }

  /** Get the type of glyph */
  public byte getType() {
    return type;
  }

  /** Set the color of glyph */
  public void setColor(Color color) {
    this.color = color;
  }

  /** Get the color of glyph */
  public Color getColor() {
    return color;
  }

  /** Set the lineWidth of glyph */
  public void setLineWidth(int lineWidth) {
    this.lineWidth = lineWidth;
  }

  /** Get the lineWidth of glyph */
  public int getLineWidth() {
    return lineWidth;
  }

  /** Set the key of glyph */
  public void setKey(int key) {
    this.key = key;
  }

  /** Get the key of glyph */
  public int getKey() {
    return key;
  }

  /** Shift the glyph by sx, sy. */
  public void shift (short sx, short sy) {

    for (int i=0; i < X.length; i++) {
      X[i] += sx; Y[i] += sy;
    }

    // set upper left corner of bounding rectangle
    r.x += sx;  r.y += sy;
  }

  /** Set the glyph to a new location with upper-left corner at (x,y). */
  public void setLocation(short x, short y) {
    shift((short)(x - r.x), (short)(y - r.y));
  }

  /** Draw the glyph onto the input graphic g */
  public void draw (Graphics g) {
    int i;

    if (this.g == null) this.g = g;

    switch(type) {
      case LINE:
	  G2dTool.drawLine(g, X[0], Y[0], X[1], Y[1], lineWidth, color);
	break;

      case RECT:
	  G2dTool.drawRect(g, r.x, r.y, r.width, r.height, lineWidth, color);
	break;

      case CIRCLE:
	  // center of circle is the start point X[0], Y[0]
	  G2dTool.drawCircle(g, X[0], Y[0], radius, lineWidth, color);
	break;

      case OVAL:
	  G2dTool.drawOval(g, r.x, r.y, r.width, r.height, lineWidth, color);
	break;

      case TEXT:
	  // text is null if only the bounding box is created.
	  if ( text == null ) {
	    G2dTool.drawRect ( g, r.x, r.y, r.width, r.height, 2, color );
	  } else {
	    text.draw ( g, r.x, r.y );
	    r.width = text.width;
	    r.height = text.height;
	  }
	break;

      case POLYLINE:
        for ( i = 0; i < X.length-1; i++ )
	     G2dTool.drawLine ( g, X[i], Y[i], X[i+1], Y[i+1], lineWidth,
                              color );
	break;

      case POLYGON:
        for ( i = 0; i < X.length-1; i++ )
	     G2dTool.drawLine ( g, X[i], Y[i], X[i+1], Y[i+1], lineWidth,
                              color );
	  G2dTool.drawLine ( g, X[i], Y[i], X[0], Y[0], lineWidth,
                              color );
	break;

      case CIRCULAR_ARC:
      case ELLIPTICAL_ARC:
 	  G2dTool.drawArc ( g, r.x, r.y, r.width, r.height, start_angle,
                          delta_angle, lineWidth, color );
	break;

      case CIRCULAR_ARC_CLOSE:
 	  G2dTool.fillArc ( g, r.x, r.y, r.width, r.height, start_angle,
                          delta_angle, color );
	break;

      case CROSSRECT:
	  // a rectangle with cross and inner rectangle
	  // for representing a moving image (or the whole canvas)
	  int w = 2 + r.width/10;
	  int h = 2 + r.height/10;
	  G2dTool.drawRect(g, r.x, r.y, r.width, r.height, lineWidth, color);
	  G2dTool.drawRect(g, (X[0]+X[1]-w)/2, (Y[0]+Y[1]-h)/2, 
			       w, h, 1 + lineWidth/2, color);
	  G2dTool.drawLine(g, X[0], Y[0], X[1], Y[1], lineWidth/2, color);
	  G2dTool.drawLine(g, X[0], Y[1], X[1], Y[0], lineWidth/2, color);
	break;
      default:
    }
  }

  /**
   * Draw a bounding pattern around the glyph. 
   */
  public void drawBound (Graphics g) {
    if (this.g == null) this.g = g;
    int d;
    int lineWidthB = Math.min(3, lineWidth);  // limit the width
    Color bcolor = Color.blue;
    // Be smart about the bounding box color
    if (Math.abs(icolor - bcolor.getRGB()) < 50 ) bcolor = Color.red;

    d = lineWidth / 2 + 2;
    G2dTool.drawRect(g, r.x-d, r.y-d, 
		     r.width+lineWidth+3, r.height+lineWidth+3, 
		     lineWidthB, bcolor);
  }

  /**
   * Finds out whether the point x,y intersects the glyph.  The mouse
   * picks a glyph by click at the glyph (not its bounding box).
   * Lots of geometric math are involved.  Hold your breath! 
   */
  public boolean intersect (short x, short y) {
    double r0, r1;
    double d;
    double x0, x1, y0, y1;

    switch(type) {
      case LINE:
	// length of line
	double l = Math.sqrt(r.width * r.width + r.height * r.height);
	// angle from x axis; note the reflection in y
	double costhe = (X[1]-X[0])/l;
	double sinthe = (-Y[1]+Y[0])/l;

	// rotate the current point relative to X[0],Y[0]
	double dx = x - X[0];
	double dy =-y + Y[0];
	double xp = dx*costhe + dy*sinthe;
	double yp =-dx*sinthe + dy*costhe;
	d = lineWidth / 2.0 + 4;     // tolerence
	if ( l + d > xp && xp > -d && Math.abs(yp) < d ) return true;
	break;

      case RECT:
	d = lineWidth / 2.0 + 2;     // tolerence
	// a slightly widen region
	x0 = r.x - d;
	x1 = r.x + r.width +d;
	y0 = r.y - d;
	y1 = r.y + r.height +d;

	if ( ( Math.abs(X[0]-x) < d || Math.abs(X[1]-x) < d ) && ( y0 < y && y < y1 ) || 
	     ( Math.abs(Y[0]-y) < d || Math.abs(Y[1]-y) < d ) && ( x0 < x && x < x1 ) )
	  return true;
	break;

      case CIRCLE:
	d = lineWidth + 2;     // tolerence
	// center of circle is the start point X[0], Y[0]
	r1 = Math.sqrt( (x-X[0])*(x-X[0]) + (y-Y[0])*(y-Y[0]) );
	if ( Math.abs(r1-radius) < d  ) return true;
	break;

      case OVAL:
	// Test by the function (dx/a)^2 + (dy/b)^2 for inner and outer rims
	x0 = (X[0]+X[1]) / 2.0;  // center of ellipse
	y0 = (Y[0]+Y[1]) / 2.0;
	double dx2 = (x-x0)*(x-x0);
	double dy2 = (y-y0)*(y-y0);
	d = lineWidth / 2.0 + 3;     // tolerence
	double Ap = (r.width / 2.0 + d);
	double Bp = (r.height / 2.0 + d);
	double Am = (r.width / 2.0 - d);
	double Bm = (r.height / 2.0 - d);
	if ( ( dx2/(Am*Am) + dy2/(Bm*Bm) ) > 1.0 &&
	     1.0 > ( dx2/(Ap*Ap) + dy2/(Bp*Bp) ) ) return true;
	break;

      case TEXT:
      case CROSSRECT:
      default:     // use bounding box to check
	d = lineWidth / 2.0 + 2;     // tolerence
	// a slightly widen region
	x0 = r.x - d;
	x1 = r.x + r.width +d;
	y0 = r.y - d;
	y1 = r.y + r.height +d;
	if ( ( x0 < x && x < x1 ) && ( y0 < y && y < y1 ) ) return true;
	break;

    }
    return false;
  }

  /** 
   * Convert the glyph to cgm using the input CgmTool.  After the
   * call, a series of bytes representing this glyph are inserted
   * to the output stream associated with the CgmTool.
   *
   * @param ct An instance of a CGM Tool for writing CGM data.
   */
  public void toCGM(CgmTool ct) {

    switch(type) {
      case LINE:
      case POLYLINE:
	  ct.LineWidth(lineWidth);
	  ct.LineColr(icolor);
	  ct.Line(X, Y);
	break;

      case RECT:
	  ct.IntStyle(CgmTool.EMPTY);
	  ct.EdgeType(CgmTool.SOLID);
	  ct.EdgeColr(icolor);
	  ct.EdgeWidth(lineWidth);
	  ct.EdgeVis(1);
	  ct.Rect(X, Y);
	break;

      case CIRCLE:
	  ct.IntStyle(CgmTool.EMPTY);
	  ct.EdgeType(CgmTool.SOLID);
	  ct.EdgeColr(icolor);
	  ct.EdgeWidth(lineWidth);
	  ct.EdgeVis(1);
	  ct.Circle(X[0], Y[0], (short)radius);
	break;

      case OVAL:
	  ct.IntStyle(CgmTool.EMPTY);
	  ct.EdgeType(CgmTool.SOLID);
	  ct.EdgeColr(icolor);
	  ct.EdgeWidth(lineWidth);
	  ct.EdgeVis(1);
	  ct.Ellipse(X, Y);
	break;

      case POLYGON:
	  ct.LineWidth(lineWidth);
	  ct.LineColr(icolor);
	  ct.Polygon(X, Y);
	break;

      case TEXT:
	  ct.TextColr(icolor);
	  ct.CharOri();
	  ct.TextFontIndex(font, fontstyle);
	  ct.CharHeight(fontsize);
	  ct.Text(X[0], Y[0], textstr);
	break;

      case ELLIPTICAL_ARC:
        short centerx, centery;
        short con1x, con1y, con2x, con2y;

        centerx = (short) ( 0.5 * ( X[0] + X[1] ) );
        centery = (short) ( 0.5 * ( Y[0] + Y[1] ) );

        con1x = (short) ( Math.abs ( X[0] - centerx ) + centerx );
        con1y = centery;

        con2x = centerx;
        con2y = (short) ( Math.abs ( Y[0] - centery ) + centery );

	ct.LineWidth ( lineWidth );
	ct.LineColr ( icolor );
        ct.EllipArc ( centerx, centery, con1x, con1y, con2x, con2y,
                      (short) 7, (short) 8,
                      (short) 9, (short) 10 );
      break;

      case CIRCULAR_ARC:
      case CIRCULAR_ARC_CLOSE:

        double start_angle_rad, end_angle_rad;

        // First, convert the start and end angles to radians

        start_angle_rad = start_angle * ( Math.PI / 180.0 );
        end_angle_rad = ( start_angle + delta_angle ) *
                        ( Math.PI / 180.0 );

        // Find the X/Y coordinate on the circular
        // arc for the start angle

        short startx = (short) ( X[0] + ( Math.cos ( start_angle_rad )
                                 * radius ) );
        short starty = (short) ( Y[0] + ( Math.sin ( start_angle_rad )
                                 * radius ) );

        // Find the X/Y coordinate on the circular
        // arc for the end angle

        short endx = (short) ( X[0] + ( Math.cos ( end_angle_rad ) 
                               * radius ) );
        short endy = (short) ( Y[0] + ( Math.sin ( end_angle_rad ) 
                               * radius ) );

        // Make the line width, color, and arc entries in the CGM buffer

        if ( type == CIRCULAR_ARC ) {
	     ct.LineWidth ( lineWidth );
	     ct.LineColr ( icolor );
	     ct.ArcCtr ( X[0], Y[0], startx, starty, endx, endy,
                      (short) radius );
        }
        else {
	     ct.EdgeWidth ( lineWidth );
	     ct.EdgeType ( CgmTool.SOLID );
	     ct.EdgeColr ( icolor );
	     ct.ArcCtrClose ( X[0], Y[0], startx, starty, endx, endy,
                           (short) radius, (short) close_style );
        }
	break;

      default:
    }

  }

  /** Show the contents of a glyph. */
  public void show () {
    System.out.println( "Glyph type = " + type ); 

    for ( int i = 0 ; i < X.length; i++ )
       System.out.println ( " X[" + i + "] = " + X[i] + "  Y[" + i + "] = " + Y[i]  );

    System.out.println(" lineWidth = " + lineWidth );
    System.out.println(" color = " + color );
    System.out.println(" close style = " + close_style );

    if ( ( type == CIRCULAR_ARC ) || ( type == CIRCULAR_ARC_CLOSE ) ) {
      System.out.println(" start angle = " + start_angle );
      System.out.println(" delta angle = " + delta_angle );
    }

    if (text != null) {
      System.out.println( " text = " + textstr );
      System.out.println( " font = " + font );
      System.out.println( " fontstyle = " + fontstyle );
      System.out.println( " fontsize = " + fontsize );
    }

    System.out.println(" key = " + key );
  }

}


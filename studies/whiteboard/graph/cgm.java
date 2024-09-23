//*************************************************************************
/*
 * Cgm.java - A class for conversion between glyphs and CGM data.
 *
 *   This file should be under $CLASSPATH/graph
 *
 * Description:
 *   CGM (Computer Graphics Metafile) class handles conversion of glyphs 
 *   to the cgm buffer and interpretation of cgm codes into glyphs.  It 
 *   uses methods in CgmTool.java.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package graph;

// standard java class libraries
import java.awt.*;
import java.util.*;       // use Vector
import java.io.*;

// local class libraries
import graph.Glyph;     // define basic glyphs 
import graph.CgmTool;   // define cgm op-codes and methods

/** CGM (Computer Graphics Metafile) class handles conversion of glyphs 
 *  to the cgm buffer and interpretation of cgm codes into glyphs.
 *
 *  Example of usage:
 *  <PRE>
 *    From glyphs to cgm -
 * 1)  Cgm cgm = new Cgm(glyphs);  // create and initialize cgm
 *     cgm.write("tem.cgm");      
 *       or
 *     buffer = cgm.getBuf();      // retrieve the cgm buffer
 *     (note: may do other cgm.add(glyph);  before write or getBuf.)
 *      
 * 2)  Cgm cgm = new Cgm();  // create and initialize cgm
 *     cgm.add(glyphs);
 *     buffer = cgm.getBuf();      // retrieve the cgm buffer
 *
 *    From cgm to glyphs -
 * 1)  Cgm cgm = new Cgm(byte[] cgm_buffer);  
 *     glyphs = cgm.getGlyphs();   // get glyphs
 *
 * 2)  Cgm cgm = new Cgm();  // create and initialize cgm
 *     cgm.read("tem.cgm");  // read from file and set cgm buffer
 *                           // it automatically calls interp 
 *                           // and add to glyphs
 *     glyphs = cgm.getGlyphs();   // get glyphs
 *
 * 3)  Cgm cgm = new Cgm();  // create and initialize cgm
 *     cgm.setBuf(cgm_buffer);  // set cgm_buffer & add to glyphs
 *     glyphs = cgm.getGlyphs();   // get glyphs
 * </PRE>
 * Please also see the main method at the end of this class.
 */
public class Cgm {

  // The current version of this tool
  public static final String VERSION = "V. 1.0";

  /** An internal byte array for writing cgm. */
  private ByteArrayOutputStream baos;

  /** A set of cgm encoding functions */
  private CgmTool ct;

  /** state variable */
  private byte state;

  // enumeration types for states
  /** Null state - idle */
  public static final byte NULL = 0;

  /** Positive states (used when converting glyphs to cgm codes):
    1 = meta file opened, 2 = closed. */
  public static final byte MF_OPEN = 1;
  public static final byte MF_ENDED = 2;

  /** Negative states (used when interpreting cgm to glyphs):
    -1 = interpreting cgm, -2 = completed interpreting all glyphs. */
  public static final byte INTERPRETING = -1;
  public static final byte INTERP_DONE = -2;

  /** Buffer containg cgm code (cgm buffer) for interpretation. */
  protected byte[] buf;

  /** A bag of glyphs (with a capacity of 10 initially). */
  protected Vector glyphs = new Vector(10);

  // Pointer into CGM buffer
  private int next = 0;

  // Class and identifier for CGM command
  private short class_id;

  // CGM command data length
  private int length;

  // Buffer containing CGM command data
  private byte[] data;

  // Length of CGM buffer
  private int buffer_size;

  double start_angle, end_angle;

  /** Basic constructor. */
  public Cgm() {
    state = NULL;
  }

  /** Constructor that adds a bag of glyphs and converts them.
    Note that it does not ends the meta file so that other
    add() can be called before write() or getBuf(). */
  public Cgm(Vector glyphs) {
    this();   // call basic constructor
    state = MF_OPEN;
    baos = new ByteArrayOutputStream(256);  /* initialize with a size.  The actual
					     * size is set automatically at writing.
					     */
    // Initialize a NITF CGM
    ct = new CgmTool(baos);
    ct.BegMF("Whiteboard CGM");
    ct.MFVersion(1);            // must use 1
    ct.MFDesc("NITF/CGM-APP-2.0.  Generated by CgmTool " + VERSION);
    // write standard font list of NITF-CGM
    //    ct.FontList(CgmTool.NITF_CGM_fontlist);

    ct.BegPic("Whiteboard Glyphs");
    ct.VDCExt(0, 480, 640, 0);  // set VDC extent
    ct.ColrMode(1);             // set modes to NITF-CGM defaults
    ct.EdgeWidthMode(0);
    ct.LineWidthMode(0);

    ct.BegPicBody();

    // put all glyphs in the same picture
    add(glyphs);

    // do not end it yet so other glyphs may be added 
  }

  /** Constructor that sets the cgm buffer to buf. 
   The "buf" should contain a complete cgm code. */
  public Cgm(byte[] buf) {
    this();   // call basic constructor
    state = INTERPRETING;
    setBuf(buf);
  }

  //------------------- Methods for writing to cgm buffer ---------------------

  /** Converts one glyph to cgm code and adds it to the buffer. */
  public void add(Glyph glyph) {
    glyph.toCGM(ct);
    glyphs.addElement(glyph);
  }

  /** Converts an array of glyphs to cgm codes and adds them to the buffer. */
  public void add(Glyph[] glyphsIn) {
    if (state != MF_OPEN) error("Meta file not open", "Cgm.add");
    for (int i=0; i<glyphsIn.length; i++) {
      add( glyphsIn[i] );
    }
  }

  /** Converts a bag of glyphs to cgm codes and adds them to the buffer. */
  public void add(Vector glyphsIn) {
    if (state != MF_OPEN) error("Meta file not open", "Cgm.add");
    for (int i=0; i<glyphsIn.size(); i++) {
      add( (Glyph) glyphsIn.elementAt(i) );
    }
  }

  /** end the cgm buffer. */
  public void end() {
    if (state != MF_OPEN) error("Meta file not open", "Cgm.end");

    // write end of meta file
    ct.EndPic();
    ct.EndMF();

    state = MF_ENDED;
  }

  /** Get the cgm byte buffer from the byte stream. */
  public byte[] getBuf() {
    // end it first if the meta file buffer is completed
    if (state != MF_ENDED) end();
    if (state != MF_ENDED) error("Meta file not completed", "Cgm.getBuf");

    // debug("baos.size() = " + baos.size());

    buf = baos.toByteArray();
    return buf;
  }

  //------------------- End of methods for writing to cgm buffer ----------------

  //---------------- Methods for reading from cgm buffer -----------------
  /** Sets the cgm buffer and invokes the interpreter. */
  public void setBuf(byte[] buf) {
    int ret;

    this.buf = buf;

    state = INTERPRETING;   // set the state
    ret = interp();
    if (ret != 0) error("error return from interp", "Cgm.setBuf");
  }

  /*
  ** get_header - retrieves the CGM class, identifier, and
  **              command length from the CGM buffer
  */
  private void get_header() {

     // Retrieve the class, id, and length

     class_id = (short) ( (buf[next] << 8) | (0xE0 & buf[next+1]) );
     length = (int) ( buf[next+1] & 31 );
     next = next + 2;

     // If the length is equal to 31, then the command length is
     // stored in the next two byte of the buffer.

     if ( length == 31 ) {
        length = (short) ( ( ( 0x7f & buf[next] ) << 8 ) | buf[next+1] );
        next = next + 2;
     } 
  }

  /*
  ** get_command_data - retrieves the CGM command data from
  **                    the CGM buffer
  */
  private void get_command_data() {
     int i;
     byte test[] = new byte[length+1];

     // Read the command data

     for ( i = 0; i < length; i++ )
        test[i] = buf[next++];

     data = test;

     // If the data ends on an odd byte boundary, then read the
     // next byte.  This is done because the command data always
     // starts on an even byte boundary.

     boolean even = next % 2 == 0;
     if ( !even )
        data[i] = buf[next++];
  }

  /*
  ** get_short - read and return an short value from the
  **             input array starting at the input offset.
  */
  private short get_short ( byte[] b, int array_offset ) {
     short value;

     value = (short) ( ( 255 & b[array_offset] ) << 8 );
     value = (short) ( value + ( b[array_offset+1] & 255 ) );
     return value;
  }

  /*
  ** read_points - read the input number of X/Y coordinate points
  **               from the input array.  Return the coordinate
  **               pairs in the supplied arrays.
  */
  private void read_points ( byte[] pt_buf, short[] x_array, 
                             short[] y_array, int num ) {
     int i = 0;
     int j;
     short value;

     // Loop for the specified number of points reading the X and
     // Y values and storing them in the supplied arrays

     for ( j = 0; j < num; j++ ) {
        value = (short) ( ( 255 & pt_buf[i++] ) << 8 );
        value = (short) ( value + ( pt_buf[i++] & 255 ) );
        x_array[j] = value;

        value = (short) ( ( 255 & pt_buf[i++] ) << 8 );
        value = (short) ( value + ( pt_buf[i++] & 255 ) );
        y_array[j] = value;
     }
  }

  /*
  ** read_color - read a red/green/blue color value from the supplied
  **              buffer, create a new instance of the Color class
  **              using these values, and return the class instance.
  */

  private Color read_color ( byte[] color_buf ) {
     int   red;
     int   green;
     int   blue;
     Color color;

     // Read the colors from the buffer

     red = (int) ( 255 & color_buf[0] );
     green = (int) ( 255 & color_buf[1] );
     blue = (int) ( 255 & color_buf[2] );

     // Create and return the class instance

     color = new Color ( red, green, blue );
     return ( color );
  }

  private void FindArcAngles ( int xstart, int ystart, int xend, int yend )
  {
     double theta1, theta2;
     double temp_angle1, temp_angle2;

     temp_angle1 = FindAngle ( 0, 0, xstart, ystart );
     temp_angle2 = FindAngle ( 0, 0, xend, yend );

debug ( "...int FindArcAngle, temp_angle1 = " + temp_angle1 );
debug ( "...int FindArcAngle, temp_angle2 = " + temp_angle2 );
/*
     end_angle = temp_angle1 - temp_angle2;
     start_angle = temp_angle2;

     if ( end_angle <= 0 )
        end_angle += 360;
*/
     start_angle = temp_angle2;
     end_angle = temp_angle1;

     if ( start_angle == 360 )
        start_angle = 0;
  }

  private double FindAngle ( int xs, int ys, int xe, int ye )
  {
     int     x, y;
     double  theta, angle;
     int     quad = 1;

     x = xe - xs;
     y = ye - ys;

     /*
     ** Determine first where the start and end vectors are.
     ** Quadrant are assumed + clockwise, 1, 2, 3, 4, upper right one is 1
     ** We need to do that, so we can deterine the correct angles for
     ** the arc
     */

     /*
     ** Start Vector
     */

     if ( ( (x) < 0 ) && ( (y) > 0 ) )
        quad = 3;

     else if ( ( (x) <= 0 ) && ( (y) <= 0 ) )
        quad = 4;

     else if ( ( (x) > 0 ) && ( (y) < 0 ) )
        quad = 1;

     else if ( ( (x) >= 0 ) && ( (y) >= 0 ) )
        quad = 2;

     if ( (double)0 != (double)x )
        theta = Math.atan ( (double)y / (double)x );
     else
        theta = Math.atan ( (double)y / 0.00001 );

     if ( theta < (double) 0.0 )
        theta = -theta;

     angle = (double) ( theta * 180.0 / Math.PI );

     switch ( quad )
     {
        case 1:
	  break;
        case 2:
           angle = (double)360.0 - angle;
        break;
        case 3:
           angle = (double)180.0 + angle;
        break;
        case 4:
           angle = (double)180.0 - angle;
        break;
     }
     return ( angle );
  }

  /*
  ** interp - Interpret the CGM buffer and create glyphs
  **          glyphs for the specified graphics elements. 
  */
  private int interp() {
     int     i;
     short   radius;
     byte    flag;
     String  text_string;
     byte[]  temp = new byte[1];
     Glyph   element;
     short   ellp_x[] = new short[2];
     short   ellp_y[] = new short[2];
     short   line_type = 1;
     short   line_width = 3;
     Color   line_color = new Color (255, 0, 0);
     Color   fill_color;
     short   interior_style;
     short   edge_visibility;
     short   edge_type;
     Color   edge_color = new Color (255, 0, 0);
     short   edge_width = 3;
     short   font_index;
     short   close_type;
     String  font_name = new String ( "TimesRoman" );
     int     font_style = Font.BOLD;
     Font    text_font = new Font ( "TimesRoman", Font.BOLD, 16);
     Color   text_color = new Color (255, 0, 0);
     int     character_height = 16;
     short   x[] = new short[3];
     short   y[] = new short[3];
     int     key = 1;
     int     num_points;

     // Loop while there is still data in the buffer

     next = 0;
     while ( next < buf.length ) {

        // Get the header associated with the CGM command

        get_header();

        // Get the data associated with the command

        get_command_data();

        // Perform some action based on the command

        switch ( class_id ) {

           // Delimiters.  Just recognize the command;
           // don't do anything with the command data.

           case CgmTool.BegMF_:
           break;

           case CgmTool.EndMF_:
           break;

           case CgmTool.BegPic_:
           break;

           case CgmTool.BegPicBody_:
           break;

           case CgmTool.EndPic_:
           break;

           // Metafile Descriptors.  Just recognize the command;
           // don't do anything with the command data.

           case CgmTool.MFVersion_:
           break;

           case CgmTool.MFDesc_:
           break;

           case CgmTool.MFElemList_:
           break;

           case CgmTool.BegMFDefaults_:
           break;

           case CgmTool.FontList_:
           break;

           // Picture Descriptors.  Just recognize the command;
           // don't do anything with the command data.

           case CgmTool.ColrMode_:
           break;

           case CgmTool.LineWidthMode_:
           break;

           case CgmTool.MarkerSizeMode_:
           break;

           case CgmTool.EdgeWidthMode_:
           break;

           case CgmTool.VDCExt_:
           break;

           // Drawing Primitives.  For each graphics element, read the
           // corresponding data, create a new glyph for the element,
           // and add the glyph to the vector.

           case CgmTool.Line_:
              num_points = length / 4;
              short line_x[] = new short[num_points];
              short line_y[] = new short[num_points];
              read_points ( data, line_x, line_y, num_points );

              if ( num_points == 2 ) 
                 flag = Glyph.LINE;
              else
                 flag = Glyph.POLYLINE;

              glyphs.addElement( new Glyph ( flag, line_x, line_y,
                                             line_width, line_color,
                                             key++ ) );
debug ( "Read Line/Polyline Command" );
           break;

           case CgmTool.Text_:

              // Read the location and flag

              read_points ( data, x, y, 1 );
              flag = data[5];

              // The CGM formats gives the point as the lower left hand
              // corner of the text string.  The glyph class wants the
              // upper left hand corner.  Subtract the character height
              // to give you the uper left hand corner.

              y[0] = (short) ( ( y[0] - character_height ) - 5 );

	        // The first pair should be upper left corner.
	        // Force the second pair to be the same. - YTLau

	        x[1] = x[0];    y[1] = y[0];

              // Read the text string to be displayed

              text_string = "";
              for ( i = 7; i < data[6] + 7; i++ ) {
                 temp[0] = data[i];
                 text_string = text_string + new String ( temp );
              }

              glyphs.addElement ( new Glyph ( text_string, text_font, x,
                                              y, text_color, key++ ) );
debug ( "Read Text Command" );
           break;

           case CgmTool.Rect_:
              read_points ( data, x, y, 2 );
              glyphs.addElement ( new Glyph( Glyph.RECT, x, y,
                                             edge_width, edge_color, 
                                             key++ ) );
debug ( "Read Rectangle Command" );
           break;

           case CgmTool.Circle_:
              read_points ( data, x, y, 1 );
              radius = get_short ( data, 4 );

              // The Glyph class expects a two points with the distance
              // between the two being the radius.  Create the second
              // point.

              x[1] = (short) ( x[0] + radius );
              y[1] = y[0];

              glyphs.addElement ( new Glyph ( Glyph.CIRCLE, x, y,
                                              edge_width, edge_color,
                                              key ++) );
debug ( "Read Circle Command" );
           break;

           case CgmTool.Ellipse_:
              read_points ( data, x, y, 3 );

              // The Glyph class expects two points which represent the
              // bounding box that contains the ellipse.  Create these
              // two points.

              ellp_x[0] = x[1];
              ellp_y[0] = y[2];
              ellp_x[1] = (short) ( x[0] - ( x[1] - x[0] ) );
              ellp_y[1] = (short) ( y[0] - ( y[2] - y[0] ) );

              glyphs.addElement ( new Glyph ( Glyph.OVAL, ellp_x,
                                              ellp_y, edge_width, 
                                              edge_color, key++ ) );
debug ( "Read Ellipse Command" );
          break;

           case CgmTool.Polygon_:
              num_points = length / 4;
              short poly_x[] = new short[num_points];
              short poly_y[] = new short[num_points];
              read_points ( data, poly_x, poly_y, num_points );

              glyphs.addElement( new Glyph ( Glyph.POLYGON, poly_x,
                                             poly_y, edge_width,
                                             edge_color, key++ ) );
debug ( "Read Polygon Command" );
           break;

           case CgmTool.ArcCtr_:
           case CgmTool.ArcCtrClose_:

              // Read the center point, start and end
              // vectors and radius from the CGM buffer

              read_points ( data, x, y, 1 );
              x[1] = get_short ( data, 4 );
              y[1] = get_short ( data, 6 );
              x[2] = get_short ( data, 8 );
              y[2] = get_short ( data, 10 );
              radius = get_short ( data, 12 ) ;

              // Convert the start and end vectors to angles

              FindArcAngles ( x[1], y[1], x[2], y[2] );

              // If the start angle is bigger than the end angle, then
              // the arc passes through 360.  Add 360 to the end angle
              // so that the delta angle calculation is correct.

              if ( start_angle > end_angle )
                 end_angle = end_angle + 360;

              // Set the bounding box for the arc and the start and
              // delta angles for the glyph.  Then, create the glyph
              // depending on whether an open or closed arc is being
              // created.

              x[1] = (short) (x[0] + radius);
              y[1] = y[0];
              x[2] = (short) (start_angle);
              y[2] = (short) (end_angle - start_angle);

              if ( class_id == CgmTool.ArcCtr_ ) {
                 glyphs.addElement ( new Glyph ( Glyph.CIRCULAR_ARC, x,
                                                 y, line_width,
                                                 line_color, key++ ) );
debug ( "Read Circular Arc Command" );
              }
              else {
                 close_type = get_short ( data, 14 );
                 glyphs.addElement ( new Glyph ( Glyph.CIRCULAR_ARC_CLOSE,
                                                 x, y, line_width,
                                                 line_color, 
                                                 close_type, key++ ) );
debug ( "Read Closed Circular Arc Command" );
              }

           break;

           case CgmTool.EllipArc_:
           case CgmTool.EllipArcClose_:

              read_points ( data, x, y, 3 );

              // The Glyph class expects two points which represent the
              // bounding box that contains the ellipse.  Create these
              // two points.

              ellp_x[0] = x[1];
              ellp_y[0] = y[2];
              ellp_x[1] = (short) ( x[0] - ( x[1] - x[0] ) );
              ellp_y[1] = (short) ( y[0] - ( y[2] - y[0] ) );

x[2] = 45;  y[2] = 90;
line_width = 3;
line_color = Color.red;
                glyphs.addElement( new Glyph ( Glyph.ELLIPTICAL_ARC,
                                               x, y, line_width,
                                               line_color, 
                                               key++ ) );

           // Drawing Attributes.  Read and save the data because it
           // will be used when creating glyphs for the graphics 
           // elements.

           case CgmTool.LineType_:
              line_type = data[1];
debug ( "Read Line Type Command" );
           break;

           case CgmTool.LineWidth_:
              line_width = data[1];
debug ( "Read Line Width Command" );
           break;

           case CgmTool.LineColr_:
              line_color = read_color ( data );
debug ( "Read Line Color Command...color = " + line_color );
           break;

           case CgmTool.TextFontIndex_:
              font_index = data[1];

              // The font index contains both font name and style
              // information.  Determine the font name first.

              if ( font_index < 5 )
                 font_name = new String ( "TimesRoman" );
              else if ( font_index < 9 )
                 font_name = new String ( "Helvetica" );
              else
                 font_name = new String ( "Courier" );

              // Now determine the font style

              if ( ( font_index == 1 ) || ( font_index == 5 ) ||
                   ( font_index == 9 ) )
                 font_style = Font.PLAIN;
              else if ( ( font_index == 2 ) || ( font_index == 6 ) ||
                        ( font_index == 10 ) )
                 font_style = Font.ITALIC;
              else if ( ( font_index == 3 ) || ( font_index == 7 ) ||
                        ( font_index == 11 ) )
                 font_style = Font.BOLD;
              else
                 font_style = Font.ITALIC | Font.BOLD;

              text_font = new Font ( font_name, font_style,
                                     character_height );
debug ( "Read Text Font Index Command" );
           break;

           case CgmTool.TextColr_:
              text_color = read_color ( data );
debug ( "Read Text Color Command" );
           break;

           case CgmTool.CharHeight_:
              character_height = data[1];
              text_font = new Font ( font_name, font_style, character_height );
debug ( "Read Character Height Command...height = " + character_height );
           break;

           case CgmTool.CharOri_:
              read_points ( data, x, y, 2 );
debug ( "Read Character Orientation Command" );
           break;

           case CgmTool.IntStyle_:
              interior_style = data[1];
debug ( "Read Interior Style Command...style = " + interior_style );
           break;

           case CgmTool.FillColr_:
              fill_color = read_color ( data );
debug ( "Read Fill Color Command" );
           break;

           case CgmTool.EdgeType_:
              edge_type = data[1];
debug ( "Read Edge Type Command...type = " + edge_type );
           break;

           case CgmTool.EdgeWidth_:
              edge_width = data[1];
debug ( "Read Edge Width Command...width = " + edge_width );
           break;

           case CgmTool.EdgeColr_:
              edge_color = read_color ( data );
debug ( "Read Edge Color Command...color = " + edge_color );
           break;

           case CgmTool.EdgeVis_:
              edge_visibility = data[1];
debug ( "Read Edge Visibility Command...visibility = " + edge_visibility);
           break;

           default:
              error ( "Read unknown class and id:" + class_id, "Cgm.interp" );
           break;
        }
     }

    return 0;
  }

  /** Returns the bag of glyphs */
  public Vector getGlyphs() {
    return glyphs;
  }

  //------------- End of methods for reading from cgm buffer ----------------


  //------------------- File in/out for testing ---------------------
  /** Write cgm byte stream to a file. */
  public int write (String file) {
    if (state >= 0) {  // ignore if interpreting
      getBuf();
    }

    try {
      FileOutputStream fo = new FileOutputStream(file);
      fo.write(buf, 0, buf.length);
      fo.close();
    } catch (IOException ex) {
      error(ex.getMessage(), "Cgm.write");
      return -1;
    }
    return 0;
  }

  /** Read from a cgm file and send the data to buf. */

  public int read (String file) {
    File f = new File(file);
    int size = (int) f.length();
    int bytes_read = 0;
    byte read_buf[] = new byte[size];

    int i = 0;

    try {
      FileInputStream in = new FileInputStream(f);

      while ( bytes_read < size ) {
        bytes_read += in.read ( read_buf, bytes_read, size - bytes_read );
      }
    } catch ( IOException e ) {
       System.out.println(e);
    }

    setBuf ( read_buf );
    return 0;
  }

  public int read2 (String file) {
    InputStream cgmFile;
    byte        read_buf[] = new byte[1];
    int         bytes_read;
    byte        buf[] = new byte[1024];
    int i = 0;

    try {
      FileInputStream source = new FileInputStream ( file );
      cgmFile = new DataInputStream ( source );

      while ( true ) {
        bytes_read = cgmFile.read ( read_buf );
        if (bytes_read == -1 ) 
           break;
        else
           buf [i++] = read_buf[0];
      }
     buffer_size = i;
    }

    catch ( IOException e ) {
       System.out.println(e);
    }

    setBuf ( buf );
    return 0;
  }

  //--------------------------------------------------------------
  void error(String msg, String src) {
    System.err.println( src + ": " + msg );
  }

  //--------------------------------------------------------------
  void debug(String msg) {
//    System.out.println( msg );
  }

  // ............................................ main for testing
  public static void main(String args[]) {

    byte type;
    short X[] = new short[3];
    short Y[] = new short[3];
    int lineWidth;
    int closeStyle;
    Color color;
    Font font = new Font("Courier", Font.ITALIC, 16); 

    Vector glyphs = new Vector(2);

//    type = Glyph.CIRCLE;
//    X[0] = 0; X[1] = 100;
//    Y[0] = 00; Y[1] = 0;
//    lineWidth = 3;
//    color = Color.red;
//    glyphs.addElement(new Glyph(type, X, Y, lineWidth, color, 0));

//    type = Glyph.TEXT;
//    X[0] = 56; X[1] = 78;
//    Y[0] = 87; Y[1] = 65;
//    lineWidth = 0;
//    color = Color.green;
//    glyphs.addElement(new Glyph("Second", font, X, Y, color, 0));

    type = Glyph.ELLIPTICAL_ARC;
    X[0] = 200; Y[0] = 160;
    X[1] = 0; Y[1] = 60;
    X[2] = 45; Y[2] = 90;
    lineWidth = 3;
    color = Color.red;
    glyphs.addElement(new Glyph(type, X, Y, lineWidth, color, 0 ) );

    type = Glyph.OVAL;
    X[0] = 200; Y[0] = 150;
    X[1] = 0; Y[1] = 50;
    lineWidth = 3;
    color = Color.green;
    glyphs.addElement ( new Glyph(type, X, Y, lineWidth, color, 1 ) );

    System.out.println("Created " + glyphs.size() + " glyphs.");
    System.out.println( " " );
/*
    for (int i=0; i<glyphs.size(); i++) {
      ( (Glyph)glyphs.elementAt(i) ).show();
      System.out.println ( " " );
    }
*/
    Cgm cgm = new Cgm(glyphs);  // create and initialize cgm
    cgm.write("tem.cgm");

    System.out.println(".........Done with cgm.write.");

    cgm.glyphs.removeAllElements();   // clear it first

    cgm = new Cgm();
    cgm.read("tem.cgm");  // read it back as a loop test.
                          // it automatically call interp 
                          // and set the cgm buffer
    
    glyphs = cgm.getGlyphs();

    System.out.println("Read back " + glyphs.size() + " glyphs.");

    for (int i=0; i<glyphs.size(); i++) {
      ( (Glyph)glyphs.elementAt(i) ).show();
    }

    System.exit(0);  // won't exit unless this is called?
  }

}

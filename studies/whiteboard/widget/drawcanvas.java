//*************************************************************************
/*
 * DrawCanvas.java - A subclass of Canvas with interactive drawing tools.
 *
 *   This file should be under $CLASSPATH/widget
 *
 * Comments:
 *   Warning - the mouse event handling methods and the paint method
 *   work together very subtly.  If you want to make changes, save 
 *   the current source code, do one thing at a time and test the result 
 *   thoroughly before going further.  This avoids a lot of trouble.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package widget;  

// standard java class libraries
import java.awt.*;
import java.awt.event.*;  // for mouse event processing
import java.util.*;       // use Vector
import java.awt.image.*;  // for PixelGrabber, ColorModel

// local class libraries
import graph.G2dTool;   // 2d graphic tools for drawing circles, lines, etc.
import graph.Glyph;     // define basic glyphs 
import graph.Cgm;       // CGM utility
import port.ImagePort;  // for image conversion and external links

// ----------------------------------------------------------------------

/** 
 *  A subclass of Canvas with interactive drawing tools.  It also
 *  manages the collection of small graphics (glyphs).  
 *
 *  It is an ActionListener for signals from TextField.
 */
public class DrawCanvas extends Canvas implements ActionListener {

  public static int mDebug = 0;  // show debug messages if > 0

  public static final int GLYPHS = -1; // All glyphs on canvas
  public static final int ALL = -2;    // All things on canvas

  // Set this to false if you don't want realtime drawing effect.
  // This may be needed on Sun Openwin.
  private boolean realtime = true;

  private int sx, sy;  // shift in x, y

  private Image img;
  public Graphics gr;  /** The graphic handle. */

  private Image img2;  // an off screen image (for double buffering)
  private Graphics g2; // its handle

  /** Indicates whether the image is new for the ImagePort */
  private boolean mNewImage = true;

  private Glyph oldGlyph, newGlyph;   

  /** The selected Glyph.  The one last drawn becomes a selected Glyph 
    by default.  It can also be selected by clicking.  When the mouse
    is being moved, theGlyph contains the current Glyph at current 
    position. 
    */
  private Glyph theGlyph;

  private int theKey;    // the nonzero key for the selected glyph

  /** A copy of a glyph. */
  private Glyph copyGlyph;

  /** Indicate whether theGlyph is being moved. */
  private boolean moving;

  /** Indicate whether the mouse is being dragged. */
  private boolean dragging;

  // point position on parent component
  private Point pp;

  // position of mouse pressed
  private int x0, y0;

  /** Background Color */
  private Color bgColor;

  ColorModel cm = ColorModel.getRGBdefault();

  /** Type of drawing glyphs */
  protected byte type = Glyph.CIRCLE;

  /** Pixel coordinates defining the glyph.  
    Should have length 2 for basic glyphs.
    X[0], Y[0] define the start (previous) point. 
    X[1], Y[1] define the end (current) point. 
    */
  protected short X[], Y[];

  /** Color of the glyph */
  protected Color color = Color.red;

  /** Pixel line width */
  protected int lineWidth = 3;

  /** Font for labels */
  protected Font font = new Font("TimesRoman", Font.BOLD, 16); // default

  /** A bag of glyphs */
  protected Vector glyphs = new Vector();

  /** A component for input.  It is public so that the parent 
    component can manipulate it.  Note that it must be added
    BEFORE adding DrawCanvas, or it will be covered up by 
    DrawCanvas.  Also, textinp.setVisible(false) must be called 
    after Main.setVisible!
    */
  public TextField textinp;

  // ...............................................................

  /** Default constructor. */
  public DrawCanvas () {
    X = new short[2];
    Y = new short[2];

    // enable event handling
    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);

    // add an invisible text input widget
    textinp = new TextField(10);

    // TextField generates ActionEvent[ACTION_PERFORMED,cmd=String]
    // when <CR> is pressed.
    textinp.addActionListener(this);

  }

  /** Constructor with an image. */
  public DrawCanvas (Image img) {
    this();  // call the default constructor
    if (img != null) {
      this.img = img;
      prepareImage(img, this);
    }
  }

  // Access to private fields

  /** Set the type of glyph */
  public void setType(byte type) {
    this.type = type;
  }

  /** Get the type of glyph */
  public byte getType() {
    return type;
  }

  /** Set the color of glyph */
  public void setColor(Color color) {
    this.color = color;
    setTheGlyph();
  }

  /** Get the color of glyph */
  public Color getColor() {
    return color;
  }

  /** Set the lineWidth of glyph */
  public void setLineWidth(int lineWidth) {
    this.lineWidth = lineWidth;
    setTheGlyph();
  }

  /** Get the lineWidth of glyph */
  public int getLineWidth() {
    return lineWidth;
  }

  /** Set the font of glyph */
  public void setFont(Font font) {
    this.font = font;
    setTheGlyph();
  }

  /** Get the font of glyph */
  public Font getFont() {
    return font;
  }

  /** Set the attributes of theGlyph to the current ones. */
  public void setTheGlyph() {
    if (theGlyph != null) {
      theGlyph.setAttributes(color, lineWidth, font);
      refresh();
    }
  }

  /** Set the background image */
  public void setImage(Image img) {
    this.img = img;
    mNewImage = true;
    refresh();
  }

  /** Get the background image */
  public Image getImage() {
    return img;
  }

  /** Get the bag of glyphs. */
  public Vector getGlyphs() {
    return glyphs;
  }

  /** Get the bag of glyphs as an array. */
  public Glyph[] getGlyphArray() {
    Glyph[] gls = new Glyph[glyphs.size()];
    for (int i=0; i<glyphs.size(); i++) {
      gls[i] = (Glyph) glyphs.elementAt(i);
    }
    return gls;
  }

  /** Update is called by repaint().  Calling chain: 
   *<PRE>
   * repaint ---[schedule a thread to call]---> update
   *         ---[call]---> paint
   *</PRE>
   * This one bypasses the refreshing part of the default, which goes like:
   *<PRE>
   *	g.setColor(getBackground());
   *	g.fillRect(0,0,width,height);
   *	g.setColor(getForeground());
   *	paint(g);
   *</PRE>
   */
  public void update(Graphics g) {
    paint(g);
  }

  /** Draws the old and new glyphs, as well as those in the "glyphs" bag.
   * Paint is called at start up or when the occluded part of the 
   * canvas is exposed.  "synchronized" may help if there are other threads
   * running.  

   * The old and new glyphs are the ones being updated by the mouse.
   * Use XOR mode for them since they are transient.  Do not use XOR mode
   * for objects that should stay.  They should be cleared by clear();

   * All drawing to screen should be here.  Other methods simply set the 
   * state of the drawing.  This strategy is better for maintenance as well
   * as compliance to future printing facilities of JDK.

   * Have tried double buffering (with offscreen image) but it does not
   * have the freshing when drawing a glyph.
   */
  public synchronized void paint(Graphics g) {
    Glyph gl;
    
    if (bgColor == null) bgColor = getBackground();

    if (gr == null) gr = getGraphics();

    // Redraw only when not being dragged.  Otherwise cause frequent
    // refreshing.
    if (img != null && dragging == false) gr.drawImage(img, 0,0, this);

    // First erase the old glyphs by drawing in XOR mode (background color)
    if (oldGlyph != null && realtime == true) {
      gr.setXORMode(bgColor);
      oldGlyph.draw(gr);
      gr.setPaintMode();
    }

    // Draw a bounding box around when moving
    if (moving == true && oldGlyph != null) {
      gr.setXORMode(bgColor);
      oldGlyph.drawBound(gr);
      gr.setPaintMode();
    }

    // Then paint all glyphs in bag
    if (dragging == false) {
      for (int i=0; i<glyphs.size(); i++) {
        gl = (Glyph) glyphs.elementAt(i);
        gl.draw(gr);
      }
    }

    // Finally paint the new one
    if (newGlyph != null && realtime == true) {
      gr.setXORMode(bgColor);
      newGlyph.draw(gr);
      gr.setPaintMode();
    }

    // Draw a bounding box around when moving
    if (moving == true && newGlyph != null) {
      gr.setXORMode(bgColor);
      newGlyph.drawBound(gr);
      gr.setPaintMode();
    }

    // Draw a bounding pattern around theGlyph when not moving
    if (moving == false && theGlyph != null) {
      theGlyph.drawBound(gr);
    }

    if (theGlyph != null) theKey = theGlyph.getKey();

    // purge the old ones
    oldGlyph = newGlyph; newGlyph = null;

  }

  /** Clears the canvas with background color.  Only the parts not
   * covered by the base image are cleared (in order to avoid too
   * much flickering).  The base image is redrawn in paint(). 
   */
    // Note: Rectangle r = getBounds(); returns the bounds in parent 
    //    component coordinates, not the local one.
  public void clear() {
    gr.setColor(bgColor);
    Dimension size = this.getSize();
    int d, w, h;

    if (img == null) {
      w = 0;  h = 0;
    } else {
      w = img.getWidth(this);
      h = img.getHeight(this);
    }
    d = size.width - w;
    if (d > 0) gr.fillRect(w, 0, d, size.height);
    d = size.height - h;
    if (d > 0) gr.fillRect(0, h, size.width, d);

    // clear exposed area with negative (x,y)
    if (sx > 0) gr.fillRect(-sx, 0, sx, size.height);
    if (sy > 0) gr.fillRect(0, -sy, size.width, sy);
    if (sx > 0 && sy > 0) gr.fillRect(-sx, -sy, sx, sy);

  }

  /** Clear and then repaint.  */
  public void refresh() {
    clear();
    repaint();
  }

  /** erases the selected Glyph (theGlyph).  Do not use setXORMode
   * approach to erase static glyphs.
   */
  public void erase () {
    if (theGlyph == null) return;

    if (theKey == 0) {
      glyphs.removeElement(theGlyph);
    } else {
      Vector newglyphs = new Vector();
      Glyph gl;
      for (int i=0; i<glyphs.size(); i++) {
	gl = (Glyph) glyphs.elementAt(i);
	if (gl.getKey() != theKey) {
	  newglyphs.addElement(gl);
	}
      }
      glyphs = newglyphs;
    }

    theGlyph = null;   // none selected
    refresh();         // repaint will call update()
  }

  /** Copy the selected glyph into buffer. */
  public void copy() {
    if (theGlyph == null) return;
    copyGlyph = theGlyph;
  }

  /** Cut the selected glyph into buffer. */
  public void cut() {
    copy();
    erase();
  }

  /** Clone the copy of glyph in buffer and paste it on the position 
   * last clicked. */
  public void paste() {
    Glyph pasteGlyph;
    if (copyGlyph == null) return;
    try {
      pasteGlyph = (Glyph)copyGlyph.clone();
      pasteGlyph.setLocation(X[0], Y[0]);
      glyphs.addElement(pasteGlyph);
      refresh();
    } catch (CloneNotSupportedException e) {
      System.err.println(e);
    }
  }
 
  /** erases glyphs with key = k (theGlyph) */
  public void erase (int k) {

    if (k <= GLYPHS) {
      if (k == ALL) img = null; // Clear all
      oldGlyph = null;  newGlyph = null;  
      theGlyph = null;

      glyphs.removeAllElements();
      clear();          // clear bound around theGlyph
      repaint();
    }

  }

  /** Shift the canvas by (sx,sy) */
  public void shift(int sx, int sy) {
    gr.translate(sx, sy);
    this.sx += sx;   // accumulative!
    this.sy += sy;
  }

  // Motion events
  public void processMouseMotionEvent(MouseEvent e) {
    // set new coordinates
    X[1] = (short) (e.getX() - sx);   Y[1] = (short) (e.getY() - sy);

    // shift of mouse location
    short msx = (short)(X[1]-X[0]);
    short msy = (short)(Y[1]-Y[0]);

    switch(e.getID()) {
    case MouseEvent.MOUSE_DRAGGED:
      if (type == Glyph.CROSSRECT) {
	theGlyph.shift(msx, msy);
	newGlyph = theGlyph;
	X[0] = X[1];  Y[0] = Y[1];      // reset reference point

      } else if (moving == true) {
	theGlyph.shift(msx, msy);
	newGlyph = theGlyph;
	X[0] = X[1];  Y[0] = Y[1];      // reset reference point

      } else {
	newGlyph = new Glyph(type, X, Y, lineWidth, color, 0);
      }
      dragging = true;
      repaint();

      break;

    case MouseEvent.MOUSE_MOVED:
      break;
    }
    super.processMouseMotionEvent(e);
  }

  /* Button click events.  

     Note that the event "release" always preceed "click"!
     On Solaris, clickCount=0 for "release" (=1 for "click")
     But on Win95, clickCount=1 for "release" or "click"!
       Thus we cannot separate click from release on Win95?!
     - YTLau 3/97 */

  public void processMouseEvent(MouseEvent e) {
    // set new coordinates
    X[1] = (short) (e.getX() - sx);   Y[1] = (short) (e.getY() - sy);

    switch(e.getID()) {
    case MouseEvent.MOUSE_PRESSED:
      X[0] = X[1]; Y[0] = Y[1];    // reset start point
      x0 = X[0];  y0 = Y[0];

      if (theGlyph != null && theGlyph.intersect(X[1], Y[1]) == true) {
	// Move theGlyph only when seletion is active
	moving = true;
	newGlyph = theGlyph;
	if (theKey == 0) glyphs.removeElement(theGlyph);
	repaint();                   // repaint will call update()

      } else if (type == Glyph.CROSSRECT) {
	// CROSSRECT indicates shifting the canvas
	moving = true;
	short x[] = new short[2];
	short y[] = new short[2];

	if (img == null) {
	  x[0] = (short)(X[0] - 10);
	  y[0] = (short)(Y[0] - 10);
	  x[1] = (short)(X[0] + 10);
	  y[1] = (short)(Y[0] + 10);
	} else {
	  x[0] = 0;
	  y[0] = 0;
	  x[1] = (short)img.getWidth(this);
	  y[1] = (short)img.getHeight(this);
	}
	newGlyph = new Glyph(type, x, y, 4, color, 0);
	theGlyph = newGlyph;
	repaint();                   // repaint will call update()

      } else {
	moving = false;
	newGlyph = new Glyph(type, X, Y, lineWidth, color, 0);
	theGlyph = null;             // set to null during drawing
	repaint();                   // repaint will call update()
      }

      break;

    case MouseEvent.MOUSE_RELEASED:

      dragging = false; // unset dragging flag
      clear();          // clear the whole canvas

      if (type == Glyph.CROSSRECT && theGlyph.getType() == Glyph.CROSSRECT) {
	shift((short)(X[1] - x0), (short)(Y[1] - y0));  // shift the canvas
	moving = false;
	newGlyph = null;
	oldGlyph = null;
	theGlyph = null;
	refresh();
	break;

      } else if (moving == true) {
	if (theKey == 0) glyphs.addElement(theGlyph);
	else {
	  // shift all glyphs with theKey
	  Glyph gl;
	  for (int i=0; i<glyphs.size(); i++) {
	    gl = (Glyph) glyphs.elementAt(i);
	    if (gl.getKey() == theKey) {
	      gl.shift((short)(X[1] - x0), (short)(Y[1] - y0));
	    }
	  }
	}
	newGlyph = null; // don't set oldGlyph=null so that it will be erased
	moving = false;
	repaint();
	break;
      }

      // For text, we pop up the text input widget
      if (type == Glyph.TEXT) {
	pp = getLocation();
	//debug("x, y of parent = " + pp.x + "; " + pp.y);

	// this seems to have no effects
	//	  textinp.setColumns(Math.abs(X[1]-X[0]));

	textinp.setLocation(pp.x + X[0], pp.y + Y[0]);
	textinp.setVisible(true);
	textinp.requestFocus();
	newGlyph = null;         
	repaint();

      } else if ( (X[1]-X[0])*(X[1]-X[0]) + (Y[1]-Y[0])*(Y[1]-Y[0]) < 20 ) {
	// ignore small movement of mouse, treat it as click
	newGlyph = null; // don't set oldGlyph=null so that it will be erased
	repaint();

      } else {                   // normal release
	// becomes a selected Glyph by default
	theGlyph = new Glyph(type, X, Y, lineWidth, color, 0);
	glyphs.addElement(theGlyph);
	newGlyph = null; // don't set oldGlyph=null so that it will be erased
	repaint();
      }

      break;

    case MouseEvent.MOUSE_CLICKED:
      Glyph gl = null;
      int i;

      clear();    // clear previous bound 
      // replace theGlyph with new selection.  gl can be null => no selection.
      /* Find the glyph intersect the point.  Search backward. */
      for (i=glyphs.size() - 1; i>=0; i--) {
	gl = (Glyph) glyphs.elementAt(i);
	if ( gl.intersect(X[1], Y[1]) == true ) break;
      }

      if (i<0) {
	theGlyph = null;
	theKey = 0;
      }
      else theGlyph = gl;

      if (theGlyph != null) {
	theKey = gl.getKey();
	if (theKey > 0) {
	  theGlyph = new Glyph(Glyph.CROSSRECT, X, Y, 4, color, theKey);
	  for (i=0; i<glyphs.size(); i++) {
	    gl = (Glyph) glyphs.elementAt(i);
	    if (gl.getKey() == theKey) {
	      // enlarge bounding box here
	    }
	  }
	}
      }

      repaint();
      break;

    case MouseEvent.MOUSE_ENTERED:
      break;

    case MouseEvent.MOUSE_EXITED:
      break;
    }
    super.processMouseEvent(e);
  }

  /** Receive action events */
  public void actionPerformed (ActionEvent e) {
    //    debug(e);

    if (e.getSource() == textinp) {
      // From text input widget.  Create new text label
      textinp.setVisible(false);
      theGlyph = new Glyph(e.getActionCommand(), font, X, Y, color, 0);
      glyphs.addElement(theGlyph);
      refresh();
      type = Glyph.CIRCLE;   // switch to other type
    }

  }

  /** Send image to port.  Do nothing if image is null or old. */
  public void sendImageTo (ImagePort imgp) {
    if (img == null || !mNewImage) return;

    // first set the image pixel buffers
    int width = img.getWidth(this);
    int height = img.getHeight(this);
    int len = width*height;

    int[] pixels = new int[len];
    // debug("ImagePort: len = " + len);

    try {
      PixelGrabber pg = new
        PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
	pg.grabPixels();

	// Check for errors.
	if ((pg.status() & ImageObserver.ABORT) != 0) {
	  System.err.println("ImagePort: error grabbing image pixels");
	  System.exit(1);
	}
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    // Set the fields
    imgp.setWidth(width);
    imgp.setHeight(height);
    imgp.setPixels(pixels);

    mNewImage = false;
  }

  /** Convert the glyphs to cgm and send to port. */
  public void sendGlyphsTo (ImagePort imgp) {
    if (glyphs.size() == 0) return;

    // create and initialize cgm, put all glyphs to one cgm
    Cgm cgm = new Cgm(glyphs);     

    // set the cgm buffers (just the first one for now)
    byte[][] cgmbuf = new byte[1][];
    cgmbuf[0] = cgm.getBuf();
    imgp.setCgm(cgmbuf);

    debug( "DrawCanvas.sendGlyphsTo: " + cgmbuf[0].length + " bytes sent.");

  }

  public void sendGlyphsTo (String file) {
    if (glyphs.size() == 0) return;

    // create and initialize cgm, put all glyphs to one cgm
    Cgm cgm = new Cgm(glyphs);     
    cgm.write(file);

  }

  /** Get image objects from port. */
  public synchronized void getImageFrom (ImagePort imgp) {

    int width = imgp.getWidth();
    int height = imgp.getHeight();
    int[] pixels = imgp.getPixels();

    if (pixels == null) return;

    debug("DrawCanvas.getImageFrom: " + width + " x " + height + " pixels");
    // for (int i=0; i < 5; i++) debug((pixels[i]));

    // no base image present
    if (width <= 1 && height <= 1) return;

    img = getToolkit().createImage(
      new MemoryImageSource(width, height, cm, pixels, 0, width));

    refresh();

  }

  /** 
   *  Get CGM data from port and convert them to glyphs.
   *  Replace the current ones with the new ones.
   */
  public void getGlyphsFrom (ImagePort imgp) {
    byte[][] cgmbuf = imgp.getCgm();
    if (cgmbuf == null) return;

    debug("DrawCanvas.getGlyphsFrom: " + cgmbuf[0].length + " bytes");

    // create and initialize cgm, interpret cgm buffer
    // (just 1 for now)
    Cgm cgm = new Cgm(cgmbuf[0]);     

    // get glyphs
    glyphs = cgm.getGlyphs();   

    debug("Number of glyphs read = " + glyphs.size());

    theGlyph = null;  // none selected
    refresh();
  }

  /** Read a CGM file and insert the glyphs. */
  public void getGlyphsFrom (String file) {

    // create and initialize cgm, interpret cgm buffer
    Cgm cgm = new Cgm();     
    cgm.read(file);

    // get glyphs
    glyphs = cgm.getGlyphs();   

    debug("Number of glyphs read = " + glyphs.size());

    Glyph gl;
    for (int i=0; i<glyphs.size(); i++) {
      gl = (Glyph) glyphs.elementAt(i);
      gl.setKey(1);
    }

    theGlyph = null;  // none selected
    refresh();
  }

  /**
   * Show the input string if mDebug > 0.
   */
  public static void debug(String s) {
    if (mDebug > 0) System.out.println(s);
  }

}

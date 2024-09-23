//*************************************************************************
/*
 * ImageLabel.java - A class for displaying images.
 *
 *   This file should be under $CLASSPATH/widget
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package widget;

import java.awt.*;
import java.net.*;

//============================================================================
/**
 * A class for displaying images. It places the Image
 * into a canvas so that it can moved around by layout managers,
 * will get repainted automatically, etc. No <TT>mouseXXX</TT> or
 * <TT>action</TT> events are defined, so it is most similar to the
 * <TT>Label</TT> Component.
 * <P>
 * By default, with FlowLayout the ImageLabel takes its minimum size
 * (just enclosing the image). The default with BorderLayout is
 * to expand to fill the region in width (North/South), height
 * (East/West) or both (Center). This is the same behavior as the builtin
 * Label class. If you give an explicit <TT>resize</TT> or
 * <TT>reshape</TT> call <B>before</B> adding the ImageLabel to the
 * Container, this size will override the defaults.
 * <P>
 * Here is an example of its use:
 * <P>
 * <PRE>
 * public class ShowImages {
 *   ImageLabel duke, javaMug;
 *
 *   public void init() {
 *     duke = new ImageLabel("http://java.sun.com/lib/images/duke.gif");
 *     javaMug = new ImageLabel("http://java.sun.com/lib/images/JAVA.85.GIF");
 *     add(duke);
 *     add(javaMug);
 *   }
 * }
 * </PRE>
 * <P>
 * The code is based on:
 * <A href="http://www.apl.jhu.edu/~hall/java/ImageLabel/ImageLabel.java">
 * http://www.apl.jhu.edu/~hall/java/ImageLabel/ImageLabel.java</A>. The latest
 * version of the documentation is at
 * <A href="http://www.apl.jhu.edu/~hall/java/ImageLabel/ImageLabel.html">
 * .../ImageLabel.html</A>, and a small example can be found at
 * <A href="http://www.apl.jhu.edu/~hall/java/ImageLabel/ImageLabelTest.html">
 * .../ImageLabelTest.html</A>.
 * <P>
 * No warranty of any kind is provided.
 * Permission is granted to use and/or modify for any purpose.
 * <P>
 * 
 * 7/96 Marty Hall:
 * <UL>
 *    <LI><A href="http://www.apl.jhu.edu/~hall/java">
 *        Java Resource Page.</A> 
 *    <LI><A href="mailto:hall@apl.jhu.edu">
 *        Email.</A>
 *    <LI><A href="http://www.apl.jhu.edu/~hall">
 *        Home Page.</A> 
 * </UL>
 *
 * @see Icon
 * @see ImageButton
 * @version 1.1 (August 1996)
 */

public class ImageLabel extends Canvas {
  //-------------------------------------------------------------------------
  // Instance variables.
  
  /** The actual Image drawn on the canvas. Don't specify this directly;
   *  pass a string, URL, or image to the ImageLabel constructor.
   */
  protected Image image;

  /** A String corresponding to the URL of the image you will get
   *  if you call the constructor with no arguments.
   */
  public static String defaultImageString
    = "http://java.sun.com/lib/images/logo.java.color-transp.55x60.gif";

  /** The URL of the image. But sometimes we will use an existing image
   *  object (e.g. made by createImage) for which this info will not be
   *  available, so a default string is used here.
   */
  protected String imageString = "<Existing Image>";

  /** Turn this on to get verbose debugging messages. */
  public boolean debug = false;

  /** Amount of extra space around the image. */
  protected int margin = 0;

  /** If there is a non-zero margin, what color should it be? Default
   *  is to use the background color of the Container.
   */
  protected Color marginColor = null;
  
  /** Width and height of the Canvas. This is the width/height of the image
   *  plus twice the margin.
   */
  protected int width, height;

  /** Determines if it will be sized automatically.
   *  If the user issues a setSize() or setBounds() call before adding the
   *  label to the Container, or if the LayoutManager resizes before
   *  drawing (as with BorderLayout), then those sizes override
   *  the default, which is to make the label the same size as the image
   *  it holds (after reserving space for the margin, if any).
   *  This flag notes this, so subclasses that override ImageLabel
   *  need to check this flag, and if it is true, and they 
   *  draw modified image, then they need to draw them based on the width
   *  height variables, not just blindly drawing them full size.
   */
  protected boolean explicitSize = false;
  private int explicitWidth=0, explicitHeight=0;
  
  // The MediaTracker that can tell if image has been loaded
  // before trying to paint it or resize based on its size.
  private MediaTracker tracker;
  
  // Used by MediaTracker to be sure image is loaded before paint & resize,
  // since you can't find out the size until it is done loading.
  private static int lastTrackerID=0;
  private int currentTrackerID;
  private boolean doneLoading = false;

  private Container parentContainer;

  //-------------------------------------------------------------------------

  /** Create an ImageLabel with the default image.
   * @see ImageLabel#defaultImageString
   */
  // Remember that the funny "this()" syntax calls constructor of same class
  public ImageLabel() {
    this(defaultImageString);
  }
  
  /** Create an ImageLabel using the image at URL specified by the string.
   * @param imageURLString A String specifying the URL of the image.
   */
  public ImageLabel(String imageURLString) {
    this(makeURL(imageURLString));
  }

  /** Create an ImageLabel using the image at URL specified.
   * @param imageURL The URL of the image.
   */
  public ImageLabel(URL imageURL) {
    this(getImage(imageURL));
    imageString = imageURL.toExternalForm();
  }
  
  /** Create an ImageLabel using the image specified. The other
   *  constructors eventually call this one, but you may want to call
   *  it directly if you already have an image (e.g. created via
   *  createImage).
   * @param image The image
   */
  public ImageLabel(Image image) {
    this.image = image;
    tracker = new MediaTracker(this);
    currentTrackerID = lastTrackerID++;
    tracker.addImage(image, currentTrackerID);
  }

  //-------------------------------------------------------------------------
  /** Makes sure that the Image associated with the Canvas is done loading
   *  before returning.
   *  getImage spins off a separate thread to do the loading. Once you
   *  get around to drawing the image, this will make sure it is loaded,
   *  waiting if not. The user does not need to call this at all, but
   *  if several ImageLabels are used on the same Comtainer, this can cause
   *  several repated layouts, so users might want to explicitly call this
   *  themselves before adding the ImageLabel to the Container. On the
   *  other hand, postponing the waiting as long as possible is more
   *  efficient, since loading can go on in the background.
   *
   * @param doLayout Determines if the Container should be re-layed out
   *                 after you are finished waiting. <B>This should be
   *                 true when called from user functions</B>, but
   *                 is set to false when called from preferredSize
   *                 to avoid an infinite loop. This is needed when
   *                 using BorderLayout, which calls preferredSize
   *                 <B>before</B> calling paint.
   */
  public void waitForImage(boolean doLayout) {
    if (!doneLoading) {
      debug("[waitForImage] - Resizing and waiting for " + imageString);
      try { tracker.waitForID(currentTrackerID); } 
      catch (InterruptedException i) {} 
      catch (Exception e) { 
	System.out.println("Error loading " + imageString + ": "
			   + e.getMessage()); 
	e.printStackTrace(); 
      } 
      if (tracker.isErrorID(0)) 
        new Throwable("Error loading image " + imageString).printStackTrace();
      doneLoading = true;
      if (explicitWidth != 0)
	width = explicitWidth;
      else
	width = image.getWidth(this) + 2*margin;
      if (explicitHeight != 0)
	height = explicitHeight;
      else
	height = image.getHeight(this) + 2*margin;
      setSize(width, height);
      debug("[waitForImage] - " + imageString + " is " +
	    width + "x" + height + ".");

      // If no parent, you are OK, since it will have been resized before
      // being added. But if parent exists, you have already been added,
      // and the change in size requires re-layout. 
      if (((parentContainer = getParent()) != null) && doLayout) {
	setBackground(parentContainer.getBackground());
	parentContainer.doLayout();
      }
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Moves the image so that it is <I>centered</I> at the specified location,
   * as opposed to the move method of Component which places the top left
   * corner at the specified location.
   * <P>
   * <B>Note:</B> The effects of this could be undone by the
   * layoutManager of the parent Container, if it is using one.
   * So this is normally only used in conjunction with a null
   * layoutManager.
   *
   * @param x The X coord of center of the image
   *          (in parent's coordinate system)
   * @param y The Y coord of center of the image
   *          (in parent's coordinate system)
   * @see java.awt.Component#move
   */

  public void centerAt(int x, int y) {
    debug("Placing center of " + imageString + " at (" + x + "," + y + ")");
    setLocation(x - width/2, y - height/2); 
  }

  //-------------------------------------------------------------------------
  // Copied from the JDK 1.02 sources. Repeated because Netscape 2.02
  // uses the JDK 1.0 buggy version.
  
  /** Determines if the x and y <B>(in the Icon's own coordinate
   *  system)</B> is inside the Icon. Put here because
   *  Netscape has a bug in which it doesn't process contains() and getComponentAt()
   *  tests correctly. They work properly with appletviewer and
   *  for applications.
   */
  public synchronized boolean contains(int x, int y) {
    return (x >= 0) && (x <= width) && (y >= 0) && (y <= height);
  }
  
  //-------------------------------------------------------------------------
  // NOTE: drawRect on Solaris 2.4, under Netscape 2.0x, appletviewer
  // from JDK 1.02, and in standalone apps under 1.02, has a bug in that
  // it draws 1 pixel too far. fillRect and draw3DRect do not suffer from
  // this problem, and I have not yet confirmed if it exists on
  // Windows or MacOS. If the problem does *not* exist on those platforms,
  // then the width-1 and height-1 below should be replaced with simply
  // width and height.
  
  /** Draws the image. If you override this in a subclass, be sure
   *  to call super.paint.
   */
  public void paint(Graphics g) {
    if (!doneLoading)
      waitForImage(true);
    else {
      if (explicitSize)
	g.drawImage(image, margin, margin,
		    width-2*margin, height-2*margin,
		    this);
      else
	g.drawImage(image, margin, margin, this);
      drawRect(g, 0, 0, width-1, height-1, margin, marginColor);
    }
  }

  //-------------------------------------------------------------------------
  /** Used by layout managers to calculate the usual size allocated for the
   *  Component. Since some layout managers (e.g. BorderLayout) may
   *  call this before paint is called, you need to make sure
   *  that the image is done loading, which will force a resize, which
   *  determines the values returned.
   */
  public Dimension getPreferredSize() {
    if (!doneLoading)
      waitForImage(false);
    return(super.getPreferredSize());
  }

  //-------------------------------------------------------------------------
  /** Used by layout managers to calculate the smallest size allocated for the
   *  Component. Since some layout managers (e.g. BorderLayout) may
   *  call this before paint is called, you need to make sure
   *  that the image is done loading, which will force a resize, which
   *  determines the values returned.
   */
   public Dimension getMinimumSize() {
     if (!doneLoading)
       waitForImage(false);
     return(super.getMinimumSize());
   }
  
  //-------------------------------------------------------------------------
  // LayoutManagers (such as BorderLayout) might call resize or reshape
  // with only 1 component of width/height non-zero. In such a case,
  // you still want the other component to come from the image itself.

  /** Resizes the ImageLabel. If you don't resize the label explicitly,
   *  then what happens depends on the layout manager. With FlowLayout,
   *  as with FlowLayout for Labels, the ImageLabel takes its minimum
   *  size, just enclosing the image. With BorderLayout, as with
   *  BorderLayout for Labels, the ImageLabel is expanded to fill the
   *  section. Stretching GIF files does not always result in clear looking
   *  images. <B>So just as with builtin Labels and Buttons, don't
   *  use BorderLayout if you don't want the Buttons to get resized.</B>
   *  If you don't use any LayoutManager, then the ImageLabel will
   *  just fit the image.
   *  <P>
   *  Note that if you resize explicitly, you must do it <B>before</B>
   *  the ImageLabel is added to the Container. In such a case, the
   *  explicit size overrides.
   *
   * @see #reshape
   */
  public void setSize(int width, int height) {
    if (!doneLoading) {
      explicitSize=true;
      if (width > 0)
	explicitWidth=width;
      if (height > 0)
	explicitHeight=height;
    }
    super.setSize(width, height);
  }

  /** Reshapes the ImageLabel. If you don't resize the label explicitly,
   *  then what happens depends on the layout manager. With FlowLayout,
   *  as with FlowLayout for Labels, the ImageLabel takes its minimum
   *  size, just enclosing the image. With BorderLayout, as with
   *  BorderLayout for Labels, the ImageLabel is expanded to fill the
   *  section. Stretching GIF files does not always result in clear looking
   *  images. <B>So just as with builtin Labels and Buttons, don't
   *  use BorderLayout if you don't want the Buttons to get resized.</B>
   *  If you don't use any LayoutManager, then the ImageLabel will
   *  just fit the image.
   *  <P>
   *  Note that if you resize/reshape explicitly, you must do it
   *  <B>before</B> the ImageLabel is added to the Container. In such a
   *  case, the explicit size overrides.
   *
   * @see #resize
   */
  public void setBounds(int x, int y, int width, int height) {
    if (!doneLoading) {
      explicitSize=true;
      if (width > 0)
	explicitWidth=width;
      if (height > 0)
	explicitHeight=height;
    }
    super.setBounds(x, y, width, height);
  }
  
  //-------------------------------------------------------------------------
  // You can't just set the background color to the marginColor and
  // skip drawing the border, since it messes up transparent gifs. You
  // need the background color to be the same as the container.
  
  /** Draws a rectangle with the specified OUTSIDE left, top, width,
   *  and height. Used to draw the border.
   */
  protected void drawRect(Graphics g, int left, int top,
			  int width, int height, int lineThickness,
			  Color rectangleColor) {
    g.setColor(rectangleColor);
    for(int i=0; i<lineThickness; i++) {
      g.drawRect(left, top, width, height);
      if (i < lineThickness-1) {  // Skip these the last rectangle
	left = left + 1;
	top = top + 1;
	width = width - 2;
	height = height - 2;
      }
    }
  }
  
  //-------------------------------------------------------------------------
  /** Calls System.out.println if the debug variable is true,
   *  does nothing otherwise.
   * @param message The String to be printed.
   */
  protected void debug(String message) {
    if (debug)
      System.out.println(message);
  }
  
  //-------------------------------------------------------------------------
  
  private static URL makeURL(String s) {
    URL u = null;
    try { u = new URL(s); }
    catch (MalformedURLException m) {
      System.out.println("Bad URL " + s + ": " + m);
      m.printStackTrace();
    }
    return(u);
  }

  //-------------------------------------------------------------------------
  // Needs to be static since it is called by the constructor.
  
  private static Image getImage(URL url) {
    return(Toolkit.getDefaultToolkit().getImage(url));
  }

  //-------------------------------------------------------------------------
  // A small test frame
  static public void main(String[] args) {
    Frame f = new Frame();
    f.add(new ImageLabel());  // use default image
    f.pack();
    f.setVisible(true);
  }
}

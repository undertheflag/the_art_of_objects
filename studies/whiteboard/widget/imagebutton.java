//*************************************************************************
/*
 * ImageButton.java - A button class with an image.
 *  
 *   See ImageButtonTest.java for usage examples.
 *
 *   This file should be under $CLASSPATH/widget
 *
 *  Modification History:
 *    7/1996  MH     Original development
 *    3/12/97 YTLau  Adopted
 *    3/18/97 YTLau  Upgrade to JDK1.1 version with upgraded event interface
 *    5/14/97 YTLau  Version 1.0 delivery
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package widget;

import java.awt.*;
import java.net.*;
import java.awt.image.*;      // For ImageFilter stuff
import java.awt.event.*;      // for event processing

import widget.ImageLabel;

//=========================================================================
// TO DO: Make button resizable and stretch the image to fit.

/**
 * A button class that uses an image instead of a textual label.
 * It triggers an ActionEvent to ActionListeners.  To use:
 * <OL>
 *  <LI><TT>implements ActionListener</TT>
 *  <LI><TT>ImageButton.setActionListener(this); new ImageButton("...");</TT>
 *  <LI>define: <TT>public void actionPerformed(ActionEvent event) {...}</TT> 
 * </OL>
 * <P>
 * By default, with FlowLayout the ImageButton takes its minimum size
 * (just enclosing the image).  (The default with BorderLayout is
 * to expand to fill the region in width (North/South), height
 * (East/West) or both (Center).) This is the same behavior as the builtin
 * Button class.  If you give an explicit <TT>setSize</TT> or
 * <TT>reshape</TT> call <B>before</B> adding the ImageButton to the
 * Container, this size will override the defaults.
 * <P>
 * This version (JDK1.1) is based on the source code at 
 * <A HREF="http://www.apl.jhu.edu/~hall/java/ImageLabel/ImageButton.java">
 * http://www.apl.jhu.edu/~hall/java/ImageLabel/ImageButton.java</A>.
 * An example program is in:
 * <A HREF="../ImageButtonTest.java"> ImageButtonTest.java</A>. 
 * <P>
 * No warranty of any kind is provided.
 * Permission is granted to use and/or modify for any purpose.
 * <P>
 *
 * @see Icon
 * @see GrayFilter
 */

public class ImageButton extends ImageLabel implements MouseListener {
  //-----------------------------------------------------------------

  /** Default width of 3D border around image. Currently 4.
   * @see #setBorderWidth
   * @see #getBorderWidth
   */
  protected static final int defaultBorderWidth = 2;

  /** Default color of 3D border around image. Currently a gray
   *  with R/G/B of 160/160/160. Light grays look best.
   * @see #setBorderColor
   * @see #getBorderColor
   */
  protected static final Color defaultBorderColor = new Color(160, 160, 160);

  /** Gray image created automatically from regular image via
   *  an image filter to use when button is depressed. You won't normally
   *  use this directly. 
   */
  protected Image grayImage = null;

  // Darker is consistent with regular buttons
  /** An int whose bits are combined via "and" ("&") with the alpha,
   *  red, green, and blue bits of the pixels of the image to produce
   *  the grayed-out image to use when button is depressed.
   *  Default is 0xffafafaf: af combines with r/g/b to darken image.
   */
  public int darkness = 0xffafafaf;
  
  private boolean mouseIsDown = false;

  // Reference to collection of Listeners
  private ActionListener actionListener = null;

  // The choice of Listener for default setting by constructors
  private static ActionListener theListener = null;  

  //-------------------------------------------------------------------------
  // Constructors
  
  /** Create an ImageButton with the default image.
   * @see ImageLabel#defaultImageString
   */
  public ImageButton() {
    super();
    initialize();
  }

  /** Create an ImageButton using the image at URL specified by the string.
   * @param imageURLString A String specifying the URL of the image.
   */
  public ImageButton(String imageURLString) {
    super(imageURLString);
    initialize();
  }

  /** Create an ImageButton using the image at URL specified.
   * @param imageURL The URL of the image.
   */
  public ImageButton(URL imageURL) {
    super(imageURL);
    initialize();
  }

  /** Create an ImageButton using the image specified. You would only want to
   *  call it directly if you already have an image (e.g. created via
   *  createImage).
   * @param image The image.
   */
  public ImageButton(Image image) {
    super(image);
    initialize();
  }

  private void initialize() {
    margin = defaultBorderWidth;
    marginColor = defaultBorderColor;

    // must add itself to be a listener
    addMouseListener(this);
    // set default if not null
    if (theListener != null) addActionListener(theListener);
    else {
      if (actionListener == null)
	System.err.println("ImageButton: no actionListener added yet!");
    }
  }

  // Another set of constructors with ActionListener set.

  /** Create an ImageButton with the default image.
   * @see ImageLabel#defaultImageString
   * @param l The listener to receive action event.
   */
  public ImageButton(ActionListener l) {
    super();
    initialize(l);
  }

  /** Create an ImageButton using the image at URL specified by the string.
   * @param imageURLString A String specifying the URL of the image.
   * @param l The listener to receive action event.
   */
  public ImageButton(String imageURLString, ActionListener l) {
    super(imageURLString);
    initialize(l);
  }

  /** Create an ImageButton using the image at URL specified.
   * @param imageURL The URL of the image.
   * @param l The listener to receive action event.
   */
  public ImageButton(URL imageURL, ActionListener l) {
    super(imageURL);
    initialize(l);
  }

  /** Create an ImageButton using the image specified. You would only want to
   *  call it directly if you already have an image (e.g. created via
   *  createImage).
   *  @param image The image.
   *  @param l The listener to receive action event.
   */
  public ImageButton(Image image, ActionListener l) {
    super(image);
    initialize(l);
  }

  private void initialize(ActionListener l) {
    margin = defaultBorderWidth;
    marginColor = defaultBorderColor;

    // must add itself to be a listener
    addMouseListener(this);
    // set to ActionListener l
    addActionListener(l);
  }
  //-----------------------------------------------------------------


  /** Sets the width around the outside of the image.
   * @see #getBorderWidth
   */
  public void setBorderWidth(int borderWidth) {
    margin = borderWidth;
  }

  //-----------------------------------------------------------------
  /** Returns the width around the outside of the image.
   * @see #setBorderWidth
   */
  public int getBorderWidth() {
    return(margin);
  }

  //-----------------------------------------------------------------
  /** Sets the Color for the border around the outside of the image.
   * @see #getBorderColor
   */
  public void setBorderColor(Color borderColor) {
    marginColor = borderColor;
  }

  //-----------------------------------------------------------------
  /** Returns the Color for the border around the outside of the image.
   * @see #setBorderColor
   */
  public Color getBorderColor() {
    return(marginColor);
  }
  
  //-----------------------------------------------------------------
  /** Draws the image with the border around it. If you override
   *  this in a subclass, call super.paint().
   */
  public void paint(Graphics g) {
    super.paint(g);          // Forces main image to get loaded
    if (grayImage == null)   // Creates gray image from main image
      createGrayImage(g);
    drawBorder(true);
  }

  //------------------- Mouse Events -----------------------------------
  // Must define all five events here !

  /** When the mouse is pressed, reverse the 3D border and
   *  draw a dark-gray version of the image. The action is not
   *  triggered until mouseUp.
   */
  public void mousePressed(MouseEvent event) {
    mouseIsDown = true;
    Graphics g = getGraphics();
    if (explicitSize)
      g.drawImage(grayImage, margin, margin,
		  width-2*margin, height-2*margin,
		  this);
    else
      g.drawImage(grayImage, margin, margin, this);
    drawBorder(false);
  }

  /** If cursor is still inside, trigger the action event and redraw
   *  the image (non-gray, button "out"). Otherwise ignore this.
   */
  public void mouseReleased(MouseEvent e) {
    mouseIsDown = false;
    if (contains(e.getX(), e.getY())) {
      paint(getGraphics());

      if (actionListener != null) {
	// generate and dispatch an actionEvent
	actionListener.actionPerformed(new ActionEvent( (Object)this, 
	      ActionEvent.ACTION_PERFORMED, "Button Pressed"));
      } 
    }
  }
  
  public void mouseClicked(MouseEvent event) {
  }

  public void mouseEntered(MouseEvent event) {
  }

  /** If you move the mouse off the button while the mouse is down,
   *  abort and do NOT trigger the action. Ignore this if
   *  button was not already down.
   */
  public void mouseExited(MouseEvent e) {
    if (mouseIsDown) 
      paint(getGraphics());
  }
  
  //------------------- end Mouse Events ------------------------------

  private void drawBorder(boolean isUp) {
    Graphics g = getGraphics();
    g.setColor(marginColor);
    int left = 0;
    int top = 0;
    int width = this.width;
    int height = this.height;
    for(int i=0; i<margin; i++) {
      g.draw3DRect(left, top, width, height, isUp);
      left++;
      top++;
      width = width - 2;
      height = height - 2;
    }
  }

  //-----------------------------------------------------------------
  // The first time the image is drawn, update() is called, and
  // the printout does not come out correctly. So this forces a
  // brief draw on loadup, replaced by real, non-gray image.
  
  private void createGrayImage(Graphics g) {
    ImageFilter filter = new GrayFilter(darkness);
    ImageProducer producer = new FilteredImageSource(image.getSource(),
						     filter);
    grayImage = createImage(producer);
    if (explicitSize)
      g.drawImage(grayImage, margin, margin,
		  width-2*margin, height-2*margin,
		  this);
    else
      g.drawImage(grayImage, margin, margin, this);
    super.paint(g);
  }

  //--------------- Action listener setting -----------------------------------
  /** Set the listener to theListener for action event generated by ImageButton.
      It stays effective until it is set again! */
  public static void setActionListener (ActionListener l) {
    theListener = l;
  }

  /** Add a listener to action event generated by ImageButton */
  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  /** Remove a listener to action event generated by ImageButton */
  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

}

//============================================================================
/** Builds an image filter that can be used to gray-out the image.
 * @see ImageButton
 */

class GrayFilter extends RGBImageFilter {

  //-----------------------------------------------------------------

  private int darkness = 0xff808080;
  
  //-----------------------------------------------------------------

  public GrayFilter() {
    canFilterIndexColorModel = true;
  }

  public GrayFilter(int darkness) {
    this();
    this.darkness = darkness;
  }

  //-----------------------------------------------------------------

  public int filterRGB(int x, int y, int rgb) {
    return(rgb & darkness);
  }
  
  //-----------------------------------------------------------------
  // A small test frame
  static public void main(String[] args) {
    Frame f = new Frame();
    f.setLayout(new FlowLayout(FlowLayout.LEFT));
    f.add(new ImageButton());  // use default image
    f.setSize(400,300);
    f.setVisible(true);
  }
}

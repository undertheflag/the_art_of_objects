// Icon.java -  JDK1.0 version

package widget;  // this file should be under $CLASSPATH/widget

import java.awt.*;
import java.net.*;

/** A class that supports Images as Components that can be repositioned
 *  with the mouse. As with all Components, the effects may be eventually
 *  undone by the layoutManager if it is not off (null) or chosen
 *  appropriately.
 *  <P>
 *  By default, with FlowLayout the Icon takes its minimum size
 *  (just enclosing the image). The default with BorderLayout is
 *  to expand to fill the region in width (North/South), height
 *  (East/West) or both (Center). This is the same behavior as the builtin
 *  Label class and of ImageLabel, upon which Icon is built. If you perform
 *  an explicit <TT>resize</TT> or
 *  <TT>reshape</TT> call <B>before</B> adding the ImageLabel to the
 *  Container, this size will override the defaults.
 *  <P>
 *  This can be used in two ways. In the first, the Component itself
 *  watches the mouse events, and allows the user to drag it around.
 *  This has one major advantage:
 *  <UL>
 *    <LI>It doesn't require any changes in the code for the Container. Thus,
 *        Icons can be placed in arbitrary Containers (including builtin ones
 *        such as Panels, Applets, and Frames).
 *  </UL>
 *
 *  However, given the current state of the AWT, it has two main disadvantages:
 *  <UL>
 *   <LI>If you move the mouse too quickly, especially for small Icons, you
 *       can lose the cursor. Given that there is no cursorLocation or
 *       warpCursor methods, there is no way to get it back.
 *   <LI>If you drag over the top of another Icon, the one on top gets
 *       the mouse events. Unfortunately, controlling who is on top
 *       is not portable.
 *  </UL>
 *
 *  On the other hand, you can have the Container insert a small amount of code
 *  into its handleEvent method. This has two advantages:
 *  <UL>
 *   <LI>You cannot lose track of the mouse as long as the cursor stays inside
 *       the Container. So you can drag very small Icons while moving the mouse
 *       quickly.
 *   <LI>You can drag over the top of other Icons reliably.
 *  </UL>
 * 
 *  This has two disadvantages:
 *  <UL>
 *   <LI>Since it requires changes in the Container's code, it cannot be
 *       used for builtin classes or classes for which source is not
 *       available. The user would have to write custom Container
 *       subclasses in such a case.
 *   <LI>Maintenance is more difficult since code is required in multiple
 *       classes.
 *   <LI>You still cannot drag over the top of arbitrary Components. If the
 *       Component is looking for a mouse drag event and is on top, it will get
 *       the event and may never pass it on to the Container. So care must be
 *       taken when using Icons with other custom Components that make use
 *       of mouseDrag.
 *  </UL>
 *
 *  Resolving these pros/cons depends upon the application, so the Icon class
 *  can be used either way. <B>If the ignoreEvents variable is set to true
 *  (the default), then it cannot be dragged without adding code to the
 *  Container.</B>
 *  <P>
 *  If the Container does not already have a custom handleEvent method,
 *  insert the following:
 *  <P>
 *  <PRE>
 *    public boolean handleEvent(Event event) {
 *      if (Icon.handleIconEvent(event, this))
 *        return(true);
 *      else
 *        return(super.handleEvent(event));
 *    }
 *  </PRE>
 *  If the Container does already have a custom handleEvent method, modify it
 *  by adding the Icon.handleEvent test at the <B>top</B>, as illustrated
 *  below:
 *  <P>
 *  <PRE>
 *    public boolean handleEvent(Event event) {
 *      <B>if (Icon.handleIconEvent(event, this))
 *        return(true);</B>
 *      <I>Original handleEvent code here</I>
 *    }
 *  </PRE>
 *  If instead you want the Icon to handle its own mouse events, <B>simply
 *  set the Icon's ignoreEvents variable to false</B>. Do not mix these
 *  two modes in a single program.
 * <P>
 * The original of the Icon source code is at
 * <A HREF="http://www.apl.jhu.edu/~hall/java/ImageLabel/Icon.java">
 * http://www.apl.jhu.edu/~hall/java/ImageLabel/Icon.java</A>,
 * the documentation is at
 * <A HREF="http://www.apl.jhu.edu/~hall/java/ImageLabel/Icon.html">
 * .../Icon.html</A>, and a small example program can be found at
 * <A HREF="http://www.apl.jhu.edu/~hall/java/ImageLabel/IconTest.html">
 * .../IconTest.html</A>. 
 * <P>
 * No warranty of any kind is provided.
 * Permission is granted to use and/or modify
 * for any purpose.
 * <P>
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
 * @version 1.0 (August 1996)
 * @see ImageButton
 */

public class Icon extends ImageLabel {

  //-------------------------------------------------------------------------
  
  /** Should the image be highlighted when you click/drag? Default = true. */
  public boolean highlightable = true;

  /** How thick should the highlighting rectangle be?
   *  Default = 3. Ignored if highlightable is false.
   */
  public int highlightThickness = 3;

  /** The Color to use for the highlight rectangle.
   *  Default is Color.blue. Ignored if highlightable is false.
   */
  public Color highlightColor = Color.blue;
  
  /** Can the image be dragged with the mouse?
   *  Default = true. (If it will always be false, use
   *  an ImageLabel instead).
   */
  public boolean draggable = true;

  // Is it currently being dragged? This is used in the case when
  // the Container is tracking movement, since the mouse may not
  // be over the Component. 
  private boolean beingDragged = false;
  
  /** The cursor to get when dragging. Default is Frame.MOVE_CURSOR */
  public int dragCursor = Frame.MOVE_CURSOR;

  // The previous cursor, so that it can be changed back
  private int previousCursor = Frame.DEFAULT_CURSOR;

  // The Frame that owns the cursor
  private Frame topFrame = null;

  // TO DO: Document
  private boolean ignoreEvents = true;
  
  //-------------------------------------------------------------------------
  /** If the Container was always going to handle the mouse events,
   *  it could simply look up the event type and call a helper method
   *  directly. This, however, allows event handling either locally
   *  (if ignoreEvents is false) or in the Container.
   *  You cannot override this arbitrarily in subclasses.
   */
  public boolean handleEvent(Event event) {
    if (ignoreEvents) {
      return(false);
    } else {
      return(super.handleEvent(event));
    }
  }
  
  //-------------------------------------------------------------------------
  /** Called from the handleEvent method of the Container if the Container
   *  is to handle the mouse events. <B>See the introduction to this class.</B>
   */
  public static boolean handleIconEvent(Event event, Container container) {
    int x = event.x;
    int y = event.y;
    Icon targetIcon = iconBeingDragged(container);
    if (targetIcon == null) {
      // You should be able to use container.locate instead of
      // Icon.componentUnder(x, y, container), but Netscape
      // has a bug with getComponentAt() and contains(). They work fine
      // in applications and appletviewer.
      Component targetComponent = Icon.componentUnder(x, y, container);
      // Component targetComponent = container.getComponentAt(x, y);
      if (!(targetComponent instanceof Icon))
	return(false);
      else
	targetIcon = (Icon)targetComponent;
    }
    Point iconLocation = targetIcon.getLocation();
    int iconX = iconLocation.x;
    int iconY = iconLocation.y;
    // a trick to get the attention of the mouse by moving
    // the thing away.  If the icon image is large, it flashes
    // and jiggles.  - YTLau
    event.translate(-iconX, -iconY);
    targetIcon.ignoreEvents = false;
    boolean result = targetIcon.handleEvent(event);
    targetIcon.ignoreEvents = true;
    event.translate(iconX, iconY);
    return(result);
  }

  //-------------------------------------------------------------------------

  private static Icon iconBeingDragged(Container container) {
    Component[] components = container.getComponents();
    Component current;
    for(int i=0; i<components.length; i++) {
      current = components[i];
      if ((current instanceof Icon) && ((Icon)current).beingDragged)
	return((Icon)current);
    }
    return(null);
  }
  
  //-------------------------------------------------------------------------
  // Constructors
  
  /** Create an Icon with the default image.
   * @see ImageLabel#defaultImageString
   */
  public Icon() {
    super();
  }

  /** Create an Icon using the image at URL specified by the string.
   * @param imageURLString A String specifying the URL of the image.
   */
  public Icon(String imageURLString) {
    super(imageURLString);
  }

  /** Create an Icon using the image at URL specified.
   * @param imageURL The URL of the image.
   */
  public Icon(URL imageURL) {
    super(imageURL);
  }

  /** Create an Icon using the image specified. You would only want to call
   *  it directly if you already have an image (e.g. created via
   *  createImage).
   * @param image The image.
   */
  public Icon(Image image) {
    super(image);
  }

  //-------------------------------------------------------------------------
  /** Changes the cursor from the current to dragCursor, and optionally
   *  draws a highlighting rectangle around image.
   * @return true if it is draggable, false otherwise.
   * @see Icon#dragCursor
   * @see Icon#highlightable
   * @see Icon#draggable
   */
  public boolean mouseDown(Event e, int x, int y) {
    report("Down", x, y);
    if (draggable && contains(x, y)) {
      beingDragged = true;
      previousCursor = parentFrame().getCursorType();
      parentFrame().setCursor(dragCursor);
      if (highlightable)
	repaint();
      return(true);
    } else
      return(false);
  }

  //-------------------------------------------------------------------------
  /** Changes the cursor from dragCursor to whatever it was at mouseDown.
   *  Erases the highlighting rectangle if necessary.
   * @return true if it is draggable, false otherwise.
   * @see Icon#dragCursor
   * @see Icon#highlightable
   * @see Icon#draggable
   */
  public boolean mouseUp(Event e, int x, int y) {
    if (draggable && contains(x, y)) {
      beingDragged = false;
      parentFrame().setCursor(previousCursor);
      if (highlightable)
	repaint();
      return(true);
    } else
      return(false);
  }

  //-------------------------------------------------------------------------
  /** If it is draggable, moves the Component to be recentered at
   *  the specified (x,y).
   * @see Icon#draggable
   */
  public boolean mouseDrag(Event e, int x, int y) {
    report("Drag", x, y);
    if (draggable && beingDragged) {
      centerAt(getLocation().x + x, getLocation().y + y);
      return(true);
    }
    return(false);
  }
  
  //-------------------------------------------------------------------------
  /** Calls the parent class' paint to draw the image, then
   *  draws a highlighting rectangle if highlightable is set.
   * @see Icon#highlightable
   */
  
  public void paint(Graphics g) {
    super.paint(g);
    if (highlightable && beingDragged)
      drawRect(g, 0, 0, width-1, height-1, highlightThickness, highlightColor);
  }

  //-------------------------------------------------------------------------
  /** Finds the Component (if any) that is under the specified (x,y)
   *  location. In the Container's coordinate system. Put here because
   *  Netscape has a bug in which it doesn't process contains() and getComponentAt()
   *  tests correctly. They work properly with appletviewer and
   *  for applications.
   * @see java.awt.Container#locate
   */
  public static Component componentUnder(int x, int y, Container container) {
    Component[] components = container.getComponents();
    Component component;
    Point componentLocation;
    Dimension componentSize;
    
    for(int i=0; i<components.length; i++) {
      component = components[i];
      componentLocation = component.getLocation();
      componentSize = component.getSize();
      if ((component != null) &&
	  (componentLocation.x <= x) &&
	  (componentLocation.y <= y) &&
	  ((componentSize.width + componentLocation.x)  >= x) &&
	  ((componentSize.height + componentLocation.y) >= y))
	return(component);
    }
    return(null);
  }

  //-------------------------------------------------------------------------
  // Ignored if the debug variable is false.
  
  private void report(String eventType, int x, int y) {
    debug("mouse" + eventType + " at (" + x + "," + y + ").");
  }

  //-------------------------------------------------------------------------
  // Used for setting the cursor.
  
  private Frame parentFrame() {
    if (topFrame != null)
      return(topFrame);
    else {
      Container parent = getParent();
      while (parent != null) {
	if (parent instanceof Frame) {
	  topFrame = ((Frame)parent);
	  return(topFrame);
	} else
	  parent = parent.getParent();
      }
      System.out.println("[parentFrame] parent is null.");
      return(null);
    }
  }
  
  //-------------------------------------------------------------------------
}

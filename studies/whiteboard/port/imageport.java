//*************************************************************************
/**
 * ImagePort.java - The message port control layer implementation.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package port;  

import port.Image; // from port.idl

import widget.DrawCanvas;

import java.util.*;
import java.io.*;

// GUI classes for requestLeadership
import java.awt.event.*;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.Timer;

// ------------------------------------------------------------------
// Control Layer
// ------------------------------------------------------------------

/**  
 * A controller for ImagePort.  It acts as a client to remote ports.
 * It also invokes and controls the display of all remote requests.
 * This class has a bi-directional link to the class ImagePortImpl.
 */
public class ImagePort extends MessagePort {

  /** The servant class for java native interface */
  private jni.ImagePort mJIP = null;

  /** A handle to the draw canvas */
  private DrawCanvas mDc = null;

  /** Base image in ARGB */
  private int mWidth;
  private int mHeight;
  private int[] mPixels = {};  // pixel = ARGB buffer

  /** This is flag is set by setPixels */
  private boolean mNewImage = true;

  /** An array of byte buffers for CGM data */
  private byte[][] mCgm = { {} };  // default to one buffer with zero length

  /**
   * Default constructor.
   */
  public ImagePort() {

  }

  /**
   * Constructor with an input user address and a draw canvas.  
   * It invokes the constructor in the superclass and sets up
   * a link to the draw canvas.  It also creates a native 
   * interface ImagePort for conversion to other image formats.
   *
   * @param address   User address (name@host)
   * @param dc The draw canvas that this object will control.
   */
  public ImagePort(String address, DrawCanvas dc) throws PortException {
    this();

    int k = address.indexOf("@");
    if (k < 0) throw new PortException("Invalid address format: " + address);
    String portName = address.substring(0,k); 

    // Set the interface layer objects, including the local server 
    // object and it TIE object.
    mPImpl = new ImagePortImpl(this);
    mPIF = new _tie_ImagePortIF((ImagePortImpl)mPImpl, portName);

    mDc = dc;
    mJIP = new jni.ImagePort(dc);

    init(address);
  }

  /**
   * Set the image width.
   * @param width   The image width
   */
  public void setWidth(int width) {
    mWidth = width;
  }

  /**
   * Set the image height.
   * @param height   The image height
   */
  public void setHeight(int height) {
    mHeight = height;
  }

  /**
   * Set the image pixels and the new image flag.
   * @param pixels   The image pixels
   */
  public void setPixels(int [] pixels) {
    mPixels = pixels;
    mNewImage = true;
  }

  /**
   * Set the cgm buffers
   * @param cgm   The cgm buffers
   */
  public void setCgm(byte[][] cgm) {
    mCgm = cgm;
  }


  /**
   * Get the image width.
   * @return  The image width
   */
  public int getWidth() {
    return mWidth;
  }

  /**
   * Get the image height.
   * @return  The image height
   */
  public int getHeight() {
    return mHeight;
  }

  /**
   * Get the image pixels.
   * @return  The image pixels
   */
  public int [] getPixels() {
    return mPixels;
  }

  /**
   * Get the cgm buffers.
   * @return   The cgm buffers
   */
  public byte[][] getCgm() {
    return mCgm;
  }

  /**
   * Get the image at this port.  Used when first connecting
   * to this port.
   *
   * @return  The port.Image data structure at this port.
   */
  public Image getImage() {

    // Image structure defined in port.idl
    Image image = new Image();

    image.name = "Markup image from " + mUserInfo.address;
    image.cgm = mCgm[0];

    image.width = mWidth;
    image.height = mHeight;
    image.pixels = mPixels;

    return image;
  }

  /**
   * Set the image to the input image.  Will show it in the
   * draw canvas if it is present.
   *
   * @param image  The image to be set
   */
  public void setImage(Image image) {
    showMessage(image.name);

    mCgm = new byte[1][];  // use only one buffer here
    mCgm[0] = image.cgm;
    debug("cgm length = " + mCgm[0].length);

    mWidth = image.width;
    mHeight = image.height;
    mPixels = image.pixels;
    debug("pixels length = " + mPixels.length);

    // invoke method on draw canvas if data buffers not empty
    if (mDc != null) {
      if (mCgm[0].length > 0) mDc.getGlyphsFrom(this);
      if (mPixels.length > 0) mDc.getImageFrom(this);
    }
  }

  /**
   * Try to set this port to be a leader.  Will call 
   * requestLeadership if needed.
   *
   * @exception if leadership is not granted by remote ports
   */
  public void setLeader() throws PortException {
    ImagePortImpl p = (ImagePortImpl) mPImpl;

    if (!p.getLeader()) {  // if not a leader
      ImagePortIF pLeader 
	= p.requestLeadership(mUserInfo.address 
	  + " requests to be the group leader to send image.", mUserInfo);

      if (pLeader != null) 
	debug("Leadership granted by " + pLeader.getUserInfo().address);

      p.setLeader(true);
    }
  }

  /**
   * Connect this port to the target port at the input address.
   * This simply calls the super class's connect method and then
   * display the image at the local port.
   *
   * @param address    the address of the target port
   * @return  The target port's object reference
   * @exception if this port cannot become a leader of its current group, or <BR>
   *		if exception is thrown from MessagePort.connect() <BR>
   * @see MessagePort.connect()
   */
  public MessagePortIF connect(String address) throws PortException {

    // Try to make this port a leader
    try {
      setLeader();
    } catch (PortException e) {
      throw new PortException("You must be a group leader before joining another group.\n" + e.toString());
    }

    // Must use narrow here or it gets ClassCastException
    ImagePortIF target = ImagePortIFHelper.narrow( 
    	(org.omg.CORBA.Object) super.connect(address) );

    ImagePortImpl p = (ImagePortImpl) mPImpl;

    // Send to other ports connected to this port on behalf
    // of the target port.
    p.propagateImage(target.getImage(), target.getUserInfo());

    // Finally, relieve the leadership role of this port
    p.setLeader(false);

    return (MessagePortIF) target;
  }

  /**
   * Process leadership request to this port. 
   *
   * @param message  The original message from the initiator
   * @return  The object reference of this port, if
   *    leadership is granted.
   *
   * @exception if leadership is not granted
   */
  public ImagePortIF requestLeadership(String message)
  throws PortException {

    ImagePortIF pLeader = (ImagePortIF) mPIF;

    if (mDc != null) {  // prompt user for request

      final JOptionPane pane = new JOptionPane(message,
        JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);

      final JDialog dialog = pane.createDialog(null, "Leadership Request");

      // Set a timer to time out the dialog in case no one is
      // attending it.
      int delay = 5000;    // delay in msec
      Timer t = new Timer( delay, 
        // an inner class instance to handle event
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	    // debug(e.toString());
	    pane.setValue(new Integer(0));  // timeout to YES
	    dialog.dispose();
	    return;
          }
        }
      );

      t.setRepeats(false);   // just fire once
      t.start();

      // now get value from the dialog
      dialog.show();
      Object selectedValue = pane.getValue();

      if(selectedValue instanceof Integer) {
        if ( ((Integer)selectedValue).intValue() == 0 ) {
	  // grant leadership
	  return pLeader;
	}
      }

      // Otherwise deny request
      throw new PortException("Cannot send image to group.  Leadership request denied by " + mUserInfo.address);

    }  // if (mDc != null) ...

    // else always grant leadership
    return pLeader;

  }

  /**
   * Send this port's image to all ports in the group connected 
   * to this port.  The image (including cgm)
   * should have be set before invoking this method.  Leadership
   * flag is checked before actually sending the image.
   *
   * @exception if not connected to a group
   */
  public void sendImage() throws PortException {

    if (getNumberOfConnectedPorts() == 0) {
      throw new PortException("You are not connected to a group.");
    }

    // Try to make this port a leader
    setLeader();

    // Image structure defined in port.idl
    Image image = new Image();

    image.name = "Markup image from " + mUserInfo.address;
    image.cgm = mCgm[0];

    if (mNewImage) {  // only send new images to avoid network delay
      image.width = mWidth;
      image.height = mHeight;
      image.pixels = mPixels;
      mNewImage = false;

    } else {
      image.pixels = new int[0];
    }

    // send to other ports connected to this port
    ((ImagePortImpl)mPImpl).propagateImage(image, mUserInfo);
  }

  /**
   * Inovkes some methods for testing.
   * This can be overridden by the subclasses.
   *
   * @param s   A string argument (optional).
   * @exception From 
   */
  public void invoke(String s) throws PortException {

    if (s.startsWith("i")) {
      show("invoking sendImage ... ");
      sendImage();

    } else {
      show("invoke - make this port a leader (default)");
      show("invoke i - sendImage");

      show("\ninvoking setLeader ... ");

      setLeader();
    }

  }

  //  Methods for the native interface in jni

  /** 
   * Read an image file using the native interface image port.  
   * @param file filename.  Its extension determines the file type and
   *     	the calls used in ImagePort.c
   * @return 0 normal; -1 error;
   * @see jni.ImagePort
   */
  public void read (String file) {
    mJIP.read(file);

    // extract data
    mWidth = mJIP.width;
    mHeight = mJIP.height;
    setPixels(mJIP.pixels);  // this will set the new image flag
    mCgm = mJIP.cgm;
  }

  /** 
   * Write an image file using the native interface image port.   
   * @param file filename.  Its extension determines the file type and
   *        	   the calls used in ImagePort.c
   * @return 0 normal; -1 error;
   * @see jni.ImagePort
   */
  public void write (String file) {
    // assign data first
    mJIP.width = mWidth;
    mJIP.height = mHeight;
    mJIP.pixels = mPixels;
    mJIP.cgm = mCgm;

    mJIP.write(file);
  }

  /**
   * Main code for testing.  This provides a terminal emulator
   * to debug the code.  Use "inovke" to invoke the test 
   * method defined above.
   */
  public static void main(String[] args) throws Exception {
    System.out.println("Usage: java ImagePort [port_name]");

    String portName = null;
    if (args.length >= 1) {
      portName = args[0];
    } else {
      portName = System.getProperty("user.name");  // default to username
    }

    // get the debug flag
    String debug = System.getProperty("debug");
    if (!debug.equals("")) mDebug = Integer.valueOf(debug).intValue();

    String computerName = System.getProperty("computerName");
    if (computerName == null) computerName = "localhost";

    String address = portName + "@" + computerName;

    ImagePort mP = new ImagePort(address, null);

  }
}

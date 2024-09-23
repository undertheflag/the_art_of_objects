//*************************************************************************
/**
 * ImagePortImpl.java - The message port interface layer implementation.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package port;  

import java.util.*;
import java.io.*;

// ------------------------------------------------------------------
// Interface Layer
// ------------------------------------------------------------------

/** 
 * Implementation for ImagePortIF on the server side of the port.  
 * This class has a bi-directional link to the class ImagePort,
 * which is in the control layer.
 */
class ImagePortImpl extends MessagePortImpl 
implements ImagePortIFOperations {

  protected boolean mLeader = true;  // leadership flag

 /**
   * Constructor.
   *
   * @param p  The ImagePort object in the control layer.
   */
  public ImagePortImpl(ImagePort p) {
    super(p);
  }

  /**
   * Set the leadership flag.
   * @param leader  Either true or false.   
   */
  public void setLeader(boolean leader) {
    mLeader = leader;
  }

  /**
   * Get the leadership flag.
   *
   * @return  True if this is a leader, otherwise false.   
   */
  public boolean getLeader() {
    return mLeader;
  }

  /**
   * Find the ImagePortIF object reference for the input key.
   * Be sure to use narrow to get subclass interfaces.
   *
   * @param address  Address of the port to be returned.
   * @return Reference to ImagePortIF.  Null if not found.
   */
  ImagePortIF findImagePortIF(String address) {
    return ImagePortIFHelper.narrow( 
      (org.omg.CORBA.Object)findMessagePortIF(address) );
  }

  /**
   * Request leadership from the group.  This is a propagating 
   * operation.
   *
   * Note that "this" userinfo is used when propagating to other 
   * ports.  This ensures that they won't propagate back to this
   * port.
   *
   * @param message  The original message from the initiator
   * @param userInfo The user info of the caller
   * @return  The object reference of the old leader in the group, if
   *    leadership is granted.  Null if no one was a leader.
   *
   * @exception if leadership is not granted
   */
  synchronized public ImagePortIF requestLeadership(String message, UserInfo userInfo)
  throws PortException {

    ImagePortIF pLeader = null;

    // request leadership from control layer if this is a leader
    if (mLeader) {
      pLeader = ((ImagePort)mP).requestLeadership(message);
      if (pLeader != null) {  // granted
        mLeader = false;  // take away leader from this port
	return pLeader;  // return this port as the old leader
      }
    }

    // Otherwise propagate the request to others

    String address = userInfo.address;
    ImagePortIF sender = findImagePortIF(address);
    ImagePortIF p = null;

    if (sender != null) mP.debug("sender: " + sender.getUserInfo().address
                     + " (" + sender.getClass() + ")" );

    // propagate to ports connected to this one
    for (Enumeration e = getConnectedPorts(); e.hasMoreElements(); ) {

      MessagePortIF mport = (MessagePortIF) e.nextElement();
      mP.debug("connected object: " + mport.getUserInfo().address
                     + " (" + mport.getClass() + ")" );

      // Must use narrow instead of casting for Corba objects
      p = ImagePortIFHelper.narrow( (org.omg.CORBA.Object)mport );

      if (p == null) continue;
      else mP.debug("narrowed object: " + p.getUserInfo().address
                     + " (" + p.getClass() + ")" );

      // exclude the sender by comparing address keys
      if ( !p.getUserInfo().address.equalsIgnoreCase(address) ) {
        pLeader = p.requestLeadership(message, getUserInfo());
        if (pLeader != null) return pLeader;  // return if granted
      }
    }

    return null;
  }

  /**
   * Display input image at this port and then propagate it to others.
   * The sender is excluded.
   *
   * Note that "this" userinfo is used when propagating to other 
   * ports.  This ensures that they won't propagate back to this
   * port.
   *
   * @param image   the image
   * @param userInfo  user information of the caller
   */
  public void propagateImage(Image image, UserInfo userInfo) {

    // do not echo image back since it will nullify it for repeated send
    if (userInfo != mP.mUserInfo)
      ((ImagePort)mP).setImage(image);  // use the method in control layer

    String address = userInfo.address;

    // propagate to ports connected to this one
    for (Enumeration e = getConnectedPorts(); e.hasMoreElements(); ) {

      // Must use narrow instead of casting for Corba objects
      MessagePortIF mport = (MessagePortIF) e.nextElement();
      ImagePortIF p = ImagePortIFHelper.narrow( (org.omg.CORBA.Object)mport );

      // exclude the sender by comparing address keys
      if ( !p.getUserInfo().address.equalsIgnoreCase(address) ) {
        p.propagateImage(image, getUserInfo());
      }
    }

  }

  /**
   * Get the image at this port.  Used when first connecting
   * to this port.
   *
   * @return  The port.Image data structure at this port.
   */
  public Image getImage() {
    return ((ImagePort)mP).getImage();
  }

}

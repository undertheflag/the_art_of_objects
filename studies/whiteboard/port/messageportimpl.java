//*************************************************************************
/**
 * MessagePortImpl.java - The message port interface layer implementation.
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
 * Implementation for MessagePortIF on the server side of the port.  
 * This class has a bi-directional link to the class MessagePort,
 * which is in the control layer.
 */
class MessagePortImpl implements MessagePortIFOperations {

  protected MessagePort mP = null;  // the port object in the control layer

  protected Dictionary mMessagePortIFs = new Hashtable();


  /**
   * Constructor.
   *
   * @param p  The MessagePort object in the control layer.
   */
  public MessagePortImpl(MessagePort p) {
    mP = p;
  }

  /**
   * Get the number of ports connected to this port.  
   *
   * @return number of ports connected to this port
   */
  public int getNumberOfConnectedPorts() {
    return mMessagePortIFs.size();
  }

  /**
   * Get an enumeration for all connected ports.
   *
   * @return enumeration for all connected ports.
   */
  public Enumeration getConnectedPorts() {
    return mMessagePortIFs.elements();
  }

  /**
   * Set the link to the local port object in the control layer.  Called by 
   * the constructor of MessagePort.
   *
   * @param mp  A reference to the local port object in the control layer
   */
  void setMessagePort(MessagePort p) {
    mP = p;
  }

  /**
   * Find the MessagePortIF object reference for the input key.
   *
   * @param address  Address of the port to be returned.
   * @return Reference to MessagePortIF.  Null if not found.
   */
  MessagePortIF findMessagePortIF(String address) {
    return (MessagePortIF) mMessagePortIFs.get(address);
  }


  /**
   * Connect the input port to this one.  The address of the user
   * is used as the key in the hashtable.    It also 
   * does a callback to connect this to the input port.
   *
   * @param portIf  The MessagePortIF of the initiator
   * @param userInfo The user info of the initiator
   * @exception if this port already contains the initiator's port
   */
  public void connect(MessagePortIF portIF, UserInfo userInfo)
    throws PortException {

    try {
      // a string of random MessagePort ID 
      String address = userInfo.address;

      MessagePortIF p = (MessagePortIF) mMessagePortIFs.get(address);

      // duplicate user address not allowed
      if (p != null) {
	throw new PortException("This port already has the user: " + address );
      }

      // save the MessagePort in the dictionary with address as key
      mMessagePortIFs.put(address, portIF);

      mP.show("...connection established with: " + address);

    } catch (Exception e) {
      throw new PortException(e.toString());
    }

  }

  /**
   * Disconnect the port for the input user from this one.  It also 
   * does a callback to disconnect this from the input port.
   *
   * @param userInfo The user info of the initiator
   * @exception if no port is found for the initiator
   */
  public void disconnect(UserInfo userInfo) throws PortException {
    MessagePortIF p = null;
    String address = userInfo.address;

    p = (MessagePortIF) mMessagePortIFs.remove(address);
    if (p == null) {
      throw new PortException("No port found for user: " + address );
    }
  }

  /**
   * Display message at this port and then propagate it to others.
   * The sender is excluded.
   *
   * Note that "this" userinfo is used when propagating to other 
   * ports.  This ensures that they won't propagate back to this
   * port.
   *
   * @param msg   the message
   * @param userInfo  user information of the initiator
   */
  public void propagateMessage(String msg, UserInfo userInfo) {

    mP.showMessage(msg);  // use the method in control layer

    String address = userInfo.address;
    MessagePortIF sender = (MessagePortIF) mMessagePortIFs.get(address);

    // propagate to ports connected to this one
    for (Enumeration e = mMessagePortIFs.elements(); e.hasMoreElements(); ) {
      MessagePortIF p = (MessagePortIF) e.nextElement();

      // exclude the sender
      if ( p != sender ) {
	// System.out.println( p.getUserInfo().address + " - " + address );
        p.propagateMessage(msg, getUserInfo());
      }
    }

  }

  /**
   *  Get user information for this port.
   *
   * @return  user information for this port
   */
  public UserInfo getUserInfo() {
    return mP.getUserInfo();
  }

  /**
   *  Get an array of user information for all ports in the group.
   *  This is a propagating operation.  
   *
   * @param userInfo  The initiator, which is excluded from the 
   *     the propagation call.
   * @return  an array of user information for all connected ports
   */
  public UserInfo [] getUserInfoList(UserInfo userInfo) {
    UserInfo u = getUserInfo();  // this port
    UserInfo [] uis = null;
    Vector uiv = new Vector();

    uiv.addElement(u);  // this goes first

    String address = userInfo.address;
    MessagePortIF sender = (MessagePortIF) mMessagePortIFs.get(address);

    // propagate to ports connected to this one
    for (Enumeration e = mMessagePortIFs.elements(); e.hasMoreElements(); ) {
      MessagePortIF p = (MessagePortIF) e.nextElement();

      // exclude the sender
      if ( p != sender ) {
        uis = p.getUserInfoList(u);
	for (int i=0; i<uis.length; i++) {
	  uiv.addElement(uis[i]);
	}
      }
    }

    // convert to array
    uis = (UserInfo []) uiv.toArray( new UserInfo[1] );
    return uis;

  }

  // package wide functions

  /**
   * Return a list of the keys of the ports connected to this port
   * directly.  The classes of the ports are also shown.
   */
  String listConnectedPorts() {
    String s = "";
    for (Enumeration e = mMessagePortIFs.keys(); e.hasMoreElements(); ) {
      String key = (String) e.nextElement();
      s += "  " + key;
      // s += " (" + mMessagePortIFs.get(key).getClass() + ")";
      if (e.hasMoreElements()) s += "\n";
    }
    return s;
  }

}

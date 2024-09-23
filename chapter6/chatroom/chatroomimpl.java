//*************************************************************************
/**
 * ChatRoomImpl.java - An implementation for the ChatRoomIF interface.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Vector;

import java.rmi.RemoteException;

/**
 * This implements the ChatRoomIF interface.  It demonstrates callbacks
 * (send) from the server object (ChatRoom).
 * <P>
 * This class extends UnicastRemoteObject so can be run as a RMI server.
 * 
 */
public class ChatRoomImpl extends java.rmi.server.UnicastRemoteObject 
    implements ChatRoomIF {

  /**
   * @serial name of this ChatRoom
   */
  private String name;  

  /**
   * @serial a list of users
   */
  private Vector users = new Vector();

  /** 
   * Constructs a ChatRoomImpl object.
   *
   * @param name	name of this ChatRoom
   * @exception RemoteException if remote invocation throws an exception
   */
  public ChatRoomImpl(String name) throws RemoteException {
    this.name = name;
  }

  /** 
   * Returns the name of this chatroom.
   *
   * @return the name of this chatroom
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Signs the chat user on to this ChatRoom.
   *
   * @param user the chat user
   * @exception RemoteException if remote invocation throws an exception
   */
  public void signOn(ChatUserIF user) throws RemoteException {
    users.addElement(user);
    send( "(" + user.getName() + ") connected");
  }

  /** 
   * Signs off the chat user from this ChatRoom.
   *
   * @param user the chat user
   * @exception RemoteException if remote invocation throws an exception
   */
  public void signOff(ChatUserIF user) throws RemoteException {
    users.removeElement(user);
    send( "(" + user.getName() + ") signed off");
  }

  /** 
   * Sends the message to all ChatRoom users.
   *
   * @param message the message
   * @exception RemoteException if remote invocation throws an exception
   */
  public void send(String message) throws RemoteException {
    show(message);
    java.util.Enumeration e = users.elements();
    while (e.hasMoreElements()) {
      ChatUserIF c = (ChatUserIF) e.nextElement();
      c.send(message);
    }
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    String s = "Chat room: " + name + "\n";
    s += "  Users: ";
    java.util.Enumeration e = users.elements();
    while (e.hasMoreElements()) {
      ChatUserIF c = (ChatUserIF) e.nextElement();
      try {
        s += " " + c.getName();
      } catch (Exception ex) {
        // do nothing
      }
    }
    return s;
  }

  /** 
   * Shows the input string.
   *
   * @s the string to be shown
   */
  public static void show(String s) { 
    System.out.println(s);
  }


  /**
   * A test that returns a string.  Note it is a static method.
   *
   * @return result of the test as a string
   * @exception Exception if any exception is thrown
   */
  public static String test() throws Exception {
    String s = "";

    ChatRoomIF cr = new ChatRoomImpl("Sports");

    ChatUserIF user1 = new ChatUserImpl("John");
    ChatUserIF user2 = new ChatUserImpl("Mary");

    cr.signOn(user1);
    cr.signOn(user2);

    s += "\n";
    s += cr.toString();

    cr.send( "(" + ((ChatRoomImpl)cr).name + ") Good Morning!  Fans.");

    return s;
  }

  /**
   * Main method for testing.
   * 
   * @exception Exception if any exception is thrown
   */
  public static void main(String args[]) throws Exception {

    if (args.length >0 && args[0].equals("test")) {
      show(test());
      System.exit(0);
    }

    String name = "ChatRoom";

    // Create and install a security manager 
    if (System.getSecurityManager() == null) { 
      System.setSecurityManager(new java.rmi.RMISecurityManager()); 
    } 

    ChatRoomImpl obj = new ChatRoomImpl(name); 

    // Bind this object instance to the name 
    java.rmi.Naming.rebind(name, obj); 
    show(name + " ready."); 
    show("Will exit automatically in 1 min."); 
    
    Thread.sleep(60000);  
    System.exit(0);

  }

}

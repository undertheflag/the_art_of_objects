//*************************************************************************
/**
 * ChatUserImpl.java - An implementation for the ChatUserIF interface.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.*;
import java.io.*;
import java.rmi.RemoteException;

/**
 * This implements the ChatUserIF interface by displaying the messages
 * sent from a chatroom.   It demonstrates callbacks
 * (send) from the server object (ChatRoom).
 */
public class ChatUserImpl extends java.rmi.server.UnicastRemoteObject 
    implements ChatUserIF {

  /**
   * @serial name of this user
   */
  private String name;

  /** 
   * Constructs a ChatUserImpl object.
   *
   * @param name	name of this ChatUser
   * @exception RemoteException if remote invocation throws an exception
   */
  public ChatUserImpl(String name) throws RemoteException {
    this.name = name;
  }

  /** 
   * Returns the name of this user.
   *
   * @return the name of this user
   * @exception RemoteException if remote invocation throws an exception
   */
  public String getName() throws RemoteException { 
    return name; 
  }

  /** 
   * Displays the message sent from a ChatRoom.
   *
   * @exception RemoteException if remote invocation throws an exception
   */
  public void send(String message) throws RemoteException {
    show("Display to " + name + " - " + message);
  }


  /**
   * Runs a command-line terminal for the object o with the input.
   */
  public void runTerminal(ChatRoomIF o, InputStream input) {

    /* read command input */
    BufferedReader instream = new BufferedReader(new InputStreamReader(input));
    /* Print help message describing the legal commands */
    printHelp();
    System.out.println();

    while (true) {
      System.out.print("> ");
      String inputLine = "";

      try {  /* read a line of command input */
        inputLine = instream.readLine();
      } catch (Exception e) {  // exceptions
        show("  " + e.toString());
	e.printStackTrace();
      }

      if (inputLine == null) { /* end of input */
	return;
      }

      if (inputLine.startsWith("#")) { // comment line
	continue;
      }

      // Tokenize the command input with a StringTokenizer.
      // Space and \t are separators
      StringTokenizer tokenizer = new StringTokenizer(inputLine, " 	");
      if (!tokenizer.hasMoreTokens()) continue;
      String command = tokenizer.nextToken();

      try {

	if ("help".equals(command)) {
	  printHelp();
	  continue;
	}

	else if ("quit".equals(command)) {
	  try { 
	    o.signOff(this);
	  } catch (Exception e) { }  // do nothing
	  System.exit(0);
	}

	else {
	  o.send("(" + name + ") " + inputLine);
	}

      } catch (Exception e) {
        System.out.println(e.getMessage());
      }

    }

  }

  /** 
   * Shows help.
   */
  static void printHelp() {
    show("-----------------------------------------------");
    show("Enter: command option1 option2 ...");
    show("Valid commands");
    show("  help                     // print this message");
    show("");
    show("  quit                     // quit");
    show("-----------------------------------------------");
  }

  static String readString(StringTokenizer tokenizer) throws TerminalException {
    if (tokenizer.hasMoreElements()) 
      return tokenizer.nextToken();
    else 
      throw new TerminalException(" Unexpected end of command input");
  }

  static int readInt(StringTokenizer tokenizer) throws TerminalException {
    if (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken();
      try {
        return Integer.valueOf(token).intValue();
      } catch (NumberFormatException e) {
        throw new TerminalException(" Number Format Exception reading \""+token+ "\"");
      }
    }
    else 
      throw new TerminalException(" Unexpected end of command input");
  }

  static float readFloat(StringTokenizer tokenizer) throws TerminalException {
    if (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken();
      try {
        return Float.valueOf(token).floatValue();
      } catch (NumberFormatException e) {
        throw new TerminalException(" Float Number Format Exception reading \""+token+ "\"");
      }
    }
    else 
      throw new TerminalException(" Unexpected end of command input");
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
   * @param username	name of this user
   * @param host 	the host
   * @return result of the test as a string
   * @exception Exception if any exception is thrown
   */
  public static String test(String username, String host) throws Exception {
    String s = "";

    return s;
  }

  /**
   * Main method.
   * 
   * @exception Exception if any exception is thrown
   */
  public static void main(String args[]) throws Exception {

    show("Usage: java ChatUserImpl [username] [host] [message_file]");
    show("   default username = ChatUser");
    show("   default host = localhost");

    String username = "ChatUser";
    String host = "localhost";

    if (args.length >= 1) {
      username = args[0];
    }

    if (args.length >= 2) {
      host = args[1];
    }

    // Get input stream
    InputStream input = System.in;
    if (args.length >= 3) {
      try { input = new FileInputStream(args[2]); }
      catch (FileNotFoundException e){
        System.out.println(e.toString());
      }
    }

    ChatUserImpl user = new ChatUserImpl(username);

    // Create and install a security manager 
    if (System.getSecurityManager() == null) { 
      System.setSecurityManager(new java.rmi.RMISecurityManager()); 
    } 

    // Bind this object instance to the name 
    java.rmi.Naming.rebind(username, user); 
    show(username + " registered."); 

    // connect to server at host
    String objRef = "//" + host + "/ChatRoom";
    ChatRoomIF cr = (ChatRoomIF) java.rmi.Naming.lookup(objRef);

    cr.signOn(user);

    user.runTerminal(cr, input);

  }

}


/**
 * An exception for the terminal simulator.
 */
class TerminalException extends Exception {
  public TerminalException (String s) {
    super(s);
  }
}

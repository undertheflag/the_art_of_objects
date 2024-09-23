//*************************************************************************
/**
 * MessagePort.java - The message port control layer implementation.
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
// Control Layer
// ------------------------------------------------------------------

/**  
 * A controller for MessagePortImpl.  It acts as a client to remote ports.
 * It also invokes and controls the display of all remote requests.
 * This class has a bi-directional link to the class MessagePortImpl.
 */
public class MessagePort {

  public static int mDebug = 0;  // show debug messages if > 0

  protected UserInfo mUserInfo = new UserInfo();  // identify this port

  /** The local port IF.  Used for connecting to remote ports. */
  protected MessagePortIF mPIF = null;

  /** The local port server object.  Used for invoking interface 
      operations. */
  protected MessagePortImpl mPImpl = null; 

  /**
   * Default constructor.
   */
  public MessagePort() {

  }

  /**
   * Constructor with an input user address argument.
   *
   * @param address   User address (name@host)
   */
  public MessagePort(String address) throws PortException {

    int k = address.indexOf("@");
    if (k < 0) throw new PortException("Invalid address format: " + address);
    String portName = address.substring(0,k); 

    // Set the interface layer objects, including the local server 
    // object and it TIE object.
    mPImpl = new MessagePortImpl(this);
    mPIF = new _tie_MessagePortIF(mPImpl, portName);

    init(address);
  }

  /**
   * Constructor with an input MessagePortIF argument, which is
   * the local server object for this port.
   *
   * @param pIF		The local server object for this port.
   */
  public MessagePort(MessagePortIF pIF) {
    mPIF = pIF;

    // get the Impl object
    mPImpl = (MessagePortImpl) ((_tie_MessagePortIF) pIF)._delegate();

    mPImpl.setMessagePort(this);  // set backward link
  }

  /**
   * Initialize the port.  It sets up user info from the input 
   * saddress, tarts a thread to run the server object 
   * for this port, and starts another thread for 
   * the terminal emulator.
   *
   * @param address   User address (name@host)
   * @exception if address format is invlaid <BR>
   */
  protected void init(String address) throws PortException {

    mUserInfo.address = address;
    mUserInfo.portNumber = 20000;

    int k = address.indexOf("@");
    if (k < 0) throw new PortException("Invalid address format: " + address);
    String portName = address.substring(0,k); 
    String host = address.substring(k);

    // Initiliaze the ORB & BOA
    String [] ss = { " " };
    org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(ss, null);
    org.omg.CORBA.BOA boa = ((com.visigenic.vbroker.orb.ORB)orb).BOA_init();

    // export the object reference
    boa.obj_is_ready(mPIF);

    debug(mPIF + " is ready.");
    show( "Your port is now active as: " + address);

    // A thread to wait for requests
    (new BoaThread("MessagePort", boa)).start();

    // Another for the terminal 
    (new TerminalThread("Message Port Terminal", this)).start();

  }

  /**
   * Return the IF object in the interface layer.
   *
   * @return the IF object in the interface layer
   */
  MessagePortIF getMessagePortIF() {
    return mPIF;
  }

  /**
   * Get the number of ports connected to this port's Impl object.  
   *
   * @return number of ports connected to this port's Impl object
   */
  public int getNumberOfConnectedPorts() {
    return mPImpl.getNumberOfConnectedPorts();
  }

  /**
   *  Get user information for this port.
   *
   * @return  user information for this port
   */
  public UserInfo getUserInfo() {
    return mUserInfo;
  }


  /**
   * Show the input message.
   * @param msg  message to be shown
   */
  public void showMessage(String msg) {
    show(msg);
  }

  /**
   * Connect this port to the target port at the input address.
   *
   * @param address    the address of the target port
   * @return  The target port's object reference
   * @exception if address format is invlaid, or <BR>
   *		if binding to target fails, or <BR>
   *		if a loop connection is detected <BR>
   */
  public MessagePortIF connect(String address) throws PortException {

    int k = address.indexOf("@");
    if (k < 0) throw new PortException("Invalid address format: " + address);
    
    String portName = address.substring(0,k); 
    String host = address.substring(k+1); 

    // use smart agent at host
    Properties props = new Properties();
    if (host != null) props.put("ORBagentAddr", host);

    // Initiliaze the ORB.
    String [] ss = { " " };
    org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(ss, props);

    // default options: no deferred bind, do rebind
    org.omg.CORBA.BindOptions options = new org.omg.CORBA.BindOptions();

    debug("...connecting to " + portName + "@" + host);

    // Locate the target server object
    MessagePortIF target = null;

    // Try a few times
    for (int i=0; i<3; i++) {
      target = MessagePortIFHelper.bind(orb, portName, host, options);
      if (target != null) break;
    }

    if (target == null) {
      throw new PortException("Cannot connect to " + portName + "@" + host);
    }

    UserInfo targetUI = target.getUserInfo();

    if (getNumberOfConnectedPorts() > 0) {
      // make sure we are not forming a loop
      UserInfo [] uis = target.getUserInfoList(target.getUserInfo());

      // check if anyone in the list exists is a connected port
      for (int i=0; i<uis.length; i++) {
        MessagePortIF p = mPImpl.findMessagePortIF((uis[i]).address);
        if (p != null) {
          throw new PortException("Loop connection not allowed!"
	    + " Already connected to a group that includes "
            + portName + "@" + host + " (via " + (uis[i]).address + ")" );
        }
      }
    }

    target.connect( mPIF , mUserInfo );

    // set up link from this to the target port
    mPImpl.connect(target, targetUI);

    return target;
  }

  /**
   * Disconnect this port from the group.  Ports connected to this
   * are reconnected to a new target port, which is one of the connected
   * ports.
   *
   * @exception if you are not connected to a group
   */
  public void disconnect() throws PortException {
    MessagePortIF newTargetMP = null;
    UserInfo newUI = null;

    if (getNumberOfConnectedPorts() > 0) {
      // Some ports are connected to this port.
      // Reconnect them to the new target port, which is 
      // one of the connected ports.

      Enumeration enum = mPImpl.getConnectedPorts();
      for ( ; enum.hasMoreElements(); ) {
	MessagePortIF p = (MessagePortIF) enum.nextElement();
	UserInfo ui = p.getUserInfo();

	p.disconnect(mUserInfo);    // disconnect backward links
	mPImpl.disconnect(ui); // disconnect forward links

	if (newTargetMP == null) {
	  newTargetMP = p;  // assign this port to be the new one
	  newUI = ui;
	} else {
	  newTargetMP.connect(p, ui);    // connect to new port
	  p.connect(newTargetMP, newUI); // backward connection from new port
	}
      }

      // let the group know
      newTargetMP.propagateMessage(mUserInfo.address + " disconnected.", mUserInfo );

    } else {
      // No connected ports and not connecting to other port
      throw new PortException("You are not connected to a group.");
    }

  }

  /**
   * Return a list of ports connected to this port directly
   * as well as all ports in the group.
   */
  public String listConnectedPorts() {
    String s = "(Not connected to a group)";
    if ( getNumberOfConnectedPorts() == 0) return s;

    s = "Directly conected ports:\n" + mPImpl.listConnectedPorts();

    UserInfo [] uis = mPImpl.getUserInfoList(mUserInfo);

    s += "\n";
    s += "All ports in group:\n";

    for (int i=0; i<uis.length; i++) {
      if (i > 0) s += "\n";
      s += "  " + (uis[i]).address;
    }

    return s;
  }

  /**
   * Send message to all ports in the group connected to this port.
   * The sender is excluded though.
   *
   * @param msg   the message
   * @exception if you are not connected to a group
   */
  public void sendMessage(String msg) throws PortException {

    if (getNumberOfConnectedPorts() == 0) {
      throw new PortException("You are not connected to a group.");
    }

    // send to other ports connected to this port
    mPImpl.propagateMessage(msg, mUserInfo );
  }

  /**
   * Inovkes some methods for testing.  
   * This can be overridden by the subclasses.
   * 
   * @param s   A string argument
   * @exception From 
   */
  public void invoke(String s) throws PortException {

  }

  /**
   * Run a command-line terminal session on the object o using the input
   * stream.
   */
  public static void runTerminal(MessagePort o, InputStream input) {

    /* read command input */
    BufferedReader instream = new BufferedReader(new InputStreamReader(input));
    /* print help message describing the legal commands */
    printHelp();
    System.out.println();

    while (true) {
      System.out.print("> ");

      try {

        /* read a line of command input */
        String inputLine = instream.readLine();

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

        if ("help".startsWith(command)) {
          printHelp();
        }

        else if ("connect".startsWith(command)) {
          String address = readString(tokenizer);
          o.connect(address);
        }

        else if ("disconnect".startsWith(command)) {
	  o.disconnect();
        }

        else if ("send".startsWith(command)) {
          String msg = "";
	  try {
	    while (true) {  // scan input till the end
   	      msg += readString(tokenizer) + " ";
	    }
	  } catch (TerminalException e) {
	  }
	  o.sendMessage(msg);
        }

        else if ("invoke".startsWith(command)) {
          String s = "";
	  try {
	    s += readString(tokenizer);
	  } catch (TerminalException e) {
	  }
          o.invoke(s);
        }

        else if ("showports".startsWith(command)) {
          show(o.listConnectedPorts());
        }

        else if ("quit".startsWith(command)) {
	  if ( o.getNumberOfConnectedPorts() > 0) o.disconnect();
	  System.exit(0);  // end all threads
          return;
        }

        else {
          show(" Command not recognized.  Try \"help\"");
        }

      } catch (TerminalException e) {  // other command errors
        show("  " + e.toString());
        continue;

      } catch (PortException e) {  // exit on any other exceptions
        show("  " + e.toString());
        continue;

      } catch (Exception e) {  // exit on any other exceptions
        show("  " + e.toString());
        continue;
      }

    }

  }

  /**
   * Show help.
   */
  static void printHelp() {
    show("-----------------------------------------------");
    show("Enter: command option1 option2 ...");
    show("Valid commands");
    show("  help               // print this message");
    show("  connect user@host  // connect to another Shared Whiteboard ");
    show("  showports          // show ports connected to this");
    show("");
    show("Commands after connect");
    show("  send message ...        // send message to group");
    show("  invoke [option]         // invoke test");
    show("  disconnect              // disconnect from group");
    show("");
    show("  quit                    // disconnect and quit");
    show("-----------------------------------------------");
  }

  /**
   * Read and return a string from the input tokenizer.
   */
  static String readString(StringTokenizer tokenizer) throws TerminalException {
    if (tokenizer.hasMoreElements()) 
      return tokenizer.nextToken();
    else 
      throw new TerminalException(" Unexpected end of command input");
  }

  /**
   * Read and return an integer from the input tokenizer.
   */
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

  /**
   * Read and return a float from the input tokenizer.
   */
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
   * Show the input string.
   */
  public static void show(String s) {
    System.out.println(s);
  }

  /**
   * Show the input string if mDebug > 0.
   */
  public static void debug(String s) {
    if (mDebug > 0) System.out.println(s);
  }

  /**
   * The main code.
   */
  public static void main(String[] args) throws Exception {

    System.out.println("Usage: java MessagePort [port_name]");

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

    MessagePort mP = new MessagePort(address);

  }

}

//------------------------------------------------------------------
// Other helper classes
//------------------------------------------------------------------

/**
 * A thread to run the BOA.
 */
class BoaThread extends Thread {
  private org.omg.CORBA.BOA mBoa;

  public BoaThread(String name, org.omg.CORBA.BOA boa) {
    super(name);
    mBoa = boa;
  }

  public void run() {
    mBoa.impl_is_ready();
  }
}

/**
 * A thread to run the terminal simulator.
 */
class TerminalThread extends Thread {
  private MessagePort mP;
  private InputStream mInput = null;

  /**
   * Constructor with default input from console.
   *
   * @param name	name of this thread
   * @param p		handle to the port object
   */
  public TerminalThread(String name, MessagePort p) {
    super(name);
    mP = p;
    mInput = System.in;
  }

  /**
   * Constructor with specified input.
   *
   * @param name	name of this thread
   * @param p		handle to the port object
   * @param input	input source
   */
  public TerminalThread(String name, MessagePort p, InputStream input) {
    this(name, p);
    mInput = input;
  }

  public void run() {
    MessagePort.runTerminal(mP, mInput);
  }

}

/**
 * Exception from the terminal emulator.
 */
class TerminalException extends Exception {
  public TerminalException (String s) {
    super(s);
  }
}

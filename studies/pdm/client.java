//*************************************************************************
/**
 * Client.java - client for the pdm server
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import java.io.*;
import java.util.*;
import pdm.*;  // from pdm.idl

class TerminalException extends Exception {
  public TerminalException (String s) {
    super(s);
  }
}

public class Client {

  static CatalogIF manager;

  public static void main(String args[])  {
      String serverName = "PDM Server";

      show("Usage: java Client [host] [command_file.txt]");

      String host = null;
      if (args.length >= 1) {
	host = args[0];
      }

      // use smart agent at host
      Properties props = new Properties();
      if (host != null) props.put("ORBagentAddr", host);

      // Set for vbroker.  Must be done before ORB.init.
      java.lang.System.setProperty("org.omg.CORBA.ORBClass", 
	"com.visigenic.vbroker.orb.ORB");
      java.lang.System.setProperty("org.omg.CORBA.ORBSingletonClass", 
	"com.visigenic.vbroker.orb.ORB");

      // Initiliaze the ORB.
      org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(args, props);

      // default options: no deferred bind, do rebind
      org.omg.CORBA.BindOptions options = new org.omg.CORBA.BindOptions();

      // Locate the server object
      manager = CatalogIFHelper.bind(orb, serverName, host, options);
      show("...connected to " + serverName + " at " + host);

      // Get input stream
      InputStream input = System.in;
      if (args.length >= 2) {
        try { input = new FileInputStream(args[1]); }
        catch (FileNotFoundException e){}
      }

      runTerminal(manager, input);
  }

  // Run a command-line terminal session on the remote object o.
  public static void runTerminal(CatalogIF o, InputStream input) {

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
          continue;
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

        else if ("getname".startsWith(command)) {
	  show( o.getName() );
        }

        else if ("getpart".startsWith(command)) {
          String name = readString(tokenizer);
	  Part p = o.getPart(name);
	  if (p != null) showPartTree( p );
	}

        else if ("getparts".startsWith(command)) {
	  Part[] ps = o.getParts();
	  show("All parts in catalog:");
	  for (int i=0; i<ps.length; i++) {
	    Part p = ps[i];
	    showPartTree( p );
	  }
	}

        else if ("addpart".startsWith(command)) {
          String name = readString(tokenizer);

	  // Simply add an empty Part.  Note: must use arrays of zero
	  // length instead of null!
	  Attribute[] attributes = new Attribute[0];
	  Part[] components = new Part[0];

	  Part p = o.addPart(name, attributes, components);
	  if (p != null) show( "Added " + p.name );
	}

        else if ("removepart".startsWith(command)) {
          String name = readString(tokenizer);
	  o.removePartByName(name);
	  show( name + " removed" );
	}

        else if ("quit".startsWith(command)) {
          return;
        }

        else {
          show(" Command not recognized.  Try \"help\"");
        }

      } catch (TerminalException e) {  // other command errors
        show("  " + e.toString());
        continue;

      } catch (Exception e) {  // exit on any other exceptions
        show("  " + e.toString());
        continue;
      }

    }

  }

  static void printHelp() {
    show("-----------------------------------------------");
    show("Enter: command option1 option2 ...");
    show("Valid commands");
    show("  help                    // print this message");
    show("  getname                 // get the name of the catalog");
    show("  getpart name            // get the part with the name");
    show("  getparts                // get a list of all parts");
    show("  addpart name            // add a part with the name");
    show("  removepart name         // remove the part with the name");
    show("");
    show("  quit                    // logout and quit");
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

  public static void show(String s) {
    System.out.println(s);
  }

  // -------- some utility functions ------------

  /** 
   * Shows the input part by itself.
   */
  public static void showPart(Part p) throws Exception {
    if (p == null) return;
    show("  " + p.name);
    showAttributes(p);
  }

  /** 
   * Shows the input part with its components.
   */
  public static void showPartTree(Part p) throws Exception {
    if (p == null) return;
    showPart( p );
    showPartSubTree( p, "\t" );
  }

  /** 
   * Shows the input part's sub tree recursively.
   *
   * @param p	The input part.
   * @param s	The indentation string for display.
   */
  public static void showPartSubTree(Part p, String s) throws Exception {
    if (p == null) return;
    Part[] comps = p.components;
    for (int i = 0; i < comps.length; i++) {
       System.out.print(s);
       showPart( comps[i] );
       showPartSubTree( comps[i], s + "\t" );
    }
  }

  /** 
   * Shows the part's attributes.
   */
  public static void showAttributes(Part p) throws Exception {
    if (p == null) return;
    Attribute[] attrs = p.attributes;
    for (int i = 0; i < attrs.length; i++) {
      Attribute a = attrs[i];
      show( "\t\t\t" + a.name + " = " + a.value + " " + a.unit );
    }
  }

}

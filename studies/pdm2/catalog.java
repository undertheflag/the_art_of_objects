//*************************************************************************
/**
 * Catalog.java - a class that manages a collection of parts.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.io.*;
import java.util.*;

/**
 * This class manages a collection of parts in the persistent layer.
 * It is serializable.  This layer can be tested by itself (java Catalog).
 */
public class Catalog extends NamedObject {

  /** 
   * A vector of parts.  It uses PVector, which is a 
   * persistent enabled class. 
   */
  private PVector parts = new PVector();


  /**
   * Constructs this object with an empty name.
   *
   */
  public Catalog() {
    super("");
  }

  /**
   * Constructs this object.
   *
   * @param name	name of the catelog
   */
  public Catalog(String name) {
    super(name);
  }

  /**
   * Returns an array of part objects.  We extract each element in
   * the Vector to construct the returned array.
   *
   * @return  an array of part objects
   */
  public Part[] getParts() {
    Object[] o = parts.toArray();
    Part[] u = new Part[o.length];
    for (int i=0; i<o.length; i++) u[i] = (Part)o[i];
    return u;
  }

  /**
   * Returns the number of part objects.
   *
   * @return 	the number of part objects
   */
  public int getPartCount() {
    return parts.size();
  }

  /**
   * Returns the part object for the input part name.
   *
   * @param name 	part name for the returned part
   * @return Part object.  Null if none matched the part name.
   */
  public Part getPart(String name) {
    Enumeration e = parts.elements();
    while (e.hasMoreElements()) {
      Part u = (Part) e.nextElement();
      if (u.getName().equals(name)) return u;
    }
    return null;
  }

  /**
   * Returns whether the input part object is in the collection.
   *
   * @param part 	a part object
   * @return true if the input part object is found.  False otherwise.
   */
  public boolean contains(Part part) {
    return parts.contains(part);
  }

  /**
   * Adds a new Part.  The input parameters are the same as in
   * Part's constructor.
   *
   * @param name	name of the part
   * @return the newly added Part object
   */

  public Part addPart(String name) { 
    Part u = new Part(name);
    parts.addElement(u);
    return u;
  }

  /**
   * Adds a new Part.  
   *
   * @param part	the part object
   */

  public void addPart(Part part) { 
    parts.addElement(part);
  }

  /**
   * Removes the named part from the collection.
   *
   * @param name	name of the part
   * @exception Exception if the part is not found
   */
  public void removePart(String name) throws Exception {
    /* get the Part Object */
    Part u = getPart(name);
    if (u == null) throw new Exception("Part not found: " + name);

    /* Remove the Part object from the vector */
    parts.removeElement(u);
  }

  /**
   * Removes the input part object from the collection.
   *
   * @param part	the part object to be removed
   * @exception Exception if the part is not found
   */
  public void removePart(Part part) throws Exception {
    if (! parts.contains(part))
      throw new Exception("Part object not found: " + part.getName());
  }

  /**
   * Removes all part objects from the collection.
   *
   */
  public void removeAllParts( ) {
    parts.removeAllElements();
  }


  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    String s = getName() + "\n";
    Part[] c = getParts();
    for (int i=0; i<c.length; i++) {
      s += c[i].toString(true, "  ");  // recursive
    }
    return s;
  }



  // Run a command-line terminal session on the remote object o.
  public static void runTerminal(Catalog o, InputStream input) {

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

        if ("shutdown".equalsIgnoreCase(inputLine)) {
          DBManager.shutdown();
    	  System.exit(0);  // end all threads
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

	  // Simply add an empty Part.
	  Part p = o.addPart(name);
	  if (p != null) show( "Added " + p.getName() );
	}

        else if ("removepart".startsWith(command)) {
          String name = readString(tokenizer);
	  o.removePart(name);
	  show( name + " removed" );
	}

        else if ("quit".startsWith(command)) {
          DBManager.shutdown();
    	  System.exit(0);  // end all threads
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
    show("  shutdown                // shutdown DB & quit");
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

  // -------- some utility functions ------------

  /** 
   * Shows the input part by itself.
   */
  public static void showPart(Part p) throws Exception {
    if (p == null) return;
    show("  " + p.getName());
    showAttributes(p);
  }

  /** 
   * Shows the input part with its components.
   */
  public static void showPartTree(Part p) throws Exception {
    if (p == null) return;
    showPart( p );
    showPartSubTree( p, "  " );
  }

  /** 
   * Shows the input part's sub tree recursively.
   *
   * @param p	The input part.
   * @param s	The indentation string for display.
   */
  public static void showPartSubTree(Part p, String s) throws Exception {
    if (p == null) return;
    Part[] comps = p.getComponents();
    for (int i = 0; i < comps.length; i++) {
       System.out.print(s);
       showPart( comps[i] );
       showPartSubTree( comps[i], s + "  " );
    }
  }

  /** 
   * Shows the part's attributes.
   */
  public static void showAttributes(Part p) throws Exception {
    if (p == null) return;
    Attribute[] attrs = p.getAttributes();
    for (int i = 0; i < attrs.length; i++) {
      Attribute a = attrs[i];
      show( "      " + a.toString() );
    }
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
  public static String test(Catalog catalog) throws Exception {
    String s = "";

    Part p0 = new Part("resistorList");
    Part p1 = new Part("resistor1");
    Part p2 = new Part("resistor2");

    DoubleAttribute s1 = new DoubleAttribute("partNumber", 123, "");
    DoubleAttribute s2 = new DoubleAttribute("partNumber", 323, "");

    FloatAttribute f1 = new FloatAttribute("resistance", 5.6F, "Ohm");
    FloatAttribute f2 = new FloatAttribute("power", 0.1F, "W");
    FloatAttribute f3 = new FloatAttribute("length", 12F, "mm");

    p1.addAttribute(s1);
    p1.addAttribute(f1);
    p1.addAttribute(f2);
    p1.addAttribute(f3);

    f1 = new FloatAttribute("resistance", 12.6F, "Ohm");
    f2 = new FloatAttribute("power", 0.2F, "W");
    f3 = new FloatAttribute("length", 16F, "mm");

    p2.addAttribute(s2);
    p2.addAttribute(f1);
    p2.addAttribute(f2);
    p2.addAttribute(f3);

    catalog.addPart(p0);

    p0.addComponent(p1);
    p0.addComponent(p2);

    s += catalog.toString();
    s += "\n";

    return s;
  }

  /**
   * Main method for testing.
   * 
   * @exception Exception if any exception is thrown
   */
  public static void main(String argv[]) throws Exception {
    Catalog c1 = new Catalog("electronics");
    show(test(c1));
  }

}

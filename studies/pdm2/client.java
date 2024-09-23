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

public class Client {

  static CatalogIF manager;

  public static void main(String args[])  {
      String serverName = "PDM2 Server";

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

    PartIF part = null;

    // current PartIF;
    PartIF thePart = null;

    while (true) {
      System.out.print("> ");

      try {

        /* read a line of command input */
        String inputLine = instream.readLine();

        if (inputLine == null) { /* end of input */
          break;
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

	// -------- Catalog ----------- //

        else if ("getname".startsWith(command)) {
	  show("Catalog: " + o.getName() );
	  if (thePart != null) show("  Selected Part: " + thePart.getName() );
        }

        else if ("getpart".startsWith(command)) {
          String name = readString(tokenizer);
	  thePart = o.getPart(name);
	  if (thePart != null) showPart( thePart );
	  else show("None found: " + name);
	}

        else if ("getpartdata".startsWith(command)) {
          String name = readString(tokenizer);
	  PartData pd = o.getPartData(name);
	  if (pd != null) showPartDataTree( pd );
	  else show("None found: " + name);
	}

        else if ("getparts".startsWith(command)) {
	  PartIF[] ps = o.getParts();
	  show("All parts in catalog: " + o.getName());
	  for (int i=0; i<ps.length; i++) {
	    PartIF p = ps[i];
	    showPart( p );
	  }
	}

        else if ("getpartdatas".startsWith(command)) {
	  PartData[] ps = o.getPartDatas();
	  show("All part data in catalog: " + o.getName());
	  for (int i=0; i<ps.length; i++) {
	    PartData p = ps[i];
	    showPartDataTree( p );
	  }
	}

        else if ("addpart".startsWith(command)) {
          String name = readString(tokenizer);

	  // Simply add an empty Part.  Note: must use arrays of zero
	  // length instead of null!
	  AttributeData[] attributes = new AttributeData[0];
	  PartData[] components = new PartData[0];

	  PartIF p = o.addPart(name, attributes, components);
	  if (p != null) show( "Added " + p.getName() );
	  else show("Problem adding part .");
	}

        else if ("removepart".startsWith(command)) {
          String name = readString(tokenizer);
	  o.removePartByName(name);
	  show( name + " removed" );
	  thePart = null;  // no part selected
	}

        else if ("removeallparts".startsWith(command)) {
	  o.removeAllParts();
	  show("All parts removed" );
	  thePart = null;  // no part selected
	}

	// -------- selected Part ----------- //

        else if ("getcomponent".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
          String name = readString(tokenizer);
	  part = thePart.getComponent(name);
	  if (part != null) showPart( thePart = part );
	  else show("None found: " + name);
	}

        else if ("getcomponentdata".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
          String name = readString(tokenizer);
	  PartData pd = thePart.getComponentData(name);
	  if (pd != null) showPartDataTree( pd );
	  else show("None found: " + name);
	}

        else if ("getcomponents".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
	  PartIF[] ps = thePart.getComponents();
	  show("All components in part: " + thePart.getName());
	  for (int i=0; i<ps.length; i++) {
	    PartIF p = ps[i];
	    showPart( p );
	  }
	}

        else if ("getcomponentdatas".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
	  PartData[] ps = thePart.getComponentDatas();
	  show("All component data in: " + thePart.getName());
	  for (int i=0; i<ps.length; i++) {
	    PartData p = ps[i];
	    showPartDataTree( p );
	  }
	}

        else if ("addcomponent".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
          String name = readString(tokenizer);

	  // Simply add an empty Part.  Note: must use arrays of zero
	  // length instead of null!
	  AttributeData[] attributes = new AttributeData[0];
	  PartData[] components = new PartData[0];

	  part = thePart.addComponent(name, attributes, components);
	  if (part != null) {
            show( "Added " + part.getName() );
          }
	  else show("Problem adding part.");
	}

        else if ("removecomponent".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
          String name = readString(tokenizer);
	  thePart.removeComponentByName(name);
	  show( name + " removed" );
	}

        else if ("removeallcomponents".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
	  thePart.removeAllComponents();
	  show("All components removed" );
	}

	// -------- attributes of selected Part ----------- //

        else if ("getattribute".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
          String name = readString(tokenizer);
	  AttributeIF attr = thePart.getAttribute(name);
	  if (attr != null) showAttribute( attr );
	  else show("Attribute not found: " + name);
	}

        else if ("getattributedata".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
          String name = readString(tokenizer);
	  AttributeData ad = thePart.getAttributeData(name);
	  if (ad != null)
	    show( "      " + ad.name + " = " + ad.value + " " + ad.unit );
	  else show("Attribute not found: " + name);
	}

        else if ("getattributes".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
	  AttributeIF[] ps = thePart.getAttributes();
	  show("All attributes in part: " + thePart.getName());
	  for (int i=0; i<ps.length; i++) {
	    showAttribute( ps[i] );
	  }
	}

        else if ("getattributedatas".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
	  AttributeData[] as = thePart.getAttributeDatas();
	  show("All attribute data in: " + thePart.getName());
	  for (int i=0; i<as.length; i++) {
	    AttributeData a = as[i];
	    show( "      " + a.name + " = " + a.value + " " + a.unit );
	  }
	}

        else if ("addattribute".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
          String name = readString(tokenizer);
          double value = (double)readFloat(tokenizer);
          String unit = readString(tokenizer);

	  AttributeIF attr = thePart.addAttribute(name, value, unit);
	  if (attr != null) show( "Added " + attr.getName() );
	}

        else if ("removeattribute".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
          String name = readString(tokenizer);
	  thePart.removeAttributeByName(name);
	  show( name + " removed" );
	}

        else if ("removeallattributes".startsWith(command)) {
	  if (thePart == null) { show("no selected part."); continue; }
	  thePart.removeAllAttributes();
	  show("All attributes removed" );
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
    show("  getname                 // name of the current part|catalog");
    show("  getpart name            // get the part itself *");
    show("  getpartdata name        // get the part and its subtree ");
    show("  getparts                // get a list of all parts");
    show("  getpartdatas            // get the full tree of all part data");
    show("  addpart name            // add a part with the name");
    show("  removepart name         // remove the part with the name");
    show("  removeallparts          // remove all parts");
    show("    (* the resulting part becomes the selected part)");
    show("");
    show("Commands for selected part");
    show("  getcomponent name       // get the component itself *");
    show("  getcomponentdata name   // get the component and its subtree");
    show("  getcomponents           // get a list of all components");
    show("  getcomponentdatas       // get the full tree of all components");
    show("  addcomponent name       // add a component with the name");
    show("  removcomponent name     // remove the component with the name");
    show("  removeallcomponents     // remove all components");
    show("    (* the resulting part becomes the selected part)");
    show("");
    show("  getattribute name       // get the attribute itself");
    show("  getattributedata name   // get the attribute with data");
    show("  getattributes           // get a list of all attributes");
    show("  getattributedatas       // get all attribute data");
    show("  addattribute name value unit // add an attribute ");
    show("  removattribute name     // remove the attribute with the name");
    show("  removeallattributes     // remove all attributes");
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

  // --------- for interfaces ----------- //

  /** 
   * Shows the input part by itself.
   */
  public static void showPart(PartIF p) throws Exception {
    if (p == null) return;
    show("  " + p.getName());
    showAttributes(p);
  }

  /** 
   * Shows the input part with its components.
   */
  public static void showPartTree(PartIF p) throws Exception {
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
  public static void showPartSubTree(PartIF p, String s) throws Exception {
    if (p == null) return;
    PartIF[] comps = p.getComponents();
    for (int i = 0; i < comps.length; i++) {
       System.out.print(s);
       showPart( comps[i] );
       showPartSubTree( comps[i], s + "  " );
    }
  }

  /** 
   * Shows the part's attributes.
   */
  public static void showAttributes(PartIF p) throws Exception {
    if (p == null) return;
    AttributeIF[] attrs = p.getAttributes();
    for (int i = 0; i < attrs.length; i++) {
      showAttribute( attrs[i] );
    }
  }

  /** 
   * Shows the attribute's content.
   */
  public static void showAttribute(AttributeIF a) throws Exception {
    if (a == null) return;
    show( "      " + a.getName() + " = " + a.getValue() + " (" 
      + a.getUnit() + ")\n" );
  }

  // --------- for data structs ----------- //

  /** 
   * Shows the input part by itself.
   */
  public static void showPartData(PartData p) throws Exception {
    if (p == null) return;
    show("  " + p.name);
    showAttributeDatas(p);
  }

  /** 
   * Shows the input part with its components.
   */
  public static void showPartDataTree(PartData p) throws Exception {
    if (p == null) return;
    showPartData( p );
    showPartDataSubTree( p, "  " );
  }

  /** 
   * Shows the input part's sub tree recursively.
   *
   * @param p	The input part.
   * @param s	The indentation string for display.
   */
  public static void showPartDataSubTree(PartData p, String s) throws Exception {
    if (p == null) return;
    PartData[] comps = p.components;
    for (int i = 0; i < comps.length; i++) {
       System.out.print(s);
       showPartData( comps[i] );
       showPartDataSubTree( comps[i], s + "  " );
    }
  }

  /** 
   * Shows the part's attributes.
   */
  public static void showAttributeDatas(PartData p) throws Exception {
    if (p == null) return;
    AttributeData[] attrs = p.attributes;
    for (int i = 0; i < attrs.length; i++) {
      AttributeData a = attrs[i];
      show( "      " + a.name + " = " + a.value + " " + a.unit );
    }
  }

}

//*************************************************************************
/**
 * PdmApplet.java - Applet client for the pdm server
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import java.net.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.applet.Applet; 
import java.awt.Graphics; 
import netscape.javascript.JSObject;    // A special class from java40.jar

import pdm.*;  // from pdm.idl

public class PdmApplet extends Applet { 

  public static int debug = 0;

  String serverName = "PDM Server";
  String message = "Not connected"; 

  private String mInfo = null;

  private static final int WIDTH = 360;
  private static final int HEIGHT= 300;

  private CatalogIF pdm = null;  // the remote pdm server object

  private JSObject window, document;

  public PdmApplet() {

  }

  public void init() { 
    try {
      // Check to see if this class is there
      Class.forName("netscape.javascript.JSObject");

      // Get a reference to the main browser window from the applet.
      window = JSObject.getWindow(this);
      document = (JSObject) window.getMember("document");

    } catch (ClassNotFoundException e) {
      // do nothing
      System.out.println("Not using Javascript connection.");
    }

    // get debug setting from HTML page
    if ("1".equals(getParameter("debug"))) debug = 1;

    setLayout(new BorderLayout());

  }

  public void destroy() {

  }

 /** 
  * Connect to the input host using IOR file.
  *
  * @param host		the remote host
  */
  public void connect(String host) throws Exception {
    String iorFile = "pdm.ior";

    URL baseUrl = null;

    String temp = getParameter("ior");
    if (temp != null && !temp.equals("")) iorFile = temp;

    try {
      baseUrl = getCodeBase();
    } catch (Exception e) {
      // not in a normal applet enivronment
    }

    if (host == null || host.equals("")) {
      if (baseUrl != null) host = baseUrl.getHost();
    }

    if (debug >= 1) popMessage("connecting to: " + host);
    if (debug >= 1) System.out.println("baseUrl: " + baseUrl);
    if (debug >= 1) System.out.println("iorFile: " + iorFile);

    try {

      Properties props = new Properties();

      // since we are using IOR to get initial object reference,
      // we must disable gatekeeper and locator.
      props.put("ORBdisableLocator", "true");

      // Initiliaze the ORB, with this as argument
      org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(this, props);

      // Use IOR file to get object reference.  Direct bind won't work.
      InputStream inStream = new URL(baseUrl, iorFile).openStream();

      LineNumberReader input =
        new LineNumberReader(new InputStreamReader(inStream));
      org.omg.CORBA.Object object = orb.string_to_object(input.readLine());
      pdm = CatalogIFHelper.narrow(object);

      getRootPartName();

      message = "...connected to " + serverName + " at " + host + ".";
      repaint();

    } catch (org.omg.CORBA.NO_IMPLEMENT e) {
      popMessage("Cannot connect to \"" +serverName + "\" at " + host);
      e.printStackTrace(System.out);

    } catch (Exception e) {
      popMessage(e.toString());
      e.printStackTrace(System.out);
    }

  } 

 /** 
  * Connect to the input host using IOR file.  For testing outside of
  * an applet.
  *
  * @param iorFile	the IOR file
  */
  public void connect2(String iorFile) throws Exception {

    try {
      Properties props = new Properties();

      // since we are using IOR to get initial object reference,
      // we must disable gatekeeper and locator.
      props.put("ORBdisableLocator", "true");

      // Initiliaze the ORB, with this as argument
      org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(new String[0], props);

      // Use IOR file to get object reference.  Direct bind won't work.
      FileInputStream inStream = new FileInputStream(iorFile);

      LineNumberReader input =
        new LineNumberReader(new InputStreamReader(inStream));
      org.omg.CORBA.Object object = orb.string_to_object(input.readLine());
      pdm = CatalogIFHelper.narrow(object);

      getRootPartName();

      message = "...connected with " + iorFile + ".";
      repaint();

    } catch (org.omg.CORBA.NO_IMPLEMENT e) {
      popMessage("Cannot connect.");
      e.printStackTrace(System.out);

    } catch (Exception e) {
      popMessage(e.toString());
      e.printStackTrace(System.out);
    }

  } 

  /** A wrapper function for the private ones.
   *  It also handles exceptions.
   */
  public void execute(String method, String a1) throws Exception {
    if (method == null) return;

    try {
      if (method.equalsIgnoreCase("getRootPartName")) {
        getRootPartName();
      }

      else if (method.equalsIgnoreCase("getPart")) {
        getPart(a1);
      }

      else if (method.equalsIgnoreCase("getParts")) {
        getParts();
      }

      else if (method.equalsIgnoreCase("addPart")) {
        addPart(a1);
      }

      else {
        popMessage("No such method: " + method);
      }

    } catch (org.omg.CORBA.NO_IMPLEMENT e) {
      popMessage("Not connected to \"" +serverName + "\"");

    } catch (Exception e) {
      popMessage(e.toString());
      e.printStackTrace(System.out);
    }
  }

  private void getRootPartName() throws Exception {
    String s = pdm.getName();
    setMessage(s);
  }

  private void getPart(String name) throws Exception {
    String s = "";
    Part p = pdm.getPart(name);
    s += name + ":\\n";
    s += showPartTree( p );
    appendMessage(s);
  }

  private void getParts() throws Exception {
    String s = "";
    Part[] ps = pdm.getParts();
    s += "All parts in catalog:\\n";
    for (int i=0; i<ps.length; i++) {
      Part p = ps[i];
      s += showPartTree( p );
    }
    appendMessage(s);
  }

  private void addPart(String name) throws Exception {
    String s = "";
    Attribute[] attributes = new Attribute[0];
    Part[] components = new Part[0];

    Part p = pdm.addPart(name, attributes, components);
    appendMessage("Added part: " + name);
  }

  // -------- some utility functions ------------

  /** 
   * Shows the input part by itself.
   */
  public static String showPart(Part p) throws Exception {
    String s = "";
    if (p == null) return s;
    s += "  " + p.name + "\\n";
    s += showAttributes(p);
    return s;
  }

  /** 
   * Shows the input part with its components.
   */
  public static String showPartTree(Part p) throws Exception {
    String s = "";
    if (p == null) return s;
    s += showPart( p );
    s += showPartSubTree( p, "   " );
    return s;
  }

  /** 
   * Shows the input part's sub tree recursively.
   *
   * @param p	The input part.
   * @param s	The indentation string for display.
   */
  public static String showPartSubTree(Part p, String s) throws Exception {
    String rs = "";
    if (p == null) return rs;
    Part[] comps = p.components;
    for (int i = 0; i < comps.length; i++) {
       rs += s;
       rs += showPart( comps[i] );
       rs += showPartSubTree( comps[i], s + "   " );
    }
    return rs;
  }

  /** 
   * Shows the part's attributes.
   */
  public static String showAttributes(Part p) throws Exception {
    String s = "";
    if (p == null) return s;
    Attribute[] attrs = p.attributes;
    for (int i = 0; i < attrs.length; i++) {
      Attribute a = attrs[i];
      s += ( "\t" + a.name + " = " + a.value + " " + a.unit ) + "\\n";
    }
    return s;
  }


  public void paint(Graphics g) { 
    g.drawString(message, 20, 20); 
  } 

  public String getAppletInfo() {
    return "PDM demo applet";
  }

 /** 
  * Pop up a message window using javascript.
  *
  * @param s	the message
  */
  public void popMessage(String s) {
    if (window != null) window.eval("alert('" + s + "')");
    else System.out.println(s);
  }

 /** 
  * Append a message to a text area in the HTML page.
  *
  * @param s	the message
  */
  public void appendMessage(String s) throws Exception {
    s = "pdmForm.console.value +='" + s + "\\n';";
    if (document != null) document.eval(s);
    else System.out.println(s);
  }

 /** 
  * Set a message in a text area in the HTML page.
  *
  * @param s	the message
  */
  public void setMessage(String s) throws Exception {
    s = "pdmForm.console.value ='" + s + "\\n';";
    if (document != null) document.eval(s);
    else System.out.println(s);
  }

  public static void main(String args[]) {
    try {
      Frame f = new Frame("PDM Demo");
      PdmApplet p = new PdmApplet();

      if ("1".equals(System.getProperty("debug"))) debug = 1;

      p.connect2("pdm.ior");  // connect using ior file

      f.add("Center", p);
      f.setSize(300, 300);
      f.show();

    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }

}

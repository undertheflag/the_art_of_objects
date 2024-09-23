//*************************************************************************
/**
 * Server.java - an implementation for the Catalog interface
 *
 *   This implementation contains only the interface layer.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import pdm.*;  // from pdm.idl

import java.io.*;
import java.util.*;

/** 
 *  Implementation for CatalogIF.
 */
class CatalogImpl implements CatalogIFOperations {

  private String mName;
  private Vector mParts = new Vector();

  /** Constructor of the catalog implementation class. */
  public CatalogImpl() {
    mName = "Default Catalog";
  }

  /** Returns the name of the catalog. */
  public String getName() {
    return mName;
  }

  /** Returns an array of parts. */
  public Part[] getParts() {
    return (Part[]) mParts.toArray(new Part[0]);
  }

  /** Returns the number of parts in the catalog. */
  public int getPartCount() {
    return mParts.size();
  }

  /**
   * Find the Part object with the input name.
   *
   * @param partName 	Name of the part
   * @return Part object.  Throw PdmError if none matched.
   *
   * @exception PdmError if none matched
   */
  public Part getPart(String partName) throws PdmError {
    Enumeration e = mParts.elements();
    while (e.hasMoreElements()) {
      Part p = (Part) e.nextElement();
      if (p.name.equals(partName)) return p;
    }
    throw new PdmError("No such part found: " + partName);
  }

  /**
   * Returns true if the catalog contains the input part.
   */
  public boolean contains(Part part) {
    return mParts.contains(part);
  }

  /**
   * Add a new Part.
   *
   * @param name	Name of the part
   * @param attributes	Attributes of part
   * @param components  Components of the part in the form of an array
   *
   * @return The new part
   */
  public Part addPart(String name, Attribute[] attributes, Part[] components)
      throws PdmError {
    if (attributes == null) attributes = new Attribute[0];
    if (components == null) components = new Part[0];
    Part part = new Part(name, attributes, components);
    try {
      Client.showPart(part);
    } catch (Exception e) {
      throw new PdmError(e.getMessage());
    }
    mParts.addElement(part);
    return part;
  }

  /**
   * Remove the Part with the input name form the vector.
   *
   * @param partName  The name of the part to be removed.
   */
  public void removePartByName(String partName) throws PdmError {
    /* get the object */
    Part c = getPart(partName);
    if (c == null) throw new PdmError("Part not found: " + partName);

    /* Remove the Part Object from the vector */
    mParts.removeElement(c);
  }

  /**
   * Remove the input part from the catalog.
   *
   * @param part  The part to be removed.
   */
  public void removePart(Part part) throws PdmError {
    mParts.removeElement(part);
  }

  /**
   * Remove the all the parts.
   */
  public void removeAllParts() {
    mParts.removeAllElements();
  }

  public static void show(String s) {
    System.out.println(s);
  }

}

/** Implementation for Server main.
 */
public class Server {
  public static CatalogImpl catalog;
  public static org.omg.CORBA.ORB orb;

  public static void main(String[] args) throws PdmError {

    System.out.println("Usage: java Server");

    // Initiliaze the ORB & BOA
    String [] ss = { " " };
    orb = org.omg.CORBA.ORB.init(ss, null);
    org.omg.CORBA.BOA boa = ((com.visigenic.vbroker.orb.ORB)orb).BOA_init();

    // create the Server object
    String serverName = "PDM Server";
    catalog = new CatalogImpl();
    CatalogIF mgr = new _tie_CatalogIF(catalog, serverName);

    // add some sample parts
    addSampleParts(mgr);

    // Write the server object's IOR to a file, so clients can access it.
    try {
      FileWriter output = new FileWriter("pdm.ior");
      output.write(orb.object_to_string(mgr));
      output.close();
      System.out.println("Wrote IOR to file: pdm.ior");
    }
    catch(java.io.IOException e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }

    // export the object reference
    boa.obj_is_ready(mgr);
    System.out.println(mgr + " is ready.");

    // wait for requests
    boa.impl_is_ready();
  }

  public static void addSampleParts(CatalogIF mgr) throws PdmError {

    Attribute a1 = new Attribute("partNumber", 123, "");
    Attribute a2 = new Attribute("resistance", 5.6, "Ohm");
    Attribute a3 = new Attribute("power", 0.1, "W");
    Attribute a4 = new Attribute("length", 12, "mm");
    Attribute[] aa1 = { a1, a2, a3, a4 };
    Part p1 = new Part("resistor", aa1, new Part[0]);

    a1 = new Attribute("partNumber", 323, "");
    a2 = new Attribute("resistance", 12.6, "Ohm");
    a3 = new Attribute("power", 0.2, "W");
    a4 = new Attribute("length", 16, "mm");
    Attribute[ ] aa2 = { a1, a2, a3, a4 };
    Part p2 = new Part("resistor", aa2, new Part[0]);

    Part[] pp1 = { p1, p2 };
    Part pList1 = new Part("resistorList", new Attribute[0], pp1);

    a1 = new Attribute("partNumber", 13, "");
    a2 = new Attribute("capacitance", 12.5, "F");
    a3 = new Attribute("voltage", 30, "V");
    Attribute[] aa3 = { a1, a2, a3 };
    Part c1 = new Part("capacitor", aa3, new Part[0]);

    Part[] cc1 = { c1 };
    Part pList2 = new Part("capacitorList", new Attribute[0], cc1);

    Part[] ppp = { pList1, pList2 };

    mgr.addPart("electronics", new Attribute[0], ppp);
  }
}

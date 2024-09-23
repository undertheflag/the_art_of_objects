//*************************************************************************
/**
 * Catalog.java - a class that manages a collection of parts.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class manages a collection of parts.
 * 
 */
public class Catalog {

  /** A vector of parts.  We take a short cut here to use the Java Vector
   *  class so that we do not need to handle array expansion. 
   */
  private Vector parts = new Vector();

  /**
   * Constructs this object.
   *
   */
  public Catalog() {

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
   * @param name	name of the person
   * @param number	the part number
   * @return the newly added Part object
   */

  public Part addPart(String name, int number) { 
    Part u = new Part(name, number);
    parts.addElement(u);
    return u;
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
    if (! parts.removeElement(part) )
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

    Catalog cat = new Catalog();

    Part p1 = cat.addPart("resistor", 123);
    Part p2 = cat.addPart("antenna", 825);
    Part p3 = cat.addPart("CRT", 102);

    s += "The Catalog has " + cat.getPartCount() + " parts.\n";

    Tool t1 = new Tool("Radio");
    Tool t2 = new Tool("TV");

    t1.addPart(p1, 3, "for filtering");
    t1.addPart(p2, 1, "for receiving");

    t2.addPart(p1, 8, "for filtering");
    t2.addPart(p2, 1, "for receiving");
    t2.addPart(p3, 1, "for display");

    s += "List of tools:\n";

    s += t1.toString();
    s += "\n";
    s += t2.toString();
    s += "\n";

    try {
      t1.addPart(p2, 1, "for receiving");
    } catch (Exception ex) {
      s += "\n... Expected exception:\n";
      s += ex.toString();
    }
    return s;
  }

  /**
   * Main method for testing.
   * 
   * @exception Exception if any exception is thrown
   */
  public static void main(String argv[]) throws Exception {
    show(test());
  }

}

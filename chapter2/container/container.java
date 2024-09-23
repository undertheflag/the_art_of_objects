//*************************************************************************
/**
 * Container.java - a container class.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class contains a set of named objects.
 * 
 */
public class Container {

  /** Use a vector to contain the objects.
   *  This is a short cut.
   */
  private Vector objects = new Vector();

  /**
   * Constructs this container.
   *
   */
  public Container() {

  }

  /**
   * Returns an array of objects.  We extract each element in
   * the Vector to construct the returned array.
   *
   * @return  an array of objects
   */
  public Object[] getAll() {
    Object[] o = objects.toArray();
    Object[] u = new Object[o.length];
    for (int i=0; i<o.length; i++) u[i] = (Object)o[i];
    return u;
  }

  /**
   * Returns the number of objects in the container.
   *
   * @return 	the number of objects
   */
  public int getCount() {
    return objects.size();
  }

  /**
   * Returns the object with the input name.  The object's name
   * is given by the toString method.
   *
   * @param name 	name for the returned object
   * @return the object.  Null if none matched the name.
   */
  public Object get(String name) {
    Enumeration e = objects.elements();
    while (e.hasMoreElements()) {
      Object o = (Object) e.nextElement();
      if (o.toString().equals(name)) return o;
    }
    return null;
  }

  /**
   * Returns whether the input object is in the container.
   *
   * @param object 	an object
   * @return true if the input object is found.  False otherwise.
   */
  public boolean contains(Object object) {
    return objects.contains(object);
  }

  /**
   * Adds a new Object.
   *
   * @param object 	an object
   */
  public void add(Object object) {
    objects.addElement(object);
  }

  /**
   * Removes the named object from the container.
   *
   * @param name	name of the object
   * @exception Exception if the object is not found
   */
  public void remove(String name) throws Exception {
    /* get the Object */
    Object u = get(name);
    if (u == null) throw new Exception("Object not found: " + name);

    /* Remove the object from the vector */
    objects.removeElement(u);
  }

  /**
   * Removes the input object from the container.
   *
   * @param object	the object to be removed
   * @exception Exception if the object is not found
   */
  public void remove(Object object) throws Exception {
    if (! objects.removeElement(object) )
      throw new Exception("Object not found: " + object.toString());
  }

  /**
   * Removes all objects from the container.
   *
   */
  public void removeAll( ) {
    objects.removeAllElements();
  }


  /** 
   * Shows the input string.
   *
   * @param s 	the string to be shown
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

    Container ctn = new Container();

    MyObject mo1 = new MyObject("CD");
    MyObject mo2 = new MyObject("Laser Disk");
    MyObject mo3 = new MyObject("DVD");

    Integer i1 = new Integer(999);

    ctn.add(mo1);
    ctn.add(mo2);
    ctn.add(mo3);
    ctn.add(i1);

    s += "There are " + ctn.getCount() + " objects.\n";

    Object[] us = ctn.getAll();
    for (int i=0; i<us.length; i++) {
      s += us[i].toString();
      s += "\n";
    }

    // Make some changes 

    ctn.remove("DVD");

    s += "... after the changes\n";

    // test the get method
    Object[] u = ctn.getAll();
    for (int i=0; i<u.length; i++) {
      s += u[i].toString();
      s += "\n";
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

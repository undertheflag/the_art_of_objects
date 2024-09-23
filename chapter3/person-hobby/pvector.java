//*************************************************************************
/**
 * PVector.java - A persistent enabled vector class.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.io.*;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/** 
 * A light-weight persistent enabled vector class that contains 
 * an expandable array of objects.
 * The most frequently used methods of the java.util.Vector class
 * are implemented.
 * <P>
 * It can be used with PSE Pro, provided the following classes are
 * post-processed: PVector and PVector$Enumerator.
 * It is also serializable using the standard Java approach.
 */
public class PVector implements Serializable {

  // static attributes are not serialized.
  private final static int DEFAULT_INIT_SIZE = 8;

  // Note: do not use transient attributes here.  Their values
  // will not be restored by PSE Pro when deserialized.

  // The values of these attributes will be persistent.

  /** 
   * @serial 	initial size of the array
   */
  private int init_size = DEFAULT_INIT_SIZE;

  /** 
   * @serial 	size increment in the array
   */
  private int size_increment = init_size;

  /** 
   * @serial 	an array of objects
   */
  private Object[] objs;

  /** 
   * @serial 	index of next unused element of objs.  It equals
   *		the current size of the vector.
   */
  private int size;

  /**
   * Constructs a PVector.
   */
  public PVector() {
    objs = new Object[init_size]; // storage array
  }

  /**
   * Constructs a PVector with input arguments.
   *
   * @param init_size		initial size of array
   * @param size_increment	increment in size
   */
  public PVector(int init_size, int size_increment) {
    this.init_size = init_size;
    this.size_increment = size_increment;
    objs = new Object[init_size]; // storage array
  }

  /**
   * Returns the size of the vector.
   *
   * @return 	the size of the vector
   */
  public int size() {
    return size;
  }

  /** 
   * Returns an element of the array.
   */
  public Object elementAt(int index) throws ArrayIndexOutOfBoundsException {
    if (index >= size) throw new ArrayIndexOutOfBoundsException(index);
    else return objs[index];
  }

  /** 
   * Returns the objects as an array.
   *
   * @return the objects as an array
   */
  public Object[] toArray() {
    Object[] o = new Object[size];
    System.arraycopy(objs, 0, o, 0, size); // copy array elements
    return o;
  }

  /** 
   * Returns an enumeration for the objects.
   *
   * @return an enumeration for the objects
   */
  public Enumeration elements() {
    return new Enumerator();
  }

  /**
   * Returns whether the input object exists.  The <TT>equals</TT>
   * method is used to compare objects.
   *
   * @param object 	an object
   * @return true if the input object is found.  False otherwise.
   */
  public boolean contains(Object object) {
    for (int i=0; i<size; i++) {
      if( objs[i].equals(object) ) return true;
    }
    return false;
  }

  /**
   * Adds a new object.
   *
   * @param object 	an object
   */
  public boolean add(Object object) {
    // Grow array, if needed.
    if (objs.length == size) resize(objs.length + size_increment);
    objs[size++] = object;
    return true;
  }

  /**
   * Adds a new object.  Same as add except it returns void.
   *
   * @param object 	an object
   */
  public void addElement(Object object) {
    // Grow array, if needed.
    if (objs.length == size) resize(objs.length + size_increment);
    objs[size++] = object;
  }

  /**
   * Removes the input object if it is found in the vector.
   * The <TT>equals</TT> method is used to compare objects.
   * Do nothing if not found.
   *
   * @param object 	the object
   */
  public void removeElement(Object object) {
    for (int i=0; i<size; i++) {
      if( objs[i].equals(object) ) {
        System.arraycopy(objs, i+1, objs, i, size-i-1); // shift the elements
        size--;
	return;
      }
    }
    // do nothing if not found
  }

  /**
   * Removes all objects in the vector.  Resets size to zero afterwards.
   * Note that the array length remains unchanged though.
   */
  public void removeAllElements() {
    for (int i=0; i<size; i++) objs[i] = null;
    size = 0;
  }


  /******** Protected and Private Methods *********/

  /** 
   * An internal method to change the allocated size of the array.
   */
  protected void resize(int newsize) {
    // show("Increasing size by " + size_increment + " to " + newsize);
    Object[] oldobjs = objs;
    objs = new Object[newsize]; // create a new array
    System.arraycopy(oldobjs, 0, objs, 0, size); // copy elements
  }


  /**
   * Write the non-static and non-transient fields of the
   * current class to this stream.  Omit unused array elements
   * before writing the array. 
   */
  private void writeObject(ObjectOutputStream out) throws IOException {
    if (objs.length > size) resize(size);  // Compact the array.
    out.defaultWriteObject();              // Then write it out normally.
  }

  /** 
   * Compute the transient size field after deserializing the array.
   */
  private void readObject(ObjectInputStream in)
          throws IOException, ClassNotFoundException {
    in.defaultReadObject(); // read the fields normally
    // restore the transient fields here if needed
  }

  /**
   * An inner class that implements Enumeration for returning the
   * array elements.
   */
  private class Enumerator implements Enumeration {
    private int indexEnumerator = 0;
      
    /**
     * Tests if this enumeration contains more elements.
     *
     * @return true if and only if this enumeration object 
     *	     contains at least one more element; false otherwise.
     */
    public boolean hasMoreElements() {
      return (indexEnumerator < size);
    }

    /**
     * Returns the next element of this enumeration.
     *
     * @return the next element of this enumeration
     * @exception NoSuchElementException  if no more elements exist
     */
    public Object nextElement() {
      if (indexEnumerator >= size) 
        throw new NoSuchElementException("no more objects!");
      return objs[indexEnumerator++];
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
  public static String test() throws Exception {
    String s = "";

    PVector v = new PVector();

    v.add("First-object");

    java.util.Date d1 = new java.util.Date();
    v.add(d1);

    v.add("Number-3");
    v.add("Number-4");

    Object[] o = v.toArray();
    for (int i=0; i<o.length; i++) {
      s += "  " + o[i].toString();
    }

    v.removeElement(d1);

    s += "\n";
    s += "...after changes:\n";

    // Test another way
    Enumeration e = v.elements();
    while (e.hasMoreElements()) {
      Object oe = (Object) e.nextElement();
      s += "  " + oe.toString();
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

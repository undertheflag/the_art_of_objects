//*************************************************************************
/**
 * MyObject.java - A simple class with a name
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This is a simple class with a name.
 */
public class MyObject {

  // Private attribute in the MyObject class
  private String name;

  /** 
   * Constructs an object.
   *
   * @param name	name of the object
   */
  public MyObject(String name) {
    this.name = name; 
  }

  /** 
   * Returns the name of this object.
   *
   * @return the name of this object
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Sets the name of this object.
   *
   * @param name	the new name of this object
   */
  public void setName(String name) { 
    this.name = name; 
  }
 
  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    return name;
  }

}


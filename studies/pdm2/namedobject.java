//*************************************************************************
/**
 * NamedObject.java - A simple class with a name
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This is a simple class with a name.  It is also serializable.
 */
public class NamedObject implements java.io.Serializable {

  /**
   * @serial name of this object
   */
  private String name;


  /** 
   * Constructs an object.
   */
  public NamedObject() {

  }

  /** 
   * Constructs an object.
   *
   * @param name	name of the object
   */
  public NamedObject(String name) {
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


//*************************************************************************
/**
 * Part.java - A class describing a part
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * A class describing a part.
 * <P>
 * This is part of the shared object pool pattern.  A Part object may
 * be used in multiple tools.
 */
public class Part {
  /** the name of the part */
  private String name;

  /** the identification number of the part */
  private int number;

  /** A vector is used to contain the attributes that use this part. */
  private Vector attributes = new Vector();

  /** 
   * Constructs a part object.
   *
   * @param name	name of the part
   * @param number	the part number
   */
  public Part(String name, int number) {
    this.name = name; 
    this.number = number;
  }

  /** 
   * Returns the name of this part.
   *
   * @return the name of this part
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Returns the identification number of this part.
   *
   * @return the number of this part
   */
  public int getNumber() { 
    return number; 
  }

  /** 
   * Returns the information of this attribute as a string.
   *
   * @return the information of this attribute
   */
  public String toString() { 
    String s = name;
    s += " - " + getNumber() + "\n";
    return s;
  }
}

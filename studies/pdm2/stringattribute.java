//*************************************************************************
/**
 * StringAttribute.java - A class describing a string attribute
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing a string attribute.
 *
 */
public class StringAttribute extends Attribute {

  // Private attributes of the class
  private String value;

  /** 
   * Constructs a string attribute object.
   *
   * @param name	name to which this attribute belongs
   * @param value	string value of the attribute
   */
  public StringAttribute(String name, String value) {
    super(name);
    this.value = value;
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    return getName() + " = " + value + "\n";
  }
}

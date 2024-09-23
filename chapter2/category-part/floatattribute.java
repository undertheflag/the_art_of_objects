//*************************************************************************
/**
 * FloatAttribute.java - A class describing a float attribute
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing a float attribute.
 * This is part of the dynamic schema pattern.
 */
public class FloatAttribute extends Attribute {

  // Private attributes of the class
  private float value;

  /** 
   * Constructs a float attribute object.
   *
   * @param property	property to which this attribute belongs
   * @param value	float value of the attribute
   */
  public FloatAttribute(Property property, float value) {
    super(property);
    this.value = value;
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    // super.toString() returns the name
    return super.toString() + " = " + value + "\n";
  }
}

//*************************************************************************
/**
 * Attribute.java - A class describing a attribute
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing a attribute.  It is serializable.
 * 
 */
public class Attribute extends NamedObject {

  private double value;
  private String unit;

  /** 
   * Constructs an attribute object.  
   *
   * @param name	name of the attribute
   */
  public Attribute(String name) {
    super(name);
  }

  /** 
   * Returns the type of this attribute.
   *
   * @return the type of this attribute as a string
   */
  public String getType() { 
    String type = "Attribute";
    if (this instanceof StringAttribute) type = "StringAttribute";
    else if (this instanceof FloatAttribute) type = "FloatAttribute";
    else if (this instanceof DoubleAttribute) type = "DoubleAttribute";
    return type; 
  }

  /** 
   * Returns the information of this object as a string.
   * This is a place holder for subclasses.
   *
   * @return the information of this object
   */
  public String toString() { 
    return getName();
  }
}

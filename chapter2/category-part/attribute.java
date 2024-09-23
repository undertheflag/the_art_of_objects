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
 * A class describing a attribute.
 * This is part of the dynamic schema pattern.
 */
public class Attribute {

  /** The property object this attribute belongs to. */
  private Property property;

  /** 
   * Constructs an attribute object.  The name is actually stored
   * in a property object.
   *
   * @param property	property of the attribute
   */
  public Attribute(Property property) {
    this.property = property;
  }

  /** 
   * Returns the name of this attribute.
   *
   * @return the name of this attribute
   */
  public String getName() { 
    return property.getName();
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
    return type; 
  }

  /** 
   * Returns the information of this object as a string.
   * This is a place holder for subclasses.
   *
   * @return the information of this object
   */
  public String toString() { 
    return property.getName();
  }
}

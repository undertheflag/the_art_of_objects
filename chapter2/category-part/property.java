//*************************************************************************
/**
 * Property.java - A class describing a property
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing a property.
 * This is part of the dynamic schema pattern.
 */
public class Property {

  /** Public constant for float. */
  public static final char FLOAT = 'f';

  /** Public constant for string. */
  public static final char STRING = 's';

  /** The name of this property. */
  private String name;

  /** The type of this property (s = string; f = float). */
  private char type;

  /** 
   * Constructs a property object.
   *
   * @param name	name of the property
   * @param type	type of the property
   */
  public Property(String name, char type) {
    this.name = name;
    this.type = type;
  }

  /** 
   * Returns the name of this property.
   *
   * @return the name of this property
   */
  public String getName() { 
    return name;
  }

  /** 
   * Returns the type of this property.
   *
   * @return the type of this property as a char
   */
  public char getType() { 
    return type; 
  }

  /** 
   * Returns the information of this object as a string.
   * This is a place holder for subclasses.
   *
   * @return the information of this object
   */
  public String toString() { 
    return name + " (" + type + ")";
  }
}

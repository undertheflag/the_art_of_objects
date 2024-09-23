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
 * This is part of the dynamic schema pattern.  The link from Part to
 * Category is unidirectional.  You may make it bi-directional if you
 * desire.  That way one can tell what parts are included in a category.
 * The same is true between Attribute and Property.
 */
public class Part {
  /** the category of the part */
  private Category category;

  /** the identification number of the part */
  private int number;

  /** A vector is used to contain the attributes of this part. */
  private Vector attributes = new Vector();

  /** 
   * Constructs a part object.
   *
   * @param category	category of the part
   * @param number	the part number
   */
  public Part(Category category, int number) {
    this.category = category; 
    this.number = number;
  }

  /** 
   * Returns the category of this part.
   *
   * @return the category of this part
   */
  public Category getCategory() { 
    return category; 
  }

  /** 
   * Returns the name of this attribute.
   *
   * @return the name of this attribute
   */
  public String getName() { 
    return category.getName();
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
   * Returns the number of attributes in the part.
   *
   * @return 	the number of attributes
   */
  public int getAttributeCount() {
    return attributes.size();
  }

  /**
   * Returns the attribute with the input name.  
   *
   * @param name 	name for the returned attribute
   * @return the attribute.  Null if none matched the name.
   */
  public Attribute getAttribute(String name) {
    Enumeration e = attributes.elements();
    while (e.hasMoreElements()) {
      Attribute o = (Attribute) e.nextElement();
      if (o.getName().equals(name)) return o;
    }
    return null;
  }

  /**
   * Returns whether the input attribute is in the part.
   *
   * @param attribute 	a Attribute attribute
   * @return true if the input attribute object is found.  False otherwise.
   */
  public boolean contains(Attribute attribute) {
    return attributes.contains(attribute);
  }

  /** 
   * Returns the attributes under this part.
   *
   * @return the attributes under this part as an array
   */
  public Attribute[] getAttributes() { 
    // forces the array elements to be of type Attribute
    return (Attribute[]) attributes.toArray(new Attribute[0]);
  }

  /** 
   * Add a attribute that uses this part.
   *
   * @param attribute	a attribute that uses this part
   */
  public void addAttribute(Attribute attribute) {
    attributes.add(attribute);
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    String s = category.getName();
    s += " - " + getNumber() + "\n";

    s += "  Attributes:\n";
    Attribute[] c = getAttributes();
    for (int i=0; i<c.length; i++) {
      s += "    " + c[i].toString();
    }

    return s;
  }
}

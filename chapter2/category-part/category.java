//*************************************************************************
/**
 * Category.java - A class describing a category
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * A class describing a category.
 * <P>
 * This is part of the dynamic schema pattern.  The link from Part to
 * Category is unidirectional.  You may make it bi-directional if you
 * desire.  That way one can tell what parts are included in a category.
 * The same is true between Attribute and Property.
 */
public class Category {
  /** the name of the category */
  private String name;

  /** A vector is used to contain the properties that use this category. */
  private Vector properties = new Vector();

  /** 
   * Constructs a category object.
   *
   * @param name	name of the category
   */
  public Category(String name) {
    this.name = name; 
  }

  /** 
   * Returns the name of this category.
   *
   * @return the name of this category
   */
  public String getName() { 
    return name; 
  }

  /**
   * Returns the number of properties in the category.
   *
   * @return 	the number of properties
   */
  public int getPropertyCount() {
    return properties.size();
  }

  /** 
   * Returns the properties under this part.
   *
   * @return the properties under this part as an array
   */
  public Property[] getProperties() { 
    // forces the array elements to be of type Property
    return (Property[]) properties.toArray(new Property[0]);
  }

  /**
   * Returns the property with the input name.  
   *
   * @param name 	name for the returned property
   * @return the property.  Null if none matched the name.
   */
  public Property getProperty(String name) {
    Enumeration e = properties.elements();
    while (e.hasMoreElements()) {
      Property o = (Property) e.nextElement();
      if (o.getName().equals(name)) return o;
    }
    return null;
  }

  /**
   * Returns whether the input property is in the category.
   *
   * @param property 	a Property property
   * @return true if the input property object is found.  False otherwise.
   */
  public boolean contains(Property property) {
    return properties.contains(property);
  }

  /** 
   * Add a property that uses this category.
   *
   * @param property	a property that uses this category
   */
  public void addProperty(Property property) {
    properties.add(property);
  }

  /** 
   * Returns the information of this category as a string.
   *
   * @return the information of this category
   */
  public String toString() { 
    String s = name;
    s += "\n";

    s += "  Properties:\n";
    Property[] p = getProperties();
    for (int i=0; i<p.length; i++) {
      s += "    " + p[i].toString();
    }

    return s;
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

    Category c1 = new Category("resistor");

    Part p1 = new Part(c1, 123);
    Part p2 = new Part(c1, 323);

    Property prop1 = new Property("resistance (Ohm)", Property.FLOAT);
    Property prop2 = new Property("power (W)", Property.FLOAT);
    Property prop3 = new Property("length (mm)", Property.FLOAT);

    c1.addProperty(prop1);
    c1.addProperty(prop2);
    c1.addProperty(prop3);

    FloatAttribute f1 = new FloatAttribute(prop1, 5.6F);
    FloatAttribute f2 = new FloatAttribute(prop2, 0.1F);
    FloatAttribute f3 = new FloatAttribute(prop3, 12F);

    p1.addAttribute(f1);
    p1.addAttribute(f2);
    p1.addAttribute(f3);

    f1 = new FloatAttribute(prop1, 12.6F);
    f2 = new FloatAttribute(prop2, 0.2F);
    f3 = new FloatAttribute(prop3, 16F);

    p2.addAttribute(f1);
    p2.addAttribute(f2);
    p2.addAttribute(f3);

    s += c1.toString();
    s += "\n";

    s += "All parts:\n";
    s += p1.toString();
    s += "\n";
    s += p2.toString();
    s += "\n";

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

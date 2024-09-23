//*************************************************************************
/**
 * Part.java - A class describing a part in the persistent layer
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;

/**
 * A class describing a part in the persistent layer.  It is serializable.
 * <P>
 */
public class Part extends NamedObject {

  /** A persistent enabled vector is used to contain the attributes. */
  private PVector attributes = new PVector();

  /** A persistent enabled vector is used to contain the components. */
  private PVector components = new PVector();

  /** 
   * Constructs a part object.
   *
   * @param name	name of the part
   */
  public Part(String name) {
    super(name); 
  }

  /*********** Attributes ***********/

  /** 
   * Returns the attributes under this part.
   *
   * @return the attributes under this part as an array
   */
  public Attribute[] getAttributes() { 
    Object[] o = attributes.toArray();
    Attribute[] u = new Attribute[o.length];
    for (int i=0; i<o.length; i++) u[i] = (Attribute)o[i];
    return u;
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
   * @param attribute 	an attribute
   * @return true if the input attribute object is found.  False otherwise.
   */
  public boolean contains(Attribute attribute) {
    return attributes.contains(attribute);
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
   * Removes the named attribute from the extent.
   *
   * @param name	Name of attribute.
   * @exception Exception if the attribute is not found
   */
  public void removeAttribute(String name) throws Exception {
    /* get the attribute Object */
    Attribute c = getAttribute(name);
    if (c == null) throw new Exception("Attribute not found: " + name);

    /* Remove the attribute Object from the vector */
    attributes.removeElement(c);
  }

  /**
   * Removes the input attribute form the vector.
   *
   * @param attribute  The attribute to be removed.
   * @exception Exception if the attribute is not found
   */
  public void removeAttribute(Attribute attribute) throws Exception {
    if (!attributes.contains(attribute))
      throw new Exception("Attribute not found: " + attribute.getName());
    attributes.removeElement(attribute);
  }

  /**
   * Removes all attribute objects from the extent.
   */
  public void removeAllAttributes( ) {
    attributes.removeAllElements();
  }

  /*********** Components ***********/

  /** 
   * Returns the components under this part.
   *
   * @return the components under this part as an array
   */
  public Part[] getComponents() { 
    Object[] o = components.toArray();
    Part[] u = new Part[o.length];
    for (int i=0; i<o.length; i++) u[i] = (Part)o[i];
    return u;
  }

  /**
   * Returns the number of components in the part.
   *
   * @return 	the number of components
   */
  public int getComponentCount() {
    return components.size();
  }

  /**
   * Returns the component with the input name.  
   *
   * @param name 	name for the returned component
   * @return the component.  Null if none matched the name.
   */
  public Part getComponent(String name) {
    Enumeration e = components.elements();
    while (e.hasMoreElements()) {
      Part o = (Part) e.nextElement();
      if (o.getName().equals(name)) return o;
    }
    return null;
  }

  /**
   * Returns whether the input component is in the part.
   *
   * @param component 	a component
   * @return true if the input component object is found.  False otherwise.
   */
  public boolean contains(Part component) {
    return components.contains(component);
  }

  /** 
   * Add a component that uses this part.
   *
   * @param component	a component that uses this part
   */
  public void addComponent(Part component) {
    components.add(component);
  }

  /**
   * Removes the named component from the extent.
   *
   * @param name	Name of component.
   * @exception Exception if the component is not found
   */
  public void removeComponent(String name) throws Exception {
    /* get the component Object */
    Part c = getComponent(name);
    if (c == null) throw new Exception("Component not found: " + name);

    /* Remove the component Object from the vector */
    components.removeElement(c);
  }

  /**
   * Removes the input component form the vector.
   *
   * @param component  The component to be removed.
   * @exception Exception if the component is not found
   */
  public void removeComponent(Part component) throws Exception {
    if (!components.contains(component))
      throw new Exception("Component not found: " + component.getName());
    components.removeElement(component);
  }

  /**
   * Removes all component objects from the extent.
   */
  public void removeAllComponents( ) {
    components.removeAllElements();
  }


  /** 
   * Returns the information of this part.
   * If recursive is true, it will recursively calls toString() for all
   * components in this part.
   * 
   * @param recursive	whether to recursively call toString for all
   *			components in this part
   * @param lead	the leading string
   * @return the information of this part as a string
   */
  public String toString(boolean recursive, String lead) { 
    String s = lead + getName() + "\n";
    if (getAttributeCount() > 0) s += lead + "    Components:\n";
    Attribute[] c = getAttributes();
    for (int i=0; i<c.length; i++) {
      s += lead + "      " + c[i].toString();
    }

    if (!recursive) return s;

    Part[] ps = getComponents();
    for (int i=0; i<ps.length; i++) {
      Part p = ps[i];
      s += p.toString(true, lead+lead);
    }

    return s;
  }


}

//*************************************************************************
/**
 * PartImpl.java - implementation for the PartIF interface in pdm.idl
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import pdm.*;  // from pdm.idl

/** 
 * Implementation for PartIF.  It uses Part from the persistent layer as
 * a servant class.
 */
class PartImpl implements PartIFOperations {

  /** the servant persistent object */
  private Part part;


  /**
   * Constructs a PartImpl object with a null persistent object.
   *
   */
  public PartImpl() { 
    this.part = null;
  }

  /**
   * Constructs a PartImpl object from a persistent part object.
   *
   * @param     part   the persistent object
   */
  public PartImpl(Part part) { 
    this.part = part;
  }

  /**
   * Returns the persistent object.
   *
   * @return     part   the persistent object
   */
  public Part getPart() { 
    return part;
  }

  /**
   * Sets the persistent part object.
   *
   * @param     part   the persistent object
   */
  public void setPart(Part part) { 
    this.part = part;
  }

  /** 
   * Returns the name of the part. 
   *
   * @return the name of the part
   */
  public String getName() {
    String name = null;

    // Commands below may involve transaction.  We put them in
    // a synchronized block to make threads cooperating with 
    // each other.
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	name = part.getName();
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return name;
  }

  /** 
   * Sets the name of the part. 
   *
   * @param name	the name of the part
   */
  public void setName(String name) {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	part.setName(name);
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
  }

  /*********** Attributes ***********/

  /** Returns the number of attributes in the part. */
  public int getAttributeCount() {
    int count = 0;
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	count = part.getAttributeCount();
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return count;
  }

  /** Returns an array of attribute interfaces. */
  public AttributeIF[] getAttributes() {
    AttributeIF[] attributeIFs = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Attribute[] attributes = part.getAttributes();
	attributeIFs = new AttributeIF[attributes.length];
	for (int i=0; i<attributes.length; i++) {
  	  attributeIFs[i] = LayerMediator.getAttributeIF(attributes[i]);
        }
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return attributeIFs;
  }

  /** 
   * Returns an array of AttributeData.
   * This is a deep get.
   */
  public AttributeData[] getAttributeDatas() {
    AttributeData[] pDatas = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Attribute[] attributes = part.getAttributes();
	pDatas = new AttributeData[attributes.length];
	for (int i=0; i<attributes.length; i++) {
  	  pDatas[i] = AttributeImpl.toAttributeData(attributes[i]);
        }
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return pDatas;
  }

  /**
   * Returns the AttributeIF object with the input name.
   *
   * @param attributeName 	Name of the attribute
   * @return AttributeIF object.  Null if none matched.
   *
   * @exception PdmError never thrown
   */
  public AttributeIF getAttribute(String attributeName) throws PdmError {
    AttributeIF attributeIF = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Attribute attribute = part.getAttribute(attributeName);
	attributeIF = LayerMediator.getAttributeIF(attribute);
        DBManager.commitTrx();
      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return attributeIF;
  }

  /**
   * Returns the AttributeData object with the input name.
   * This is a deep get.
   *
   * @param attributeName 	Name of the attribute
   * @return AttributeData object.  Null if none matched.
   *
   * @exception PdmError never thrown
   */
  public AttributeData getAttributeData(String attributeName) throws PdmError {
    AttributeData attributeData = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Attribute attribute = part.getAttribute(attributeName);
	if (attribute == null) return null;
	attributeData = AttributeImpl.toAttributeData(attribute);
        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return attributeData;
  }

  /**
   * Returns whether the part contains the input attribute.
   *
   * @param attribute 	the attribute interface object
   * @return true if the part contains the input attribute.  False otherwise.
   */
  public boolean containsAttribute(AttributeIF attributeIF) {
    boolean result = false;
    
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Attribute attribute = LayerMediator.getAttribute(attributeIF);
	result = part.contains(attribute);
	DBManager.commitTrx();
      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return result;
  }

  /**
   * Add a new Attribute.
   *
   * @param name	name of the attribute
   * @param value	value of the part
   * @param unit	unit of the part
   *
   * @return the new AttributeIF object
   * @exception PdmError if there is transaction error
   */
  public AttributeIF addAttribute(String name, double value,
  			String unit)  throws PdmError {
    AttributeIF attributeIF = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();

	DoubleAttribute attribute
	  = new DoubleAttribute(name, value, unit);
	part.addAttribute(attribute);
	attributeIF = LayerMediator.getAttributeIF(attribute);

        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	throw new PdmError(e.getMessage());
      }
    }
    return attributeIF;
  }

  /**
   * Remove the Attribute with the input name form the part.
   *
   * @param attributeName  The name of the attribute to be removed.
   * @exception PdmError if the attribute is not found or transaction error occurs
   */
  public void removeAttributeByName(String attributeName) throws PdmError {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	part.removeAttribute(attributeName);
        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	throw new PdmError(e.getMessage());
      }
    }
  }

  /**
   * Remove the input attribute from the part.
   *
   * @param attribute  The attribute interface object
   * @exception PdmError if the attribute is not found or transaction error occurs
   */
  public void removeAttribute(AttributeIF attributeIF) throws PdmError {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	part.removeAttribute(LayerMediator.getAttribute(attributeIF));
        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	throw new PdmError(e.getMessage());
      }
    }
  }

  /**
   * Remove the all the attributes.
   */
  public void removeAllAttributes() {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	part.removeAllAttributes();
        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
  }


  /*********** Components ***********/

  /** Returns the number of components in the part. */
  public int getComponentCount() {
    int count = 0;
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	count = part.getComponentCount();

        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return count;
  }

  /** Returns an array of components as part interfaces. */
  public PartIF[] getComponents() {
    PartIF[] componentIFs = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Part[] parts = part.getComponents();
	componentIFs = new PartIF[parts.length];
	for (int i=0; i<parts.length; i++) {
  	  componentIFs[i] = LayerMediator.getPartIF(parts[i]);
        }
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return componentIFs;
  }

  /** 
   * Returns an array of components as PartData.
   * This is a deep get.
   */
  public PartData[] getComponentDatas() {
    PartData[] pDatas = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();

	Part[] parts = part.getComponents();
	pDatas = new PartData[parts.length];

	for (int i=0; i<parts.length; i++) {
  	  pDatas[i] = toPartData(parts[i]);
        }

        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return pDatas;
  }

  /**
   * Returns the component PartIF object with the input name.
   *
   * @param name 	Name of the component
   * @return PartIF object.  Null if none matched.
   *
   * @exception PdmError never thrown
   */
  public PartIF getComponent(String name) throws PdmError {
    PartIF componentIF = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Part p = part.getComponent(name);
	componentIF = LayerMediator.getPartIF(p);
        DBManager.commitTrx();
      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return componentIF;
  }

  /**
   * Returns the component PartData object with the input name.
   * This is a deep get.
   *
   * @param name 	Name of the component
   * @return PartData object.  Null if none matched.
   *
   * @exception PdmError never thrown
   */
  public PartData getComponentData(String name) throws PdmError {
    PartData partData = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Part p = part.getComponent(name);
	if (p == null) return null;

	partData = toPartData(p);

        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return partData;
  }

  /**
   * Returns whether this part contains the input component.
   *
   * @param componentIF 	the interface object for the component
   * @return true if the part contains the input part.  False otherwise.
   */
  public boolean containsComponent(PartIF componentIF) {
    boolean result = false;
    
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Part p = LayerMediator.getPart(componentIF);
	result = part.contains(p);
	DBManager.commitTrx();
	
      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return result;
  }

  /**
   * Add a new Component using the input data, 
   * which include PartData and AttributeData arrays.
   * <P>
   * We use the recursive operation createPart to do the job.
   * <P>
   * <I>This is a deep operation if the input arrays are not empty.</I>
   *
   * @param name	Name of the part
   * @param attributes	Attributes of part as AttributeData[]
   * @param components  Components of part as PartData[]
   *
   * @return the new PartIF object
   * @exception PdmError if a transaction error occurs
   */
  public PartIF addComponent(String name, AttributeData[] attributes, 
  			PartData[] components)  throws PdmError {
    PartIF componentIF = null;

    if (attributes == null) attributes = new AttributeData[0];
    if (components == null) components = new PartData[0];

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();

	// use the static operation in PartImpl
	Part p = createPart(name, attributes, components);

	componentIF = LayerMediator.getPartIF(p);
	part.addComponent(p);

        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	throw new PdmError(e.getMessage());
      }
    }
    return componentIF;
  }

  /**
   * Remove the component with the input name form the part.
   *
   * @param name  The name of the component to be removed.
   * @exception PdmError if the component is not found or transaction error occurs
   */
  public void removeComponentByName(String name) throws PdmError {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	part.removeComponent(name);
        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	throw new PdmError(e.getMessage());
      }
    }
  }

  /**
   * Remove the input component from the part.
   *
   * @param componentIF  The component interface object
   * @exception PdmError if the component is not found or transaction error occurs
   */
  public void removeComponent(PartIF componentIF) throws PdmError {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	Part p = LayerMediator.getPart(componentIF);
	part.removeComponent(p);
        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	throw new PdmError(e.getMessage());
      }
    }
  }

  /**
   * Remove the all the components.
   */
  public void removeAllComponents() {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	part.removeAllComponents();
        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
  }

  /*********** Special Operations ***********/
  //  for conversion between PartData and Part

  /**
   * Returns a PartData object for the input part and its entire subtree.
   * <P>
   * <I>This is a static recursive function.</I>
   *
   * @param part	the part object
   * @return the PartData for the Part and its subtree
   */
  public static PartData toPartData(Part p) {
    Attribute[] attr = p.getAttributes();
    AttributeData[] ad = new AttributeData[attr.length]; 

    // create the attribute data from the attribute objects
    for (int i=0; i<attr.length; i++) {
      ad[i] = AttributeImpl.toAttributeData(attr[i]);
    }

    Part[] components = p.getComponents();
    PartData[] pd = new PartData[components.length];

    // create the component data from the component objects recursively
    for (int i=0; i<components.length; i++) {
      pd[i] = toPartData(components[i]);
    }

    return new PartData(p.getName(), ad, pd);
  }

  /**
   * Creates a new Part and its entire subtree from the input data, 
   * which include PartData and AttributeData arrays.
   * <P>
   * <I>This is a static recursive function.</I>
   *
   * @param name	Name of the part
   * @param attributes	Attributes of part as AttributeData[]
   * @param components  Components of part as PartData[]
   *
   * @return the new Part object
   */
  public static Part createPart(String name, AttributeData[] attributes, 
  			PartData[] components) {
    Part part = new Part(name);

    if (attributes == null) attributes = new AttributeData[0];
    if (components == null) components = new PartData[0];

    // create the attributes from the input array
    for (int i=0; i<attributes.length; i++) {
      AttributeData ad = attributes[i];
      Attribute attr = new DoubleAttribute(ad.name, ad.value, ad.unit);
      part.addAttribute(attr);
    }

    // create the components from the input array recursively
    for (int i=0; i<components.length; i++) {
      PartData pd = components[i];
      Part comp = createPart(pd.name, pd.attributes, pd.components);
      part.addComponent(comp);
    }

    return part;
  }


  /** 
   * Shows the input string.
   *
   * @s the string to be shown
   */
  public static void show(String s) { 
    System.out.println(s);
  }


}

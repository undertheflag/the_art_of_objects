//*************************************************************************
/**
 * AttributeImpl.java - implementation for the AttributeIF interface
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import pdm.*;  // from pdm.idl

/** 
 * Implementation for AttributeIF.  It uses Attribute from the persistent
 * layer as a servant class.
 * <P>
 * Attribute has several subclasses.  This interface class wraps around
 * those subclasses so that we do not need an interface class for each
 * subclass.  See the use of "instanceof" below.
 */
class AttributeImpl implements AttributeIFOperations {

  /** the servant persistent object */
  private Attribute attribute;

  /**
   * Constructs a AttributeImpl object with a null persistent object.
   *
   * @param     attribute   the persistent object
   */
  public AttributeImpl() { 
    this.attribute = null;
  }

  /**
   * Constructs a AttributeImpl object from a persistent attribute object.
   *
   * @param     attribute   the persistent object
   */
  public AttributeImpl(Attribute attribute) { 
    this.attribute = attribute;
  }

  /**
   * Returns the persistent object.
   *
   * @return     attribute   the persistent object
   */
  public Attribute getAttribute() { 
    return attribute;
  }

  /**
   * Set the persistent attribute object.
   *
   * @param     attribute   the persistent object
   */
  public void setAttribute(Attribute attribute) { 
    this.attribute = attribute;
  }

  /** 
   * Returns the name of the attribute. 
   *
   * @return the name of the attribute
   */
  public String getName() {
    String name = null;

    // Commands below may involve transaction.  We put them in
    // a synchronized block to make threads cooperating with 
    // each other.
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	name = attribute.getName();
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return name;
  }

  /** 
   * Sets the name of the attribute. 
   *
   * @param name	the name of the attribute
   */
  public void setName(String name) {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	attribute.setName(name);
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
  }

  /** 
   * Returns the unit of the attribute. 
   *
   * @return the unit of the attribute
   */
  public String getUnit() {
    String unit = null;

    // Commands below may involve transaction.  We put them in
    // a synchronized block to make threads cooperating with 
    // each other.
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	unit = _getUnit(attribute);
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return unit;
  }

  /** 
   * Returns the unit of the attribute.  A private static function.
   *
   * @param attr the attribute
   * @return the unit of the attribute
   */
  private static String _getUnit(Attribute attr) {
    String unit = null;
    if (attr instanceof StringAttribute) {
      unit = "";
    } else if (attr instanceof FloatAttribute) {
      unit = ((FloatAttribute)attr).getUnit();
    } else if (attr instanceof DoubleAttribute) {
      unit = ((DoubleAttribute)attr).getUnit();
    }
    return unit;
  }

  /** 
   * Sets the unit of the attribute. 
   *
   * @param unit	the unit of the attribute
   */
  public void setUnit(String unit) {

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	if (attribute instanceof StringAttribute) {
	  // do nothing
	} else if (attribute instanceof FloatAttribute) {
	  ((FloatAttribute)attribute).setUnit(unit);
	} else if (attribute instanceof DoubleAttribute) {
	  ((DoubleAttribute)attribute).setUnit(unit);
	}
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
  }

  /** 
   * Returns the value of the attribute. 
   *
   * @return the value of the attribute
   */
  public double getValue() {
    double value = 0.0;

    // Commands below may involve transaction.  We put them in
    // a synchronized block to make threads cooperating with 
    // each other.
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	_getValue(attribute);
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return value;
  }

  /** 
   * Returns the value of the attribute.  A private static function.
   *
   * @param attr the attribute
   * @return the value of the attribute
   */
  private static double _getValue(Attribute attr) {
    double value = 0.0;
    if (attr instanceof StringAttribute) {
      value = 0.0;
    } else if (attr instanceof FloatAttribute) {
      value = (double)((FloatAttribute)attr).getValue();
    } else if (attr instanceof DoubleAttribute) {
      value = ((DoubleAttribute)attr).getValue();
    }
    return value;
  }

  /** 
   * Sets the value of the attribute. 
   *
   * @param value	the value of the attribute
   */
  public void setValue(double value) {

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();

	if (attribute instanceof StringAttribute) {
	  // do nothing
	} else if (attribute instanceof FloatAttribute) {
	  ((FloatAttribute)attribute).setValue((float)value);
	} else if (attribute instanceof DoubleAttribute) {
	  ((DoubleAttribute)attribute).setValue(value);
	}

        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
  }

  /**
   * Returns a AttributeData object for the input attribute.
   * This static function uses the _getValue and _getUnit static functions.
   *
   * @param attr	the attribute object
   * @return the AttributeData object
   */
  public static AttributeData toAttributeData(Attribute attr) {
    String name = attr.getName();
    double value = _getValue(attr);
    String unit = _getUnit(attr);
    return new AttributeData(name, value, unit);
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

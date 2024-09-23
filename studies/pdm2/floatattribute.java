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
 *
 */
public class FloatAttribute extends Attribute {

  // Private attributes of the class
  private float value;
  private String unit;

  /** 
   * Constructs a float attribute object.
   *
   * @param name	name to which this attribute belongs
   * @param value	float value of the attribute
   * @param unit	unit of the attribute
   */
  public FloatAttribute(String name, float value, String unit) {
    super(name);
    this.value = value;
    this.unit = unit;
  }

  /** 
   * Returns the value of the attribute. 
   *
   * @return the value of the attribute
   */
  public float getValue() {
    return value;
  }

  /** 
   * Returns the unit of the attribute. 
   *
   * @return the unit of the attribute
   */
  public String getUnit() {
    return unit;
  }

  /** 
   * Sets the value of the attribute. 
   *
   * @param value value of the attribute
   */
  public void setValue(float value) {
    this.value = value;
  }

  /** 
   * Sets the unit of the attribute. 
   *
   * @param unit unit of the attribute
   */
  public void setUnit(String unit) {
    this.unit = unit;
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    return getName() + " = " + value + " (" + unit + ")\n";
  }
}

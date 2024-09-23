//*************************************************************************
/**
 * Buyer.java - A class describing a buyer
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Vector;

/**
 * A class describing a buyer.
 * This class is a subclass of Role.
 */
public class Buyer extends Role {

  // Private attributes in the Buyer class
  private String name;
  private String address;


  /** 
   * Constructs a buyer object.
   *
   * @param name	name of the buyer
   * @param address	address of the buyer
   */
  public Buyer(String name, String address) {
    this.name = name; 
    this.address = address;
  }

  /** 
   * Returns the name of this buyer.
   *
   * @return the name of this buyer
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Returns the address of this buyer.
   *
   * @return the address of this buyer
   */
  public String getAddress() {
    return address; 
  }

  /** 
   * Sets the address of this buyer.
   *
   * @param address	the address of this buyer
   */
  public void setAddress(String address) { 
    this.address = address; 
  }

  /** 
   * Returns the information of this buyer as a string.
   * Information from the superclass is also included.
   *
   * @return the information of this buyer
   */
  public String toString() { 
    // call the operation in the superclass
    String s = super.toString();
    s += " - Buyer: " + name + "\n";
    s += "Address: " + address + "\n";
    return s;
  }

}

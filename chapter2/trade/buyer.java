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
 * This class is part of a binary association (Trade).
 */
public class Buyer {

  // Private attributes in the Buyer class
  private String name;
  private String address;

  /** A vector is used to contain the trades. */
  private Vector trades = new Vector();


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
   * Returns the trades of this buyer.
   *
   * @return the trades of this buyer as an array
   */
  public Trade[] getTrades() { 
    // forces the array elements to be of type Trade
    return (Trade[]) trades.toArray(new Trade[0]);
  }

  /** 
   * Add a new trade for this buyer.
   *
   * @param trade	the trade
   */
  public void addTrade(Trade trade) {
    trades.add(trade);
  }

  /** 
   * Returns the information of this buyer as a string.
   *
   * @return the information of this buyer
   */
  public String toString() { 
    String s = "Buyer: " + name + "\n";
    s += "Address: " + address + "\n";
    s += "Trades:\n";

    Trade[] t = getTrades();
    for (int i=0; i<t.length; i++) {
      s += "  " + t[i].toString();
    }

    return s;
  }

}


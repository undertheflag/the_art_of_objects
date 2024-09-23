//*************************************************************************
/**
 * Supplier.java - A class describing a supplier
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Vector;

/**
 * A class describing a supplier.
 * This class is part of a binary association (Trade).
 */
public class Supplier {

  // Private attributes in the Supplier class
  private String company_name;
  private String address;

  /** A vector is used to contain the trades. */
  private Vector trades = new Vector();


  /** 
   * Constructs a supplier object.
   *
   * @param company_name	company_name of the supplier
   * @param address		address of the supplier
   */
  public Supplier(String company_name, String address) {
    this.company_name = company_name; 
    this.address = address;
  }

  /** 
   * Returns the company_name of this supplier.
   *
   * @return the company_name of this supplier
   */
  public String getName() { 
    return company_name; 
  }

  /** 
   * Returns the address of this supplier.
   *
   * @return the address of this supplier
   */
  public String getAddress() {
    return address; 
  }

  /** 
   * Sets the address of this supplier.
   *
   * @param address	the address of this supplier
   */
  public void setAddress(String address) { 
    this.address = address; 
  }

  /** 
   * Returns the trades of this supplier.
   *
   * @return the trades of this supplier as an array
   */
  public Trade[] getTrades() { 
    // forces the array elements to be of type Trade
    return (Trade[]) trades.toArray(new Trade[0]);
  }

  /** 
   * Add a new trade for this supplier.
   *
   * @param trade	the trade
   */
  public void addTrade(Trade trade) {
    trades.add(trade);
  }

  /** 
   * Returns the information of this supplier as a string.
   *
   * @return the information of this supplier
   */
  public String toString() { 
    String s = "Supplier: " + company_name + "\n";
    s += "Address: " + address + "\n";
    s += "Trades:\n";

    Trade[] t = getTrades();
    for (int i=0; i<t.length; i++) {
      s += "  " + t[i].toString();
    }

    return s;
  }

}


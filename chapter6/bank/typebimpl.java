//*************************************************************************
/**
 * TypeBImpl.java - A class implementing AccountMgrIF.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class is an implementation of the account manager interface.
 * The open operation works somewhat differently from that in TypeAImpl.java.
 * It only has some bare bone implementation.  You are encouraged
 * to enrich it with a collection of account objects, etc.
 */
public class TypeBImpl implements AccountMgrIF {

  /** Name of this implementation */
  private String name;

  /** Account Info */
  private String accInfo = "";

  /** The number of accounts */
  private static int count = 0;

  // should have some collection to hold the accounts

  /**
   * Constructs this object.
   *
   * @param name	name of this implementation
   */
  TypeBImpl(String name) {
    this.name = name;
  }

  /** 
   * Returns the name of this object.
   *
   * @return the name of this object
   */
  public String getName() { 
    return name; 
  }

  /**
   * Opens an account.
   *
   * @param accountName		name of account
   * @param initialAmount	initial amount of deposit
   * @return the account number assigned
   */
  public int open(String accountName, float initialAmount) {
    count++;
    int num = Integer.parseInt(accountName, 36);
    accInfo += "    " + accountName + " (" + num + ")\n";

    // add account info to collection

    // get an account number
    return num;
  }

  /**
   * Removes an account.
   *
   * @param accountInfo		account number
   */
  public void remove(String accountInfo) {
    count--;
    int i = Integer.valueOf(accountInfo).intValue();
    System.out.println("...removing account number: " + i);

    // remove the account from collection
  }

  /**
   * Shows all account information.
   */
  public String showAccounts() {
    return "There are " + (count) + " acounts.\n" + accInfo;
  }
}

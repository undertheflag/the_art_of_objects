//*************************************************************************
/**
 * TypeAImpl.java - A class implementing AccountMgrIF.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class is an implementation of the account manager interface.
 * It only has some bare bone implementation.  You are encouraged
 * to enrich it with a collection of account objects, etc.
 */
public class TypeAImpl implements AccountMgrIF {

  /** Name of this implementation */
  private String name;

  /** The next available number for the accounts */
  private static int count = 1;

  // should have some collection to hold the accounts

  /**
   * Constructs this object.
   *
   * @param name	name of this implementation
   */
  TypeAImpl(String name) {
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
    // add account info to collection

    // return account number
    return count++;
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
    return "There are " + (count-1) + " acounts.";
  }
}

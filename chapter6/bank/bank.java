//*************************************************************************
/**
 * Bank.java - A class describing a Bank
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * A class describing a Bank.  It contains a set of servant interfaces of
 * type AccountMgrIF.
 * <P>
 * The implementation classes TypeAImpl and TypeBImpl only have some 
 * bare bone implementation.  You are encouraged to enrich 
 * it with collections of account objects, etc.
 */
public class Bank {

  // Private attributes in the Bank class
  private String name;

  /** A vector is used to contain the accMgrIFs. */
  private Vector accMgrIFs = new Vector();


  /** 
   * Constructs a Bank object.
   *
   * @param name	name of the Bank
   */
  public Bank(String name) {
    this.name = name; 
  }

  /** 
   * Returns the name of this Bank.
   *
   * @return the name of this Bank
   */
  public String getName() { 
    return name; 
  }


  /******** accMgrIFs *********/

  /** 
   * Returns the account managers of this Bank.
   *
   * @return the account managers of this Bank as an array
   */
  public AccountMgrIF[] getAccountMgrIFs() {
    Object[] o = accMgrIFs.toArray();
    AccountMgrIF[] r = new AccountMgrIF[o.length];
    for (int i=0; i<o.length; i++) r[i] = (AccountMgrIF)o[i];
    return r;
  }

  /**
   * Returns the number of AccountMgrIF objects in the extent.
   *
   * @return 	the number of AccountMgrIF objects
   */
  public int getAccountMgrIFCount() {
    return accMgrIFs.size();
  }

  /**
   * Returns the AccountMgrIF object for the input name.
   *
   * @param name 	Name of AccountMgrIF
   * @return AccountMgrIF object.  Null if none matched.
   */
  public AccountMgrIF getAccountMgrIF(String name) {
    Enumeration e = accMgrIFs.elements();
    while (e.hasMoreElements()) {
      AccountMgrIF c = (AccountMgrIF) e.nextElement();
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  /**
   * Returns whether the input AccountMgrIF object exists.
   *
   * @param AccountMgrIF 	an AccountMgrIF object
   * @return true if the input AccountMgrIF object is found.  False otherwise.
   */
  public boolean contains(AccountMgrIF accountMgrIF) {
    return accMgrIFs.contains(accountMgrIF);
  }

  /**
   * Adds a new AccountMgrIF.
   *
   * @param AccountMgrIF 	an AccountMgrIF object
   */
  public void addAccountMgrIF(AccountMgrIF accountMgrIF) {
    accMgrIFs.addElement(accountMgrIF);
  }

  /**
   * Removes the named AccountMgrIF.
   *
   * @param name	Name of AccountMgrIF.
   * @exception Exception if the AccountMgrIF is not found
   */
  public void removeAccountMgrIF(String name) throws Exception {
    /* get the AccountMgrIF Object */
    AccountMgrIF c = getAccountMgrIF(name);
    if (c == null) throw new Exception("AccountMgrIF not found: " + name);

    /* Remove the AccountMgrIF Object from the vector */
    accMgrIFs.removeElement(c);
  }

  /**
   * Removes the input AccountMgrIF.
   *
   * @param AccountMgrIF  The AccountMgrIF to be removed.
   * @exception Exception if the AccountMgrIF is not found
   */
  public void removeAccountMgrIF(AccountMgrIF accountMgrIF) throws Exception {
    if (!accMgrIFs.contains(accountMgrIF))
      throw new Exception("AccountMgrIF not found: " + accountMgrIF.getName());
    accMgrIFs.removeElement(accountMgrIF);
  }

  /**
   * Removes all AccountMgrIF objects.
   */
  public void removeAllaccMgrIFs( ) {
    accMgrIFs.removeAllElements();
  }

  /** 
   * Returns the information of this Bank as a string.
   *
   * @return the information of this Bank
   */
  public String toString() { 
    String s = "Bank: " + name + "\n";
    s += "Account Managers:\n";

    AccountMgrIF[] h = getAccountMgrIFs();
    for (int i=0; i<h.length; i++) {
      s += "  " + h[i].getName();
      s += ": " + h[i].showAccounts();
      s += "\n\n";
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
    String s;

    Bank b = new Bank("My Bank");

    AccountMgrIF amIF1 = new TypeAImpl("Bank Group A");
    amIF1.open("John", 123.50F);
    amIF1.open("Mary", 999.50F);

    AccountMgrIF amIF2 = new TypeBImpl("Bank Group B");
    amIF2.open("Tony", 73.22F);
    amIF2.open("Jane", 8.35F);
    amIF2.open("Kay", 222.50F);

    b.addAccountMgrIF(amIF1);
    b.addAccountMgrIF(amIF2);

    s = b.toString();
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


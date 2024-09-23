//*************************************************************************
/**
 * AccountMgrIF.java - An interface defining an account manager
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

public interface AccountMgrIF {

  /** 
   * Returns the name of this interface.
   *
   * @return the name of this interface object
   */
  public String getName();

  /**
   * Opens an account.
   *
   * @param accountName		name of account
   * @param initialAmount	initial amount of deposit
   * @return the account number assigned
   */
  public int open(String accountName, float initialAmount);

  /**
   * Removes an account.
   *
   * @param accountInfo		account name or number
   */
  public void remove(String accountInfo);

  /**
   * Shows all account information.
   */
  public String showAccounts();
}

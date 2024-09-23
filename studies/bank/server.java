//*************************************************************************
/**
 * Server.java - the Bank server implementation
 *
 *  The AccountMgr and Account follow the object manager pattern.
 *  AccountIF objects (Corba objects) are used to simulate persistent
 *  account objects.  Thus the life cycle of Corba objects is that of 
 *  an actual accounts' life cycle.  
 *
 *  Multiple clients may connect to the same account object.  Thus 
 *  transactions on Account are declared synchronized.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import java.util.*;
import bank.*;  // from bank.idl

/** Implementation for AccountIF.
 */
class AccountImpl implements AccountIFOperations {

  // These are private variables.  In reality their values are obtained
  // from the bank's database.
  private AccInfo mAccInfo;
  private float mBalance;
  private String mHistory;

  // constructor
  AccountImpl(AccInfo accInfo, float balance) {	
    mAccInfo = accInfo;
    mBalance = balance;
    mHistory = "Created on " + (new Date());
  }

  // get operations
  public float getBalance() {
    return mBalance;
  }

  public AccInfo getAccInfo() {
    return mAccInfo;
  }

  public String getHistory() {
    return mHistory;
  }

  // transactions
  synchronized public float deposit(float amount) throws BankError {
    if (amount <= 0.0) throw
      new BankError("Transfer rejected.  Amount <= 0.");

    mBalance += amount;
    mHistory += "\nDeposit " + amount + " on " + (new Date());
    return mBalance;
  }

  synchronized public float withdraw(float amount) throws BankError {
    if (amount > mBalance) throw
      new BankError("Withdrawal rejected.  Requested amount > current balance.");
    if (amount <= 0.0) throw
      new BankError("Transaction rejected.  Amount <= 0.");

    mBalance -= amount;
    mHistory += "\nWithdraw " + amount + " on " + (new Date());
    return mBalance;
  }

  synchronized public float transferTo(String name, float amount)
    throws BankError {

    if (amount > mBalance) throw
      new BankError("Transfer rejected.  Requested amount > current balance.");
    if (amount <= 0.0) throw
      new BankError("Transfer rejected.  Amount <= 0.");

    AccountIF acc = Server.accountMgr.findAccount(name);
    if (acc == null) throw
      new BankError("Cannot find account for: " + name);
    if (acc == this) throw
      new BankError("Cannot transfer to same account.");

    mBalance -= amount;
    mHistory += "\nTransfer " + amount + " on " + (new Date()) 
      + " to account: " + acc.getAccInfo().name;

    acc.deposit(amount);
    return mBalance;
  }

  synchronized public float payBill(String Receiver, float amount)
    throws BankError {

    if (amount > mBalance) throw
      new BankError("Bill payment rejected.  Requested amount > current balance.");
    if (amount <= 0.0) throw
      new BankError("Transaction rejected.  Amount <= 0.");

    mBalance -= amount;
    mHistory += "\nPay " + amount + " on " + (new Date()) 
      + " to " + Receiver;
    
    // add amount to Receiver via some network connection
    return mBalance;
  }

}

/** Implementation for AccountMgrIF.  Manages the AccountIF objects.
 *
 */
class AccountMgrImpl implements AccountMgrIFOperations {

  private Dictionary mAccounts = new Hashtable();
  private Random mRandom = new Random();
  private org.omg.CORBA.BOA mBoa; 

  public AccountMgrImpl(org.omg.CORBA.BOA boa) {
    mBoa = boa;
  }

  public AccountIF findAccount(String name) {
    // lookup the account in the account dictionary
    return (AccountIF) mAccounts.get(name);
  }

  /** Returns the server object (TIE object) of a previously
   *  opened account.
   */
  public AccountIF login(AccInfo accInfo) throws BankError {

    // lookup the account in the account dictionary
    AccountIF account = (AccountIF) mAccounts.get(accInfo.name);

    if(account == null) {
      throw new BankError("No such account for: " + accInfo.name);
    }

    if(!account.getAccInfo().passwd.equals(accInfo.passwd)) {
      throw new BankError("Invalid password for: " + accInfo.name);
    }
    return account;

  }

  public void logout(AccountIF account) throws BankError {
    // This is how to get the Impl object
    // AccountImpl accountImpl = (AccountImpl) ((_tie_AccountIF)account)._delegate();
    System.out.println("...logging out " + account.getAccInfo().name);
  }

  public AccountIF open(AccInfo accInfo, float initialAmount)
    throws BankError {

    // lookup the account in the account dictionary
    AccountIF account = (AccountIF) mAccounts.get(accInfo.name);

    if(account == null) {

      if (accInfo.passwd.length() == 0) {
        throw new BankError("Empty password no allowed");
      }

      // random account number
      accInfo.number = Math.abs(mRandom.nextInt()) % 1000;

      float balance = initialAmount;
      // if no initial amount, create a new account with 0-100 dollars!
      if (balance == 0)
        balance = Math.abs(mRandom.nextInt()) % 10000 / 100f;

      AccountImpl newAccount = new AccountImpl(accInfo, balance);

      // create the tie object for the account 
      account = new _tie_AccountIF(newAccount);

// This is optional.  Needed only if the object allows direct binding.
//      // export the object reference
//      mBoa.obj_is_ready(account);
//      System.out.println("New account for " + accInfo.name + ": " + account);

      // save the account in the account dictionary
      mAccounts.put(accInfo.name, account);
      return account;

    } else {
      throw new BankError("Account already exists for: " + accInfo.name);
    }
  }

  public void remove(AccInfo accInfo) throws BankError {
    AccountIF account = (AccountIF) mAccounts.remove(accInfo.name);
    if(account == null) {
      throw new BankError("No such account for: " + accInfo.name);
    }
    mBoa.deactivate_obj(account);  // deactivate Corba object
  }

  public String showAccounts() {
    String s = "";

    for (Enumeration e = mAccounts.keys(); e.hasMoreElements(); ) {
      s += e.nextElement() + ", ";
    }
    return s;
  }

}

/** Implementation for Server main.
 */
public class Server {
  public static AccountMgrImpl accountMgr;

  public static void main(String[] args) {
    System.out.println("Usage: java Server [server_name]");

    String accountMgrName = "ABC_Bank_Account_Manager";
    if (args.length >= 1) {
      accountMgrName = args[0];
    }

    // Initiliaze the ORB & BOA
    String [] ss = { " " };
    org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(ss,null);
    org.omg.CORBA.BOA boa = ((com.visigenic.vbroker.orb.ORB)orb).BOA_init();

    // create the account Mgr 
    accountMgr = new AccountMgrImpl(boa);
    AccountMgrIF mgr = new _tie_AccountMgrIF(accountMgr, accountMgrName);

    // export the object reference
    boa.obj_is_ready(mgr);
    System.out.println(mgr + " is ready.");

    // wait for requests
    boa.impl_is_ready();
  }
}

//*************************************************************************
/**
 * Server.java - the atmcs (ATM Central Server) implementation
 *
 *   This implementation contains only the interface layer.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import atmcs.*;  // from atmcs.idl
import bank.*;   // from bank.idl

import java.util.*;

/** Implementation for SessionIF.  Note that this class is also a client
 *  to AccountIF, which is used as a servant class here.
 */
class SessionImpl implements SessionIFOperations {

  private String mID;	// session id
  private CardInfo mCardInfo;
  private String mHistory;

  private AccountIF mAccount;     // current account
  private float mBalance = 0.0F;  // current balance

  // constructor
  SessionImpl(String id, CardInfo cardInfo, AccountIF account) {	
    mID = id;
    mCardInfo = cardInfo;
    mAccount = account;
    mHistory = "Session " + id + " created on " + (new Date());
  }

  // get operations on "this"
  public String getID() {
    return mID;
  }

  public CardInfo getCardInfo() {
    return mCardInfo;
  }

  public String getHistory() {
    return mHistory;
  }

  // get operations on current account
  public float getBalance() {
    return mAccount.getBalance();
  }

  public AccountInfo getAccountInfo() {
    AccInfo ai = mAccount.getAccInfo();
    return new AccountInfo(ai.name, ai.number);
  }

  public String getAccountHistory() {
    return mAccount.getHistory();
  }

  // transactions on an account
  public float deposit(float amount) throws AtmcsError {
    try {
      mBalance = mAccount.deposit(amount);
    } catch (BankError e) {
      throw new AtmcsError(e.message);
    }
    logTrx("Deposit " + amount);
    return mBalance;
  }

  public float withdraw(float amount) throws AtmcsError {
    try {
      mBalance = mAccount.withdraw(amount);
    } catch (BankError e) {
      throw new AtmcsError(e.message);
    }
    logTrx("Withdraw " + amount);
    return mBalance;
  }

  public float transferTo(String name, float amount) throws AtmcsError {
    try {
      mBalance = mAccount.transferTo(name, amount);
    } catch (BankError e) {
      throw new AtmcsError(e.message);
    }
    logTrx("Transfer " + amount + " to " + name);
    return mBalance;
  }

  public float payBill(String receiver, float amount) throws AtmcsError {
    try {
      mBalance = mAccount.payBill(receiver, amount);
    } catch (BankError e) {
      throw new AtmcsError(e.message);
    }
    logTrx("Pay " + amount + " to " + receiver);
    return mBalance;
  }

  // save transaction info to log or db
  public void logTrx(String content) {
    mHistory += "\n" + content + " on " + (new Date());
  }

}

/** Implementation for SessionMgrIF.  Manages the SessionIF objects.
 *
 *  SessionIF objects (Corba objects) are used to simulate persistent
 *  Session objects.  Thus the life cycle of Corba objects is the same
 *  as actual Sessions' life cycle.
 */
class SessionMgrImpl implements SessionMgrIFOperations {

  private org.omg.CORBA.BOA mBoa; 
  private Dictionary mSessions = new Hashtable();
  private Random mRandom = new Random();

  // bank account manager names (hard coded for this example)
  private String [] mAccountMgrs = { "ABC_Bank_Account_Manager" };

  public SessionMgrImpl(org.omg.CORBA.BOA boa) {
    mBoa = boa;
  }

  // Validate card information (password) and create a new session.
  public SessionIF login(CardInfo cardInfo, String agentId)
  throws AtmcsError {

    AccountIF account; // primary account

    String accountMgrName = mAccountMgrs[0];
    int accNumber = 1234;  // primary account

    // Connect to account manager at the bank server
    AccountMgrIF manager = null;
    try {
      manager = AccountMgrIFHelper.bind(Server.orb, accountMgrName);
    } catch (org.omg.CORBA.NO_IMPLEMENT e) {
      throw new AtmcsError("SessionMgrImpl.login: cannot connect to \"" + accountMgrName + "\"");
    }

    // account info
    String accName = cardInfo.name;
    AccInfo accInfo = new AccInfo(accName, accNumber, cardInfo.passwd);

    try {  // login to bank account
      account = manager.login(accInfo);
    } catch (BankError e) {
      throw new AtmcsError(e.message);
    }

    System.out.println("...connected to account: " + account.getAccInfo().name);

    // a string of random session ID 
    String sessionID = String.valueOf(Math.abs(mRandom.nextInt()));
    SessionIF session = (SessionIF) mSessions.get(sessionID);

    // make sure it is unique in the dictionary
    while (session != null) {
      sessionID = String.valueOf(Math.abs(mRandom.nextInt()));
      session = (SessionIF) mSessions.get(sessionID);
    }

    SessionImpl newSession = 
      new SessionImpl(sessionID, cardInfo, account);

    // create the tie object for the Session 
    session = new _tie_SessionIF(newSession);

    // save the Session in the dictionary with card number as key
    mSessions.put(sessionID, session);

    // may also start a thread to timeout the session after it
    // is inactive for a certain period.

    return session;
  }

  public void logout(SessionIF session) throws AtmcsError {
    // get the Impl object
    SessionImpl sessionImpl = (SessionImpl) ((_tie_SessionIF) session)._delegate();
    String id = sessionImpl.getID();
    CardInfo cardInfo = sessionImpl.getCardInfo();

    System.out.println("Session history for: " + cardInfo.name);
    System.out.println( sessionImpl.getHistory() );
    System.out.println("Logged out ");
    remove(id);
  }

  /** Returns a list of bank account manager names.
   */
  public String getAccountMgrNames() {
    String s = "";
    for (int i=0; i<mAccountMgrs.length; i++) {
      if (i>1) s += ", ";
      s += mAccountMgrs[i];
    }
    return s;
  }

  // private functions
  private String showSessions() {
    String s = "";

    for (Enumeration e = mSessions.keys(); e.hasMoreElements(); ) {
      s += e.nextElement() + ", ";
    }
    return s;
  }

  private void remove(String sessionID) throws AtmcsError {
    SessionIF session = (SessionIF) mSessions.remove(sessionID);
    if(session == null) {
      throw new AtmcsError("No such session for: " + sessionID);
    }
    mBoa.deactivate_obj(session);  // deactivate Corba object
  }

}

/** Implementation for Server main.
 */
public class Server {
  public static SessionMgrImpl sessionMgr;
  public static org.omg.CORBA.ORB orb;

  public static void main(String[] args) {

    System.out.println("Usage: java Server");

    // Initiliaze the ORB & BOA
    String [] ss = { " " };
    orb = org.omg.CORBA.ORB.init(ss, null);
    org.omg.CORBA.BOA boa = ((com.visigenic.vbroker.orb.ORB)orb).BOA_init();

    // create the Session Mgr 
    String sessionMgrName = "ATM Session Manager";
    sessionMgr = new SessionMgrImpl(boa);
    SessionMgrIF mgr = new _tie_SessionMgrIF(sessionMgr, sessionMgrName);

    // export the object reference
    boa.obj_is_ready(mgr);
    System.out.println(mgr + " is ready.");

    // wait for requests
    boa.impl_is_ready();
  }

}

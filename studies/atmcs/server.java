//*************************************************************************
/**
 * Server.java - the full atmcs (ATM Central Server) implementation
 *
 * Note: the use of new Date() cause unknown problems.  They are 
 * temporarily commented out.
 *
 *   This implementation contains both the interface and links to the
 *   persistent layer.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

// explicit link to persistent layer Session class
import atmcs.Session;

import atmcs.*;  // from atmcs.idl
import bank.*;   // from bank.idl

import java.util.*;
import java.io.*;

import COM.odi.*; // for persistent objects

/**
 *  Implementation for SessionIF.  Note that this class is also a client
 *  to AccountIF, which is used as a servant class here.
 */
class SessionImpl implements SessionIFOperations {

  private String mID;	// session id
  private CardInfo mCardInfo;
  private String mHistory = "";

  private AccountIF mAccount;     // current account
  private float mBalance = 0.0F;  // current balance

  // reference to persistent object
  private Session mpSession = null;

  // constructor
  SessionImpl(String id, CardInfo cardInfo, AccountIF account) {	
    mID = id;
    mCardInfo = cardInfo;
    mAccount = account;
    mHistory = "Session " + id + " created on " + (new Date());
  }

  // constructor
  SessionImpl(String id, CardInfo cardInfo, AccountIF account, Session session) {	
    mID = id;
    mCardInfo = cardInfo;
    mAccount = account;
    mpSession = session;
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

  public Session getPersistentObject() {
    return mpSession;
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

  // transactions on current account
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

  /** 
   * Log transaction info to log file or DB.
   *
   * @param content	String content of the transaction.
   */
  public void logTrx(String content) {
    mHistory += content + "\n";

    if (mpSession != null) {  // log trx to db

     // Since it involves transaction, we put them in a synchronized 
     // block to make threads cooperating with each other.
     synchronized (DBManager.TOKEN) {
     try {
      DBManager.beginUpdateTrx();
      mpSession.addTransaction((new DateTime()), content);
      DBManager.commitTrx();

     } catch (Exception e) {
      DBManager.handleTrxError(e);
      e.printStackTrace();
     }
     }

    }

  }

}

/** 
 * Implementation for SessionMgrIF.  Manages the SessionIF objects.
 */
class SessionMgrImpl implements SessionMgrIFOperations {

  private org.omg.CORBA.BOA mBoa; 
  private Dictionary mSessions = new Hashtable();
  private Random mRandom = new Random();

  // bank account manager names (hard coded for this example)
  private String [] mAccountMgrs = { "ABC_Bank_Account_Manager" };

  // reference to persistent objects
  private ATMManager mpATMMgr = null;

  // constructor without link to persistent objects
  public SessionMgrImpl(org.omg.CORBA.BOA boa) {
    mBoa = boa;
  }

  // constructor with link to persistent objects
  public SessionMgrImpl(org.omg.CORBA.BOA boa, ATMManager atmMgr) {
    mBoa = boa;
    mpATMMgr = atmMgr;
  }

  /**
   * Validate card information (password) and create a new session.
   *
   * @param cardInfo	Card information
   * @param agentId	The id of the agent on the client side
   */
  public SessionIF login(CardInfo cardInfo, String agentId)
  throws AtmcsError {

    AccountIF account; // primary account

    String accountMgrName = mAccountMgrs[0];
    int accNumber = 1234;  // primary account

    Session psession = null; // persistent session object

    if (mpATMMgr != null) {  // retrieve values from db
     // Since it involves transaction, we put them in a synchronized 
     // block to make threads cooperating with each other.
     synchronized (DBManager.TOKEN) {
     try {
      DBManager.beginUpdateTrx();

      Agent agent = mpATMMgr.getAgent(agentId);

      if (agent == null) throw 
        new AtmcsError("SessionMgrImpl.login: agent not found");

      BankInfo b = mpATMMgr.getBankInfo(cardInfo.number);
      if (b == null) throw 
        new AtmcsError("SessionMgrImpl.login: bank info not found");

      accountMgrName = b.getServerName();
      Card card = b.getCard(cardInfo.number);
      if (card == null) throw 
        new AtmcsError("SessionMgrImpl.login: card not found");

      accNumber = card.getAccountNumber();
      psession = mpATMMgr.addSession(new DateTime(), card, agent);

      DBManager.commitTrx();

     } catch (Exception e) {
      DBManager.handleTrxError(e);
      e.printStackTrace();
      throw new AtmcsError("SessionMgrImpl.login: " + e.toString()); 
     }
     }

    }

    trace("...connecting to " + accountMgrName);

    // Connect to account manager at the bank server
    AccountMgrIF manager = null;
    try {
      manager = AccountMgrIFHelper.bind(Server.orb, accountMgrName);
    } catch (org.omg.CORBA.NO_IMPLEMENT e) {
      throw new AtmcsError("SessionMgrImpl.login: cannot connect to \"" + accountMgrName + "\"");
    } catch (org.omg.CORBA.UNKNOWN e) {
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
      new SessionImpl(sessionID, cardInfo, account, psession);

    // create the tie object for the Session 
    session = new _tie_SessionIF(newSession);

    // save the Session in the dictionary with card number as key
    mSessions.put(sessionID, session);

    // may also start a thread to timeout the session after it
    // is inactive for a certain period.

    return session;

  }

  /**
   * Log out a session.
   *
   * @param session	The session to be logged out
   */
  public void logout(SessionIF session) throws AtmcsError {

    // get the Impl object
    SessionImpl sessionImpl = 
      (SessionImpl) ((_tie_SessionIF) session)._delegate();

    String id = sessionImpl.getID();
    CardInfo cardInfo = sessionImpl.getCardInfo();

    System.out.println("Session history for: " + cardInfo.name);
    System.out.println( sessionImpl.getHistory() );
// This somehow causes trouble.  Unknown exception.  
//    String d = (new Date()).toString();
    System.out.println("Logged out ");

    Session s = sessionImpl.getPersistentObject();

    if (s != null) {
     // Since it involves transaction, we put them in a synchronized 
     // block to make threads cooperating with each other.
     synchronized (DBManager.TOKEN) {
     try {
      DBManager.beginUpdateTrx();
      s.endSession(new DateTime());
      DBManager.commitTrx();

     } catch (Exception e) {
      DBManager.handleTrxError(e);
      e.printStackTrace();
     }
     }
    }

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

  // Trace the run.  For debugging.
  public static void trace(String s) {
    System.out.println(s);
  }

}

/** 
 * Implementation for Server main.
 */
public class Server {
  public static SessionMgrImpl sessionMgr;
  public static org.omg.CORBA.ORB orb;

  public static void main(String[] args) {

    System.out.println("Usage: java Server [database_file.odb] [command_file.txt]");
    System.out.println("  If no database file is specified, run on transient objects.");
    System.out.println("  If command file is specified, start a thread to process it.");
    System.out.println("==> Type in 'shutdown' to stop the server.");

    String dbfile = null;
    if (args.length >= 1) {
      dbfile = args[0];
    }

    String cmdfile = null;
    if (args.length >= 2) {
      cmdfile = args[1];
    }

    ATMManager atmMgr = null;
    if (dbfile != null) {  // with persistent objects
      DBManager.initialize(dbfile, "atmcs.ATMManager");
      atmMgr = (ATMManager)DBManager.getDbRoot();

    }

    // Initiliaze the ORB & BOA
    String [] ss = { " " };
    orb = org.omg.CORBA.ORB.init(ss, null);
    org.omg.CORBA.BOA boa = ((com.visigenic.vbroker.orb.ORB)orb).BOA_init();

    // Create the Session Mgr 
    String sessionMgrName = "ATM Session Manager";
    sessionMgr = new SessionMgrImpl(boa, atmMgr);
    SessionMgrIF mgr = new _tie_SessionMgrIF(sessionMgr, sessionMgrName);

    // Export the object reference
    boa.obj_is_ready(mgr);
    System.out.println(mgr + " is ready.");

    // A thread to wait for requests
    (new BoaThread("Main Server", boa)).start();

    // One for command file if present
    if (cmdfile != null) {
      InputStream input = null;
      try { 
        input = new FileInputStream(cmdfile);
        (new TerminalThread("Admin Command File", atmMgr, input)).start();
      } catch (Exception e){
        System.out.println(e.toString());
	e.printStackTrace();
      }
    }

    // Another for the terminal 
    if (atmMgr != null)
      (new TerminalThread("Admin Terminal", atmMgr)).start();

  }

}

/**
 * A thread to run the terminal simulator.
 */
class TerminalThread extends Thread {
  private ATMManager mMgr;
  private InputStream mInput = null;

  /**
   * Constructor with default input from console.
   *
   * @param name	name of this thread
   * @param mgr		handle to the terminal object
   */
  public TerminalThread(String name, ATMManager mgr) {
    super(name);
    mMgr = mgr;
    mInput = System.in;
  }

  /**
   * Constructor with specified input.
   *
   * @param name	name of this thread
   * @param mgr		handle to the terminal object
   * @param input	input source
   */
  public TerminalThread(String name, ATMManager mgr, InputStream input) {
    super(name);
    mMgr = mgr;
    mInput = input;
  }

  public void run() {
    ATMManager.runTerminal(mMgr, mInput);
  }
}

/**
 * A thread to run the BOA.
 */
class BoaThread extends Thread {
  private org.omg.CORBA.BOA mBoa;

  public BoaThread(String name, org.omg.CORBA.BOA boa) {
    super(name);
    mBoa = boa;
  }

  public void run() {
    mBoa.impl_is_ready();
  }
}


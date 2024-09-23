//*************************************************************************
/**
 * ATMManager.java - The database root for the persistent layer.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package atmcs;

// Explicit import to avoid ambiguous Iterator class
import COM.odi.util.Iterator;

import java.util.*;
import java.io.*;
import COM.odi.*;
import COM.odi.util.OSVector;

public class ATMManager {

  private static String smAdminPassword = "atmcs";

  /* Vectors of collection */
  private OSVector mBankInfos = new OSVector();
  private OSVector mSessions = new OSVector();
  private OSVector mDailyLogs = new OSVector();
  private OSVector mAgents = new OSVector();

  /**
   * Constructor.
   */
  public ATMManager() {
  }

  /**
   * Constructor
   *
   * @param adminPassword	Password for administrator
   */
  public ATMManager(String adminPassword) {
    smAdminPassword = adminPassword;
  }

  /**
   * Validate the input password
   *
   * @param password	Password to be validated
   */
  public static boolean validatePassword(String password) {
    if (smAdminPassword.equals(password)) return true;
    else return false;
  }

  /******** BankInfos *********/

  public BankInfo[] getBankInfos() {
    Object[] o = mBankInfos.toArray();
    BankInfo[] r = new BankInfo[o.length];
    for (int i=0; i<o.length; i++) r[i] = (BankInfo)o[i];
    return r;
  }

  public int getBankInfoCount() {
    return mBankInfos.size();
  }

  /**
   * Find BankInfo object for the input name.
   *
   * @param name 	Name of bank
   * @return BankInfo object.  Null if none matched.
   */
  public BankInfo getBankInfo(String name) {
    Enumeration e = mBankInfos.elements();
    while (e.hasMoreElements()) {
      BankInfo c = (BankInfo) e.nextElement();
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  /**
   * Find BankInfo object for the input number or prefix (first 6 digits
   * in number).
   *
   * @param number 	Card number or prefix for a bank
   * @return BankInfo object.  Null if none matched.
   */
  public BankInfo getBankInfo(int number) {
    String s = String.valueOf(number).substring(0,4);
    int prefix = Integer.valueOf(s).intValue();
    Enumeration e = mBankInfos.elements();
    while (e.hasMoreElements()) {
      BankInfo c = (BankInfo) e.nextElement();
      if (c.getPrefix() == prefix) return c;
    }
    return null;
  }

  public boolean contains(BankInfo bankInfo) {
    return mBankInfos.contains(bankInfo);
  }

  /**
   * Add a new BankInfo.
   *
   * @param name	Name of bank
   * @param prefix	Prefix of bank
   * @param serverName	Server name of bank
   */
  public void addBankInfo(String name, int prefix, String serverName)
  throws Exception {
    BankInfo bankInfo = new BankInfo(name, prefix, serverName);
    mBankInfos.addElement(bankInfo);
  }

  /**
   * Remove BankInfo form the vector.
   *
   * @param name	Name of Bank.
   */
  public void removeBankInfo(String name) throws AtmcsException {
    /* get the BankInfo Object */
    BankInfo c = getBankInfo(name);
    if (c == null) throw new AtmcsException("BankInfo not found: " + name);

    /* Remove the BankInfo Object from the vector */
    mBankInfos.removeElement(c);
  }

  /**
   * Remove BankInfo form the vector.
   *
   * @param prefix	Prefix of Bank.
   */
  public void removeBankInfo(int prefix) throws AtmcsException {
    /* get the BankInfo Object */
    BankInfo c = getBankInfo(prefix);
    if (c == null) throw new AtmcsException("BankInfo not found: " + prefix);

    /* Remove the BankInfo Object from the vector */
    mBankInfos.removeElement(c);
  }

  /**
   * Remove the input BankInfo form the vector.
   *
   * @param BankInfo  The BankInfo to be removed.
   */
  public void removeBankInfo(BankInfo BankInfo) throws AtmcsException {
    if (!mBankInfos.contains(BankInfo))
      throw new AtmcsException("BankInfo not found: " + BankInfo.getName());
    mBankInfos.removeElement(BankInfo);
  }

  public void removeAllBankInfos( ) {
    mBankInfos.removeAllElements();
  }

  /******** Sessions *********/

  public Session[] getSessions() {
    Object[] o = mSessions.toArray();
    Session[] r = new Session[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Session)o[i];
    return r;
  }

  public int getSessionCount() {
    return mSessions.size();
  }

  public Session[] getSessionsBetween(Date timeA, Date timeB) {
    Vector v = new Vector();
    Iterator it = mSessions.iterator();
    while ( it.hasNext() ) {
      Session s = (Session) it.next();
      if ( s.getStartTime().after(timeA) &&
           s.getStartTime().before(timeB)   ) v.add(s);
    }
    // this forces the return array elements to be of type Session
    return (Session[]) v.toArray(new Session[1]);
  }

  public boolean contains(Session session) {
    return mSessions.contains(session);
  }

  /**
   * Create and add a session.
   */
  public Session addSession(DateTime startTime, Card card, Agent agent) {
    Session session = new Session(startTime, card, agent);
    mSessions.add(session);
    card.addSession(session);
    agent.addSession(session);

    DailyLog dl = getDailyLog(startTime);
    if (dl == null) dl = addDailyLog(startTime);
    dl.addSession(session);

    return session;
  }

  /**
   * Remove the input Session form the vector.
   *
   * @param Session  The Session to be removed.
   */
  public void removeSession(Session Session) throws AtmcsException {
    if (!mSessions.contains(Session))
      throw new AtmcsException("Session not found: " + Session.getStartTime());
    mSessions.removeElement(Session);
  }

  public void removeAllSessions( ) {
    mSessions.removeAllElements();
  }


  /******** DailyLogs *********/

  public DailyLog[] getDailyLogs() {
    Object[] o = mDailyLogs.toArray();
    DailyLog[] r = new DailyLog[o.length];
    for (int i=0; i<o.length; i++) r[i] = (DailyLog)o[i];
    return r;
  }

  public int getDailyLogCount() {
    return mDailyLogs.size();
  }

  /**
   * Find DailyLog object for the input DailyLog number.
   *
   * @param number 	DailyLog number
   * @return DailyLog object.  Null if none matched.
   */
  public DailyLog getDailyLog(DateTime date) {
    Enumeration e = mDailyLogs.elements();
    while (e.hasMoreElements()) {
      DailyLog c = (DailyLog) e.nextElement();
      if (c.getDate().isSameDate(date)) return c;
    }
    return null;
  }

  public boolean contains(DailyLog dailyLog) {
    return mDailyLogs.contains(dailyLog);
  }

  /**
   * Add a new DailyLog.
   *
   * @param DailyLog 	DailyLog object
   */

  public DailyLog addDailyLog(DateTime date) {
    DailyLog dailyLog = new DailyLog(date);
    mDailyLogs.addElement(dailyLog);
    return dailyLog;
  }

  /**
   * Remove DailyLog form the vector.
   *
   * @param id 	DailyLog id.
   */
  public void removeDailyLog(DateTime date) throws AtmcsException {
    /* get the DailyLog Object */
    DailyLog c = getDailyLog(date);
    if (c == null) throw new AtmcsException("DailyLog not found: " + date);

    /* Remove the DailyLog Object from the vector */
    mDailyLogs.removeElement(c);
  }

  /**
   * Remove the input DailyLog form the vector.
   *
   * @param DailyLog  The DailyLog to be removed.
   */
  public void removeDailyLog(DailyLog dailyLog) throws AtmcsException {
    if (!mDailyLogs.contains(dailyLog))
      throw new AtmcsException("DailyLog not found: " + dailyLog.getDate());
    mDailyLogs.removeElement(dailyLog);
  }

  public void removeAllDailyLogs( ) {
    mDailyLogs.removeAllElements();
  }

  /******** Agents *********/

  public Agent[] getAgents() {
    Object[] o = mAgents.toArray();
    Agent[] r = new Agent[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Agent)o[i];
    return r;
  }

  public int getAgentCount() {
    return mAgents.size();
  }

  /**
   * Find Agent object for the input Agent number.
   *
   * @param number 	Agent number
   * @return Agent object.  Null if none matched.
   */
  public Agent getAgent(String id) {
    Enumeration e = mAgents.elements();
    while (e.hasMoreElements()) {
      Agent c = (Agent) e.nextElement();
      if (c.getId().equals(id)) return c;
    }
    return null;
  }

  public boolean contains(Agent agent) {
    return mAgents.contains(agent);
  }

  /**
   * Add a new Agent.
   *
   * @param agent 	Agent object
   */

  public void addAgent(Agent agent) {
    mAgents.addElement(agent);
  }

  /**
   * Remove Agent form the vector.
   *
   * @param id 	Agent id.
   */
  public void removeAgent(String id) throws AtmcsException {
    /* get the Agent Object */
    Agent c = getAgent(id);
    if (c == null) throw new AtmcsException("Agent not found: " + id);

    /* Remove the Agent Object from the vector */
    mAgents.removeElement(c);
  }

  /**
   * Remove the input agent form the vector.
   *
   * @param agent  The agent to be removed.
   */
  public void removeAgent(Agent agent) throws AtmcsException {
    if (!mAgents.contains(agent))
      throw new AtmcsException("Agent not found: " + agent.getId());
    mAgents.removeElement(agent);
  }

  public void removeAllAgents( ) {
    mAgents.removeAllElements();
  }


  /* since we dont want this to be stored in hash table we overload
   * hashCode and call super.hashCode() 
   */
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Run a command-line terminal for the object o with the input.
   * It can handle o being a persistent object.
   */
  public static void runTerminal(ATMManager o, InputStream input) {

    /* read command input */
    BufferedReader instream = new BufferedReader(new InputStreamReader(input));

    System.out.println("Please enter admin password:");
    try {
      String password = instream.readLine();
      if (!ATMManager.validatePassword(password)) {
        System.out.println("Invalid admin password.  Exiting...");
        return;
      }
    } catch (Exception e) {
      show("  " + e.toString());
      return;
    }

    /* print help message describing the legal commands */
    printHelp();
    System.out.println();

    while (true) {
      System.out.print("> ");
      String inputLine = "";

      try {  /* read a line of command input */
        inputLine = instream.readLine();
      } catch (Exception e) {  // exceptions
        show("  " + e.toString());
	e.printStackTrace();
      }

      if (inputLine == null) { /* end of input */
	return;
      }

      if (inputLine.startsWith("#")) { // comment line
	continue;
      }

      if ("shutdown".equalsIgnoreCase(inputLine)) {
        DBManager.shutdown();
	System.exit(0);  // end all threads
      }

      // Tokenize the command input with a StringTokenizer.
      // Space and \t are separators
      StringTokenizer tokenizer = new StringTokenizer(inputLine, " 	");
      if (!tokenizer.hasMoreTokens()) continue;
      String command = tokenizer.nextToken();

      if ("help".startsWith(command)) {
	printHelp();
      }

      else if ("quit".startsWith(command)) {
	return;
      }

     // Commands below may involve transaction.  We put them in
     // a synchronized block to make threads cooperating with 
     // each other.
     synchronized (DBManager.TOKEN) {
      try {
        if (DBManager.getDB() != null) DBManager.beginUpdateTrx();

        if ("addbankinfo".startsWith(command)) {
          String name = readString(tokenizer);
          int prefix = readInt(tokenizer);
          String serverName = readString(tokenizer);
	  o.addBankInfo(name, prefix, serverName);
        }

        else if ("showbankinfo".startsWith(command)) {
	  int number = 0;
	  String name = "";
	  try { number = readInt(tokenizer); }
	  catch(TerminalException e) { 
	    name = "all";
	  }
	  
	  if (number > 0) {
	    BankInfo b = o.getBankInfo(number);
	    show(" " + b.getName() + " @ " + b.getServerName() + " (" + 
		 b.getPrefix() + ")" );

	  } else if ("all".startsWith(name)) {
	    BankInfo[] bs = o.getBankInfos();
	    for (int i=0; i<bs.length; i++) {
	      BankInfo b = bs[i];
	      show(" " + b.getName() + " @ " + b.getServerName() + " (" + 
		b.getPrefix() + ")" );
	    }
          } else {
	    BankInfo b = o.getBankInfo(name);
	    show(" " + b.getName() + " @ " + b.getServerName() + " (" + 
		 b.getPrefix() + ")" );
          }
        }

        else if ("addatm".startsWith(command)) {
          String id = readString(tokenizer);
          String name = readString(tokenizer);
          String location = readString(tokenizer);
	  Agent a = new ATM(id, name, location);
	  o.addAgent(a);
        }

        else if ("showagent".startsWith(command)) {
	  String id;
	  try {
            id = readString(tokenizer);
	  } catch(TerminalException e) { 
	    id = "all";
	  }
	  if ("all".startsWith(id)) {
	    Agent[] bs = o.getAgents();
	    for (int i=0; i<bs.length; i++) {
	      ATM b = (ATM) bs[i];
	      show(" " + b.getId() + ": " + b.getName() + " @ " + 
		b.getLocation() + "" );
	    }
          } else {
	    ATM b = (ATM) o.getAgent(id);
	    if (b==null) show("Agent " + id + " not found.");
	    else show(" " + b.getId() + ": " + b.getName() + " @ " + 
	    		b.getLocation() + "" );
	  }
        }

        else if ("addcard".startsWith(command)) {
          int number = readInt(tokenizer);
          String name = readString(tokenizer);
          int accNumber = readInt(tokenizer);
	  BankInfo b = o.getBankInfo(number);
	  if (b==null) show("No bank matching the card.");
	  b.addCard(number, name, accNumber);
        }

        else if ("showcard".startsWith(command)) {
	  int number = 0;
	  String name = "";
	  try { number = readInt(tokenizer); }
	  catch(TerminalException e) { 
	    name = "all";
	  }
	  
	  if (number > 0) {
	    BankInfo b = o.getBankInfo(number);
	    Card c = b.getCard(number);
	    show(" " + c.getName() + " card: " + c.getNumber() 
	      + " acc:" + c.getAccountNumber() + "" );

	  } else if ("all".startsWith(name)) {
	    BankInfo[] bs = o.getBankInfos();
	    for (int j=0; j<bs.length; j++) {
	      BankInfo b = bs[j];
	      show("--- " + b.getName());
	      Card[] cs = b.getCards();
	      for (int i=0; i<cs.length; i++) {
		Card c = cs[i];
		show(" " + c.getName() + " card: " + c.getNumber() 
		  + " acc:" + c.getAccountNumber() + "" );
	      }
	    }
          } 
        }

        else if ("showdailylog".startsWith(command)) {
          int relDate = 0;  // default to today
	  try { relDate = readInt(tokenizer); }
	  catch(TerminalException e) {  }

	  DateTime date = new DateTime();
	  date.shiftDate(relDate);
	  DailyLog dl = o.getDailyLog(date);
	  if (dl == null) {
	    throw new Exception("No log for this day.");
	  }

	  Session[] ss = dl.getSessions();
	  for (int i=0; i<ss.length; i++) {
	    Session s = (Session) ss[i];
	    show("Session for card " + s.getCard().getNumber() 
	      + " and agent " + s.getAgent().getName() + "" );
	    show("  start " + s.getStartTime() );

	    Transaction[] tt = s.getTransactions();
	    for (int j=0; j<tt.length; j++) {
  	      Transaction t = (Transaction) tt[j];
	      show("  " + t.getTime() + " " + t.getContent() );
	    }

	    show("  end " + s.getEndTime() );
	  }
        }

        else {
          show(" Command not recognized.  Try \"help\"");
        }

        if (DBManager.getDB() != null) DBManager.commitTrx();
	
      } catch (Exception e) {

        if (DBManager.getDB() != null) DBManager.handleTrxError(e);
	System.out.println(e.getMessage());
      }
     }

    }

  }

  // show help
  static void printHelp() {
    show("-----------------------------------------------");
    show("Enter: command option1 option2 ...");
    show("Valid commands");
    show("  help                            // print this message");
    show("  addbankinfo name prefix serverName  // add bank info");
    show("  showbankinfo [prefix]   // show one or all bank infos");
    show("  addatm id name location  // add a new atm");
    show("  showagent [id]           // show one or all agents");
    show("  addcard number name accNumber  // add a new card");
    show("  showcard [number]        // show one or all cards");

    show("  showdailylog rel_date    // show dailylog (rel_date = 0 means today)");

    show("");
    show("  shutdown                    // shutdown DB & quit");
    show("-----------------------------------------------");
  }

  static String readString(StringTokenizer tokenizer) throws TerminalException {
    if (tokenizer.hasMoreElements()) 
      return tokenizer.nextToken();
    else 
      throw new TerminalException(" Unexpected end of command input");
  }

  static int readInt(StringTokenizer tokenizer) throws TerminalException {
    if (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken();
      try {
        return Integer.valueOf(token).intValue();
      } catch (NumberFormatException e) {
        throw new TerminalException(" Number Format Exception reading \""+token+ "\"");
      }
    }
    else 
      throw new TerminalException(" Unexpected end of command input");
  }

  static float readFloat(StringTokenizer tokenizer) throws TerminalException {
    if (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken();
      try {
        return Float.valueOf(token).floatValue();
      } catch (NumberFormatException e) {
        throw new TerminalException(" Float Number Format Exception reading \""+token+ "\"");
      }
    }
    else 
      throw new TerminalException(" Unexpected end of command input");
  }

  public static void show(String s) {
    System.out.println(s);
  }

  public static void main(String[] args) {

    show("Usage: java atmcs.ATMManager [database_file] [command_file.txt]");
    show("  If no database file is specified, run on transient objects.");

    String dbfile = null;
    if (args.length >= 1) {
      dbfile = args[0];
    }

    // Get input stream
    InputStream input = System.in;
    if (args.length >= 2) {
      try { input = new FileInputStream(args[1]); }
      catch (FileNotFoundException e){
        System.out.println(e.toString());
      }
    }

    if (dbfile != null) {  // with persistent objects
      DBManager.initialize(dbfile, "atmcs.ATMManager");

      ATMManager mgr = (ATMManager)DBManager.getDbRoot();

      ATMManager.runTerminal(mgr, input);

      DBManager.shutdown();

    } else {  // transient test
      ATMManager mgr = new ATMManager();
      runTerminal(mgr, input);
    }

  }
}

/**
 * An exception for the terminal simulator.
 */
class TerminalException extends Exception {
  public TerminalException (String s) {
    super(s);
  }
}

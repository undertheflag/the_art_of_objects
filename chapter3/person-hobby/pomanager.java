//*************************************************************************
/*
 * POManager.java - a simple utility for persistent objects.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 *
 */
//*************************************************************************

import java.io.*;

/**
 * This class provides static methods to read and write persistent objects
 * serialized in a file.  No transaction management is provided.
 * The objects are read in or created by setupDB.
 * <P>
 * This class has the same API as the DBManager class, which works with
 * PSE Pro.
 * <P>
 * Typical usage:
 * <PRE>
 *    POManager.initialize("DB_filename", "DB_Root_Class");
 *
 *    DB_Root_Class dbRoot = (DB_Root_Class)POManager.getDbRoot();
 *
 *    // ... call methods of dbRoot
 *
 *    POManager.shutdown();
 * </PRE>
 *
 */
public class POManager {

  /** A dummy token object used as a synchronization lock. */
  public static Object TOKEN = new Object();

  private static File smDb;        // Database file handle
  private static Object smDbRoot;  // DB root object


  /**
   * Open a database file or create it if not found.
   * Also setup the database root or create it if not found.
   *
   * @param     dbName  the database file name
   * @param 	dbRootClassName  Class name of the database root.  
   */
  public static void initialize(String dbName, String dbRootClassName) {
     // Open the database or create a new one if not found.
     openDb(dbName);

     // setup the database root.  May create it if not found.
     setupDbRoot(dbRootClassName);
  }

  /**
   * Shut down the database system, including closing the database file.
   *
   */
  public static void shutdown() {
    try {
      FileOutputStream fos = new FileOutputStream(smDb);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(smDbRoot);
      oos.flush();
      oos.close();

    } catch (IOException e) {
      show(e.toString());
    }

    closeDb();
  }

  /**
   * Return a reference to the database file.
   *
   * @return    a reference to the database file
   */
  public static File getDB() {
     return smDb;
  }

  /**
   * Return a reference to the db root object.
   *
   * @return    a reference to the DB root object
   */
  public static Object getDbRoot() {
     return smDbRoot;
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
   * Shows the input string.  For debugging.
   *
   * @s the string to be shown
   */
  public static void debug(String s) { 
    // System.out.println(s);
  }

  /********** Dummy operations for transaction handling *************/

  /**
   * Open a read-only database transaction.
   */
  public static void beginReadTrx() {
    debug("... begin read transaction");
  }

  /**
   * Open an update database transaction.
   */
  public static void beginUpdateTrx() {
    debug("... begin update transaction");
  }

  /**
   * Abort a database transaction.
   */
  public static void abortTrx() {
    debug("... abort transaction");
  }

  /**
   * Commit a database transaction.
   */
  public static void commitTrx() {
    debug("... commit transaction");
  }

  /**
   * Handle an exception thrown in any of the above methods:
   * 1. Abort the current database transaction, if any.
   * 2. Show the exception message
   *
   */
  public static void handleTrxError(Exception e) {
     POManager.abortTrx();
     show(e.toString());
  }

  /********** Private methods *************/

  /**
   * Open a database file or create it if not found.
   * Since we don't have transaction management, we
   * simply create a new file object.
   *
   * @param     dbName  the database file name
   */
  private static void openDb(String dbName) {
     closeDb();  // Force to close it first
     smDb = new File(dbName);
  }

  /**
   * Close the database file, if one is opened.
   * Since we don't have transaction management, we
   * simply nullify the file object.
   *
   */
  private static void closeDb() {
     smDb = null;
  }

  /**
   * Retrieve the database root or create it if not found.
   * The database file is closed afterwards.
   *
   * @param dbRootClassName  Class name of the database root.  
   */
  private static void setupDbRoot(String dbRootClassName) {

    if (smDb.exists()) {  // DB file exists, read it
      try {
	FileInputStream fis = new FileInputStream(smDb);
	ObjectInputStream ois = new ObjectInputStream(fis);
	smDbRoot = ois.readObject();
	ois.close();

      } catch (Exception e) {
	show(e.toString());
      }

    } else {  // create the database root

      try {
	Class c = Class.forName(dbRootClassName);
	smDbRoot = c.newInstance();
      } catch (Exception e2) {
	show(e2.toString());
      }

    }

  }

}

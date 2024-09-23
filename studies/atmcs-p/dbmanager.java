//*************************************************************************
/*
 * DBManager.java - a generic utility to handle database file and transactions
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 *
 */
//*************************************************************************

package atmcs;

import COM.odi.Transaction;
import COM.odi.Session;

import COM.odi.*;
import COM.odi.util.*;

/**
 * This class provides static methods to open and close a database file etc.
 */
public class DBManager {

  /** A dummy token object used as a synchronization lock. */
  public static Object TOKEN = new Object();

  private static Database smDb;      // Database
  private static Object smDbRoot;    // DB root object
  private static Session  smSession; // Active session for OS.

  /**
   * Open a database file or create it if not found.
   * Also setup the database root or create it if not found.
   *
   * @param     dbName  the database file name
   * @param dbRootClassName  Class name of the database root.  
   *		Also use it as the db root name.
   * @return    None
   */
  public static void initialize(String dbName, String dbRootClassName) {
     // Create an OS session and join this thread to the new session.
     smSession = Session.create(null, null);

     connectCurrentThread();

     // Open the database or create a new one if not found.
     openDb(dbName);

     // setup the database root.  May create it if not found.
     setupDbRoot(dbRootClassName);
  }

  /**
   * Open a database file or create it if not found.
   *
   * @param     dbName  the database file name
   * @return    None
   */
  private static void openDb(String dbName) {

     closeDb();  // Force to close it first
     try {
	smDb = Database.open(dbName, ObjectStore.OPEN_UPDATE);
     } catch (DatabaseNotFoundException e) {
	smDb = Database.create(dbName, ObjectStore.ALL_READ | ObjectStore.ALL_WRITE);
     }
  }

  /**
   * Shut down the database system, including closing the database file.
   *
   * @return    None
   */
  public static void shutdown() {
     closeDb();
     if (smSession != null) {
	smSession.terminate();
	smSession = null;
     }
  }

  /**
   * Close the database file, if one is opened.
   *
   * @return    None
   */
  private static void closeDb() {
     if (smDb != null && smDb.isOpen()) smDb.close();
     smDb = null;
  }

  /********** Transaction handling *************/

  /**
   * Open a read-only database transaction.  Do nothing if database
   * not opened.
   */
  public static void beginReadTrx() {
     if (smDb == null) return;
     // Join the current thread to the current database session.
     connectCurrentThread();
     Transaction.begin(ObjectStore.READONLY);
  }

  /**
   * Open an update database transaction. Do nothing if database
   * not opened.
   */
  public static void beginUpdateTrx() {
     if (smDb == null) return;
     connectCurrentThread();
     Transaction.begin(ObjectStore.UPDATE);
  }

  /**
   * Abort a database transaction.  Do nothing if database
   * not opened.
   */
  public static void abortTrx() {
     if (smDb == null) return;
     if (Transaction.inTransaction())
	Transaction.current().abort(ObjectStore.RETAIN_HOLLOW);
  }

  /**
   * Commit a database transaction.  Do nothing if database
   * not opened.
   */
  public static void commitTrx() {
     if (smDb == null) return;
     Transaction.current().commit(ObjectStore.RETAIN_READONLY);
  }

  /**
   * Connect the current thread to the database session 
   *
   * @return    None
   */
  public static void connectCurrentThread() {
     try {
	if (Session.ofThread(Thread.currentThread()) == null)
	   smSession.join();
     }
     catch (IllegalArgumentException e) {
	System.out.println(e.toString());
     }
  }

  /**
   * Return a reference to the database.
   *
   * @return    A reference to the database.
   */
  public static Database getDB() {
     return smDb;
  }

  /**
   * Return a reference to the db root.
   *
   * @return    A reference to the DB root.
   */
  public static Object getDbRoot() {
     return smDbRoot;
  }

  /**
   * Handle an exception thrown in any of the above methods:
   * 1. Abort the current database transaction, if any.
   * 2. Show the exception message
   *
   * @return    none
   */
  public static void handleTrxError(Exception e) {
     DBManager.abortTrx();
     System.out.println(e.toString());
  }


  /**
   * Retrieve the database root or create it if not found.
   *
   * @param dbRootClassName  Class name of the database root.  
   *		Also use it as the db root name.
   * @return    None
   */
  private static void setupDbRoot(String dbRootClassName) {
     // Start a transaction.
     Transaction tr = Transaction.begin(ObjectStore.UPDATE);

     try {
       // Try to retrieve the database root.
       smDbRoot = smDb.getRoot(dbRootClassName);

     } catch (DatabaseRootNotFoundException e) {

       // Create the database root.
       try {
	 Class c = Class.forName(dbRootClassName);
	 smDbRoot = c.newInstance();
       } catch (Exception e2) {
	 System.out.println(e2);
       }
       smDb.createRoot(dbRootClassName, smDbRoot);
     }

     // End the transaction and retain a handle to the root object
     tr.commit(ObjectStore.RETAIN_HOLLOW);
  }

}

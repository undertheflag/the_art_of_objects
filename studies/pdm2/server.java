//*************************************************************************
/**
 * Server.java - implementation for the interfaces in pdm.idl
 *
 *   This implementation contains only the interface layer objects.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import pdm.*;  // from pdm.idl

// import them so their updates will be compiled
import CatalogImpl;
import PartImpl;
import AttributeImpl;

import java.io.*;
import java.util.*;

/** 
 * Implementation for Server main.
 */
public class Server {
  public static CatalogImpl catalogImpl;
  public static org.omg.CORBA.ORB orb;

  public static void main(String[] args) throws Exception {

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

    Catalog catalog = null;

    if (dbfile != null) {  // with persistent objects
      DBManager.initialize(dbfile, "Catalog");
      catalog = (Catalog)DBManager.getDbRoot();

    } else {  // use internal test data
      catalog = new Catalog("electronics");

      // run the test to put some data
      test(catalog);
    }


    // Initiliaze the ORB & BOA
    String [] ss = { " " };
    orb = org.omg.CORBA.ORB.init(ss, null);
    org.omg.CORBA.BOA boa = ((com.visigenic.vbroker.orb.ORB)orb).BOA_init();

    // Create the Session Mgr 
    String catalogImplName = "PDM2 Server";
    catalogImpl = new CatalogImpl(catalog);
    CatalogIF catalogIF = new _tie_CatalogIF(catalogImpl, catalogImplName);

    // Export the object reference
    boa.obj_is_ready(catalogIF);
    System.out.println(catalogIF + " is ready.");

    // A thread to wait for requests
    (new BoaThread("Main Server", boa)).start();

    // One for command file if present
    if (cmdfile != null) {
      InputStream input = null;
      try { 
        input = new FileInputStream(cmdfile);
        (new TerminalThread("Admin Command File", catalog, input)).start();
      } catch (Exception e){
        System.out.println(e.toString());
	e.printStackTrace();
      }
    }

    // Another for the terminal 
    if (catalog != null)
      (new TerminalThread("Admin Terminal", catalog)).start();

  }

  /**
   * A test that returns a string.  Note it is a static method.
   *
   * @return result of the test as a string
   * @exception Exception if any exception is thrown
   */
  public static String test(Catalog catalog) throws Exception {
    String s = "";
      try {
        DBManager.beginReadTrx();
	Catalog.test(catalog);
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
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

}

/**
 * A thread to run the terminal simulator.
 */
class TerminalThread extends Thread {
  private Catalog mCatalog;
  private InputStream mInput = null;

  /**
   * Constructor with default input from console.
   *
   * @param name	name of this thread
   * @param catalog	handle to the terminal object
   */
  public TerminalThread(String name, Catalog catalog) {
    super(name);
    mCatalog = catalog;
    mInput = System.in;
  }

  /**
   * Constructor with specified input.
   *
   * @param name	name of this thread
   * @param catalog	handle to the terminal object
   * @param input	input source
   */
  public TerminalThread(String name, Catalog catalog, InputStream input) {
    super(name);
    mCatalog = catalog;
    mInput = input;
  }

  public void run() {
    Catalog.runTerminal(mCatalog, mInput);
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


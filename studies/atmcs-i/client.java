//*************************************************************************
/**
 * Client.java - client for the atmcs (ATM Central Server) server
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import java.io.*;
import java.util.*;
import atmcs.*;  // from atmcs.idl

class TerminalException extends Exception {
  public TerminalException (String s) {
    super(s);
  }
}

public class Client {

  static SessionMgrIF manager;
  static SessionIF session = null; // current Session

  public static void main(String args[])  {
      String sessionMgrName = "ATM Session Manager";

      show("Usage: java Client [host] [command_file.txt]");

      String host = null;
      if (args.length >= 1) {
        host = args[0];
      }

      // use smart agent at host
      Properties props = new Properties();
      if (host != null) props.put("ORBagentAddr", host);

      // Initiliaze the ORB.
      org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(args, props);

      // default options: no deferred bind, do rebind
      org.omg.CORBA.BindOptions options = new org.omg.CORBA.BindOptions();

      // Locate the account manager.
      manager = SessionMgrIFHelper.bind(orb, sessionMgrName, host, options);
      show("...connected to " + sessionMgrName + " at " + host);

      // Get input stream
      InputStream input = System.in;
      if (args.length >= 2) {
        try { input = new FileInputStream(args[1]); }
        catch (FileNotFoundException e){}
      }

      runTerminal(manager, input);
  }

  // Run a command-line terminal session on the remote object o.
  public static void runTerminal(SessionMgrIF o, InputStream input) {

    /* read command input */
    BufferedReader instream = new BufferedReader(new InputStreamReader(input));
    /* print help message describing the legal commands */
    printHelp();
    System.out.println();

    while (true) {
      System.out.print("> ");

      try {

        /* read a line of command input */
        String inputLine = instream.readLine();

        if (inputLine == null) { /* end of input */
          if (session != null) o.logout(session);
          return;
        }

        if (inputLine.startsWith("#")) { // comment line
          continue;
        }

        // Tokenize the command input with a StringTokenizer.
	// Space and \t are separators
        StringTokenizer tokenizer = new StringTokenizer(inputLine, " 	");
        if (!tokenizer.hasMoreTokens()) continue;
        String command = tokenizer.nextToken();

        if ("help".startsWith(command)) {
          printHelp();
        }

        else if ("login".startsWith(command)) {
          int cardNumber = readInt(tokenizer);
          String name = readString(tokenizer);
          String passwd = readString(tokenizer);
          CardInfo cardInfo = new CardInfo(cardNumber, name, passwd);
          try {
            // create a new Session
            session = o.login(cardInfo, "TestClient");

            // Get the balance of the session.
            float balance = session.getBalance();
            show(" New session opened for: " + name);
            show(" Initial balance is $" + balance);
          } catch (AtmcsError e) {
            show(e.message);
          }

        }

        else if ("balance".startsWith(command)) {
          if (!isSessionOK(session)) {
            session = null; continue;
          }
          float balance = session.getBalance();
          show(" Current balance: $" + balance);

        }

        else if ("history".startsWith(command)) {
          if (!isSessionOK(session)) {
            session = null; continue;
          }
          String s = session.getAccountHistory();
	  AccountInfo info = session.getAccountInfo();
          show("  Account history: " + info.name + " (account No. " + info.number + ")\n" + s);

        }

        else if ("deposit".startsWith(command)) {
          if (!isSessionOK(session)) {
            session = null; continue;
          }
          float amount = readFloat(tokenizer);
          try {
            float balance = session.deposit(amount);
            show(" New balance: $" + balance);
          } catch (AtmcsError e) {
            show(e.message);
          }
        }

        else if ("withdraw".startsWith(command)) {
          if (!isSessionOK(session)) {
            session = null; continue;
          }
          float amount = readFloat(tokenizer);
          try {
            float balance = session.withdraw(amount);
            show(" New balance: $" + balance);
          } catch (AtmcsError e) {
            show(e.message);
          }

        }

        else if ("pay".startsWith(command)) {
          if (!isSessionOK(session)) {
            session = null; continue;
          }
          String receiver = readString(tokenizer);
          float amount = readFloat(tokenizer);
          try {
            float balance = session.payBill(receiver, amount);
            show(" New balance: $" + balance);
          } catch (AtmcsError e) {
            show(e.message);
          }

        }

        else if ("transfer".startsWith(command)) {
          if (!isSessionOK(session)) {
            session = null; continue;
          }
          String name = readString(tokenizer);
          float amount = readFloat(tokenizer);
          try {
            float balance = session.transferTo(name, amount);
            show(" New balance: $" + balance);
          } catch (AtmcsError e) {
            show(e.message);
          }

        }

        else if ("showBanks".startsWith(command)) {
          show(o.getAccountMgrNames());
        }

        else if ("quit".startsWith(command)) {
          if (session != null) o.logout(session);
          return;
        }

        else {
          show(" Command not recognized.  Try \"help\"");
        }

      } catch (TerminalException e) {  // other command errors
        show("  " + e.toString());
        continue;

      } catch (Exception e) {  // exit on any other exceptions
        show("  " + e.toString());
        return;
      }

    }

  }

  static boolean isSessionOK(SessionIF session) {
    if (session == null) {
      show(" Please login first");
      return false;
    }
    if (session._non_existent()) {
      show(" session no longer exist.  Please login again.");
      return false;
    }
    return true;
  }

  static void printHelp() {
    show("-----------------------------------------------");
    show("Enter: command option1 option2 ...");
    show("Valid commands");
    show("  help                            // print this message");
    show("  showBanks                       // show available banks");
    show("  login cardNumber name password  // login to primary account");
    show("Commands after login");
    show("  balance                 // get balance");
    show("  history                 // get history");
    show("  deposit amount          // deposit the amount");
    show("  withdraw amount         // withdraw the amount");
    show("  pay receiver amount     // pay a bill to reiver in the amount");
    show("  transfer accountName amount // transfer to another account");
    show("");
    show("  quit                    // logout and quit");
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
}

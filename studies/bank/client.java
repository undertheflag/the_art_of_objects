//*************************************************************************
/**
 * Client.java - connect to the Bank server and do some transactions
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import java.io.*;
import java.util.*;
import bank.*;  // from bank.idl

class TerminalException extends Exception {
  public TerminalException (String s) {
    super(s);
  }
}

public class Client {

  static AccountMgrIF manager;
  static AccountIF account = null; // current account
  static String accName = null;    // current account name

  public static void main(String args[])  {
      String accountMgrName = "ABC_Bank_Account_Manager";

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
      manager = AccountMgrIFHelper.bind(orb, accountMgrName, host, options);
      show("...connected to " + accountMgrName + " at " + host);

      // Get input stream
      InputStream input = System.in;
      if (args.length >= 2) {
        try { input = new FileInputStream(args[1]); }
        catch (FileNotFoundException e){}
      }

      runTerminal(manager, input);
  }

  // Run a command-line terminal session on the remote object o.
  public static void runTerminal(AccountMgrIF o, InputStream input) {

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
          if (account != null) o.logout(account);
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

        else if ("open".startsWith(command)) {
          String name = readString(tokenizer);
          String passwd = readString(tokenizer);
          AccInfo accInfo = new AccInfo(name, 0, passwd);
          try {
            // open a new account
            account = o.open(accInfo, 0);
            accName = account.getAccInfo().name;
            // Get the balance of the account.
            float balance = account.getBalance();
            show(" New account opened for: " + name);
            show(" Initial balance is $" + balance);
          } catch (BankError e) {
            show(e.message);
          }

        }

        else if ("remove".startsWith(command)) {
          String name = readString(tokenizer);
          String passwd = readString(tokenizer);
          AccInfo accInfo = new AccInfo(name, 0, passwd);
          try {
            // remove an account
            o.remove(accInfo);
            show(" Account remove: " + name);
          } catch (BankError e) {
            show(e.message);
          }

        }

        else if ("showAccounts".startsWith(command)) {
          show(o.showAccounts());

        }

        else if ("login".startsWith(command)) {
          String name = readString(tokenizer);
          String passwd = readString(tokenizer);
          AccInfo accInfo = new AccInfo(name, 0, passwd);
          try {
            account = o.login(accInfo);
            accName = account.getAccInfo().name;
            show(" Logged in to " + name + "'s account");
          } catch (BankError e) {
            show(e.message);
          }

        }

        else if ("balance".startsWith(command)) {
          if (!isAccountOK(account)) {
            account = null; continue;
          }
          float balance = account.getBalance();
          show(" Current balance: $" + balance);

        }

        else if ("history".startsWith(command)) {
          if (!isAccountOK(account)) {
            account = null; continue;
          }
          String s = account.getHistory();
	  AccInfo info = account.getAccInfo();
          show(" Account history: " + info.name + " (" + info.number + ")\n" + s);

        }

        else if ("deposit".startsWith(command)) {
          if (!isAccountOK(account)) {
            account = null; continue;
          }
          float amount = readFloat(tokenizer);
          try {
            float balance = account.deposit(amount);
            show(" New balance: $" + balance);
          } catch (BankError e) {
            show(e.message);
          }
        }

        else if ("withdraw".startsWith(command)) {
          if (!isAccountOK(account)) {
            account = null; continue;
          }
          float amount = readFloat(tokenizer);
          try {
            float balance = account.withdraw(amount);
            show(" New balance: $" + balance);
          } catch (BankError e) {
            show(e.message);
          }

        }

        else if ("pay".startsWith(command)) {
          if (!isAccountOK(account)) {
            account = null; continue;
          }
          String receiver = readString(tokenizer);
          float amount = readFloat(tokenizer);
          try {
            float balance = account.payBill(receiver, amount);
            show(" New balance: $" + balance);
          } catch (BankError e) {
            show(e.message);
          }

        }

        else if ("transfer".startsWith(command)) {
          if (!isAccountOK(account)) {
            account = null; continue;
          }
          String name = readString(tokenizer);
          float amount = readFloat(tokenizer);
          try {
            float balance = account.transferTo(name, amount);
            show(" New balance: $" + balance);
          } catch (BankError e) {
            show(e.message);
          }

        }

        else if ("quit".startsWith(command)) {
          if (isAccountOK(account)) o.logout(account);
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

  static boolean isAccountOK(AccountIF account) {
    if (account == null) {
      show(" Please login first");
      return false;
    }
    if (account._non_existent()) {
      show(" Account " +accName+ " no longer exist.");
      return false;
    }
    return true;
  }

  static void printHelp() {
    show("-----------------------------------------------");
    show("Enter: command option1 option2 ...");
    show("Valid commands");
    show("  help                         // print this message");
    show("  open account_name password   // open a new account");
    show("  remove account_name password // remove an account");
    show("  showAccounts                 // show a list of accounts");
    show("  login account_name password  // login to an account");
    show("Commands after login/open");
    show("  balance                 // get account balance");
    show("  history                 // get account history");
    show("  deposit amount          // deposit the amount");
    show("  withdraw amount         // withdraw the amount");
    show("  pay receiver amount     // pay a bill to reiver in the amount");
    show("  transfer account_name amount // transfer to another account");
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

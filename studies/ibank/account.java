//*************************************************************************
/**
 * Account.java - This class provides bank account services.
 *   To invoke this class, use "_a=Account" as request parameter to Main.java
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package ibank;

import java.io.*;
import java.util.Hashtable;
import javax.servlet.*;
import javax.servlet.http.*;

import atmcs.*;  // from atmcs.idl

/**
 * This Application Bean provides bank account services.  
 * It assumes security check has been performed in Main.java.
 */

public class Account implements ApplicationBean {

  // static definitions
  final static String URL_BASE = "/ooda/ibank";
  final static String URL_LOGIN = URL_BASE + "/iBankLogin.htm";

  /** Private handles */
  private HttpServletRequest mRequest;
  private HttpServletResponse mResponse;
  private PrintWriter mOut;
  private SessionIF mSessionIF;

  /** private handle for template */
  private static Template mTemplate;

  /** Constructor */
  public Account() {
  }

  /** Constructor */
  public Account(HttpServletRequest request, 
 		 HttpServletResponse response) {
    init(request, response);
  }

  /** Init function for Application Bean */
  public void init(HttpServletRequest request, 
  		   HttpServletResponse response) {
    mRequest = request;
    mResponse = response;

    HttpSession session = request.getSession(true);
    mSessionIF = (SessionIF) session.getValue("sessionIF");

    try {
       mOut = mResponse.getWriter();

       File inFile = new File(Main.mWwwRoot,"ibank/payBill.htm");
       mTemplate = new Template(inFile, null);
       mTemplate.analyze();
    } catch (Exception e) {
       show("Account.init: " + e.getMessage() + "; wwwroot=" + Main.mWwwRoot);
    }

  }

  /** Process command from client.  A function for Application Bean.
   *  Either GET or POST parameters are accepted.
   *  The key for command is "_c".  Valid commands
   *  are list in the method "printHelp()".
   */
  public void process() throws Exception {

    // context for HTML template
    Hashtable context = new Hashtable();

    mOut = mResponse.getWriter();

    String command = mRequest.getParameter("_c");

    AccountInfo aInfo = mSessionIF.getAccountInfo();
    context.put("account name", aInfo.name);
    context.put("bank name", Main.AGENT_ID);

    if (command == null || "help".startsWith(command)) {
      printHelp();
    }

    else if ("balance".startsWith(command)) {
      float balance = mSessionIF.getBalance();
      // show(" Current balance: $" + balance);

      try {
        context.put("balance", ""+balance);
        mTemplate.write(mOut, context);
      } catch (IOException e) {
        show(e.toString());
      }
    }

    else if ("history".startsWith(command)) {
      String s = mSessionIF.getAccountHistory();
      AccountInfo info = mSessionIF.getAccountInfo();
      show("  Account history: " + info.name + " (account No. " + info.number + ")\n" + s);

    }

    else if ("deposit".startsWith(command)) {
      float amount = readFloat(mRequest, "amount");
      try {
	float balance = mSessionIF.deposit(amount);
	show(" New balance: $" + balance);
      } catch (AtmcsError e) {
	show(e.message);
      }
    }

    else if ("withdraw".startsWith(command)) {
      float amount = readFloat(mRequest, "amount");
      try {
	float balance = mSessionIF.withdraw(amount);
	show(" New balance: $" + balance);
      } catch (AtmcsError e) {
	show(e.message);
      }
    }

    else if ("pay".startsWith(command)) {
      String receiver = mRequest.getParameter("receiver");
      float amount = readFloat(mRequest, "amount");

      float balance = mSessionIF.payBill(receiver, amount);
      // show(" New balance: $" + balance);

      try {
        context.put("balance", ""+balance);
        mTemplate.write(mOut, context);
      } catch (IOException e) {
        show(e.toString());
      }

    }

    else if ("transfer".startsWith(command)) {
      String name = mRequest.getParameter("accountName");
      float amount = readFloat(mRequest, "amount");
      try {
	float balance = mSessionIF.transferTo(name, amount);
	show(" New balance: $" + balance);
      } catch (AtmcsError e) {
	show(e.message);
      }
    }

    else {
      show("Account: command not recognized.");
      printHelp();
    }

  }

  void printHelp() {
    show("<PRE>");
    show("-----------------------------------------------");
    show("URL: http://.../ibank.Main?_a=Account&_c='command'&param1=value1&...");
    show("Valid commands for Account service:");
    show("  help                    // print this message");
    show("  balance                 // get balance");
    show("  history                 // get history");
    show("  deposit amount          // deposit the amount");
    show("  withdraw amount         // withdraw the amount");
    show("  pay receiver amount     // pay a bill to reiver in the amount");
    show("  transfer accountName amount // transfer to another account");
    show("");
    show("-----------------------------------------------");
    show("</PRE>");
  }


  // Some utility functions
  static int readInt(HttpServletRequest request, String name)
  throws Exception {
    String v = request.getParameter(name);
    try {
      return Integer.valueOf(v).intValue();
    } catch (NumberFormatException e) {
      throw new Exception("Nonexistent or invalid integer for \""+name+ "\"");
    }
  }

  static float readFloat(HttpServletRequest request, String name)
  throws Exception {
    String v = request.getParameter(name);
    try {
      return Float.valueOf(v).floatValue();
    } catch (NumberFormatException e) {
      throw new Exception("Nonexistent or invalid float for  \""+name+ "\"");
    }
  }

  // show string in a line
  public void show(String s) {
    mOut.println(s + "<BR>");
  }

}

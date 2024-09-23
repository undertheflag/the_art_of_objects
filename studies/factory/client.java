//*************************************************************************
/**
 * Client.java - client to the Factory server
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import java.io.*;
import java.util.*;
import factory.*;  // from factory.idl

class TerminalException extends Exception {
  public TerminalException (String s) {
    super(s);
  }
}

public class Client {

  static ServiceFactory factory;
  static Service service = null; // current service
  static String svName = null;    // current service name

  public static void main(String args[])  {
      String serviceFactoryName = "URL Service Factory";

      show("Usage: java Client [host] [command_file.txt]");

      String host = "localhost";
      if (args.length >= 1) {
        host = args[0];
      }

      // use smart agent at host
      Properties props = new Properties();
      props.put("ORBagentAddr", host);

      // Initiliaze the ORB.
      org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(args, props);

      // default options: no deferred bind, do rebind
      org.omg.CORBA.BindOptions options = new org.omg.CORBA.BindOptions();

      // Locate the service factory.
      factory = ServiceFactoryHelper.bind(orb, serviceFactoryName, host, options);
      show("...connected to " + serviceFactoryName + " at " + host);

      // Get input stream
      InputStream input = System.in;
      if (args.length >= 2) {
        try { input = new FileInputStream(args[1]); }
        catch (FileNotFoundException e){}
      }

      runTerminal(factory, input);
  }

  // Run a command-line terminal session on the remote object o.
  public static void runTerminal(ServiceFactory o, InputStream input) {

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

        else if ("create".startsWith(command)) {
            // open a new service
            service = o.create();
            svName = service.getName();
            show(" New service created: " + svName);
        }

        else if ("find".startsWith(command)) {
          String name = readString(tokenizer);
          try {
            // open a new service
            service = o.find(name);
            svName = service.getName();
            show(" found: " + svName);
          } catch (FactoryError e) {
            show(" " + e);
          }

        }

        else if ("remove".startsWith(command)) {
          String name = readString(tokenizer);
          try {
            o.remove(name);
            service = null;
            svName = null;
            show(" removed: " + name);
          } catch (FactoryError e) {
            show(" " + e);
          }

        }

        else if ("showAll".startsWith(command)) {
          show(o.showAll());
        }

        else if ("get".startsWith(command)) {
          if (!isServiceOK(service)) {
            service = null; continue;
          }
          String url = readString(tokenizer);
          org.omg.CORBA.Any a1 = o._orb().create_any();
          a1.insert_string(url);
          try {
            org.omg.CORBA.Any a = service.performService(a1);
            show(a.extract_string());
          } catch (ServiceError e) {
            show(" " + e);
          }

        }

        else if ("quit".startsWith(command)) {
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

  static boolean isServiceOK(Service service) {
    if (service == null) {
      show(" Please connect to Service Factory first");
      return false;
    }
    if (service._non_existent()) {
      show(" Service " +svName+ " no longer exist.");
      return false;
    }
    return true;
  }

  static void printHelp() {
    show("-----------------------------------------------");
    show("Enter: command option1 option2 ...");
    show("Valid commands");
    show("  help                    // print this message");
    show("  create                  // create a service");
    show("  showAll                 // show all services");
    show("  find serviceName        // find a service");
    show("  remove serviceName      // remove a service");
    show("Commands after getting a service");
    show("  get URL                 // get the content of the URL");
    show("");
    show("  quit                    // quit");
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

  public static void show(String s) {
    System.out.println(s);
  }
}

//*************************************************************************
/**
 * DBRoot.java - This class contains a set of extents for the
 *	persistent classes.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.*;

/**
 * This class is the database root.  It contains a set of extents for the 
 * persistent objects (Person and Hobby).  Both Person and Hobby is 
 * identical to the original one, except they are marked serializable.
 * The persistent enabled PVector is used for the extents.
 */
public class DBRoot implements java.io.Serializable {

  // Will validate password if it is not empty.
  private static String smAdminPassword = null;

  /* Vectors of collection (extents). */
  private PVector persons = new PVector();
  private PVector hobbies = new PVector();

  /**
   * Constructor.
   */
  public DBRoot() {
  }

  /**
   * Constructor
   *
   * @param adminPassword	Password for administrator
   */
  public DBRoot(String adminPassword) {
    smAdminPassword = adminPassword;
  }

  /**
   * Validate the input password
   *
   * @param password	Password to be validated
   */
  public static boolean validatePassword(String password) {
    if (smAdminPassword == null) return true;
    if (smAdminPassword.equals(password)) return true;
    else return false;
  }

  /******** Persons *********/

  /**
   * Returns an array of person objects.  We extract each element in
   * the Vector to construct the returned array.
   *
   * @return  an array of person objects
   */
  public Person[] getPersons() {
    Object[] o = persons.toArray();
    Person[] r = new Person[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Person)o[i];
    return r;
  }

  /**
   * Returns the number of person objects in the extent.
   *
   * @return 	the number of person objects
   */
  public int getPersonCount() {
    return persons.size();
  }

  /**
   * Returns the person object for the input name.
   *
   * @param name 	Name of person
   * @return Person object.  Null if none matched.
   */
  public Person getPerson(String name) {
    Enumeration e = persons.elements();
    while (e.hasMoreElements()) {
      Person c = (Person) e.nextElement();
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  /**
   * Returns whether the input person object is in the extent.
   *
   * @param person 	a person object
   * @return true if the input person object is found.  False otherwise.
   */
  public boolean contains(Person person) {
    return persons.contains(person);
  }

  /**
   * Adds a new person.
   *
   * @param person 	a person object
   */
  public void addPerson(Person person) {
    persons.addElement(person);
  }

  /**
   * Removes the named person from the extent.
   *
   * @param name	Name of person.
   * @exception Exception if the person is not found
   */
  public void removePerson(String name) throws Exception {
    /* get the person Object */
    Person c = getPerson(name);
    if (c == null) throw new Exception("Person not found: " + name);

    /* Remove the person Object from the vector */
    persons.removeElement(c);
  }

  /**
   * Removes the input person form the vector.
   *
   * @param person  The person to be removed.
   * @exception Exception if the person is not found
   */
  public void removePerson(Person person) throws Exception {
    if (!persons.contains(person))
      throw new Exception("Person not found: " + person.getName());
    persons.removeElement(person);
  }

  /**
   * Removes all person objects from the extent.
   */
  public void removeAllPersons( ) {
    persons.removeAllElements();
  }


  /******** Hobbies *********/

  /**
   * Returns an array of hobby objects.  We extract each element in
   * the Vector to construct the returned array.
   *
   * @return  an array of hobby objects
   */
  public Hobby[] getHobbies() {
    Object[] o = hobbies.toArray();
    Hobby[] r = new Hobby[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Hobby)o[i];
    return r;
  }

  /**
   * Returns the number of hobby objects in the extent.
   *
   * @return 	the number of hobby objects
   */
  public int getHobbyCount() {
    return hobbies.size();
  }

  /**
   * Returns the hobby object for the input name.
   *
   * @param name 	Name of hobby
   * @return Hobby object.  Null if none matched.
   */
  public Hobby getHobby(String name) {
    Enumeration e = hobbies.elements();
    while (e.hasMoreElements()) {
      Hobby c = (Hobby) e.nextElement();
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  /**
   * Returns whether the input hobby object is in the extent.
   *
   * @param hobby 	a hobby object
   * @return true if the input hobby object is found.  False otherwise.
   */
  public boolean contains(Hobby hobby) {
    return hobbies.contains(hobby);
  }

  /**
   * Adds a new hobby.
   *
   * @param hobby 	a hobby object
   */
  public void addHobby(Hobby hobby) {
    hobbies.addElement(hobby);
  }

  /**
   * Removes the named hobby from the extent.
   *
   * @param name	Name of hobby.
   * @exception Exception if the hobby is not found
   */
  public void removeHobby(String name) throws Exception {
    /* get the hobby Object */
    Hobby c = getHobby(name);
    if (c == null) throw new Exception("Hobby not found: " + name);

    /* Remove the hobby Object from the vector */
    hobbies.removeElement(c);
  }

  /**
   * Removes the input hobby form the vector.
   *
   * @param hobby  The hobby to be removed.
   * @exception Exception if the hobby is not found
   */
  public void removeHobby(Hobby hobby) throws Exception {
    if (!hobbies.contains(hobby))
      throw new Exception("Hobby not found: " + hobby.getName());
    hobbies.removeElement(hobby);
  }

  /**
   * Removes all hobby objects from the extent.
   */
  public void removeAllHobbies() {
    hobbies.removeAllElements();
  }



  /**
   * Runs a command-line terminal for the object o with the input.
   * It can handle o being a persistent object.
   */
  public static void runTerminal(DBRoot o, InputStream input) {

    /* read command input */
    BufferedReader instream = new BufferedReader(new InputStreamReader(input));
    if (smAdminPassword != null) {
      System.out.println("Please enter admin password:");
      try {
	String password = instream.readLine();
	if (!DBRoot.validatePassword(password)) {
	  System.out.println("Invalid admin password.  Exiting...");
	  return;
	}
      } catch (Exception e) {
	show("  " + e.toString());
	return;
      }
    }

    /* Print help message describing the legal commands */
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
        POManager.shutdown();
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
     synchronized (POManager.TOKEN) {
      try {
        if (POManager.getDB() != null) POManager.beginUpdateTrx();

        if ("showperson".startsWith(command)) {
	  String name = "";
	  try { name = readString(tokenizer); }
	  catch(TerminalException e) { 
	    name = "all";  // default to all if no name specified
	  }
	  
	  if ("all".startsWith(name)) {
	    Person[] ps = o.getPersons();
	    for (int i=0; i<ps.length; i++) {
	      Person p = ps[i];
	      show(" " + p.toString());
	    }
          } else {
	    Person p = o.getPerson(name);
	    show(" " + p.toString());
          }
        }

        else if ("addperson".startsWith(command)) {
          String name = readString(tokenizer);
          int age = readInt(tokenizer);
	  Person p = new Person(name, age);
	  o.addPerson(p);
        }

        else if ("addhobby".startsWith(command)) {
          String name = readString(tokenizer);
          String hobby = readString(tokenizer);
	  Person p = o.getPerson(name);
	  if (p != null) {
  	    Hobby h = new Hobby(hobby, p);
	    o.addHobby(h);
 	    p.addHobby(h);
          } else {
     	    show("No such person: " + name);
	  }
        }

        else if ("removeperson".startsWith(command)) {
          String name = readString(tokenizer);
	  Person p = o.getPerson(name);
	  if (p != null) {
	    // remove all hobbies
	    Hobby[] h = p.getHobbies();
	    for (int i=0; i<h.length; i++) {
	      p.removeHobby(h[i]);
	    }
	    p.removeAllHobbies();
	    o.removePerson(p);
          } else {
     	    show("No such person: " + name);
	  }
        }

        else if ("removehobby".startsWith(command)) {
          String name = readString(tokenizer);
          String hobby = readString(tokenizer);
	  Person p = o.getPerson(name);
	  // remove hobby from person and extent
	  p.removeHobby(hobby);
	  o.removeHobby(hobby);
        }

        else if ("removeall".startsWith(command)) {
	  o.removeAllPersons();
	  o.removeAllHobbies();
	}

        else {
          show(" Command not recognized.  Try \"help\"");
        }

        if (POManager.getDB() != null) POManager.commitTrx();
	
      } catch (Exception e) {

        if (POManager.getDB() != null) POManager.handleTrxError(e);
	System.out.println(e.getMessage());
      }
     }

    }

  }

  /** 
   * Shows help.
   */
  static void printHelp() {
    show("-----------------------------------------------");
    show("Enter: command argument1 argument2 ...");
    show("Valid commands");
    show("  help                   // print this message");
    show("  showperson [name]      // show one or all person(s)");
    show("  addperson name age     // add a person");
    show("  addhobby name hobby    // add a hobby to the person");
    show("  removeperson name      // remove the person");
    show("  removehobby name hobby // remove the hobby from the person");
    show("  removeall              // remove all persons");

    show("");
    show("  quit                   // shutdown DB & quit");
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


  /** 
   * Shows the input string.
   *
   * @s the string to be shown
   */
  public static void show(String s) { 
    System.out.println(s);
  }

  /**
   * Main method for testing.
   *
   * @exception Exception if any exception is thrown
   */
  public static void main(String[] args) throws Exception {

    show("Usage: java DBRoot [database_file.odb] [command_file.txt]");
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
      POManager.initialize(dbfile, "DBRoot");

      DBRoot mgr = (DBRoot)POManager.getDbRoot();

      DBRoot.runTerminal(mgr, input);

      POManager.shutdown();

    } else {  // transient test
      DBRoot mgr = new DBRoot();
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

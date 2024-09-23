//*************************************************************************
/**
 * University.java - This class contains a set of extents for the
 *	persistent classes.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import java.util.*;
import java.io.*;

/**
 * This class is the database root.  It contains a set of extents for the 
 * persistent objects (Department and Campus).  
 * All persistent classes are subclasses of NamedObject, which is 
 * serializable.  Their implementation is the same as non-persistent 
 * classes.
 */
public class University extends NamedObject {

  // Will validate password if it is not empty.
  private static String smAdminPassword = null;

  /* Vectors of collection (extents). */
  private PVector departments = new PVector();
  private PVector campuses = new PVector();

  /**
   * Constructor.
   */
  public University() {
  }

  /**
   * Constructor
   *
   * @param adminPassword	Password for administrator
   */
  public University(String adminPassword) {
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

  /******** Departments *********/

  /**
   * Returns an array of department objects.  We extract each element in
   * the Vector to construct the returned array.
   *
   * @return  an array of department objects
   */
  public Department[] getDepartments() {
    Object[] o = departments.toArray();
    Department[] r = new Department[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Department)o[i];
    return r;
  }

  /**
   * Returns the number of department objects in the extent.
   *
   * @return 	the number of department objects
   */
  public int getDepartmentCount() {
    return departments.size();
  }

  /**
   * Returns the department object for the input name.
   *
   * @param name 	Name of department
   * @return Department object.  Null if none matched.
   */
  public Department getDepartment(String name) {
    Enumeration e = departments.elements();
    while (e.hasMoreElements()) {
      Department c = (Department) e.nextElement();
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  /**
   * Returns whether the input department object is in the extent.
   *
   * @param department 	a department object
   * @return true if the input department object is found.  False otherwise.
   */
  public boolean contains(Department department) {
    return departments.contains(department);
  }

  /**
   * Adds a new department.
   *
   * @param name 	name of the department
   */
  public void addDepartment(String name) {
    Department d = new Department(name);
    departments.addElement(d);
  }

  /**
   * Adds a new department.
   *
   * @param department 	a department object
   */
  public void addDepartment(Department department) {
    departments.addElement(department);
  }

  /**
   * Removes the named department from the extent.
   *
   * @param name	Name of department.
   * @exception Exception if the department is not found
   */
  public void removeDepartment(String name) throws Exception {
    /* get the department Object */
    Department c = getDepartment(name);
    if (c == null) throw new Exception("Department not found: " + name);

    /* Remove the department Object from the vector */
    departments.removeElement(c);
  }

  /**
   * Removes the input department form the vector.
   *
   * @param department  The department to be removed.
   * @exception Exception if the department is not found
   */
  public void removeDepartment(Department department) throws Exception {
    if (!departments.contains(department))
      throw new Exception("Department not found: " + department.getName());
    departments.removeElement(department);
  }

  /**
   * Removes all department objects from the extent.
   */
  public void removeAllDepartments( ) {
    departments.removeAllElements();
  }


  /******** Campuses *********/

  /**
   * Returns an array of campus objects.  We extract each element in
   * the Vector to construct the returned array.
   *
   * @return  an array of campus objects
   */
  public Campus[] getCampuses() {
    Object[] o = campuses.toArray();
    Campus[] r = new Campus[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Campus)o[i];
    return r;
  }

  /**
   * Returns the number of campus objects in the extent.
   *
   * @return 	the number of campus objects
   */
  public int getCampusCount() {
    return campuses.size();
  }

  /**
   * Returns the campus object for the input name.
   *
   * @param name 	Name of campus
   * @return Campus object.  Null if none matched.
   */
  public Campus getCampus(String name) {
    Enumeration e = campuses.elements();
    while (e.hasMoreElements()) {
      Campus c = (Campus) e.nextElement();
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  /**
   * Returns whether the input campus object is in the extent.
   *
   * @param campus 	a campus object
   * @return true if the input campus object is found.  False otherwise.
   */
  public boolean contains(Campus campus) {
    return campuses.contains(campus);
  }

  /**
   * Adds a new campus.
   *
   * @param name 	name of the campus
   */
  public void addCampus(String name) {
    Campus c = new Campus(name);
    campuses.addElement(c);
  }

  /**
   * Adds a new campus.
   *
   * @param campus 	a campus object
   */
  public void addCampus(Campus campus) {
    campuses.addElement(campus);
  }

  /**
   * Removes the named campus from the extent.
   *
   * @param name	Name of campus.
   * @exception Exception if the campus is not found
   */
  public void removeCampus(String name) throws Exception {
    /* get the campus Object */
    Campus c = getCampus(name);
    if (c == null) throw new Exception("Campus not found: " + name);

    /* Remove the campus Object from the vector */
    campuses.removeElement(c);
  }

  /**
   * Removes the input campus form the vector.
   *
   * @param campus  The campus to be removed.
   * @exception Exception if the campus is not found
   */
  public void removeCampus(Campus campus) throws Exception {
    if (!campuses.contains(campus))
      throw new Exception("Campus not found: " + campus.getName());
    campuses.removeElement(campus);
  }

  /**
   * Removes all campus objects from the extent.
   */
  public void removeAllCampuses( ) {
    campuses.removeAllElements();
  }

  /******** Other queries *********/

  /**
   * Returns the course object for the input name.  It scans thru
   * all departments to locate the course.
   *
   * @param name 	Name of course
   * @return Course object.  Null if none matched.
   */
  public Course getCourse(String name) {
    Enumeration e = departments.elements();
    while (e.hasMoreElements()) {
      Department d = (Department) e.nextElement();
      Course c = d.getCourse(name);
      if (c != null) return c;
    }
    return null;
  }

  /**
   * Returns the instructor object for the input name.  It scans thru
   * all departments to locate the instructor.
   *
   * @param name 	Name of instructor
   * @return Instructor object.  Null if none matched.
   */
  public Instructor getInstructor(String name) {
    Enumeration e = departments.elements();
    while (e.hasMoreElements()) {
      Department d = (Department) e.nextElement();
      Instructor c = d.getInstructor(name);
      if (c != null) return c;
    }
    return null;
  }

  /**
   * Returns the classroom object for the input name.  It scans thru
   * all campuss to locate the classroom.
   *
   * @param name 	Name of classroom
   * @return Classroom object.  Null if none matched.
   */
  public Classroom getClassroom(String name) {
    Enumeration e = campuses.elements();
    while (e.hasMoreElements()) {
      Campus d = (Campus) e.nextElement();
      Classroom c = d.getClassroom(name);
      if (c != null) return c;
    }
    return null;
  }


  /**
   * Runs a command-line terminal for the object o with the input.
   * It can handle o being a persistent object.
   */
  public static void runTerminal(University o, InputStream input) {

    /* read command input */
    BufferedReader instream = new BufferedReader(new InputStreamReader(input));
    if (smAdminPassword != null) {
      System.out.println("Please enter admin password:");
      try {
	String password = instream.readLine();
	if (!University.validatePassword(password)) {
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

        if ("show".startsWith(command)) {
	  String name = "";
	  try { name = readString(tokenizer); }
	  catch(TerminalException e) { 
	    name = "all";  // default to all if no name specified
	  }
	  
	  if ("all".startsWith(name)) {
	    Department[] ps = o.getDepartments();
	    for (int i=0; i<ps.length; i++) {
	      Department p = ps[i];
	      show(p.toString());
	    }
	    Campus[] cs = o.getCampuses();
	    for (int i=0; i<cs.length; i++) {
	      Campus c = cs[i];
	      show(c.toString());
	    }
          } else {
	    Department p = o.getDepartment(name);
	    if (p != null) {
	      show(p.toString());
	    } else {
  	      Campus c = o.getCampus(name);
	      if (c != null) show(c.toString());
	      else show("None found.");
	    } 
          }
	  show("--------------------");
        }

        else if ("adddepartment".startsWith(command)) {
          String name = readString(tokenizer);
	  o.addDepartment(name);
        }

        else if ("addcourse".startsWith(command)) {
          String name = readString(tokenizer);
          String dept = readString(tokenizer);
	  Department d = o.getDepartment(dept);
	  if (d != null) {
	    d.addCourse(name);
	  } else {
	    show("No such department: " + dept);
	  }
        }

        else if ("addinstructor".startsWith(command)) {
          String name = readString(tokenizer);
          String dept = readString(tokenizer);
	  Department d = o.getDepartment(dept);
	  if (d != null) {
	    d.addInstructor(name);
	  } else {
	    show("No such department: " + dept);
	  }
        }

        else if ("addcampus".startsWith(command)) {
          String name = readString(tokenizer);
	  o.addCampus(name);
        }

        else if ("addclassroom".startsWith(command)) {
          String name = readString(tokenizer);
          String campus = readString(tokenizer);
	  Campus c = o.getCampus(campus);
	  if (c != null) {
	    c.addClassroom(name);
	  } else {
	    show("No such campus: " + campus);
	  }
        }

        else if ("addsession".startsWith(command)) {
          String name = readString(tokenizer);
          String course = readString(tokenizer);
          String schedule = readString(tokenizer);
          String classroom = readString(tokenizer);

          String itor1 = readString(tokenizer);
	  String itor2;
	  try { itor2 = readString(tokenizer); }
	  catch(TerminalException e) { 
	    itor2 = "none";  // default to none
	  }

	  Course c = o.getCourse(course);
	  if (c != null) {
	    c.addSession(name, 
	      new Schedule(schedule), o.getClassroom(classroom),
	      o.getInstructor(itor1), o.getInstructor(itor2) );
	  } else {
	    show("No such course: " + course);
	  }
        }

        else if ("removedepartment".startsWith(command)) {
          String name = readString(tokenizer);
	  o.removeDepartment(name);
        }

        else if ("removecourse".startsWith(command)) {
          String name = readString(tokenizer);
          String dept = readString(tokenizer);
	  Department d = o.getDepartment(dept);
	  if (d != null) {
	    d.removeCourse(name);
	  } else {
	    show("No such department: " + dept);
	  }
        }

        else if ("removeinstructor".startsWith(command)) {
          String name = readString(tokenizer);
          String dept = readString(tokenizer);
	  Department d = o.getDepartment(dept);
	  if (d != null) {
	    d.removeInstructor(name);
	  } else {
	    show("No such department: " + dept);
	  }
        }

        else if ("removecampus".startsWith(command)) {
          String name = readString(tokenizer);
	  o.removeCampus(name);
        }

        else if ("removeclassroom".startsWith(command)) {
          String name = readString(tokenizer);
          String campus = readString(tokenizer);
	  Campus c = o.getCampus(campus);
	  if (c != null) {
	    c.removeClassroom(name);
	  } else {
	    show("No such campus: " + campus);
	  }
        }

        else if ("removesession".startsWith(command)) {
          String name = readString(tokenizer);
          String course = readString(tokenizer);

	  Course c = o.getCourse(course);
	  if (c != null) {
	    c.removeSession(name);
	  } else {
	    show("No such course: " + course);
	  }
        }

        else if ("removeall".startsWith(command)) {
	  o.removeAllCampuses();
	  o.removeAllDepartments();
        }

        else if ("test".startsWith(command)) {
          int nDept = readInt(tokenizer);
          int nCourse = readInt(tokenizer);
	  test(o, nDept, nCourse);
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
    show("Enter: command option1 option2 ...");
    show("Valid commands");
    show("  help                     // print this message");
    show("  show [department|campus|all] // show department / campus");

    show("  adddepartment name       // add a department");
    show("  addcourse name dept      // add a course to the department");
    show("  addinstructor name dept  // add an instructor to the department");

    show("  addcampus name           // add a campus to the department");
    show("  addclassroom name campus // add a classroom to the campus");

    show("  addsession name course schedule classroom instructor1 [instructor2]");
    show("                           // add a session to a course");

    show("  removedepartment name      // remove the department");
    show("  removecourse name dept     // remove a course from department");
    show("  removeinstructor name dept // remove an instructor from department");

    show("  removecampus name          // remove the campus from the department");
    show("  removeclassroom name campus// remove a classroom from campus");

    show("  removesession name course  // remove the session under the course");
    show("  removeall                  // remove all campuses & departments");

    show("");
    show("  test nDept nCourse         // a test to add departments & course");
    show("  quit                       // shutdown DB & quit");
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

    show("Usage: java University [database_file] [command_file.txt]");
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
      POManager.initialize(dbfile, "University");

      University mgr = (University)POManager.getDbRoot();

      University.runTerminal(mgr, input);

      POManager.shutdown();

    } else {  // transient test
      University mgr = new University();
      runTerminal(mgr, input);
    }

  }

  /**
   * A method for testing.  Add nDept departments to the input
   * university.  Each department having nCourse courses.
   *
   * @exception Exception if any exception is thrown
   */
  public static void test(University u, int nDept, int nCourse)
      throws Exception {
    for (int i=0; i<nDept; i++) {
      Department d = new Department("Department " + i);
      u.addDepartment(d);
      for (int j=0; j<nCourse; j++) {
        d.addCourse("Course " + i + "." + j);
      }
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

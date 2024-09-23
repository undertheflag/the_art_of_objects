//*************************************************************************
/**
 * Version.java - A class describing a Version
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Date;

/**
 * This class is the superclass of Revision.  It forms a relationship loop
 * with Revision (a link list with Version being the head).
 * <P>
 * You may implement other operations such as getLastRevision(),
 * getVersionCount().  They are recursive operations.
 * <P>
 * Can you think of a way to extend this sample to allow branching?
 * That is, allow more than one link list under a Verison.
 */
public class Version {

  // Attributes in the Version class
  private String name;
  private String author;
  private Date date;
  private Revision next;

  /** 
   * Constructs a Version object.
   *
   * @param name	name of the Version
   * @param author	author of the Version
   * @param date	date of the Version
   */
  public Version(String name, String author, Date date) {
    this.name = name; 
    this.author = author;
    this.date = date;
  }

  /** 
   * Returns the name of this Version.
   *
   * @return the name of this Version
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Returns the author of this Version.
   *
   * @return the author of this Version
   */
  public String getAuthor() { 
    return author; 
  }

  /** 
   * Returns the date of this Version.
   *
   * @return the date of this Version
   */
  public Date getDate() { 
    return date; 
  }

  /** 
   * Returns the next Revision of this Version.
   *
   * @return the next Revision of this Version
   */
  public Revision getNextRevision() { 
    return next; 
  }

  /** 
   * Sets the name of this Version.
   *
   * @param name	the name of this Version
   */
  public void setName(String name) { 
    this.name = name; 
  }

  /** 
   * Sets the author of this Version.
   *
   * @param author	the author of this Version
   */
  public void setAuthor(String author) { 
    this.author = author; 
  }

  /** 
   * Sets the date of this Version.
   *
   * @param date	the date of this Version
   */
  public void setDate(Date date) { 
    this.date = date; 
  }

  /** 
   * Sets the next revision of this Version.
   *
   * @param revision 	the next revision
   */
  public void setNextRevision(Revision revision) { 
    this.next = revision; 
  }

  /** 
   * Returns the information of this Version as a string.
   *
   * @return the information of this Version
   */
  public String toString() { 
    String s = name + " - by " + author;
    s += " on " + date;
    s += "\n";
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


  /**
   * A test that returns a string.  Note it is a static method.
   *
   * @return result of the test as a string
   * @exception Exception if any exception is thrown
   */
  public static String test() throws Exception {
    String s;

    Version original = new Version("V. 1", "Jane", new Date());
    Revision r1 = new Revision("1.1.1", "Tom", new Date(), original);
    Revision r2 = new Revision("1.1.2", "Tom", new Date(), r1);
    Revision r3 = new Revision("1.1.3", "Jane", new Date(), r2);

    s = r3.toString();
    s += "\n";

    return s;
  }

  /**
   * Main method for testing.
   *
   * @exception Exception if any exception is thrown
   */
  public static void main(String argv[]) throws Exception {
    show(test());
  }

}


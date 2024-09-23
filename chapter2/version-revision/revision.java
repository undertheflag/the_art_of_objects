//*************************************************************************
/**
 * Revision.java - A class describing a Revision
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Date;

/**
 * This class is the subclass of Version.  It forms a relationship loop
 * with Version (a link list with Version being the head).
 * <P>
 * You may implement other operations such as getFirstVersion(),
 * which is a recursive operation.
 */
public class Revision extends Version {

  // Attributes in the Revision class
  private Version previous;

  /** 
   * Constructs a Revision object.
   *
   * @param name	name of the Revision
   * @param author	author of the Revision
   * @param date	date of the Revision
   * @param previous	previous version
   */
  public Revision(String name, String author, Date date, Version previous) {
    super(name, author, date);
    this.previous = previous;
    previous.setNextRevision(this);  // backward link
  }

  /** 
   * Returns the previous Version of this Revision.
   *
   * @return the previous Version of this Revision
   */
  public Version getPreviousVersion() { 
    return previous; 
  }

  /** 
   * Sets the previous version of this Revision.
   *
   * @param version 	the previous version
   */
  public void setPreviousVersion(Revision version) { 
    this.previous = version; 
  }

  /** 
   * Returns the information of this Revision and all its previous
   * versions as a string.
   * This is a recursive operation.
   *
   * @return the information of this Revision
   */
  public String toString() { 
    String s = super.toString();  // the operation in Version
    s += getPreviousVersion().toString();
    return s;
  }

}


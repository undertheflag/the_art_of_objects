//*************************************************************************
/**
 * Year.java - A class describing a year
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Vector;

/**
 * A class describing a year.
 * The classes Committee, Person, and Year are part of a ternary
 * association.  The links of the association are stored in the 
 * variable "posts".
 */
public class Year {
  /** the year */
  private int year;

  /** A vector is used to contain the {committee, person} pairs. */
  private Vector posts = new Vector();


  /** 
   * Constructs a year object.
   *
   * @param year	the year
   */
  public Year(int year) {
    this.year = year; 
  }

  /** 
   * Returns the year.
   *
   * @return the year
   */
  public int getYear() { 
    return year; 
  }

  /** 
   * Returns the posts in this year as an array of {committee, person} pairs.
   *
   * @return the posts in this year as an array of {committee, person} pairs
   */
  public Object[][] getPosts() { 
    // forces the array elements to be of type Object
    return (Object[][]) posts.toArray(new Object[0][]);
  }

  /** 
   * Add a new post in this year.  Note that this 
   * this a package scope operation (not public) since
   * by itself it does not ensure referential integrity.
   *
   * @param committee	the committee object
   * @param person	the person object
   */
  void addPost(Committee committee, Person person) {
    Object[] o = {committee, person};
    posts.add(o);
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    String s = "Year: " + year + "\n";
    return s;
  }
}

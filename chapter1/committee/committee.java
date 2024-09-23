//*************************************************************************
/**
 * Committee.java - A class describing a committee
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Vector;

/**
 * A class describing a committee.
 * The classes Committee, Person, and Year are part of a ternary
 * association.  The links of the association are stored in the 
 * variable "posts".
 * <P>
 * This class also demonstrates the use of object arrays to
 * hold the link information.
 * This class contains a core operation "addPost" for referential 
 * integrity.
 * <P>
 * The enforcement of the candidate key requirement and the 
 * multiplicity constraints given in the book is left as an
 * exercise for you.  You may also add the operation removePost.
 */
public class Committee {

  /** the name of the committee */
  private String name;

  /** A vector is used to contain the {person, year} pairs. */
  private Vector posts = new Vector();

  /** 
   * Constructs a committee object.
   *
   * @param name	name of the committee
   */
  public Committee(String name) {
    this.name = name; 
  }

  /** 
   * Returns the name of this committee.
   *
   * @return the name of this committee
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Returns the posts in this committee as an array of {person, year} pairs.
   *
   * @return the posts in this committee as an array of {person, year} pairs
   */
  public Object[][] getPosts() { 
    // forces the array elements to be of type Object
    return (Object[][]) posts.toArray(new Object[0][]);
  }

  /** 
   * Add a new post in this committee.  This is a core operation
   * for referential integrity.
   *
   * @param person	the person object
   * @param year	the year object
   */
  public void addPost(Person person, Year year) {
    Object[] o = {person, year};
    posts.add(o);

    // add backward links to ensure referential integrity
    person.addPost(this, year);
    year.addPost(this, person);
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    String s = "Committee: " + name + "\n";
    s += "Posts:\n";

    Object[][] ps = getPosts();
    for (int i=0; i<ps.length; i++) {
      s += "  " + ((Person) ps[i][0]).getName();
      s += "/" + ((Year) ps[i][1]).getYear();
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


  /**
   * A test that returns a string.  Note it is a static method.
   *
   * @return result of the test as a string
   * @exception Exception if any exception is thrown
   */
  public static String test() throws Exception {
    String s = "";

    Person p1 = new Person("John", 20);
    Person p2 = new Person("Mary", 18);
    Person p3 = new Person("Tom", 60);
    Person p4 = new Person("Kathy", 65);

    Committee c1 = new Committee("Fund Raising Committee");
    Committee c2 = new Committee("Ethnics Committee");

    Year y1 = new Year(2001);
    Year y2 = new Year(2002);
    Year y3 = new Year(2003);
    Year y4 = new Year(2004);

    s += "... Committees:\n";
    s += c1.toString();
    s += "\n";
    s += c2.toString();
    s += "\n";

    s += "... Persons:\n";
    s += p1.toString();
    s += "\n";
    s += p2.toString();
    s += "\n";
    s += p3.toString();
    s += "\n";
    s += p4.toString();
    s += "\n";

    // Make post assignments
    c1.addPost(p1, y1);
    c1.addPost(p2, y1);
    c1.addPost(p4, y1);

    c2.addPost(p3, y1);
    c2.addPost(p3, y2);
    c2.addPost(p3, y4);
    c2.addPost(p1, y1);
    c2.addPost(p4, y1);
    c2.addPost(p4, y3);

    s += "... after post assignments\n";

    s += "... Committees:\n";
    s += c1.toString();
    s += "\n";
    s += c2.toString();
    s += "\n";

    s += "... Persons:\n";
    s += p1.toString();
    s += "\n";
    s += p2.toString();
    s += "\n";
    s += p3.toString();
    s += "\n";
    s += p4.toString();
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

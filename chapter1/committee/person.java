//*************************************************************************
/**
 * Person.java - A class describing a person
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Vector;

/**
 * A class describing a person.
 * The classes Committee, Person, and Year are part of a ternary
 * association.  The links of the association are stored in the 
 * variable "posts".
 */
public class Person {

  // Private attributes in the Person class
  private String name;
  private int age;

  /** A vector is used to contain the {committee, year} pairs. */
  private Vector posts = new Vector();


  /** 
   * Constructs a person object.
   *
   * @param name	name of the person
   * @param age		age of the person
   */
  public Person(String name, int age) {
    this.name = name; 
    this.age = age;
  }

  /** 
   * Returns the name of this person.
   *
   * @return the name of this person
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Returns the age of this person.
   *
   * @return the age of this person
   */
  public int getAge() {
    return age; 
  }

  /** 
   * Changes the name of this person.
   *
   * @param name	the new name of this person
   */
  public void changeName(String name) { 
    this.name = name; 
  }
 
  /** 
   * Increments the age of this person.
   *
   */
  public void incrementAge() { 
    age++; 
  }

  /** 
   * Sets the age of this person.
   *
   * @param age		the age of this person
   */
  public void setAge(int age) { 
    this.age = age; 
  }

  /** 
   * Returns the posts assumed by this person.
   *
   * @return the posts assumed by this person as an array of 
   *		{committee, year} pairs
   */
  public Object[][] getPosts() { 
    // forces the array elements to be of type Object
    return (Object[][]) posts.toArray(new Object[0][]);
  }

  /** 
   * Add a new post for this person.  Note that this 
   * this a package scope operation (not public) since
   * by itself it does not ensure referential integrity.
   *
   * @param committee	the committee object
   * @param year	the year object
   */
  public void addPost(Committee committee, Year year) {
    Object[] o = {committee, year};
    posts.add(o);
  }

  /** 
   * Returns the information of this person as a string.
   *
   * @return the information of this person
   */
  public String toString() { 
    String s = "Name: " + name + "\n";
    s += "Age: " + age + "\n";
    s += "Posts:\n";

    Object[][] ps = getPosts();
    for (int i=0; i<ps.length; i++) {
      s += "  " + ((Committee) ps[i][0]).getName();
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

}

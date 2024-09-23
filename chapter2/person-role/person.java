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
 * This and the Role class and its subclasses form a handle-body pattern.
 * 
 */
public class Person {

  // Private attributes in the Person class
  private String name;
  private int age;

  /** A vector is used to contain the roles. */
  private Vector roles = new Vector();


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
   * Returns the roles of this person.
   *
   * @return the roles of this person as an array
   */
  public Role[] getRoles() { 
    // forces the array elements to be of type Role
    return (Role[]) roles.toArray(new Role[0]);
  }

  /** 
   * Add a new role for this person.
   *
   * @param role	a role
   */
  public void addRole(Role role) {
    roles.add(role);
  }

  /** 
   * Returns the information of this person as a string.
   *
   * @return the information of this person
   */
  public String toString() { 
    String s = "Name: " + name + "\n";
    s += "Age: " + age + "\n";
    s += "Roles:\n";

    Role[] h = getRoles();
    for (int i=0; i<h.length; i++) {
      s += "  " + h[i].toString();
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

    User u1 = new User("u-john", "abcd");
    User u2 = new User("u-mary", "4321");
    User u3 = new User("u-john-2", "9999");

    Buyer b1 = new Buyer("John", "40 Water St");
    Buyer b2 = new Buyer("Mary", "101 Pearl St");

    p1.addRole(u1);
    p1.addRole(u3);
    p1.addRole(b1);

    p2.addRole(u2);
    p2.addRole(b2);

    s += "u2 is of type: " + u2.getType();
    s += "\n";
    s += "b1 is of type: " + b1.getType();
    s += "\n";
    s += "\n";

    s += p1.toString();
    s += "\n";
    s += p2.toString();
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

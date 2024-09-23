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
 * This and the Hobby classes demonstrate a binary association (one to 
 * many and bi-directional).  Referential integrity is ensured by the 
 * Hobby constructor.
 */
public class Person {

  // Private attributes in the Person class
  private String name;
  private int age;

  /** A vector is used to contain the hobbies. */
  private Vector hobbies = new Vector();


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
   * Returns the hobbies of this person.
   *
   * @return the hobbies of this person as an array
   */
  public Hobby[] getHobbies() { 
    // forces the array elements to be of type Hobby
    return (Hobby[]) hobbies.toArray(new Hobby[0]);
  }

  /** 
   * Add a new hobby for this person.
   *
   * @param hobby	name of the hobby
   */
  public void addHobby(String hobbyName) {
    hobbies.add(new Hobby(hobbyName, this));
  }

  /** 
   * Returns the information of this person as a string.
   *
   * @return the information of this person
   */
  public String toString() { 
    String s = "Name: " + name + "\n";
    s += "Age: " + age + "\n";
    s += "Hobbies:\n";

    Hobby[] h = getHobbies();
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
    String s;

    Person p1 = new Person("John", 20);
    Person p2 = new Person("Mary", 18);

    p1.addHobby("Biking");
    p1.addHobby("Swimming");

    p2.addHobby("Biking");
    p2.addHobby("Dancing");

    s = p1.toString();
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


//*************************************************************************
/**
 * Person.java - A class describing a person
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;

/**
 * A class describing a person.
 * This and the Hobby classes demonstrate a binary association (one to 
 * many and bi-directional).  Referential integrity is ensured by the 
 * Hobby constructor.
 * <P>
 * This class is the same as the original Person class 
 * (chapter2\Person-Hobby) except it uses PVector, which is a 
 * persistent enabled class.  It is also Serializable.
 */
public class Person implements java.io.Serializable {

  // Private attributes in the Person class
  private String name;
  private int age;

  /** A vector is used to contain the hobbies. */
  private PVector hobbies = new PVector();


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


  /******** Hobbies *********/

  /** 
   * Returns the hobbies of this person.
   *
   * @return the hobbies of this person as an array
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
   * Returns whether the input hobby object exists.
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
   * Add a new hobby for this person.
   *
   * @param hobby	name of the hobby
   */
  public void addHobby(String hobbyName) {
    hobbies.add(new Hobby(hobbyName, this));
  }

  /**
   * Removes the named hobby.
   *
   * @param name	Name of hobby.
   * @exception 	if the hobby is not found
   */
  public void removeHobby(String name) throws Exception {
    /* get the hobby Object */
    Hobby c = getHobby(name);
    if (c == null) throw new Exception("Hobby not found: " + name);

    /* Remove the hobby Object from the vector */
    hobbies.removeElement(c);
  }

  /**
   * Removes the input hobby.
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
   * Removes all hobby objects.
   */
  public void removeAllHobbies( ) {
    hobbies.removeAllElements();
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


//*************************************************************************
/**
 * Person.java - A simple class describing a person
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This is a simple class that demonstrates the basic concepts of
 * attributes and operations.
 */
public class Person {

  // Private attributes in the Person class
  private String name;
  private int age;

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
   * Returns the information of this person as a string.
   *
   * @return the information of this person
   */
  public String toString() { 
    String s = "Name: " + name + "\n";
    s += "Age: " + age + "\n";
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

    s = p1.toString();
    s += p2.toString();

    p1.incrementAge();

    p2.changeName("Mary Ann");
    p2.setAge(p2.getAge()+1);  // test setAge

    s += "\n";
    s += "... after changes\n";

    // test the get methods
    s += p1.getName() + "\t";
    s += p1.getAge() + "\n";

    s += p2.getName() + "\t";
    s += p2.getAge() + "\n";

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

  // -------------------------------------------------------------
  // A person may have children.  It is left as an exercise for
  // you to fully implement this feature!  Some skeletal codes are
  // provided for you.
  // -------------------------------------------------------------

  private Person[] children = null;

  /** 
   * Constructs a person object with some children.
   *
   * @param name	name of the person
   * @param age		age of the person
   * @param children	an array of children (each is a Person)
   */
  public Person(String name, int age, Person children[]) {
    this.name = name;
    this.age = age; 
    this.children = children;
  }

  /** 
   * Returns the children of this person.
   *
   * @return the children of this person
   */
  public Person[] getChildren() { 
    return children; 
  }

  /** 
   * Sets the children of this person.
   *
   * @children the children of this person
   */
  public void setChildren(Person[] children) {
    this.children = children;
  }

}


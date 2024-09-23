//*************************************************************************
/**
 * Hobby.java - A class describing a hobby
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing a hobby.
 * Only one person is associated with each hohby.
 * This and the Person classes demonstrate a binary association 
 * (bi-directional).  Referential integrity is ensured by the 
 * Hobby constructor.
 * This class is also Serializable.
 */
public class Hobby implements java.io.Serializable {
  /** the name of the hobby */
  private String name;

  /** the person who has this hobby */
  private Person person;

  /** 
   * Constructs a hobby object.
   *
   * @param name	name of the hobby
   * @param person	the person object who has the hobby
   */
  public Hobby(String name, Person person) {
    this.name = name; 
    this.person = person;
  }

  /** 
   * Returns the name of this hobby.
   *
   * @return the name of this hobby
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Returns the person object of this hobby.
   *
   * @return the person object of this hobby
   */
  public Person getPerson() { 
    return person; 
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    String s = name;
    s += " (" + person.getName() + ")\n";
    return s;
  }
}

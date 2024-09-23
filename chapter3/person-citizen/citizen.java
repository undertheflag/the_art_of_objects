//*************************************************************************
/**
 * Citizen.java - A subclass of Person.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class demonstrates the basic concepts of inheritance.
 * 
 */
public class Citizen extends Person {

  // Private attributes in the Citizen class
  private int passportNumber;

  /** 
   * Constructs a citizen object.
   *
   * @param name	name of the person
   * @param age		age of the person
   * @param passportNumber	passport number of the citizen
   */
  public Citizen(String name, int age, int passportNumber) {
    // Invokes superclass' constructor.  This must be the first 
    // statement.
    super(name, age);
    this.passportNumber = passportNumber;
  }

  /** 
   * Returns the passportNumber of this citizen.
   *
   * @return the passportNumber of this citizen
   */
  public int getPassportNumber() {
    return passportNumber; 
  }

  /** 
   * Returns the information of this citizen as a string.
   * Information from the superclass Person is also included.
   *
   * @return the information of this citizen
   */
  public String toString() { 
    // call the operation in the superclass
    String s = super.toString();
    s += "  passport: " + passportNumber + "\n";
    return s;
  }

}


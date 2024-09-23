//*************************************************************************
/**
 * Resident.java - A subclass of Person.
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
public class Resident extends Person {

  // Private attributes in the Resident class
  private String nationality;
  private String visaType;

  /** 
   * Constructs a resident object.
   *
   * @param name	name of the person
   * @param age		age of the person
   * @param nationality	nationality of the resident
   * @param visaType	visa type of the resident
   */
  public Resident(String name, int age, String nationality, String visaType) {
    // Invokes superclass' constructor.  This must be the first 
    // statement.
    super(name, age);
    this.nationality = nationality;
    this.visaType = visaType;
  }

  /** 
   * Returns the nationality of this resident.
   *
   * @return the nationality of this resident
   */
  public String getNationality() {
    return nationality; 
  }

  /** 
   * Returns the visaType of this resident.
   *
   * @return the visaType of this resident
   */
  public String getVisaType() {
    return visaType; 
  }

  /** 
   * Returns the information of this resident as a string.
   * Information from the superclass Person is also included.
   *
   * @return the information of this resident
   */
  public String toString() { 
    // call the operation in the superclass
    String s = super.toString();
    s += "  nationality: " + nationality + ", ";
    s += "visa type: " + visaType + "\n";
    return s;
  }

}


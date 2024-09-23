//*************************************************************************
/**
 * Car.java - A class describing a car
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Vector;

/**
 * A class describing a car.
 * This and the Car classes demonstrate an aggregation (many to many).
 * This class also contains a core operation for referential integrity.
 */
public class Car {

  // Private attributes in the Car class
  private String manufacturer;
  private int year;

  /** A vector is used to contain the parts. */
  private Vector parts = new Vector();


  /** 
   * Constructs a car object.
   *
   * @param manufacturer	manufacturer of the car
   * @param year		year of the car
   */
  public Car(String manufacturer, int year) {
    this.manufacturer = manufacturer; 
    this.year = year;
  }

  /** 
   * Returns the manufacturer of this car.
   *
   * @return the manufacturer of this car
   */
  public String getManufacturer() { 
    return manufacturer; 
  }

  /** 
   * Returns the year of this car.
   *
   * @return the year of this car
   */
  public int getYear() {
    return year; 
  }

  /** 
   * Returns the parts of this car.
   *
   * @return the parts of this car as an array
   */
  public Part[] getParts() { 
    // forces the array elements to be of type Part
    return (Part[]) parts.toArray(new Part[0]);
  }

  /** 
   * Add a new part for this car.  This is a core operation
   * for referential integrity.
   *
   * @param part	the new part
   */
  public void addPart(Part part) {
    parts.add(part);
    part.addCar(this);  // this ensure referential integrity
  }

  /** 
   * Returns the information of this car as a string.
   *
   * @return the information of this car
   */
  public String toString() { 
    String s = "Manufacturer: " + manufacturer + "\n";
    s += "Year: " + year + "\n";
    return s;
  }

  /** 
   * Returns all the parts of this car as a string.
   *
   * @return the parts of this car as a string
   */
  public String partsToString() { 
    String s = "Parts:\n";
    Part[] h = getParts();
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

    Car c1 = new Car("Honda", 1998);
    Car c2 = new Car("Acura", 1999);
    Car c3 = new Car("Ford", 2001);

    Part p1 = new Part("Oil Filter", 13);
    Part p2 = new Part("Oil Filter", 4043);
    Part p3 = new Part("Tire", 1023);
    Part p4 = new Part("Navigation Monitor", 99023);

    c1.addPart(p1);
    c1.addPart(p3);

    c2.addPart(p1);
    c2.addPart(p3);
    c2.addPart(p4);

    c3.addPart(p3);
    c3.addPart(p4);

    s = c1.toString();
    s += c1.partsToString();
    s += "\n";

    s += c2.toString();
    s += c2.partsToString();
    s += "\n";

    s += c3.toString();
    s += c3.partsToString();
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


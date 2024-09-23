//*************************************************************************
/**
 * Part.java - A class describing a part
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Vector;

/**
 * A class describing a part.
 * This and the Car classes demonstrate an aggregation (many to many).
 */
public class Part {
  /** the name of the part */
  private String name;

  /** the number of the part */
  private int number;

  /** A vector is used to contain the cars that use this part. */
  private Vector cars = new Vector();

  /** 
   * Constructs a part object.
   *
   * @param name	name of the part
   * @param number	the part number
   */
  public Part(String name, int number) {
    this.name = name; 
    this.number = number;
  }

  /** 
   * Returns the name of this part.
   *
   * @return the name of this part
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Returns the number of this part.
   *
   * @return the number of this part
   */
  public int getNumber() { 
    return number; 
  }

  /** 
   * Returns the cars using this part.
   *
   * @return the cars using this part as an array
   */
  public Car[] getCars() { 
    // forces the array elements to be of type Car
    return (Car[]) cars.toArray(new Car[0]);
  }

  /** 
   * Add a car that uses this part.  Note that this 
   * this a package scope operation (not public) since
   * by itself it does not ensure referential integrity.
   *
   * @param car	a car that uses this part
   */
  void addCar(Car car) {
    cars.add(car);
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    String s = name;
    s += " - " + getNumber() + "  ";

    s += "  (used in: ";
    Car[] c = getCars();
    for (int i=0; i<c.length; i++) {
      if (i > 0) s += ", ";
      s += c[i].getManufacturer() + " " + c[i].getYear();
    }
    s += ")\n";

    return s;
  }
}

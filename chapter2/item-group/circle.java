//*************************************************************************
/**
 * Circle.java - A class describing a circle
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing a circle.  It is a subclass of Item.
 * This and the Point classes demonstrate a composition (unidirectional).
 * The Point class is used as a servant class.
 */
public class Circle extends Item {

  /** the radius of the circle */
  private double radius;

  /** the center of the circle */
  private Point center;

  /** 
   * Constructs a circle object.
   *
   * @param points		the array of points
   * @exception Exception	if the radius is negative
   */
  public Circle(double radius, Point center) throws Exception {
    if (radius < 0.0) throw new Exception("Radius must not be negative!");
    this.radius = radius; 
    this.center = center; 
  }

  /** 
   * Returns the radius of this circle.
   *
   * @return the radius of this circle
   */
  public double getRadius() { 
    return radius;
  }

  /** 
   * Returns the center of this circle.
   *
   * @return the center of this circle
   */
  public Point getCenter() { 
    return center;
  }

  /** 
   * Moves the circle by (dx, dy).
   *
   * @param dx	shift in the x coordinate of the circle
   * @param dy	shift in the y coordinate of the circle
   */
  public void move(double dx, double dy) {
    center.move(dx, dy);
  }

  /** 
   * Returns the information of this circle as a string.
   *
   * @return the information of this circle
   */
  public String toString() { 
    String s = "Circle: radius = " + getRadius()
    		+ "; center at" + center.toString() + "\n";
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

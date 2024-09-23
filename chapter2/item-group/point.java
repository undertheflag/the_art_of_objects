//*************************************************************************
/**
 * Point.java - A class describing a point
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing a point.
 */
public class Point {

  double x, y;  // coordinates of this point

  /** 
   * Constructs a point object.
   *
   * @param x	x coordinate of the point
   * @param y	y coordinate of the point
   */
  public Point(double x, double y) {
    this.x = x; 
    this.y = y; 
  }

  /** 
   * Returns the x coordinate of this point.
   *
   * @return the x coordinate of this point
   */
  public double getX() { 
    return x; 
  }

  /** 
   * Returns the y coordinate of this point.
   *
   * @return the y coordinate of this point
   */
  public double getY() { 
    return y; 
  }

  /** 
   * Moves the point by (dx, dy).
   *
   * @param dx	shift in the x coordinate of the point
   * @param dy	shift in the y coordinate of the point
   */
  public void move(double dx, double dy) {
    this.x += dx; 
    this.y += dy; 
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    String s = "";
    s += "(" + getX() + ", " + getY() + ")";
    return s;
  }
}

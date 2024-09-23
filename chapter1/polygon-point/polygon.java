//*************************************************************************
/**
 * Polygon.java - A class describing a polygon
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing a polygon.
 * This and the Point classes demonstrate an aggregation (ordered and
 * unidirectional).  The Point class is used as a servant class.
 */
public class Polygon {

  /** the array of points (3 or more) */
  private Point[] points;

  /** 
   * Constructs a polygon object.
   *
   * @param points		the array of points
   * @exception Exception	if the number of points is less than three.
   */
  public Polygon(Point[] points) throws Exception {
    if (points.length < 3) throw new Exception("Invalid number of points!");
    this.points = points; 
  }

  /** 
   * Returns the points of this polygon.
   *
   * @return the points of this polygon as an array
   */
  public Point[] getPoints() { 
    return points;
  }

  /** 
   * Returns the information of this polygon as a string.
   *
   * @return the information of this polygon
   */
  public String toString() { 
    String s = "Polygon with " + points.length + " points:";
    s += "\n";

    // We can directly access "points" but we do this in
    // order to test all operations.
    Point[] ps = getPoints();
    for (int i=0; i<ps.length; i++) {
      s += "  " + ps[i].toString();
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

    Point p1 = new Point(0.0, 0.0);
    Point p2 = new Point(1.0, 0.0);
    Point p3 = new Point(0.8, 0.2);
    Point p4 = new Point(0.4, 0.5);
    Point p5 = new Point(0.2, 0.1);

    Point[] ps = { p1, p2, p3, p4, p5 };

    Polygon pl = new Polygon(ps);

    s = pl.toString();
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

    Point p1 = new Point(0.0, 0.0);
    Point p2 = new Point(1.0, 0.0);
    Point[] ps = { p1, p2 };

    // This will result in an exception.
    show(">>> Expected exception ...");
    try {
      Polygon pl = new Polygon(ps);
    } catch (Exception e) {
      show(e.getMessage());
    }

  }

}

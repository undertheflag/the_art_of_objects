//*************************************************************************
/**
 * GeometryItem.java - A super class for geometric items
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This is a super class for geometric items.  It implements the move
 * operation, which is defined by the Movable interface.
 */
public class GeometryItem implements Movable {

  /** the array of points */
  private Point[] points;

  /** 
   * Constructs a geometry item object.
   *
   * @param points		the array of points
   */
  public GeometryItem(Point[] points) {
    this.points = points; 
  }

  /** 
   * Returns the points of this geometry item.
   *
   * @return the points of this geometry item as an array
   */
  public Point[] getPoints() { 
    return points;
  }

  /** 
   * Moves the item by (dx, dy).  This operation is a place holder
   * for the actual ones in the subclasses.
   *
   * @param dx	shift in the x coordinate
   * @param dy	shift in the y coordinate
   */
  public void move(double dx, double dy) {
    for (int i=0; i<points.length; i++) {
      points[i].move(dx, dy);
    }
  }


  /** 
   * Returns the information of this geometry item as a string.
   *
   * @return the information of this geometry item
   */
  public String toString() { 
    String s = "GeometryItem with " + points.length + " points:";
    s += "\n";
    for (int i=0; i<points.length; i++) {
      s += "  " + points[i].toString();
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

    Movable mo = new GeometryItem(ps);

    s = mo.toString();
    s += "\n";

    mo.move(0.1, -0.1);

    s += "\n... after move\n";
    s += mo.toString();
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

  }

}


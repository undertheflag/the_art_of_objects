//*************************************************************************
/**
 * Group.java - a class containing a group of geometric items.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class contains a group of geometric items.  It itself is a 
 * subclass of Item, thus forming a relationship loop with backward 
 * containment and leaf nodes (Circle).
 * <P>
 * You are encouraged to add the Square class as another leaf node class.
 */
public class Group extends Item {

  /** Use a vector to contain the items.
   */
  private Vector items = new Vector();

  /**
   * Constructs this group.
   *
   * @param item	the first item in the group
   */
  public Group(Item item) {
    items.add(item);
  }

  /**
   * Returns an array of items.  We extract each element in
   * the Vector to construct the returned array.
   *
   * @return  an array of items
   */
  public Item[] getAll() {
    Object[] o = items.toArray();
    Item[] u = new Item[o.length];
    for (int i=0; i<o.length; i++) u[i] = (Item)o[i];
    return u;
  }

  /**
   * Returns the number of items in the group.
   *
   * @return 	the number of items
   */
  public int getCount() {
    return items.size();
  }

  /**
   * Returns whether the input item is in the group.
   *
   * @param item 	an item
   * @return true if the input item is found.  False otherwise.
   */
  public boolean contains(Item item) {
    return items.contains(item);
  }

  /**
   * Adds a new Item.
   *
   * @param item 	an item
   */
  public void add(Item item) {
    items.addElement(item);
  }

  /**
   * Removes the input item from the group.
   *
   * @param item	the item to be removed
   * @exception Exception if the item is not found
   */
  public void remove(Item item) throws Exception {
    if (! items.removeElement(item) )
      throw new Exception("Item not found: " + item.toString());
  }

  /**
   * Removes all items from the group.
   *
   */
  public void removeAll( ) {
    items.removeAllElements();
  }

  /** 
   * Moves all items in the group by (dx, dy).
   * <P>
   * This is an recursive operation.
   *
   * @param dx	shift in the x coordinate
   * @param dy	shift in the y coordinate
   */
  public void move(double dx, double dy) {
    Enumeration e = items.elements();
    while (e.hasMoreElements()) {
      Item o = (Item) e.nextElement();
      o.move(dx, dy);
    }
  }

  /** 
   * Returns the information of all items as a string.
   * <P>
   * This is an recursive operation.
   *
   * @return the information of all items as a string
   */
  public String toString() { 
    String s = "Group:\n";
    Enumeration e = items.elements();
    while (e.hasMoreElements()) {
      Item o = (Item) e.nextElement();
      s += o.toString();
    }
    return s;
  }


  /** 
   * Shows the input string.
   *
   * @param s 	the string to be shown
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

    Point p1 = new Point(0.4, 0.5);
    Point p2 = new Point(0.6, 0.7);
    Point p3 = new Point(0.8, 0.9);

    Circle c1 = new Circle(1.1, p1);
    Circle c2 = new Circle(1.1, p2);
    Circle c3 = new Circle(1.1, p3);

    Group g1 = new Group(c1);
    Group g2 = new Group(c3);

    g1.add(c2);
    g1.add(g2);

    s = g1.toString();
    s += "\n";

    g1.move(0.1, -0.1);

    s += "... After move:\n";
    s += g1.toString();
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

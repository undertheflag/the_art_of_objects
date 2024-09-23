//*************************************************************************
/**
 * Item.java - A super class for geometric items
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This is a super class for geometric items.  It defines a move
 * operation, which is basically an abstract method.
 */
public class Item {

  /** 
   * Constructs an item object.
   *
   */
  public Item() {

  }

  /** 
   * Moves the item by (dx, dy).  This operation is a place holder
   * for the actual ones in the subclasses.
   *
   * @param dx	shift in the x coordinate
   * @param dy	shift in the y coordinate
   */
  public void move(double dx, double dy) {

  }

}


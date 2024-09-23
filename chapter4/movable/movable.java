//*************************************************************************
/**
 * Movable.java - An interface for movable things on a 2D plane
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This interface defines a move operation on a 2D plane.
 * 
 */
public interface Movable {

  /** 
   * Moves the item by (dx, dy).  This operation is a place holder
   * for the actual ones in the subclasses.
   *
   * @param dx	shift in the x coordinate
   * @param dy	shift in the y coordinate
   */
  public void move(double dx, double dy);

}


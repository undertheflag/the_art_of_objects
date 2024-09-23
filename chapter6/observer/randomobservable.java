//*************************************************************************
/**
 * RandomObservable.java - A observable class that generates random changes.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.*;

/**
 * This class fires events periodically.  It inherits from the swing 
 * Observable class.  This class and ObserverImpl demonstrate the 
 * basic observer pattern.
 */
public class RandomObservable extends Observable {

  private final int mMaxDelay;

  /** 
   * Constructs a RandomObservable object.
   *
   * @param maxDelay	the maximum delay (seconds) between changes
   * @param listener	the listener to this timer
   */
  public RandomObservable(int maxDelay) {
    mMaxDelay = maxDelay; 

    // an unnamed inner class instance to generate 
    // random changes
    Thread t = new Thread() {

      public void run() {
	int dt;
	Random r = new Random();
	while (true) {
	  dt = (r.nextInt(mMaxDelay) + 1 );
	  System.out.println("... random observable sleeping for " + dt + " secs");
	  dt *= 1000;
	  try {
  	    Thread.sleep(dt);
	  } catch (Exception e) {}
	  setChanged();  // indicate a change
	  notifyObservers(new java.util.Date());
	}
      }

    };

    t.start();

  }

  /** 
   * Invokes the super class' method.  Needed since inner anonymous
   * class instance cannot reach protected methods.
   */
  public void setChanged() { 
    super.setChanged();
  }

  /** 
   * Returns the information of this object as a string.
   * This is a place holder for subclasses.
   *
   * @return the information of this object
   */
  public String toString() { 
    return getClass().toString();
  }
  
}

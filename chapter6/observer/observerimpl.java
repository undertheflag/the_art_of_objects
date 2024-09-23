//*************************************************************************
/**
 * ObserverImpl.java - An implementation for the ListnerIF interface.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Observable;

/**
 * This implements the observer interface of java.util by simply showing
 * the updates from an Observable.  It counts five updates and 
 * then stops.  This class and RandomObservable demonstrate the 
 * basic observer pattern.
 */
public class ObserverImpl implements java.util.Observer {

  private String name;  
  private int count = 0;  // counter for events

  /** 
   * Constructs a ObserverImpl object.
   *
   * @param name	name of this object
   */
  public ObserverImpl(String name) {
    this.name = name;
  }

  /** 
   * Processes the event sent to this Observer.
   * Stops after five counts.
   *
   * @param o	the observable object
   * @param arg  an argument passed to the notifyObservers method 
   * 		of the observable
   */
  public void update(Observable o, Object arg) {
    count++;
    show( name + " - " );
    show("  got change No. " + count + " at " + arg.toString());
    if (count >= 5) System.exit(0);
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
    String s = "";

    RandomObservable ro = new RandomObservable(3);

    ObserverImpl o1 = new ObserverImpl("Observer A");
    ObserverImpl o2 = new ObserverImpl("Observer B");

    ro.addObserver(o1);
    ro.addObserver(o2);

    return s;
  }

  /**
   * Main method for testing.
   * 
   * @exception Exception if any exception is thrown
   */
  public static void main(String argv[]) throws Exception {
    show(test());

    // an infinite loop to wait for timer events
    while(true) {
    }

  }

}

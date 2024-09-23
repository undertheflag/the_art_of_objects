//*************************************************************************
/**
 * ListenerImpl.java - An implementation for the ListenerIF interface.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This implements the listener interface by simply showing the message
 * from a timer.  It counts five messages from the timer and then
 * stops.
 */
public class ListenerImpl implements ListenerIF {

  private int count = 0;  // counter for events

  /** 
   * Constructs a ListenerImpl object.
   *
   * @param delay	the delay (seconds) between event firing
   */
  public ListenerImpl(int delay) {
    (new Timer(delay, this)).start();
  }

  /** 
   * Processes the event sent to this Listener.
   * Stops after five counts.
   */
  public void processEvent(String message) {
    show(message);
    count++;
    if (count > 5) System.exit(0);
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

    ListenerImpl l = new ListenerImpl(1);

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

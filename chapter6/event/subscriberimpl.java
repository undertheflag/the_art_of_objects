//*************************************************************************
/**
 * SubscriberImpl.java - An implementation for the SubscriberIF interface.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This implements the SubscriberIF interface by displaying the messages
 * sent from an EventHolder.   It demonstrates a subscription and 
 * notification service (asynchronous).
 */
public class SubscriberImpl implements SubscriberIF {

  private String name;  // name of this Subscriber

  /** 
   * Constructs a SubscriberImpl object.
   *
   * @param name	name of this Subscriber
   */
  public SubscriberImpl(String name) {
    this.name = name;
  }

  /** 
   * Returns the name of this subscriber.
   *
   * @return the name of this subscriber
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Processes the event sent to this subscriber.
   *
   * @param data  the data for the event
   */
  public void processEvent(EventData data) {
    show("Display to " + name + " - " + data.toString());
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

    SubscriberImpl subscriber1 = new SubscriberImpl("John");

    // need to get the object reference of EventManager
    // and then subscribe to it.

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

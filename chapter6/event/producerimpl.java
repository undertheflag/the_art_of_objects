//*************************************************************************
/**
 * ProducerImpl.java - An event producer that generates events
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Vector;

/**
 * An event producer that generates events.  It implement the ProducerIF
 * interface.  
 * <P>
 * Once created, it periodically notifies all subscribers
 * that have new events pending.
 * Note that we did not inherit from Thread but choose to use an
 * unnamed inner class (Thread) in the constructor to do the job.
 *
 * <P>
 * This class is also Serializable.
 */
public class ProducerImpl 
  implements ProducerIF, java.io.Serializable {

  /**
   * @serial name of this ProducerImpl
   */
  private String name; 

  /**
   * @serial list of eventHolders
   */
  private Vector eventHolders = new Vector();

  /**
   * @serial interval between notifications in milli secs
   */
  private final int interval = 2000;  


  /** 
   * Constructs a ProducerImpl object.  It also kicks off the periodic
   * notification service.
   *
   * @param name	name of this ProducerImpl
   */
  public ProducerImpl(String name) {
    this.name = name;

    // an unnamed inner class instance to invoke
    // notifySubscribers periodically.
    Thread t = new Thread() {

      /** 
       * Invokes notifySubscribers periodically.
       */
       public void run() {

	while (true) {
	  // wait a little
	  try {
	    Thread.sleep(interval);
	    show("... notifying subscribers");
	    notifySubscribers();
	  } catch (Exception e) {
	    // do nothing, just continue
	  }
	}
      }
    };

    t.start();
  }

  /** 
   * Returns the name of this user.
   *
   * @return the name of this user
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Subscribe to this producer.
   *
   * @param subscriber	subscriber to this producer
   * @exception EventError	if the subscriber is already there
   */
  public void subscribe(SubscriberIF subscriber) throws EventError {
    EventHolder eh = getEventHolder(subscriber);
    if (eh != null) throw new EventError("Subscriber already exists: " + subscriber.getName());
    eh = new EventHolder(subscriber);
    eventHolders.addElement(eh);
  }

  /** 
   * Unsubscribe from this producer.
   *
   * @param subscriber	subscriber of the producer
   * @exception EventError	if the subscriber is not found
   */
  public void unsubscribe(SubscriberIF subscriber) throws EventError {
    EventHolder eh = getEventHolder(subscriber);
    if (eh == null) throw new EventError("Subscriber not found: " + subscriber.getName());
    eventHolders.removeElement(eh);
  }

  /** 
   * Returns the event holder for this subscriber.
   *
   * @param subscriber	subscriber of the producer
   * @return the subscriber of this producer.  Null if none found.
   */
  public EventHolder getEventHolder(SubscriberIF subscriber) {
    java.util.Enumeration e = eventHolders.elements();
    while (e.hasMoreElements()) {
      EventHolder eh = (EventHolder) e.nextElement();
      SubscriberIF sub = eh.getSubscriber();
      if ( sub == subscriber || 
        sub.getName().equals(subscriber.getName()) ) return eh;
    }
    return null;
  }

  /** 
   * Processes the event data by adding them to the event holders.
   *
   * @param data  	the data for the event
   */
  public void processEvent(EventData data) {
    java.util.Enumeration e = eventHolders.elements();
    while (e.hasMoreElements()) {
      EventHolder eh = (EventHolder) e.nextElement();	
      // we must create a new object for each holder since they
      // may have different status.
      Event ev = new Event(data);
      eh.addEvent(ev);
    }
  }

  /** 
   * Sends the event data in event holders to all subscribers.
   * This is periodically called by a separate thread.
   */
  public void notifySubscribers( ) {
    java.util.Enumeration e = eventHolders.elements();

    while (e.hasMoreElements()) {
      EventHolder eh = (EventHolder) e.nextElement();	
      SubscriberIF sub = eh.getSubscriber();

      try {
        // send all new events
        Event[] es = eh.getEvents(Event.NEW);
        for (int i=0; i<es.length; i++) {
	  sub.processEvent(es[i].getData());  // this may go across network
	  es[i].setStatus(Event.SENT);  // set the flag if sent
        }
      } catch (Exception e1) {
	// do nothing, just continue with next holder
      }
    }
  }


  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    String s = "Producer: " + name + "\n";
    s += "  Subscribers: ";
    java.util.Enumeration e = eventHolders.elements();
    while (e.hasMoreElements()) {
      EventHolder c = (EventHolder) e.nextElement();
      s += " " + c.getSubscriber().getName();
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
   * A test that returns an event producer.  Note it is a static method.
   *
   * @return the event producer
   * @exception Exception if any exception is thrown
   */
  public static ProducerIF test() throws Exception {
    String s = "";

    ProducerIF prod = new ProducerImpl("Sports");

    SubscriberIF user1 = new SubscriberImpl("John");
    SubscriberIF user2 = new SubscriberImpl("Mary");

    prod.subscribe(user1);
    prod.subscribe(user2);

    s += "\n";
    s += prod.toString();

    return prod;
  }

  /**
   * Main method for testing.
   * 
   * @exception Exception if any exception is thrown
   */
  public static void main(String argv[]) throws Exception {
    ProducerIF prod = test();

    EventData ed = new EventData("Good Morning!", "Tony");
    // send the event to the event producer
    prod.processEvent(ed);

    SubscriberIF user3 = new SubscriberImpl("Jane");
    prod.subscribe(user3); 

    try {
      Thread.sleep(2 * 1000);  // wait a little
    } catch (Exception e) {}

    ed = new EventData("How are you today?", "Tony");
    // send the event to the event producer
    prod.processEvent(ed);

    try {
      Thread.sleep(8 * 1000);  // wait a little before exiting
    } catch (Exception e) {}


    System.exit(0);
  }

}

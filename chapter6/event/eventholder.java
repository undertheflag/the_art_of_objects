//*************************************************************************
/**
 * EventHolder.java - An event holder class 
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * A class containing a set of events to be sent to a subscriber.
 * It is part of a subscription and notification service (asynchronous).
 * <P>
 * This class is also Serializable.
 * 
 */
public class EventHolder implements java.io.Serializable {

  /**
   * @serial subscriber for this EventHolder
   */
  private SubscriberIF subscriber;

  /**
   * @serial events in this EventHolder
   */
  private PVector events = new PVector();


  /** 
   * Constructs a EventHolder object.
   *
   * @param subscriber	subscriber of the EventHolder
   */
  public EventHolder(SubscriberIF subscriber) {
    this.subscriber = subscriber; 
  }

  /** 
   * Returns the subscriber of this EventHolder.
   *
   * @return the subscriber of this EventHolder
   */
  public SubscriberIF getSubscriber() { 
    return subscriber; 
  }

  /******** Events *********/

  /** 
   * Returns the events of this EventHolder.
   *
   * @return the events of this EventHolder as an array
   */
  public Event[] getEvents() {
    Object[] o = events.toArray();
    Event[] r = new Event[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Event)o[i];
    return r;
  }

  /**
   * Returns the number of event objects in the extent.
   *
   * @return 	the number of event objects
   */
  public int getEventCount() {
    return events.size();
  }

  /**
   * Returns the event objects with the status.
   *
   * @param status 	status of event
   * @return an array of Event objects.  Zero length array if none matched.
   */
  public Event[] getEvents(char status) {
    Vector ev = new Vector();
    Enumeration e = events.elements();
    while (e.hasMoreElements()) {
      Event c = (Event) e.nextElement();
      if (c.getStatus() == status) ev.addElement(c);
    }
    return (Event[]) ev.toArray(new Event[0]);
  }

  /**
   * Returns whether the input event object exists.
   *
   * @param event 	a event object
   * @return true if the input event object is found.  False otherwise.
   */
  public boolean contains(Event event) {
    return events.contains(event);
  }

  /**
   * Adds a new event.
   *
   * @param event 	a event object
   */
  public void addEvent(Event event) {
    events.addElement(event);
  }

  /**
   * Removes the input event.
   *
   * @param event  The event to be removed.
   * @exception Exception if the event is not found
   */
  public void removeEvent(Event event) throws Exception {
    if (!events.contains(event))
      throw new Exception("Event not found: " + event);
    events.removeElement(event);
  }

  /**
   * Removes all event objects.
   */
  public void removeAllEvents( ) {
    events.removeAllElements();
  }

  /** 
   * Returns the information of this EventHolder as a string.
   *
   * @return the information of this EventHolder
   */
  public String toString() { 
    String s = "EventHolder for: " + subscriber.getName() + "\n";
    s += "  Events:\n";

    Event[] h = getEvents();
    for (int i=0; i<h.length; i++) {
      s += "  " + h[i].toString();
    }

    return s;
  }

}


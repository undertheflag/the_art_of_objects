//*************************************************************************
/**
 * Event.java - A class describing an event
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing an event.  It is contained by an EventHolder
 * for asynchronous distribution to subscribers.
 * <P>
 * This class is also Serializable.
 */
public class Event implements java.io.Serializable {

  /** 
   * @serial the status of this event
   */
  private char status = NEW;

  /** 
   * @serial the data of the event 
   */
  private EventData data;

  // some flags for status
  public static final char NEW = 'n';
  public static final char SENT = 's';

  /** 
   * Constructs an event object.
   *
   * @param data	data of the event
   */
  public Event(EventData data) {
    this.data = data; 
  }

  /** 
   * Returns the data of this event.
   *
   * @return the data of this event
   */
  public EventData getData() { 
    return data; 
  }

  /** 
   * Returns the status of this event.
   *
   * @return the status of this event
   */
  public char getStatus() { 
    return status; 
  }

  /** 
   * Sets the status of this event.
   *
   * @param status	the status of this event
   */
  public void setStatus(char status) { 
    this.status = status; 
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    return data.toString() + " - " + status;
  }
}

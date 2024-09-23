//*************************************************************************
/**
 * EventData.java - A class describing the data of an event
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing the data of an event.  It is contained by an 
 * EventHolder for asynchronous distribution to subscribers.
 * <P>
 * This class is also Serializable.
 */
public class EventData implements java.io.Serializable {

  /**
   * @serial the message 
   */
  public String message;

  /** 
   * @serial the person who sent this EventData
   */
  public String sender;

  /** 
   * Constructs an EventData object.
   *
   * @param message	message of the EventData
   * @param sender	the sender
   */
  public EventData(String message, String sender) {
    this.message = message; 
    this.sender = sender;
  }

  /** 
   * Returns the message of this EventData.
   *
   * @return the message of this EventData
   */
  public String getMessage() { 
    return message; 
  }

  /** 
   * Returns the sender of this EventData.
   *
   * @return the sender of this EventData
   */
  public String getSender() { 
    return sender; 
  }

  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object
   */
  public String toString() { 
    String s = message;
    s += " (from " + sender + ")";
    return s;
  }

}
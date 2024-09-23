//*************************************************************************
/**
 * SubscriberIF.java - An interface for a Subscriber.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This interface defines the operations for a Subscriber.
 */
public interface SubscriberIF {
 
  /** 
   * Returns the name of this subscriber.
   */
  public String getName();

  /** 
   * Processes the event sent to this subscriber.
   *
   * @param data  the data for the event
   */
  public void processEvent(EventData data);

}

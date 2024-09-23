//*************************************************************************
/**
 * ProducerIF.java - An interface for an event Producer.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This interface defines the operations for an event Producer.
 */
public interface ProducerIF {
 
  /** 
   * Subscribe to this producer.
   *
   * @param subscriber	subscriber to this producer
   * @exception 	if the subscriber is already there
   */
  public void subscribe(SubscriberIF subscriber) throws EventError;

  /** 
   * Unsubscribe from this producer.
   *
   * @param subscriber	subscriber of the producer
   * @exception 	if the subscriber is not found
   */
  public void unsubscribe(SubscriberIF subscriber) throws EventError;

  /** 
   * Processes the event data.
   *
   * @param data  	the data for the event
   */
  public void processEvent(EventData data);

}
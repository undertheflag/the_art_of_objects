//*************************************************************************
/**
 * ListenerIF.java - An interface for a Listener.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This interface defines the operations for a Listener.
 */
public interface ListenerIF {
 
  /** 
   * Processes the event sent to this Listener.
   *
   */
  public void processEvent(String message);

}
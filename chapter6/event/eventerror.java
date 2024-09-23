//*************************************************************************
/**
 * EventError.java - An exception for event service.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * An exception for event service.
 */
public class EventError extends Exception {
 
  /** 
   * Constructs this object.
   *
   * @param message	message of this EventError
   */
  public EventError(String message) {
    super(message);
  }

}


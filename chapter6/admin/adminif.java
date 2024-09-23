//*************************************************************************
/**
 * AdminIF.java - An interface for an admin user.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This interface defines the operations for an admin user.
 * This is part of an inheritance ladder.
 */
public interface AdminIF extends UserIF {

  /** 
   * Sends a request to this admin user.
   *
   * @param content	content of the request
   * @exception Exception if any exception is thrown
   */
  public void sendRequest(String content)
    throws Exception;

}

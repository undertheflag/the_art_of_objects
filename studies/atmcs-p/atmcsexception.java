//*************************************************************************
/**
 * AtmcsException.java - an exception for the package.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package atmcs;

public class AtmcsException extends Exception
{
  /**
   * Constructor
   *
   * @param s  The exception message
   */
   public AtmcsException(String s) {
     super(s);
   }
}

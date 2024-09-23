//*************************************************************************
/**
 * UserIF.java - An interface for a user.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This interface defines the operations for a user.
 * This is part of an inheritance ladder.
 * 
 */
public interface UserIF {

  /** 
   * Returns the name of this user.
   *
   * @return the name of this user
   */
  public String getName();

  /** 
   * Returns the username of this user.
   *
   * @return the username of this user
   */
  public String getUsername();

  /** 
   * Changes the password of this user.
   *
   * @param oldPasswd		the old password of this user
   * @param newPasswd		the new password of this user
   * @exception Exception if the old password fails validation
   */
  public void changePassword(String oldPasswd, String newPasswd)
    throws Exception;

  /** 
   * Validate the password for this user.
   *
   * @param password		the password to be validated
   * @exception Exception if the validation fails
   */
  public void validatePassword(String password) throws Exception;

}

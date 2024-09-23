//*************************************************************************
/**
 * UserImpl.java - An implementation for the user interface
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class implements the user interface.
 * It is part of an inheritance ladder.
 */
public class UserImpl implements UserIF {

  // Private attributes in the UserImpl class
  private String name;
  private String username;
  private String password;

  /** 
   * Constructs a user object.
   *
   * @param name	name of the person
   * @param username	username of the user
   * @param password	password of the user
   */
  public UserImpl(String name, String username, String password) {
    this.name = name; 
    this.username = username; 
    this.password = password;
  }

  /** 
   * Returns the name of this user.
   *
   * @return the name of this user
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Returns the username of this user.
   *
   * @return the username of this user
   */
  public String getUsername() { 
    return username; 
  }

  /** 
   * Changes the password of this user.  If mainframe user is
   * set, relay the call to that object.
   *
   * @param oldPasswd		the old password of this user
   * @param newPasswd		the new password of this user
   * @exception Exception if the old password fails validation
   */
  public void changePassword(String oldPasswd, String newPasswd)
  throws Exception { 
    validatePassword(oldPasswd);
    password = newPasswd;
  }

  /** 
   * Validate the password for this user.  If mainframe user is
   * set, relay the call to that object's login method.
   *
   * @param password		the password to be validated
   * @exception Exception if the validation fails
   */
  public void validatePassword(String password) throws Exception { 
    if (! this.password.equals(password)) {
      throw new Exception("Password validation failed!");
    }
  }

  /** 
   * Returns the information of this user as a string.
   * Information from the superclass Person is also included.
   *
   * @return the information of this user
   */
  public String toString() { 
    String s = "";
    s += "Username: " + username + "\n";
    return s;
  }

}


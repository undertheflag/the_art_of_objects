//*************************************************************************
/**
 * User.java - A subclass of Role.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class is a subclass of Role.
 * 
 */
public class User extends Role {

  // Private attributes in the User class
  private String username;
  private String password;

  /** 
   * Constructs a user object.
   *
   * @param username	username of the user
   * @param password	password of the user
   */
  public User(String username, String password) {
    this.username = username; 
    this.password = password;
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
   * Returns the password of this user.
   *
   * @return the password of this user
   */
  public String getPassword() {
    return password; 
  }

  /** 
   * Changes the password of this user.
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
   * Validate the password for this user.
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
   * Information from the superclass is also included.
   *
   * @return the information of this user
   */
  public String toString() { 
    // call the operation in the superclass
    String s = super.toString();
    s += " - User: " + username + "\n";
    return s;
  }

}


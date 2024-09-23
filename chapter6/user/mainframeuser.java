//*************************************************************************
/**
 * MainframeUser.java - A class for a mainframe user.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class is for a mainframe user.
 */
public class MainframeUser {

  // Private attributes
  private String name;
  private String username;
  private String password;
  private String homeDirectory;

  /** 
   * Constructs a mainframe user object.
   *
   * @param name	name of the person
   * @param username	username of the user
   * @param password	password of the user
   * @param homeDir	home directory of the user
   */
  public MainframeUser(String name, String username,
  	String password, String homeDir) {
    this.name = name; 
    this.username = username; 
    this.password = password;
    this.homeDirectory = homeDir;
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
   * Returns the password of this user.
   *
   * @return the password of this user
   */
  public String getPassword() {
    return password; 
  }

  /** 
   * Returns the home directory of this user.
   *
   * @return the home directory of this user
   */
  public String getHomeDirectory() { 
    return homeDirectory; 
  }

  /** 
   * Changes the password of this user.
   *
   * @param oldPasswd		the old password of this user
   * @param newPasswd		the new password of this user
   * @exception Exception if the old password fails validation
   */
  public void updatePasswd(String oldPasswd, String newPasswd)
  throws Exception { 
    login(oldPasswd);
    password = newPasswd;
  }

  /** 
   * Login to the system with the password.
   *
   * @param password	the password 
   * @exception Exception if the login fails
   */
  public void login(String password) throws Exception { 
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
    s += "Mainframe user: " + username + "\n";
    return s;
  }

}


//*************************************************************************
/**
 * UserImpl.java - A class for a user.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class demonstrates the basic usage of an object adapter.
 * It simply relays the operations of the UserIF interface to
 * the MainframeUser class.
 */
public class UserImpl implements UserIF {

  // Private attributes in the UserImpl class
  private String name;
  private String username;
  private String password;
  private MainframeUser mfu;

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
   * Sets the mainframe user object.
   *
   * @param mfu the mainframe user object
   */
  public void setMainframeUser(MainframeUser mfu) {
    this.mfu = mfu;
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
    if (mfu != null) {
      mfu.updatePasswd(oldPasswd, newPasswd);
      return;
    }
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
    if (mfu != null) {
      mfu.login(password);
      return;
    }
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



  /** 
   * Shows the input string.
   *
   * @s the string to be shown
   */
  public static void show(String s) { 
    System.out.println(s);
  }


  /**
   * A test that returns a string.  Note it is a static method.
   *
   * @return result of the test as a string
   * @exception Exception if any exception is thrown
   */
  public static String test() throws Exception {
    String s = "";

    MainframeUser mfu = 
      new MainframeUser("John", "u-john", "abcd", "[u-john]");
    UserIF u1 = new UserImpl("John", "u-john", "abcd");

    ((UserImpl)u1).setMainframeUser(mfu);

    s += mfu.toString();

    u1.changePassword("abcd", "1234");
    s += "... password changed for " + mfu.getUsername();

    return s;
  }

  /**
   * Main method for testing.
   * 
   * @exception Exception if any exception is thrown
   */
  public static void main(String argv[]) throws Exception {
    show(test());
  }

}


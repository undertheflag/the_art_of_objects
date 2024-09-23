//*************************************************************************
/**
 * AdminImpl.java - An implementation for the admin interface.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class implements the admin interface.
 * This is part of an inheritance ladder.
 */
public class AdminImpl extends UserImpl implements AdminIF {

  /** 
   * Constructs a user object.
   *
   * @param name	name of the person
   * @param username	username of the user
   * @param password	password of the user
   */
  public AdminImpl(String name, String username, String password) {
    super(name, username, password);
  }

  /** 
   * Sends a request to this admin user.
   *
   * @param content	content of the request
   * @exception Exception if any exception is thrown
   */
  public void sendRequest(String content) throws Exception {
    show(content);
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

    UserIF u1 = new UserImpl("John", "u-john", "abcd");
    AdminIF a1 = new AdminImpl("Mary", "u-mary", "1234");

    String s1 = "Please unlock the account for " + u1.getUsername();
    a1.sendRequest(s1);

    u1.changePassword("abcd", "ABCD");
    s += "Password changed for " + u1.getUsername();
    
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

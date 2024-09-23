//*************************************************************************
/**
 * UserManager.java - a class that manages a collection of users.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class manages a collection of users.
 * There is no backward link from User to UserManager.  You may
 * add such a link as an exercise.
 */
public class UserManager {

  /** A vector of users.  We take a short cut here to use the Java Vector
   *  class so that we do not need to handle array expansion. 
   */
  private Vector users = new Vector();

  /**
   * Constructs this object.
   *
   */
  public UserManager() {

  }

  /**
   * Returns an array of user objects.  We extract each element in
   * the Vector to construct the returned array.
   *
   * @return  an array of user objects
   */
  public User[] getUsers() {
    Object[] o = users.toArray();
    User[] u = new User[o.length];
    for (int i=0; i<o.length; i++) u[i] = (User)o[i];
    return u;
  }

  /**
   * Returns the number of user objects.
   *
   * @return 	the number of user objects
   */
  public int getUserCount() {
    return users.size();
  }

  /**
   * Returns the user object for the input username.
   *
   * @param username 	username for the returned user
   * @return User object.  Null if none matched the username.
   */
  public User getUser(String username) {
    Enumeration e = users.elements();
    while (e.hasMoreElements()) {
      User u = (User) e.nextElement();
      if (u.getUsername().equals(username)) return u;
    }
    return null;
  }

  /**
   * Returns whether the input user object is in the collection.
   *
   * @param user 	a user object
   * @return true if the input user object is found.  False otherwise.
   */
  public boolean contains(User user) {
    return users.contains(user);
  }

  /**
   * Adds a new User.  The input parameters are the same as in
   * User's constructor.
   *
   * @param name	name of the person
   * @param age		age of the person
   * @param username	username of the user
   * @param password	password of the user
   * @return the newly added User object
   */

  public User addUser(String name, int age, 
  		      String username, String password) {
    User u = new User(name, age, username, password);
    users.addElement(u);
    return u;
  }

  /**
   * Removes the named user from the collection.
   *
   * @param username	username of the user
   * @exception Exception if the user is not found
   */
  public void removeUser(String username) throws Exception {
    /* get the User Object */
    User u = getUser(username);
    if (u == null) throw new Exception("User not found: " + username);

    /* Remove the User object from the vector */
    users.removeElement(u);
  }

  /**
   * Removes the input user object from the collection.
   *
   * @param user	the user object to be removed
   * @exception Exception if the user is not found
   */
  public void removeUser(User user) throws Exception {
    if (! users.removeElement(user) )
      throw new Exception("User object not found: " + user.getUsername());
  }

  /**
   * Removes all user objects from the collection.
   *
   */
  public void removeAllUsers( ) {
    users.removeAllElements();
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

    UserManager umgr = new UserManager();

    User u1 = umgr.addUser("John", 20, "u-john", "abcd");
    User u2 = umgr.addUser("Mary", 18, "u-mary", "4321");
    User u3 = umgr.addUser("Tom", 60, "u-tom", "T321");

    s += "There are " + umgr.getUserCount() + " users.\n";

    User[] us = umgr.getUsers();
    for (int i=0; i<us.length; i++) {
      s += us[i].toString();
      s += "\n";
    }

    // Make some changes on the user objects.
    // Note the details!

    u1.incrementAge();

    u1 = umgr.getUser("u-mary");
    u1.changeName("Mary Ann");
    u1.changePassword("4321", "5432");

    umgr.removeUser("u-tom");

    s += "... after the changes\n";

    // test the get method
    User[] u = umgr.getUsers();
    for (int i=0; i<u.length; i++) {
      s += u[i].toString();
      s += "\n";
    }

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

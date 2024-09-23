//*************************************************************************
/**
 * User.java - A subclass of Person.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class demonstrates the basic concepts of inheritance.
 * 
 */
public class User extends Person {

  // Private attributes in the User class
  private String username;
  private String password;

  /** 
   * Constructs a user object.
   *
   * @param name	name of the person
   * @param age		age of the person
   * @param username	username of the user
   * @param password	password of the user
   */
  public User(String name, int age, String username, String password) {
    // Invokes superclass' constructor.  This must be the first 
    // statement.
    super(name, age);
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
   * Information from the superclass Person is also included.
   *
   * @return the information of this user
   */
  public String toString() { 
    // call the operation in the superclass
    String s = super.toString();
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
    String s;

    User u1 = new User("John", 20, "u-john", "abcd");

    // We may use an object reference for Person to hold
    // a User, but not the other way around.
    Person u2 = new User("Mary", 18, "u-mary", "4321");

    // This won't compile though:
    // User u3 = new Person("Tom", 44);

    s = u1.toString();

    // This will use the toString() in User, instead of that in Person
    s += u2.toString();

    u1.incrementAge();

    u2.changeName("Mary Ann");

    // Need to cast u2 to User
    ((User)u2).changePassword("4321", "5432");

    s += "\n";
    s += "... after changes\n";

    // test the get methods
    s += u1.getName() + "\t";
    s += u1.getUsername() + "\t";
    s += u1.getAge() + "\n";

    s += u2.getName() + "\t";
    s += ((User)u2).getUsername() + "\t";
    s += u2.getAge() + "\n";

    return s;
  }

  /**
   * Main method for testing.
   * 
   * @exception Exception if any exception is thrown
   */
  public static void main(String argv[]) throws Exception {
    show(test());

    show(">>> Expected exception ...");
    try {
      User u1 = new User("John", 20, "u-john", "abcd");
      u1.validatePassword("abc");
    } catch (Exception e) {
      show(e.getMessage());
    }
  }

}


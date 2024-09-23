//*************************************************************************
/**
 * Role.java - A class describing a role
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * A class describing a role.
 * This is part of the handle-body pattern.
 */
public class Role {

  /** 
   * Constructs a role object.
   */
  public Role() {
  }

  /** 
   * Returns the type of this role.
   *
   * @return the type of this role as a string
   */
  public String getType() { 
    String type = "Role";
    if (this instanceof User) type = "User";
    else if (this instanceof Buyer) type = "Buyer";
    return type; 
  }

  /** 
   * Returns the information of this object as a string.
   * This is a place holder for subclasses.
   *
   * @return the information of this object
   */
  public String toString() { 
    return "Role";
  }
}

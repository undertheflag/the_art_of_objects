//*************************************************************************
/**
 * Context.java - A class describing a Context
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class is an association class between Tool and Part.
 * The candidate key requirement is enforced by the addPart operation 
 * in the Tool class.
 */
public class Context {

  // Links to Tool and Part
  private Tool tool;
  private Part part;

  // Attributes in the Context class
  private int quantity;
  private String description;

  /** 
   * Constructs a Context object.  It also sets backward links from
   * the tool object for referential integrity.
   *
   * @param tool	the tool that uses the part
   * @param part	the part
   * @param quantity	the quantity of the part
   * @param description	the description for the part used here
   */
  public Context(Tool tool, Part part, int quantity, String description) {
    this.tool = tool;
    this.part = part;
    this.quantity = quantity;
    this.description = description; 

    // set backward links for referential integrity
    tool.addContext(this);
  }

  /** 
   * Returns the tool of this Context.
   *
   * @return the tool of this Context
   */
  public Tool getTool() {
    return tool; 
  }

  /** 
   * Returns the part of this Context.
   *
   * @return the part of this Context
   */
  public Part getPart() {
    return part; 
  }

  /** 
   * Returns the description of this Context.
   *
   * @return the description of this Context
   */
  public String getDescription() { 
    return description; 
  }

  /** 
   * Returns the quantity of this Context.
   *
   * @return the quantity of this Context
   */
  public int getQuantity() {
    return quantity; 
  }

  /** 
   * Sets the quantity of this Context.
   *
   * @param quantity	the quantity of this Context
   */
  public void setQuantity(int quantity) { 
    this.quantity = quantity; 
  }

  /** 
   * Returns the information of this Context as a string.
   *
   * @return the information of this Context
   */
  public String toString() { 
    String s = "";
    s += "  Part: " + part.getName();
    s += "    - " + description + "\n";
    s += "    Quantity = " + quantity + "\n";
    return s;
  }

}


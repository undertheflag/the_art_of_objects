//*************************************************************************
/**
 * Tool.java - A class describing a tool
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * A class describing a tool.
 * <P>
 * This is part of the shared object pool pattern.  A tool object may
 * use multiple parts.
 */
public class Tool {

  // Private attributes in the Tool class
  private String name;

  /** A vector is used to contain the contexts. */
  private Vector contexts = new Vector();


  /** 
   * Constructs a tool object.
   *
   * @param name	name of the tool
   */
  public Tool(String name) {
    this.name = name; 
  }

  /** 
   * Returns the name of this tool.
   *
   * @return the name of this tool
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Returns the contexts of this tool.
   *
   * @return the contexts of this tool as an array
   */
  public Context[] getContexts() { 
    // forces the array elements to be of type Context
    return (Context[]) contexts.toArray(new Context[0]);
  }

  /** 
   * Add a new context for this tool.
   *
   * @param context	the context
   */
  public void addContext(Context context) {
    contexts.add(context);
  }

  /** 
   * Add a new part for this tool.
   * The candidate key requirement is enforced here by forbidding 
   * duplicate part objects.
   *
   * @param part	the part
   * @param quantity	the quantity of the part
   * @param description	the description for the part used here
   * @exception Exception if the input part is already used in this tool
   */
  public void addPart(Part part, int quantity, String description)
  	 throws Exception {
    if (contains(part)) throw 
      new Exception("The part " + part.getName() + " is already used in this tool!");
    Context context = new Context(this, part, quantity, description);
  }

  /**
   * Returns whether the input part is used in this tool.
   *
   * @param part 	the part 
   * @return true if the input part is used by this tool.  False otherwise.
   */
  public boolean contains(Part part) {
    Enumeration e = contexts.elements();
    while (e.hasMoreElements()) {
      Context c = (Context) e.nextElement();
      if (c.getPart() == part) return true;
    }
    return false;
  }

  /** 
   * Returns the information of this tool as a string.
   *
   * @return the information of this tool
   */
  public String toString() { 
    String s = "Tool: " + name + "\n";
    s += "Parts used:\n";

    Context[] t = getContexts();
    for (int i=0; i<t.length; i++) {
      s += "  " + t[i].toString();
    }

    return s;
  }

}


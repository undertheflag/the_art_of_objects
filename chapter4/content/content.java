//*************************************************************************
/**
 * Content.java - An abstract class describing an XML Content
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.io.*;

/**
 * This abstract class is the superclass of Element and Text.
 * It demonstrates the template method save(), which calls 
 * an abstract operation toString().
 *
 */
public abstract class Content {

  /** 
   * The parent element of this content.  It is null if this is
   * a top level element. 
   */
  private Element parentElement;

  /** 
   * Constructs a Content object.
   * 
   * @param parentElement 	parent element of this content
   */
  public Content(Element parentElement) {
    this.parentElement = parentElement; 
  }

  /** 
   * Returns the parent Element of this Content.
   *
   * @return the parent Element of this Content
   */
  public Element getParentElement() { 
    return parentElement; 
  }

  /** 
   * Sets the parent element of this Content.
   *
   * @param element 	the parent Element
   */
  public void setParentElement(Element element) {
    this.parentElement = element; 
  }

  /**
   * Saves the XML content to a file.  This is a template method 
   * that calls the abstract operation toString().
   *
   * @param filename	the filename for output
   * @exception if file cannot be opened or cannot be writen.
   */ 
  public void save(String filename) throws IOException {
    FileOutputStream out = new FileOutputStream(filename);
    String str = toString();  // call abstract operation
    byte[] buf = str.getBytes();
    out.write(buf);
    out.flush();
    out.close();
  }

  /** 
   * Returns the information of this Content as a string.
   * This is a place holder (abstract operation).
   *
   * @return the information of this Content as a string
   */
  public abstract String toString();

}


//*************************************************************************
/**
 * Text.java - A simple class with a text
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This is a simple class with a text.  It is also serializable.
 */
public class Text extends Content implements java.io.Serializable {

  /**
   * @serial text of this object
   */
  private String text;


  /** 
   * Constructs a text object.
   *
   * @param text	text of the object
   * @param parentElement 	parent element of this content
   */
  public Text(String text, Element parentElement) {
    super(parentElement);
    this.text = text; 
  }

  /** 
   * Returns the text of this object.
   *
   * @return the text of this object
   */
  public String getText() { 
    return text; 
  }

  /** 
   * Returns the text length of this object.
   *
   * @return the text length of this object
   */
  public int getLength() { 
    return text.length(); 
  }

  /** 
   * Sets the parent element of this Text.
   *
   * @param element 	the parent Element
   */
  public void setParentElement(Element element) {
    super.setParentElement(element); 
  }

  /** 
   * Sets the text of this object.
   *
   * @param text	the new text of this object
   */
  public void setText(String text) { 
    this.text = text; 
  }
 
  /** 
   * Returns the information of this object as a string.
   *
   * @return the information of this object as a string
   */
  public String toString() { 
    return text;
  }

}


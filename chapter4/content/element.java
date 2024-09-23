//*************************************************************************
/**
 * Element.java - A class describing an Element
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class is the subclass of Content.  It contains a set of
 * Content objects, which belong to the superclass of Element.  This 
 * demonstrates the backward relationship loop.  Element is 
 * also a concrete subclass of the abstract Content class.
 */
public class Element extends Content {

  /** Name of this element */
  private String name;

  /** Vector containing the contents  */
  private Vector contents = new Vector();

  /** 
   * Constructs an Element object.
   * This gaurantees referential integrity with a backward link.
   *
   * @param name	name of the Element
   * @param parentElement 	parent element of this content
   */
  public Element(String name, Element parentElement) {
    super(parentElement);
    this.name = name;
  }

  /** 
   * Returns the name of this Element.
   *
   * @return the name of this Element
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Sets the name of this Element.
   *
   * @param name	the name of this Element
   */
  public void setName(String name) { 
    this.name = name; 
  }

  /**
   * Returns the number of Contents in the element.
   *
   * @return 	the number of Contents
   */
  public int getContentCount() {
    return contents.size();
  }

  /**
   * Returns an array of Contents.  We extract each element in
   * the Vector to construct the returned array.
   *
   * @return  an array of Contents
   */
  public Content[] getContents() {
    Object[] o = contents.toArray();
    Content[] f = new Content[o.length];
    for (int i=0; i<o.length; i++) f[i] = (Content)o[i];
    return f;
  }

  /**
   * Returns the Element with the input name.
   *
   * @param name 	name for the Element
   * @return the Element.  Null if none matched the name.
   */
  public Element getElement(String name) {
    Enumeration e = contents.elements();
    while (e.hasMoreElements()) {
      Element o = (Element) e.nextElement();
      if (o.getName().equals(name)) return o;
    }
    return null;
  }

  /**
   * Returns whether the input elementItem is in the element.
   *
   * @param elementItem 	a Content object
   * @return true if the input elementItem object is found.  False otherwise.
   */
  public boolean contains(Content elementItem) {
    return contents.contains(elementItem);
  }

  /**
   * Adds an element.
   *
   * @param name 	name of the elment
   */
  public Element addElement(String name) {
    Element e = new Element(name, this);
    contents.addElement(e);
    return e;
  }

  /**
   * Adds a Text object.
   *
   * @param text 	text
   */
  public Text addText(String text) {
    Text e = new Text(text, this);
    contents.addElement(e);
    return e;
  }

  /**
   * Removes the input content object from the element.
   * This is a core operation for referential integrity.
   *
   * @param content	the content object to be removed
   * @exception Exception if the content is not found
   */
  public void removeContent(Content content) throws Exception {
    if (! contents.removeElement(content) )
      throw new Exception("Content not found!");
    // remove the backward link
    content.setParentElement(null);
  }

  /**
   * Removes all Content objects.
   */
  public void removeAll( ) {
    Object[] o = contents.toArray();
    // remove backward links
    for (int i=0; i<o.length; i++) {
      ((Content)o[i]).setParentElement(null);
    }
    contents.removeAllElements();
  }


  /** 
   * Returns the information of this element.
   * If recursive is true, it will recursively calls toString() for all
   * Contents in this element.
   * 
   * @param recursive	whether to recursively call toString for all
   *			Contents in this element
   * @param lead	the leading string
   * @return the information of this element as a string
   */
  public String toString(boolean recursive, String lead) { 

    String s = lead + "<" + name + ">\n";  // start tag

    if (!recursive) return s;

    for (int i=0; i<contents.size(); i++) {
      Content c = (Content)contents.elementAt(i);
      if (c instanceof Element) s += ((Element)c).toString(true, lead+lead);
      else s += lead + c.toString() + "\n";  // a Text
    }

    s += lead + "</" + name + ">\n";  // end tag
    return s;
  }

  /** 
   * Returns the information of this object as a string.
   * This implements the abstract method in Content.
   *
   * @return the information of this object as a string
   */
  public String toString() { 
    return toString(true, "  ");
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

    Element book = new Element("Book", null);
    Element title = book.addElement("Title");
    title.addText("The Art of Objects");

    Element preface = book.addElement("Preface");
    preface.addText("This book is about objects.");

    Element chap1 = book.addElement("Chapter_1");
    title = chap1.addElement("Title");
    title.addText("Introduction");
    chap1.addText("Objects are wonderful.");

    Element chap2 = book.addElement("Chapter_2");
    title = chap2.addElement("Title");
    title.addText("Basics");
    chap2.addText("Everything is object.");

    s = book.toString(true, "  ");
    s += "\n";

    s += "\n... saving to test.xml\n";
    book.save("test.xml");

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

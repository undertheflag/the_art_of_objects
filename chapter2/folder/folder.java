//*************************************************************************
/**
 * Folder.java - A class describing a Folder
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;
import java.util.Date;

/**
 * This class is the subclass of FolderItem.  It contains a set of
 * FolderItem's, which belong to the superclass of Folder.  This 
 * demonstrates the backward relationship loop.
 */
public class Folder extends FolderItem {

  /** Use a vector to contain the items.  */
  private Vector items = new Vector();

  /** 
   * Constructs a Folder object.
   *
   * @param name	name of the Folder
   * @param size	size of the Folder
   * @param date	date of the Folder
   */
  public Folder(String name, int size, Date date) {
    super(name, size, date);
  }

  /**
   * Returns the number of FolderItems in the folder.
   *
   * @return 	the number of FolderItems
   */
  public int getFolderItemCount() {
    return items.size();
  }

  /**
   * Returns an array of FolderItems.  We extract each element in
   * the Vector to construct the returned array.
   *
   * @return  an array of FolderItems
   */
  public FolderItem[] getFolderItems() {
    Object[] o = items.toArray();
    FolderItem[] f = new FolderItem[o.length];
    for (int i=0; i<o.length; i++) f[i] = (FolderItem)o[i];
    return f;
  }

  /**
   * Returns the FolderItem with the input name.
   *
   * @param name 	name for the FolderItem
   * @return the FolderItem.  Null if none matched the name.
   */
  public FolderItem getFolderItem(String name) {
    Enumeration e = items.elements();
    while (e.hasMoreElements()) {
      FolderItem o = (FolderItem) e.nextElement();
      if (o.getName().equals(name)) return o;
    }
    return null;
  }

  /**
   * Returns whether the input folderItem is in the folder.
   *
   * @param folderItem 	a FolderItem object
   * @return true if the input folderItem object is found.  False otherwise.
   */
  public boolean contains(FolderItem folderItem) {
    return items.contains(folderItem);
  }

  /**
   * Adds a new FolderItem.  
   * This is a core operation for referential integrity.
   *
   * @param folderItem 	a FolderItem object
   */
  public void addFolderItem(FolderItem folderItem) {
    items.addElement(folderItem);
    folderItem.setParentFolder(this);
  }

  /**
   * Removes the named FolderItem from the folder.
   *
   * @param name	name of the folderItem
   * @exception Exception if the FolderItem is not found
   */
  public void removeFolderItem(String name) throws Exception {
    /* get the FolderItem */
    FolderItem u = getFolderItem(name);
    if (u == null) throw new Exception("FolderItem not found: " + name);

    /* Remove the FolderItem object from the vector */
    removeFolderItem(u);
  }

  /**
   * Removes the input FolderItem object from the folder.
   * This is a core operation for referential integrity.
   *
   * @param folderItem	the folderItem object to be removed
   * @exception Exception if the folderItem is not found
   */
  public void removeFolderItem(FolderItem folderItem) throws Exception {
    if (! items.removeElement(folderItem) )
      throw new Exception("FolderItem not found: " + folderItem.getName());
    // remove the backward link
    folderItem.setParentFolder(null);
  }

  /**
   * Removes all FolderItem objects from the folder.
   */
  public void removeAll( ) {
    Object[] o = items.toArray();
    // remove backward links
    for (int i=0; i<o.length; i++) {
      ((FolderItem)o[i]).setParentFolder(null);
    }
    items.removeAllElements();
  }


  /** 
   * Returns the information of this folder.
   * If recursive is true, it will recursively calls toString() for all
   * FolderItems in this folder.
   * 
   * @param recursive	whether to recursively call toString for all
   *			FolderItems in this folder
   * @param lead	the leading string
   * @return the information of this folder as a string
   */
  public String toString(boolean recursive, String lead) { 
    String s = lead + "Folder: " + super.toString();
    if (!recursive) return s;

    FolderItem[] folderItems;

    // we use the get operation here just to test it
    folderItems = getFolderItems();
    for (int i=0; i<folderItems.length; i++) {
      FolderItem f = folderItems[i];
      if (f instanceof Folder) s += ((Folder)f).toString(true, lead+lead);
      else s += "\t" + f.toString();
    }

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

    Folder book = new Folder("Book", 120, new Date());
    Folder chap1 = new Folder("Chapter 1", 4420, new Date());
    Folder chap2 = new Folder("Chapter 2", 3011, new Date());
    Folder chap3 = new Folder("Chapter 3", 2420, new Date());

    FolderItem fig1 = new FolderItem("Figure 1", 180, new Date());
    FolderItem fig2 = new FolderItem("Figure 2", 280, new Date());
    FolderItem fig3 = new FolderItem("Figure 3", 380, new Date());
    FolderItem fig4 = new FolderItem("Figure 4", 480, new Date());

    book.addFolderItem(chap1);
    book.addFolderItem(chap2);
    book.addFolderItem(chap3);

    chap1.addFolderItem(fig1);
    chap2.addFolderItem(fig2);
    chap2.addFolderItem(fig3);
    chap3.addFolderItem(fig4);

    s = book.toString(true, "  ");
    s += "\n";

    chap2.removeFolderItem("Figure 3");
    chap3.removeAll();
    chap3.addFolderItem(fig3);
    chap3.addFolderItem(fig4);

    s += "\n... after the changes\n";
    s += book.toString(true, "  ");

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


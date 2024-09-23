//*************************************************************************
/**
 * FolderItem.java - A class describing a FolderItem
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Date;

/**
 * This class is the superclass of Folder.  It encapsulates information
 * about an item in a folder.
 */
public class FolderItem {

  // Attributes in the FolderItem class
  private String name;
  private int size;
  private Date date;
  private Folder parentFolder;

  /** 
   * Constructs a FolderItem object.
   *
   * @param name	name of the FolderItem
   * @param size	size of the FolderItem
   * @param date	date of the FolderItem
   */
  public FolderItem(String name, int size, Date date) {
    this.name = name; 
    this.size = size;
    this.date = date;
  }

  /** 
   * Returns the name of this FolderItem.
   *
   * @return the name of this FolderItem
   */
  public String getName() { 
    return name; 
  }

  /** 
   * Returns the size of this FolderItem.
   *
   * @return the size of this FolderItem
   */
  public int getSize() {
    return size; 
  }

  /** 
   * Returns the date of this FolderItem.
   *
   * @return the date of this FolderItem
   */
  public Date getDate() { 
    return date; 
  }

  /** 
   * Returns the parent Folder of this FolderItem.
   *
   * @return the parent Folder of this FolderItem
   */
  public Folder getParentFolder() { 
    return parentFolder; 
  }

  /** 
   * Sets the name of this FolderItem.
   *
   * @param name	the name of this FolderItem
   */
  public void setName(String name) { 
    this.name = name; 
  }

  /** 
   * Sets the size of this FolderItem.
   *
   * @param size	the size of this FolderItem
   */
  public void setSize(int size) {
    this.size = size; 
  }

  /** 
   * Sets the date of this FolderItem.
   *
   * @param date	the date of this FolderItem
   */
  public void setDate(Date date) { 
    this.date = date; 
  }

  /** 
   * Sets the parent folder of this FolderItem.
   *
   * @param folder 	the parent Folder
   */
  public void setParentFolder(Folder folder) { 
    this.parentFolder = folder; 
  }

  /** 
   * Returns the information of this FolderItem as a string.
   *
   * @return the information of this FolderItem
   */
  public String toString() { 
    String s = name + " - ";
    s += " Size: " + size;
    s += ", Date: " + date;
    if (parentFolder != null)
      s += ", Parent: " + parentFolder.getName();
    s += "\n";
    return s;
  }

}


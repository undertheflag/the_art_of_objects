//*************************************************************************
/**
 * Model.java - A data model for an array of Person objects
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.*;
import javax.swing.*;

/**
 * A data model for an array of Person objects.  It automatically sorts
 * the objects when a new one is added.
 */
public class Model extends DefaultListModel {

  private Person[] persons;

  /**
   * Adds a person with the name and age.
   *
   * @param person	the person
   */
  public void addElement(Person person) {
    super.addElement(person);
    persons = new Person[size()];

    //copy the Persons into an array
    for(int i=0; i< size(); i++) {
        persons[i] = (Person)elementAt(i);
    }

    //sort them
    Arrays.sort (persons);

    // clear all and put them back
    clear();

    for (int i  =0; i < persons.length; i++) {
      super.addElement(persons[i]);
    }

    // fire an event
    fireContentsChanged(this, 0, size());
  }

}

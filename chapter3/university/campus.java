//*************************************************************************
/**
 * Campus.java - A class describing a campus with classrooms
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;

/**
 * This class describes a campus with classrooms.
 */
public class Campus extends NamedObject {

  /** A vector is used to contain the classrooms. */
  private PVector classrooms = new PVector();

  /** 
   * Constructs an object.
   */
  public Campus() {

  }

  /** 
   * Constructs an object.
   *
   * @param name	name of the object
   */
  public Campus(String name) {
    super(name); 
  }


  /******** Classrooms *********/

  /** 
   * Returns the classrooms of this campus.
   *
   * @return the classrooms of this campus as an array
   */
  public Classroom[] getClassrooms() {
    Object[] o = classrooms.toArray();
    Classroom[] r = new Classroom[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Classroom)o[i];
    return r;
  }

  /**
   * Returns the number of classroom objects in the extent.
   *
   * @return 	the number of classroom objects
   */
  public int getClassroomCount() {
    return classrooms.size();
  }

  /**
   * Returns the classroom object for the input name.
   *
   * @param name 	Name of classroom
   * @return Classroom object.  Null if none matched.
   */
  public Classroom getClassroom(String name) {
    Enumeration e = classrooms.elements();
    while (e.hasMoreElements()) {
      Classroom c = (Classroom) e.nextElement();
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  /**
   * Returns whether the input classroom object exists.
   *
   * @param classroom 	a classroom object
   * @return true if the input classroom object is found.  False otherwise.
   */
  public boolean contains(Classroom classroom) {
    return classrooms.contains(classroom);
  }

  /**
   * Adds a new classroom.
   *
   * @param classroom 	a classroom object
   */
  public void addClassroom(Classroom classroom) {
    classrooms.addElement(classroom);
  }

  /** 
   * Add a new classroom for this campus.
   *
   * @param name	name of the classroom
   */
  public void addClassroom(String name) {
    classrooms.add(new Classroom(name, null));
  }

  /**
   * Removes the named classroom.
   *
   * @param name	Name of classroom.
   * @exception Exception if the classroom is not found
   */
  public void removeClassroom(String name) throws Exception {
    /* get the classroom Object */
    Classroom c = getClassroom(name);
    if (c == null) throw new Exception("Classroom not found: " + name);

    /* Remove the classroom Object from the vector */
    classrooms.removeElement(c);
  }

  /**
   * Removes the input classroom.
   *
   * @param classroom  The classroom to be removed.
   * @exception Exception if the classroom is not found
   */
  public void removeClassroom(Classroom classroom) throws Exception {
    if (!classrooms.contains(classroom))
      throw new Exception("Classroom not found: " + classroom.getName());
    classrooms.removeElement(classroom);
  }

  /**
   * Removes all classroom objects.
   */
  public void removeAllClassrooms( ) {
    classrooms.removeAllElements();
  }

  /** 
   * Returns the information of this campus as a string.
   *
   * @return the information of this campus
   */
  public String toString() { 
    String s = "  Campus: " + super.toString() + "\n";
    s += "    Classrooms:\n";

    Classroom[] h = getClassrooms();
    for (int i=0; i<h.length; i++) {
      s += h[i].toString();
    }

    return s;
  }

}


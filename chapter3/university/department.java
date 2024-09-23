//*************************************************************************
/**
 * Department.java - A class describing a department
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;

/**
 * This class describes a department with instructors and courses.
 */
public class Department extends NamedObject {

  /** A vector is used to contain the instructors. */
  private PVector instructors = new PVector();

  /** A vector is used to contain the course. */
  private PVector courses = new PVector();

  /** 
   * Constructs an object.
   */
  public Department() {

  }

  /** 
   * Constructs an object.
   *
   * @param name	name of the object
   */
  public Department(String name) {
    super(name); 
  }


  /******** Courses *********/

  /** 
   * Returns the courses of this department.
   *
   * @return the courses of this department as an array
   */
  public Course[] getCourses() {
    Object[] o = courses.toArray();
    Course[] r = new Course[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Course)o[i];
    return r;
  }

  /**
   * Returns the number of course objects in the extent.
   *
   * @return 	the number of course objects
   */
  public int getCourseCount() {
    return courses.size();
  }

  /**
   * Returns the course object for the input name.
   *
   * @param name 	Name of course
   * @return Course object.  Null if none matched.
   */
  public Course getCourse(String name) {
    Enumeration e = courses.elements();
    while (e.hasMoreElements()) {
      Course c = (Course) e.nextElement();
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  /**
   * Returns whether the input course object exists.
   *
   * @param course 	a course object
   * @return true if the input course object is found.  False otherwise.
   */
  public boolean contains(Course course) {
    return courses.contains(course);
  }

  /**
   * Adds a new course.
   *
   * @param course 	a course object
   */
  public void addCourse(Course course) {
    courses.addElement(course);
  }

  /** 
   * Add a new course for this department.
   *
   * @param name	name of the course
   */
  public void addCourse(String name) {
    courses.add(new Course(name));
  }

  /**
   * Removes the named course.
   *
   * @param name	Name of course.
   * @exception Exception if the course is not found
   */
  public void removeCourse(String name) throws Exception {
    /* get the course Object */
    Course c = getCourse(name);
    if (c == null) throw new Exception("Course not found: " + name);

    /* Remove the course Object from the vector */
    courses.removeElement(c);
  }

  /**
   * Removes the input course.
   *
   * @param course  The course to be removed.
   * @exception Exception if the course is not found
   */
  public void removeCourse(Course course) throws Exception {
    if (!courses.contains(course))
      throw new Exception("Course not found: " + course.getName());
    courses.removeElement(course);
  }

  /**
   * Removes all course objects.
   */
  public void removeAllCourses( ) {
    courses.removeAllElements();
  }


  /******** Instructors *********/

  /** 
   * Returns the instructors of this department.
   *
   * @return the instructors of this department as an array
   */
  public Instructor[] getInstructors() {
    Object[] o = instructors.toArray();
    Instructor[] r = new Instructor[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Instructor)o[i];
    return r;
  }

  /**
   * Returns the number of instructor objects in the extent.
   *
   * @return 	the number of instructor objects
   */
  public int getInstructorCount() {
    return instructors.size();
  }

  /**
   * Returns the instructor object for the input name.
   *
   * @param name 	Name of instructor
   * @return Instructor object.  Null if none matched.
   */
  public Instructor getInstructor(String name) {
    Enumeration e = instructors.elements();
    while (e.hasMoreElements()) {
      Instructor c = (Instructor) e.nextElement();
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  /**
   * Returns whether the input instructor object exists.
   *
   * @param instructor 	a instructor object
   * @return true if the input instructor object is found.  False otherwise.
   */
  public boolean contains(Instructor instructor) {
    return instructors.contains(instructor);
  }

  /**
   * Adds a new instructor.
   *
   * @param instructor 	a instructor object
   */
  public void addInstructor(Instructor instructor) {
    instructors.addElement(instructor);
  }

  /** 
   * Add a new instructor for this department.
   *
   * @param name	name of the instructor
   */
  public void addInstructor(String name) {
    instructors.add(new Instructor(name));
  }

  /**
   * Removes the named instructor.
   *
   * @param name	Name of instructor.
   * @exception Exception if the instructor is not found
   */
  public void removeInstructor(String name) throws Exception {
    /* get the instructor Object */
    Instructor c = getInstructor(name);
    if (c == null) throw new Exception("Instructor not found: " + name);

    /* Remove the instructor Object from the vector */
    instructors.removeElement(c);
  }

  /**
   * Removes the input instructor.
   *
   * @param instructor  The instructor to be removed.
   * @exception Exception if the instructor is not found
   */
  public void removeInstructor(Instructor instructor) throws Exception {
    if (!instructors.contains(instructor))
      throw new Exception("Instructor not found: " + instructor.getName());
    instructors.removeElement(instructor);
  }

  /**
   * Removes all instructor objects.
   */
  public void removeAllInstructors( ) {
    instructors.removeAllElements();
  }

  /** 
   * Returns the information of this department as a string.
   *
   * @return the information of this department
   */
  public String toString() { 
    String s = "Department: " + super.toString() + "\n";

    s += "  Courses in " + getName() + " department:\n";
    Course[] c = getCourses();
    for (int i=0; i<c.length; i++) {
      s += c[i].toString();
    }

    s += "  Instructors in " + getName() + " department:\n";
    Instructor[] h = getInstructors();
    for (int i=0; i<h.length; i++) {
      s += h[i].toString();
    }

    return s;
  }

}


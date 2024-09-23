//*************************************************************************
/**
 * Course.java - A class describing a course with sessions
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;

/**
 * This class describes a course with sessions.
 */
public class Course extends NamedObject {

  /** A vector is used to contain the sessions. */
  private PVector sessions = new PVector();

  /** 
   * Constructs an object.
   */
  public Course() {

  }

  /** 
   * Constructs an object.
   *
   * @param name	name of the object
   */
  public Course(String name) {
    super(name); 
  }


  /******** Sessions *********/

  /** 
   * Returns the sessions of this course.
   *
   * @return the sessions of this course as an array
   */
  public Session[] getSessions() {
    Object[] o = sessions.toArray();
    Session[] r = new Session[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Session)o[i];
    return r;
  }

  /**
   * Returns the number of session objects in the extent.
   *
   * @return 	the number of session objects
   */
  public int getSessionCount() {
    return sessions.size();
  }

  /**
   * Returns the session object for the input name.
   *
   * @param name 	Name of session
   * @return Session object.  Null if none matched.
   */
  public Session getSession(String name) {
    Enumeration e = sessions.elements();
    while (e.hasMoreElements()) {
      Session c = (Session) e.nextElement();
      if (c.getName().equals(name)) return c;
    }
    return null;
  }

  /**
   * Returns whether the input session object exists.
   *
   * @param session 	a session object
   * @return true if the input session object is found.  False otherwise.
   */
  public boolean contains(Session session) {
    return sessions.contains(session);
  }

  /** 
   * Add a new session for this course.
   * This core operation guarantees referential integrity.
   *
   * @param name	name of the Session
   * @param schedule	schedule of the Session
   * @param classroom	classroom of the Session
   * @param itor1	first instructor of the Session
   * @param itor2	second instructor of the Session (may be null)
   */
  public void addSession(String name, Schedule schedule, 
  	Classroom classroom, Instructor itor1, Instructor itor2) {
    Session s = new Session(name, this, schedule, classroom, itor1, itor2);
    sessions.add(s);
  }

  /**
   * Removes the named session.
   *
   * @param name	Name of session.
   * @exception Exception if the session is not found
   */
  public void removeSession(String name) throws Exception {
    /* get the session Object */
    Session c = getSession(name);
    if (c == null) throw new Exception("Session not found: " + name);

    /* Remove the session Object from the vector */
    removeSession(c);
  }

  /**
   * Removes the input session.
   * This core operation guarantees referential integrity.
   *
   * @param session  The session to be removed.
   * @exception Exception if the session is not found
   */
  public void removeSession(Session session) throws Exception {
    if (!sessions.contains(session))
      throw new Exception("Session not found: " + session.getName());

    // delink the course, schedule, classroom
    session.setCourse(null);
    session.setSchedule(null);
    session.setClassroom(null);

    // delink the instructors
    session.setInstructors(null, null);

    sessions.removeElement(session);
  }

  /**
   * Removes all session objects.  Calls core operation removeSession 
   * to do this in order to maintain referential integrity.
   */
  public void removeAllSessions() {
    Enumeration e = sessions.elements();
    try {
      while (e.hasMoreElements()) {
	Session s = (Session) e.nextElement();
	removeSession(s);  // call core operation
      }
    } catch (Exception e2) {}
  }

  /** 
   * Returns the information of this course as a string.
   *
   * @return the information of this course
   */
  public String toString() { 
    String s = "    " + super.toString() + "\n";
    s += "      Sessions:\n";

    Session[] h = getSessions();
    for (int i=0; i<h.length; i++) {
      s += h[i].toString(this);
    }

    return s;
  }

}


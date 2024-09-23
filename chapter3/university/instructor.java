//*************************************************************************
/**
 * Instructor.java - A class describing a instructor with sessions
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;

/**
 * This class describes a instructor with sessions.
 */
public class Instructor extends NamedObject {

  /** A vector is used to contain the sessions. */
  private PVector sessions = new PVector();

  /** 
   * Constructs an object.
   */
  public Instructor() {

  }

  /** 
   * Constructs an object.
   *
   * @param name	name of the object
   */
  public Instructor(String name) {
    super(name); 
  }


  /******** Sessions *********/

  /** 
   * Returns the sessions of this instructor.
   *
   * @return the sessions of this instructor as an array
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
   * Adds a new session.
   *
   * @param session 	a session object
   */
  public void addSession(Session session) {
    sessions.addElement(session);
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
    sessions.removeElement(c);
  }

  /**
   * Removes the input session.
   *
   * @param session  The session to be removed.
   * @exception Exception if the session is not found
   */
  public void removeSession(Session session) throws Exception {
    if (!sessions.contains(session))
      throw new Exception("Session not found: " + session.getName());
    sessions.removeElement(session);
  }

  /**
   * Removes all session objects.
   */
  public void removeAllSessions( ) {
    sessions.removeAllElements();
  }

  /** 
   * Returns the information of this instructor as a string.
   *
   * @return the information of this instructor
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


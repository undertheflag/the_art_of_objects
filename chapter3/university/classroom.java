//*************************************************************************
/**
 * Classroom.java - A class describing a classroom
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;

/**
 * A class describing a classroom.
 * This class is part of a binary association (Session).
 */
public class Classroom extends NamedObject {

  // Private attributes in the Classroom class
  private String location;

  /** A vector is used to contain the sessions. */
  private PVector sessions = new PVector();


  /** 
   * Constructs a classroom object.
   *
   * @param name	name of the classroom
   */
  public Classroom(String name) {
    this(name, null); 
  }

  /** 
   * Constructs a classroom object.
   *
   * @param name	name of the classroom
   * @param location	location of the classroom
   */
  public Classroom(String name, String location) {
    super(name); 
    this.location = location;
  }

  /** 
   * Returns the location of this classroom.
   *
   * @return the location of this classroom
   */
  public String getLocation() {
    return location; 
  }

  /** 
   * Sets the location of this classroom.
   *
   * @param location	the location of this classroom
   */
  public void setLocation(String location) { 
    this.location = location; 
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
   * Returns the sessions of this classroom.
   *
   * @return the sessions of this classroom as an array
   */
  public Session[] getSessions() { 
    Object[] o = sessions.toArray();
    Session[] r = new Session[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Session)o[i];
    return r;
  }

  /** 
   * Add a new session for this classroom.
   *
   * @param session	the session
   */
  public void addSession(Session session) {
    sessions.add(session);
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
   * Returns the information of this classroom as a string.
   *
   * @return the information of this classroom
   */
  public String toString() { 
    String s = "      " + super.toString();
    if (location != null) s += " (" + location + ")";
    s += "\n";
    s += "        Sessions:\n";

    Session[] t = getSessions();
    for (int i=0; i<t.length; i++) {
      s += t[i].toString(this);
    }

    return s;
  }

}


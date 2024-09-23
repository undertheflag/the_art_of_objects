//*************************************************************************
/**
 * Agent.java - a persistent class for an Agent.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package atmcs;

// Explicit import to avoid ambiguous Iterator class
import COM.odi.util.Iterator;

import java.util.*;
import COM.odi.*;
import COM.odi.util.*;

/**
 * The Agent class.
 */
public class Agent {
  /* Agent id. */
  private String mId;

  /* Agent holder's name */
  private String mName;

  /* Agent's related sessions */
  private OSVector mSessions = new OSVector();

  /**
   * Constructor hidden.
   *
   * @param id 		Agent id
   * @param name 	Name of Agent holder
   */
  protected Agent(String id, String name) {
    mId = id;
    mName = name;
  }

  // Get methods
  public String getId() {
    return mId;
  }
     
  public String getName() {
    return mName;
  }

  public Session[] getSessions() {
    Object[] o = mSessions.toArray();
    Session[] r = new Session[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Session)o[i];
    return r;
  }

  public int getSessionCount() {
    return mSessions.size();
  }

  /**
   * Get the sessions between two times.
   */
  public Session[] getSessionsBetween(Date timeA, Date timeB) {
    Vector v = new Vector();
    Iterator it = mSessions.iterator();
    while ( it.hasNext() ) {
      Session s = (Session) it.next();
      if ( s.getStartTime().after(timeA) &&
           s.getStartTime().before(timeB)   ) v.add(s);
    }
    return (Session[]) v.toArray(new Session[1]);
  }

  public boolean contains(Session session) {
    return mSessions.contains(session);
  }

  public void addSession(Session session) {
    mSessions.add(session);
  }

  /* Overload hashCode() to avoid being stored in hash table.
   */	
  public int hashCode() {
    return super.hashCode();
  }

}

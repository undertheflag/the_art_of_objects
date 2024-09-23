//*************************************************************************
/**
 * Card.java - a persistent class for a cash card.
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
 * The Card class.
 */
public class Card {
  /* Card number. */
  private int mNumber;

  /* Card holder's name */
  private String mName;

  /* Card holder's primary account number */
  private int mAccountNumber;

  private OSVector mSessions = new OSVector();

  /**
   * Constructor.
   *
   * @param number 	Card number
   * @param name 	Name of card holder
   * @param accountNumber 	Account number
   */
  public Card(int number, String name, int accountNumber) {
    mNumber = number;
    mName = name;
    mAccountNumber = accountNumber;
  }

  public int getNumber() {
    return mNumber;
  }
     
  public String getName() {
    return mName;
  }

  public int getAccountNumber() {
    return mAccountNumber;
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

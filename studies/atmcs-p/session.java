//*************************************************************************
/**
 * Session.java - a persistent class for a Session.
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
 * The Session class.
 */
public class Session {
  /* Start and end times. */
  private DateTime mStartTime;
  private DateTime mEndTime;

  /* The card that used this session. */
  private Card mCard;

  /* The Agent that used this session. */
  private Agent mAgent;

  /* The Transactions in this session. */
  private OSVector mTransactions = new OSVector();

  /**
   * Constructor.
   *
   * @param startTime 	Session start time.
   * @param card 	The card that uses this session.
   * @param agent	The agent that uses this session.
   */
  public Session(DateTime startTime, Card card, Agent agent) {
    mStartTime = startTime;
    mCard = card;
    mAgent = agent;
  }

  public void endSession(DateTime endTime) {
    mEndTime = endTime;
  }

  public DateTime getStartTime() {
    return mStartTime;
  }

  public DateTime getEndTime() {
    return mEndTime;
  }

  public Card getCard() {
    return mCard;
  }

  public Agent getAgent() {
    return mAgent;
  }
     
  public Transaction[] getTransactions() {
    Object[] o = mTransactions.toArray();
    Transaction[] r = new Transaction[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Transaction)o[i];
    return r;
  }

  public int getTransactionCount() {
    return mTransactions.size();
  }

  public Transaction[] getTransactionsBetween(DateTime timeA, DateTime timeB) {
    Vector v = new Vector();
    Iterator it = mTransactions.iterator();
    while ( it.hasNext() ) {
      Transaction s = (Transaction) it.next();
      if ( s.getTime().after(timeA) &&
           s.getTime().before(timeB)   ) v.add(s);
    }
    return (Transaction[]) v.toArray(new Transaction[1]);
  }

  public boolean contains(Transaction transaction) {
    return mTransactions.contains(transaction);
  }

  public Transaction addTransaction(DateTime time, String content) 
    throws AtmcsException {
    if (mEndTime != null) 
      throw new AtmcsException("session already ended");
    Transaction transaction = new Transaction(time, content);
    mTransactions.add(transaction);
    return transaction;
  }

  /* Overload hashCode() to avoid being stored in hash table.
   */	
  public int hashCode() {
    return super.hashCode();
  }

}

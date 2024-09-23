//*************************************************************************
/**
 * Transaction.java - a persistent class for a Transaction.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package atmcs;

import java.util.*;
import COM.odi.*;
import COM.odi.util.*;

/**
 * Transaction class.
 */
public class Transaction {
  private DateTime mTime;

  /* Transaction holder's name */
  private String mContent;

  /**
   * Constructor.
   *
   * @param time 	Transaction time
   * @param content 	Content of Transaction
   */
  public Transaction(DateTime time, String content) {
    mTime = time;
    mContent = content;
  }

  public DateTime getTime() {
    return mTime;
  }
     
  public String getContent() {
    return mContent;
  }

  /* Overload hashCode() to avoid being stored in hash table.
   */	
  public int hashCode() {
    return super.hashCode();
  }

}

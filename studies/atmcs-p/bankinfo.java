//*************************************************************************
/**
 * BankInfo.java - a persistent class of bank information.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package atmcs;

import COM.odi.*;
import java.util.Enumeration;
import COM.odi.util.OSVector;

/**
 * This class contains bank information.  It manages a collection of 
 * cash cards.
 * Each bank issues cards with a unique prefix so it can be found by
 * the card prefix.
 */
public class BankInfo {

  /* Bank name */
  private String mName;

  /* Card prefix */
  private int mPrefix;

  /* Bank server name */
  private String mServerName;

  /* Vector of Cards */
  private OSVector mCards = new OSVector(50, 10);

  /**
   * Constructor
   *
   * @param name	name of the bank
   * @param prefix	card prefix
   * @param serverName	name of the bank server
   */
  public BankInfo(String name, int prefix, String serverName) 
  throws Exception {
    mName = name;
    mPrefix = prefix;
    mServerName = serverName;
    if ( prefix < 1000 || prefix > 9999 ) 
      throw new Exception("Bank prefix must be 4-digit.");
  }

  public String getName() {
    return mName;
  }

  public int getPrefix() {
    return mPrefix;
  }
     
  public String getServerName() {
    return mServerName;
  }

  public Card[] getCards() {
    Object[] o = mCards.toArray();
    Card[] r = new Card[o.length];
    for (int i=0; i<o.length; i++) r[i] = (Card)o[i];
    return r;
  }

  public int getCardCount() {
    return mCards.size();
  }

  /**
   * Find Card object for the input card number.
   *
   * @param number 	card number
   * @return Card object.  Null if none matched.
   */
  public Card getCard(int number) {
    Enumeration e = mCards.elements();
    while (e.hasMoreElements()) {
      Card c = (Card) e.nextElement();
      if (c.getNumber() == number) return c;
    }
    return null;
  }

  public boolean contains(Card card) {
    return mCards.contains(card);
  }

  /**
   * Add a new Card.
   *
   * @param number 	Card number.
   * @param name 	Card holder's name.
   * @return A Card object.
   */

  public void addCard(int number, String name, int accountNumber) {
    Card ac = new Card(number, name, accountNumber);
    mCards.addElement(ac);
  }

  /**
   * Remove Card form the vector.
   *
   * @param number 	Card number.
   * @param ssn Social security number of account to be removed.
   */

  public void removeCard(int number) throws AtmcsException {
    /* get the Card Object */
    Card c = getCard(number);
    if (c == null) throw new AtmcsException("Card not found:" + number);

    /* Remove the Card Object from the vector */
    mCards.removeElement(c);
  }

  public void removeCard(Card card) {
    mCards.removeElement(card);
  }

  public void removeAllCards( ) {
    mCards.removeAllElements();
  }

}

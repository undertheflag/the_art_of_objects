/*
 * DateTime.java - a persistent enabled Date class.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */

package atmcs;

import java.io.Serializable;
import java.util.*;

/**
 * A persistent enabled Date class.  It has methods to compare dates.
 */
public final class DateTime implements Serializable {
  static final long CID = 1;

  private short mMonth;
  private short mDate;
  private short mYear;
  private short mHour;
  private short mMinute;
  private short mSecond;

  public DateTime() {
     Calendar now = Calendar.getInstance();
     // MONTH ranges from 0 to 11
     mMonth = (short) (now.get(Calendar.MONTH) + 1);
     mDate = (short) (now.get(Calendar.DAY_OF_MONTH));
     mYear = (short) (now.get(Calendar.YEAR));
     mHour = (short) (now.get(Calendar.HOUR_OF_DAY));
     mMinute = (short) (now.get(Calendar.MINUTE));
     mSecond = (short) (now.get(Calendar.SECOND));
  }

   /**
    * Convert this to a java.util.Date object.
    *
    * @return    a Date object.
    */
  public Date toDate() {
    Calendar c = Calendar.getInstance();
    c.set((int)mYear, (int)mMonth - 1, (int)mDate, 
	  (int)mHour, (int)mMinute, (int)mSecond);
    return c.getTime();
  }

   /**
    * Returns the year information
    *
    * @return    year
    */
  public short getYear() {
     return mYear;
  }

   /**
    * Returns the month of year information
    *
    * @return    month of year
    */
  public short getMonth() {
     return mMonth;
  }

   /**
    * Returns the Date of month information
    *
    * @return    Date of month
    */
  public short getDate() {
     return mDate;
  }

   /**
    * Returns the hour of day (0-24) information
    *
    * @return    hour of day
    */
  public short getHour() {
     return mHour;
  }

 /**
  * Returns the minute of hour information
  *
  * @return    minute of hour
  */
  public short getMinute() {
     return mMinute;
  }

 /**
  * Returns the second of minute information
  *
  * @return    second of minute
  */
  public short getSecond() {
     return mSecond;
  }

   /**
    * Generates a string in the form "month/day/year hour:minute:second".
    *
    * @return    String representation of the date object 
    */
  public String toString() {
     return String.valueOf(mMonth) + "/" +
	    String.valueOf(mDate) + "/" +
	    String.valueOf(mYear) + " " +
	    String.valueOf(mHour) + ":" +
	    String.valueOf(mMinute) + ":" +
	    String.valueOf(mSecond);
  }

 /**
  * Check if this is before the input date.
  *
  * @param date 	A date object
  * @return    true if this is before the input date.
  */
  public boolean before(Date date) {
    return toDate().before(date);
  }

 /**
  * Check if this is before the input date.
  *
  * @param date 	A persistent date object
  * @return    true if this is before the input date.
  */
  public boolean before(DateTime date) {
    return toDate().before(date.toDate());
  }


 /**
  * Check if this is after the input date.
  *
  * @param date 	A date object
  * @return    true if this is after the input date.
  */
  public boolean after(Date date) {
    return toDate().after(date);
  }

 /**
  * Check if this is after the input date.
  *
  * @param date 	A persistent date object
  * @return    true if this is after the input date.
  */
  public boolean after(DateTime date) {
    return toDate().after(date.toDate());
  }

 /**
  * Check if this date is the same as the input (Year, Month, Date only)
  *
  * @param date 	A persistent date object
  * @return    true if this is the same date as the input.
  */
  public boolean isSameDate(DateTime date) {
    return (mYear == date.getYear() && mMonth == date.getMonth() 
	 && mDate == date.getDate() );
  }

 /**
  * Shift the date by the input delta amount.
  */
  public void shiftDate(int del) {
    mDate += (short)del;
  }
}

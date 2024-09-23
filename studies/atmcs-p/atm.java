//*************************************************************************
/**
 * ATM.java - a persistent class for an ATM.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package atmcs;

import COM.odi.*;

/**
 * The ATM class.
 */
public class ATM extends Agent {

  /* ATM Location. */
  private String mLocation;

  /**
   * Constructor.
   *
   * @param id 		ATM id
   * @param name 	Name of ATM
   * @param location	ATM location
   */
  public ATM(String id, String name, String location) {
    super(id, name);
    mLocation = location;
  }

  public String getLocation() {
    return mLocation;
  }
     
}

//*************************************************************************
/**
 * Cashier.java - a persistent class for a Cashier.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package atmcs;

import COM.odi.*;

/**
 * The Cashier class.
 */
public class Cashier extends Agent {

  /* Cashier Branch. */
  private String mBranch;

  /**
   * Constructor.
   *
   * @param id 		Cashier id
   * @param name 	Name of Cashier
   * @param branch	Cashier's branch
   */
  public Cashier(String id, String name, String branch) {
    super(id, name);
    mBranch = branch;
  }

  public String getBranch() {
    return mBranch;
  }
     
}

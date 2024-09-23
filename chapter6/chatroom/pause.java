//*************************************************************************
/**
 * Pause.java - A class for pausing 
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This simple class just pauses by the input seconds.
 */
public class Pause {
 
  /**
   * Main method.
   * 
   * @exception Exception if any exception is thrown
   */
  public static void main(String args[]) throws Exception {
    int d = 1;
    System.out.println("java Pause [seconds]  (default = 1 sec)");
    if (args.length >0) {
      d = Integer.valueOf(args[0]).intValue();
    }
    Thread.sleep(d*1000);
  }

}

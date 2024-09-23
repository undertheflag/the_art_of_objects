//*************************************************************************
/**
 * Timer.java - A timer class
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class fires events periodically.  It uses the swing Timer class
 * to do the job.  This class demonstrates a simple push event supplier.
 */
public class Timer {

  private int mDelay;
  private final ListenerIF mListener;
  private final javax.swing.Timer mTimer;

  private int count = 0;

  /** 
   * Constructs a Timer object.
   *
   * @param delay	the delay (seconds) between event firing
   * @param listener	the listener to this timer
   */
  public Timer(int delay, ListenerIF listener) {
    this.mDelay = delay; 
    this.mListener = listener;

    int delay_msec = 1000 * delay;    // delay in msec

    // We use the Timer in swing for convenience.
    mTimer = new javax.swing.Timer( delay_msec, 

      // an unnamed inner class instance to handle
      // ActionEvent from javax.swing.Timer
      new java.awt.event.ActionListener() {
	public void actionPerformed(java.awt.event.ActionEvent e) {
	  // debug(e.toString());
	  String msg = "Timer message " + count++ + " at " 
	  	+ (new java.util.Date()).toString();
	  mListener.processEvent(msg);
	  return;
	}
      }

    );

    mTimer.setRepeats(true);  // make it repetitive

  }

  /** 
   * Starts the timer.
   */
  public void start() {
    mTimer.start();
  }

  /** 
   * Stops the timer.
   */
  public void stop() {
    mTimer.stop();
  }

}

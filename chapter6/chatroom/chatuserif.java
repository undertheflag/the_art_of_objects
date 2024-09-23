//*************************************************************************
/**
 * ChatUserIF.java - An interface for a ChatUser.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.rmi.RemoteException; 

/**
 * This interface defines the operations for a ChatUser.
 * It extends the java.rmi.Remote interface so can be used with RMI.
 */
public interface ChatUserIF extends java.rmi.Remote {
 
  /** 
   * Returns the name of this ChatUser.
   */
  public String getName() throws RemoteException;

  /** 
   * Send a message to this ChatUser for display.
   */
  public void send(String message) throws RemoteException;

}

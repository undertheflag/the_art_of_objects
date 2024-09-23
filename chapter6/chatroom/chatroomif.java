//*************************************************************************
/**
 * ChatRoomIF.java - An interface for a ChatRoom.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.rmi.RemoteException; 

/**
 * This interface defines the operations for a ChatRoom.
 * It extends the java.rmi.Remote interface so can be used with RMI.
 */
public interface ChatRoomIF extends java.rmi.Remote {
 
  /** 
   * Signs the chat user on to this ChatRoom.
   *
   * @param user the chat user
   */
  public void signOn(ChatUserIF user) throws RemoteException;

  /** 
   * Signs off the chat user from this ChatRoom.
   *
   * @param user the chat user
   */
  public void signOff(ChatUserIF user) throws RemoteException;

  /** 
   * Send a message to this ChatRoom.
   *
   * @param message the message
   */
  public void send(String message) throws RemoteException;

}

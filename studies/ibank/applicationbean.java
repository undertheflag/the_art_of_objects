//*************************************************************************
/**
 * ApplicationBean.java - This interface defines bean for servlet 
 *	applications.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package ibank;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This interface defines bean for servlet applications.
 *	
 */
public interface ApplicationBean {

  /**
   * This is called after the bean has been instantiated.
   */
  public void init(HttpServletRequest request, HttpServletResponse response);

  /**
   * This is called to process the command embedded in request.
   */
  public void process() throws Exception;

}
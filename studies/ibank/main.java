//*************************************************************************
/**
 * Main.java - This servlet simulates Internet Banking applications.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package ibank;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import atmcs.*;  // from atmcs.idl

/**
 * This class is the main entry gate to other web applications in this
 * package.  It deals with security control and session management.  Once
 * a request passes security check, the appropriate application object
 * is invoked to process the request.
 *
 * Each instance of servlet Main may be associated with multiple web
 * browser sessions.  The associated sessionIF uses the key "sessionIF".
 * The constant IDLE_TIMEOUT controls the idle timeout of an inactive
 * session.
 */

public class Main extends HttpServlet {

  // static definitions
  final static String AGENT_ID = "iBank";
  final static String URL_BASE = "/ooda/ibank";
  final static String URL_LOGIN = URL_BASE + "/iBankLogin.htm";
  final static String URL_WELCOME = URL_BASE + "/test1.htm";

  final static String [] CONNECT_ARGS = null;

  /** public string for www root under this servlet zone */
  public static String mWwwRoot;

  /** Name of session manager */
  private String mMgrName = "ATM Session Manager";

  /** Handle for session manager */
  private SessionMgrIF mSessionMgr = null;

  /** Private handles */
  private HttpServletResponse mResponse;
  private PrintWriter mOut;
  private HttpSession mSession;

  /** Constructor */
  public Main() {
    super();
  }

  /** Initialize handles and template files. 
   * Override init() in GenericServlet.
   *
   * @exception ServletException
   */
  public void init() throws ServletException {
    System.out.println("Main initializing...");
    mSessionMgr = connect(CONNECT_ARGS);

    mWwwRoot = getInitParameter("wwwroot");
    if (mWwwRoot == null) {
      show("Main.init: global parameter 'wwwroot' not found in ooda.properties");

      // this exception will appear in JServ/logs/mod_jserv.log
      // throw new ServletException("Main.init: wwwroot not found in ooda.properties");
    }

  }

  /** Connect to the ATM Central Server */
  public SessionMgrIF connect(String args[]) {

    SessionMgrIF mgr = null;
    String host = null; // localhost or 127.0.0.1 won't work here

    // use smart agent at host
    Properties props = new Properties();
    if (host != null) props.put("ORBagentAddr", host);

    // Set for vbroker.  Must be done before ORB.init.
    java.lang.System.setProperty("org.omg.CORBA.ORBClass", 
      "com.visigenic.vbroker.orb.ORB");
    java.lang.System.setProperty("org.omg.CORBA.ORBSingletonClass", 
      "com.visigenic.vbroker.orb.ORB");

    // Initiliaze the ORB.
    org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(args, props);

    // default options: no deferred bind, do rebind
    org.omg.CORBA.BindOptions options = new org.omg.CORBA.BindOptions();

    // Locate the Session manager.
    try {
      mgr = SessionMgrIFHelper.bind(orb, mMgrName, host, options);
    } catch (org.omg.CORBA.NO_IMPLEMENT e) {
      System.err.println("Main: cannot connect to \"" +mMgrName+ "\"");
    }

    return mgr;
  }

  /** Process request from client.  Either GET or POST parameters
   *  are accepted.  The key for command is "_c".  Valid commands
   *  are list in the method "printHelp()".  
   *  The key for application is "_a".  For example, for _a=Account,
   *  the class ibank.Account will be loaded.
   */
  public void service(HttpServletRequest request, 
  		      HttpServletResponse response)
  throws ServletException, IOException {

    mResponse = response;  // set member variable

    // set content type and other response header fields first
    response.setContentType("text/html");

    // get the communication channel with the requesting client
    mOut = response.getWriter();

    // get the server identification
    String server = getServletConfig().getServletContext().getServerInfo();

    // try to reconect several times if connection is down
    if (mSessionMgr == null || mSessionMgr._non_existent()) {
      for (int i=0; i<2; i++) {
        mSessionMgr = connect(CONNECT_ARGS);
	if (mSessionMgr != null) break;
      }
    }

    // sanity check
    if (mSessionMgr == null || mSessionMgr._non_existent()) {
      show("Cannot connect to \"" + mMgrName + "\"");
      show("Please try again later.");
      return;
    }

    // Get session object - it will be created if does not exist
    mSession = request.getSession(true);

    // Get remote object from session.  
    // Non-null means session already authenticated.
    SessionIF sessionIF = (SessionIF) mSession.getValue("sessionIF");

    String command = request.getParameter("_c");
    String app = request.getParameter("_a");

    // Process commands available before login, including login
    try {
      if (command == null || "login".startsWith(command)) {

	String name = request.getParameter("username");
	String passwd = request.getParameter("password");

	if (name == null || passwd == null) {
	  response.sendRedirect(URL_LOGIN);
	}

	int cardNumber = 123;
	CardInfo cardInfo = new CardInfo(cardNumber, name, passwd);

	try {
	  // create a new sessionIF
	  sessionIF = mSessionMgr.login(cardInfo, AGENT_ID);

	  // save some objects with this session
	  mSession.putValue("sessionIF", sessionIF);
	  mSession.putValue("LastAuthenticatedAcessTime", 
	    new Long(System.currentTimeMillis()) );

	  // Get the balance of the sessionIF.
	  /*
	  float balance = sessionIF.getBalance();
	  show(" New sessionIF opened for: " + name);
	  show(" Initial balance is $" + balance);
	  */

	  if (mSession.getValue("targetUrl") != null ) {  // jump to target
	    String url = (String)mSession.getValue("targetUrl");
	    mSession.removeValue("targetUrl");  // use it once only
  	    response.sendRedirect(url);
	  } else {  // go to welcome page
  	    response.sendRedirect("/OODA/ibank.Main?_a=Account&_c=balance");
	  }
	  return;

	} catch (AtmcsError e) {
	  show(e.message);
	  return;
	}

      } 

      else if ("logout".startsWith(command)) {
        removeSession(sessionIF);
	show("Session Removed!");
	return;
      }

      else if ("showBanks".startsWith(command)) {
	show(mSessionMgr.getAccountMgrNames());
	return;
      } 

      else if (app == null && "help".startsWith(command)) {
	printHelp();
	return;
      } 

      else if (app == null && "init".startsWith(command)) {
        init();
        show("Main: init completed.");
        return;
      } 

      else if (mSession.isNew()) { // Not login yet.  Force login.
        mSession.putValue("targetUrl",  // record target url
	  request.getAttribute("org.apache.jserv.REQUEST_URI") );
	show("Please login first");
	response.sendRedirect(URL_LOGIN);
	return;
      }

      else if (!isSessionValid(sessionIF)) {  // check old session
        mSession.putValue("targetUrl",  // record target url
	  request.getAttribute("org.apache.jserv.REQUEST_URI") );
	response.sendRedirect(URL_LOGIN);
	return;
      }

      // Record current time
      mSession.putValue( "LastAuthenticatedAcessTime", 
	new Long(System.currentTimeMillis()) );

      // Process command for a validated session
      if (app == null) app = "Account";  // default
      app = "ibank." + app;  // prepend package name

      // Load the class dynamically and invoke process 
      try {
        Class c = Class.forName(app);
	ApplicationBean ab = (ApplicationBean) c.newInstance();
	ab.init(request, response);
	ab.process();

      } catch (ClassNotFoundException e) {
	show("Main: cannot find class file for application '" + app + "'");
      }
   
    } catch (Exception e) {
      e.printStackTrace(mOut);
    }

  }


  void printHelp() {
    show("<PRE>");
    show("-----------------------------------------------");
    show("URL: http://.../ibank.Main?_c=command&param1=value1&...");
    show("Valid commands:");
    show("  showBanks               // show available banks");
    show("  login username password // login to primary account");
    show("  logout                  // logout and quit");
    show("-----------------------------------------------");
    show("</PRE>");
  }

  /** Check if the session is valid.  A session is also invalid if
   *  it has expired.
   */
  boolean isSessionValid(SessionIF sessionIF) throws Exception {
    if ( sessionIF == null || sessionIF._non_existent() ) {
      show(" Please login again");
      removeSession(sessionIF);
      return false;
    }
    if ( hasSessionExpired() ) {
      show("Your previous session expired.  Please login again.");
      removeSession(sessionIF);
      return false;
    }
    return true;
  }

  /** Check if the session has expired. */
  boolean hasSessionExpired() {
    final long IDLE_TIMEOUT = 1 * 60;  // seconds

    Long last = (Long)mSession.getValue("LastAuthenticatedAcessTime");
    if (last == null) return false;  // not set yet.  Not expired.

    long dt = System.currentTimeMillis() - last.longValue();

    // show("Elapse time from last action (secs) = " + dt / 1000);
    if ( dt > IDLE_TIMEOUT * 1000 ) return true;
    return false;
  }

  // remove current session and logout
  private void removeSession(SessionIF sessionIF)
  throws Exception {
    if (sessionIF != null) mSessionMgr.logout(sessionIF);
    mSession.removeValue("sessionIF");
    mSession.invalidate();
    mResponse.sendRedirect(URL_LOGIN);
  }

  static int readInt(HttpServletRequest request, String name)
  throws Exception {
    String v = request.getParameter(name);
    try {
      return Integer.valueOf(v).intValue();
    } catch (NumberFormatException e) {
      throw new Exception("Nonexistent or invalid integer for parameter \""+name+ "\"");
    }
  }

  static float readFloat(HttpServletRequest request, String name)
  throws Exception {
    String v = request.getParameter(name);
    try {
      return Float.valueOf(v).floatValue();
    } catch (NumberFormatException e) {
      throw new Exception("Nonexistent or invalid float number for parameter \""+name+ "\"");
    }
  }

  public void show(String s) {
    mOut.println(s + "<BR>");
  }

  /** A main method for unit testing. */
  public static void main(String args[]) throws Exception {

    Main m = new Main();
    m.mSessionMgr = m.connect(args);
    String acc = m.mSessionMgr.getAccountMgrNames();
    System.out.println(acc);

  }

}

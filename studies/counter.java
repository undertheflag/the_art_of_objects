
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This servlet holds a transient counter of visitors.
 */

public class Counter extends HttpServlet {

    public static final String s = "Hello!";
    private static int i = 0;
    private static int j = 0;

    public Counter () {
      j++;
//      log("Counter servlet created: " + j);
    }

    public void service (HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // set content type and other response header fields first
        response.setContentType("text/html");

        // get the communication channel with the requesting client
        PrintWriter out = response.getWriter();

        // get the server identification
        String server = getServletConfig().getServletContext().getServerInfo();
	i++;

        // write the data
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">"
            + "<HTML>"
            + "<HEAD>"
            + " <TITLE>" + s + "</TITLE>"
            + " <META NAME=\"Author\" CONTENT=\"" + server + "\">"
            + "</HEAD>"
            + "<BODY BGCOLOR=\"#DDCCDD\">"
            + " <CENTER>"
            + "  <H1>" + s + "</H1>"
            + "  <H2> You are the number " + i + " visitor.</H1>"
            + "  <H2> among " + j + " servlet instance(s).</H1>"
	    + "<A HREF='/OODA/Counter?delay=4'>Try again with delay</A>"
	    + "<P>"
	    + "<A HREF='/ooda/Counter.java'>Link to servlet source</A>"
            + " </CENTER>"
            + "</BODY>"
            + "</HTML>");

	String delay_str = request.getParameter("delay");
	long delay = 0;
	if (delay_str != null) delay = (new Long(delay_str)).longValue();
	try { Thread.sleep(delay * 1000); } 
	catch (InterruptedException e) {; }

    }

  public static void main(String args[])  {

    new Counter();
    new Counter();
    new Counter();

    System.out.println("Number of Counter instances: " + j);

  }

}


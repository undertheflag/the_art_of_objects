package widget;  // this file should be under $CLASSPATH/widget

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

public class TestIcon extends Applet {
  Label message;
  Icon icon1a, icon1b, icon2, icon3;
  
  //--------------------------------------------------------------

  public void init() {

    /* Full path of base dir (that invokes the code) */
    String basedir = "file:///" + System.getProperty("user.dir") + "/";

    Font labelFont = new Font("Helvetica", Font.BOLD, 28);
    message = new Label("Click and drag any of the images");
    message.setFont(labelFont);
    message.setBackground(Color.red);
    message.setForeground(Color.yellow);
    message.setLocation(10,90);    // this has no effect
    add(message);
    icon1a = new Icon( basedir + "images/cut.gif");
    icon1b = new Icon( basedir + "images/paste.gif");
    icon2 = new Icon( basedir + "images/hand.gif");
    icon3 = new Icon( basedir + "images/copy.gif");
    icon1b.margin = 5;
    icon1b.marginColor = Color.yellow;
    add(icon1a);
    add(icon1b);
    add(icon2);
    add(icon3);

    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }

  //--------------------------------------------------------------

  public void processMouseEvent(MouseEvent e) {
    System.out.println("Applet: Mouseevent " + e);
  }
  
  //--------------------------------------------------------------
  // Lets this be run as either an applet or an application.

  public static void main(String[] args) {
    Frame mainFrame = new Frame("TestIcon");
    TestIcon app = new TestIcon();
    app.init();
    mainFrame.setSize(500,400);
    mainFrame.add("Center", app);
    mainFrame.setVisible(true);
    mainFrame.repaint();
  }

  //--------------------------------------------------------------
} 

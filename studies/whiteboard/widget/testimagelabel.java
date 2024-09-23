//*************************************************************************
/*
 * TestImageLabel.java - A test for the image label class.
 *  
 *   This file should be under $CLASSPATH/widget
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package widget;

import java.applet.Applet;
import java.awt.*;

import widget.ImageLabel;

public class TestImageLabel extends Applet {

  ImageLabel imageLabel1a, imageLabel1b, imageLabel1c;
  
  public void init() {
    setBackground(Color.red);
    imageLabel1a =
      new ImageLabel("file:///" + System.getProperty("user.dir")
			+ "/images/" + "sendfile.gif");

    imageLabel1b =
      new ImageLabel("file:///" + System.getProperty("user.dir")
			+ "/images/" + "question.gif");

    imageLabel1c =
      new ImageLabel("file:///" + System.getProperty("user.dir")
			+ "/images/" + "hand.gif");

    imageLabel1b.margin = 5;
    imageLabel1b.setSize(200, 200);

    imageLabel1c.margin = 1;
    imageLabel1c.marginColor = Color.blue;

    add(imageLabel1a);
    add(imageLabel1b);
    add(imageLabel1c);
  }

  //--------------------------------------------------------------

  public static void main(String[] args) {
    Frame mainFrame = new Frame("TestImageLabel");
    TestImageLabel app = new TestImageLabel();
    app.init();
    mainFrame.setSize(500,400);
    mainFrame.add("Center", app);
    mainFrame.setVisible(true);
    mainFrame.repaint();
  }
  
} 

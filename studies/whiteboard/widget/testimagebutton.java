//*************************************************************************
/*
 * TestImageButton.java - A test for the image button class.
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
import java.awt.event.*;      // for event processing

import widget.ImageButton;

public class TestImageButton extends Applet implements ActionListener {

  Button darkenButton, lightenButton;
  ImageButton imageButton1a, imageButton1b, imageButton1c;

  /** Set up the buttons */
  public void init() {

    darkenButton = new Button("Darken Borders");
    add(darkenButton);
    darkenButton.addActionListener(this);
    darkenButton.setBounds(10, 10, 100, 30);

    lightenButton = new Button("Lighten Borders");
    add(lightenButton);
    lightenButton.addActionListener(this);

    ImageButton.setActionListener(this);

    imageButton1a =
      new ImageButton("file:///" + System.getProperty("user.dir")
			+ "/images/" + "copy.gif");

    imageButton1b =
      new ImageButton("file:///" + System.getProperty("user.dir")
			+ "/images/" + "question.gif");
    imageButton1b.setSize(100, 100);

    imageButton1c =
      new ImageButton("file:///" + System.getProperty("user.dir")
			+ "/images/" + "sendfile.gif");
    imageButton1c.setBorderWidth(3);

    add(imageButton1a);  
    add(imageButton1b);
    add(imageButton1c);

  }

  private int gray = 130;

  //-----------------------------------------------------------------
  public void actionPerformed (ActionEvent event) {

    System.out.println( event.getSource() );

    if (event.getSource() == darkenButton) {
      System.out.println("darkenButton");
      gray = gray - 25;
      if (gray < 0) gray = 0;
      setColors(gray);
      paintComponents(getGraphics());
      return;

    } else if (event.getSource() == lightenButton) {
      System.out.println("lightenButton");
      gray = gray + 25;
      if (gray > 255) gray = 255;
      setColors(gray);
      paintComponents(getGraphics());
      return;

    } else {
      System.out.println("Container got ACTION_EVENT for button");
      System.out.println(" showing " +
			 ((ImageButton)event.getSource()).imageString + ".");
      return;
    }

  }

  //-----------------------------------------------------------------

  /** Draw a mesh.  Just for fun. */
  public void paint(Graphics g) {
    g.setColor(Color.yellow);
    for (int i=10; i<500; i=i+10)
      g.drawLine(i, 0, i, 400);
    for (int i=10; i<400; i=i+10)
      g.drawLine(0, i, 500, i);
  }

  //-----------------------------------------------------------------

  private void setColors(int gray) {
    System.out.println("Gray level=" + gray);
    Color c = new Color(gray, gray, gray);
    imageButton1a.setBorderColor(c);
    imageButton1b.setBorderColor(c);
    imageButton1c.setBorderColor(c);
  }
  
  //--------------------------------------------------------------
  // This main function allows this applet be run as an application.

  public static void main(String[] args) {
    Frame mainFrame = new Frame("TestImageButton");
    TestImageButton app = new TestImageButton();
    app.init();
    mainFrame.setSize(500,400);
    mainFrame.add("Center", app);
    mainFrame.setVisible(true);
  }
  
  //--------------------------------------------------------------
} 

//*************************************************************************
/*
 * ColorChooser.java - A class for choosing colors.
 *
 *   This file should be under $CLASSPATH/widget
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package widget;  

// standard java class libraries
import java.awt.*;
import java.awt.event.*;

// local class libraries

/** 
 * A simple class for choosing colors.  It triggers an action event
 * so that the selected color can be retrieved.
 *
 * Currently it uses a simple approach by extending Choice.  One may
 * build more graphic oriented color chooser.
 *
 * Example of usage:
 * <PRE>
 * Inside an AWT component class -
 *   colorchooser = new ColorChooser();
 *   add(colorchooser);
 *   colorchooser.addActionListener(this);
 *
 * Also in the same class -
 *    public void actionPerformed (ActionEvent e) {
 *      if (e.getSource() == colorchooser) {
 *        ...do something
 *      }
 *    }
 * </PRE>
 */
public class ColorChooser extends Choice {

  private Color color;  // The chosen color

  private Color[] colors = { Color.red, Color.green, Color.blue, Color.black,
			   Color.magenta, Color.orange, Color.yellow, 
			   Color.lightGray, Color.white };

  private String[] colornames = { "red", "green", "blue", "black",
			   "magenta", "orange", "yellow", 
			   "gray", "white" };

  public ColorChooser () {

    // Enable event processing
    enableEvents(AWTEvent.ITEM_EVENT_MASK);

    // Initialize the color choices
    for (int i=0; i<colornames.length; i++) {
      this.addItem(colornames[i]);
    }
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color c) {
    int i=0;

    for (i=0; i<colors.length; i++) {
      if ( colors[i].equals(c) ) break;
    }
    if (i>=colors.length) {
      System.err.println("ColorChooser: No such color.");
      i = 0;
    }
    select(i);

  }

  protected void processItemEvent(ItemEvent e) {
    // do this before trigger event processing in "super"!
    this.color = colors[this.getSelectedIndex()];

    super.processItemEvent(e);
  }

  /** A small test frame. */
  static public void main(String[] args) {
    Frame f = new Frame();
    f.add(new ColorChooser());
    f.pack();
    f.setVisible(true);
  }

}

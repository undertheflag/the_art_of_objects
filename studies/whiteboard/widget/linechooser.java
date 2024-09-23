//*************************************************************************
/*
 * LineChooser.java - A class for choosing lines.
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

// local class libraries

/** 
 * A simple class for choosing lines.  It triggers an action event
 * so that the selected line can be retrieved.
 *
 * Currently it uses a simple approach by extending Choice.  One may
 * build more graphic oriented line chooser.
 *
 * Example of usage:<PRE>
 * Inside an AWT component class -
 *    linechooser = new LineChooser();
 *    add(linechooser);
 *    linechooser.addActionListener(this);
 *
 * In the same class -
 *    public void actionPerformed (ActionEvent e) {
 *      if (e.getSource() == linechooser) {
 *        ...do something
 *      }
 *    }
 * </PRE>
 */
public class LineChooser extends Choice {

  private int[] lineWidths = { 1, 2, 3, 4, 5, 6, 7, 8, 9};

  public LineChooser () {

    String s = new String();
    // Initialize the line choice.
    for (int i=0; i<lineWidths.length; i++) {
      this.addItem(s.valueOf(lineWidths[i]));
    }

  }

  /** set line Widths to one in the list.  */
  public void setLineWidth(int lw) {
    int i;
    for (i=0; i<lineWidths.length; i++) {
      if (lw == lineWidths[i]) break;
    }
    if (i>=lineWidths.length) {
      System.err.println("LineChooser: No such line width.");
      i = 0;
    }
    select(i);
  }

  /** Get the selected line width. */
  public int getLineWidth() {
    return lineWidths[getSelectedIndex()];
  }

  /** A small test frame. */
  static public void main(String[] args) {
    Frame f = new Frame();
    f.add(new LineChooser());
    f.pack();
    f.setVisible(true);
  }


}

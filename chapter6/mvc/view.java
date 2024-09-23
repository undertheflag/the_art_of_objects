//*************************************************************************
/**
 * View.java - a view class that displays the data model
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A view class that displays the data model.
 *
 */
public class View extends JFrame {

  ListModel model;

  public View(ListModel model) {

    super("View for Persons");

    this.model = model;

    addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent e) {System.exit(0);}
    } );

    JPanel jp = new JPanel();
    getContentPane().add(jp);
    jp.setLayout(new BorderLayout());

    //create list using SOrted data model
    JList nList = new JList(model);
    JScrollPane sc = new JScrollPane();

    sc.getViewport().setView(nList);
    jp.add ("Center", sc);

    getContentPane().add(jp);
  }

}

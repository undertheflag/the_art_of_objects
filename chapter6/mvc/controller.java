//*************************************************************************
/**
 * Controller.java - a control class that manages the data model
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
 * A control class that manages the data model.  
 * This is part of the Model-View-Controller demonstration.
 *
 */
public class Controller implements ActionListener {

  /** the data model this object controls. */
  private Model model;

  /** an input text field */
  private JTextField text = new JTextField(12);

  /** a timer for automatic shut down */
  private Timer timer;

  // some test data
  String persons[] = { "John - 20", "Mary - 18",
      "Adam - 53", "Edward - 33", "Bob - 44" };
  int index = 0;

  /**
   * Contructs this object.
   *
   * @param model	the data model
   */
  public Controller(Model model) {
    this.model = model;

    // a Timer to shut down the application
    timer = new Timer( 8000,   // 8 secs

      // an unnamed inner class instance to handle
      // ActionEvent from javax.swing.Timer
      new java.awt.event.ActionListener() {
	public void actionPerformed(java.awt.event.ActionEvent e) {
	  System.exit(0);
	}
      }

    );
    timer.start();

  }

  /**
   * Performs action on events from the button.
   *
   * @param evt		the ActionEvent
   */
  public void actionPerformed(ActionEvent evt) {
    String name = "";
    String age = "0";

    String s = text.getText();

    // use text input if not empty
    if(s.equals("") && index < persons.length) {
      s = persons[index++];
    }

    // parse out the name and age
    if( ! s.equals("") ) {
      int i = s.indexOf("-");
      if (i < 0) {
        name = s;
      } else {
        name = s.substring(0, i).trim();
	age = s.substring(i+1).trim();
      }
      if (age.equals("")) age = "0";
      Person p = new Person(name, Integer.valueOf(age).intValue());
      model.addElement(p);
    }

    // reset the timer
    timer.restart();
  }

  /**
   * Main method.
   * 
   */
  static public void main(String argv[]) {

    // the model of data 
    Model model = new Model();

    // the views
    View v1 = new View(model);
    View v2 = new View(model);

    // the control
    Controller c = new Controller(model);


    // set the views' properties

    v1.setSize(new Dimension(300,200));
    v1.setLocation(200,200);
    v1.setVisible(true);

    v2.setSize(new Dimension(300,200));
    v2.setLocation(500,200);
    v2.setVisible(true);


    // A text field and a button for control.  The control object listens to
    // action events from the button.

    JButton add = new JButton("Add");
    add.addActionListener(c);

    JPanel button = new JPanel();
    button.add("South", add);

    JPanel jp = new JPanel();
    jp.setLayout(new BorderLayout());

    jp.add("North", c.text);
    jp.add("Center", button);

    // a frame for control
    JFrame f = new JFrame("Persons");

    // make it a listner of window events
    f.addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent e) {System.exit(0);}
    } );

    f.getContentPane().add(jp);
    f.setSize(new Dimension(200,85));
    f.setLocation(400,420);
    f.setVisible(true);

  }
}

//*************************************************************************
/*
 * Main.java - Main program for whiteboard.
 *
 *   This file should be under $CLASSPATH/wb
 *   To run:   java wb.Main
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package wb;  

// standard java class libraries
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

// local class libraries - they let the compiler know what to compile
import port.ImagePort;      // for image conversion and external links

import widget.DrawCanvas;   // Major canvas for drawing
import widget.ImageButton;  // Button with image
import widget.LineChooser;  // Line
import widget.ColorChooser; // Color
import widget.FontChooser;  // Font

import graph.Glyph;         // define basic glyphs 
import graph.G2dTool;       // 2D graphing tool
import graph.TextLine;      // A line of parsed text
import graph.Cgm;           // CGM utility

public final class Main extends Frame implements WindowListener {

  // Class variables visible to the package web.
  // They can be referred to as: Main.mb etc.

  /** Debug flag.  Show debug message if > 0. */
  static int debug = 0;

  /** Frame size */
  static final int WIDTH = 640;
  static final int HEIGHT= 550;

  /** Handle to this */
  static Main mThis;

  /** Menubar on top */
  static MainMenuBar mmb;

  /** ButtonPanel next */
  static MainButtonPanel mbp;

  /** DrawCanvas */
  static DrawCanvas dc;

  /** Status bar */
  static StatusBar status;

  /** Full path of base dir (that invokes the code) */
  static String basedir = "file:///" + System.getProperty("user.dir");

  /** File dialog handle */
  static FileDialog fd;

  /** Toolkit handle */
  static Toolkit tk;

  /** Whether this is a standalone whiteboard */
  static String address = "standalone";

  /** A logo for this application */
  static ImageIcon logo = null;

  /** Hanlde for image on DrawCanvas */
  private Image image = null;

  /** A constructor with an initial image file, which can be null. */
  Main(String imgfile) throws Exception {

    super("WhiteBoard - " + address);

    logo = new ImageIcon( basedir + "/images/hand.gif" );

    addWindowListener(this);

    this.setSize(WIDTH, HEIGHT);
    this.setBackground(Color.white);

    // The trick with TextField only works under FlowLayout, which
    // preserves the component size.
    setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));

    // Set some fields;
    fd = new FileDialog(this, "File Dialog");
    tk = getToolkit();

    // main menu bar 
    mmb = new MainMenuBar();
    setMenuBar(mmb);

    mbp = new MainButtonPanel();
    add(mbp);

    // Set draw canvas image
    if (imgfile != null) {

      // Must use new URL approach!  The following won't work.
      //   image = getToolkit().getImage( basedir + "/" + imgfile);
      //   dc = new DrawCanvas( image );
      //

      // This may cause the next image loading to freeze on solaris.
      // So do not start with it for now.  The problem may be caused
      // by poor memory management by the native toolkit in JDK1.1.  
      // The same will happen if tk.getImage is called later. (JDK1.1 BUG?)
      //	- YTLau 4-8-97
      //
      // Works fine on NT JDK 1.2.  Have not yet tested on Solaris. - 12/99

      try {
	URL url = new URL( basedir + "/" + imgfile);
	image = tk.getImage( url );
      } catch (Exception e) {
	e.printStackTrace();
      }
    }

    dc = new DrawCanvas( image );

    // drawing appears only within the set area
    // which can be larger than the parent frame
    dc.setSize(1280, 1024);  

    // this must preceed adding DrawCanvas, or it won't be seen!
    add(dc.textinp);

    add(dc);

    // this somehow does not show up
    status = new StatusBar("...loading resources");
    add(status);

    // Initialize the state (font etc.) for all widgets
    if (mbp != null) {
      Font f = new Font("TimesRoman", Font.BOLD, 16);
      dc.setFont(f);
      mbp.fontchooser.setFont(f);

      Color c = Color.red;
      dc.setColor(c);
      mbp.colorchooser.setColor(c);

      dc.setLineWidth(3);
      mbp.linechooser.setLineWidth(3);
    }

    dc.setType(Glyph.CIRCLE);

    if ( !address.equals("standalone") ) {  // only if defined
      ImagePort.mDebug = debug;  // relay the debug flag
      ImagePort imgp = new ImagePort(address, dc);
      mmb.setImagePort(imgp);
    }

// this does not work?
//    setIconImage(logo.getImage());

    // center the frame
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((screenSize.width - WIDTH)/2, (screenSize.height - HEIGHT)/2);

    setVisible(true);  // make everything visible

    // this must be called after Main's setVisible()!
    dc.textinp.setVisible(false);

    status.setText("Ready");
  }

  /** 
   * Error handling static method.
   *
   * @param s  Error message.
   */
  public static void error(String s) {
    // reformat string for Corba exceptions
    int i2;
    int i1 = s.lastIndexOf("reason=\"");
    if (i1 >= 0) {
      i2 = s.indexOf(";", i1);
      s = s.substring(i1+8, i2-1);
    } else {
      i1 = s.lastIndexOf("message=\"");
      if (i1 >= 0) {
        i2 = s.indexOf(";", i1);
        s = s.substring(i1+9, i2-1);
      }
    }

    // wrap it 
    s = wrapString(s, 60);

    // pop up a dialog
    JOptionPane.showMessageDialog(mThis, s, "Error!", JOptionPane.ERROR_MESSAGE); 
    // System.out.println(s);
  }

  /**
   * Show the input string if mDebug > 0.
   */
  public static void debug(String s) {
    if (debug > 0) System.out.println(s);
  }

  public static String wrapString(String str, int len) {
    StringTokenizer st = new StringTokenizer(str, " ");
    String rStr = "";
    String lineStr = "";         
    while (st.hasMoreTokens()) {
      String aToken = st.nextToken();
      if ((lineStr.length() + aToken.length())> len) {
        rStr += "\n" + lineStr;
	lineStr = aToken + " ";
      }	else lineStr += aToken + " "; 
    }
    rStr += "\n" + lineStr;
    return rStr;
  }

  /**
   * Shutdown the program.
   */
  public void shutdown() {
    try {
      mmb.disconnectImagePort();  // disconnect from group
    } catch (Exception e) {
      error(e.toString());
    }

    setVisible(false);         // hide the Frame
    dispose();      // tell windowing system to free resources
    System.exit(0); // exit
  }

  // ........... Window events definitions start

  // exit when window is closed
  public void windowClosing(WindowEvent event) {
    shutdown();
  }
 
  public void windowClosed(WindowEvent event) {
  }

  public void windowDeiconified(WindowEvent event) {
  }

  public void windowIconified(WindowEvent event) {
  }

  public void windowActivated(WindowEvent event) {
  }

  public void windowDeactivated(WindowEvent event) {
  }

  public void windowOpened(WindowEvent event) {
  }
  // ........... Window events definitions end

  /** 
   * The main function.
   */
  public static void main(String[] args) throws Exception {

    System.out.println("Usage: java wb.Main [image_file] [port_name]");
    System.out.println("   Default port_name = username");

    String imgfile = null;
    if (args.length >= 1) {
      imgfile = args[0];
    }

    // if orb is set up, then this is not standalone.  Set up address.
    if( System.getProperty("org.omg.CORBA.ORBClass") != null ) {

      String portName = null;
      if (args.length >= 2) {
	portName = args[1];
      } else {
	portName = System.getProperty("user.name");  // default to username
      }

      // get the debug flag
      String debugFlag = System.getProperty("debug");
      if (!debugFlag.equals("")) 
        debug = Integer.valueOf(debugFlag).intValue();

      String computerName = System.getProperty("computerName");
      if (computerName == null) computerName = "localhost";

      address = portName + "@" + computerName;
      debug(address);
    }

    mThis = new Main(imgfile);
  }

}

//------------------------------------------------------------------

/** 
 * A menubar encapsulated with ActionListener functions. 
 */
class MainMenuBar extends MenuBar implements ActionListener {

  ImagePort imgp = null;

  /** the path selected */
  static String path;

  /** the dialog for connect / list */
  InfoDialog mInfoDialog = new InfoDialog(Main.mThis, "Group Information","");

  public MainMenuBar () {

    path = System.getProperty("user.dir");  // set to starting dir

    // File
    Menu file = new Menu("File");
    addMenuItem(file, "Open");
    addMenuItem(file, "Save As");
    addMenuItem(file, "Loop Test");
    file.addSeparator();
    addMenuItem(file, "Exit");
    this.add(file);

    // Edit
    Menu edit = new Menu("Edit");
    addMenuItem(edit, "Hand");
    addMenuItem(edit, "Cut");
    addMenuItem(edit, "Copy");
    addMenuItem(edit, "Paste");
    addMenuItem(edit, "Erase Selection");
    addMenuItem(edit, "Erase Markups");
    addMenuItem(edit, "Erase All");
    this.add(edit);

    // Draw
    Menu draw = new Menu("Draw");
    addMenuItem(draw, "Rectangle");
    addMenuItem(draw, "Circle");
    addMenuItem(draw, "Ellipse");
    addMenuItem(draw, "Line");
    addMenuItem(draw, "Text");
    this.add(draw);

    if ( !Main.address.equals("standalone") ) {
      Menu group = new Menu("Group");
      addMenuItem(group, "Connect");
      addMenuItem(group, "Disconnect");
      addMenuItem(group, "Send");
      this.add(group);
    }

    // Help on right
    Menu help = new Menu("Help");
    addMenuItem(help, "About");
    this.add(help);
    this.setHelpMenu(help);

  }

  /** Set the image port attribute. */
  public void setImagePort(ImagePort imgport) {
    imgp = imgport;
  }

  /** Connect to the image port. */
  public void connectImagePort(String address) throws Exception {
    if ( imgp != null ) imgp.connect(address);
  }

  /** Disconnect the image port if it is in a group. */
  public void disconnectImagePort() throws Exception {
    if ( imgp != null && imgp.getNumberOfConnectedPorts() > 0)
      imgp.disconnect();
  }

  /** add menu item and this as listener */
  private void addMenuItem (Menu m, String s) {
    MenuItem i = new MenuItem(s);
    i.addActionListener(this);
    m.add(i);
  }

  public void actionPerformed (ActionEvent e) {
    String s = e.getActionCommand();

    try {

      // File menu
      if ("Open".equals(s)) {
	Main.fd.setMode(FileDialog.LOAD);
	Main.fd.setDirectory(path);
	Main.fd.setVisible(true);
	String file = Main.fd.getFile();
	if (file == null) return;

	// make full path for file
	path = Main.fd.getDirectory();
	file = path.concat(file);
	// System.out.println("file = " + file);

	if (file.endsWith(".jpg") || file.endsWith(".gif") ) {
	  // use toolkit interface
	  try {
	    URL url = new URL("file:///" + file);
	    Image image = Main.tk.getImage( url );
	    Main.dc.setImage(image);

	  } catch (MalformedURLException ex) {
	    ex.printStackTrace();
	  }

	} else if (file.endsWith(".cgm")) {   // read CGM file

	  Main.dc.getGlyphsFrom(file);

	} else if (file.endsWith(".ntf") || file.endsWith(".nitf")
		   || file.endsWith(".tif") ) {

	  if (imgp == null) {
	    Main.error("image port not defined");
	    return;
	  }
	  // use native interface to read
  //	long t1 = System.currentTimeMillis();
	  imgp.read(file);
  //	long t2 = System.currentTimeMillis();
  //	System.out.println( t2-t1 );
	  Main.dc.getGlyphsFrom(imgp);
	  Main.dc.getImageFrom(imgp);

	} else {
	  Main.error("Unrecognized file format.  Suffix must be: ntf, nitf, jpg, gif, cgm");

	}
	return;

      } else if ("Save As".equals(s)) {

	Main.fd.setMode(FileDialog.SAVE);
	Main.fd.setDirectory(path);
	Main.fd.setVisible(true);
	String file = Main.fd.getFile();
	if (file == null) return;  // do nothing

	// make full path for file
	path = Main.fd.getDirectory();
	file = path.concat(file);

	if (file.endsWith(".ntf") || file.endsWith(".nitf")
		   || file.endsWith(".tif") ) {
	  // Formats that need conversion
	  // initialize native interface 

	  if (imgp == null) {
	    Main.error("image port not defined");
	    return;
	  }
	  Main.dc.sendImageTo(imgp);
	  Main.dc.sendGlyphsTo(imgp);
	  imgp.write(file);

	} else if (file.endsWith(".cgm")) {
	  Main.dc.sendGlyphsTo(file);

	} else {
	  Main.error("Unsupported file format.  Suffix must be: cgm, ntf, nitf");
	}

	return;

      } else if ("Loop Test".equals(s)) {

	/* A loop test within java */
	if (imgp == null) {
	  Main.error("image port not defined");
	  return;
	}
	Main.dc.sendImageTo(imgp);
	Main.dc.getImageFrom(imgp);
	
	JOptionPane.showMessageDialog(Main.mThis,
	  "ImagePort loop test successful.",
	  "Test Result", JOptionPane.INFORMATION_MESSAGE); 

	return;

      } else if ("Exit".equals(s)) {
	Main.mThis.shutdown();
      }

      // Edit menu
      if ("Hand".equals(s)) {
	Main.dc.setType(Glyph.CROSSRECT);
	return;
      } else if ("Cut".equals(s)) {
	Main.dc.cut();
	return;
      } else if ("Copy".equals(s)) {
	Main.dc.copy();
	return;
      } else if ("Paste".equals(s)) {
	Main.dc.paste();
	return;
      } else if ("Erase Selection".equals(s)) {
	Main.dc.erase();
	return;
      } else if ("Erase Markups".equals(s)) {
	Main.dc.erase(DrawCanvas.GLYPHS);
	return;
      } else if ("Erase All".equals(s)) {
	Main.dc.erase(DrawCanvas.ALL);     // erase all!
	return;
      }

      // Draw menu
      if ("Rectangle".equals(s)) {
	Main.dc.setType(Glyph.RECT);
	return;
      } else if ("Circle".equals(s)) {
	Main.dc.setType(Glyph.CIRCLE);
      } else if ("Ellipse".equals(s)) {
	Main.dc.setType(Glyph.OVAL);
      } else if ("Line".equals(s)) {
	Main.dc.setType(Glyph.LINE);
      } else if ("Text".equals(s)) {
	Main.dc.setType(Glyph.TEXT);
	return;
      }

      // Group menu
      if ("Connect".equals(s)) {

        String info = imgp.listConnectedPorts();
	mInfoDialog.setInfo(info);
	mInfoDialog.setVisible(true);

      } else if ("Disconnect".equals(s)) {

        try {
          if ( imgp != null ) imgp.disconnect();
	  Main.status.setText("Successfully disconnected");
          JOptionPane.showMessageDialog(null, "Successfully disconnected.",
	    "Confirmation", JOptionPane.INFORMATION_MESSAGE); 

        } catch (Exception ex) {
          Main.error(ex.toString());
        }

      } else if ("Send".equals(s)) {

	Main.dc.sendImageTo(imgp);
	Main.dc.sendGlyphsTo(imgp);
	imgp.sendImage();
	return;

      }

      // Help menu
      if ("About".equals(s)) {

        String message = "Shared Whiteboard 1.0\n"
	  + "Copyright (c) 1999-2000 Tung Lau\n\n";

	JOptionPane.showMessageDialog(Main.mThis, message, 
	  "About Shared Whiteboard", JOptionPane.PLAIN_MESSAGE, Main.logo);

	return;
      } 

    } catch (Exception ex) {
      Main.error(ex.toString());
      // ex.printStackTrace();
    }

  }

}

//------------------------------------------------------------------
/** A panel of buttons for controlling the input object. */
class MainButtonPanel extends Panel implements ActionListener, ItemListener {

  ImageButton send, hand, cut, copy, paste;
  ImageButton line, rect, circle, oval, text;
  LineChooser linechooser;
  ColorChooser colorchooser;
  FontChooser fontchooser;

  public MainButtonPanel () {

    // Set the size
    Dimension size = new Dimension(300, 40);
    setSize(size);
    setBackground(Color.lightGray);

    setLayout(new FlowLayout(FlowLayout.LEFT, 2, 3));

    ImageButton.setActionListener(this);

    String imgdir = Main.basedir + "/images/";

    if ( !Main.address.equals("standalone") ) {  // only if defined
      send = new ImageButton( imgdir + "send.gif");
      send.setSize(24, 24);    add(send);
    }

    hand = new ImageButton( imgdir + "hand.gif");
    hand.setSize(24, 24);    add(hand);

    cut = new ImageButton( imgdir + "cut.gif");
    cut.setSize(24, 24);    add(cut);

    copy = new ImageButton( imgdir + "copy.gif");
    copy.setSize(24, 24);    add(copy);

    paste = new ImageButton( imgdir + "paste.gif");
    paste.setSize(24, 24);    add(paste);

    circle = new ImageButton( imgdir + "circle.gif");
    circle.setSize(24, 24);    add(circle);

    oval = new ImageButton( imgdir + "ellipse.gif");
    oval.setSize(24, 24);    add(oval);

    line = new ImageButton( imgdir + "line.gif");
    line.setSize(24, 24);    add(line);

    rect = new ImageButton( imgdir + "rectangle.gif");
    rect.setSize(24, 24);    add(rect);

    text = new ImageButton( imgdir + "text.gif");
    text.setSize(24, 24);    add(text);

    linechooser = new LineChooser();
    add(linechooser);
    linechooser.addItemListener(this);

    colorchooser = new ColorChooser();
    add(colorchooser);
    colorchooser.addItemListener(this);

    fontchooser = new FontChooser();
    add(fontchooser);
    fontchooser.addItemListener(this);

  }

  /** Draw a border around it.  This is done thru paint since getGraphics
      is null until the code is running. */
  public void paint(Graphics g) {
    super.paint(g);          // Forces main image to get loaded
    draw3DBorder(g, true, 1);
  }

  /** Draw a 3D border to a component
   * @param g The Graphics object.
   * @param raised Whether the border is raised.
   * @param thickness  Pixel thickness of the border.
   */
  private void draw3DBorder(Graphics g, boolean raised, int thickness) {
    g.setColor(getBackground());
    Dimension size = getSize();
    int left = 0;    int top = 0;
    int width = size.width;    int height = size.height;
    for(int i=0; i<thickness; i++) {
      g.draw3DRect(left, top, width, height, raised);
      left++;      top++;
      width -= 2;  height -= 2;
    }
  }

  /** Processes button action */
  public void actionPerformed (ActionEvent e) {

    try {
     if (e.getSource() == send) {
       Main.dc.sendImageTo(Main.mmb.imgp);
       Main.dc.sendGlyphsTo(Main.mmb.imgp);
       Main.mmb.imgp.sendImage();
       return;

     } else if (e.getSource() == hand) {
       Main.dc.setType(Glyph.CROSSRECT);
       return;
     } else if (e.getSource() == cut) {
       Main.dc.cut();
       return;
     } else if (e.getSource() == copy) {
       Main.dc.copy();
       return;
     } else if (e.getSource() == paste) {
       Main.dc.paste();
       return;
     } else if (e.getSource() == rect) {
       Main.dc.setType(Glyph.RECT);
       return;
     } else if (e.getSource() == line) {
       Main.dc.setType(Glyph.LINE);
       return;
     } else if (e.getSource() == circle) {
       Main.dc.setType(Glyph.CIRCLE);
       return;
     } else if (e.getSource() == oval) {
       Main.dc.setType(Glyph.OVAL);
       return;
     } else if (e.getSource() == text) {
       Main.dc.setType(Glyph.TEXT);
       return;
     }

    } catch (Exception ex) {
      Main.error(ex.toString());
    }

   }

  /** Processes item events */
  public void itemStateChanged(ItemEvent e) {

    if (e.getSource() == linechooser) {
       Main.dc.setLineWidth(linechooser.getLineWidth());
       return;
     } else if (e.getSource() == colorchooser) {
       Main.dc.setColor(colorchooser.getColor());
       return;
     } else if (e.getSource() == fontchooser) {
       // Either way will work
       // Main.dc.setFont(fontchooser.getFont());
       Main.dc.setFont((Font)e.getItem());
       return;
     } 
  }

}

//------------------------------------------------------------------
/** A status bar */
class StatusBar extends JLabel {
  public StatusBar(String text) {
    super(text);
    setBorder(new EtchedBorder());
  }

  public Insets getInsets() {
    return new Insets(5, 5, 5, 5);
  }

  public void setText(String text) {
    super.setText(text);

    // In order to update the display of the status text while an event 
    // is being dispatched, we need to call paintImmediately().
    Dimension d = getSize();
    paintImmediately(0, 0, d.width, d.height);
  }
}
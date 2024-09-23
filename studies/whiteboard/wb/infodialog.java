//*************************************************************************
/*
 * InfoDialog.java - A dialog for showing a text area of information.
 *   It also have a input text field and a button below.  The text
 *   area is embedded in a scroll pane.
 *
 *   This file should be under $CLASSPATH/wb
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

package wb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

class InfoDialog extends JDialog {

   private static final int WIDTH = 360;
   private static final int HEIGHT= 300;
   private static final String INPUT_MESSAGE= "(new address)";

   private String mTitle = null;
   private String mInfo = null;

   private JTextArea mText;    // text area for displaying info
   private JTextField mInput;  // text field input

   public InfoDialog(Frame owner, String title, String info) {

      super(owner, title, true);

      mTitle = title;
      mInfo = info;

      // center will expand it
      getContentPane().add("Center", createInfoPanel()); 

      JPanel panel;

      panel = new JPanel();
      panel.add(createInputPanel());
      panel.add(createButtonPanel());

      getContentPane().add("South", panel);

      setSize(WIDTH, HEIGHT);
      setResizable(false);
      setLocationRelativeTo(owner);

      mInput.setText(INPUT_MESSAGE);

   }

   public void setTitle(String title) {
     mTitle = title;
   }

   public void setInfo(String info) {
     mInfo = info;
     mText.setText(mInfo);
   }

   private JScrollPane createInfoPanel() {
      JPanel infoPanel = new JPanel(new BorderLayout(2,2));

      Font f = new Font("TimesRoman", Font.PLAIN, 16);

      mText = new JTextArea(mInfo);
      mText.setLineWrap(true);
      mText.setFont(f);
      mText.setBackground(new Color(250,250,230));
      mText.setEditable(false);

      Dimension d = new Dimension( (int)(WIDTH * 0.9), (int)(HEIGHT * 0.7));
      mText.setSize(d);

      infoPanel.add(mText, BorderLayout.CENTER);

      infoPanel.setBorder(new LineBorder(Color.lightGray, 8));

      JScrollPane scrollPane = new JScrollPane(infoPanel);

      return scrollPane;
   }

   private JPanel createInputPanel() {

      JPanel inputPanel = new JPanel();
      GridBagLayout gbLyt = new GridBagLayout();
      inputPanel.setLayout(gbLyt);

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(2,5,2,5);

      addInputField(gbLyt, inputPanel, gbc);

      return inputPanel;
   }

   private void addInputField(GridBagLayout gbLyt, JPanel inputPanel,
       GridBagConstraints gbc) {

      JLabel inLabel = new JLabel("Add");
      inLabel.setForeground(Color.darkGray);
      gbc.gridx = 1;
      gbc.gridy = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.NONE;
      gbLyt.setConstraints(inLabel, gbc);
      inputPanel.add(inLabel);

      mInput = new JTextField(18);
      mInput.addFocusListener(
        new FocusListener() {  // an inner class to handle event
          public void focusGained(FocusEvent e) {
	    String address = mInput.getText();
	    if ( INPUT_MESSAGE.startsWith(address) ) {
	      mInput.setText("");  // erase it
	      return;
	    }
          }

          public void focusLost(FocusEvent e) {
	  }
        }
      );

      gbc.gridx = 2;
      gbc.gridy = 1;

      // weightx on the last item is needed for proper display
      gbc.weightx = 4;

      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbLyt.setConstraints(mInput, gbc);
      inputPanel.add(mInput);
   }


   private JPanel createButtonPanel() {
      JPanel buttonPanel = new JPanel(new FlowLayout());
      JButton okButton = new JButton("OK");
      okButton.setOpaque(true);
      buttonPanel.add(okButton);

      okButton.addActionListener(
        // an inner class to handle event
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

	    String address = mInput.getText();
	    if (address == null || address.equals("")
	        || INPUT_MESSAGE.startsWith(address) ) {
	      dispose();
	      return;
	    }

	    try {
	      Main.mmb.connectImagePort(address);  // connect to address
	      mInput.setText(INPUT_MESSAGE);  // reset input if successful
	      dispose();
	    } catch (Exception ex) {
	      Main.error(ex.toString());
	    }

          }
        }
      );

      return buttonPanel;
   }

  /** 
   * The main function for testing.
   */
  public static void main(String[] args) throws Exception {
    String s = "The main function for testing.\n  The main function for testing.";
     InfoDialog d = new InfoDialog(null, "Test", s );
     d.setResizable(true);
     d.mText.setEditable(true);
     d.setVisible(true);
     System.exit(0);
  }

}


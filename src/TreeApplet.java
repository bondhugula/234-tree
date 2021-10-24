/*
 * A Java Applet that animates B-Trees
 * Copyright (C) 2002 Uday Bondhugula
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 */

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TreeApplet extends Applet {
  Panel controlPane;
  ButtonGroup cbg;
  JTextField keyField;
  MyCanvas canvas;
  JTextArea displayArea;
  Panel subControlPane1, subControlPane3;
  Thread runner;
  int key;
  JButton nextButton, prevButton;
  Image arrow, treePicture;
  DocPanel docp;

  public void init() {
    MediaTracker tracker = new MediaTracker(this);
    arrow = getImage(getCodeBase(), "ARROW_3D.gif");
    treePicture = getImage(getCodeBase(), "tree-picture.gif");
    tracker.addImage(arrow, 0);
    tracker.addImage(treePicture, 1);
    try {
      showStatus("Loading Images ...");
      tracker.waitForID(0);
      tracker.waitForID(1);
    } catch (InterruptedException e) {
    }
    showStatus("Finished loading images");
    setForeground(Color.black);
    setBackground(Color.white);
    setLayout(new BorderLayout());

    Panel header = new Panel();
    header.setBackground(MyCanvas.gold);
    header.add(new JLabel("2-3-4  Trees"));
    add(header, BorderLayout.NORTH);

    Panel drawingPane = new Panel();
    drawingPane.setLayout(new BorderLayout());
    canvas = new MyCanvas(this);
    drawingPane.add(canvas);
    add(drawingPane, BorderLayout.CENTER);

    controlPane = new Panel();
    controlPane.setLayout(new GridLayout(2, 1));
    controlPane.setForeground(Color.black);
    // controlPane.setBackground(Color.lightGray);

    subControlPane1 = new Panel();
    // subControlPane1.setBackground(Color.lightGray);

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    subControlPane1.setLayout(gridbag);

    c.fill = GridBagConstraints.BOTH;

    c.weightx = 0.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    JLabel mode = new JLabel("Select Mode: ");
    gridbag.setConstraints(mode, c);
    subControlPane1.add(mode);
    cbg = new ButtonGroup();
    makeCheckbox("Single step", gridbag, c);
    makeCheckbox("Run", gridbag, c);
    makeCheckbox("Step by step", gridbag, c);
    c.gridwidth = 1;
    makebutton("Prev step", subControlPane1);
    gridbag.setConstraints(prevButton, c);
    c.gridwidth = GridBagConstraints.REMAINDER;
    makebutton("Next step", subControlPane1);
    gridbag.setConstraints(nextButton, c);

    Panel subControlPane2 = new Panel() {
      public Insets getInsets() {
        return new Insets(25, 0, 0, 0);
      }
    };

    subControlPane2.setLayout(gridbag);
    // subControlPane2.setBackground(Color.lightGray);
    c.weightx = 1.0;
    c.gridwidth = 1;
    JLabel l = new JLabel("  Enter key value: ");
    gridbag.setConstraints(l, c);
    subControlPane2.add(l);
    c.gridwidth = GridBagConstraints.REMAINDER;
    keyField = new JTextField(0);
    keyField.setBackground(Color.white);
    keyField.setForeground(Color.black);
    gridbag.setConstraints(keyField, c);
    subControlPane2.add(keyField);
    gridbag.setConstraints(subControlPane2, c);
    subControlPane1.add(subControlPane2);
    controlPane.add(subControlPane1);

    subControlPane3 = new Panel();
    // subControlPane3.setBackground(Color.lightGray);
    subControlPane3.setLayout(new GridLayout(5, 1));
    makebutton("Insert", subControlPane3);
    makebutton("Delete", subControlPane3);
    makebutton("Search", subControlPane3);
    makebutton("Reset", subControlPane3);
    makebutton("Exit", subControlPane3);

    controlPane.add(subControlPane3);
    controlPane.setSize(100, 800);
    add(controlPane, BorderLayout.EAST);

    Panel display = new Panel();
    display.setLayout(new BorderLayout());
    displayArea = new JTextArea(4, 50);
    displayArea.setEditable(false);
    displayArea.setBackground(new Color(255, 255, 235));
    displayArea.setForeground(Color.black);
    displayArea.setFont(new Font("Helvetica", Font.BOLD, 11));
    JScrollPane jsp = new JScrollPane(displayArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    docp = new DocPanel(displayArea);
    displayArea.setText(docp.whatNext);
    display.add(docp, BorderLayout.WEST);
    display.add(jsp, BorderLayout.CENTER);
    add(display, BorderLayout.SOUTH);
  }

  protected void makebutton(String name, Panel panel) {
    JButton button = new JButton(name);
    button.addActionListener(canvas);
    // button.setBackground(Color.lightGray);
    button.setForeground(Color.black);
    if (name.equals("Prev step")) {
      button.setEnabled(false);
      prevButton = button;
    }
    if (name.equals("Next step")) {
      nextButton = button;
      button.setEnabled(false);
    }
    panel.add(button);
  }

  protected void makeCheckbox(String name, GridBagLayout gridbag, GridBagConstraints c) {
    JRadioButton cbox = new JRadioButton(name, false);
    if (name.equals("Single step"))
      cbox.setSelected(true);
    gridbag.setConstraints(cbox, c);
    // cbox.setBackground(Color.lightGray);
    cbox.setForeground(Color.black);
    cbox.addItemListener(canvas);
    subControlPane1.add(cbox);
    cbg.add(cbox);
  }

  public Insets getInsets() {
    return new Insets(10, 10, 10, 10);
  }

  public void start() {}
  public void stop() {
    if (canvas.runner != null)
      canvas.runner = null;
  }
}

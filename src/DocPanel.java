import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class DocPanel extends Panel implements ItemListener {
  JComboBox doc;
  JTextArea displayArea;

  DocPanel(JTextArea displayArea) {
    this.displayArea = displayArea;
    // setBackground(Color.lightGray);
    setLayout(new GridLayout(3, 1, 0, 5));

    // pull down menu for guidelines
    add(new JLabel("Documentation"));
    String[] docStrings = {"Introduction", "2-Node", "3-Node", "4-Node", "Properties"};
    doc = new JComboBox(docStrings);
    // doc.addItem("Introduction");
    // doc.addItem("2-Node");
    // doc.addItem("3-Node");
    // doc.addItem("4-Node");
    // doc.addItem("Properties");
    // doc.addItem("Run algorithm");
    // doc.addItem("Step algorithm");
    doc.addItemListener(this);
    add(doc);
  }

  public void itemStateChanged(ItemEvent ie) {
    String str = (String) ie.getItem();
    if (str.equals("Introduction"))
      displayArea.setText(tree);
    else if (str.equals("2-Node"))
      displayArea.setText(Twonode);
    else if (str.equals("3-Node"))
      displayArea.setText(Threenode);
    else if (str.equals("2-3-4 Trees"))
      displayArea.setText(tree);
    else if (str.equals("Properties"))
      displayArea.setText(properties);
  }

  final String tree = new String("What is a 2-3-4 tree ?\n"
      + "A 2-3-4 Tree is a special form of a B-Tree. This name derives from the fact that\n"
      + "each internal node has degree two, three or four children. These trees allow\n"
      + "insertion, deletion and searching in O(log n) time.\n");

  final String Twonode = new String("What is a 2-node ?\n"
      + "A degree 2 node is called a 2-Node. It has only one key namely Left Key and\n"
      + "two children namely Left child and Middle child.");

  final String Threenode = new String("What is a 3-node ?\n"
      + "A degree 3 node is called a 3-Node. It has two keys namely Left Key and\n"
      + "Right Key. It has three children namely Left child, Middle child and\n"
      + "Right Child.");

  final String properties = new String("Properties of 2-3-4 Trees:\n"
      + "A 2-3-4 Tree is a search tree that is either empty or satisfies the following 4 properties:\n"
      + "1. Each internal node has two, three and four children only.\n"
      + "2. The height of all the leaves/external nodes is the same.\n"
      + "3. Let Lchild and Mchild denote the children of a 2-Node. Let Lkey be the\n"
      + "   only element present in this node. All elements in the sub 2-3-4 Tree with\n"
      + "   root Lchild have key less than Lkey, while all elements in the sub 2-3-4 Tree\n"
      + "   with root Mchild have key greater than Lkey.\n"
      + "4. Let Lchild, Mchild and Rchild denote the children of a 3-Node. Let Lkey\n"
      + "   and Rkey be the two elements in this node. Then, Lkey < Rkey, all keys\n"
      + "   in the sub 2-3-4 Tree with root Lchild are less than Lkey, all keys in the\n"
      + "   sub 2-3-4 Tree with root Mchild are less than Rkey and greated than Lkey,\n"
      + "   and all keys in the sub 2-3-4 Tree with root Rchild are greater than Rkey.\n"
      + "5. All external/leaf nodes are at the same level.");

  final String whatNext = "1. INSERT: To insert a key, click on the Insert button.\n"
      + "2. DELETE: To delete a key, click on the Delete button.\n"
      + "3. SEARCH: To search for a key, click on the Search button.\n";
  final String invalid = "Invalid key input:\n"
      + "You have entered an invalid key value. Only two digit numbers are valid key values.";
}

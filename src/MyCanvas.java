import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* This listens for all GUI events */
public class MyCanvas extends Canvas
    implements ActionListener, ItemListener, MouseMotionListener, MouseListener, Runnable {
  public int WIDTH;
  public int HEIGHT;
  public TwoThreeFourTree T;
  public static final int KEY_WIDTH = 20;
  public static final int NODE_HEIGHT = 17;
  private static final int VERTICAL_SPACING = 70;
  public static final Color gold = new Color(255, 215, 0);
  private static final int OFFSET = 8; // creates spacing between leaves to prevent overlapping
  private final static int RUN = 0;
  private final static int SINGLE_STEP = 1;
  private final static int STEP_BY_STEP = 2;
  private int mode;
  public final static int INSERT = 1;
  public final static int DELETE = 2;
  public final static int SEARCH = 3;
  public int OPERATION;
  private Font f;
  private Point currentDragPosition;
  public Thread runner;
  private boolean blockMouseDragging = false;
  public TreeApplet applet;
  private Thread animator;
  private boolean animate = false;

  MyCanvas(TreeApplet applet) {
    super();
    this.applet = applet;
    T = new TwoThreeFourTree(this);
    mode = SINGLE_STEP;
    setBackground(Color.white);
    f = new Font("TimesRoman", Font.PLAIN, 14);
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  Image animatedImage;
  String text;

  public void paint(Graphics screen) {
    WIDTH = getSize().width;
    HEIGHT = getSize().height;

    if (T.root.key[0] != -1) {
      if (!animate) {
        Image bufferImage = createImage(WIDTH, HEIGHT);
        Graphics offscreen = bufferImage.getGraphics();
        offscreen.drawImage(applet.treePicture, 0, 0, 100, 100, null);
        if (runner == null)
          setCo_ordinates(T.root, WIDTH / 2, 40);
        drawTree(offscreen, T.root);
        screen.drawImage(bufferImage, 0, 0, null);
        String text;
        if (!(text = applet.displayArea.getText()).equals(applet.docp.whatNext))
          applet.displayArea.setText(applet.docp.whatNext);
        offscreen.dispose();
        applet.keyField.requestFocus();
      } else if (animatedImage != null) {
        screen.drawImage(animatedImage, 0, 0, null);
        applet.displayArea.setText(text);
      }
    } else {
      screen.setColor(Color.white);
      screen.fillRect(0, 0, WIDTH, HEIGHT);
    }
  }

  public void update(Graphics screen) {
    paint(screen);
  }

  public void setCo_ordinates(Node node, int x, int y) {
    if (node == T.root)
      T.root.level = 0;
    int level = node.level;
    node.x = x;
    node.y = y;
    int NODE_WIDTH = KEY_WIDTH * node.n;

    if (!node.leaf) {
      for (int j = 0; j < node.n + 1; j++) {
        int childX, childY;
        if (node.c[j].leaf) {
          childX = x - (int) ((VERTICAL_SPACING - 3) / Math.tan((j + 1) * Math.PI / (node.n + 2)));
          if (level >= 1)
            childY = y + VERTICAL_SPACING + OFFSET * (j % 2);
          else
            childY = y + VERTICAL_SPACING;
        } else {
          childX = x
              - (int) ((VERTICAL_SPACING + 160 - 50 * Math.pow(2, level))
                  / Math.tan((j + 1) * Math.PI / (node.n + 2)));
          childY = y + VERTICAL_SPACING;
        }
        node.c[j].level = node.level + 1;
        setCo_ordinates(node.c[j], childX, childY);
      }
    }
  }

  public void drawTree(Graphics offscreen, Node node) {
    int NODE_WIDTH = KEY_WIDTH * node.n;
    Point corner = new Point(node.x - NODE_WIDTH / 2, node.y);

    if (node == T.root) {
      offscreen.setFont(f);
      offscreen.drawLine(WIDTH / 2, 0, T.root.x, T.root.y);
    }

    offscreen.setColor(Color.black);
    offscreen.draw3DRect(corner.x, corner.y, NODE_WIDTH, NODE_HEIGHT, true);
    offscreen.setColor(gold);
    offscreen.fill3DRect(corner.x + 1, corner.y + 1, NODE_WIDTH - 1, NODE_HEIGHT - 1, true);
    offscreen.setColor(Color.black);

    for (int i = 1; i <= node.n - 1; i++)
      offscreen.drawLine(corner.x + NODE_WIDTH / node.n * i, corner.y,
          corner.x + NODE_WIDTH / node.n * i, corner.y + NODE_HEIGHT);
    for (int i = 1; i <= node.n; i++) {
      String key = Integer.toString(node.key[i - 1]);
      if (key.length() == 1)
        key = ' ' + key;
      offscreen.drawString(key, corner.x + NODE_WIDTH / node.n * (i - 1) + 3, corner.y + 13);
    }

    if (!node.leaf) {
      for (int j = 0; j < node.n + 1; j++) {
        drawTree(offscreen, node.c[j]);
        drawLineToChild(offscreen, node, j);
      }
    }
  }

  public void drawLineToChild(Graphics offscreen, Node parent, int j) {
    Node child = parent.c[j];
    int x1 = parent.x - parent.n * KEY_WIDTH / 2 + j * KEY_WIDTH;
    int y1 = parent.y + NODE_HEIGHT;
    int x2 = child.x;
    int y2 = child.y;
    offscreen.drawLine(x1, y1, x2, y2);
  }

  public void run() { // Thread which runs whenever the mouse is being dragged
    boolean vicinity = false;
    Node node = null;
    Point previousPosition = null;

    while (runner != null) {
      Node[] nodearray = T.toArray();
      if (!vicinity) {
        int i = 0;
        for (i = 0; i < nodearray.length; i++) {
          node = nodearray[i];
          if (Math.abs(currentDragPosition.x - node.x) < KEY_WIDTH * node.n / 2
              && currentDragPosition.y - node.y < NODE_HEIGHT && currentDragPosition.y - node.y > 0)
            break;
        }
        if (i != nodearray.length)
          vicinity = true;
        else
          vicinity = false;
      }
      if (vicinity && (previousPosition != null) && !currentDragPosition.equals(previousPosition)) {
        setCo_ordinates(node, currentDragPosition.x, currentDragPosition.y);
        repaint();
      }
      previousPosition = currentDragPosition;
    }
  }

  public void itemStateChanged(ItemEvent ie) {
    if (((JRadioButton) (ie.getItem())).getText().equals("Step by step"))
      mode = STEP_BY_STEP;
    else if (((JRadioButton) ie.getItem()).getText().equals("Run"))
      mode = RUN;
    else if (((JRadioButton) ie.getItem()).getText().equals("Single step"))
      mode = SINGLE_STEP;
  }

  int stepCount = 0;
  Display[] buffer = new Display[10];
  public void actionPerformed(ActionEvent ae) {
    try {
      if (applet.keyField.getText().length() > 2)
        throw new NumberFormatException();
      if (ae.getActionCommand().equals("Insert") && !animate) {
        if (mode == SINGLE_STEP) {
          T.INSERT(Integer.parseInt(applet.keyField.getText()));
          applet.keyField.setText("");
          repaint();
        } else {
          OPERATION = INSERT;
          animate = true;
          if (mode == RUN)
            new RunAnimator(Integer.parseInt(applet.keyField.getText()), this);
          else {
            buffer = T.INSERT_ANIMATED(Integer.parseInt(applet.keyField.getText()));
            stepCount = 0;
            applet.nextButton.setEnabled(true);
            animate = true;
            animatedImage = buffer[0].im;
            text = buffer[0].text;
            if (buffer[stepCount + 1] == null)
              applet.nextButton.setLabel("Finish");
            repaint();
          }
          applet.keyField.setText("");
        }
      } else if (ae.getActionCommand().equals("Delete") && !animate) {
        if (mode == SINGLE_STEP) {
          T.DELETE(T.root, Integer.parseInt(applet.keyField.getText()));
          applet.keyField.setText("");
          repaint();
        } else {
          OPERATION = DELETE;
          animate = true;
          if (mode == RUN)
            new RunAnimator(Integer.parseInt(applet.keyField.getText()), this);
          else {
            buffer = T.DELETE_ANIMATED(T.root, Integer.parseInt(applet.keyField.getText()));
            stepCount = 0;
            applet.nextButton.setEnabled(true);
            animate = true;
            animatedImage = buffer[0].im;
            text = buffer[0].text;
            if (buffer[stepCount + 1] == null)
              applet.nextButton.setLabel("Finish");
            repaint();
          }
          applet.keyField.setText("");
        }
      } else if (ae.getActionCommand().equals("Search")) {
        if (mode == SINGLE_STEP) {
          Pair result;
          result = T.SEARCH(T.root, Integer.parseInt(applet.keyField.getText()));
          if (result != null) {
            applet.displayArea.setText(
                "Key " + ((Node) result.obj).key[result.value - 1] + " found!");
          } else {
            applet.displayArea.setText(
                "Search failed: key " + ((Node) result.obj).key[result.value - 1] + " not found!");
          }
          applet.keyField.setText("");
        } else {
          OPERATION = SEARCH;
          animate = true;
          if (mode == RUN)
            animator = new RunAnimator(Integer.parseInt(applet.keyField.getText()), this);
          else {
            buffer = T.SEARCH_ANIMATED(T.root, Integer.parseInt(applet.keyField.getText()));
            stepCount = 0;
            applet.nextButton.setEnabled(true);
            animate = true;
            animatedImage = buffer[0].im;
            text = buffer[0].text;
            if (buffer[stepCount + 1] == null)
              applet.nextButton.setLabel("Finish");
            repaint();
          }
          applet.keyField.setText("");
        }
      } else if (ae.getActionCommand().equals("Prev step")) {
        if (stepCount == 1)
          applet.prevButton.setEnabled(false);
        animatedImage = buffer[--stepCount].im;
        text = buffer[stepCount].text;
        if (applet.nextButton.getLabel().equals("Finish"))
          applet.nextButton.setLabel("Next step");
        repaint();
      } else if (ae.getActionCommand().equals("Next step")) {
        animatedImage = buffer[++stepCount].im;
        text = buffer[stepCount].text;
        if (stepCount == 1)
          applet.prevButton.setEnabled(true);
        repaint();
        if (buffer[stepCount + 1] == null)
          applet.nextButton.setLabel("Finish");
      } else if (ae.getActionCommand().equals("Finish")) {
        animate = false;
        applet.prevButton.setEnabled(false);
        applet.nextButton.setEnabled(false);
        applet.nextButton.setLabel("Next step");
        repaint();
        applet.keyField.requestFocus();
        stepCount = 0;
      } else if (ae.getActionCommand().equals("Reset")) {
        animate = false;
        if (animator != null && animator.isAlive()) {
          animate = false;
          animator.interrupt();
        }
        System.runFinalization();
        T = new TwoThreeFourTree(this);
        applet.keyField.setText("");
        applet.keyField.requestFocus();
        repaint();
      }
    } catch (NumberFormatException e) {
      applet.displayArea.setText(applet.docp.invalid);
      applet.keyField.setText("");
      return;
    }
  }

  class RunAnimator extends Thread {
    MyCanvas canvas;
    Display[] buffer;

    RunAnimator(int key, MyCanvas canvas) {
      this.canvas = canvas;
      switch (OPERATION) {
        case INSERT:
          buffer = T.INSERT_ANIMATED(key);
          break;
        case DELETE:
          buffer = T.DELETE_ANIMATED(T.root, key);
          break;
        case SEARCH:
          buffer = T.SEARCH_ANIMATED(T.root, key);
          break;
      }
      start();
    }

    public void run() {
      try {
        for (int i = 0; buffer[i] != null; i++) {
          animatedImage = buffer[i].im;
          text = buffer[i].text;
          repaint();
          Thread.sleep(1000);
        }
        animate = false;
        repaint();
      } catch (InterruptedException e) {
      }
    }
  }

  int i = 0;
  public void mouseDragged(MouseEvent e) {
    currentDragPosition = e.getPoint();
    if (i == 0 && !blockMouseDragging) {
      runner = new Thread(this);
      runner.start();
    }
    i++;
  }

  public void mouseReleased(MouseEvent e) {
    runner = null;
    i = 0;
  }

  public void mouseMoved(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
}

import java.awt.*;
import java.util.Vector;

public class TwoThreeFourTree {
  public Node root;
  private MyCanvas canvas;

  TwoThreeFourTree(MyCanvas canvas) {
    root = new Node();
    root.leaf = true;
    root.n = 0;
    root.key[0] = -1;
    this.canvas = canvas;
  }

  public void INSERT(int k) {
    Node x = root;
    if (x.n == 3) {
      Node s = new Node();
      root = s;
      s.n = 0;
      s.leaf = false;
      s.c[0] = x;
      SPLIT_CHILD(s, 1, x);
      INSERT_NONFULL(s, k);
    } else
      INSERT_NONFULL(x, k);
  }

  public void INSERT_NONFULL(Node x, int k) {
    int i = x.n;
    if (x.leaf) {
      while (i >= 1 && k < x.key[i - 1]) {
        x.key[i] = x.key[i - 1];
        i--;
      }
      x.key[i] = k;
      x.n++;
    } else {
      while (i >= 1 && k < x.key[i - 1]) i--;
      i++;
      if (x.c[i - 1].n == 3) {
        SPLIT_CHILD(x, i, x.c[i - 1]);
        if (k > x.key[i - 1])
          i++;
      }
      INSERT_NONFULL(x.c[i - 1], k);
    }
  }

  public void SPLIT_CHILD(Node x, int i, Node y) {
    Node z = new Node();
    z.leaf = y.leaf;
    z.n = 1;
    z.key[0] = y.key[2];

    if (!y.leaf) {
      z.c[1] = y.c[3];
      z.c[0] = y.c[2];
    }
    y.n = 1;

    for (int j = x.n + 1; j >= i + 1; j--) {
      x.c[j] = x.c[j - 1];
      x.key[j - 1] = x.key[j - 2];
    }
    x.c[i] = z;
    x.key[i - 1] = y.key[1];
    x.n++;
  }

  Display[] buffer = new Display[20];
  int imageCount = 0;
  Graphics g;
  int key;

  public Display[] INSERT_ANIMATED(int k) {
    imageCount = 0;
    this.key = k;

    Node x = root;
    if (x.n == 3) {
      drawTreeImage(root);
      encircleNode(root, true);
      buffer[imageCount++].text = "Since the root is full, Node splitting takes place at the root";
      Node s = new Node();
      root = s;
      s.n = 0;
      s.leaf = false;
      s.c[0] = x;
      SPLIT_CHILD(s, 1, x);
      INSERT_NONFULL_ANIMATED(s, k);
    } else
      INSERT_NONFULL_ANIMATED(x, k);
    g.dispose();
    imageCount = 0;
    return buffer;
  }

  public void INSERT_NONFULL_ANIMATED(Node x, int k) {
    drawTreeImage(x);
    buffer[imageCount].text =
        "The key should be inserted into the subtree with the encircled node as the root\n";
    int i = x.n;
    if (x.leaf) {
      while (i >= 1 && k < x.key[i - 1]) {
        x.key[i] = x.key[i - 1];
        i--;
      }
      x.key[i] = k;
      x.n++;
    } else {
      while (i >= 1 && k < x.key[i - 1]) i--;
      i++;
      showArrow(x.c[i - 1]);
      if (x.c[i - 1].n == 3) {
        encircleNode(x.c[i - 1], true);
        buffer[imageCount++].text +=
            "\nThe key should next be inserted into the subtree with the encircled node as the "
            + "root\n"
            + "Since the encircled node is full, node splitting takes place here";
        SPLIT_CHILD(x, i, x.c[i - 1]);
        if (k > x.key[i - 1])
          i++;
        drawTreeImage(x);
        buffer[imageCount].text = "The full node has been split";
        showArrow(x.c[i - 1]);
      }
      imageCount++;
      INSERT_NONFULL_ANIMATED(x.c[i - 1], k);
    }
  }

  public Pair SEARCH(Node x, int k) {
    int i = 1;
    while (i <= x.n && k > x.key[i - 1]) i++;
    if (i <= x.n && k == x.key[i - 1])
      return new Pair(x, i);
    if (!x.leaf)
      return SEARCH(x.c[i - 1], k);
    return null;
  }

  public Display[] SEARCH_ANIMATED(Node x, int k) {
    if (x == root) {
      imageCount = 0;
      this.key = k;
    }
    drawTreeImage(x);
    encircleNode(x, true);
    pointToKey(x, 0);
    imageCount++;
    int i;
    pointToKey(x, 0);
    for (i = 1; i <= x.n && k > x.key[i - 1]; i++) {
      if (i < x.n) {
        drawTreeImage(x);
        encircleNode(x, true);
        pointToKey(x, i);
        imageCount++;
      }
    }
    if (i <= x.n && k == x.key[i - 1])
      ;
    else if (!x.leaf)
      SEARCH_ANIMATED(x.c[i - 1], k);
    g.dispose();
    return buffer;
  }

  public boolean DELETE(Node x, int k) {
    System.out.println("Calling delete on Node " + x.key[0] + " on " + k);
    int i = 1;
    while (i <= x.n && k > x.key[i - 1]) i++;
    if (i <= x.n && k == x.key[i - 1]) {
      if (x.leaf) {
        for (int c = i; c <= x.n - 1; c++) x.key[c - 1] = x.key[c];
        x.n--;
        if (root.n == 0)
          root = root.c[0];
        return true;
      } else if (x.c[i - 1].n >= 2) {
        Node y = PREDECESSOR(x, i);
        x.key[i - 1] = y.key[y.n - 1];
        return DELETE(y, y.key[y.n - 1]);
      } else if (x.c[i].n >= 2) {
        Node y = SUCCESSOR(x, i);
        x.key[i - 1] = y.key[0];
        return DELETE(y, y.key[0]);
      } else if (x.c[i - 1].n < 2 && x.c[i].n < 2) {
        System.out.println("executing");
        x.c[i - 1].n += 2;
        x.c[i - 1].key[1] = x.key[i - 1];
        x.c[i - 1].key[2] = x.c[i].key[0];
        if (!x.c[i].leaf) {
          x.c[i - 1].c[2] = x.c[i].c[0];
          x.c[i - 1].c[3] = x.c[i].c[1];
        }
        for (int j = i; j <= x.n - 1; j++) {
          x.key[j - 1] = x.key[j];
          x.c[j] = x.c[j + 1];
        }
        x.n--;
        return DELETE(x.c[i - 1], k);
      }
    }
    if (!x.leaf) {
      if (x.c[i - 1].n >= 2)
        return DELETE(x.c[i - 1], k);
      else {
        int m = i - 1;
        while (++m <= x.n && x.c[m].n == 1)
          ;
        if (m == x.n + 1) {
          System.out.println("Here 1");
          m = i - 1;
          while (--m >= 0 && x.c[m].n == 1)
            ;
          if (m == -1) {
            System.out.println("Here 2");
            if (i != x.n + 1) {
              x.c[i - 1].key[1] = x.key[i - 1];
              x.c[i - 1].key[2] = x.c[i].key[0];
              x.c[i - 1].c[2] = x.c[i].c[0];
              x.c[i - 1].c[3] = x.c[i].c[1];
              x.c[i - 1].n += 2;
              for (int l = i - 1; l < x.n - 1; l++) {
                x.key[l] = x.key[l + 1];
                x.c[l + 1] = x.c[l + 2];
              }
              x.n--;
              return DELETE(x.c[i - 1], k);
            } else {
              x.c[i - 2].key[1] = x.key[i - 2];
              x.c[i - 2].key[2] = x.c[i - 1].key[0];
              x.c[i - 2].c[2] = x.c[i - 1].c[0];
              x.c[i - 2].c[3] = x.c[i - 1].c[1];
              x.c[i - 2].n += 2;
              x.n--;
              return DELETE(x.c[i - 2], k);
            }
          } else {
            int j = i;
            do {
              System.out.println("Left");
              for (int l = 0; l <= x.c[j - 1].n - 1; l++) x.c[j - 1].key[l + 1] = x.c[j - 1].key[l];
              if (!x.c[i - 1].leaf)
                for (int l = 0; l <= x.c[j - 1].n - 1; l++) x.c[j - 1].c[l + 1] = x.c[j - 1].c[l];
              x.c[j - 1].key[0] = x.key[j - 2];
              x.key[j - 2] = x.c[j - 2].key[x.c[j - 2].n - 1];
              if (!x.c[i - 1].leaf)
                x.c[j - 1].c[0] = x.c[j - 2].c[x.c[j - 2].n];
              x.c[j - 1].n++;
              x.c[j - 2].n--;
              System.out.println(x.c[j - 1].key[0]);
              System.out.println(x.c[j - 1].key[1]);
            } while (x.c[j - 2].n < 1 && --j - 2 >= 0);
            return DELETE(x.c[i - 1], k);
          }
        } else {
          int j = i;
          do {
            System.out.println("Right");
            x.c[j - 1].key[x.c[j - 1].n] = x.key[j - 1];
            x.key[j - 1] = x.c[j].key[0];
            if (!x.c[i - 1].leaf)
              x.c[j - 1].c[x.c[j - 1].n + 1] = x.c[j].c[0];
            x.c[j - 1].n++;
            for (int l = 0; l < x.c[j].n - 1; l++) x.c[j].key[l] = x.c[j].key[l + 1];
            if (!x.c[i - 1].leaf)
              for (int l = 0; l < x.c[j].n; l++) x.c[j].c[l] = x.c[j].c[l + 1];
            x.c[j].n--;
          } while (x.c[j].n < 1 && ++j <= x.n + 1);
          return DELETE(x.c[i - 1], k);
        }
      }
    } else
      return false;
  }

  public Display[] DELETE_ANIMATED(Node x, int k) {
    System.out.println("Calling delete on Node " + x.key[0] + " on " + k);
    int i = 1;
    while (i <= x.n && k > x.key[i - 1]) {
      drawTreeImage(x);
      pointToKey(x, i - 1);
      imageCount++;
      i++;
    }
    if (i <= x.n && k == x.key[i - 1]) {
      if (x.leaf) {
        drawTreeImage(x);
        pointToKey(x, i - 1);
        for (int c = i; c <= x.n - 1; c++) x.key[c - 1] = x.key[c];
        x.n--;
        if (root.n == 0)
          root = root.c[0];
        g.dispose();
        imageCount = 0;
        return buffer;
      } else if (x.c[i - 1].n >= 2) {
        Node y = PREDECESSOR(x, i);
        x.key[i - 1] = y.key[y.n - 1];
        return DELETE_ANIMATED(y, y.key[y.n - 1]);
      } else if (x.c[i].n >= 2) {
        Node y = SUCCESSOR(x, i);
        x.key[i - 1] = y.key[0];
        return DELETE_ANIMATED(y, y.key[0]);
      } else if (x.c[i - 1].n < 2 && x.c[i].n < 2) {
        System.out.println("executing");
        x.c[i - 1].n += 2;
        x.c[i - 1].key[1] = x.key[i - 1];
        x.c[i - 1].key[2] = x.c[i].key[0];
        if (!x.c[i].leaf) {
          x.c[i - 1].c[2] = x.c[i].c[0];
          x.c[i - 1].c[3] = x.c[i].c[1];
        }
        for (int j = i; j <= x.n - 1; j++) {
          x.key[j - 1] = x.key[j];
          x.c[j] = x.c[j + 1];
        }
        x.n--;
        return DELETE_ANIMATED(x.c[i - 1], k);
      }
    }
    if (!x.leaf) {
      if (x.c[i - 1].n >= 2)
        return DELETE_ANIMATED(x.c[i - 1], k);
      else {
        int m = i - 1;
        while (++m <= x.n && x.c[m].n == 1)
          ;
        if (m == x.n + 1) {
          System.out.println("Here 1");
          m = i - 1;
          while (--m >= 0 && x.c[m].n == 1)
            ;
          if (m == -1) {
            System.out.println("Here 2");
            if (i != x.n + 1) {
              x.c[i - 1].key[1] = x.key[i - 1];
              x.c[i - 1].key[2] = x.c[i].key[0];
              x.c[i - 1].c[2] = x.c[i].c[0];
              x.c[i - 1].c[3] = x.c[i].c[1];
              x.c[i - 1].n += 2;
              for (int l = i - 1; l < x.n - 1; l++) {
                x.key[l] = x.key[l + 1];
                x.c[l + 1] = x.c[l + 2];
              }
              x.n--;
              return DELETE_ANIMATED(x.c[i - 1], k);
            } else {
              x.c[i - 2].key[1] = x.key[i - 2];
              x.c[i - 2].key[2] = x.c[i - 1].key[0];
              x.c[i - 2].c[2] = x.c[i - 1].c[0];
              x.c[i - 2].c[3] = x.c[i - 1].c[1];
              x.c[i - 2].n += 2;
              x.n--;
              return DELETE_ANIMATED(x.c[i - 2], k);
            }
          } else {
            int j = i;
            do {
              System.out.println("Left");
              for (int l = 0; l <= x.c[j - 1].n - 1; l++) x.c[j - 1].key[l + 1] = x.c[j - 1].key[l];
              if (!x.c[i - 1].leaf)
                for (int l = 0; l <= x.c[j - 1].n - 1; l++) x.c[j - 1].c[l + 1] = x.c[j - 1].c[l];
              x.c[j - 1].key[0] = x.key[j - 2];
              x.key[j - 2] = x.c[j - 2].key[x.c[j - 2].n - 1];
              if (!x.c[i - 1].leaf)
                x.c[j - 1].c[0] = x.c[j - 2].c[x.c[j - 2].n];
              x.c[j - 1].n++;
              x.c[j - 2].n--;
              System.out.println(x.c[j - 1].key[0]);
              System.out.println(x.c[j - 1].key[1]);
            } while (x.c[j - 2].n < 1 && --j - 2 >= 0);
            return DELETE_ANIMATED(x.c[i - 1], k);
          }
        } else {
          int j = i;
          do {
            System.out.println("Right");
            x.c[j - 1].key[x.c[j - 1].n] = x.key[j - 1];
            x.key[j - 1] = x.c[j].key[0];
            if (!x.c[i - 1].leaf)
              x.c[j - 1].c[x.c[j - 1].n + 1] = x.c[j].c[0];
            x.c[j - 1].n++;
            for (int l = 0; l < x.c[j].n - 1; l++) x.c[j].key[l] = x.c[j].key[l + 1];
            if (!x.c[i - 1].leaf)
              for (int l = 0; l < x.c[j].n; l++) x.c[j].c[l] = x.c[j].c[l + 1];
            x.c[j].n--;
          } while (x.c[j].n < 1 && ++j <= x.n + 1);
          return DELETE_ANIMATED(x.c[i - 1], k);
        }
      }
    } else {
      g.dispose();
      imageCount = 0;
      return buffer;
    }
  }

  public Node PREDECESSOR(Node x, int i) {
    Node child = x.c[i - 1];
    while (!child.leaf) child = child.c[child.n];
    return child;
  }

  public Node SUCCESSOR(Node x, int i) {
    Node child = x.c[i];
    while (!child.leaf) child = child.c[0];
    return child;
  }

  public void drawTreeImage(Node highlight) {
    buffer[imageCount] = new Display();
    buffer[imageCount + 1] = null;
    buffer[imageCount].im = canvas.createImage(canvas.WIDTH, canvas.HEIGHT);
    if (g != null)
      g.dispose();
    g = buffer[imageCount].im.getGraphics();

    if (canvas.OPERATION != canvas.SEARCH && canvas.OPERATION != canvas.DELETE) {
      canvas.setCo_ordinates(root, canvas.WIDTH / 2, 40);
      g.draw3DRect(highlight.x - 25, highlight.y - 25, 20, 20, true);
      g.setColor(canvas.gold);
      g.fill3DRect(highlight.x - 25 + 1, highlight.y - 25 + 1, 19, 19, true);
      g.setColor(Color.black);
      g.drawString(Integer.toString(key), highlight.x - 22, highlight.y - 12);
    }
    canvas.drawTree(g, root);
    encircleNode(highlight, false);
  }

  public void showArrow(Node node) {
    g.drawImage(canvas.applet.arrow, node.x - 10, node.y - 25, 20, 20, null);
  }

  public void pointToKey(Node node, int i) {
    int NODE_WIDTH = canvas.KEY_WIDTH * node.n;
    int x = node.x - NODE_WIDTH / 2 + canvas.KEY_WIDTH * i;
    int y = node.y - 15;
    g.drawImage(canvas.applet.arrow, x, y, canvas.KEY_WIDTH, 15, null);
    if (canvas.OPERATION == canvas.SEARCH) {
      g.draw3DRect(x - 3, y - 22, canvas.KEY_WIDTH + 6, 20, true);
      g.setColor(canvas.gold);
      g.fill3DRect(x - 3 + 1, y - 22 + 1, canvas.KEY_WIDTH + 5, 19, true);
      g.setColor(Color.black);
      g.drawString(Integer.toString(key) + '?', x, y - 10);
    }
  }

  public void encircleNode(Node node, boolean full) {
    int x = node.x;
    int y = node.y;
    int NODE_WIDTH = MyCanvas.KEY_WIDTH * node.n;
    if (!full) {
      g.setColor(Color.red);
      g.drawOval(
          node.x - NODE_WIDTH / 2 - 20, node.y - 10, NODE_WIDTH + 40, MyCanvas.NODE_HEIGHT + 20);
    } else {
      g.setColor(Color.blue);
      g.drawOval(
          node.x - NODE_WIDTH / 2 - 20, node.y - 10, NODE_WIDTH + 40, MyCanvas.NODE_HEIGHT + 20);
    }
  }

  public Node[] toArray() {
    Vector list = new Vector();
    addSubTree(root, list);
    Node[] array = new Node[list.size()];
    for (int i = 0; i < list.size(); i++) array[i] = (Node) list.elementAt(i);
    return array;
  }

  public void addSubTree(Node node, Vector list) {
    list.addElement(node);
    if (!node.leaf)
      for (int i = 0; i <= node.n; i++) addSubTree(node.c[i], list);
  }
}

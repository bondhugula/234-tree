/*
 * A single node of the 234-tree
 */
public class Node {
  public int n;
  public int key[];
  public Node c[];
  public boolean leaf;
  public int level;
  public int x;
  public int y;

  Node() {
    c = new Node[4];
    key = new int[3];
  }
}

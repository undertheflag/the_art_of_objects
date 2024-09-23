//*************************************************************************
/**
 * Node.java - a node class with bi-directional links.
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class demonstrates a self association with arbitrary multiplicity
 * on both ends, i.e., a network of nodes.
 * 
 */
public class Node {

  /** Name of the node */
  private String name;

  /** Use vectors to contain the nodes.
   */
  private Vector upstreamNodes = new Vector();
  private Vector downstreamNodes = new Vector();

  /**
   * Constructs this node.
   */
  public Node(String name) {
    this.name = name;
  }

  /** 
   * Returns the name of this node.
   *
   * @return the name of this node
   */
  public String getName() { 
    return name; 
  }

  /**
   * Returns the number of upstream nodes to this node.
   *
   * @return 	the number of upstream nodes
   */
  public int getUpstreamNodeCount() {
    return upstreamNodes.size();
  }

  /**
   * Returns the number of downstream nodes to this node.
   *
   * @return 	the number of downstream nodes
   */
  public int getDownstreamNodeCount() {
    return downstreamNodes.size();
  }

  /**
   * Returns an array of upstream nodes.
   *
   * @return  an array of upstream nodes
   */
  public Node[] getUpstreamNodes() {
    // forces the array elements to be of type Hobby
    return (Node[]) upstreamNodes.toArray(new Node[0]);
  }

  /**
   * Returns an array of downstream nodes.
   *
   * @return  an array of downstream nodes
   */
  public Node[] getDownstreamNodes() {
    // forces the array elements to be of type Hobby
    return (Node[]) downstreamNodes.toArray(new Node[0]);
  }

  /**
   * Returns the upstream node with the input name.
   *
   * @param name 	name for the upstream node
   * @return the node.  Null if none matched the name.
   */
  public Node getUpstreamNode(String name) {
    Enumeration e = upstreamNodes.elements();
    while (e.hasMoreElements()) {
      Node o = (Node) e.nextElement();
      if (o.getName().equals(name)) return o;
    }
    return null;
  }

  /**
   * Returns the downstream node with the input name.
   *
   * @param name 	name for the downstream node
   * @return the node.  Null if none matched the name.
   */
  public Node getDownstreamNode(String name) {
    Enumeration e = downstreamNodes.elements();
    while (e.hasMoreElements()) {
      Node o = (Node) e.nextElement();
      if (o.getName().equals(name)) return o;
    }
    return null;
  }

  /**
   * Returns whether this node upstream to the input node.
   * This is a recursive operation.  It goes up to all upstream
   * node and returns true if any one matches the input node.
   *
   * @param node 	a node
   * @return true if the input node is upstream to this node.  
   *         False otherwise.
   */
  public boolean isUpstreamTo(Node node) {
    boolean r = false;
    Enumeration e = downstreamNodes.elements();
    while (e.hasMoreElements()) {
      Node o = (Node) e.nextElement();
      if (o.equals(node)) r = true;
      else r = o.isUpstreamTo(node);  // go down recursively
      if (r == true) return true;
      // else continue to next downstream node
    }
    return false;
  }

  /**
   * Adds a new upstream node.
   * <P>
   * This is a core operation for referential integrity.
   *
   * @param node 	an upstream node
   */
  public void addUpstreamNode(Node node) {
    upstreamNodes.addElement(node);
    // backward link for referential integrity
    node.downstreamNodes.addElement(this); 
  }

  /**
   * Adds a new downstream node.
   * <P>
   * This is a core operation for referential integrity.
   *
   * @param node 	an downstream node
   */
  public void addDownstreamNode(Node node) {
    downstreamNodes.addElement(node);
    // backward link for referential integrity
    node.upstreamNodes.addElement(this); 
  }

  /**
   * Removes the input upstream node from this node.
   * <P>
   * This is a core operation for referential integrity.
   *
   * @param node	the upstream node to be removed
   * @exception Exception if the node is not found
   */
  public void removeUpstreamNode(Node node) throws Exception {
    if (! upstreamNodes.removeElement(node) )
      throw new Exception("Upstream Node not found: " + node.getName());
    // remove backward link for referential integrity
    node.downstreamNodes.removeElement(this); 
  }

  /**
   * Removes the input downstream node from this node.
   * <P>
   * This is a core operation for referential integrity.
   *
   * @param node	the downstream node to be removed
   * @exception Exception if the node is not found
   */
  public void removeDownstreamNode(Node node) throws Exception {
    if (! downstreamNodes.removeElement(node) )
      throw new Exception("Downstream Node not found: " + node.getName());
    // remove backward link for referential integrity
    node.upstreamNodes.removeElement(this); 
  }

  /**
   * Removes the named upstream node from this node.
   *
   * @param name	name of the upstream node
   * @exception Exception if the node is not found
   */
  public void removeUpstreamNode(String name) throws Exception {
    /* get the Node */
    Node node = getUpstreamNode(name);
    if (node == null) throw new Exception("Upstream Node not found: " + name);
    /* Remove the node using the core operation */
    removeUpstreamNode(node);
  }

  /**
   * Removes the named downstream node from this node.
   *
   * @param name	name of the downstream node
   * @exception Exception if the node is not found
   */
  public void removeDownstreamNode(String name) throws Exception {
    /* get the Node */
    Node node = getDownstreamNode(name);
    if (node == null) throw new Exception("Downstream Node not found: " + name);
    /* Remove the node using the core operation */
    removeDownstreamNode(node);
  }

  /**
   * Removes all nodes connected to this node.
   *
   */
  public void removeAllNode( ) {
    // Note: the following two lines are not enough since we must
    //   preserve referential integrity.
    // upstreamNodes.removeAllElements();
    // downstreamNodes.removeAllElements();

    Enumeration e;
    try {
      e = upstreamNodes.elements();
      while (e.hasMoreElements()) {
	Node node = (Node) e.nextElement();
	removeUpstreamNode(node);
      }

      e = downstreamNodes.elements();
      while (e.hasMoreElements()) {
	Node node = (Node) e.nextElement();
	removeDownstreamNode(node);
      }

    } catch (Exception ex) {
      // the code should never get here
    }
  }

  /** 
   * Returns the information of this node and its down/up-stream
   * nodes as a string.  This is a recursively operation.
   * 
   * @param down	whether to go downstream or upstream
   * @param lead	the leading string
   * @return the information of this node as a string
   */
  public String toString(boolean down, String lead) { 
    String s = lead + name + "\n";
    Node[] nodes;

    // we use the get operation here just to test it
    if (down) nodes = getDownstreamNodes();
    else nodes = getUpstreamNodes();

    for (int i=0; i<nodes.length; i++) {
      s += nodes[i].toString(down, lead+lead);
    }

    return s;
  }

  /** 
   * Shows the input string.
   *
   * @s the string to be shown
   */
  public static void show(String s) { 
    System.out.println(s);
  }


  /**
   * A test that returns a string.  Note it is a static method.
   *
   * @return result of the test as a string
   * @exception Exception if any exception is thrown
   */
  public static String test() throws Exception {
    String s = "";

    Node macao = new Node("Macao");
    Node tokyo = new Node("Tokyo");
    Node sf = new Node("San Francisco");
    Node was = new Node("Washington");
    Node hk = new Node("Hong Kong");
    Node chi = new Node("Chicago");
    Node la = new Node("Los Angeles");

    // set up the network
    macao.addDownstreamNode(tokyo);
    macao.addDownstreamNode(hk);
    macao.addDownstreamNode(sf);

    tokyo.addDownstreamNode(sf);

    hk.addDownstreamNode(sf);
    hk.addDownstreamNode(chi);
    hk.addDownstreamNode(la);

    was.addUpstreamNode(la);
    was.addUpstreamNode(sf);
    was.addUpstreamNode(chi);

    // test the boolean operation
    if ( macao.isUpstreamTo(was) ) {
      s += "Macao is upstream to Washington\n";
    } else {
      s += "Macao is NOT upstream to Washington\n";
    }

    if ( tokyo.isUpstreamTo(la) ) {
      s += "Tokyo is upstream to Los Angeles\n";
    } else {
      s += "Tokyo is NOT upstream to Los Angeles\n";
    }

    s += "All downstream nodes from " + macao.getName() + ":\n";
    s += macao.toString(true, "  ");

    // Make some changes 
    chi.removeUpstreamNode("Hong Kong");
    macao.removeDownstreamNode("Tokyo");

    s += "\n... after the changes\n";
    s += "  ... going down:\n";
    s += macao.toString(true, "  ");

    s += "  ... going up:\n";
    s += was.toString(false, "  ");

    return s;
  }

  /**
   * Main method for testing.
   * 
   * @exception Exception if any exception is thrown
   */
  public static void main(String argv[]) throws Exception {
    show(test());
  }

}

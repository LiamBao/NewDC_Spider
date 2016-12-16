package com.cic.datacrawl.core.browser.entity;

import java.util.Vector;

import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;

/**
 * XmlNode所在节点的路径
 */
public class NodePath {
  public NodePath(String[] nodeNames, int[] nodeIndexs) {
    this(nodeNames, nodeIndexs, 0, nodeNames.length);
  }
  
  private NodePath(String[] nodeNames, int[] nodeIndexs, int offset, int length) {
    this.nodeNames = nodeNames;
    this.nodeSameTagIdxs = nodeIndexs;
    this.offset = offset;
    this.length = length;
  }
  
  public NodePath(String[] nodeNames) {
    this.nodeNames = nodeNames;
    this.offset = 0;
    this.length = nodeNames.length;

    this.nodeSameTagIdxs = new int[length];
    for (int i=0; i < length; i++)
      nodeSameTagIdxs[i] = -1;
  }
  
  private String[] nodeNames;
  private int[] nodeSameTagIdxs; // -1 表示仅一个
  private int offset, length;
  
  public int length() {
    return length;
  }
  
  public String nodeName(int index) {
    return nodeNames[offset+index];
  }
  
  public int nodeSameTagIdx(int index) {
    return nodeSameTagIdxs[offset+index];
  }
  
  public String nodeNameIdx(int index) {
    String tName = nodeNames[offset+index];
    int tIdx = nodeSameTagIdxs[offset+index];
    if (tIdx < 0) return tName;
    else return tName +'['+tIdx+']';
  }
  
  public NodePath nextPath() {
    if (length == 1)
      return null;
    return new NodePath(nodeNames, nodeSameTagIdxs, offset+1, this.length-1);
  }
  
  public NodePath nextPath(int nextN) {
    if (length <= nextN)
      return null;
    return new NodePath(nodeNames, nodeSameTagIdxs, offset+nextN, this.length-nextN);
  }
  
  public NodePath subPath(int subLength) {
    if (length <= subLength)
      return null;
    return new NodePath(nodeNames, nodeSameTagIdxs, offset, subLength);
  }
  
  public boolean isParentOf(NodePath childPath) {
    if (childPath.length <=  this.length)
      return false;
    for (int i=0; i < this.length; i++) {
      if (! this.nodeName(i).equals(childPath.nodeName(i)))
        return false;
      if (this.nodeSameTagIdx(i) != childPath.nodeSameTagIdx(i))
        return false;
    }
    return true;
  }
  
  public boolean isChildOf(NodePath parentPath) {
    return parentPath.isParentOf(this);
  }
  
  public static NodePath w3cNodePath(Node node) {
    Vector<String> nodeNames = new Vector<String>();
    Vector<Integer> nodeIndexs = new Vector<Integer>();
    nodeNames.insertElementAt(node.getNodeName(), 0);
    nodeIndexs.insertElementAt(nodeSameTagIdx(node), 0);
    while (true) {
      node = node.getParentNode();
      if (node == null) break;
      nodeNames.insertElementAt(node.getNodeName(), 0);
      nodeIndexs.insertElementAt(nodeSameTagIdx(node), 0);
    }
    
    String[] nodeNamesA = new String[nodeNames.size()];
    int[] nodeIndexsA = new int[nodeIndexs.size()];
    for (int i=0; i < nodeNamesA.length; i++) {
      nodeNamesA[i] = nodeNames.get(i);
      nodeIndexsA[i] = nodeIndexs.get(i);
    }
    return new NodePath(nodeNamesA, nodeIndexsA);
  }
  
  private static int nodeSameTagIdx(Node currNode) {
    String nodeTag = currNode.getNodeName();
    
    int prevCount = 0;
    Node node = currNode;
    while (true) {
      node = node.getPreviousSibling();
      if (node == null)
        break;
      if (nodeTag.equals(node.getNodeName()))
        prevCount++;
    }
    
    int nextCount = 0;
    node = currNode;
    while (true) {
      node = node.getNextSibling();
      if (node == null)
        break;
      if (nodeTag.equals(node.getNodeName()))
        nextCount++;
    }

    if ((prevCount == 0) && (nextCount == 0))
      return -1;
    return prevCount;
  }
  
  public static NodePath domNodePath(nsIDOMNode node) {
    Vector<String> nodeNames = new Vector<String>();
    Vector<Integer> nodeIndexs = new Vector<Integer>();
    nodeNames.insertElementAt(node.getNodeName().toUpperCase(), 0);
    nodeIndexs.insertElementAt(nodeSameTagIdx(node), 0);
    while (true) {
      node = node.getParentNode();
      if (node == null) break;
      if ("#document".equals(node.getNodeName())) break; //**
      
      nodeNames.insertElementAt(node.getNodeName().toUpperCase(), 0);
      nodeIndexs.insertElementAt(nodeSameTagIdx(node), 0);
    }
    
    String[] nodeNamesA = new String[nodeNames.size()];
    int[] nodeIndexsA = new int[nodeIndexs.size()];
    for (int i=0; i < nodeNamesA.length; i++) {
      nodeNamesA[i] = nodeNames.get(i);
      nodeIndexsA[i] = nodeIndexs.get(i);
      
      //当有多个HTML节点时,仅取其中一个非空HTML节点
      if ((i == 0) && ("HTML".equals(nodeNamesA[i])))
        nodeIndexsA[i] = -1;
    }
    return new NodePath(nodeNamesA, nodeIndexsA);
  }
  
  private static int nodeSameTagIdx(nsIDOMNode currNode) {
    String nodeTag = currNode.getNodeName();
    
    int prevCount = 0;
    nsIDOMNode node = currNode;
    while (true) {
      node = node.getPreviousSibling();
      if (node == null)
        break;
      if (nodeTag.equals(node.getNodeName()))
        prevCount++;
    }
    
    int nextCount = 0;
    node = currNode;
    while (true) {
      node = node.getNextSibling();
      if (node == null)
        break;
      if (nodeTag.equals(node.getNodeName()))
        nextCount++;
    }

    if ((prevCount == 0) && (nextCount == 0))
      return -1;
    return prevCount;
  }
  
  public boolean equals(NodePath anotherPath) {
    if (anotherPath == null)
      return false;
    if (anotherPath.length != this.length)
      return false;
    for (int i=0; i < length; i++) {
      if (anotherPath.nodeSameTagIdx(i) != this.nodeSameTagIdx(i))
        return false;
      if (! anotherPath.nodeName(i).equals(this.nodeName(i)))
        return false;
    }
    return true;
  }
  
  public boolean equals(String[] anotherPath) {
    return equals(new NodePath(anotherPath));
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof NodePath)
      return equals((NodePath)obj);
    else if (obj instanceof String[])
      return equals((String[])obj);
    else return false;
  }
  
  public String toString() {
    StringBuilder ts = new StringBuilder();
    for (int i=0; i < length; i++) {
      if (i > 0) ts.append('.');
      ts.append(nodeNameIdx(i));
    }
    return ts.toString();
  }
}

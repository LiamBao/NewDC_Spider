package com.cic.datacrawl.ui.beans;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.cic.datacrawl.core.browser.entity.NodePath;

public class XmlTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = -6499475446112555191L;

	public XmlTreeNode() {
		super();
	}

	private String nodeName,  nodeValue;
	private XmlTreeNode[] attributeNodes;
	

	private String nodeId, nodeHtmlName, nodeClass,nodeTitle;

	private int nodeSameTagIdx; // -1 表示仅一个
	private int txtOffset, txtLength; // 节点在字符串中的位置

	public String toString() {
		StringBuilder ts = new StringBuilder();
		ts.append(nodeName);
		if (nodeSameTagIdx >= 0)
			ts.append("[" + nodeSameTagIdx + "]");
		if (nodeId != null)
			ts.append(" ID:" + nodeId);
		if (nodeClass != null)
			ts.append(" CLASS:" + nodeClass);
		return ts.toString();
	}

	public void calcSameTagIdx() {
		XmlTreeNode parentNode = (XmlTreeNode) getParent();
		if (parentNode == null)
			this.nodeSameTagIdx = -1;
		else {
			int sameTagCount = 0;
			int sameTagIndex = -1;
			int childCount = parentNode.getChildCount();
			for (int i = 0; i < childCount; i++) {
				XmlTreeNode childNode = ((XmlTreeNode) parentNode.getChildAt(i));
				if (childNode.nodeName.equals(this.nodeName)) {
					if (childNode == this)
						sameTagIndex = sameTagCount;
					sameTagCount++;
				}
			}
			if (sameTagCount == 1)
				this.nodeSameTagIdx = -1;
			else
				this.nodeSameTagIdx = sameTagIndex;
		}

		int childCount = this.getChildCount();
		for (int i = 0; i < childCount; i++) {
			XmlTreeNode childNode = ((XmlTreeNode) this.getChildAt(i));
			childNode.calcSameTagIdx();
		}
	}

	public NodePath getNodePath() {
		int n = this.getLevel() + 1;
		String[] nodeNames = new String[n];
		int[] nodeIndexs = new int[n];

		int i = n - 1;
		XmlTreeNode treeNode = this;
		while (treeNode != null) {
			nodeNames[i] = treeNode.nodeName;
			nodeIndexs[i] = treeNode.nodeSameTagIdx;
			i--;
			treeNode = (XmlTreeNode) treeNode.getParent();
		}

		return new NodePath(nodeNames, nodeIndexs);
	}

	public XmlTreeNode findNodePath(NodePath nodePath) {
		String findName = nodePath.nodeName(0);
		int findTagIndex = nodePath.nodeSameTagIdx(0);
		if (nodeName != null && nodeName.equals(findName)
				&& (findTagIndex == nodeSameTagIdx)) {
			NodePath nextPath = nodePath.nextPath();
			if (nextPath == null)
				return this;

			int childCount = this.getChildCount();
			for (int i = 0; i < childCount; i++) {
				XmlTreeNode childNode = ((XmlTreeNode) this.getChildAt(i));
				XmlTreeNode foundNode = childNode.findNodePath(nextPath);
				if (foundNode != null)
					return foundNode;
			}
			return null;
		} else
			return null;
	}

	public TreePath getTreePath() {
		int n = this.getLevel() + 1;
		XmlTreeNode[] nodes = new XmlTreeNode[n];

		int i = n - 1;
		XmlTreeNode treeNode = this;
		while (treeNode != null) {
			nodes[i] = treeNode;
			i--;
			treeNode = (XmlTreeNode) treeNode.getParent();
		}

		return new TreePath(nodes);
	}

	// JTree 相关操作
	public static XmlTreeNode getSelectionNode(JTree tree) {
		return (XmlTreeNode) tree.getSelectionPath().getLastPathComponent();
	}

	public static NodePath getSelectionNodePath(JTree tree) {
		return getSelectionNode(tree).getNodePath();
	}

	public static void setSelectionNodePath(JTree tree, NodePath nodePath) {
		XmlTreeNode rootNode = (XmlTreeNode) tree.getModel().getRoot();
		XmlTreeNode selectNode = rootNode.findNodePath(nodePath);
		if (selectNode != null) {
			tree.setSelectionPath(selectNode.getTreePath());
		}
	}

	// 各属性值get/set方法
	public String getNodeName() {
		return nodeName;
	}



	public int getNodeSameTagIdx() {
		return nodeSameTagIdx;
	}

	public int getTxtOffset() {
		return txtOffset;
	}

	public int getTxtLength() {
		return txtLength;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeId() {
		return nodeId;
	}

	public String getNodeClass() {
		return nodeClass;
	}
	/**
	 * @return the nodeTitle
	 */
	public String getNodeTitle() {
		return nodeTitle;
	}

	/**
	 * @param nodeTitle the nodeTitle to set
	 */
	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeHtmlName() {
		return nodeHtmlName;
	}

	public void setNodeHtmlName(String nodeHtmlName) {
		this.nodeHtmlName = nodeHtmlName;
	}

	public void setNodeClass(String nodeClass) {
		this.nodeClass = nodeClass;
	}

	public void setNodeSameTagIdx(int nodeSameTagIdx) {
		this.nodeSameTagIdx = nodeSameTagIdx;
	}

	public void setTxtOffset(int txtOffset) {
		this.txtOffset = txtOffset;
	}

	public void setTxtLength(int txtLength) {
		this.txtLength = txtLength;
	}
	/**
	 * @return the attributeNodes
	 */
	public XmlTreeNode[] getAttributeNodes() {
		return attributeNodes;
	}

	/**
	 * @param attributeNodes the attributeNodes to set
	 */
	public void setAttributeNodes(XmlTreeNode[] attributeNodes) {
		this.attributeNodes = attributeNodes;
	}

	/**
	 * @return the nodeValue
	 */
	public String getNodeValue() {
		return nodeValue;
	}

	/**
	 * @param nodeValue the nodeValue to set
	 */
	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
	}
}

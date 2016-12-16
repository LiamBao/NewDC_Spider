package com.cic.datacrawl.ui.treecombo;

import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import javax.swing.tree.TreeModel;

class ChildrenEnumeration implements Enumeration {
	TreeModel treeModel;
	Object node;
	int index = -1;
	int depth = 0;

	public ChildrenEnumeration(TreeModel treeModel, Object node) {
		this.treeModel = treeModel;
		this.node = node;
	}

	public boolean hasMoreElements() {
		return index < treeModel.getChildCount(node) - 1;
	}

	public Object nextElement() {
		return treeModel.getChild(node, ++index);
	}
}

class PreorderEnumeration implements Enumeration {
	private TreeModel treeModel;
	protected Stack stack;

	public PreorderEnumeration(TreeModel treeModel) {
		this.treeModel = treeModel;
		Vector v = new Vector(1);
		v.addElement(treeModel.getRoot());
		stack = new Stack();
		stack.push(v.elements());
	}

	public boolean hasMoreElements() {
		return (!stack.empty() && ((Enumeration) stack.peek())
				.hasMoreElements());
	}

	private int depth = 0;

	public Object nextElement() {
		Enumeration enumer = (Enumeration) stack.peek();
		Object node = enumer.nextElement();
		depth = enumer instanceof ChildrenEnumeration ? ((ChildrenEnumeration) enumer).depth
				: 0;
		if (!enumer.hasMoreElements())
			stack.pop();
		ChildrenEnumeration children = new ChildrenEnumeration(treeModel, node);
		children.depth = depth + 1;
		if (children.hasMoreElements()) {
			stack.push(children);
		}
		return node;
	}

	public int getDepth() {
		return depth;
	}

}

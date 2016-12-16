package com.cic.datacrawl.ui.treecombo;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

public class TreeListCellRenderer extends JPanel implements ListCellRenderer {
	private static final JTree tree = new JTree();
	TreeModel treeModel;
	TreeCellRenderer treeRenderer;
	IndentBorder indentBorder = new IndentBorder();

	public TreeListCellRenderer(TreeModel treeModel,
			TreeCellRenderer treeRenderer) {
		this.treeModel = treeModel;
		this.treeRenderer = treeRenderer;
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(indentBorder);
		setOpaque(false);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value == null) { // if selected value is null
			removeAll();
			return this;
		}

		boolean leaf = treeModel.isLeaf(value);
		Component comp = treeRenderer.getTreeCellRendererComponent(tree, value,
				isSelected, true, leaf, index, cellHasFocus);
		removeAll();
		add(comp);

		PreorderEnumeration enumer = new PreorderEnumeration(treeModel);
		for (int i = 0; i <= index; i++)
			enumer.nextElement();
		indentBorder.setDepth(enumer.getDepth());

		return this;
	}
}

class IndentBorder extends EmptyBorder {
	int indent = UIManager.getInt("Tree.leftChildIndent");

	public IndentBorder() {
		super(0, 0, 0, 0);
	}

	public void setDepth(int depth) {
		left = indent * depth;
	}
}

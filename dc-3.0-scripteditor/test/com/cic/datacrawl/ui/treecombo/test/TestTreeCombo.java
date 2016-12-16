package com.cic.datacrawl.ui.treecombo.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.cic.datacrawl.ui.treecombo.TreeListCellRenderer;
import com.cic.datacrawl.ui.treecombo.TreeListModel;

public class TestTreeCombo {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
//		root.setAllowsChildren(true);
		for (int i = 0; i < 5; ++i) {
			DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(
					">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> node_"
							+ i);
			root.add(node1);
//			node1.setAllowsChildren(true);
			for (int j = 0; j < 5; ++j) {
				DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(
						">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> node_"
								+ i + "_" + j);
//				node2.setAllowsChildren(true);
				node1.add(node2);
			}
		}
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		DefaultTreeModel treeComboModel = new DefaultTreeModel(root);
		final JComboBox combox = new JComboBox(new TreeListModel(treeComboModel));
		combox.setRenderer(new TreeListCellRenderer(treeModel,
				new DefaultTreeCellRenderer()));
		
		combox.setUI(new BasicComboBoxUI() {
			protected ComboPopup createPopup() {
				return new BasicComboPopup(combox) {
					protected JList createList() {
						return new JList(comboBox.getModel()) {
							public void processMouseEvent(MouseEvent e) {
								if (e.isControlDown()) {
									e = new MouseEvent((Component) e
											.getSource(), e.getID(), e
											.getWhen(), e.getModifiers()
											^ InputEvent.CTRL_MASK, e.getX(), e
											.getY(), e.getClickCount(), e
											.isPopupTrigger());
								}
								super.processMouseEvent(e);
							}

							public String getToolTipText(MouseEvent event) {
								int index = locationToIndex(event.getPoint());
								if (index != -1) {
									Object value = getModel().getElementAt(
											index);
									ListCellRenderer renderer = getCellRenderer();
									Component rendererComp = renderer
											.getListCellRendererComponent(this,
													value, index, true, false);
									if (rendererComp.getPreferredSize().width > getVisibleRect().width) {
										return value == null ? null : value
												.toString();
									} else {
										return null;
									}
								}
								return null;
							}

							public Point getToolTipLocation(MouseEvent event) {
								int index = locationToIndex(event.getPoint());
								if (index != -1) {
									Rectangle cellBounds = getCellBounds(index,
											index);
									return new Point(cellBounds.x, cellBounds.y);
								}
								return null;
							}
						};
					}
				};
			}
		});
		
		
		combox.setPreferredSize(new Dimension(20, 20));
		combox.setMinimumSize(new Dimension(20, 20));
		JTree tree = new JTree(treeModel);
		
		
		ScrollPane scrollPane = new ScrollPane();
//		scrollPane.add(tree);
		scrollPane.add(new JTextArea());
		frame.setLayout(new BorderLayout());
		frame.add(combox, BorderLayout.NORTH);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.pack();
//		UIManager.installLookAndFeel("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		frame.setVisible(true);
		
	}
}

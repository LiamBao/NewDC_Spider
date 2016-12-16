package com.cic.datacrawl.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class FolderTreeScrollPane extends JScrollPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1055505945597887143L;
	public static final ImageIcon ICON_COMPUTER = new ImageIcon(
			FolderTreeScrollPane.class.getClassLoader().getResource(
					"computer.png"));
	public static final ImageIcon ICON_DISK = new ImageIcon(
			FolderTreeScrollPane.class.getClassLoader()
					.getResource("drive.png"));
	public static final ImageIcon ICON_FOLDER = new ImageIcon(
			FolderTreeScrollPane.class.getClassLoader().getResource(
					"folder_close.png"));
	public static final ImageIcon ICON_EXPANDEDFOLDER = new ImageIcon(
			FolderTreeScrollPane.class.getClassLoader().getResource(
					"Folder_Open.png"));
	protected JTree m_tree;
	protected DefaultTreeModel m_model;

	public FolderTreeScrollPane() {
		super();
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new IconData(
				ICON_COMPUTER, null, "Computer"));
		DefaultMutableTreeNode node;
		File[] roots = File.listRoots();
		for (int k = 0; k < roots.length; k++) {
			node = new DefaultMutableTreeNode(new IconData(ICON_DISK, null,
					new FileNode(roots[k])));
			top.add(node);
			node.add(new DefaultMutableTreeNode(new Boolean(true)));
		}
		m_model = new DefaultTreeModel(top);

		m_tree = new JTree(m_model);
		m_tree.putClientProperty("JTree.lineStyle", "Angled");
		TreeCellRenderer renderer = new IconCellRenderer();
		m_tree.setCellRenderer(renderer);
		m_tree.addTreeExpansionListener(new DirExpansionListener());
		m_tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);		
		m_tree.setShowsRootHandles(true);
		m_tree.setEditable(false);

		getViewport().add(m_tree);

		setVisible(true);
	}

	public void addTreeSelectionListener(TreeSelectionListener listener) {
		m_tree.addTreeSelectionListener(listener);
	}

	DefaultMutableTreeNode getTreeNode(TreePath path) {
		return (DefaultMutableTreeNode) (path.getLastPathComponent());
	}

	FileNode getFileNode(DefaultMutableTreeNode node) {
		if (node == null)
			return null;
		Object obj = node.getUserObject();
		if (obj instanceof IconData)
			obj = ((IconData) obj).getObject();
		if (obj instanceof FileNode)
			return (FileNode) obj;
		else
			return null;
	}

	// Make sure expansion is threaded and updating the tree model
	// only occurs within the event dispatching thread.
	class DirExpansionListener implements TreeExpansionListener {
		public void treeExpanded(TreeExpansionEvent event) {
			final DefaultMutableTreeNode node = getTreeNode(event.getPath());
			final FileNode fnode = getFileNode(node);
			Thread runner = new Thread() {
				@Override
				public void run() {
					if (fnode != null && fnode.expand(node)) {
						Runnable runnable = new Runnable() {
							public void run() {
								m_model.reload(node);
							}
						};
						SwingUtilities.invokeLater(runnable);
					}
				}
			};
			runner.start();
		}

		public void treeCollapsed(TreeExpansionEvent event) {
		}
	}

	public static void main(String argv[]) {
		new FolderTreeScrollPane();
	}

	private String formatPath(String path){
		if(!path.endsWith(File.separator)){
			path = path + File.separator;
		}
		return path;
	}
	
	public void show(String path) {
		path = formatPath(path.toLowerCase());
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)m_model.getRoot();		
		DefaultMutableTreeNode node = getNodeInPath(rootNode,getFileNode(rootNode), path);
		if(node != null){
			TreePath selectPath = new TreePath(node.getPath());
			m_tree.setSelectionPath(selectPath);
			m_tree.scrollPathToVisible(selectPath);
		}
	}

	private DefaultMutableTreeNode getNodeInPath(DefaultMutableTreeNode parent,FileNode parentFileNode,
			String path) {
		if(parent.isRoot()||(parentFileNode != null && parentFileNode.hasSubDirs())){
			m_model.reload(parent);			
			int count = m_model.getChildCount(parent);
			for (int i = 0; i < count; ++i) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_model
						.getChild(parent, i);
				FileNode fileNode = getFileNode(node);
				if(fileNode == null)return null;
				boolean isEqualPath = path.equalsIgnoreCase(formatPath(fileNode.getFile()
						.getAbsolutePath()).toLowerCase());
				if (path.startsWith(formatPath(fileNode.getFile().getAbsolutePath())
						.toLowerCase()) && !isEqualPath ) {
					if(fileNode != null){
						fileNode.expand(node);						
						return getNodeInPath(node, fileNode, path);
					}
				} else if (isEqualPath) {
					return node;
				}
			}
		}
		return null;
	}

}

class IconCellRenderer extends JLabel implements TreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8312059176193984647L;
	protected Color m_textSelectionColor;
	protected Color m_textNonSelectionColor;
	protected Color m_bkSelectionColor;
	protected Color m_bkNonSelectionColor;
	protected Color m_borderSelectionColor;
	protected boolean m_selected;

	public IconCellRenderer() {
		super();
		m_textSelectionColor = UIManager.getColor("Tree.selectionForeground");
		m_textNonSelectionColor = UIManager.getColor("Tree.textForeground");
		m_bkSelectionColor = UIManager.getColor("Tree.selectionBackground");
		m_bkNonSelectionColor = UIManager.getColor("Tree.textBackground");
		m_borderSelectionColor = UIManager
				.getColor("Tree.selectionBorderColor");
		setOpaque(false);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus)

	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object obj = node.getUserObject();
		setText(obj.toString());
		if (obj instanceof Boolean)
			setText("Retrieving data...");
		if (obj instanceof IconData) {
			IconData idata = (IconData) obj;
			if (expanded)
				setIcon(idata.getExpandedIcon());
			else
				setIcon(idata.getIcon());
		} else
			setIcon(null);
		setFont(tree.getFont());
		setForeground(sel ? m_textSelectionColor : m_textNonSelectionColor);
		setBackground(sel ? m_bkSelectionColor : m_bkNonSelectionColor);
		m_selected = sel;
		return this;
	}

	@Override
	public void paintComponent(Graphics g) {
		Color bColor = getBackground();
		Icon icon = getIcon();
		g.setColor(bColor);
		int offset = 0;
		if (icon != null && getText() != null)
			offset = (icon.getIconWidth() + getIconTextGap());
		g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);

		if (m_selected) {
			g.setColor(m_borderSelectionColor);
			g.drawRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
		}
		super.paintComponent(g);
	}
}

class IconData {
	protected Icon m_icon;
	protected Icon m_expandedIcon;
	protected Object m_data;

	public IconData(Icon icon, Object data) {
		m_icon = icon;
		m_expandedIcon = null;
		m_data = data;
	}

	public IconData(Icon icon, Icon expandedIcon, Object data) {
		m_icon = icon;
		m_expandedIcon = expandedIcon;
		m_data = data;
	}

	public Icon getIcon() {
		return m_icon;
	}

	public Icon getExpandedIcon() {
		return m_expandedIcon != null ? m_expandedIcon : m_icon;
	}

	public Object getObject() {
		return m_data;
	}

	@Override
	public String toString() {
		return m_data.toString();
	}
}

class FileNode {
	protected File m_file;

	public FileNode(File file) {
		m_file = file;
	}

	public File getFile() {
		return m_file;
	}

	@Override
	public String toString() {
		return m_file.getName().length() > 0 ? m_file.getName() : m_file
				.getPath();
	}

	public void expand(TreePath path){
		
	}
	
	public boolean expand(DefaultMutableTreeNode parent) {
		if(!((DefaultMutableTreeNode) parent).children().hasMoreElements()){
			return false;
		}
		DefaultMutableTreeNode flag = (DefaultMutableTreeNode) parent
				.getFirstChild();
		if (flag == null) // No flag
			return false;
		Object obj = flag.getUserObject();
		if (!(obj instanceof Boolean))
			return false; // Already expanded
		parent.removeAllChildren(); // Remove Flag
		File[] files = listFiles();
		if (files == null)
			return true;
		Vector<FileNode> v = new Vector<FileNode>();
		for (int k = 0; k < files.length; k++) {
			File f = files[k];
			if (!(f.isDirectory()))
				continue;
			FileNode newNode = new FileNode(f);

			boolean isAdded = false;
			for (int i = 0; i < v.size(); i++) {
				FileNode nd = (FileNode) v.elementAt(i);
				if (newNode.compareTo(nd) < 0) {
					v.insertElementAt(newNode, i);
					isAdded = true;
					break;
				}
			}
			if (!isAdded)
				v.addElement(newNode);
		}
		for (int i = 0; i < v.size(); i++) {
			FileNode nd = (FileNode) v.elementAt(i);
			IconData idata = new IconData(FolderTreeScrollPane.ICON_FOLDER,
					FolderTreeScrollPane.ICON_EXPANDEDFOLDER, nd);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(idata);
			parent.add(node);

			if (nd.hasSubDirs())
				node.add(new DefaultMutableTreeNode(new Boolean(true)));
		}
		return true;
	}

	public boolean hasSubDirs() {
		File[] files = listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		if (files == null || files.length == 0)
			return false;

		return true;
	}

	public int compareTo(FileNode toCompare) {
		return m_file.getAbsolutePath().compareToIgnoreCase(
				toCompare.m_file.getAbsolutePath());
	}

	protected File[] listFiles() {
		return listFiles(null);
	}

	protected File[] listFiles(FileFilter fileFilter) {
		if (!m_file.isDirectory())
			return null;
		try {
			if (fileFilter == null)
				return m_file.listFiles();
			else
				return m_file.listFiles(fileFilter);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Error reading directory "
					+ m_file.getAbsolutePath(), "Warning",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}
	}
}

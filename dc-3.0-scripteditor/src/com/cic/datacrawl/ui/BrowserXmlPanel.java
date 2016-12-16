package com.cic.datacrawl.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.ParserConfigurationException;

import org.fife.ui.rtextarea.RTextScrollPane;
import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.RhinoXmlUtil;
import org.xml.sax.SAXException;

import com.cic.datacrawl.core.browser.AbstractJavaWebBrowser;
import com.cic.datacrawl.core.browser.SwingBrowser;
import com.cic.datacrawl.core.browser.entity.DomNode;
import com.cic.datacrawl.core.browser.entity.IFrameName;
import com.cic.datacrawl.core.browser.entity.NodePath;
import com.cic.datacrawl.core.browser.listener.RobotEditorListener;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.core.util.XMLUtil;
import com.cic.datacrawl.ui.beans.XmlTreeNode;
import com.cic.datacrawl.ui.tools.CommandConstants;
import com.cic.datacrawl.ui.tools.ItemFactory;

public class BrowserXmlPanel extends JPanel implements PreferectLayout {

	private static final long serialVersionUID = 7600479437652609779L;

	// private static final Logger LOG =
	// Logger.getLogger(BrowserXmlPanel.class);

	private JSplitPane jSplitPane = null;
	private JPanel jPanel = null;
	private RTextScrollPane jScrollPane = null;
	private SyntaxEditorPane txtXML = null;
	// private JPanel panXmlTools = null;
	private JPanel panBrowser = null;
	private SwingGui swingGui;

	public BrowserXmlPanel() {
		super();
		initialize();
	}

	public BrowserXmlPanel(SwingGui swingGui) {
		this();
		this.swingGui = swingGui;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(704, 429);
		this.setLayout(new BorderLayout());
		this.add(getJSplitPane(), BorderLayout.CENTER);
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			browserToolbar = new BrowserToolbar(this);
			browserToolbar.setFloatable(false);
			panBrowser = getPanBrowser();
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			p.add(browserToolbar, BorderLayout.SOUTH);
			p.add(panBrowser, BorderLayout.CENTER);
			jPanel = getJPanel();
			jSplitPane.setBottomComponent(jPanel);
			jSplitPane.setTopComponent(p);
			jSplitPane.setOneTouchExpandable(true);

		}
		return jSplitPane;
	}

	private NodePath[] baseElements;

	public void setBaseElements(final XMLObject xmlObject) {
		org.w3c.dom.Node[] w3cNodes = RhinoXmlUtil.toXmlNodes(xmlObject);
		NodePath[] elementsPath = new NodePath[w3cNodes.length];
		for (int i = 0; i < w3cNodes.length; i++)
			elementsPath[i] = NodePath.w3cNodePath(w3cNodes[i]);

		if (baseElements != null)
			browser.highlightElements(baseElements, 0, "baseElementStyle", lastFrameName);

		lastFrameName = frameComboModel.getSelectedIFrameName();
		browser.highlightElements(elementsPath, Config.getInstance().getIntBaseColor(), "baseElementStyle",
									lastFrameName);

		baseElements = elementsPath;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @param parentSplitPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridLayout());

			JPanel p2 = new JPanel();

			p2.setLayout(new BorderLayout());
			// p2.add(getPanXmlTools(), BorderLayout.NORTH);
			p2.add(getJSplitPane1(), BorderLayout.CENTER);
			jPanel.add(p2);
		}
		return jPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new RTextScrollPane(getTxtXML());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes txtXML
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private SyntaxEditorPane getTxtXML() {
		if (txtXML == null) {
			txtXML = new SyntaxEditorPane();
			txtXML.setEditable(false);
			final JPopupMenu popupMenu = new JPopupMenu();

			ActionListener theActionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String cmd = e.getActionCommand();
					if (cmd.equals(CommandConstants.COPY)) {
						txtXML.copy();
					} else if (cmd.equals(CommandConstants.SELECT_ALL)) {
						txtXML.selectAll();
					}
				}
			};

			for (int i = 0; i < CommandConstants.GROUP_MENU_HTML_SOURCE_POPUP.length; ++i) {
				JMenuItem item = ItemFactory.createMenuItem(CommandConstants.GROUP_MENU_HTML_SOURCE_POPUP[i],
															Event.CTRL_MASK);
				item.addActionListener(theActionListener);
				popupMenu.add(item);
			}
			txtXML.addMouseListener(new MouseListener() {
				/**
				 * Checks if the popup menu should be shown.
				 * 
				 * @param b
				 */
				private void checkPopup(MouseEvent e) {
					if (e.isPopupTrigger()) {
						popupMenu.show(txtXML, e.getX(), e.getY());
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					checkPopup(e);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					checkPopup(e);
				}

				@Override
				public void mouseExited(MouseEvent e) {

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						e.consume();
					}
					checkPopup(e);
					txtXML.requestFocus();
					txtXML.getCaret().setVisible(true);
				}
			});
			txtXML.add(popupMenu);

		}
		return txtXML;
	}

	/**
	 * This method initializes panBrowser
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanBrowser() {
		if (panBrowser == null) {
			panBrowser = new JPanel();
			panBrowser.setLayout(new GridBagLayout());
		}
		return panBrowser;
	}

	public void startup() {
		SwingBrowser swingBrowser = new SwingBrowser(panBrowser);
		swingBrowser.setBrowser("", false);
		browser = swingBrowser.getWebBrowser();
		browserToolbar.setBrowser(browser);

		browser.initForRobotEditor(robotEditorListener);

		// txtXML.setContentType("text/xml");
		domTree.setExpandsSelectedPaths(true);
	}

	private AbstractJavaWebBrowser browser;

	public AbstractJavaWebBrowser getBrowser() {
		return browser;
	}

	private IFrameName lastFrameName;
	private RobotEditorListener robotEditorListener = new RobotEditorListener() {

		class HighlightThread implements Runnable {
			private DomNode rootNode;
			private NodePath nodePath;
			private IFrameName frameName;

			Object lock = new Object();

			public void highlight(DomNode rootNode, NodePath nodePath, IFrameName frameName) {

				synchronized (lock) {
					this.rootNode = rootNode;
					this.nodePath = nodePath;
					this.frameName = frameName;
					lock.notifyAll();
				}
			}

			@Override
			public void run() {
				while (true) {
					synchronized (lock) {

						try {
							lock.wait();
						} catch (InterruptedException e) {							
						}
						if (nodePath.equals(new String[] { "HTML" })) {
							return;
						}
						if (nodePath.equals(new String[] { "HTML", "BODY" })) {
							return;
						}
						if (nodePath.equals(selectedNodePath) && frameName.equals(lastFrameName)) {
							return;
						}

						int index = frameComboModel.getIndexOf(frameName);

						frameSelectionCombo.setSelectedIndex(index);
						// changeDomTree(rootNode);
						if (selectedNodePath != null) {
							browser.highlightElement(selectedNodePath, 0, "clickBackup", lastFrameName);
						}
						if (nodePath != null) {
							lastFrameName = frameName;
							browser.highlightElement(nodePath, Config.getInstance().getIntClickColor(),
														"clickBackup", frameName);

							settingSelection = true;
							XmlTreeNode.setSelectionNodePath(domTree, nodePath);

							settingSelection = false;
							onSelectioNodeChanged(1, frameName);
						}
						selectedNodePath = nodePath;
					}

				}

			}

		}

		// private HighlightThread highlightThread = new HighlightThread();
		// private Thread t;

		public void nodeClick(final DomNode rootNode, final NodePath nodePath, final IFrameName frameName) {

			// if (t == null || !t.isAlive()) {
			// t = new Thread(highlightThread);
			// t.setName("Thread_highlight_html_element");
			// t.start();
			// }
			// while (!t.isAlive()) {
			// try {
			// Thread.sleep(100);
			// } catch (InterruptedException e) {
			// }
			// }
			// highlightThread.highlight(rootNode, nodePath, frameName);

			if (nodePath.equals(new String[] { "HTML" })) {
				return;
			}
			if (nodePath.equals(new String[] { "HTML", "BODY" })) {
				return;
			}
			if (nodePath.equals(selectedNodePath) && frameName.equals(lastFrameName)) {
				return;
			}

			int index = frameComboModel.getIndexOf(frameName);

			frameSelectionCombo.setSelectedIndex(index);
			// changeDomTree(rootNode);
			if (selectedNodePath != null) {
				browser.highlightElement(selectedNodePath, 0, "clickBackup", lastFrameName);
			}
			if (nodePath != null) {
				lastFrameName = frameName;
				browser.highlightElement(nodePath, Config.getInstance().getIntClickColor(), "clickBackup",
											frameName);

				settingSelection = true;
				XmlTreeNode.setSelectionNodePath(domTree, nodePath);

				settingSelection = false;
				onSelectioNodeChanged(1, frameName);
			}
			selectedNodePath = nodePath;

		}

		public void locationChanged() {
			// SwingUtilities.invokeLater(new Runnable() {
			//				
			// @Override
			// public void run() {
			// System.out.println("------------------------start "
			// + browser.getUrl()
			// + " -----------------------------------------------");
			IFrameName[] iframeNames = browser.getAllIFrameNames(true);

			// System.out.println("------------------------ finished "
			// + browser.getUrl()
			// + " -----------------------------------------------");

			frameSelectionCombo.setEnabled(true);
			changeIFrameNames(iframeNames);
			frameSelectionCombo.setSelectedIndex(0);
			// }
			// });

		}

		@Override
		public void locationChanging() {
			// IFrameName[] iframeNames = browser.getAllIFrameNames(true);

			// System.out.println("------------------------ finished "
			// + browser.getUrl()
			// + " -----------------------------------------------");

			changeIFrameNames(new IFrameName[0]);
			frameSelectionCombo.setEnabled(false);
			txtXML.setText("");
		}
	};

	public void reloadContent() {
		// XmlTreeNode treeNode = new XmlTreeNode();
		// DomNode docDomNode = browser.getDocDomNode(true);
		//
		// StringBuilder xml = new StringBuilder();
		// docDomNode.toXml(xml, 0, treeNode);
		// treeNode.calcSameTagIdx();
		//
		// txtXML.loadText(xml.toString());
		// domTree.setModel(new DefaultTreeModel(treeNode));
		changeIFrameNames(browser.getAllIFrameNames(true));
		frameSelectionCombo.setSelectedIndex(0);
	}

	private NodePath selectedNodePath;
	private JScrollPane jScrollPane1 = null;
	private BrowserToolbar browserToolbar = null;
	private JTree domTree = null;
	private JSplitPane jSplitPane1 = null;

	// private JTextField txtAbsPath = null;

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getDomTree());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes domTree
	 * 
	 * @return javax.swing.JTree
	 */
	private JTree getDomTree() {
		if (domTree == null) {
			domTree = new JTree(new DefaultMutableTreeNode("Root"));
			domTree.setRootVisible(false);
			domTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
				public void valueChanged(javax.swing.event.TreeSelectionEvent e) {

					if (!settingSelection)
						onSelectioNodeChanged(2, frameComboModel.getSelectedIFrameName());
				}
			});

			domTree.addMouseListener(new MouseListener() {
				private AutoCompletePopupMenu popupMenu;

				private void checkPopup(MouseEvent e) {
					if (e.isPopupTrigger()) {
						if (popupMenu != null)
							popupMenu.show(domTree, e.getX(), e.getY());
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						TreePath path = domTree.getSelectionPath();
						// TreePath path = domTree.getPathForLocation(e.getX(),
						// e.getY());
						// 选中节点
						// domTree.setSelectionPath(path);
						// 得到当前节点
						if (path != null) {
							XmlTreeNode treeNode = (XmlTreeNode) path.getLastPathComponent();
							if (treeNode != null) {
								ArrayList<String> autoCompleteList = new ArrayList<String>();
								autoCompleteList.add("." + treeNode.getNodeName());

								XmlTreeNode[] attributeNodes = treeNode.getAttributeNodes();
								if (attributeNodes != null && attributeNodes.length > 0) {
									for (int i = 0; i < attributeNodes.length; ++i) {
										autoCompleteList.add("."
																+ treeNode.getNodeName()
																+ ".(@"
																+ attributeNodes[i].getNodeName()
																+ " == \""
																+ attributeNodes[i].getNodeValue()
																+ "\")");

										String regex = StringUtil.buildJSRegex(attributeNodes[i]
												.getNodeValue());

										if (!attributeNodes[i].getNodeValue().equals(regex)) {
											autoCompleteList.add("."
																	+ treeNode.getNodeName()
																	+ ".(/"
																	+ regex
																	+ "/.test(@"
																	+ attributeNodes[i].getNodeName()
																	+ "))");
										}

									}
								}

								if (baseElements != null) {
									NodePath nodePath = treeNode.getNodePath();
									for (int i = 0; i < baseElements.length; i++) {
										if (nodePath.isChildOf(baseElements[i])) {
											String ts = nodePath.nextPath(baseElements[i].length())
													.toString();
											autoCompleteList.add("." + ts);
											break;
										}
									}
								}

								autoCompleteList.add(treeNode.getNodePath().toString());
								String[] autoCompleteStrings = new String[autoCompleteList.size()];
								autoCompleteList.toArray(autoCompleteStrings);
								// TODO
								popupMenu = new AutoCompletePopupMenu(swingGui, domTree, txtXML);
								popupMenu.addItems(treeNode, autoCompleteStrings);
								checkPopup(e);
							}

						}
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
		}
		return domTree;
	}

	// 节点变更. changeType类型: 1 点击Browser 2 点击Tree 3 点击txtXML
	private void onSelectioNodeChanged(int changeType, IFrameName iframeName) {
		TreePath selectionPath = domTree.getSelectionPath();
		if (selectionPath == null)
			return;
		Object lastPathComponent = selectionPath.getLastPathComponent();
		if (lastPathComponent instanceof XmlTreeNode) {
			XmlTreeNode treeNode = (XmlTreeNode) lastPathComponent;
			NodePath nodePath = treeNode.getNodePath();
			if (changeType != 2) {
				domTree.scrollPathToVisible(selectionPath);
			}
			if (changeType != 1) {
				if (selectedNodePath != null)
					browser.highlightElement(selectedNodePath, 0, "clickBackup", lastFrameName);
				browser.highlightElement(nodePath, Config.getInstance().getIntClickColor(), "clickBackup",
											iframeName);
				lastFrameName = iframeName;
				selectedNodePath = nodePath;
			}
			if (changeType != 3) {

				txtXML.select(treeNode.getTxtOffset(), treeNode.getTxtOffset() + treeNode.getTxtLength());
				txtXML.removeAllHighlight();
				txtXML.highlight(treeNode.getTxtOffset(), treeNode.getTxtOffset() + treeNode.getTxtLength());
				//				
				int lineStart = StringUtil.calcRowNumber(txtXML.getText(), treeNode.getTxtOffset());
				int lineEnd = StringUtil.calcRowNumber(txtXML.getText(), treeNode.getTxtOffset()
																			+ treeNode.getTxtLength());

				jScrollPane.getGutter().setActiveLineRange(lineStart, lineEnd);
				// txtXML.scrollToReference(txtXML.getSelectedText());
			}
		}
	}

	private boolean settingSelection = false;

	/**
	 * This method initializes jSplitPane1
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane1() {
		if (jSplitPane1 == null) {
			jSplitPane1 = new JSplitPane();
			jSplitPane1.setDividerLocation(200);
			jSplitPane1.setOneTouchExpandable(true);
			jSplitPane1.setRightComponent(getJScrollPane());
			jSplitPane1.setLeftComponent(buildNodeSelectionPanel());
		}
		return jSplitPane1;
	}

	private JComboBox frameSelectionCombo;
	private FrameNameComboBoxModel frameComboModel;

	private JPanel buildNodeSelectionPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		frameSelectionCombo = initFrameCombobox();
		panel.add(frameSelectionCombo, BorderLayout.NORTH);
		panel.add(getJScrollPane1(), BorderLayout.CENTER);
		// frameSelectionCombo.setSelectedIndex(0);
		return panel;
	}

	public void changeIFrameNames(IFrameName[] iframeName) {
		if (frameComboModel == null)
			frameComboModel = new FrameNameComboBoxModel();
		frameComboModel.removeAllElements();
		if (iframeName != null) {
			// frameComboModel.addElement(iframeName[0]);
			for (int i = 0; i < iframeName.length; ++i) {
				frameComboModel.addElement(iframeName[i]);
			}
		}
	}

	private String lastRootContent = "";

	private void changeDomTree(DomNode rootNode) {
		if (rootNode == null)
			return;
		StringBuilder xml = new StringBuilder();
		XmlTreeNode treeNode = new XmlTreeNode();
		rootNode.toXml(xml, 0, treeNode);
		treeNode.calcSameTagIdx();
		String xmlContent = xml.toString();
		synchronized (lastRootContent) {
			if (lastRootContent == null || !lastRootContent.equals(xmlContent)) {
				lastRootContent = xmlContent;
				// synchronized (txtXML) {
				txtXML.loadText(xmlContent);
				// }
				domTree.setModel(new DefaultTreeModel(treeNode));
			}
		}
	}

	private JComboBox initFrameCombobox() {

		changeIFrameNames(null);
		final JComboBox combox = new JComboBox();

		combox.setUI(new BasicComboBoxUI() {
			@Override
			protected ComboPopup createPopup() {
				return new BasicComboPopup(combox) {
					private static final long serialVersionUID = 8675323832992087965L;

					@Override
					protected JList createList() {
						return new JList(comboBox.getModel()) {
							private static final long serialVersionUID = -8125778810371027857L;

							@Override
							public void processMouseEvent(MouseEvent e) {
								if (e.isControlDown()) {
									// Fix for 4234053. Filter out the Control
									// Key from the list.
									// ie., don 't allow CTRL key deselection.
									e = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(),
											e.getModifiers() ^ InputEvent.CTRL_MASK, e.getX(), e.getY(), e
													.getClickCount(), e.isPopupTrigger());
								}
								super.processMouseEvent(e);
							}

							@Override
							public String getToolTipText(MouseEvent event) {
								int index = locationToIndex(event.getPoint());
								if (index != -1) {
									Object value = getModel().getElementAt(index);
									ListCellRenderer renderer = getCellRenderer();
									Component rendererComp = renderer.getListCellRendererComponent(this,
																									value,
																									index,
																									true,
																									false);
									if (rendererComp.getPreferredSize().width > getVisibleRect().width) {
										return value == null ? null : value.toString();
									} else {
										return null;
									}
								}
								return null;
							}

							@Override
							public Point getToolTipLocation(MouseEvent event) {
								int index = locationToIndex(event.getPoint());
								if (index != -1) {
									Rectangle cellBounds = getCellBounds(index, index);
									return new Point(cellBounds.x, cellBounds.y);
								}
								return null;
							}
						};
					}
				};
			}
		});

		combox.setModel(frameComboModel);
		combox.setPreferredSize(new Dimension(20, 20));
		combox.setMinimumSize(new Dimension(20, 20));

		combox.addItemListener(new ItemListener() {

			private void changeIFrame(final IFrameName frameName) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {

						domTree.setRootVisible(true);
						browserToolbar.addURL(browser.getUrl());
						DomNode docDomNode = frameName.getDomNode();
						if (docDomNode != null) {
							changeDomTree(docDomNode);
						} else {
							XmlTreeNode treeNode = new XmlTreeNode();
							// synchronized (txtXML) {
							txtXML.loadText("");
							// }
							domTree.setModel(new DefaultTreeModel(treeNode));
						}
					}
				});
			}

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED
				// && (currentFrameName == null ||
				// !currentFrameName.equals(frameComboModel
				// .getSelectedIFrameName()))
				) {

					IFrameName currentFrameName = frameComboModel.getSelectedIFrameName();

					changeIFrame(currentFrameName);
				}

			}
		});
		return combox;
	}

	// private volatile IFrameName currentFrameName = null;

	// @Override
	public void changeLayout() {
		jSplitPane.setDividerLocation(0.66);
		SwingGui.setResizeWeight(jSplitPane, 0.7);
	}

} // @jve:decl-index=0:visual-constraint="10,10"

class FrameNameComboBoxModel extends DefaultComboBoxModel {

	private static final long serialVersionUID = -7055667254153878810L;

	@Override
	public void setSelectedItem(Object obj) {
		if (obj == null)
			return;
		String str = null;
		if (obj instanceof String) {
			str = (String) obj;
		} else if (obj instanceof IFrameName) {
			str = ((IFrameName) obj).getShowName();
		}
		if (str != null) {
			for (int i = 0; i < super.getSize(); ++i) {
				IFrameName frameName = getIFrameNameAt(i);
				if (frameName != null && frameName.getShowName().equals(str)) {
					super.setSelectedItem(frameName);
					return;
				}
			}
		}
	}

	@Override
	public Object getSelectedItem() {
		Object selectedItem = getSelectedIFrameName();
		if (selectedItem == null) {
			return getIFrameNameAt(0);
		}
		return ((IFrameName) selectedItem).getFrameName();
	}

	public IFrameName getSelectedIFrameName() {
		Object selectedItem = super.getSelectedItem();
		if (selectedItem == null) {
			selectedItem = super.getElementAt(0);
		}
		if (selectedItem == null) {
			return null;
		}
		return (IFrameName) selectedItem;
	}

	public IFrameName getIFrameNameAt(int i) {
		if (i >= 0 && i < super.getSize())
			return (IFrameName) super.getElementAt(i);
		else
			return null;
	}

	@Override
	public int getIndexOf(Object obj) {
		String str = null;
		if (obj instanceof String) {
			str = (String) obj;
		} else if (obj instanceof IFrameName) {
			str = ((IFrameName) obj).getShowName();
		}
		for (int i = 0; i < getSize(); ++i) {
			if (getElementAt(i).equals(str)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object getElementAt(int i) {
		IFrameName frameName = getIFrameNameAt(i);
		if (frameName != null)
			return frameName.getShowName();
		else
			return null;
	}

}

class AutoCompletePopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = 4491115062276936397L;
	private SwingGui swingGui;
	private JTree domTree;
	private SyntaxEditorPane txtXML;
	private static final String ACTION_COPY_ATTRIBUTE_VALUE = "CopyAttributeValue_";
	private static final int ACTION_COPY_ATTRIBUTE_VALUE_LENGTH = ACTION_COPY_ATTRIBUTE_VALUE.length();

	public AutoCompletePopupMenu(SwingGui swingGui, JTree domTree, SyntaxEditorPane txtXML) {
		super();
		this.swingGui = swingGui;
		this.domTree = domTree;
		this.txtXML = txtXML;
	}

	void addItems(XmlTreeNode treeNode, String[] items) {
		if (treeNode != null && items != null) {
			JMenuItem item = null;
			JMenu menu = new JMenu("Auto Complete");
			for (int i = 0; i < items.length; ++i) {
				item = new JMenuItem(items[i]);
				item.setActionCommand("AutoComplete");
				item.addActionListener(this);
				menu.add(item);
			}
			add(menu);
			if (treeNode.getAttributeNodes() != null && treeNode.getAttributeNodes().length > 0) {
				JMenu attributeMenu = new JMenu("Copy Attribute Value");

				for (int i = 0; i < treeNode.getAttributeNodes().length; ++i) {
					XmlTreeNode attributeNode = treeNode.getAttributeNodes()[i];
					item = new JMenuItem("@"+attributeNode.getNodeName());
					item.setActionCommand(ACTION_COPY_ATTRIBUTE_VALUE + attributeNode.getNodeValue());
					item.addActionListener(this);
					attributeMenu.add(item);
				}
				add(attributeMenu);
			}
			item = ItemFactory.createMenuItem(CommandConstants.COPY_XML, Event.CTRL_MASK);
			item.addActionListener(this);
			add(item);
			item = ItemFactory.createMenuItem(CommandConstants.COPY_TEXT, Event.CTRL_MASK + Event.SHIFT_MASK);
			item.addActionListener(this);
			add(item);

			item = ItemFactory.createMenuItem(CommandConstants.COPY_TEXT_CURRENT_NODE, Event.CTRL_MASK
																						+ Event.SHIFT_MASK
																						+ Event.ALT_MASK);

			item.addActionListener(this);
			add(item);

			item = ItemFactory.createMenuItem(CommandConstants.COPY_SHOW_TEXT, Event.CTRL_MASK
																				+ Event.ALT_MASK);
			item.addActionListener(this);
			add(item);
			addSeparator();
			item = ItemFactory.createMenuItem(CommandConstants.SCROLL_TO_SELECTION, Event.CTRL_MASK);
			item.addActionListener(this);
			add(item);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("AutoComplete".equals(e.getActionCommand())) {
			if (e.getSource() instanceof JMenuItem) {
				JMenuItem menuItem = (JMenuItem) e.getSource();
				String text = menuItem.getText();
				if (text.length() > 0) {
					FileWindow currentWindow = swingGui.getSelectedFrame();
					if (currentWindow != null) {
						JTextComponent textComponent = currentWindow.getTextArea();

						if (textComponent.getSelectionStart() - textComponent.getSelectionEnd() != 0) {
							try {
								textComponent.getDocument().remove(
																	textComponent.getSelectionStart(),
																	textComponent.getSelectionEnd()
																			- textComponent
																					.getSelectionStart());

							} catch (BadLocationException e1) {
							}
						}
						try {

							textComponent.getDocument().insertString(textComponent.getSelectionStart(), text,
																		null);
						} catch (BadLocationException e1) {
						}
					}
				}
			}
		} else if (e.getActionCommand().startsWith(ACTION_COPY_ATTRIBUTE_VALUE)) {
			if (e.getSource() instanceof JMenuItem) {
				String text = e.getActionCommand().substring(ACTION_COPY_ATTRIBUTE_VALUE_LENGTH);
				if (text.length() > 0) {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(new StringSelection(text), null);
				}
			}
		} else if (CommandConstants.SCROLL_TO_SELECTION.getCmd().equals(e.getActionCommand())) {
			TreePath path = domTree.getSelectionPath();
			domTree.scrollPathToVisible(path);
		} else if (CommandConstants.COPY_SHOW_TEXT.getCmd().equals(e.getActionCommand())) {
			TreePath selectionPath = domTree.getSelectionPath();
			Object lastPathComponent = selectionPath.getLastPathComponent();
			if (lastPathComponent instanceof XmlTreeNode) {
				XmlTreeNode treeNode = (XmlTreeNode) lastPathComponent;
				try {
					String text = txtXML.getText(treeNode.getTxtOffset(), treeNode.getTxtLength());
					text = XMLUtil.parseToText(text, "utf-8", true, true);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(new StringSelection(text), null);
				} catch (BadLocationException e1) {
				} catch (SAXException e1) {
				} catch (IOException e1) {
				} catch (ParserConfigurationException e1) {
				}
			}		
		} else if (CommandConstants.COPY_TEXT_CURRENT_NODE.getCmd().equals(e.getActionCommand())) {
			TreePath selectionPath = domTree.getSelectionPath();
			Object lastPathComponent = selectionPath.getLastPathComponent();
			if (lastPathComponent instanceof XmlTreeNode) {
				XmlTreeNode treeNode = (XmlTreeNode) lastPathComponent;
				try {
					String text = txtXML.getText(treeNode.getTxtOffset(), treeNode.getTxtLength());
					text = XMLUtil.parseToText(text, "utf-8", false, false);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(new StringSelection(text), null);
				} catch (BadLocationException e1) {
				} catch (SAXException e1) {
				} catch (IOException e1) {
				} catch (ParserConfigurationException e1) {
				}
			}
		} else if (CommandConstants.COPY_TEXT.getCmd().equals(e.getActionCommand())) {
			TreePath selectionPath = domTree.getSelectionPath();
			Object lastPathComponent = selectionPath.getLastPathComponent();
			if (lastPathComponent instanceof XmlTreeNode) {
				XmlTreeNode treeNode = (XmlTreeNode) lastPathComponent;
				try {
					String text = txtXML.getText(treeNode.getTxtOffset(), treeNode.getTxtLength());
					text = XMLUtil.parseToText(text, "utf-8", true, false);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(new StringSelection(text), null);
				} catch (BadLocationException e1) {
				} catch (SAXException e1) {
				} catch (IOException e1) {
				} catch (ParserConfigurationException e1) {
				}
			}
		} else if (CommandConstants.COPY_XML.getCmd().equals(e.getActionCommand())) {
			TreePath selectionPath = domTree.getSelectionPath();
			Object lastPathComponent = selectionPath.getLastPathComponent();
			if (lastPathComponent instanceof XmlTreeNode) {
				XmlTreeNode treeNode = (XmlTreeNode) lastPathComponent;
				try {
					String text = txtXML.getText(treeNode.getTxtOffset(), treeNode.getTxtLength());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(new StringSelection(text), null);
				} catch (BadLocationException e1) {
				}
			}
		}
	}
}
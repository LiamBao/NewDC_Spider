/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Rhino JavaScript Debugger code, released
 * November 21, 2000.
 *
 * The Initial Developer of the Original Code is
 * SeeBeyond Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2000
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Igor Bukanov
 *   Matt Gould
 *   Cameron McCormack
 *   Christopher Oliver
 *   Hannes Wallnoefer
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.cic.datacrawl.ui;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.MenuComponent;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.ResizeableToolbarUI;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.SecurityUtilities;
import org.mozilla.javascript.xml.XMLObject;

import com.cic.datacrawl.core.browser.AbstractJavaWebBrowser;
import com.cic.datacrawl.core.browser.RhinoBrowserImpl;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.config.ConfigurationSaveRunner;
import com.cic.datacrawl.core.jsfunction.RhinoStandardFunction;
import com.cic.datacrawl.core.rhino.RhinoContext;
import com.cic.datacrawl.core.rhino.debugger.GuiCallback;
import com.cic.datacrawl.core.rhino.debugger.RhinoDim;
import com.cic.datacrawl.core.rhino.debugger.RhinoDim.SourceInfo;
import com.cic.datacrawl.core.rhino.debugger.ScriptExecuter;
import com.cic.datacrawl.core.rhino.debugger.ScriptExecuterManager;
import com.cic.datacrawl.core.rhino.shell.Global;
import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.ui.dialog.ChooseColorDialog;
import com.cic.datacrawl.ui.dialog.FindAndReplaceDialog;
import com.cic.datacrawl.ui.dialog.FindDialog;
import com.cic.datacrawl.ui.panel.OutputPanel;
import com.cic.datacrawl.ui.thread.RunThreadManager;
import com.cic.datacrawl.ui.tools.CommandConstants;
import com.cic.datacrawl.ui.utils.FindAndReplaceUtil;
import com.cic.datacrawl.ui.utils.SwingFileUtils;

/**
 * GUI for the Rhino debugger.
 */
public class SwingGui extends JFrame implements GuiCallback, PreferectLayout, ConfigurationSaveRunner {

	public static final String FRAME_TITLE = "Data Crawl Script Editor";

	private static SwingGui instance;

	/**
	 * @return the instance
	 */
	public static SwingGui getInstance() {
		return instance;
	}

	private static final Logger logger = Logger.getLogger(SwingGui.class);
	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = -8217029773456711621L;

	/**
	 * The debugger.
	 */
	public RhinoDim dim;

	/**
	 * The action to run when the 'Exit' menu item is chosen or the frame is
	 * closed.
	 */
	private Runnable exitAction;

	/**
	 * The {@link JDesktopPane} that holds the script windows.
	 */
	private JDesktopPane desk;

	/**
	 * The {@link JPanel} that shows information about the context.
	 */
	private ContextWindow context;
	/**
	 * The {@link JPanel} that shows information about the context.
	 */
	private JTabbedPane tabPanel;

	/**
	 * The menu bar.
	 */
	private Menubar menubar;

	/**
	 * The debugTool bar.
	 */
	private JSToolBar debugToolBar;
	/**
	 * The debugTool bar.
	 */
	private JSToolBar fileToolBar;
	/**
	 * The debugTool bar.
	 */
	private JSToolBar editToolBar;

	/**
	 * The console that displays I/O from the script.
	 */
	private JSInternalConsole inputConsole;
	/**
	 * The console that displays I/O from the script.
	 */
	private OutputConsole outputConsole;

	private BrowserXmlPanel browserPanel;
//	private HelpPanel helpPanel;

	/**
	 * The {@link JSplitPane} that separates {@link #desk} from
	 * {@link org.mozilla.javascript.Context}.
	 */
	private JSplitPane split1;
	/**
	 * The {@link JSplitPane} that separates {@link #desk} from
	 * {@link org.mozilla.javascript.Context}.
	 */
	private JSplitPane split2;
	/**
	 * The split pane.
	 */
	public JSplitPane vSplit;
	/**
	 * The status bar.
	 */
	private JLabel statusBar;

	private RhinoContext rhinoContext;

	private OutputPanel outputPanel;

	/**
	 * @return the outputPanel
	 */
	public OutputPanel getOutputPanel() {
		return outputPanel;
	}

	/**
	 * @param rhinoContext
	 *            the rhinoContext to set
	 */
	public void setRhinoContext(RhinoContext rhinoContext) {
		this.rhinoContext = rhinoContext;
		RhinoStandardFunction.rhinoContext = rhinoContext;
	}

	public void changeTitle() {
		FileWindow currentFrame = getSelectedFrame();
		if (currentFrame == null) {
			setTitle(SwingGui.FRAME_TITLE);
		} else {
			setTitle(currentFrame.buildFrameTitle());
		}
	}

	public AbstractJavaWebBrowser getBrowser() {
		return browserPanel.getBrowser();
	}

	public void locationToFunction(String functionName) {
		if (functionName != null)
			functionName = functionName.trim();
		if (functionName == null || functionName.length() == 0) {
			MessageDialogWrapper.showMessageDialog(this, "function name is not defined.",
													"Function Not Found", JOptionPane.ERROR_MESSAGE);
		}
		try {
			RhinoDim.FunctionSource item = dim.functionSourceByName(functionName);
			if (item != null) {
				RhinoDim.SourceInfo si = item.sourceInfo();
				String url = si.url();
				int lineNumber = item.firstLine();
				showFileWindow(url, lineNumber);
			} else {
				MessageDialogWrapper.showMessageDialog(this, "Can not found function: " + functionName,
														"Function Not Found", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			showFindFunctionWindow(e.getMessage());
		}

	}

	private void showFindFunctionWindow(String functionName) {
		FindFunction dlg = new FindFunction(this, "Search function...");
		dlg.setFunctionFilterString(functionName);
		dlg.doFilter();
		dlg.showDialog(this);
	}

	/**
	 * @return the rhinoContext
	 */
	public RhinoContext getRhinoContext() {
		return rhinoContext;
	}

	/**
	 * Hash table of internal frame names to the internal frames themselves.
	 */
	private final Map<String, JFrame> toplevels = Collections.synchronizedMap(new HashMap<String, JFrame>());

	/**
	 * Hash table of script URLs to their internal frames.
	 */
	private final Map<String, FileWindow> fileWindows = Collections
			.synchronizedMap(new HashMap<String, FileWindow>());

	// /**
	// * The {@link FileWindow} that last had the focus.
	// */
	// private FileWindow currentWindow;

	/**
	 * File choose dialog for loading a script.
	 */
	private JFileChooser dlg;

	/**
	 * The AWT EventQueue. Used for manually pumping AWT events from
	 * {@link #dispatchNextGuiEvent()}.
	 */
	private EventQueue awtEventQueue;

	/**
	 * Creates a new SwingGui.
	 */
	public SwingGui(RhinoDim dim, String title) {
		super(title);
		instance = this;
		this.dim = dim;
		init();
		dim.setGuiCallback(this);
	}

	public void startupBrowser() {

		try {
//			helpPanel.startup();
			browserPanel.startup();
			getRhinoContext().setBrowser(RhinoBrowserImpl.newInstance(browserPanel.getBrowser()));
		} catch (Exception e) {
			if (logger.isDebugEnabled())
				logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * Returns the Menubar of this debugger frame.
	 */
	public Menubar getMenubar() {
		return menubar;
	}

	private FindAndReplaceDialog findAndReplaceDialog;

	private JSToolBar searchToolbar;

	private FindDialog findDialog;

	public void showFindDialog() {
		if (findDialog == null)
			findDialog = new FindDialog(this, "Find");

		FileWindow frame = getSelectedFrame();
		if (frame != null) {
			String selectedText = frame.getTextArea().getSelectedText();
			if (selectedText != null && selectedText.length() > 0) {
				findDialog.changeFromText(selectedText);
			}
		}
		if (findAndReplaceDialog != null)
			findAndReplaceDialog.setVisible(false);
		findDialog.doShow();
	}

	public void showFindAndReplaceDialog() {
		if (findAndReplaceDialog == null)
			findAndReplaceDialog = new FindAndReplaceDialog(this, "Find And Replace");

		FileWindow frame = getSelectedFrame();
		if (frame != null) {
			String selectedText = frame.getTextArea().getSelectedText();
			if (selectedText != null && selectedText.length() > 0) {
				findAndReplaceDialog.changeFromText(selectedText);
			}
		}
		if (findDialog != null)
			findDialog.setVisible(false);
		findAndReplaceDialog.doShow();
	}

	/**
	 * Sets the {@link Runnable} that will be run when the "Exit" menu item is
	 * chosen.
	 */
	public void setExitAction(Runnable r) {
		exitAction = r;
	}

	/**
	 * Returns the debugger console component.
	 */
	public JSInternalConsole getConsole() {
		return inputConsole;
	}

	/**
	 * Returns the debugger console component.
	 */
	public OutputConsole getOutputConsole() {
		return outputConsole;
	}

	/**
	 * Sets the visibility of the debugger GUI.
	 */
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b) {
			// this needs to be done after the window is visible
			inputConsole.consoleTextArea.requestFocus();
			context.hSplit.setDividerLocation(0.5);
			try {
				inputConsole.setVisible(b);
				inputConsole.doLayout();
				inputConsole.consoleTextArea.requestFocus();
			} catch (Exception exc) {
			}
		}
	}

	/**
	 * Records a new internal frame.
	 */
	public void addTopLevel(String key, JFrame frame) {
		if (frame != this) {
			toplevels.put(key, frame);
		}
	}

	private JDesktopPane createDesktopPane() {
		JDesktopPane desk = new JDesktopPane();
		desk.setPreferredSize(new Dimension(600, 300));
		desk.setMinimumSize(new Dimension(150, 50));
		final JDesktopPane finalDesk = desk;

		desk.addContainerListener(new ContainerListener() {

			@Override
			public void componentRemoved(ContainerEvent e) {
				if (finalDesk.getAllFrames().length == 0) {
					doNoneFileWindow();
				}
			}

			@Override
			public void componentAdded(ContainerEvent e) {
				doHaveFileWindow();
			}
		});
		return desk;
	}

	public void doHaveFileWindow() {
		menubar.doHaveFileWindow();
		debugToolBar.resetStatus();
		fileToolBar.resetStatus();
		editToolBar.resetStatus();
	}

	public void doNoneFileWindow() {
		menubar.doNoneFileWindow();
		debugToolBar.setEnabled(false);
		fileToolBar.setEnabled(false);
		editToolBar.setEnabled(false);
	}

	/**
	 * Constructs the debugger GUI.
	 */
	private void init() {
		menubar = new Menubar(this);
		setJMenuBar(menubar);

		debugToolBar = new JSToolBar(CommandConstants.GROUP_TOOLBAR_DEBUG, menubar);

		fileToolBar = new JSToolBar(CommandConstants.GROUP_TOOLBAR_FILE, menubar);

		searchToolbar = new JSToolBar(CommandConstants.GROUP_TOOLBAR_SEARCH, menubar);

		editToolBar = new JSToolBar(CommandConstants.GROUP_TOOLBAR_EDIT, menubar);

		chooseColorDialog = new ChooseColorDialog(this, "Choose Color", true);

		// 顶端工具栏
		JPanel toolbarPanel = new JPanel();
		FlowLayout toolbarPanelLayout = new FlowLayout();
		toolbarPanelLayout.setAlignment(FlowLayout.LEFT);
		toolbarPanel.setLayout(toolbarPanelLayout);
		toolbarPanel.add(fileToolBar);
		toolbarPanel.add(editToolBar);
		toolbarPanel.add(searchToolbar);
		toolbarPanel.add(debugToolBar);

		// contentPane包涵 脚本窗口 ，浏览器窗口，数据展示面板
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		getContentPane().add(toolbarPanel, BorderLayout.NORTH);
		getContentPane().add(contentPane, BorderLayout.CENTER);
		// 脚本窗口
		desk = createDesktopPane();
		// 浏览器窗口
		browserPanel = new BrowserXmlPanel(this);
		// 属性 - 值面板
		context = new ContextWindow(this);
		context.setPreferredSize(new Dimension(300, 120));
		context.setMinimumSize(new Dimension(50, 50));
		context.setVisible(false);
		// 放置浏览器窗口
		tabPanel = new JTabbedPane();
		tabPanel.addTab("Browser", new ImageIcon(getClass().getClassLoader().getResource("world.png")),
						browserPanel);
		tabPanel.setVisible(false);

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		
		// 放置 JS内置控制台和日志输出控制台
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout());

		// JS命令控制台
		inputConsole = new JSInternalConsole();
		JTabbedPane consoleTabPanel = new JTabbedPane();
		consoleTabPanel.addTab("Console", new ImageIcon(getClass().getClassLoader()
				.getResource("application_osx_terminal.png")), inputConsole);
		consoleTabPanel.setVisible(false);
		// 日志输出控制台
		outputConsole = new OutputConsole();
		JSplitPane consoleSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, consoleTabPanel,
				outputConsole);
		consoleSplitPanel.setOneTouchExpandable(true);
		SwingGui.setResizeWeight(consoleSplitPanel, 0.5);

		// outputConsole 上的操作bar
		JToolBar t2 = new JToolBar();
		t2.setName("Console");

		t2.setUI(new ResizeableToolbarUI());
		t2.setLayout(new GridLayout());
		t2.add(consoleSplitPanel);
		t2.setFloatable(true);
		p2.add(t2);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		// 抓取数据展示面板
		outputPanel = new OutputPanel();
		outputPanel.setMinimumSize(new Dimension(screen.width, 150));
		// 抓取数据展示面板 	JS内置控制台和日志输出控制台 
		JSplitPane outputSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputPanel, outputConsole);
		//SwingGui.setResizeWeight(outputSplit, 0.4);
		outputSplit.setOneTouchExpandable(false);
		// outputSplit.setDividerLocation((int)0);
		// FIXME change Toolbar can undock.
		// t2.setFloatable(false);

		// 脚本窗口 + 属性-值面板
		vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, desk, context);
		vSplit.setOneTouchExpandable(false);

		// 脚本窗口 + 浏览器窗口
		split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, desk, tabPanel);
		split2.setOneTouchExpandable(false);

		// 脚本、浏览器窗口 + 抓取数据展示面板
		split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, split2, outputSplit);
		// TODO Browser can not hide
		split1.setOneTouchExpandable(false);
		
		//desk outputPanel outputConsole

		contentPane.add(split1, BorderLayout.CENTER);
		statusBar = new JLabel();
		statusBar.setText("Thread: ");
		contentPane.add(statusBar, BorderLayout.SOUTH);
		setDlg(new JFileChooser());

		javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String n = f.getName();
				int i = n.lastIndexOf('.');
				if (i > 0 && i < n.length() - 1) {
					String ext = n.substring(i + 1).toLowerCase();
					if (ext.equals("js")) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "JavaScript Files (*.js)";
			}
		};
		getDlg().addChoosableFileFilter(filter);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}

		});

		
		final JToolBar finalBottomToolbar = t2;
		final JPanel finalBottomPanel = p2;
		final JSplitPane finalSplit = split1;
		final JPanel finalThis = p2;
		ComponentListener clistener = new ComponentListener() {
			boolean t2Docked = true;

			void check(Component comp) {
				Component thisParent = finalThis.getParent();
				if (thisParent == null) {
					return;
				}
				Component parent = finalBottomToolbar.getParent();
				boolean leftDocked = true;
				boolean rightDocked = true;
				boolean adjustVerticalSplit = false;

				if (parent != null) {
					if (parent != finalBottomPanel) {
						while (!(parent instanceof JFrame)) {
							parent = parent.getParent();
						}
						JFrame frame = (JFrame) parent;
						addTopLevel("Console", frame);
						frame.setResizable(true);
						rightDocked = false;
					} else {
						rightDocked = true;
					}
				}
				if (leftDocked && t2Docked && rightDocked && t2Docked) {
					// no change
					return;
				}
				t2Docked = rightDocked;
				// JSplitPane split = (JSplitPane)thisParent;
				if (leftDocked) {
					if (rightDocked) {
						finalSplit.setDividerLocation(0.75);
					} else {
						finalSplit.setDividerLocation(1.0);
					}
					if (adjustVerticalSplit) {
						// split.setDividerLocation(0.66);
					}

				} else if (rightDocked) {
					finalSplit.setDividerLocation(0.0);
					// split.setDividerLocation(0.66);
				} else {
					// both undocked
					// split.setDividerLocation(1.0);
				}
			}

			public void componentHidden(ComponentEvent e) {
				check(e.getComponent());
			}

			public void componentMoved(ComponentEvent e) {
				check(e.getComponent());
			}

			public void componentResized(ComponentEvent e) {
				check(e.getComponent());
			}

			public void componentShown(ComponentEvent e) {
				check(e.getComponent());
			}
		};
		p2.addContainerListener(new ContainerListener() {
			public void componentAdded(ContainerEvent e) {
				// Component thisParent = finalThis.getParent().getParent();
				// JSplitPane split = (JSplitPane)thisParent;
				// if (e.getChild() == finalTopToolbar) {
				if (finalBottomToolbar.getParent() == finalBottomPanel) {
					// both docked
					finalSplit.setDividerLocation(0.75);
				} else {
					// left docked only
					finalSplit.setDividerLocation(1.0);
				}
				// split.setDividerLocation(0.66);
				// }
			}

			public void componentRemoved(ContainerEvent e) {
				// Component thisParent = finalThis.getParent().getParent();
				// JSplitPane split = (JSplitPane)thisParent;
				// if (e.getChild() == finalTopToolbar) {
				if (finalBottomToolbar.getParent() == finalBottomPanel) {
					// right docked only
					finalSplit.setDividerLocation(0.0);
					// split.setDividerLocation(0.66);
				} else {
					// both undocked
					// split.setDividerLocation(1.0);
				}
				// }
			}
		});
		t2.addComponentListener(clistener);
		changeLayout();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				String[] filenames = Config.getInstance().getDefaultOpenFile();

				if (filenames == null || filenames.length == 0) {
					doNewFile();
				} else {
					doOpenFile(filenames);
				}

				int waitTime = filenames == null ? 0 : (filenames.length / 30);
				try {
					Thread.sleep(waitTime == 0 ? 1 : waitTime * 500);
				} catch (InterruptedException e) {
				}
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						String currentFileName = Config.getInstance().getCurrentWindow();
						if (!StringUtil.isEmpty(currentFileName)) {
							doOpenFile(new String[] { currentFileName });
						}
					}
				});
			}
		});
		util = FindAndReplaceUtil.getDefaultInstance();
		util.setGui(this);
	}

	private void doOpenFile(String[] filenames) {
		if (filenames == null || filenames.length == 0) {
			return;
		}
		String currentWindowFile = Config.getInstance().getCurrentWindow();
		for (int i = 0; i < filenames.length; ++i) {
			if (!filenames[i].equals(currentWindowFile))
				openFile(filenames[i]);
		}
		// try {
		// Thread.sleep(3500);
		// } catch (InterruptedException e) {
		// }
		openFile(currentWindowFile);
	}

	public FileWindow[] getAllFrames() {
		FileWindow[] ret = new FileWindow[0];
		JInternalFrame[] frames = desk.getAllFrames();
		if (frames != null) {
			ret = new FileWindow[frames.length];
			for (int i = 0; i < frames.length; ++i) {
				ret[i] = (FileWindow) frames[i];
			}
		}
		return ret;
	}

	public void updateAllOpenedWindowSetting() {
		String[] filenames = new String[0];
		JInternalFrame[] allFileWindows = desk.getAllFrames();
		if (allFileWindows != null) {
			ArrayList<String> filenameList = new ArrayList<String>();
			for (int i = 0; i < allFileWindows.length; ++i) {
				String filename = ((FileWindow) allFileWindows[i]).getSourceInfo().url();
				if (filename.toLowerCase().endsWith(".js")) {
					filenameList.add(filename);
				}
			}
			filenames = new String[filenameList.size()];
			filenameList.toArray(filenames);
		}
		Config.getInstance().putDefaultOpenFile(filenames);
	}

	public void saveConfiguration() {
		updateAllOpenedWindowSetting();
		try {
			Config.getInstance().save();
		} catch (IOException e) {
		}
	}

	/**
	 * Runs the {@link #exitAction}.
	 * 
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	private void exit() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		List<FileWindow> unsavedWindowList = getUnsavedFileWindowList();
		if (unsavedWindowList.size() > 0) {
			int ret = MessageDialogWrapper.showConfirmDialog(this, "Do you want to save all unsaved files?",
																"Save Files",
																JOptionPane.YES_NO_CANCEL_OPTION,
																JOptionPane.WARNING_MESSAGE);
			if (JOptionPane.YES_OPTION == ret) {
				for (int i = 0; i < unsavedWindowList.size(); ++i) {
					FileWindow window = unsavedWindowList.get(i);
					File f = new File(window.getUrl());
					if (f.exists()) {
						window.save();
					} else {
						window.doSaveAs(window.getUrl());
					}
				}
			} else if (JOptionPane.CANCEL_OPTION == ret) {
				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			}
		}
		if (getDefaultCloseOperation() != DO_NOTHING_ON_CLOSE) {
			saveConfiguration();
			if (exitAction != null) {
				new Thread(exitAction).start();
				// SwingUtilities.invokeLater(exitAction);
			}

			dim.setReturnValue(RhinoDim.EXIT);
		}
	}

	private List<FileWindow> getUnsavedFileWindowList() {
		ArrayList<FileWindow> ret = new ArrayList<FileWindow>();
		Iterator<String> iterator = fileWindows.keySet().iterator();
		while (iterator.hasNext()) {
			FileWindow window = getFileWindow(iterator.next());
			if (window.isModified()) {
				ret.add(window);
			}
		}
		return ret;
	}

	/**
	 * Returns the {@link FileWindow} for the given URL.
	 */
	FileWindow getFileWindow(String url) {
		if (url == null || url.equals("<stdin>")) {
			return null;
		}
		return fileWindows.get(url);
	}

	/**
	 * Returns a short version of the given URL.
	 */
	public static String getShortName(String url) {
		int lastSlash = url.lastIndexOf('/');
		if (lastSlash < 0) {
			lastSlash = url.lastIndexOf('\\');
		}
		String shortName = url;
		if (lastSlash >= 0 && lastSlash + 1 < url.length()) {
			shortName = url.substring(lastSlash + 1);
		}
		return shortName;
	}

	/**
	 * Closes the given {@link FileWindow}.
	 */
	public void removeWindow(FileWindow w) {
		String name = w.getUrl();
		fileWindows.remove(w.getUrl());
		JMenu windowMenu = getWindowMenu();
		int count = windowMenu.getItemCount();
		JMenuItem lastItem = windowMenu.getItem(count - 1);
		for (int i = 3; i < count; i++) {
			JMenuItem item = windowMenu.getItem(i);
			if (item == null)
				continue; // separator
			String text = item.getText();
			// 1 D:\foo.js
			// 2 D:\bar.js
			int pos = text.indexOf(' ');
			if (text.substring(pos + 1).equals(name)) {
				windowMenu.remove(item);
				// Cascade [0]
				// Tile [1]
				// ------- [2]
				if (count == 4) {
					// remove the final separator
					windowMenu.remove(2);
				} else {
					int j = i - 2;
					for (; i < count - 1; i++) {
						JMenuItem thisItem = windowMenu.getItem(i);
						if (thisItem != null) {
							// 1 D:\foo.js
							// 2 D:\bar.js
							text = thisItem.getText();
							if (text.equals("More Windows...")) {
								break;
							} else {
								pos = text.indexOf(' ');
								thisItem.setText((char) ('0' + j) + " " + text.substring(pos + 1));
								thisItem.setMnemonic('0' + j);
								j++;
							}
						}
					}
					if (count - 4 == 0 && lastItem != item) {
						if (lastItem.getText().equals("More Windows...")) {
							windowMenu.remove(lastItem);
						}
					}
				}
				break;
			}
		}
		windowMenu.revalidate();
	}

	/**
	 * Shows the line at which execution in the given stack frame just stopped.
	 */
	public void showStopLine(RhinoDim.StackFrame frame) {
		String sourceName = frame.getUrl();
		if (sourceName == null || sourceName.equals("<stdin>")) {
			if (inputConsole.isVisible()) {
				inputConsole.setVisible(true);
			}
		} else {
			showFileWindow(sourceName, -1);
			int lineNumber = frame.getLineNumber();
			FileWindow w = getFileWindow(sourceName);
			if (w != null) {
				setFilePosition(w, lineNumber);
			}
		}
	}

	/**
	 * Shows a {@link FileWindow} for the given source, creating it if it
	 * doesn't exist yet. if <code>lineNumber</code> is greater than -1, it
	 * indicates the line number to select and display.
	 * 
	 * @param sourceUrl
	 *            the source URL
	 * @param lineNumber
	 *            the line number to select, or -1
	 */
	public void showFileWindow(String sourceUrl, int lineNumber) {
		FileWindow w = getFileWindow(sourceUrl);
		if (w == null) {
			RhinoDim.SourceInfo si = dim.sourceInfo(sourceUrl);
			if (si == null) {
				w = openFile(sourceUrl);
			} else {
				createFileWindow(si, -1);
				w = getFileWindow(sourceUrl);
			}
		}
		if (lineNumber > -1) {
			int start = w.getPosition(lineNumber - 1);
			int end = w.getLineEndPosition(lineNumber - 1) - 1;
			w.getTextArea().select(start);
			w.getTextArea().setCaretPosition(start);
			w.getTextArea().moveCaretPosition(end);
		}
		try {
			if (w.isIcon()) {
				w.setIcon(false);
			}
			w.setVisible(true);
			w.moveToFront();
			w.setSelected(true);
			requestFocus();
			w.requestFocus();
			w.getTextArea().requestFocus();
		} catch (Exception exc) {
		}
	}

	/**
	 * Creates and shows a new {@link FileWindow} for the given source.
	 */
	public void createFileWindow(RhinoDim.SourceInfo sourceInfo, int line) {
		boolean activate = true;

		String url = sourceInfo.url();
		FileWindow w = new FileWindow(this, sourceInfo);
		registerFileWindow(url, w);
		FileWindow currentWindow = getSelectedFrame();
		if (line != -1) {
			if (currentWindow != null) {
				currentWindow.setPosition(-1);
			}
			try {
				w.setPosition(w.getTextArea().getLineStartOffset(line - 1));
			} catch (BadLocationException exc) {
				try {
					w.setPosition(w.getTextArea().getLineStartOffset(0));
				} catch (BadLocationException ee) {
					w.setPosition(-1);
				}
			}
		}
		desk.add(w);
		menubar.addFile(url);
		w.getTextArea().discardAllEdits();
		w.setVisible(true);

		if (activate) {
			try {
				w.setMaximum(true);
				w.setSelected(true);
				w.moveToFront();
			} catch (Exception exc) {
			}
		}
	}

	/**
	 * Update the source text for <code>sourceInfo</code>. This returns true if
	 * a {@link FileWindow} for the given source exists and could be updated.
	 * Otherwise, this does nothing and returns false.
	 * 
	 * @param sourceInfo
	 *            the source info
	 * @return true if a {@link FileWindow} for the given source exists and
	 *         could be updated, false otherwise.
	 */
	public boolean updateFileWindow(RhinoDim.SourceInfo sourceInfo) {
		String fileName = sourceInfo.url();
		FileWindow w = getFileWindow(fileName);
		if (w != null) {
			w.updateText(sourceInfo);
			w.show();
			return true;
		}
		return false;
	}

	/**
	 * Moves the current position in the given {@link FileWindow} to the given
	 * line.
	 */
	private void setFilePosition(FileWindow w, int line) {
		boolean activate = true;
		SyntaxEditorInnerPane textArea = w.getTextArea();
		try {
			FileWindow currentWindow = getSelectedFrame();
			if (line == -1) {
				w.setPosition(-1);
				if (currentWindow == w) {
					currentWindow = null;
				}
			} else {
				int loc = textArea.getLineStartOffset(line - 1);
				if (currentWindow != null && currentWindow != w) {
					currentWindow.setPosition(-1);
				}
				w.setPosition(loc);
				currentWindow = w;
			}
		} catch (BadLocationException exc) {
			// fix me
		}
		if (activate) {
			if (w.isIcon()) {
				desk.getDesktopManager().deiconifyFrame(w);
			}
			desk.getDesktopManager().activateFrame(w);
			try {
				w.show();
				w.toFront(); // required for correct frame layering (JDK 1.4.1)
				w.setSelected(true);
			} catch (Exception exc) {
			}
		}
	}

	/**
	 * Handles script interruption.
	 */
	public void resumeInterrupt() {

		FileWindow w = getSelectedFrame();
		if (w != null) {
			if (w.hasBreakTag()) {
				w.removeLastBreakStepTag();
			} else {
				FileWindow[] windows = getAllFrame();
				for (int i = 0; i < windows.length; ++i) {
					if (windows[i].hasBreakTag()) {
						windows[i].removeLastBreakStepTag();
					}
				}
			}
		}

	}

	/**
	 * Handles script interruption.
	 */
	public void enterInterruptImpl(RhinoDim.StackFrame lastFrame, String threadTitle, String alertMessage) {
		statusBar.setText("Thread: " + threadTitle);

		showStopLine(lastFrame);

		if (alertMessage != null) {
			MessageDialogWrapper.showMessageDialog(this, alertMessage, "Exception in Script",
													JOptionPane.ERROR_MESSAGE);
		}

		updateEnabled(true);

		RhinoDim.ContextData contextData = lastFrame.contextData();

		JComboBox ctx = context.context;
		List<String> toolTips = context.toolTips;
		context.disableUpdate();
		int frameCount = contextData.frameCount();
		ctx.removeAllItems();
		// workaround for JDK 1.4 bug that caches selected value even after
		// removeAllItems() is called
		ctx.setSelectedItem(null);
		toolTips.clear();
		for (int i = 0; i < frameCount; i++) {
			RhinoDim.StackFrame frame = contextData.getFrame(i);
			String url = frame.getUrl();
			int lineNumber = frame.getLineNumber();
			String shortName = url;
			if (url.length() > 20) {
				shortName = "..." + url.substring(url.length() - 17);
			}
			String location = "\"" + shortName + "\", line " + lineNumber;
			ctx.insertItemAt(location, i);
			location = "\"" + url + "\", line " + lineNumber;
			toolTips.add(location);
		}
		context.enableUpdate();
		ctx.setSelectedIndex(0);
		ctx.setMinimumSize(new Dimension(50, ctx.getMinimumSize().height));

	}

	/**
	 * Returns the 'Window' menu.
	 */
	private JMenu getWindowMenu() {
		return menubar.getMenu(3);
	}

	private File getCurrentDir() {
		String dir = SecurityUtilities.getSystemProperty(Config.getUserDirPropertyName());
		if (dir != null) {
			return new File(dir);
		}
		return null;
	}

	/**
	 * Displays a {@link JFileChooser} and returns the selected filename.
	 */
	public String saveFile(String title, String defaultFileName, String context) {
		getDlg().setDialogTitle(title);
		getDlg().setMultiSelectionEnabled(false);
		getDlg().setSelectedFile(new File(defaultFileName));
		File CWD = getCurrentDir();

		if (CWD != null) {
			getDlg().setCurrentDirectory(CWD);
		}
		int returnVal = getDlg().showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				String result = getDlg().getSelectedFile().getCanonicalPath();
				if (!result.toLowerCase().endsWith(".js")) {
					result = result + ".js";
				}
				File f = new File(result);
				boolean needSave = true;
				if (f.exists()) {
					needSave = MessageDialogWrapper
							.showConfirmDialog(this, "File already existed, do you want to save?",
												"File already existed.", JOptionPane.YES_NO_OPTION,
												JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
				}
				CWD = getDlg().getSelectedFile().getParentFile();
				Properties props = System.getProperties();
				props.put(Config.getUserDirPropertyName(), CWD.getPath());
				System.setProperties(props);
				if (needSave) {
					SwingFileUtils.saveFile(result, context, this);
					return result;
				}
			} catch (IOException ignored) {
			} catch (SecurityException ignored) {
			}
		}
		return null;
	}

	/**
	 * Displays a {@link JFileChooser} and returns the selected filename.
	 */
	private String chooseFile(String title) {
		getDlg().setDialogTitle(title);
		File CWD = getCurrentDir();

		if (CWD != null) {
			getDlg().setCurrentDirectory(CWD);
		}
		getDlg().setMultiSelectionEnabled(false);
		int returnVal = getDlg().showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				String result = getDlg().getSelectedFile().getCanonicalPath();
				CWD = getDlg().getSelectedFile().getParentFile();
				Properties props = System.getProperties();
				props.put(Config.getUserDirPropertyName(), CWD.getPath());
				System.setProperties(props);
				return result;
			} catch (IOException ignored) {
			} catch (SecurityException ignored) {
			}
		}
		return null;
	}

	/**
	 * Displays a {@link JFileChooser} and returns the selected filename.
	 */
	private String[] chooseFiles(String title) {
		getDlg().setDialogTitle(title);
		File CWD = getCurrentDir();

		if (CWD != null) {
			getDlg().setCurrentDirectory(CWD);
		}
		getDlg().setMultiSelectionEnabled(true);
		int returnVal = getDlg().showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			ArrayList<String> list = new ArrayList<String>();
			try {
				CWD = getDlg().getSelectedFile().getParentFile();
				Properties props = System.getProperties();
				props.put(Config.getUserDirPropertyName(), CWD.getPath());
				System.setProperties(props);

				File[] files = getDlg().getSelectedFiles();
				if (files != null) {
					for (int i = 0; i < files.length; ++i) {
						list.add(files[i].getCanonicalPath());
					}
				}

			} catch (IOException ignored) {
			} catch (SecurityException ignored) {
			}
			String[] ret = new String[list.size()];
			list.toArray(ret);
			return ret;
		}
		return null;
	}

	public FileWindow[] getAllFrame() {
		JInternalFrame[] frames = desk.getAllFrames();
		if (frames == null)
			return new FileWindow[0];
		FileWindow[] ret = new FileWindow[frames.length];
		for (int i = 0; i < frames.length; ++i) {
			ret[i] = (FileWindow) frames[i];
		}
		return ret;
	}

	/**
	 * Returns the current selected internal frame.
	 */
	public FileWindow getSelectedFrame() {
		// JInternalFrame[] frames = desk.getAllFrames();
		// for (int i = 0; i < frames.length; i++) {
		// if (frames[i].isSelected()) {
		// return (FileWindow) frames[i];
		// }
		// }
		// if (frames.length > 0)
		// return (FileWindow) frames[frames.length - 1];
		// return null;
		JInternalFrame frame = desk.getSelectedFrame();
		if (frame == null)
			return null;
		return (FileWindow) frame;
	}

	// private boolean isInterrupted;
	/**
	 * Enables or disables the menu and tool bars with respect to the state of
	 * script execution.
	 */
	private void updateEnabled(boolean interrupted) {
		// isInterrupted = interrupted;
		((Menubar) getJMenuBar()).updateEnabled(interrupted);
		for (int ci = 0, cc = debugToolBar.getComponentCount(); ci < cc; ci++) {
			boolean enableButton;
			Component c = debugToolBar.getComponent(ci);
			if (c instanceof JButton) {
				JButton button = (JButton) c;
				if (CommandConstants.EXECUTE.getCmd().equals(button.getActionCommand())) {
					// Execute
					enableButton = !interrupted;
				} else if (CommandConstants.DEBUG_BREAK.getCmd().equals(button.getActionCommand())) {
					// Break
					enableButton = true;
				} else {
					enableButton = interrupted;
				}

				c.setEnabled(enableButton);
			}
		}
		if (interrupted) {
			debugToolBar.setEnabled(true);
			// raise the debugger window
			int state = getExtendedState();
			if (state == Frame.ICONIFIED) {
				setExtendedState(Frame.NORMAL);
			}
			toFront();
			context.enable();
		} else {
			FileWindow currentWindow = getSelectedFrame();
			if (currentWindow != null)
				currentWindow.setPosition(-1);
			context.disable();
		}
	}

	/**
	 * Calls {@link JSplitPane#setResizeWeight} via reflection. For
	 * compatibility, since JDK &lt; 1.3 does not have this method.
	 */
	public static void setResizeWeight(JSplitPane pane, double weight) {
		try {
			Method m = JSplitPane.class.getMethod("setResizeWeight", new Class[] { double.class });
			m.invoke(pane, new Object[] { new Double(weight) });
		} catch (NoSuchMethodException exc) {
		} catch (IllegalAccessException exc) {
		} catch (java.lang.reflect.InvocationTargetException exc) {
		}
	}

	// GuiCallback

	/**
	 * Called when the source text for a script has been updated.
	 */
	public void updateSourceText(RhinoDim.SourceInfo sourceInfo) {
		FileWindow fileWindow = getFileWindow(sourceInfo.url());
		if (fileWindow != null) {
			fileWindow.setSourceInfo(sourceInfo);
			// } else {
			// RunProxy proxy = new RunProxy(this, RunProxy.UPDATE_SOURCE_TEXT);
			// proxy.sourceInfo = sourceInfo;
			// SwingUtilities.invokeLater(proxy);
		}
	}

	private ChooseColorDialog chooseColorDialog;

	/**
	 * Called when the interrupt loop has been entered.
	 */
	public void enterInterrupt(RhinoDim.StackFrame lastFrame, String threadTitle, String alertMessage) {
		if (SwingUtilities.isEventDispatchThread()) {
			enterInterruptImpl(lastFrame, threadTitle, alertMessage);
		} else {
			scrollToInterruptPoint(lastFrame.getLineNumber());
			RunProxy proxy = new RunProxy(this, RunProxy.ENTER_INTERRUPT);
			proxy.lastFrame = lastFrame;
			proxy.threadTitle = threadTitle;
			proxy.alertMessage = alertMessage;
			SwingUtilities.invokeLater(proxy);
		}
	}

	private void scrollToInterruptPoint(int lineNumber) {
		FileWindow currentFrame = getSelectedFrame();
		if (currentFrame != null) {
			currentFrame.scrollToInterruptPoint(lineNumber);
		}
	}

	/**
	 * Returns whether the current thread is the GUI event thread.
	 */
	public boolean isGuiEventThread() {
		return SwingUtilities.isEventDispatchThread();
	}

	/**
	 * Processes the next GUI event.
	 */
	public void dispatchNextGuiEvent() throws InterruptedException {
		EventQueue queue = awtEventQueue;
		if (queue == null) {
			queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
			awtEventQueue = queue;
		}
		AWTEvent event = queue.getNextEvent();
		if (event instanceof ActiveEvent) {
			((ActiveEvent) event).dispatch();
		} else {
			Object source = event.getSource();
			if (source instanceof Component) {
				Component comp = (Component) source;
				comp.dispatchEvent(event);
			} else if (source instanceof MenuComponent) {
				((MenuComponent) source).dispatchEvent(event);
			}
		}
	}

	// ActionListener

	/**
	 * Performs an action from the menu or toolbar.
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		int returnValue = -1;
		if (cmd.equals(CommandConstants.CUT.getCmd())
			|| cmd.equals(CommandConstants.COPY.getCmd())
			|| cmd.equals(CommandConstants.PASTE.getCmd())
			|| cmd.equals(CommandConstants.UNDO.getCmd())
			|| cmd.equals(CommandConstants.REDO.getCmd())) {

			JInternalFrame f = getSelectedFrame();
			if (f != null && f instanceof ActionListener) {
				((ActionListener) f).actionPerformed(e);
			}
		} else if (cmd.equals(CommandConstants.COMPLIE.getCmd())) {
			FileWindow currentWindow = getSelectedFrame();
			if (currentWindow == null) {
				if (JOptionPane.YES_OPTION == MessageDialogWrapper
						.showConfirmDialog(
											this,
											"There is no file was opened, please open one or more file to check",
											"Error Compiling ", JOptionPane.YES_NO_OPTION,
											JOptionPane.WARNING_MESSAGE)) {

					doOpen();
				}
			} else {
				File f = new File(currentWindow.getUrl());
				if (f.exists()) {
					currentWindow.doComplie();
				} else {
					if (JOptionPane.YES_OPTION == MessageDialogWrapper
							.showConfirmDialog(this, "Before checking, you must save this file first.",
												"Error Compiling ", JOptionPane.YES_NO_OPTION,
												JOptionPane.WARNING_MESSAGE)) {
						currentWindow.doSaveAs(currentWindow.getUrl());
					}
				}
			}
		} else if (cmd.equals(CommandConstants.CHOOSE_COLOR.getCmd())) {
			chooseColorDialog.setVisible(true);
		} else if (cmd.equals(CommandConstants.SEARCH.getCmd())) {
			showFindDialog();
		} else if (cmd.equals(CommandConstants.SEARCH_AND_REPLACE.getCmd())) {
			showFindAndReplaceDialog();
		} else if (cmd.equals(CommandConstants.SEARCH_NEXT.getCmd())) {
			util.findNext();
		} else if (cmd.equals(CommandConstants.SEARCH_PREV.getCmd())) {
			util.findPrev();
		} else if (cmd.equals(CommandConstants.HIGHLIGHT.getCmd())) {
			util.highlight();
		} else if (cmd.equals(CommandConstants.REMOVE_ALL_HIGHLIGHT.getCmd())) {
			util.removeAllHighlight();
		} else if (cmd.equals(CommandConstants.DEBUG_STEP_OVER.getCmd())) {
			returnValue = RhinoDim.STEP_OVER;
		} else if (cmd.equals(CommandConstants.DEBUG_STEP_INTO.getCmd())) {
			returnValue = RhinoDim.STEP_INTO;
		} else if (cmd.equals(CommandConstants.DEBUG_STEP_OUT.getCmd())) {
			returnValue = RhinoDim.STEP_OUT;
		} else if (cmd.equals(CommandConstants.EXECUTE.getCmd())) {
			getOutputPanel().removeAll();
			FileWindow currentFrame = getSelectedFrame();
			if (currentFrame != null) {
				boolean needDoExecute = true;
				List<FileWindow> unsavedWindowList = getUnsavedFileWindowList();
				if (unsavedWindowList.size() > 0) {
					int ret = MessageDialogWrapper
							.showConfirmDialog(this, "Some files is not saved.\nYou must save all changed files before executing?", "Save changed files before executing.",
												JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					
					if (JOptionPane.YES_OPTION == ret) {
						for (int i = 0; i < unsavedWindowList.size(); ++i) {
							FileWindow window = unsavedWindowList.get(i);
							File f = new File(window.getUrl());
							if (f.exists()) {
								window.save();
							} else {
								window.doSaveAs(window.getUrl());
							}
						}
					} else {
						needDoExecute = false;
					}
				}

				if (needDoExecute) {
					logger.info("SwingGui load");
					Config.setJSFolder(com.cic.datacrawl.core.util.FileUtils
							.getParentAbsolutePath(currentFrame.getUrl()));
					currentFrame.load();
				}
			}
		} else if (cmd.equals(CommandConstants.DEBUG_RUN.getCmd())) {
			returnValue = RhinoDim.GO;
		} else if (cmd.equals(CommandConstants.DEBUG_BREAK.getCmd())) {
			stopCurrentThread();
			returnValue = RhinoDim.GO;
		} else if (cmd.equals(CommandConstants.EXIT.getCmd())) {
			exit();
		} else if (cmd.equals(CommandConstants.OPEN.getCmd())) {
			doOpen();
		} else if (cmd.equals(CommandConstants.NEW.getCmd())) {
			doNewFile();
		} else if (cmd.equals(CommandConstants.REOPEN.getCmd())) {
			doReopen();
		} else if (cmd.equals(CommandConstants.CODE_FORMAT.getCmd())) {
			doFormatCurrentWindowCode();
		} else if (cmd.equals(CommandConstants.SAVE.getCmd())) {
			saveConfiguration();
			if (desk.getComponentCount() > 0) {
				doSave(getSelectedFrame());
			}
		} else if (cmd.equals(CommandConstants.SAVE_AS.getCmd())) {
			saveConfiguration();
			doSaveAs();
		} else if (cmd.equals(CommandConstants.SAVE_ALL.getCmd())) {
			saveConfiguration();
			if (desk.getComponentCount() > 0) {
				Component[] components = desk.getComponents();
				for (int i = 0; i < components.length; ++i) {
					doSave((FileWindow) components[i]);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
					}
				}
			}
		} else if (cmd.equals(CommandConstants.RUN.getCmd())) {
			getOutputPanel().removeAll();
			String fileName = chooseFile("Select a file to execute");
			RunProxy.executeFile(this, fileName);
		} else if (cmd.equals(CommandConstants.MORE_WINDOWS.getCmd())) {
			MoreWindows dlg = new MoreWindows(this, fileWindows, "Window", "Files");
			dlg.showDialog(this);
		} else if (cmd.equals(CommandConstants.LOCATE_BROWSER_ELEMENT.getCmd())) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					doLocateBrowserElement();
				}
			});
		} else if (cmd.equals(CommandConstants.SEARCH_FUNCTION.getCmd())) {
			FindFunction dlg = new FindFunction(this, "Search function...");
			dlg.showDialog(this);
		} else if (cmd.equals(CommandConstants.TILE.getCmd())) {
			JInternalFrame[] frames = desk.getAllFrames();
			int count = frames.length;
			int rows, cols;
			rows = cols = (int) Math.sqrt(count);
			if (rows * cols < count) {
				cols++;
				if (rows * cols < count) {
					rows++;
				}
			}
			Dimension size = desk.getSize();
			int w = size.width / cols;
			int h = size.height / rows;
			int x = 0;
			int y = 0;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					int index = (i * cols) + j;
					if (index >= frames.length) {
						break;
					}
					JInternalFrame f = frames[index];
					try {
						f.setIcon(false);
						f.setMaximum(false);
					} catch (Exception exc) {
					}
					desk.getDesktopManager().setBoundsForFrame(f, x, y, w, h);
					x += w;
				}
				y += h;
				x = 0;
			}
		} else if (cmd.equals(CommandConstants.CASCADE.getCmd())) {
			JInternalFrame[] frames = desk.getAllFrames();
			int count = frames.length;
			int x, y, w, h;
			x = y = 0;
			h = desk.getHeight();
			int d = h / count;
			if (d > 30)
				d = 30;
			for (int i = count - 1; i >= 0; i--, x += d, y += d) {
				JInternalFrame f = frames[i];
				try {
					f.setIcon(false);
					f.setMaximum(false);
				} catch (Exception exc) {
				}
				Dimension dimen = f.getPreferredSize();
				w = dimen.width;
				h = dimen.height;
				desk.getDesktopManager().setBoundsForFrame(f, x, y, w, h);
			}
		} else {
			Object obj = getFileWindow(cmd);
			if (obj != null) {
				FileWindow w = (FileWindow) obj;
				try {
					if (w.isIcon()) {
						w.setIcon(false);
					}
					w.setMaximum(true);
					w.setVisible(true);
					w.moveToFront();
					w.setSelected(true);
				} catch (Exception exc) {

				}
			}
		}
		if (returnValue != -1) {
			updateEnabled(false);
			dim.setReturnValue(returnValue);
		}
	}

	public void stopCurrentThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ScriptExecuter currentExecuter = ScriptExecuterManager.getInstance().getCurrentExecuter();
				if (currentExecuter != null)
					RunThreadManager.getInstance().stopThread(currentExecuter.getExecuterName());
			}
		}).start();

	}

	private void doFormatCurrentWindowCode() {
		FileWindow currentWindow = getSelectedFrame();
		if (currentWindow == null)
			return;
		currentWindow.formatCode();
	}

	private void stopCurrentExecuter() {
		ScriptExecuter currentExecutor = ScriptExecuterManager.getInstance().getCurrentExecuter();
		currentExecutor.stop();
	}

	private void doLocateBrowserElement() {
		FileWindow currentFileWindow = getSelectedFrame();
		if (currentFileWindow == null)
			return;
		if (currentFileWindow.hasSelectedText()) {
			String text = currentFileWindow.getSelectedText();
			if (text.trim().length() > 0) {
				if (text.startsWith(".")) {
					text = "(new XML(browser.document.xmlContent))" + text;
				}

				if (dim.stringIsCompilableUnit(text)) {
					XMLObject result;
					try {
						result = (XMLObject) dim.evalReturnObject(text, true);

						browserPanel.setBaseElements(result);
					} catch (Exception e) {
						StringBuilder errorMessage = new StringBuilder();
						if (e instanceof JavaScriptException) {
							JavaScriptException jsException = (JavaScriptException) e;
							errorMessage.append(jsException.getMessage());
							errorMessage.append("(");
							errorMessage.append(jsException.sourceName());
							errorMessage.append("#");
							errorMessage.append(jsException.lineNumber());
							errorMessage.append(")");

							if (jsException.lineSource() != null) {
								String lineSource = jsException.lineSource().trim();
								if (lineSource.length() > 0 && !lineSource.equalsIgnoreCase("NULL")) {

									errorMessage.append("\nLine Source:\n");
									errorMessage.append(lineSource);
								}
							}

						} else {
							errorMessage.append(e.getMessage());
						}
						String errMessage = errorMessage.toString();
						MessageDialogWrapper.showMessageDialog(this, errMessage, "Locate Error",
																JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	private void doSave(FileWindow window) {
		if (window != null) {
			File f = new File(window.getUrl());
			if (f.exists()) {
				window.save();
			} else {
				window.doSaveAs(window.getUrl());
			}
		}
	}

	private void doOpen() {
		String[] fileNames = chooseFiles("Select a file to open");
		if (fileNames != null) {
			for (int i = 0; i < fileNames.length; ++i) {
				openFile(fileNames[i]);
			}
		}
	}

	public FileWindow openFile(String fileName) {
		FileWindow window = null;
		if (fileName != null) {
			window = getFileWindow(fileName);
			if (window == null) {
				String text = SwingFileUtils.readFile(fileName, this);
				SourceInfo sourceInfo = SourceInfo.newInstance(text, null, fileName);
				if (text != null) {
					RunProxy proxy = new RunProxy(this, RunProxy.UPDATE_SOURCE_TEXT);
					proxy.sourceInfo = sourceInfo;
					SwingUtilities.invokeLater(proxy);
					doComplie(fileName);
				}
			} else {
				File f = new File(fileName);
				if (window.getSourceInfo().getLastUpdateTime() != f.lastModified()) {
					doReopen(fileName);
				} else {
					window.setVisible(true);
					try {
						window.setIcon(false);
						window.setMaximum(true);
						window.setSelected(true);
					} catch (PropertyVetoException e) {
					}
					window.moveToFront();
				}
			}
		}
		return window;
	}

	public void doReopen() {
		if (desk.getComponentCount() > 0) {
			FileWindow theCurrentWindow = getSelectedFrame();
			String currentUrl = theCurrentWindow.getUrl();
			File f = new File(currentUrl);
			if (f.exists()) {
				theCurrentWindow.dispose();
				openFile(currentUrl);
			}
		}
	}

	public void doReopen(String url) {
		if (desk.getComponentCount() > 0) {
			FileWindow theCurrentWindow = getFileWindow(url);
			File f = new File(url);
			if (f.exists()) {
				theCurrentWindow.dispose();
				openFile(url);
			}
		}
	}

	public void addWatch(String watchString) {
		context.addWatch(watchString);
	}

	private void doComplie(final String url) {
		File f = null;
		if (url != null) {
			f = new File(url);
			if (!f.exists())
				f = null;
		}
		if (f == null) {
			doSaveAs();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					FileWindow window = fileWindows.get(url);
					while (window == null) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
						window = fileWindows.get(url);
					}
					window.doComplie();
				}
			});
		}
	}

	private void doComplieAll() {
		Iterator<FileWindow> fileWindowIterator = fileWindows.values().iterator();
		while (fileWindowIterator.hasNext()) {
			FileWindow fileWindow = fileWindowIterator.next();
			fileWindow.doComplie();
		}
	}

	private long newFileIndex = 1;

	private void doNewFile() {
		RhinoDim.SourceInfo si = RhinoDim.SourceInfo.newInstance("", null, "New File " + newFileIndex++);
		createFileWindow(si, -1);
	}

	private void doSaveAs() {
		FileWindow currentFrame = getSelectedFrame();
		currentFrame.doSaveAs(currentFrame.getUrl());
	}

	public void unregisterFileWindow(String filename) {
		registerFileWindow(filename, null, null);
	}

	public void registerFileWindow(String filename, FileWindow window) {
		registerFileWindow(null, filename, window);
	}

	public void registerFileWindow(String oldFilename, String newFilename, FileWindow window) {
		if (oldFilename != null) {
			if (window == null)
				window = fileWindows.get(oldFilename);

			fileWindows.remove(oldFilename);
		}
		if (newFilename != null && window != null) {
			fileWindows.put(newFilename, window);
		}
	}

	public void setDlg(JFileChooser dlg) {
		this.dlg = dlg;
	}

	public JFileChooser getDlg() {
		return dlg;
	}

	@Override
	public void changeLayout() {
		SwingGui.setResizeWeight(vSplit, 0.5);
		SwingGui.setResizeWeight(split2, 0.17);
		SwingGui.setResizeWeight(split1, 0.66);
		//browserPanel.changeLayout();
	}

	public void setGlobal(Global global) {
		RhinoContext rhinoContext = new RhinoContext(global);
		setRhinoContext(rhinoContext);
	}

	private FindAndReplaceUtil util;

	/**
	 * @return the util
	 */
	public FindAndReplaceUtil getFindAndReplaceUtil() {
		return util;
	}
}

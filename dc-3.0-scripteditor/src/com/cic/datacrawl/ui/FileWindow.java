package com.cic.datacrawl.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.rhino.debugger.RhinoDim;
import com.cic.datacrawl.core.rhino.debugger.ScriptExecuterDefaultImpl;
import com.cic.datacrawl.core.util.CodeUtil;
import com.cic.datacrawl.ui.tools.CommandConstants;
import com.cic.datacrawl.ui.utils.SwingFileUtils;
import com.cic.datacrawl.ui.utils.SwingUtil;

/**
 * An internal frame for script files.
 */
public class FileWindow extends JInternalFrame implements ActionListener {
	private static final Logger logger = Logger.getLogger(FileWindow.class);
	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = -6212382604952082370L;

	/**
	 * The debugger GUI.
	 */
	private SwingGui debugGui;

	/**
	 * The SourceInfo object that describes the file.
	 */
	private RhinoDim.SourceInfo sourceInfo;

	/**
	 * The FileTextArea that displays the file.
	 */
	private SyntaxEditorInnerPane textArea;

	/**
	 * @return the textArea
	 */
	public SyntaxEditorInnerPane getTextArea() {
		return textArea;
	}

	/**
	 * The FileHeader that is the gutter for {@link #textArea}.
	 */
	// public FileHeader fileHeader;

	/**
	 * Scroll pane for containing {@link #textArea}.
	 */
	private JScrollPane p;

	/**
	 * The current offset position.
	 */
	int currentPos;
	private Gutter gutter;

	/**
	 * Loads the file.
	 */
	public void load() {
		logger.info("FileWindow load()");
		String url = getUrl();
		RunProxy.executeFile(debugGui, url);
	}

	/**
	 * Returns the offset position for the given line.
	 */
	public int getPosition(int line) {
		int result = -1;
		try {
			result = textArea.getLineStartOffset(line);
		} catch (javax.swing.text.BadLocationException exc) {
		}
		return result;
	}

	/**
	 * Returns the offset position for the given line.
	 */
	public int getLineEndPosition(int line) {
		int result = -1;
		try {
			result = textArea.getLineEndOffset(line);
		} catch (javax.swing.text.BadLocationException exc) {
			result = getPosition(line);
		}
		return result;
	}

	/**
	 * Returns whether the given line has a breakpoint.
	 */
	public boolean isBreakPoint(int line) {
		return sourceInfo.breakableLine(line) && sourceInfo.breakpoint(line);
	}

	/**
	 * Toggles the breakpoint on the given line.
	 */
	public void toggleBreakPoint(int line) {
		if (!isBreakPoint(line)) {
			setBreakPoint(line);
		} else {
			clearBreakPoint(line);
		}
	}

	public boolean stringIsCompilableUnit(String text) {
		return debugGui.dim.stringIsCompilableUnit(text);
	}

	public void eval(final String text, final int lineIndex) {
		new Thread(new Eval(debugGui, text, lineIndex)).start();
	}

	public void changeFontSize(int fontSize) {
		getTextArea().changeFontSize(fontSize);
	}

	public void display(final String text, final int lineIndex) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String commandText = text.trim();
				try {
					if (commandText.startsWith(".")) {
						commandText = "(new XML(browser.document.xmlContent))" + commandText;
					}

					System.out
							.println(ObjectUtils.toString(debugGui.dim.evalReturnObject(commandText,
																						lineIndex, true)));

				} catch (JavaScriptException e) {
					StringBuilder errorMessage = new StringBuilder();
					errorMessage.append(ObjectUtils.toString(e.getValue()));
					if (e.lineSource() != null) {
						String lineSource = e.lineSource().trim();
						if (lineSource.length() > 0 && !lineSource.equalsIgnoreCase("NULL")) {

							errorMessage.append("\nLine Source:\n");
							errorMessage.append(lineSource);
						}
					}

					MessageDialogWrapper.showMessageDialog(debugGui, errorMessage.toString(),
															"Execute Selection Error",
															JOptionPane.ERROR_MESSAGE);

				} catch (Exception e) {
					MessageDialogWrapper.showMessageDialog(debugGui, e.getMessage(),
															"Execute Selection Error",
															JOptionPane.ERROR_MESSAGE);
				}
				// debugGui.getConsole().consoleTextArea.eval(text);
			}
		}).start();
	}

	/**
	 * Sets a breakpoint on the given line.
	 */
	public void setBreakPoint(int line) {
		if (sourceInfo.breakableLine(line)) {
			boolean changed = sourceInfo.breakpoint(line, true);
			if (changed) {
				try {
					breakIconList.put(new Integer(line - 1), gutter.addLineTrackingIcon(line - 1, breakIcon));
				} catch (BadLocationException e1) {
				}

				gutter.repaint();
			}
		}
	}

	/**
	 * Clears a breakpoint from the given line.
	 */
	public void clearAllBreakPoint() {
		sourceInfo.removeAllBreakpoints();

		Iterator<Integer> keyIterator = breakIconList.keySet().iterator();
		while (keyIterator.hasNext()) {
			doRemoveGutterIcon(keyIterator.next());
		}

		gutter.repaint();
	}

	private void doRemoveGutterIcon(Integer line) {
		GutterIconInfo iconInfo = breakIconList.get(line);
		boolean needDoRemove = false;
		if (iconInfo != null) {
			needDoRemove = true;
			if (arrowIconImpl != null) {
				needDoRemove = !(arrowIconImpl.getMarkedOffset() == iconInfo.getMarkedOffset());
			}
		}
		if (needDoRemove)
			gutter.removeTrackingIcon(iconInfo);
	}

	/**
	 * Clears a breakpoint from the given line.
	 */
	public void clearBreakPoint(int line) {
		if (sourceInfo.breakableLine(line)) {
			boolean changed = sourceInfo.breakpoint(line, false);
			if (changed) {
				doRemoveGutterIcon(new Integer(line - 1));

				gutter.repaint();
			}
		}
	}

	public String getContentString() {
		return textArea.getText();
	}

	private Icon arrowIcon;
	private Icon breakIcon;

	/**
	 * Creates a new FileWindow.
	 */
	public FileWindow(final SwingGui debugGui, RhinoDim.SourceInfo sourceInfo) {
		super(SwingGui.getShortName(sourceInfo.url()), true, true, true, true);
		this.debugGui = debugGui;
		this.sourceInfo = sourceInfo;
		updateToolTip();
		currentPos = -1;

		URL arrowIconUrl = getClass().getClassLoader().getResource("16-em-right.png");
		arrowIcon = new ImageIcon(arrowIconUrl);
		URL breakIconUrl = getClass().getClassLoader().getResource("record.png");
		breakIcon = new ImageIcon(breakIconUrl);

		textArea = new SyntaxEditorInnerPane(this);
		// textArea.setRows(24);
		// textArea.setColumns(80);
		// p = new JScrollPane(textArea);

		p = new RTextScrollPane(textArea);
		// p.setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);
		// p.setIconRowHeaderEnabled(true);
		// fileHeader = new FileHeader(this);
		// p.setViewportView(textArea);
		// p.setRowHeaderView(fileHeader);

		// ((RTextScrollPane)p).getGutter().setBookmarkingEnabled(true);
		gutter = ((RTextScrollPane) p).getGutter();
		// gutter.setBookmarkingEnabled(true);
		//
		// URL url = getClass().getClassLoader().getResource("bookmark.png");
		// gutter.setBookmarkIcon(new ImageIcon(url));
		gutter.setIconRowHeaderEnabled(true);
		gutter.addIconAreaMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					int offs = textArea.viewToModel(new Point(0, e.getY()));
					int line = -1;
					try {
						line = textArea.getLineOfOffset(offs);
					} catch (BadLocationException e1) {
					}
					if (line < 0)
						return;
					toggleBreakPoint(line + 1);
					// if (isBreakPoint(line + 1)) {
					// try {
					// breakIconList.put(new Integer(line), gutter
					// .addLineTrackingIcon(line, breakIcon));
					// } catch (BadLocationException e1) {
					// }
					// } else {
					// GutterIconInfo iconInfo = breakIconList
					// .get(new Integer(line));
					// if (iconInfo != null)
					// gutter.removeTrackingIcon(iconInfo);
					// }
					gutter.repaint();
				}
			}
		});

		setContentPane(p);
		pack();
		updateText(sourceInfo);
		textArea.select(0);
		addInternalFrameListener(new InternalFrameListener() {

			@Override
			public void internalFrameOpened(InternalFrameEvent e) {
				debugGui.changeTitle();
			}

			@Override
			public void internalFrameIconified(InternalFrameEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {
				debugGui.changeTitle();
			}

			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				e.getInternalFrame().setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
				if (getTextArea().isModified()) {
					int ret = MessageDialogWrapper.showConfirmDialog(getDebugGui(),
																		"Do you want to save this files?",
																		"Save File",
																		JOptionPane.YES_NO_CANCEL_OPTION,
																		JOptionPane.WARNING_MESSAGE);
					if (JOptionPane.YES_OPTION == ret) {

						File f = new File(getUrl());
						if (f.exists()) {
							save();
						} else {
							doSaveAs(getUrl());
						}
					} else if (JOptionPane.CANCEL_OPTION == ret) {
						e.getInternalFrame().setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
					}
				}
			}

			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				Config.getInstance().setCurrentWindow(null);
				debugGui.changeTitle();
				debugGui.unregisterFileWindow(getUrl());
				// debugGui.dim.removeSourceInfo(getUrl());
			}

			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				Config.getInstance().setCurrentWindow(((FileWindow) e.getInternalFrame()).getUrl());
				if (!isIcon())
					debugGui.changeTitle();
			}
		});

		// TODO 文件变更后自动重载
		// final SourceInfo finalSourceInfo = this.sourceInfo;
		// final SwingGui finalDebugGui = this.debugGui;
		// addFocusListener(new FocusListener() {
		//
		// @Override
		// public void focusLost(FocusEvent e) {
		// }
		//
		// @Override
		// public void focusGained(FocusEvent e) {
		// File f = new File(finalSourceInfo.url());
		// if (f.lastModified() != finalSourceInfo.getLastUpdateTime()) {
		// if (JOptionPane.YES_OPTION == MessageDialogWrapper
		// .showConfirmDialog(
		// finalDebugGui,
		// "Current file was changed by another application. Do you want reload it?",
		// "File Changed", JOptionPane.YES_NO_OPTION,
		// JOptionPane.QUESTION_MESSAGE)) {
		//
		// finalDebugGui.doReopen();
		// }
		// }
		// }
		// });
	}

	private HashMap<Integer, GutterIconInfo> breakIconList = new HashMap<Integer, GutterIconInfo>();

	public String buildFrameTitle() {
		return SwingGui.FRAME_TITLE + " - " + getTitle();
		// + (getUrl() == null ? getTitle() : getUrl());
	}

	/**
	 * @return the debugGui
	 */
	public SwingGui getDebugGui() {
		return debugGui;
	}

	/**
	 * @return the sourceInfo
	 */
	public RhinoDim.SourceInfo getSourceInfo() {
		return sourceInfo;
	}

	public void scrollToInterruptPoint(int lineNumber) {
		// SyntaxEditorInnerPane textArea = getTextArea();
		// Rectangle visibleRect = textArea.getVisibleRect();
		// int interruptPointLocation = lineNumber * textArea.getRowHeight();
		// int halfHeight = textArea.getHeight() / 2;
		// if (interruptPointLocation < halfHeight) {
		// visibleRect.y = 0;
		// } else {
		// visibleRect.y = interruptPointLocation - halfHeight;
		// }
		// textArea.scrollRectToVisible(visibleRect);
		SwingUtil.scrollToLine(getTextArea(), lineNumber, getTextArea().getRowHeight());
		setVisible(true);
		try {
			setIcon(false);
			setMaximum(true);
			setSelected(true);
		} catch (PropertyVetoException e) {
		}
		moveToFront();
	}

	/**
	 * @param sourceInfo
	 *            the sourceInfo to set
	 */
	public void setSourceInfo(RhinoDim.SourceInfo sourceInfo) {
		this.sourceInfo = sourceInfo;
	}

	/**
	 * Updates the tool tip contents.
	 */
	private void updateToolTip() {
		// Try to set tool tip on frame. On Mac OS X 10.5,
		// the number of components is different, so try to be safe.
		int n = getComponentCount() - 1;
		if (n > 1) {
			n = 1;
		} else if (n < 0) {
			return;
		}
		Component c = getComponent(n);
		// this will work at least for Metal L&F
		if (c != null && c instanceof JComponent) {
			((JComponent) c).setToolTipText(getUrl());
		}
	}

	/**
	 * Returns the URL of the source.
	 */
	public String getUrl() {
		return sourceInfo.url();
	}

	/**
	 * Called when the text of the script has changed.
	 */
	public void updateText(RhinoDim.SourceInfo sourceInfo) {
		this.sourceInfo = sourceInfo;
		String fileName = sourceInfo.url();
		String newText = sourceInfo.source();
		if (!textArea.getText().equals(newText)) {
			// textArea.loadText(newText);
			textArea.setText(newText);
			textArea.setLastSavedContent(newText);
			setTitle(fileName);
			int pos = 0;
			if (currentPos != -1) {
				pos = currentPos;
			}
			textArea.select(pos);
		}
		removeLastBreakStepTag();
		// gutter.removeTrackingIcon(arrowIconImpl);
		gutter.repaint();
	}

	/**
	 * Sets the cursor position.
	 */
	public void setPosition(int pos) {

		textArea.select(pos);
		currentPos = pos;
		if (pos > 0) {
			int line = -1;
			try {
				line = textArea.getLineOfOffset(currentPos);

			} catch (BadLocationException e) {
			}
			changeBreakTag(line);
		} else {
			removeLastBreakStepTag();
			// gutter.removeTrackingIcon(this.breakIconList.get(arrowIconImpl));
			// if (lastBreakedLine >= 0) {
			// gutter.removeTrackingIcon(this.breakIconList.get(new Integer(
			// lastBreakedLine)));
			//
			// if (isBreakPoint(lastBreakedLine + 1))
			// try {
			// gutter.addLineTrackingIcon(lastBreakedLine, breakIcon);
			// } catch (BadLocationException e1) {
			// }
			// }
		}
		// TODO
		gutter.repaint();
		// fileHeader.repaint();
	}

	private int lastBreakedLine = -1;
	private GutterIconInfo arrowIconImpl;

	public boolean hasBreakTag() {
		return arrowIconImpl != null;
	}

	public void removeLastBreakStepTag() {
		gutter.removeTrackingIcon(arrowIconImpl);
		arrowIconImpl = null;
		if (lastBreakedLine >= 0) {
			if (isBreakPoint(lastBreakedLine + 1)) {
				try {
					gutter.addLineTrackingIcon(lastBreakedLine, breakIcon);
				} catch (BadLocationException e1) {
				}
			}
		}
	}

	private void changeBreakTag(int line) {
		if (line >= 0) {
			removeLastBreakStepTag();
			gutter.removeTrackingIcon(this.breakIconList.get(new Integer(line)));

			try {
				arrowIconImpl = gutter.addLineTrackingIcon(line, arrowIcon);
				// breakIconList.put(new Integer(line), arrowIconImpl);
			} catch (BadLocationException e1) {
			}
			lastBreakedLine = line;
			gutter.repaint();
		}
	}

	/**
	 * Selects a range of characters.
	 */
	public void select(int start, int end) {
		int docEnd = textArea.getDocument().getLength();
		textArea.select(docEnd, docEnd);
		textArea.select(start, end);
	}

	/**
	 * Disposes this FileWindow.
	 */
	@Override
	public void dispose() {
		debugGui.removeWindow(this);
		super.dispose();
	}

	// ActionListener

	/**
	 * Performs an action.
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(CommandConstants.SAVE.getCmd()) || cmd.equals(CommandConstants.SAVE_ALL.getCmd())) {

			save();
		} else if (cmd.equals(CommandConstants.UNDO.getCmd())) {
			textArea.undo();
		} else if (cmd.equals(CommandConstants.REDO.getCmd())) {
			textArea.redo();
		} else if (cmd.equals(CommandConstants.CUT.getCmd())) {
			textArea.cut();
		} else if (cmd.equals(CommandConstants.COPY.getCmd())) {
			textArea.copy();
		} else if (cmd.equals(CommandConstants.PASTE.getCmd())) {
			textArea.paste();
		} else {
			this.debugGui.actionPerformed(e);
		}
	}

	public void save() {
		save(getUrl(), getContentString());
	}

	private void save(String fileName, String context) {
		if (textArea.isModified()) {
			clearAllBreakPoint();
			textArea.setLastSavedContent(context);
			File f = SwingFileUtils.saveFile(fileName, context, debugGui);
			if (f != null) {
				sourceInfo.setLastUpdateTime(f.lastModified());
				setTitle(fileName);
				debugGui.setTitle(fileName);
				doComplie();
			}
			// textArea.discardAllEdits();
		}
	}

	public String doSaveAs(String defaultFileName) {
		String fileName = debugGui.saveFile("Save and compile a file", defaultFileName, getContentString());
		if (fileName != null) {
			debugGui.openFile(fileName);
			this.dispose();
		}
		return fileName;
	}

	public void doComplie() {
		if (sourceInfo != null) {
			String fileName = getUrl();
			RunProxy proxy2 = new RunProxy(debugGui, RunProxy.OPEN_FILE);
			proxy2.setFileName(fileName);
			proxy2.setText(SwingFileUtils.readFile(fileName, debugGui));
			SwingUtilities.invokeLater(proxy2);
		}
	}

	public void addWatch(String selectedText) {
		debugGui.addWatch(selectedText);
	}

	public boolean hasSelectedText() {
		return textArea.getSelectionEnd() - textArea.getSelectionStart() > 0;
	}

	public int getSelectedTextStartRow() {
		int startRow = 0;
		try {
			startRow = textArea.getLineOfOffset(textArea.getSelectionStart()) + 1;
		} catch (BadLocationException ex) {
		}
		return startRow;
	}

	public String getSelectedText() {
		return textArea.getSelectedText();
	}

	public boolean isModified() {
		return textArea.isModified();
	}

	private class Eval extends ScriptExecuterDefaultImpl implements Runnable {

		private String text;
		private SwingGui debugGui;
		private int lineIndex;

		public Eval(SwingGui swingGui, String text, int lineIndex) {
			this.debugGui = swingGui;
			this.text = text;
			this.lineIndex = lineIndex;
		}

		@Override
		public void run() {
			try {
				waitForExecute();
				enter();
				debugGui.dim.eval(text, lineIndex, true);
			} catch (JavaScriptException e) {
				StringBuilder errorMessage = new StringBuilder();
				errorMessage.append(ObjectUtils.toString(e.getValue()));
				if (e.lineSource() != null) {
					String lineSource = e.lineSource().trim();
					if (lineSource.length() > 0 && !lineSource.equalsIgnoreCase("NULL")) {

						errorMessage.append("\nLine Source:\n");
						errorMessage.append(lineSource);
					}
				}
				MessageDialogWrapper.showMessageDialog(debugGui, errorMessage.toString(),
														"Java Script Exception", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				MessageDialogWrapper.showMessageDialog(debugGui, e.getMessage(), "Error Compiling Selection",
														JOptionPane.ERROR_MESSAGE);
			} finally {
				exit();
			}
			// debugGui.getConsole().consoleTextArea.eval(text);
		}
	}

	public void formatCode() {
		String text = textArea.getText();
		Rectangle currentViewRect = textArea.getVisibleRect();
		textArea.getHighlighter().removeAllHighlights();
		textArea.setText(CodeUtil.format(text));
		textArea.scrollRectToVisible(currentViewRect);
	}
}

package com.cic.datacrawl.ui;

/**
 * 支持语法高亮,始终显示选中的JEditorPane
 */
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.fife.ui.rsyntaxtextarea.CodeTemplateManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.templates.CodeTemplate;
import org.fife.ui.rsyntaxtextarea.templates.StaticCodeTemplate;

import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.ui.tools.CommandConstants;

public class SyntaxEditorInnerPane extends RSyntaxTextArea implements ActionListener, PopupMenuListener,
		KeyListener, MouseListener {

	private static final long serialVersionUID = -405938828196563697L;

	private String lastSavedContent;

	private int rowHeight;
	private int fontSize = 12;
	private static final int MIN_FONT_SIZE = 9;
	private static final int MAX_FONT_SIZE = 72;
	public void changeFontSize(int fontSize){
		this.fontSize = fontSize;
		if(this.fontSize < MIN_FONT_SIZE){
			this.fontSize = MIN_FONT_SIZE;
		}else if(this.fontSize > MAX_FONT_SIZE){
			this.fontSize = MAX_FONT_SIZE;
		}
		Font font = getFont();
		Font newFont = new Font(font.getName(), 0, this.fontSize);
		setFont(newFont);
		repaint();
	}

	/**
	 * @return the lastSavedContent
	 */
	public String getLastSavedContent() {
		return lastSavedContent;
	}

	/**
	 * Defines the meaning of the height of a row. This defaults to the height
	 * of the font.
	 * 
	 * @return the height >= 1
	 */
	@Override
	public int getRowHeight() {
		if (rowHeight == 0) {
			FontMetrics metrics = getFontMetrics(getFont());
			rowHeight = metrics.getHeight();
		}
		return rowHeight;
	}

	/**
	 * @param lastSavedContent
	 *            the lastSavedContent to set
	 */
	public void setLastSavedContent(String lastSavedContent) {
		this.lastSavedContent = lastSavedContent;
	}

	private void initTemplate() {
		setTemplatesEnabled(true);
		CodeTemplateManager ctm = RSyntaxTextArea.getCodeTemplateManager();

		// This template is for a for-loop. The caret is placed at the upper
		// bound of the loop.
		CodeTemplate ct = new StaticCodeTemplate("for", "for (int i=0; i<", "; i++) {\n\t\n}\n");

		ctm.addTemplate(ct);

		// This template is for a for-loop. The caret is placed at the upper
		// bound of the loop.
		ct = new StaticCodeTemplate("foreach", "for each (item in ", ") {\n\t\n}\n");

		ctm.addTemplate(ct);

		// This template is for a if. The caret is placed at the upper
		// bound of the loop.
		ct = new StaticCodeTemplate("if", "if (", ") {\n\t\n}\n");

		ctm.addTemplate(ct);

		// This template is for a if. The caret is placed at the upper
		// bound of the loop.
		ct = new StaticCodeTemplate("ifelse", "if (", ") {\n\t\n} else  {\n\t\n}\n");
		ctm.addTemplate(ct);

		// This template is for a if. The caret is placed at the upper
		// bound of the loop.
		ct = new StaticCodeTemplate("elseif", "} else if (", ") {\n\t\n");

		ctm.addTemplate(ct);

		// This template is for a if. The caret is placed at the upper
		// bound of the loop.
		ct = new StaticCodeTemplate("while", "while (", ") {\n\t\n} else  {\n\t\n}\n");

		ctm.addTemplate(ct);// This template is for a if. The caret is placed at
		// the upper
		// bound of the loop.
		ct = new StaticCodeTemplate("dowhile", "do {\n\t\n} while (", ");\n");

		ctm.addTemplate(ct);
	}

	/**
	 * @return the modified
	 */
	public boolean isModified() {
		return !getText().equals(lastSavedContent);
	}

	public SyntaxEditorInnerPane(FileWindow w) {
		super();

		super.setSyntaxEditingStyle(SYNTAX_STYLE_JAVASCRIPT);
		setTabSize(4);
		this.w = w;
		popup = new JSPopupMenu(CommandConstants.GROUP_MENU_FILE_WINDOW_POPUP, this);

		popup.addEnabledButtonGroup("SELECT_TEXT", CommandConstants.GROUP_MENU_FILE_WINDOW_POPUP);

		popup.addVisibleButtonGroup("SELECT_TEXT", CommandConstants.GROUP_MENU_FILE_WINDOW_POPUP);

		popup.addPopupMenuListener(this);

		addMouseListener(this);
		addKeyListener(this);
		setFont(new Font("Monospaced", 0, fontSize));
		lastSavedContent = getText();

//		final MouseWheelListener[] currentMouseWheelListeners = getMouseWheelListeners();
//		addMouseWheelListener(new MouseAdapter() {	
//			
//			@Override
//			public void mouseWheelMoved(MouseWheelEvent e) {
//				if(e.isControlDown()){
//					changeFontSize(fontSize + e.getWheelRotation());
//				}else{
//					if(currentMouseWheelListeners != null && currentMouseWheelListeners.length > 0){
//						for(int i=0;i<currentMouseWheelListeners.length;++i){
//							currentMouseWheelListeners[i].mouseWheelMoved(e);
//						}
//					}
//				}
//			}
//		});
		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				changeFileWindowTitle();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				changeFileWindowTitle();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				changeFileWindowTitle();
			}
		});
		// initTemplate();
	}

	private void changeFileWindowTitle() {
		if (isModified())
			this.w.setTitle(this.w.getUrl() + "*");
		else
			this.w.setTitle(this.w.getUrl());

		this.w.getDebugGui().changeTitle();
	}

	/**
	 * Execute Undo Action
	 */
	public void undo() {
		if (!isFocusOwner()) {
			requestFocus();
		}
		undoLastAction();
	}

	/**
	 * Execute Redo Action
	 */
	public void redo() {
		if (!isFocusOwner()) {
			requestFocus();
		}
		redoLastAction();
	}

	/**
	 * The owning {@link FileWindow}.
	 */
	private FileWindow w;
	/**
	 * The popup menu.
	 */
	private JSPopupMenu popup;

	/**
	 * Moves the selection to the given offset.
	 */
	public void select(int pos) {
		if (pos >= 0) {
			try {
				int line = getLineOfOffset(pos);
				Rectangle rect = modelToView(pos);
				if (rect == null) {
					select(pos, pos);
				} else {
					try {
						Rectangle nrect = modelToView(getLineStartOffset(line + 1));
						if (nrect != null) {
							rect = nrect;
						}
					} catch (Exception exc) {
					}
					JViewport vp = (JViewport) getParent();
					Rectangle viewRect = vp.getViewRect();
					if (viewRect.y + viewRect.height > rect.y) {
						// need to scroll up
						select(pos, pos);
					} else {
						// need to scroll down
						rect.y += (viewRect.height - rect.height) / 2;
						scrollRectToVisible(rect);
						select(pos, pos);
					}
				}
			} catch (BadLocationException exc) {
				select(pos, pos);
				// exc.printStackTrace();
			}
		}
	}

	/**
	 * Performs an action.
	 */
	public void actionPerformed(ActionEvent e) {
		int pos = viewToModel(new Point(popup.x, popup.y));
		popup.setVisible(false);
		String cmd = e.getActionCommand();
		int line = -1;
		try {
			line = getLineOfOffset(pos);
		} catch (Exception exc) {
		}
		if (cmd.equals(CommandConstants.SET_BREAKPOINT.getCmd())) {
			w.setBreakPoint(line + 1);
		} else if (cmd.equals(CommandConstants.CLEAR_BREAKPOINT.getCmd())) {
			w.clearBreakPoint(line + 1);
		} else if (cmd.equals(CommandConstants.CLEAR_ALL_BREAKPOINT.getCmd())) {
			w.clearAllBreakPoint();
		} else if (cmd.equals(CommandConstants.JUMP_TO.getCmd())) {
			w.getDebugGui().locationToFunction(getSelectedText());
		} else if (cmd.equals(CommandConstants.DEBUG_RUN.getCmd())) {
			w.load();
		} else if (cmd.equals(CommandConstants.SELECT_ALL.getCmd())) {
			selectAll();
		} else if (cmd.equals(CommandConstants.CUT.getCmd())) {
			cut();
		} else if (cmd.equals(CommandConstants.COPY.getCmd())) {
			copy();
		} else if (cmd.equals(CommandConstants.SHOW_IN_BROWSER.getCmd())) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					String url = getSelectedText();
					if (isValidUrl(url))
						w.getDebugGui().getBrowser().setUrl(url);

					else
						MessageDialogWrapper.showMessageDialog(w.getDebugGui(), "\""
																				+ url
																				+ "\" is not valid url.",
																"Invalid Url", JOptionPane.ERROR_MESSAGE);
				}
			});
		} else if (cmd.equals(CommandConstants.PASTE.getCmd())) {
			paste();
		} else if (cmd.equals(CommandConstants.DISPLAY.getCmd())) {
			int startRow = 0;
			try {
				startRow = getLineOfOffset(getSelectionStart()) + 1;
			} catch (BadLocationException ex) {
			}
			w.display(getSelectedText(), startRow);
		} else if (cmd.equals(CommandConstants.WATCH.getCmd())) {
			this.w.addWatch(getSelectedText());
		} else if (cmd.equals(CommandConstants.DEBUG_RUN_SELECTED.getCmd())) {
			w.getDebugGui().getOutputPanel().removeAll();
			String path = com.cic.datacrawl.core.util.FileUtils.getParentAbsolutePath(w.getUrl());
			if (path != null)
				Config.setJSFolder(path);
			String text = getSelectedText();
			if (text.trim().length() > 0) {
				int startRow = 0;
				try {
					startRow = getLineOfOffset(getSelectionStart()) + 1;
				} catch (BadLocationException ex) {
				}
				w.eval(text, startRow);
			}
		} else {
			this.w.actionPerformed(e);
		}
	}

	private boolean isValidUrl(String url) {
		char[] invalidChar = new char[] { '\b', '\f', '\r', '\t', '\n', ' ', '(', ')', '[', ']', '{', '}',
				'\'', '"', '<', '>', '*', '@', '~', '`', '^', '|', '\\' };
		boolean isValid = true;

		for (int i = 0; i < invalidChar.length; ++i) {
			if (url.lastIndexOf(invalidChar[i]) >= 0) {
				return false;
			}
		}

		return isValid;
	}

	/**
	 * Checks if the popup menu should be shown.
	 * 
	 * @param b
	 */
	private void checkPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			if (getSelectedText() != null && getSelectedText().length() > 0)
				popup.setStatus("SELECT_TEXT");
			else {
				popup.resetStatus();
			}
			popup.show(this, e.getX(), e.getY());
		}
	}

	// MouseListener

	/**
	 * Called when a mouse button is pressed.
	 */
	public void mousePressed(MouseEvent e) {
		checkPopup(e);
	}

	/**
	 * Called when the mouse is clicked.
	 */
	public void mouseClicked(MouseEvent e) {
		checkPopup(e);
		requestFocus();
		getCaret().setVisible(true);
	}

	/**
	 * Called when the mouse enters the component.
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Called when the mouse exits the component.
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Called when a mouse button is released.
	 */
	public void mouseReleased(MouseEvent e) {
		checkPopup(e);
	}

	// PopupMenuListener

	/**
	 * Called before the popup menu will become visible.
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}

	/**
	 * Called before the popup menu will become invisible.
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

	/**
	 * Called when the popup menu is cancelled.
	 */
	public void popupMenuCanceled(PopupMenuEvent e) {
	}

	// KeyListener
	/**
	 * Called when a key is pressed.
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_DELETE:
		case KeyEvent.VK_ENTER:
			// w.fileHeader.update();
			// w.fileHeader.repaint();
		case KeyEvent.VK_TAB:
			// default:
			// e.consume();
		}
	}

	/**
	 * Called when a key is typed.
	 */
	public void keyTyped(KeyEvent e) {
		// e.consume();
	}

	/**
	 * Called when a key is released.
	 */
	public void keyReleased(KeyEvent e) {
		// if(e.getKeyCode() == KeyEvent.VK_H && e.isControlDown()){
		// w.getDebugGui().showFindAndReplaceDialog();
		// }
		// e.consume();
	}

	// Copy from JTextArea.java

	/**
	 * Translates an offset into the components text to a line number.
	 * 
	 * @param offset
	 *            the offset >= 0
	 * @return the line number >= 0
	 * @exception BadLocationException
	 *                thrown if the offset is less than zero or greater than the
	 *                document length.
	 */
	public int getLineOfOffset(int offset) throws BadLocationException {
		Document doc = getDocument();
		if (offset < 0) {
			throw new BadLocationException("Can't translate offset to line", -1);
		} else if (offset > doc.getLength()) {
			throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
		} else {
			Element map = getDocument().getDefaultRootElement();
			return map.getElementIndex(offset);
		}
	}

	/**
	 * Determines the number of lines contained in the area.
	 * 
	 * @return the number of lines > 0
	 */
	public int getLineCount() {
		Element map = getDocument().getDefaultRootElement();
		return map.getElementCount();
	}

	/**
	 * Determines the offset of the start of the given line.
	 * 
	 * @param line
	 *            the line number to translate >= 0
	 * @return the offset >= 0
	 * @exception BadLocationException
	 *                thrown if the line is less than zero or greater or equal
	 *                to the number of lines contained in the document (as
	 *                reported by getLineCount).
	 */
	public int getLineStartOffset(int line) throws BadLocationException {
		int lineCount = getLineCount();
		if (line < 0) {
			throw new BadLocationException("Negative line", -1);
		} else if (line >= lineCount) {
			throw new BadLocationException("No such line", getDocument().getLength() + 1);
		} else {
			Element map = getDocument().getDefaultRootElement();
			Element lineElem = map.getElement(line);

			return lineElem.getStartOffset();

		}
	}

	/**
	 * Determines the offset of the start of the given line.
	 * 
	 * @param line
	 *            the line number to translate >= 0
	 * @return the offset >= 0
	 * @exception BadLocationException
	 *                thrown if the line is less than zero or greater or equal
	 *                to the number of lines contained in the document (as
	 *                reported by getLineCount).
	 */
	public int getLineEndOffset(int line) throws BadLocationException {
		int lineCount = getLineCount();
		if (line < 0) {
			throw new BadLocationException("Negative line", -1);
		} else if (line >= lineCount) {
			throw new BadLocationException("No such line", getDocument().getLength() + 1);
		} else {
			Element map = getDocument().getDefaultRootElement();
			Element lineElem = map.getElement(line);

			return lineElem.getEndOffset();

		}
	}
}

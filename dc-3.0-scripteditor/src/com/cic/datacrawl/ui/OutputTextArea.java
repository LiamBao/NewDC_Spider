package com.cic.datacrawl.ui;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import org.apache.commons.lang.ArrayUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.ui.tools.CommandConstants;
import com.cic.datacrawl.ui.utils.Log4jParser;

class LimitativeDocument extends RSyntaxDocument {
	private static final long serialVersionUID = -6466163819002802040L;
	private OutputTextArea textComponent;
	private int lineMax = 1000;

	public LimitativeDocument(OutputTextArea tc, String syntaxStyle, int lineMax) {
		super(syntaxStyle);
		textComponent = tc;
		textComponent.calcLineCount();
		this.lineMax = lineMax;
	}

	@Override
	public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
		String value = textComponent.getText();
		int overrun = 0;
		if (value != null && value.indexOf('\n') >= 0) {
			for (int num = lineMax - textComponent.getLineCount(); num < 0; num = lineMax
																					- textComponent
																							.getLineCount()) {

				overrun = value.indexOf('\n') + 1;
				super.remove(0, overrun);
				textComponent.subLineCount();
			}
		}
		super.insertString(offset - overrun, s, attributeSet);
	}
}

class OutputWrite implements Runnable {
	private OutputTextArea textArea;
	private String str;

	public OutputWrite(OutputTextArea textArea, String str) {
		this.textArea = textArea;
		this.str = str;
	}

	public void run() {
		textArea.write(str);

	}
}

class OutputWriter extends java.io.OutputStream {

	private OutputTextArea textArea;
	private Vector<Byte> buffer;

	public OutputWriter(OutputTextArea textArea) {
		this.textArea = textArea;
		buffer = new Vector<Byte>();
	}

	@Override
	public synchronized void write(int ch) {
		try {
			buffer.add(new Byte((byte) ch));
			if (ch == '\n') {
				flushBuffer();
			}
		} catch (Exception e) {
		}
	}

	public synchronized void write(char[] data, int off, int len) {
		try {
			for (int i = off; i < len; i++) {
				buffer.add(new Byte((byte) data[i]));
				if (data[i] == '\n' || data[i] == '\r') {
					flushBuffer();
				}
			}
		} catch (Exception e) {
		} finally {
			textArea.setCaretPosition(textArea.getText().length());
		}
	}

	//
	// @Override
	// public void write(byte b[], int off, int len) throws IOException {
	// textArea.append(new String(b, off, len));
	// textArea.setCaretPosition(textArea.getText().length());
	// }

	@Override
	public synchronized void flush() {
		if (buffer.size() > 0) {
			flushBuffer();
		}
	}

	@Override
	public void close() {
		flush();
	}

	private void flushBuffer() {
		byte[] bytes = new byte[buffer.size()];
		for (int i = 0; i < buffer.size(); ++i) {
			bytes[i] = buffer.get(i).byteValue();
		}
		String str = null;
		try {
			str = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		buffer.clear();
		if (str != null) {
			synchronized (textArea) {
				try {
					SwingUtilities.invokeLater(new OutputWrite(textArea, str));
				} catch (Exception e) {
				}
			}
		}
	}
}

public class OutputTextArea extends RSyntaxTextArea implements PopupMenuListener, KeyListener, FocusListener,
		ActionListener, MouseListener {
	static final long serialVersionUID = 8557083244830872961L;

	private OutputWriter console1;
	private OutputWriter console2;
	private PrintStream out;
	private PrintStream err;
	private int outputMark = 0;
	private volatile int lineCount = 0;
	private static final String OUTPUT_FILE_NAME = "stdout.log";

	public synchronized int calcLineCount() {
		lineCount = StringUtil.split(getText(), "\n").length;
		return lineCount;
	}

	public synchronized void subLineCount() {
		--lineCount;
	}

	/**
	 * @return the lineCount
	 */
	public synchronized int getLineCount() {
		return lineCount;
	}

	private JSPopupMenu popup;

	private int currentLocate;

	// @Override
	// public void select(int start, int end) {
	// requestFocus();
	// super.select(start, end);
	// }

	public OutputTextArea(String[] arg) {
		super();
		setDocument(new LimitativeDocument(this, SYNTAX_STYLE_HTML, 4000));
		addParser(new Log4jParser());
		popup = new JSPopupMenu(CommandConstants.GROUP_MENU_OUTPUT_CONSOLE_POPUP, this);
		popup.addEnabledButtonGroup("SELECT_TEXT", CommandConstants.GROUP_MENU_OUTPUT_CONSOLE_POPUP);
		popup.addVisibleButtonGroup("SELECT_TEXT", CommandConstants.GROUP_MENU_OUTPUT_CONSOLE_POPUP);
		popup.addPopupMenuListener(this);
		console1 = new OutputWriter(this);
		console2 = new OutputWriter(this);
		out = new PrintStream(console1, true);
		err = new PrintStream(console2, true);

		addKeyListener(this);
		addMouseListener(this);

		setFont(new Font("Monospaced", 0, 12));
		addFocusListener(this);

		setAutoscrolls(false);
	}

	private boolean isLocked = false;

	private short attachFileType = 1;

	public void lock() {
		isLocked = true;
	}

	public void unlock() {
		isLocked = false;
	}

	@Override
	public void focusLost(FocusEvent e) {
		((JTextArea) e.getSource()).getCaret().setSelectionVisible(true);
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	private static final int[] USEFUL_KEYS = new int[] { KeyEvent.VK_C, KeyEvent.VK_A };
	private static final int[] CONTROL_KEYS = new int[] { KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT,
			KeyEvent.VK_RIGHT, KeyEvent.VK_HOME, KeyEvent.VK_END, KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN };

	public void keyPressed(KeyEvent e) {
		if (!(e.isControlDown() && ArrayUtils.contains(USEFUL_KEYS, e.getKeyCode()))
			&& !(ArrayUtils.contains(CONTROL_KEYS, e.getKeyCode())))
			e.consume();
	}

	public void keyTyped(KeyEvent e) {
		if (!(e.isControlDown() && ArrayUtils.contains(USEFUL_KEYS, e.getKeyCode()))
			&& !(ArrayUtils.contains(CONTROL_KEYS, e.getKeyCode())))
			e.consume();
	}

	public synchronized void keyReleased(KeyEvent e) {
		if (!(e.isControlDown() && ArrayUtils.contains(USEFUL_KEYS, e.getKeyCode()))
			&& !(ArrayUtils.contains(CONTROL_KEYS, e.getKeyCode())))
			e.consume();
	}

	private void writeToFile(String str) {
		File f = new File(Config.getLogFilePath() + File.separator + OUTPUT_FILE_NAME);
		if (!f.exists()) {
			try {
				if (!f.createNewFile())
					return;
			} catch (IOException e) {
				return;
			}
		} else {
			if (f.length() > 2 * 1024 * 1024) {
				f.renameTo(new File(f.getAbsolutePath() + "." + attachFileType));
				++attachFileType;
				try {
					if (!f.createNewFile())
						return;
				} catch (IOException e) {
					return;
				}
			}
		}
		BufferedWriter fileWriter = null;
		try {
			fileWriter = new BufferedWriter(new FileWriter(f, true));
			fileWriter.write(str);
		} catch (IOException e) {
		} finally {
			if (fileWriter != null)
				try {
					fileWriter.close();
				} catch (IOException e) {
				}
		}
	}

	public synchronized void write(String str) {
		Rectangle oldVisibleRect = getVisibleRect();
		if (currentLocate == 0 || currentLocate > outputMark)
			currentLocate = outputMark;
		append(StringUtil.readUTF8(str));
		writeToFile(StringUtil.readUTF8(str));
		int len = str.length();
		outputMark += len;
		lineCount += StringUtil.calcNum(str, "\n");

		if (isLocked) {
			if (getSelectionEnd() == getSelectionStart() && getSelectionStart() == outputMark) {
				select(outputMark - 1, outputMark - 1);
			}
			scrollRectToVisible(oldVisibleRect);
		} else {
			int y = getLineCount() * getRowHeight();
			int gap = y - oldVisibleRect.height;
			if (gap < 0) {
				y = 0;
			} else if (gap < (2 * oldVisibleRect.height)) {
				y = gap;
			}
			oldVisibleRect.y = y;
			scrollRectToVisible(oldVisibleRect);
		}
	}

	public void scrollToSelectionStart() {
		int startIndex = getSelectionStart();
		if (getSelectionEnd() != startIndex) {
			Rectangle oldVisibleRect = getVisibleRect();
			int startRow = 0;
			try {
				startRow = getLineOfOffset(startIndex) + 1;
			} catch (BadLocationException e) {
			}
			int y = startRow * getRowHeight();
			if (y < oldVisibleRect.height) {
				y = 0;
			}
			oldVisibleRect.y = y;
			scrollRectToVisible(oldVisibleRect);
		}
	}

	public PrintStream getOut() {
		return out;
	}

	public PrintStream getErr() {
		return err;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(CommandConstants.CLEAR.getCmd())) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setText("");
					outputMark = 0;
					lineCount = 0;
				}
			});
		} else if (cmd.equals(CommandConstants.COPY.getCmd())) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					copy();
				}
			});
		} else if (cmd.equals(CommandConstants.SELECT_ALL.getCmd())) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					selectAll();
				}
			});
		} else if (cmd.equals(CommandConstants.SCROLL_TO_SELECTION_START.getCmd())) {
			scrollToSelectionStart();

		}
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
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
}

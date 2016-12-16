package com.cic.datacrawl.ui;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Segment;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

/**
 * Extension of JTextArea for script evaluation input.
 */
public class EvalTextArea extends JTextArea implements KeyListener,
		DocumentListener {

	private static final Logger logger = Logger.getLogger(EvalTextArea.class);

	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = -3918033649601064194L;

	/**
	 * The debugger GUI.
	 */
	private SwingGui debugGui;

	/**
	 * History of expressions that have been evaluated
	 */
	private List<String> history;

	/**
	 * Index of the selected history item.
	 */
	private int historyIndex = -1;

	/**
	 * Position in the display where output should go.
	 */
	private int outputMark;

	private Object scope;

	public void changeScope(Object scope) {
		this.scope = scope;
	}

	/**
	 * Creates a new EvalTextArea.
	 */
	public EvalTextArea(SwingGui debugGui) {
		this.debugGui = debugGui;
		history = Collections.synchronizedList(new ArrayList<String>());
		Document doc = getDocument();
		doc.addDocumentListener(this);
		addKeyListener(this);
		setLineWrap(true);
		setFont(new Font("Monospaced", 0, 12));
		append("% ");
		outputMark = doc.getLength();
	}

	/**
	 * Selects a subrange of the text.
	 */
	@Override
	public void select(int start, int end) {
		// requestFocus();
		super.select(start, end);
	}

	/**
	 * Called when Enter is pressed.
	 */
	private synchronized void returnPressed() {
		Document doc = getDocument();
		int len = doc.getLength();
		Segment segment = new Segment();
		try {
			doc.getText(outputMark, len - outputMark, segment);
		} catch (javax.swing.text.BadLocationException ignored) {
		}
		String text = segment.toString();
		if (debugGui.dim.stringIsCompilableUnit(text)) {
			if (text.trim().length() > 0) {
				history.add(text);
				historyIndex = history.size();
			}
			append("\n");
			try {
				String result = ObjectUtils.toString(debugGui.dim
						.getObjectProperty(scope, text));

				if (result.length() > 0) {
					append(result);
					append("\n");
				}
				append("% ");
				outputMark = doc.getLength();
			} catch (Exception e) {
				MessageDialogWrapper.showMessageDialog(debugGui,
						e.getMessage(), "Error Compiling ",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			append("\n");
		}
	}

	/**
	 * Writes output into the text area.
	 */
	public synchronized void write(String str) {
		insert(str, outputMark);
		int len = str.length();
		outputMark += len;
		select(outputMark, outputMark);
	}

	// KeyListener

	/**
	 * Called when a key is pressed.
	 */
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_LEFT) {
			if (outputMark == getCaretPosition()) {
				e.consume();
			}
		} else if (code == KeyEvent.VK_HOME) {
			int caretPos = getCaretPosition();
			if (caretPos == outputMark) {
				e.consume();
			} else if (caretPos > outputMark) {
				if (!e.isControlDown()) {
					if (e.isShiftDown()) {
						moveCaretPosition(outputMark);
					} else {
						setCaretPosition(outputMark);
					}
					e.consume();
				}
			}
		} else if (code == KeyEvent.VK_ENTER) {
			returnPressed();
			e.consume();
		} else if (code == KeyEvent.VK_UP) {
			historyIndex--;
			if (historyIndex >= 0) {
				if (historyIndex >= history.size()) {
					historyIndex = history.size() - 1;
				}
				if (historyIndex >= 0) {
					String str = history.get(historyIndex);
					int len = getDocument().getLength();
					replaceRange(str, outputMark, len);
					int caretPos = outputMark + str.length();
					select(caretPos, caretPos);
				} else {
					historyIndex++;
				}
			} else {
				historyIndex++;
			}
			e.consume();
		} else if (code == KeyEvent.VK_DOWN) {
			int caretPos = outputMark;
			if (history.size() > 0) {
				historyIndex++;
				if (historyIndex < 0) {
					historyIndex = 0;
				}
				int len = getDocument().getLength();
				if (historyIndex < history.size()) {
					String str = history.get(historyIndex);
					replaceRange(str, outputMark, len);
					caretPos = outputMark + str.length();
				} else {
					historyIndex = history.size();
					replaceRange("", outputMark, len);
				}
			}
			select(caretPos, caretPos);
			e.consume();
		}
	}

	/**
	 * Called when a key is typed.
	 */
	public void keyTyped(KeyEvent e) {
		int keyChar = e.getKeyChar();
		if (keyChar == 0x8 /* KeyEvent.VK_BACK_SPACE */) {
			if (outputMark == getCaretPosition()) {
				e.consume();
			}
		} else if (getCaretPosition() < outputMark) {
			setCaretPosition(outputMark);
		}
	}

	/**
	 * Called when a key is released.
	 */
	public synchronized void keyReleased(KeyEvent e) {
	}

	// DocumentListener

	/**
	 * Called when text was inserted into the text area.
	 */
	public synchronized void insertUpdate(DocumentEvent e) {
		int len = e.getLength();
		int off = e.getOffset();
		if (outputMark > off) {
			outputMark += len;
		}
	}

	/**
	 * Called when text was removed from the text area.
	 */
	public synchronized void removeUpdate(DocumentEvent e) {
		int len = e.getLength();
		int off = e.getOffset();
		if (outputMark > off) {
			if (outputMark >= off + len) {
				outputMark -= len;
			} else {
				outputMark = off;
			}
		}
	}

	/**
	 * Attempts to clean up the damage done by {@link #updateUI()}.
	 */
	public synchronized void postUpdateUI() {
		// requestFocus();
		setCaret(getCaret());
		select(outputMark, outputMark);
	}

	/**
	 * Called when text has changed in the text area.
	 */
	public synchronized void changedUpdate(DocumentEvent e) {
	}
}

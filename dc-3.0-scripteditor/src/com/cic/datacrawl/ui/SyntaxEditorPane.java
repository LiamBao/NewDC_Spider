package com.cic.datacrawl.ui;

/**
 * 支持语法高亮,始终显示选中的JEditorPane
 */
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;

public class SyntaxEditorPane extends RSyntaxTextArea {
	private static final Logger LOG = Logger.getLogger(SyntaxEditorPane.class);
	private static final long serialVersionUID = 967678159486082446L;
	private RSyntaxTextAreaHighlighter highlighter;
	private HighlightPainter highlighterPaint;

	public SyntaxEditorPane() {
		super();

		// if (!SyntaxKitInited) {
		// // DefaultSyntaxKit.initKit();
		// SyntaxKitInited = true;
		// }
		//		
		highlighter = new RSyntaxTextAreaHighlighter();
		highlighterPaint = new DefaultHighlighter.DefaultHighlightPainter(new Color(157, 206, 255));

		this.setHighlighter(highlighter);
		super.setSyntaxEditingStyle(SYNTAX_STYLE_XML);
		addFocusListener(focusListener);
	}
	public void highlight(int start, int end) {
		try {
			highlighter.addHighlight(start, end, highlighterPaint);
		} catch (BadLocationException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void removeAllHighlight() {
		highlighter.removeAllHighlights();
	}

	// static boolean SyntaxKitInited = false;

	final static FocusListener focusListener = new FocusListener() {
		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent e) {
			((JTextArea) e.getSource()).getCaret().setSelectionVisible(true);
		}
	};

	public void loadText(String t) {
		super.setText(t);

		// ( getDocument()).clearUndos();
		super.select(0, -1);
		getCaret().setSelectionVisible(true);
	}

}

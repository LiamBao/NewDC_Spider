package com.cic.datacrawl.ui.utils;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rtextarea.SearchEngine;

import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.ui.FileWindow;
import com.cic.datacrawl.ui.SwingGui;

public class FindAndReplaceUtil {
	private RSyntaxTextArea textarea;
	private HighlightPainter highlightPainter;
	private Highlighter highlighter;
	private SwingGui gui;

	/**
	 * @return the gui
	 */
	public SwingGui getGui() {
		return gui;
	}

	/**
	 * @param gui
	 *            the gui to set
	 */
	public void setGui(SwingGui gui) {
		this.gui = gui;
	}

	private static FindAndReplaceUtil defaultInstance;

	public static FindAndReplaceUtil getDefaultInstance() {
		if (defaultInstance == null) {
			defaultInstance = new FindAndReplaceUtil(new Color(164, 255, 164));
		}
		return defaultInstance;
	}

	private boolean caseMatchValue = false;
	private boolean regexUsedValue = false;
	private boolean switchCodeUsedValue = false;
	private boolean wordOnlyValue = false;
	private boolean onlyInSelectionValue = false;
	private boolean wrapSearchValue = false;

	private String searchStr;
	private boolean searchStringChanged;

	protected void changeSearchStr(String newStr) {
		if (newStr == null || newStr.length() == 0)
			return;
		String oldSearchStr = searchStr;
		searchStr = newStr;
		if (oldSearchStr == null || newStr.equals(oldSearchStr)) {
			searchStringChanged = false;
		} else {
			searchStringChanged = true;
		}
	}

	/**
	 * @param caseMatchValue
	 *            the caseMatchValue to set
	 */
	public void setCaseMatchValue(boolean caseMatchValue) {
		this.caseMatchValue = caseMatchValue;
	}

	/**
	 * @param regexUsedValue
	 *            the regexUsedValue to set
	 */
	public void setRegexUsedValue(boolean regexUsedValue) {
		this.regexUsedValue = regexUsedValue;
	}

	/**
	 * @param switchCodeUsedValue
	 *            the switchCodeUsedValue to set
	 */
	public void setSwitchCodeUsedValue(boolean switchCodeUsedValue) {
		this.switchCodeUsedValue = switchCodeUsedValue;
	}

	/**
	 * @param wordOnlyValue
	 *            the wordOnlyValue to set
	 */
	public void setWordOnlyValue(boolean wordOnlyValue) {
		this.wordOnlyValue = wordOnlyValue;
	}

	/**
	 * @param onlyInSelectionValue
	 *            the onlyInSelectionValue to set
	 */
	public void setOnlyInSelectionValue(boolean onlyInSelectionValue) {
		this.onlyInSelectionValue = onlyInSelectionValue;
	}

	/**
	 * @param wrapSearchValue
	 *            the wrapSearchValue to set
	 */
	public void setWrapSearchValue(boolean wrapSearchValue) {
		this.wrapSearchValue = wrapSearchValue;
	}

	/**
	 * @return the caseMatchValue
	 */
	public boolean isCaseMatchValue() {
		return caseMatchValue;
	}

	/**
	 * @return the regexUsedValue
	 */
	public boolean isRegexUsedValue() {
		return regexUsedValue;
	}

	/**
	 * @return the switchCodeUsedValue
	 */
	public boolean isSwitchCodeUsedValue() {
		return switchCodeUsedValue;
	}

	/**
	 * @return the wordOnlyValue
	 */
	public boolean isWordOnlyValue() {
		return wordOnlyValue;
	}

	/**
	 * @return the onlyInSelectionValue
	 */
	public boolean isOnlyInSelectionValue() {
		return onlyInSelectionValue;
	}

	/**
	 * @return the wrapSearchValue
	 */
	public boolean isWrapSearchValue() {
		return wrapSearchValue;
	}

	protected FindAndReplaceUtil(Color highlightColor) {
		super();
		highlighter = new RSyntaxTextAreaHighlighter();

		highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(
				highlightColor);
	}

	public FindAndReplaceUtil(SwingGui swingGui, Color highlightColor) {
		this(highlightColor);
		this.gui = swingGui;
	}

	public void setTextarea(RSyntaxTextArea textarea) {
		this.textarea = textarea;
	}

	private boolean isHighlight = false;

	public void highlight() {
		if (searchStr == null || searchStr.length() == 0)
			return;
		boolean wrap = isWrapSearchValue();
		setWrapSearchValue(false);
		initTextArea();
		saveAllWindowDefaultHighlight();
		disableAllWindowHighlight();
		textarea.setHighlighter(highlighter);
		int start = textarea.getSelectionStart();
		int end = textarea.getSelectionEnd();
		textarea.setSelectionStart(0);
		textarea.setSelectionEnd(0);
		while (findNext(searchStr).trim().length() == 0)
			try {
				highlighter.addHighlight(textarea.getSelectionStart(), textarea
						.getSelectionEnd(), this.highlightPainter);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		setWrapSearchValue(wrap);
		textarea.setSelectionStart(start);
		textarea.setSelectionEnd(end);
		isHighlight = true;
	}

	public void highlight(String searchStr) {
		changeSearchStr(searchStr);
		highlight();
	}

	/**
	 * @return the isHighlight
	 */
	public boolean isHighlight() {
		return isHighlight;
	}

	public void removeAllHighlight() {
		initTextArea();
		try {
			highlighter.removeAllHighlights();
		} catch (Exception e) {
		}
	}

	private String doFind(boolean isForward) {
		if (searchStr == null || searchStr.length() == 0)
			return " ";

		initTextArea();

		if (textarea == null) {
			return "Please open a file first.";
		}
		if (isSwitchCodeUsedValue())
			searchStr = removeEscapeChar(searchStr);

		boolean found = false;
		// if (searchStringChanged) {
		// // removeAllHighlight();
		// searchStringChanged = false;
		// int start = textarea.getSelectionStart();
		// int end = textarea.getSelectionEnd();
		// textarea.setSelectionStart(0);
		// textarea.setSelectionEnd(0);
		// highlight(searchStr);
		//
		// textarea.setSelectionStart(isWrapSearchValue() ? 0 : start);
		// textarea.setSelectionEnd(isWrapSearchValue() ? 0 : start);
		//
		// }

		if (isForward) {
			found = SearchEngine.find(textarea, searchStr, true,
					isCaseMatchValue(), isWordOnlyValue(), isRegexUsedValue());
			if (!found) {
				if (isWrapSearchValue()) {
					textarea.setSelectionStart(0);
					textarea.setSelectionEnd(0);
					found = SearchEngine.find(textarea, searchStr, true,
							isCaseMatchValue(), isWordOnlyValue(),
							isRegexUsedValue());
				}
			}
		} else {
			found = SearchEngine.find(textarea, searchStr, false,
					isCaseMatchValue(), isWordOnlyValue(), isRegexUsedValue());
			if (!found) {
				if (isWrapSearchValue()) {
					textarea.setSelectionStart(textarea.getText().length() - 1);
					textarea.setSelectionEnd(textarea.getText().length() - 1);
					found = SearchEngine.find(textarea, searchStr, false,
							isCaseMatchValue(), isWordOnlyValue(),
							isRegexUsedValue());
				}
			}
		}

		return found ? " " : "Text not found.";
	}

	public String findNext() {
		return doFind(true);

	}

	public String findPrev() {
		return doFind(false);
	}

	public String findNext(String searchStr) {
		changeSearchStr(searchStr);
		return doFind(true);

	}

	public String findPrev(String searchStr) {
		changeSearchStr(searchStr);
		return doFind(false);
	}

	public String replace(String searchStr, String replaceStr) {
		if (searchStr == null || searchStr.length() == 0)
			return " ";

		changeSearchStr(searchStr);
		if (isSwitchCodeUsedValue()) {
			searchStr = removeEscapeChar(searchStr);
			replaceStr = removeEscapeChar(replaceStr);
		}
		initTextArea();
		if (isWrapSearchValue()) {
			setWrapSearchValue(false);
			textarea.setSelectionStart(0);
			textarea.setSelectionEnd(0);
		}
		String ret = " ";
		boolean found = SearchEngine
				.replace(textarea, searchStr, replaceStr, true,
						isCaseMatchValue(), isWordOnlyValue(),
						isRegexUsedValue());
		SearchEngine.find(textarea, searchStr, true, isCaseMatchValue(),
				isWordOnlyValue(), isRegexUsedValue());
		if (!found)
			ret = "Text not found.";
		return ret;
	}

	public String replaceAll(String searchStr, String replaceStr) {
		if (searchStr == null || searchStr.length() == 0)
			return " ";
		if (isSwitchCodeUsedValue()) {
			searchStr = removeEscapeChar(searchStr);
			replaceStr = removeEscapeChar(replaceStr);
		}
		initTextArea();

		int foundCount = SearchEngine.replaceAll(textarea, searchStr,
				replaceStr, isCaseMatchValue(), isWordOnlyValue(),
				isRegexUsedValue());

		String ret = "Total replaced " + foundCount + " place"
				+ (foundCount > 0 ? "s." : ".");

		return ret;
	}

	private HashMap<String, Highlighter> highlightMap = new HashMap<String, Highlighter>();

	private void disableAllWindowHighlight() {
		FileWindow[] frames = gui.getAllFrames();
		if (frames != null) {
			for (int i = 0; i < frames.length; ++i) {
				frames[i].getTextArea().setHighlighter(
						highlightMap.get(frames[i].getUrl()));
			}
		}
	}

	private void saveAllWindowDefaultHighlight() {
		FileWindow[] frames = gui.getAllFrames();
		if (frames != null) {
			for (int i = 0; i < frames.length; ++i) {
				if (!highlightMap.containsKey(frames[i].getUrl())) {
					highlightMap.put(frames[i].getUrl(), frames[i]
							.getTextArea().getHighlighter());
				}
			}
		}
	}

	private void initTextArea() {
		textarea = gui.getSelectedFrame().getTextArea();

	}

	private String removeEscapeChar(String sourceString) {
		String[] fromArray = new String[] { "\\t", "\\n" };
		String[] toArray = new String[] { "\t", "\n" };
		int size = Math.min(fromArray.length, toArray.length);
		for (int i = 0; i < size; ++i) {
			sourceString = StringUtil.replaceAll(sourceString, fromArray[i],
					toArray[i]);
		}
		return sourceString;
	}
}

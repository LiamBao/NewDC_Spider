package com.cic.datacrawl.ui.utils;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;

public class Log4jParser extends AbstractParser {
	private DefaultParseResult result;
	private String ERROR_PATTERN = "[ERROR]";
	private String WARN_PATTERN = "[WARN]";
	private Color ERROR_COLOR = Color.RED;
	private Color WARN_COLOR = Color.BLUE;

	/**
	 * Creates a new Log4j parser.  The default parser treats the following
	 * identifiers in comments as task definitions:  "<code>[WARN]</code>",
	 * "<code>[ERROR]</code>".
	 */
	public Log4jParser() {
		result = new DefaultParseResult(this);
	}
	
	@Override
	public ParseResult parse(RSyntaxDocument doc, String style) {
		Element root = doc.getDefaultRootElement();
		int lineCount = root.getElementCount();

		if (style == null || SyntaxConstants.SYNTAX_STYLE_NONE.equals(style)) {
			result.clearNotices();
			result.setParsedLines(0, lineCount - 1);
			return result;
		}
		result.clearNotices();
		result.setParsedLines(0, lineCount - 1);
		for (int line = 0; line < lineCount; line++) {
			Element lineElement = root.getElement(line);
			String text = "";
			try {
				text = doc.getText(lineElement.getStartOffset(), lineElement.getEndOffset());
			} catch (BadLocationException e) {
			}
			int len = text.length();
			if (text.indexOf(ERROR_PATTERN) > 0) {
				DefaultParserNotice pn = new DefaultParserNotice(this, text, line, 0, len);
				pn.setLevel(ParserNotice.ERROR);
				pn.setShowInEditor(false);
				pn.setColor(ERROR_COLOR);
				result.addNotice(pn);
			} else if (text.indexOf(WARN_PATTERN) > 0) {
				DefaultParserNotice pn = new DefaultParserNotice(this, text, line, 0, len);
				pn.setLevel(ParserNotice.WARNING);
				pn.setShowInEditor(false);
				pn.setColor(WARN_COLOR);
				result.addNotice(pn);
			}

		}
		return result;
	}

}

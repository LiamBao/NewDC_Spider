package com.cic.datacrawl.ui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class SwingUtil {
	public final static int VK_CTRL_V = 86;

	public static Color parseToColor(String rgbString) {
		if (rgbString == null)
			return null;
		int rgb = Integer.valueOf(rgbString);
		return new Color(rgb);
	}

	public static String parseToHexString(Color color) {
		return "" + color.getRGB();
		// String redStr = Integer.toHexString(color.getRed());
		// String greenStr = Integer.toHexString(color.getGreen());
		// String blueStr = Integer.toHexString(color.getBlue());
		// return redStr + greenStr + blueStr;
	}

	/** 显示打开文件对话框 */
	public static String showOpenFile(String currentDirectoryPath,
			Component parent, final String filterRegex,
			final String filterDescription) {
		JFileChooser fileChooser = new JFileChooser(currentDirectoryPath);
		fileChooser.addChoosableFileFilter(new FileFilter() {
			private Pattern regexPattern = Pattern.compile(filterRegex);

			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				return regexPattern.matcher(f.getName()).matches();
			}

			public String getDescription() {
				return filterDescription;
			}

		});
		fileChooser.showOpenDialog(parent);

		File choosedFile = fileChooser.getSelectedFile();
		if (choosedFile == null)
			return null;
		return choosedFile.getAbsolutePath();
	}

	public static void setTableColumnsWidth(int[] colWidths, JTable table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn col = table.getColumn(table.getColumnName(i));
			col.setPreferredWidth(colWidths[i]);
		}
	}

	public static void setTableColumnsAlignment(int[] colAlignments,
			JTable table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			if (colAlignments[i] == JLabel.LEFT)
				continue;

			TableColumn col = table.getColumn(table.getColumnName(i));
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(colAlignments[i]);
			col.setCellRenderer(renderer);
		}
	}

	public static void setPaneHtmlText(String htmlText, JEditorPane pane) {
		if (htmlText == null) {
			pane.setText("");
			return;
		} else if (htmlText.length() == 0) {
			pane.setText("");
			return;
		}

		StringReader htmReader = new StringReader(htmlText);
		HTMLEditorKit kit = (HTMLEditorKit) pane
				.getEditorKitForContentType("text/html");
		HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();

		ParserDelegator parser = new ParserDelegator();
		try {
			parser.parse(htmReader, doc.getReader(0), true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		pane.setDocument(doc);
	}

	public static String getClipboardText() {
		Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
		Object objClip = system.getContents(null);
		if (objClip == null)
			return null;
		try {
			Transferable transClip = (Transferable) objClip;
			String strClip = (String) transClip
					.getTransferData(DataFlavor.stringFlavor);
			return strClip;
		} catch (IOException ex) {
			return null;
		} catch (UnsupportedFlavorException ex) {
			return null;
		}
	}

	public static void setClipboardText(String str) {
		Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection selection = new StringSelection(str);
		clipBoard.setContents(selection, null);
	}

	public static String StringToJava(String str) {
		StringBuffer ts = new StringBuffer(str);
		for (int i = str.length() - 1; i >= 0; i--) {
			char tc = str.charAt(i);
			switch (tc) {
			case '\\':
				ts.insert(i, '\\');
				break;
			case '\"':
				ts.insert(i, '\\');
				break;
			case '\r':
				ts.replace(i, i + 1, "\\r");
				break;
			case '\n':
				ts.replace(i, i + 1, "\\n");
				break;
			case '\t':
				ts.replace(i, i + 1, "\\t");
				break;
			}
		}
		ts.insert(0, '\"');
		ts.append('\"');
		return ts.toString();
	}

	public static void ShowWindowAtScreenCenter(Window window) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = window.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		window.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
//		window.setVisible(true);
	}

	public static void scrollToLine(JEditorPane editorPane, int lineNumber) {
		FontMetrics metrics = editorPane.getFontMetrics(editorPane.getFont());
		int rowHeight = metrics.getHeight();
		scrollToLine(editorPane, lineNumber, rowHeight);
	}
	public static void scrollToLine(JTextArea textarea, int lineNumber,
			int rowHeight) {
		Rectangle visibleRect = textarea.getVisibleRect();
		int location = lineNumber * rowHeight;
		int halfHeight = textarea.getHeight() / 2;

		if (location < halfHeight) {
			visibleRect.y = 0;
		} else {
			visibleRect.y = location - halfHeight;
		}
		textarea.scrollRectToVisible(visibleRect);
	}
	
	public static void scrollToLine(JEditorPane editorPane, int lineNumber,
			int rowHeight) {
		Rectangle visibleRect = editorPane.getVisibleRect();
		int location = lineNumber * rowHeight;
		int halfHeight = editorPane.getHeight() / 2;

		if (location < halfHeight) {
			visibleRect.y = 0;
		} else {
			visibleRect.y = location - halfHeight;
		}
		editorPane.scrollRectToVisible(visibleRect);
	}
	/*
	 * public static class RegexFileFilter extends
	 * javax.swing.filechooser.FileFilter { public
	 * RegexFileFilter(RegPattern_match regexPattern, String filterDescript) {
	 * this.regexPattern = regexPattern; this.strDescript = filterDescript; }
	 * private RegPattern_match regexPattern; private String strDescript;
	 * 
	 * public boolean accept(File f) { if (f.isDirectory()) return true; return
	 * regexPattern.match(f.getName()); } public String getDescription() {
	 * return strDescript; } }
	 */
}

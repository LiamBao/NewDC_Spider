package com.cic.datacrawl.ui.utils;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * 参考: http://www.veip.cn/arc_617248.html
 */
public class EditorPanePrintStream extends OutputStream {
  private EditorPanePrintStream(JEditorPane editorPane, Color textColor, OutputStream rewriteStream) {
    this.editorPane = editorPane;
    this.textColor = textColor;
    this.rewriteStream = rewriteStream;
  }

  private JEditorPane editorPane;
  private Color textColor;
  private OutputStream rewriteStream;
  
  @Override
  public void write(int b) throws IOException {
    write(new String(new char[]{(char)b}));
    if (rewriteStream != null)
      rewriteStream.write(b);
  }
  
  @Override
  public void write(byte b[], int off, int len) throws IOException {
    write(new String(b, off, len));
    if (rewriteStream != null)
      rewriteStream.write(b, off, len);
  }
  
  private void write(final String message) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() { 
        try {
          writePane(message);
        } catch (BadLocationException e) {
          //throw new RuntimeException(e);
        }
      }
    });
  }
  
  private void writePane(String message) throws BadLocationException {
    // Create a style object and then set the style attributes
    StyledDocument doc = (StyledDocument) editorPane.getDocument();
    Style style = doc.addStyle("StyleName", null);
    StyleConstants.setForeground(style, textColor);
    doc.insertString(doc.getLength(), message, style);
    
    // Make sure the last line is always visible
    editorPane.setCaretPosition(doc.getLength());

    // Keep the text area down to a certain line count
    int idealLine = 5000;
    int maxExcess = 50;

    int lineCount = doc.getDefaultRootElement().getElementCount();
    int excess = lineCount - idealLine;
    if (excess >= maxExcess) {
      String str = "";
      int start = 0;
      Element lineElement = doc.getDefaultRootElement().getElement(excess);
      int end = lineElement.getStartOffset();
      
      if (doc instanceof AbstractDocument) {
        ((AbstractDocument) doc).replace(start, end - start, str, null);
      } else {
        doc.remove(start, end - start);
        doc.insertString(start, str, null);
      }
    } 
  }
  
  public static void setOut(JEditorPane editorPane) {
    PrintStream pStream = new PrintStream(new EditorPanePrintStream(editorPane, Color.BLACK, System.out));
    System.setOut(pStream);
  }
  
  public static void setErr(JEditorPane editorPane) {
    PrintStream pStream = new PrintStream(new EditorPanePrintStream(editorPane, Color.RED, System.err));
    System.setErr(pStream);
  }
}

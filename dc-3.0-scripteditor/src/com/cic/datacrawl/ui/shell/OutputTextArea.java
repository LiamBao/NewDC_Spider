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
 * See Beyond Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2000
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Christopher Oliver
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
package com.cic.datacrawl.ui.shell;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Segment;

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
    private StringBuffer buffer;

    public OutputWriter(OutputTextArea textArea) {
        this.textArea = textArea;
        buffer = new StringBuffer();
    }

    @Override
    public synchronized void write(int ch) {
        buffer.append((char)ch);
        if(ch == '\n') {
            flushBuffer();
        }
    }

    public synchronized void write (char[] data, int off, int len) {
        for(int i = off; i < len; i++) {
            buffer.append(data[i]);
            if(data[i] == '\n') {
                flushBuffer();
            }
        }
    }

    @Override
    public synchronized void flush() {
        if (buffer.length() > 0) {
            flushBuffer();
        }
    }

    @Override
    public void close () {
        flush();
    }

    private void flushBuffer() {
        String str = buffer.toString();
        buffer.setLength(0);
        SwingUtilities.invokeLater(new OutputWrite(textArea, str));
    }
}

public class OutputTextArea
    extends JTextArea implements KeyListener, DocumentListener
{
    static final long serialVersionUID = 8557083244830872961L;

    private OutputWriter console1;
    private OutputWriter console2;
    private PrintStream out;
    private PrintStream err;
    private volatile PrintWriter inPipe;
    private volatile PipedInputStream in;
    private java.util.List<String> history;
    private int historyIndex = -1;
    private int outputMark = 0;

    @Override
    public void select(int start, int end) {
        requestFocus();
        super.select(start, end);
    }

    public OutputTextArea(String[] argv) {
        super();
        history = new java.util.ArrayList<String>();
        console1 = new OutputWriter(this);
        console2 = new OutputWriter(this);
        out = new PrintStream(console1, true);
        err = new PrintStream(console2, true);
        PipedOutputStream outPipe = new PipedOutputStream();
        inPipe = new PrintWriter(outPipe);
        in = new PipedInputStream();
        try {
            outPipe.connect(in);
        } catch(IOException exc) {
        }
        getDocument().addDocumentListener(this);
        addKeyListener(this);
        setLineWrap(true);
        setFont(new Font("Monospaced", 0, 12));
    }


    synchronized void returnPressed() {
        Document doc = getDocument();
        int len = doc.getLength();
        Segment segment = new Segment();
        try {
            doc.getText(outputMark, len - outputMark, segment);
        } catch(javax.swing.text.BadLocationException ignored) {            
        }
        if(segment.count > 0) {
            history.add(segment.toString());
        }
        historyIndex = history.size();
        inPipe.write(segment.array, segment.offset, segment.count);
        append("\n");
        outputMark = doc.getLength();
        inPipe.write("\n");
        inPipe.flush();
        console1.flush();
    }

    public void eval(String str) {
        inPipe.write(str);
        inPipe.write("\n");
        inPipe.flush();
        console1.flush();
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_LEFT) {
            if(outputMark == getCaretPosition()) {
                e.consume();
            }
        } else if(code == KeyEvent.VK_HOME) {
           int caretPos = getCaretPosition();
           if(caretPos == outputMark) {
               e.consume();
           } else if(caretPos > outputMark) {
               if(!e.isControlDown()) {
                   if(e.isShiftDown()) {
                       moveCaretPosition(outputMark);
                   } else {
                       setCaretPosition(outputMark);
                   }
                   e.consume();
               }
           }
        } else if(code == KeyEvent.VK_ENTER) {
            returnPressed();
            e.consume();
        } else if(code == KeyEvent.VK_UP) {
            historyIndex--;
            if(historyIndex >= 0) {
                if(historyIndex >= history.size()) {
                    historyIndex = history.size() -1;
                }
                if(historyIndex >= 0) {
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
        } else if(code == KeyEvent.VK_DOWN) {
            int caretPos = outputMark;
            if(history.size() > 0) {
                historyIndex++;
                if(historyIndex < 0) {historyIndex = 0;}
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

    public void keyTyped(KeyEvent e) {
        int keyChar = e.getKeyChar();
        if(keyChar == 0x8 /* KeyEvent.VK_BACK_SPACE */) {
            if(outputMark == getCaretPosition()) {
                e.consume();
            }
        } else if(getCaretPosition() < outputMark) {
            setCaretPosition(outputMark);
        }
    }

    public synchronized void keyReleased(KeyEvent e) {
    }

    public synchronized void write(String str) {
        insert(str, outputMark);
        int len = str.length();
        outputMark += len;
        select(outputMark, outputMark);
    }

    public synchronized void insertUpdate(DocumentEvent e) {
        int len = e.getLength();
        int off = e.getOffset();
        if(outputMark > off) {
            outputMark += len;
        }
    }

    public synchronized void removeUpdate(DocumentEvent e) {
        int len = e.getLength();
        int off = e.getOffset();
        if(outputMark > off) {
            if(outputMark >= off + len) {
                outputMark -= len;
            } else {
                outputMark = off;
            }
        }
    }

    public synchronized void postUpdateUI() {
        // this attempts to cleanup the damage done by updateComponentTreeUI
        requestFocus();
        setCaret(getCaret());
        select(outputMark, outputMark);
    }

    public synchronized void changedUpdate(DocumentEvent e) {
    }


    public InputStream getIn() {
        return in;
    }

    public PrintStream getOut() {
        return out;
    }

    public PrintStream getErr() {
        return err;
    }

}

package com.cic.datacrawl.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.text.BadLocationException;



/**
 * Gutter for FileWindows.
 */
public class FileHeader extends JPanel implements MouseListener {

    /**
     * Serializable magic number.
     */
    private static final long serialVersionUID = -2858905404778259127L;

    /**
     * The line that the mouse was pressed on.
     */
    private int pressLine = -1;

    /**
     * The owning FileWindow.
     */
    private FileWindow fileWindow;

    /**
     * Creates a new FileHeader.
     */
    public FileHeader(FileWindow fileWindow) {
        this.fileWindow = fileWindow;
        addMouseListener(this);
        update();
    }

    /**
     * Updates the gutter.
     */
    public void update() {
//        FileTextArea textArea = fileWindow.textArea;
    	SyntaxEditorInnerPane textArea = fileWindow.getTextArea();
        Font font = textArea.getFont();
        setFont(font);
        FontMetrics metrics = getFontMetrics(font);
        int h = metrics.getHeight();
        int lineCount = textArea.getLineCount() + 1;
        String dummy = Integer.toString(lineCount);
        if (dummy.length() < 2) {
            dummy = "99";
        }
        Dimension d = new Dimension();
        d.width = metrics.stringWidth(dummy) + 16;
        d.height = lineCount * h + 100;
        setPreferredSize(d);
        setSize(d);
    }

    /**
     * Paints the component.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
//        FileTextArea textArea = fileWindow.textArea;
        SyntaxEditorInnerPane textArea = fileWindow.getTextArea();
        Font font = textArea.getFont();
        g.setFont(font);
        FontMetrics metrics = getFontMetrics(font);
        Rectangle clip = g.getClipBounds();
        g.setColor(getBackground());
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
        int ascent = metrics.getMaxAscent();
        int h = metrics.getHeight();
        int lineCount = textArea.getLineCount();
        String dummy = Integer.toString(lineCount);
        if (dummy.length() < 2) {
            dummy = "99";
        }
        int startLine = clip.y / h;
        int endLine = (clip.y + clip.height) / h + 1;
        int width = getWidth();
        if (endLine > lineCount) endLine = lineCount;
        for (int i = startLine; i < endLine; i++) {
            String text;
            int pos = -2;
            try {
                pos = textArea.getLineStartOffset(i);
            } catch (BadLocationException ignored) {
            }
            boolean isBreakPoint = fileWindow.isBreakPoint(i + 1);
            text = Integer.toString(i + 1) + " ";
            int y = i * h+3;
            g.setColor(Color.blue);
            g.drawString(text, 3, y + ascent);
            int x = width - ascent;
            if (isBreakPoint) {
                g.setColor(new Color(0x80, 0x00, 0x00));
                int dy = y + ascent - 9;
                g.fillOval(x, dy, 9, 9);
                g.drawOval(x, dy, 8, 8);
                g.drawOval(x, dy, 9, 9);
            }
            if (pos == fileWindow.currentPos) {
                Polygon arrow = new Polygon();
                int dx = x;
                y += ascent - 10;
                int dy = y;
                arrow.addPoint(dx, dy + 3);
                arrow.addPoint(dx + 5, dy + 3);
                for (x = dx + 5; x <= dx + 10; x++, y++) {
                    arrow.addPoint(x, y);
                }
                for (x = dx + 9; x >= dx + 5; x--, y++) {
                    arrow.addPoint(x, y);
                }
                arrow.addPoint(dx + 5, dy + 7);
                arrow.addPoint(dx, dy + 7);
                g.setColor(Color.yellow);
                g.fillPolygon(arrow);
                g.setColor(Color.black);
                g.drawPolygon(arrow);
            }
        }
    }

    // MouseListener

    /**
     * Called when the mouse enters the component.
     */
    public void mouseEntered(MouseEvent e) {
    }
    
    /**
     * Called when a mouse button is pressed.
     */
    public void mousePressed(MouseEvent e) {
        Font font = fileWindow.getTextArea().getFont();
        FontMetrics metrics = getFontMetrics(font);
        int h = metrics.getHeight();
        pressLine = e.getY() / h;
    }

    /**
     * Called when the mouse is clicked.
     */
    public void mouseClicked(MouseEvent e) {
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
        if (e.getComponent() == this
                && (e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
            int y = e.getY();
            Font font = fileWindow.getTextArea().getFont();
            FontMetrics metrics = getFontMetrics(font);
            int h = metrics.getHeight();
            int line = y/h;
            if (line == pressLine) {
                fileWindow.toggleBreakPoint(line + 1);
            } else {
                pressLine = -1;
            }
        }
    }
}


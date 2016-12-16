package com.cic.datacrawl.ui;

import java.awt.Component;

import javax.swing.JOptionPane;


/**
 * Helper class for showing a message dialog.
 */
public class MessageDialogWrapper {

    /**
     * Shows a message dialog, wrapping the <code>msg</code> at 60
     * columns.
     */
    public static void showMessageDialog(Component parent, String msg,
                                         String title, int flags) {
    	if(msg == null)
    		msg = "";
        if (msg.length() > 60) {
            StringBuffer buf = new StringBuffer();
            int len = msg.length();
            int j = 0;
            int i;
            for (i = 0; i < len; i++, j++) {
                char c = msg.charAt(i);
                buf.append(c);
                if (Character.isWhitespace(c)) {
                    int k;
                    for (k = i + 1; k < len; k++) {
                        if (Character.isWhitespace(msg.charAt(k))) {
                            break;
                        }
                    }
                    if (k < len) {
                        int nextWordLen = k - i;
                        if (j + nextWordLen > 60) {
                            buf.append('\n');
                            j = 0;
                        }
                    }
                }
            }
            msg = buf.toString();
        }
        JOptionPane.showMessageDialog(parent, msg, title, flags);
    }
    
    /**
     * Shows a confirm dialog, wrapping the <code>msg</code> at 60
     * columns.
     * @param messageType 
     * @param optionType 
     * @return 
     */
    public static int showConfirmDialog(Component parent, String msg,
                                         String title, int optionType, int messageType) {
        if (msg.length() > 60) {
            StringBuffer buf = new StringBuffer();
            int len = msg.length();
            int j = 0;
            int i;
            for (i = 0; i < len; i++, j++) {
                char c = msg.charAt(i);
                buf.append(c);
                if (Character.isWhitespace(c)) {
                    int k;
                    for (k = i + 1; k < len; k++) {
                        if (Character.isWhitespace(msg.charAt(k))) {
                            break;
                        }
                    }
                    if (k < len) {
                        int nextWordLen = k - i;
                        if (j + nextWordLen > 60) {
                            buf.append('\n');
                            j = 0;
                        }
                    }
                }
            }
            msg = buf.toString();
        }
       return JOptionPane.showConfirmDialog(parent, msg, title, optionType, messageType);
    }
    

    /**
     * Shows a input dialog, wrapping the <code>msg</code> at 60
     * columns.
     * @param messageType 
     * @param optionType 
     * @return 
     */
    public static String showInputDialog(Component parent, String msg,
                                         String title, int messageType) {
        if (msg.length() > 60) {
            StringBuffer buf = new StringBuffer();
            int len = msg.length();
            int j = 0;
            int i;
            for (i = 0; i < len; i++, j++) {
                char c = msg.charAt(i);
                buf.append(c);
                if (Character.isWhitespace(c)) {
                    int k;
                    for (k = i + 1; k < len; k++) {
                        if (Character.isWhitespace(msg.charAt(k))) {
                            break;
                        }
                    }
                    if (k < len) {
                        int nextWordLen = k - i;
                        if (j + nextWordLen > 60) {
                            buf.append('\n');
                            j = 0;
                        }
                    }
                }
            }
            msg = buf.toString();
        }
       return JOptionPane.showInputDialog(parent, msg, title, messageType);
    }
}


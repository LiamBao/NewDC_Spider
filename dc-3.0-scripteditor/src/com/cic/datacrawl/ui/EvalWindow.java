package com.cic.datacrawl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import com.cic.datacrawl.ui.tools.CommandConstants;


/**
 * An internal frame for evaluating script.
 */
public class EvalWindow extends JInternalFrame implements ActionListener {

    /**
     * Serializable magic number.
     */
    private static final long serialVersionUID = -2860585845212160176L;

    /**
     * The text area into which expressions can be typed.
     */
    private EvalTextArea evalTextArea;

    /**
     * Creates a new EvalWindow.
     */
    public EvalWindow(String name, SwingGui debugGui) {
        super(name, true, false, true, true);
        evalTextArea = new EvalTextArea(debugGui);
        evalTextArea.setRows(24);
        evalTextArea.setColumns(80);
        JScrollPane scroller = new JScrollPane(evalTextArea);
        setContentPane(scroller);
        //scroller.setPreferredSize(new Dimension(600, 400));
        pack();
        setVisible(true);
    }

    /**
     * Sets whether the text area is enabled.
     */
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        evalTextArea.setEnabled(b);
    }

    // ActionListener

    /**
     * Performs an action on the text area.
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals(CommandConstants.CUT.getCmd())) {
            evalTextArea.cut();
        } else if (cmd.equals(CommandConstants.COPY.getCmd())) {
            evalTextArea.copy();
        } else if (cmd.equals(CommandConstants.PASTE.getCmd())) {
            evalTextArea.paste();
        }
    }
}



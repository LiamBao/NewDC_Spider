package com.cic.datacrawl.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.InputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.mozilla.javascript.tools.shell.ConsoleTextArea;

import com.cic.datacrawl.ui.tools.CommandConstants;

/**
 * Internal frame for the console.
 */
public class JSInternalConsole extends JPanel implements ActionListener {

	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = -5523468828771087292L;

	/**
	 * Creates a new JSInternalConsole.
	 */
	public JSInternalConsole() {
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);

		consoleTextArea = new ConsoleTextArea(null);
		consoleTextArea.setRows(24);
		consoleTextArea.setColumns(80);

		JScrollPane scroller = new JScrollPane(consoleTextArea);

		this.add(scroller, BorderLayout.CENTER);

		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				// hack
				if (consoleTextArea.hasFocus()) {
					consoleTextArea.getCaret().setVisible(false);
					consoleTextArea.getCaret().setVisible(true);
				}
			}

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * The console text area.
	 */
	public ConsoleTextArea consoleTextArea;

	/**
	 * Returns the input stream of the console text area.
	 */
	public InputStream getIn() {
		return consoleTextArea.getIn();
	}

	/**
	 * Returns the output stream of the console text area.
	 */
	public PrintStream getOut() {
		return consoleTextArea.getOut();
	}

	/**
	 * Returns the error stream of the console text area.
	 */
	public PrintStream getErr() {
		return consoleTextArea.getErr();
	}

	// ActionListener

	/**
	 * Performs an action on the text area.
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(CommandConstants.CUT.getCmd())) {
			consoleTextArea.cut();
		} else if (cmd.equals(CommandConstants.COPY.getCmd())) {
			consoleTextArea.copy();
		} else if (cmd.equals(CommandConstants.PASTE.getCmd())) {
			consoleTextArea.paste();
		}

	}
}

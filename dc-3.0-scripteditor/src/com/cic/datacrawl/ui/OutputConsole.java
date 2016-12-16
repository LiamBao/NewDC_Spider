package com.cic.datacrawl.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.cic.datacrawl.ui.tools.CommandConstants;

/**
 * Internal frame for the console.
 */
public class OutputConsole extends JPanel implements ActionListener {

	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = -5523468828771087292L;

	private JSToolBar consoleToolbar;
	private JScrollPane scroller;

	/**
	 * Creates a new JSInternalConsole.
	 */
	public OutputConsole() {
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);
		consoleToolbar = new JSToolBar(
				CommandConstants.GROUP_TOOLBAR_OUTPUT_CONSOLE, this);

		this.add(consoleToolbar, BorderLayout.NORTH);

		outputTextArea = new OutputTextArea(null);
		outputTextArea.setRows(24);
		outputTextArea.setColumns(80);

		scroller = new JScrollPane(outputTextArea);

		this.add(scroller, BorderLayout.CENTER);

		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				// hack
				if (outputTextArea.hasFocus()) {
					outputTextArea.getCaret().setVisible(false);
					outputTextArea.getCaret().setVisible(true);
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
		setLock(false);
	}

	/**
	 * The console text area.
	 */
	public OutputTextArea outputTextArea;

	/**
	 * Returns the output stream of the console text area.
	 */
	public PrintStream getOut() {
		return outputTextArea.getOut();
	}

	/**
	 * Returns the error stream of the console text area.
	 */
	public PrintStream getErr() {
		return outputTextArea.getErr();
	}

	// ActionListener

	/**
	 * Performs an action on the text area.
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(CommandConstants.CUT.getCmd())) {
			outputTextArea.cut();
		} else if (cmd.equals(CommandConstants.COPY.getCmd())) {
			outputTextArea.copy();
		} else if (cmd.equals(CommandConstants.PASTE.getCmd())) {
			outputTextArea.paste();
		} else if (cmd.equals(CommandConstants.NO_WRAP.getCmd())) {
			setWrap(false);
		} else if (cmd.equals(CommandConstants.AUTO_WRAP.getCmd())) {
			setWrap(true);
		} else if (cmd.equals(CommandConstants.LOCK.getCmd())) {
			setLock(true);
		} else if (cmd.equals(CommandConstants.UNLOCK.getCmd())) {
			setLock(false);
		} else {
			outputTextArea.actionPerformed(e);
		}

	}

	private void setWrap(boolean isAutoWrap) {
		JButton btnNoWrap = consoleToolbar.getButton(CommandConstants.NO_WRAP
				.getCmd());
		JButton btnAutoWrap = consoleToolbar
				.getButton(CommandConstants.AUTO_WRAP.getCmd());
		btnAutoWrap.setEnabled(!isAutoWrap);
		btnNoWrap.setEnabled(isAutoWrap);
		outputTextArea.setLineWrap(isAutoWrap);
	}

	private void setLock(boolean isLocked) {
		JButton btnLock = consoleToolbar.getButton(CommandConstants.LOCK
				.getCmd());
		JButton btnUnlock = consoleToolbar.getButton(CommandConstants.UNLOCK
				.getCmd());
		
		btnLock.setEnabled(!isLocked);
		btnUnlock.setEnabled(isLocked);
		if (isLocked)
			outputTextArea.lock();
		else {
			outputTextArea.unlock();
		}
	}
}

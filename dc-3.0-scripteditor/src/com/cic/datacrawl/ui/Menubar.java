package com.cic.datacrawl.ui;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.cic.datacrawl.ui.tools.CommandConstants;
import com.cic.datacrawl.ui.tools.ItemFactory;

/**
 * The debugger frame menu bar.
 */
public class Menubar extends JMenuBar implements ActionListener {

	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = 3217170497245911461L;

	/**
	 * Items that are enabled only when interrupted.
	 */
	private List<JMenuItem> interruptOnlyItems = Collections
			.synchronizedList(new ArrayList<JMenuItem>());

	/**
	 * Items that are enabled only when running.
	 */
	private List<JMenuItem> runOnlyItems = Collections
			.synchronizedList(new ArrayList<JMenuItem>());

	/**
	 * The debugger GUI.
	 */
	private SwingGui debugGui;

	/**
	 * The menu listing the internal frames.
	 */
	private JMenu windowMenu;

	/**
	 * The "Break on exceptions" menu item.
	 */
	private JCheckBoxMenuItem breakOnExceptions;

	/**
	 * The "Break on enter" menu item.
	 */
	private JCheckBoxMenuItem breakOnEnter;

	/**
	 * The "Break on return" menu item.
	 */
	private JCheckBoxMenuItem breakOnReturn;

	private JMenu fileMenu;

	private JMenu editMenu;

	private JMenu plafMenu;

	private JMenu debugMenu;

	/**
	 * Creates a new Menubar.
	 */
	public Menubar(SwingGui debugGui) {
		super();
		this.debugGui = debugGui;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		plafMenu = new JMenu("Platform");
		plafMenu.setMnemonic('P');
		debugMenu = new JMenu("Debug");
		debugMenu.setMnemonic('D');
		windowMenu = new JMenu("Window");
		windowMenu.setMnemonic('W');
		for (int i = 0; i < CommandConstants.GROUP_MENU_FILE.length; ++i) {
			JMenuItem item = ItemFactory.createMenuItem(
					CommandConstants.GROUP_MENU_FILE[i], Event.CTRL_MASK);
			if (item == null) {
				fileMenu.addSeparator();
			} else {
				item.addActionListener(this);
				fileMenu.add(item);
			}
		}
		for (int i = 0; i < CommandConstants.GROUP_MENU_EDIT.length; ++i) {
			JMenuItem item = null;
			if (CommandConstants.GROUP_MENU_EDIT[i] != null) {
				if (CommandConstants.GROUP_MENU_EDIT[i].getCmd() == CommandConstants.SEARCH_NEXT
						.getCmd()) {
					item = ItemFactory.createMenuItem(
							CommandConstants.GROUP_MENU_EDIT[i], 0);
				} else if (CommandConstants.GROUP_MENU_EDIT[i].getCmd() == CommandConstants.CODE_FORMAT
						.getCmd()) {
					item = ItemFactory.createMenuItem(
							CommandConstants.GROUP_MENU_EDIT[i],
							Event.SHIFT_MASK + Event.CTRL_MASK);

				} else if (CommandConstants.GROUP_MENU_EDIT[i].getCmd() == CommandConstants.SEARCH_PREV
						.getCmd()) {
					item = ItemFactory.createMenuItem(
							CommandConstants.GROUP_MENU_EDIT[i],
							Event.SHIFT_MASK);
				} else if (CommandConstants.GROUP_MENU_EDIT[i].getCmd() == CommandConstants.REMOVE_ALL_HIGHLIGHT
						.getCmd()) {
					item = ItemFactory
							.createMenuItem(
									CommandConstants.GROUP_MENU_EDIT[i],
									Event.ALT_MASK);
				} else {
					item = ItemFactory.createMenuItem(
							CommandConstants.GROUP_MENU_EDIT[i],
							Event.CTRL_MASK);
				}
			}
			if (item == null) {
				editMenu.addSeparator();
			} else {

				item.addActionListener(this);
				editMenu.add(item);
			}
		}
		for (int i = 0; i < CommandConstants.GROUP_MENU_PLAF.length; ++i) {
			JMenuItem item = ItemFactory.createMenuItem(
					CommandConstants.GROUP_MENU_PLAF[i], Event.CTRL_MASK);
			if (item == null) {
				plafMenu.addSeparator();
			} else {
				item.addActionListener(this);
				plafMenu.add(item);
			}
		}
		for (int i = 0; i < CommandConstants.GROUP_MENU_DEBUG.length; ++i) {
			JMenuItem item = ItemFactory.createMenuItem(
					CommandConstants.GROUP_MENU_DEBUG[i], 0);

			if (item == null) {
				debugMenu.addSeparator();
			} else {
				item.addActionListener(this);

				if (i != 0) {
					interruptOnlyItems.add(item);
				} else {
					runOnlyItems.add(item);
				}
				debugMenu.add(item);
			}
		}
		breakOnExceptions = new JCheckBoxMenuItem("Break on Exceptions");
		breakOnExceptions.setMnemonic('X');
		breakOnExceptions.addActionListener(this);
		breakOnExceptions.setSelected(false);
		debugMenu.add(breakOnExceptions);

		breakOnEnter = new JCheckBoxMenuItem("Break on Function Enter");
		breakOnEnter.setMnemonic('E');
		breakOnEnter.addActionListener(this);
		breakOnEnter.setSelected(false);
		debugMenu.add(breakOnEnter);

		breakOnReturn = new JCheckBoxMenuItem("Break on Function Return");
		breakOnReturn.setMnemonic('R');
		breakOnReturn.addActionListener(this);
		breakOnReturn.setSelected(false);
		debugMenu.add(breakOnReturn);

		for (int i = 0; i < CommandConstants.GROUP_MENU_WINDOW.length; ++i) {
			JMenuItem item = ItemFactory.createMenuItem(
					CommandConstants.GROUP_MENU_WINDOW[i], Event.CTRL_MASK);

			if (item == null) {
				windowMenu.addSeparator();
			} else {
				item.addActionListener(this);

				windowMenu.add(item);
			}
		}

		add(fileMenu);
		add(editMenu);
		// add(plafMenu);
		add(debugMenu);
		add(windowMenu);

		updateEnabled(false);
	}

	/**
	 * Returns the "Break on exceptions" menu item.
	 */
	public JCheckBoxMenuItem getBreakOnExceptions() {
		return breakOnExceptions;
	}

	/**
	 * Returns the "Break on enter" menu item.
	 */
	public JCheckBoxMenuItem getBreakOnEnter() {
		return breakOnEnter;
	}

	/**
	 * Returns the "Break on return" menu item.
	 */
	public JCheckBoxMenuItem getBreakOnReturn() {
		return breakOnReturn;
	}

	/**
	 * Returns the "Debug" menu.
	 */
	public JMenu getDebugMenu() {
		return getMenu(2);
	}

	// ActionListener

	/**
	 * Performs an action.
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		String plaf_name = null;
		if (cmd.equals(CommandConstants.PLAF_METAL.getTitle())) {
			plaf_name = "javax.swing.plaf.metal.MetalLookAndFeel";
		} else if (cmd.equals(CommandConstants.PLAF_WINDOWS.getTitle())) {
			plaf_name = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		} else if (cmd.equals(CommandConstants.PLAF_MOTIF.getTitle())) {
			plaf_name = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		} else {
			Object source = e.getSource();
			if (source == breakOnExceptions) {
				debugGui.dim.setBreakOnExceptions(breakOnExceptions
						.isSelected());
			} else if (source == breakOnEnter) {
				debugGui.dim.setBreakOnEnter(breakOnEnter.isSelected());
			} else if (source == breakOnReturn) {
				debugGui.dim.setBreakOnReturn(breakOnReturn.isSelected());
			} else {
				debugGui.actionPerformed(e);
			}
			return;
		}
		try {
			UIManager.setLookAndFeel(plaf_name);
			SwingUtilities.updateComponentTreeUI(debugGui);
			SwingUtilities.updateComponentTreeUI(debugGui.getDlg());
		} catch (Exception ignored) {
			// ignored.printStackTrace();
		}
	}

	/**
	 * Adds a file to the window menu.
	 */
	public void addFile(String url) {
		int count = windowMenu.getItemCount();
		JMenuItem item;
		if (count == CommandConstants.GROUP_MENU_WINDOW.length) {
			windowMenu.addSeparator();
			count++;
		}
		JMenuItem lastItem = windowMenu.getItem(count - 1);
		boolean hasMoreWin = false;
		int maxWin = 5;
		if (lastItem != null
				&& lastItem.getText().equals(
						CommandConstants.MORE_WINDOWS.getTitle())) {
			hasMoreWin = true;
			maxWin++;
		}
		if (!hasMoreWin
				&& count - CommandConstants.GROUP_MENU_WINDOW.length == 5) {
			windowMenu.add(item = ItemFactory.createMenuItem(
					CommandConstants.MORE_WINDOWS, Event.CTRL_MASK));
			item.addActionListener(this);
			return;
		} else if (count - CommandConstants.GROUP_MENU_WINDOW.length <= maxWin) {
			if (hasMoreWin) {
				count--;
				windowMenu.remove(lastItem);
			}
//			String shortName = SwingGui.getShortName(url);
			String shortName = url;

			windowMenu
					.add(item = new JMenuItem(
							(char) ('0' + (count - CommandConstants.GROUP_MENU_WINDOW.length))
									+ " " + shortName,
							'0' + (count - CommandConstants.GROUP_MENU_WINDOW.length)));
			if (hasMoreWin) {
				windowMenu.add(lastItem);
			}
		} else {
			return;
		}
		item.setActionCommand(url);
		item.addActionListener(this);
	}

	/**
	 * Updates the enabledness of menu items.
	 */
	public void updateEnabled(boolean interrupted) {
		for (int i = 0; i != interruptOnlyItems.size(); ++i) {
			JMenuItem item = interruptOnlyItems.get(i);
			item.setEnabled(interrupted);
		}

		for (int i = 0; i != runOnlyItems.size(); ++i) {
			JMenuItem item = runOnlyItems.get(i);
			item.setEnabled(!interrupted);
		}
	}

	public void doHaveFileWindow() {
		// TODO Auto-generated method stub

	}

	public void doNoneFileWindow() {
		// TODO Auto-generated method stub

	}
}

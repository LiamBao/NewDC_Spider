package com.cic.datacrawl.ui;

import java.awt.Component;
import java.awt.Event;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.cic.datacrawl.ui.tools.CommandConstants;
import com.cic.datacrawl.ui.tools.ItemDesc;
import com.cic.datacrawl.ui.tools.ItemFactory;

/**
 * Popup menu class for right-clicking on {@link FileTextArea}s.
 */
public class ConsolePopupMenu extends JPopupMenu {

	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = 3589525009546013565L;

	/**
	 * The popup x position.
	 */
	public int x;

	/**
	 * The popup y position.
	 */
	public int y;

	public ConsolePopupMenu(OutputTextArea outputTextArea) {
		JMenuItem item;
		for (int i = 0; i < CommandConstants.GROUP_MENU_CONSOLE_POPUP.length; ++i) {
			ItemDesc desc = CommandConstants.GROUP_MENU_CONSOLE_POPUP[i];
			if (desc == null) {
				addSeparator();
			} else {
				add(item = ItemFactory.createMenuItem(desc, Event.CTRL_MASK));
				item.addActionListener(outputTextArea);
			}
		}
	}

	/**
	 * Displays the menu at the given coordinates.
	 */
	public void show(JComponent comp, int x, int y) {
		this.x = x;
		this.y = y;
		super.show(comp, x, y);
	}

	private ItemDesc[] selectedDescs = { CommandConstants.COPY };

	public void setHasSelectText(boolean hasSelectText) {
		Component[] items = getComponents();
		for (int i = 0; i < items.length; ++i) {
			if (items[i] instanceof JMenuItem) {
				JMenuItem item = (JMenuItem) items[i];
				for (int j = 0; j < selectedDescs.length; ++j) {
					if (selectedDescs[j].getCmd().equals(
							item.getActionCommand())) {
						item.setEnabled(hasSelectText);
						break;
					}
				}
			}
		}
	}
}

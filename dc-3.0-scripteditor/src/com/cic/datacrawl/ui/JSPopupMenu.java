package com.cic.datacrawl.ui;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.cic.datacrawl.ui.tools.ItemDesc;
import com.cic.datacrawl.ui.tools.ItemFactory;

public class JSPopupMenu extends JPopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2233801746090786484L;
	/**
	 * Hash table of toolbar button command to the toolbar buttons themselves.
	 */
	private final Map<String, JMenuItem> itemMap = Collections
			.synchronizedMap(new HashMap<String, JMenuItem>());

	public JMenuItem getButton(String ActionCommand) {
		return itemMap.get(ActionCommand);
	}

	private final Map<String, ItemDesc[]> visibleButtonGroup = Collections
			.synchronizedMap(new HashMap<String, ItemDesc[]>());

	public void addVisibleButtonGroup(String name, ItemDesc[] buttonDefineGroup) {
		if (name == null || buttonDefineGroup == null)
			throw new NullPointerException();
		visibleButtonGroup.put(name, buttonDefineGroup);
	}

	/**
	 * The popup x position.
	 */
	public int x;

	/**
	 * The popup y position.
	 */
	public int y;

	/**
	 * Displays the menu at the given coordinates.
	 */
	public void show(JComponent comp, int x, int y) {
		this.x = x;
		this.y = y;
		super.show(comp, x, y);
	}

	public ItemDesc[] getVisibleButtonGroup(String name) {
		if (name == null)
			throw new NullPointerException();
		return visibleButtonGroup.get(name);
	}

	private final Map<String, ItemDesc[]> enabledButtonGroup = Collections
			.synchronizedMap(new HashMap<String, ItemDesc[]>());

	public void addEnabledButtonGroup(String name, ItemDesc[] buttonDefineGroup) {
		if (name == null || buttonDefineGroup == null)
			throw new NullPointerException();
		enabledButtonGroup.put(name, buttonDefineGroup);
	}

	public ItemDesc[] getEnabledButtonGroup(String name) {
		if (name == null)
			throw new NullPointerException();
		return enabledButtonGroup.get(name);
	}

	public void setStatus(String name) {
		Component[] items = getComponents();
		setEnabledStatus(name, items);
		setVisibledStatus(name, items);
	}

	public void setVisibledStatus(String name) {
		setVisibledStatus(name, getComponents());
	}

	public JSPopupMenu(ItemDesc[] itemDescGroup, ActionListener listener) {
		this.itemDescs = itemDescGroup;
		init(listener);
	}

	public void setVisibledStatus(String name, Component[] items) {
		ItemDesc[] descGroup = getEnabledButtonGroup(name);
		if (descGroup != null)
			for (int i = 0; i < items.length; ++i) {
				if (items[i] instanceof JMenuItem) {
					JMenuItem item = (JMenuItem) items[i];
					boolean isVisibled = true;
					for (int j = 0; j < descGroup.length; ++j) {
						if (descGroup[j] != null && descGroup[j].getCmd().equals(
								item.getActionCommand())) {
							item.setVisible(true);
							isVisibled = false;
							break;
						}
					}
					if (isVisibled) {
						item.setVisible(false);
					}
				}
			}
	}

	public void resetStatus() {
		Component[] items = getComponents();
		if (items != null)
			for (int i = 0; i < items.length; ++i) {
				if (items[i] instanceof JMenuItem) {
					JMenuItem item = (JMenuItem) items[i];
					for (int j = 0; j < itemDescs.length; ++j) {
						if (itemDescs[j] != null && itemDescs[j].getCmd().equals(
								item.getActionCommand())) {
							item.setEnabled(itemDescs[j].isDefaultEnable());
							item.setVisible(itemDescs[j].isDefaultVisible());
							break;
						}
					}
				}
			}
	}

	public void setEnabledStatus(String name) {
		setEnabledStatus(name, getComponents());
	}

	public void setEnabledStatus(String name, Component[] items) {
		ItemDesc[] descGroup = getEnabledButtonGroup(name);
		if (descGroup != null)
			for (int i = 0; i < items.length; ++i) {
				if (items[i] instanceof JMenuItem) {
					JMenuItem item = (JMenuItem) items[i];
					boolean isDisabled = true;
					for (int j = 0; j < descGroup.length; ++j) {
						if (descGroup[j] != null && descGroup[j].getCmd().equals(
								item.getActionCommand())) {
							item.setEnabled(true);
							isDisabled = false;
							break;
						}
					}
					if (isDisabled) {
						item.setEnabled(false);
					}
				}
			}
	}

	protected void init(ActionListener listener) {
		if (itemDescs != null) {
			for (int i = 0; i < itemDescs.length; ++i) {
				if (itemDescs[i] == null) {
					addSeparator();
				} else {
					JMenuItem item = ItemFactory.createMenuItem(itemDescs[i],Event.CTRL_MASK);
					item.addActionListener(listener);
					add(item);
					itemMap.put(itemDescs[i].getCmd(), item);
				}
			}
		}
	}

	protected ItemDesc[] itemDescs;

}

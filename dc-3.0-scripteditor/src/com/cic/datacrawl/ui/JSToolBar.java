package com.cic.datacrawl.ui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JToolBar;

import com.cic.datacrawl.ui.tools.ItemDesc;
import com.cic.datacrawl.ui.tools.ItemFactory;

public class JSToolBar extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6951354777208258189L;
	/**
	 * Hash table of toolbar button command to the toolbar buttons themselves.
	 */
	protected final Map<String, JButton> toolbarButton = Collections
			.synchronizedMap(new HashMap<String, JButton>());

	public JButton getButton(String ActionCommand) {
		return toolbarButton.get(ActionCommand);
	}

	protected final Map<String, ItemDesc[]> visibleButtonGroup = Collections
			.synchronizedMap(new HashMap<String, ItemDesc[]>());

	public void addVisibleButtonGroup(String name, ItemDesc[] buttonDefineGroup) {
		if (name == null || buttonDefineGroup == null)
			throw new NullPointerException();
		visibleButtonGroup.put(name, buttonDefineGroup);
	}

	public ItemDesc[] getVisibleButtonGroup(String name) {
		if (name == null)
			throw new NullPointerException();
		return visibleButtonGroup.get(name);
	}

	protected final Map<String, ItemDesc[]> enabledButtonGroup = Collections
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

	public void setVisibledStatus(String name, Component[] items) {
		ItemDesc[] buttonDescGroup = getEnabledButtonGroup(name);
		if (buttonDescGroup != null)
			for (int i = 0; i < items.length; ++i) {
				if (items[i] instanceof JButton) {
					JButton item = (JButton) items[i];
					boolean isVisibled = true;
					for (int j = 0; j < buttonDescGroup.length; ++j) {
						if (buttonDescGroup[j] != null
								&& buttonDescGroup[j].getCmd().equals(
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

	public void setEnabledStatus(String name) {
		setEnabledStatus(name, getComponents());
	}

	public void setEnabledStatus(String name, Component[] items) {
		ItemDesc[] buttonDescGroup = getEnabledButtonGroup(name);
		if (buttonDescGroup != null)
			for (int i = 0; i < items.length; ++i) {
				if (items[i] instanceof JButton) {
					JButton item = (JButton) items[i];
					boolean isDisabled = true;
					for (int j = 0; j < buttonDescGroup.length; ++j) {
						if (buttonDescGroup[j] != null
								&& buttonDescGroup[j].getCmd().equals(
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

	public JSToolBar(ItemDesc[] itemDescGroup, ActionListener listener) {
		this.itemDescs = itemDescGroup;
		init(listener);
	}

	public void resetStatus() {
		Component[] items = getComponents();
		if (items != null)
			for (int i = 0; i < items.length; ++i) {
				if (items[i] instanceof JButton) {
					JButton item = (JButton) items[i];
					for (int j = 0; j < itemDescs.length; ++j) {
						try {
							if (itemDescs[j] != null
									&& itemDescs[j].getCmd().equals(
											item.getActionCommand())) {
								item.setEnabled(itemDescs[j].isDefaultEnable());
								item
										.setVisible(itemDescs[j]
												.isDefaultVisible());
								break;
							}
						} catch (Exception e) {
						}
					}
				}
			}
	}

	protected ItemDesc[] itemDescs;

	protected void init(ActionListener listener) {
		for (int i = 0; i < itemDescs.length; ++i) {
			if (itemDescs[i] == null) {
				addSeparator();
			} else {
				JButton button = ItemFactory.createButton(itemDescs[i]);
				button.addActionListener(listener);
				add(button);
				toolbarButton.put(itemDescs[i].getCmd(), button);
			}
		}
		setFloatable(false);
	}

}

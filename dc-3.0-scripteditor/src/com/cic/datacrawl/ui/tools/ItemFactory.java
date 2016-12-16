package com.cic.datacrawl.ui.tools;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.cic.datacrawl.ui.utils.WindowUtils;

public class ItemFactory {
	private static ItemFactory factory = new ItemFactory();

	public static JMenuItem createMenuItem(ItemDesc itemDesc, int keyMask) {
		return factory.createNewMenuItem(itemDesc,  keyMask);
	}

	public static JButton createButton(ItemDesc itemDesc) {
		return factory.createNewButton(itemDesc);
	}

	private JMenuItem createNewMenuItem(ItemDesc itemDesc, int keyMask) {
		if (itemDesc == null)
			return null;
		JMenuItem newItem = new JMenuItem();
		newItem.setText(itemDesc.getTitle());
		if (itemDesc.getIcon() != null
				&& itemDesc.getIcon().trim().length() > 0) {
			try {
				newItem.setIcon(new ImageIcon(factory.getClass()
						.getClassLoader().getResource(itemDesc.getIcon())));
			} catch (Exception e) {
			}
		}

		if (itemDesc.getTip() != null && itemDesc.getTip().trim().length() > 0) {
			newItem.setToolTipText(itemDesc.getTip());
		}
		if (itemDesc.getShortcut() > 0) {
			newItem.setMnemonic(itemDesc.getShortcut());
		}
		if (itemDesc.getCmd() != null && itemDesc.getCmd().trim().length() > 0) {
			newItem.setActionCommand(itemDesc.getCmd());
		}
		if (itemDesc.getAccelerator() > 0) {
			if (keyMask < 0)
				keyMask = 0;
			KeyStroke k = KeyStroke.getKeyStroke(itemDesc.getAccelerator(),
					keyMask);
			newItem.setAccelerator(k);
		}
		newItem.setVisible(itemDesc.isDefaultVisible());
		newItem.setEnabled(itemDesc.isDefaultEnable());
		return newItem;
	}

	private JButton createNewButton(ItemDesc itemDesc) {
		if (itemDesc == null)
			return null;
		JButton newButton = new JButton();
		boolean hasIcon = false;

		if (itemDesc.getIcon() != null
				&& itemDesc.getIcon().trim().length() > 0) {
			try {
				newButton.setIcon(new ImageIcon(factory.getClass()
						.getClassLoader().getResource(itemDesc.getIcon())));

				hasIcon = true;
			} catch (Exception e) {
			}
		} else {
			newButton.setText(itemDesc.getTitle());
		}
		if (hasIcon) {
			newButton.setPreferredSize(WindowUtils.DEFAULT_BUTTON_SIZE);
			newButton.setMinimumSize(WindowUtils.DEFAULT_BUTTON_SIZE);
			newButton.setMaximumSize(WindowUtils.DEFAULT_BUTTON_SIZE);
			newButton.setSize(WindowUtils.DEFAULT_BUTTON_SIZE);
		}
		if (itemDesc.getTip() != null && itemDesc.getTip().trim().length() > 0) {
			newButton.setToolTipText(itemDesc.getTip());
		}
		if (itemDesc.getShortcut() > 0) {
			newButton.setMnemonic(itemDesc.getShortcut());
		}
		if (itemDesc.getCmd() != null && itemDesc.getCmd().trim().length() > 0) {
			newButton.setActionCommand(itemDesc.getCmd());
		}
		newButton.setVisible(itemDesc.isDefaultVisible());
		newButton.setEnabled(itemDesc.isDefaultEnable());

		return newButton;
	}
}

package com.cic.datacrawl.ui.tools;

public class ItemGroup extends MenuItem {

	private MenuItem[] items;

	private char shortcut;

	private String title;

	public ItemGroup(String title, char shortcut, int defaultType, MenuItem... item) {
		items = item;
		this.title = title;
		this.shortcut = shortcut;
		this.defaultType = defaultType;
	}

	/**
	 * @return the items
	 */
	public MenuItem[] getItems() {
		return items;
	}

	/**
	 * @return the shortcut
	 */
	@Override
	public char getShortcut() {
		return shortcut;
	}

	/**
	 * @return the title
	 */
	@Override
	public String getTitle() {
		return title;
	}

}

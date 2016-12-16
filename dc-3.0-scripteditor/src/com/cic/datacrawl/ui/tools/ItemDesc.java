package com.cic.datacrawl.ui.tools;

public class ItemDesc extends MenuItem{
	private String title;
	private char shortcut;
	private int accelerator;
	private String cmd;
	private String tip;
	private String icon;

	public ItemDesc(String title, char shortcut, int accelerator, String cmd,
			String tip, String icon) {
		this(title, shortcut, accelerator, cmd, tip, icon, DEFAULT_ENABLE_VISIBLE);
	}
	public ItemDesc(String title, char shortcut, int accelerator, String cmd,
			String tip, String icon,int defaultType) {
		super();
		this.title = title;
		this.shortcut = shortcut;
		this.accelerator = accelerator;
		this.cmd = cmd;
		this.tip = tip;
		this.icon = icon;
		this.defaultType = defaultType;
	}
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @return the title
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * @return the shortcut
	 */
	@Override
	public char getShortcut() {
		return shortcut;
	}

	/**
	 * @return the accelerator
	 */
	public int getAccelerator() {
		return accelerator;
	}

	/**
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}

	/**
	 * @return the tip
	 */
	public String getTip() {
		return tip;
	}


}

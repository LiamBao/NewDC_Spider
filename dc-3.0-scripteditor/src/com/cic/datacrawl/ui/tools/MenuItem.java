package com.cic.datacrawl.ui.tools;

public abstract class MenuItem {

	public static final int DEFAULT_ENABLE_VISIBLE = 0;
	public static final int DEFAULT_DISABLE_VISIBLE = 1;
	public static final int DEFAULT_ENABLE_INVISIBLE = 2;
	public static final int DEFAULT_DISABLE_INVISIBLE = 3;
	protected int defaultType;

	/**
	 * @return the defaultVisible
	 */
	public boolean isDefaultVisible() {
		return defaultType == DEFAULT_ENABLE_VISIBLE || defaultType == DEFAULT_DISABLE_VISIBLE;
	}
	/**
	 * @return the defaultEnable
	 */
	public boolean isDefaultEnable() {
		return defaultType == DEFAULT_ENABLE_VISIBLE || defaultType == DEFAULT_ENABLE_INVISIBLE;
	}
	/**
	 * @return the shortcut
	 */
	public abstract char getShortcut() ;

	/**
	 * @return the title
	 */
	public abstract String getTitle() ;

}

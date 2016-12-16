package com.cic.datacrawl.ui.beans;

import java.util.List;

public class BrowserDefination {
	private String name;

	public BrowserDefination() {
		super();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @param isDefault the isDefault to set
	 */
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the isDefault
	 */
	public boolean isDefault() {
		return isDefault;
	}
	

	public List<String> getBrowserIgnoreUrlList() {
		return browserIgnoreUrlList;
	}

	public void setBrowserIgnoreUrlList(List<String> browserIgnoreUrlList) {
		this.browserIgnoreUrlList = browserIgnoreUrlList;
	}

	private String className;
	private boolean isDefault;
	private List<String> browserIgnoreUrlList;

}

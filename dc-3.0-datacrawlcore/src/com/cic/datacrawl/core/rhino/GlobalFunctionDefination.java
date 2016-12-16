package com.cic.datacrawl.core.rhino;

public class GlobalFunctionDefination {
	private int attributes;
	private Class clazz;
	private String[] names;

	public GlobalFunctionDefination() {
		super();
	}

	public GlobalFunctionDefination(String[] names, Class clazz) {
		super();
		this.names = names;
		this.clazz = clazz;
	}

	public GlobalFunctionDefination(String[] names, Class clazz, int attributes) {
		super();
		this.names = names;
		this.clazz = clazz;
		this.attributes = attributes;
	}

	/**
	 * @return the attributes
	 */
	public int getAttributes() {
		return attributes;
	}

	/**
	 * @return the clazz
	 */
	public Class getClazz() {
		return clazz;
	}

	/**
	 * @return the names
	 */
	public String[] getNames() {
		return names;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(int attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param clazz
	 *            the clazz to set
	 */
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	/**
	 * @param names
	 *            the names to set
	 */
	public void setNames(String[] names) {
		this.names = names;
	}
}

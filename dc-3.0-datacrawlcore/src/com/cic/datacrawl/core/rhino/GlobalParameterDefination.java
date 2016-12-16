package com.cic.datacrawl.core.rhino;

public class GlobalParameterDefination {
	private Object instance;
	private String name;

	public GlobalParameterDefination() {
		super();
	}

	public GlobalParameterDefination(String name, Object instance) {
		super();
		this.name = name;
		this.instance = instance;
	}

	/**
	 * @return the instance
	 */
	public Object getInstance() {
		return instance;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public void setInstance(Object instance) {
		this.instance = instance;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}

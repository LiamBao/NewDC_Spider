package com.cic.datacrawl.core.browser.entity;

public abstract class IFrameName {

	public static final String MAIN_FRAME_NAME = "Main_Frame";

	private String frameName;
	private String showName;
	private int index;

	private IFrameName parentFrameName;

	/**
	 * @param parentFrameName
	 *            the parentFrameName to set
	 */
	public void setParentFrameName(IFrameName parentFrameName) {
		this.parentFrameName = parentFrameName;
	}

	public abstract IFrameName getDefaultParent();

	/**
	 * @return the parentFrameName
	 */
	public IFrameName getParentFrameName() {
		if (this.getShowName().equals(getDefaultParent().getShowName()))
			return null;
		if (parentFrameName == null)
			parentFrameName = getDefaultParent();
		return parentFrameName;
	}

	public IFrameName() {
		super();
	}

	public IFrameName(String frameName, int index) {
		super();
		this.frameName = frameName;
		this.index = index;
	}

	/**
	 * @return the frameName
	 */
	public String getFrameName() {
		return frameName;
	}

	/**
	 * @param frameName
	 *            the frameName to set
	 */
	public void setFrameName(String frameName) {
		this.frameName = frameName;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		if (this.parentFrameName == null)
			return 0;

		return this.parentFrameName.getLevel() + 1;
	}

	@Override
	public boolean equals(Object o){
		if(!(o instanceof IFrameName)){
			return false;
		}
		IFrameName frameName = (IFrameName) o;
		if(frameName.getIndex() != getIndex()){
			return false;
		}
		if(frameName.getLevel() != getLevel()){
			return false;
		}
		if(!frameName.getFrameName().equals(getFrameName())){
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder(getShowName());
		if (getParentFrameName() != null) {
			ret.append(" ------- Parent Frame: ");
			ret.append(getParentFrameName().getFrameName());
		}
		return ret.toString();
	}

	public String getShowName() {
		if (showName == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < getLevel(); ++i) {
				sb.append("    ");
			}
			sb.append(getFrameName());
			if (getIndex() > 0) {
				sb.append("[");
				sb.append(getIndex());
				sb.append("]");
			}
			showName = sb.toString();
		}
		return showName;
	}

	public abstract DomNode getDomNode();
}

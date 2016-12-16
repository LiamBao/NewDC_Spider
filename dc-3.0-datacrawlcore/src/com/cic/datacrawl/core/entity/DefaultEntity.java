package com.cic.datacrawl.core.entity;



/**
 * @author Rex.wu
 */
public class DefaultEntity extends BaseEntity {
	public DefaultEntity(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8559375425239414365L;

	@Override
	public BaseEntity getDefaultEmptyBean() {
		return null;
	}

	private String tableName;

	@Override
	public String getTheEntityName() {
		if (tableName == null || tableName.trim().length() == 0) {
			tableName = "DefaultEntity";
		}
		return tableName;
	}

	protected String[] initOtherColumns() {
		return new String[0];
	}

	protected String[] initOtherProperties() {
		return new String[0];
	}

	@Override
	protected String[] initColumns() {
		return new String[0];
	}

	@Override
	protected String[] initCompareColumns() {
		return new String[0];
	}

	public void setTagName(String tagName) {
		tableName = tagName;
	}

	public String getTagName() {
		return tableName;
	}
	
}

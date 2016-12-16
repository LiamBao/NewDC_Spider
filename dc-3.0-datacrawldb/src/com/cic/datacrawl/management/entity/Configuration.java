//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import java.text.DateFormat;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.DateUtil;

/**
 * 
 * Configuration: 用来记录configuration的信息
 */
public class Configuration extends BaseEntity {
	private static final Configuration DEFAULT_ENTITY = new Configuration();

	/**
	 *	Create an default Configuration Entity.
	 */
	public Configuration() {
	}
	/**
	 * Create an Configuration Entity.
	 * @param name. Type: java.lang.String. 
	 * @param value. Type: java.lang.String. 
	 */
	public Configuration(
		java.lang.String name, 
				java.lang.String value
		) {
		
		setName(name);
		setValue(value);
	}


	public long getId(){
		return getLong("id");
	}
	
	public void setId(long id){
		set("id", id);
	}
	/**
	 * Get Name Value.<br>
	 * @return Name type: java.lang.String
	 */
	public java.lang.String getName() {
		return getString("name");
	}	
	/**
	 * Set 名称 Value
	 * @param 名称 type: java.lang.String
	 */
	public void setName(java.lang.String name) {
		setString("name", name, 20);
	}	/**
	 * Get Value Value.<br>
	 * @return Value type: java.lang.String
	 */
	public java.lang.String getValue() {
		return getString("value");
	}	
	/**
	 * Set 值 Value
	 * @param 值 type: java.lang.String
	 */
	public void setValue(java.lang.String value) {
		setString("value", value, 50);
	}	
	@Override
	public String getTheEntityName() {
		return "t_configuration";
	}
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id", "name", "value"};
	}
	
	@Override
	protected String[] initCompareColumns() {
		return new String[]{"id"};
	}
	
	@Override
	public BaseEntity getDefaultEmptyBean() {
		return DEFAULT_ENTITY;
	}
}

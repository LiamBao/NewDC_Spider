//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import java.text.DateFormat;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.DateUtil;

/**
 * 
 * Dict: 用来记录dict的信息
 */
public class Dict extends BaseEntity {
	private static final Dict DEFAULT_ENTITY = new Dict();

	/**
	 *	Create an default Dict Entity.
	 */
	public Dict() {
	}
	/**
	 * Create an Dict Entity.
	 * @param type. Type: java.lang.String. 
	 * @param value. Type: java.lang.String. 
	 * @param text. Type: java.lang.String. 
	 */
	public Dict(
		java.lang.String type, 
				java.lang.String value, 
				java.lang.String text
		) {
		
		setType(type);
		setValue(value);
		setText(text);
	}


	public long getId(){
		return getLong("id");
	}
	
	public void setId(long id){
		set("id", id);
	}
	/**
	 * Get Type Value.<br>
	 * @return Type type: java.lang.String
	 */
	public java.lang.String getType() {
		return getString("type");
	}	
	/**
	 * Set 分类名称 Value
	 * @param 分类名称 type: java.lang.String
	 */
	public void setType(java.lang.String type) {
		setString("type", type, 20);
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
	}	/**
	 * Get Text Value.<br>
	 * @return Text type: java.lang.String
	 */
	public java.lang.String getText() {
		return getString("text");
	}	
	/**
	 * Set 显示的文字 Value
	 * @param 显示的文字 type: java.lang.String
	 */
	public void setText(java.lang.String text) {
		setString("text", text, 50);
	}	
	@Override
	public String getTheEntityName() {
		return "t_dict";
	}
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id", "type", "value", "text"};
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

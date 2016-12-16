//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import java.text.DateFormat;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.DateUtil;

/**
 * 
 * User: 用来记录用户的信息
 */
public class User extends BaseEntity {
	private static final User DEFAULT_ENTITY = new User();

	/**
	 *	Create an default User Entity.
	 */
	public User() {
	}
	/**
	 * Create an User Entity.
	 * @param name. Type: java.lang.String. 
	 * @param email. Type: java.lang.String. 
	 */
	public User(
		java.lang.String name, 
				java.lang.String email
		) {
		
		setName(name);
		setEmail(email);
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
	 * Set 姓名 Value
	 * @param 姓名 type: java.lang.String
	 */
	public void setName(java.lang.String name) {
		setString("name", name, 20);
	}	/**
	 * Get Email Value.<br>
	 * @return Email type: java.lang.String
	 */
	public java.lang.String getEmail() {
		return getString("email");
	}	
	/**
	 * Set Email Value
	 * @param Email type: java.lang.String
	 */
	public void setEmail(java.lang.String email) {
		setString("email", email, 100);
	}	
	@Override
	public String getTheEntityName() {
		return "t_user";
	}
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id", "name", "email"};
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

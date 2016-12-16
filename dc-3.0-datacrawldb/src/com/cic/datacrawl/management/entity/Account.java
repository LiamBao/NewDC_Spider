//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import java.text.DateFormat;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.DateUtil;

/**
 * 
 * Account: 用来记录登录网站的帐号和密码
 */
public class Account extends BaseEntity {
	private static final Account DEFAULT_ENTITY = new Account();

	/**
	 *	Create an default Account Entity.
	 */
	public Account() {
	}
	/**
	 * Create an Account Entity.
	 * @param siteId. Type: long. 
	 * @param account. Type: java.lang.String. 
	 * @param password. Type: java.lang.String. 
	 * @param lastGetTime. Type: java.sql.Timestamp. 
	 * @param lastGetKey. Type: java.lang.String. 
	 * @param invalid. Type: byte. 
	 */
	public Account(
		long siteId, 
				java.lang.String account, 
				java.lang.String password, 
				java.sql.Timestamp lastGetTime, 
				java.lang.String lastGetKey, 
				byte invalid
		) {
		
		setSiteId(siteId);
		setAccount(account);
		setPassword(password);
		setLastGetTime(lastGetTime);
		setLastGetKey(lastGetKey);
		setInvalid(invalid);
	}


	public long getId(){
		return getLong("id");
	}
	
	public void setId(long id){
		set("id", id);
	}
	/**
	 * Get SiteId Value.<br>
	 * @return SiteId type: long
	 */
	public long getSiteId() {
		return getLong("siteId");
	}	
	/**
	 * Set 网站ID Value
	 * @param 网站ID type: long
	 */
	public void setSiteId(long siteId) {
		set("siteId", siteId);
	}	/**
	 * Get Account Value.<br>
	 * @return Account type: java.lang.String
	 */
	public java.lang.String getAccount() {
		return getString("account");
	}	
	/**
	 * Set 帐号 Value
	 * @param 帐号 type: java.lang.String
	 */
	public void setAccount(java.lang.String account) {
		setString("account", account, 50);
	}	/**
	 * Get Password Value.<br>
	 * @return Password type: java.lang.String
	 */
	public java.lang.String getPassword() {
		return getString("password");
	}	
	/**
	 * Set 密码 Value
	 * @param 密码 type: java.lang.String
	 */
	public void setPassword(java.lang.String password) {
		setString("password", password, 20);
	}	/**
	 * Get LastGetTime Value.<br>
	 * @return LastGetTime type: java.sql.Timestamp
	 */
	public java.sql.Timestamp getLastGetTime() {
		return getTimestamp("lastGetTime");
	}
	
	public String getLastGetTimeString(){
		return DateUtil.formatTimestamp(getLastGetTime());
	}
	
	public String getLastGetTimeString(DateFormat format){
		return format.format(getLastGetTime());
	}
	
	
	public String getLastGetTimeString(String format){
		try{
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
			return formatter.format(getLastGetTime());
		} catch (Throwable t) {
			return getLastGetTimeString();
		}
	}
	
	/**
	 * Set 使用该帐号的最后时间 Value
	 * @param 使用该帐号的最后时间 type: java.sql.Timestamp
	 */
	public void setLastGetTime(java.sql.Timestamp lastGetTime) {
		set("lastGetTime", lastGetTime);
	}
	
	public void setLastGetTime(String lastGetTime) {
		setLastGetTime(DateUtil.format(lastGetTime));
	}
	/**
	 * Get LastGetKey Value.<br>
	 * @return LastGetKey type: java.lang.String
	 */
	public java.lang.String getLastGetKey() {
		return getString("lastGetKey");
	}	
	/**
	 * Set 使用该帐号时最后产生的key Value
	 * @param 使用该帐号时最后产生的key type: java.lang.String
	 */
	public void setLastGetKey(java.lang.String lastGetKey) {
		setString("lastGetKey", lastGetKey, 10);
	}	/**
	 * Get Invalid Value.<br>
	 * @return Invalid type: byte
	 */
	public byte getInvalid() {
		byte ret = getByte("invalid");
		return ret == Byte.MIN_VALUE ? 0 : ret;
	}	
	/**
	 * Set 该帐号是否有效 Value
	 * @param 该帐号是否有效 type: byte
	 */
	public void setInvalid(byte invalid) {
		set("invalid", invalid);
	}	
	@Override
	public String getTheEntityName() {
		return "t_site_login_account";
	}
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id", "siteId", "account", "password", "lastGetTime", "lastGetKey", "invalid"};
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

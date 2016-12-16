//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import com.cic.datacrawl.core.entity.BaseEntity;

/**
 * 
 * Site: 用来记录网站的名称和英文缩写
 */
public class Site extends BaseEntity {
	private static final Site DEFAULT_ENTITY = new Site();

	/**
	 *	Create an default Site Entity.
	 */
	public Site() {
	}
	/**
	 * Create an Site Entity.
	 * @param name. Type: java.lang.String. 
	 * @param domain. Type: java.lang.String. 
	 * @param url. Type: java.lang.String. 
	 * @param infoStatus. Type: byte. 记录进程的状态：0. 只有网站信息，1. 完成
	 * @param itemType. Type: java.lang.String. 类型
	 * @param qa. Type: java.lang.String. QA人员列表
	 * @param rw. Type: java.lang.String. RW人员列表
	 * @param remark. Type: java.lang.String. 备注
	 * @param deleteFlag. Type: byte. 是否被删除
	 */
	public Site(
			java.lang.String name, 
			java.lang.String domain, 
			java.lang.String url, 
			byte infoStatus,
			java.lang.String itemType,  
			java.lang.String qa, 
			java.lang.String rw, 
			java.lang.String remark,
			byte deleteFlag
		) {
		
		setName(name);
		setDomain(domain);
		setUrl(url);
		setInfoStatus(infoStatus);
		setItemType(itemType);
		setQa(qa);
		setRw(rw);
		setRemark(remark);
		setDeleteFlag(deleteFlag);
	}


	public int getId(){
		return getInt("id");
	}
	
	public void setId(int id){
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
	 * Set 网站名 Value
	 * @param 网站名 type: java.lang.String
	 */
	public void setName(java.lang.String name) {
		setString("name", name, 50);
	}	
	/**
	 * Get Domain Value.<br>
	 * @return Domain type: java.lang.String
	 */
	public java.lang.String getDomain() {
		return getString("domain");
	}	
	/**
	 * Set 网站域名 Value
	 * @param 网站域名 type: java.lang.String
	 */
	public void setDomain(java.lang.String domain) {
		setString("domain", domain, 50);
	}	
	/**
	 * Get Url Value.<br>
	 * @return Url type: java.lang.String
	 */
	public java.lang.String getUrl() {
		return getString("url");
	}	
	/**
	 * Set 网站url Value
	 * @param 网站url type: java.lang.String
	 */
	public void setUrl(java.lang.String url) {
		setString("url", url, 500);
	}	/**
	 * Get InfoStatus Value.<br>
	 * 记录进程的状态：0. 只有网站信息，1. 完成
	 * @return InfoStatus type: byte
	 */
	public byte getInfoStatus() {
		return getByte("infoStatus");
	}	
	/**
	 * Set 站点创建信息完成情况 Value
	 * @param 站点创建信息完成情况 type: int
	 */
	public void setInfoStatus(byte infoStatus) {
		set("infoStatus", infoStatus);
	}
	
	/**
	 * Get Type Value.<br>
	 * @return Type itemType: java.lang.String
	 */
	public java.lang.String getItemType() {
		return getString("itemType");
	}	
	/**
	 * Set 网站类型 Value
	 * @param 网站类型 type: java.lang.String
	 */
	public void setItemType(java.lang.String itemType) {
		setString("itemType", itemType, 50);
	}	
	/**
	 * Get Qa Value.<br>
	 * @return Qa type: java.lang.String
	 */
	public java.lang.String getQa() {
		return getString("qa");
	}	
	/**
	 * Set 负责该网站的QA Value
	 * @param 负责该网站的QA type: java.lang.String
	 */
	public void setQa(java.lang.String qa) {
		setString("qa", qa, 50);
	}	
	/**
	 * Get Rw Value.<br>
	 * @return Rw type: java.lang.String
	 */
	public java.lang.String getRw() {
		return getString("rw");
	}	
	/**
	 * Set 负责该网站的RW Value
	 * @param 负责该网站的RW type: java.lang.String
	 */
	public void setRw(java.lang.String rw) {
		setString("rw", rw, 50);
	}
	
	public String getRemark() {
		return getString("remark");
	}
	
	public void setRemark(String remark) {
		setString("remark", remark, 255);
	}
	
	public byte getDeleteFlag() {
		return getByte("deleteFlag");
	}
	
	public void setDeleteFlag(byte deleteFlag) {
		set("deleteFlag", deleteFlag);
	}
	
	
	@Override
	public String getTheEntityName() {
		return "t_site";
	}
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id", "name", "domain", "url", "infoStatus", "itemType", "qa", "rw", "remark", "deleteFlag"};
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

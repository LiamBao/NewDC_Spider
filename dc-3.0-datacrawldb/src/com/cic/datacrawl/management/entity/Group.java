//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import java.text.DateFormat;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.DateUtil;

/**
 * 
 * Group: 用来记录登录网站和Agent的分组
 */
public class Group extends BaseEntity {
	private static final Group DEFAULT_ENTITY = new Group();

	/**
	 *	Create an default Group Entity.
	 */
	public Group() {
	}
	/**
	 * Create an Group Entity.
	 * @param name. Type: java.lang.String. 
	 * @param siteCount. Type: int. 
	 * @param agentCount. Type: int. 
	 */
	public Group(
		java.lang.String name, 
				int siteCount, 
				int agentCount
		) {
		
		setName(name);
		setSiteCount(siteCount);
		setAgentCount(agentCount);
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
	 * Set 组名 Value
	 * @param 组名 type: java.lang.String
	 */
	public void setName(java.lang.String name) {
		setString("name", name, 30);
	}	/**
	 * Get SiteCount Value.<br>
	 * @return SiteCount type: int
	 */
	public int getSiteCount() {
		return getInt("siteCount");
	}	
	/**
	 * Set 组内站点总数 Value
	 * @param 组内站点总数 type: int
	 */
	public void setSiteCount(int siteCount) {
		set("siteCount", siteCount);
	}	/**
	 * Get AgentCount Value.<br>
	 * @return AgentCount type: int
	 */
	public int getAgentCount() {
		return getInt("agentCount");
	}	
	/**
	 * Set 组内Agent总数 Value
	 * @param 组内Agent总数 type: int
	 */
	public void setAgentCount(int agentCount) {
		set("agentCount", agentCount);
	}	
	@Override
	public String getTheEntityName() {
		return "t_group";
	}
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id", "name", "siteCount", "agentCount"};
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

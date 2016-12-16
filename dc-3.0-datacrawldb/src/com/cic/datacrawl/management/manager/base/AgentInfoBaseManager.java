//DON'T MODIFY ME
package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.management.entity.AgentInfo;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.AgentInfoDAO;

public abstract class AgentInfoBaseManager{

	protected AgentInfoDAO dao;
	protected AgentInfoDAO getAgentInfoDAO() {		
		if(dao == null){
			dao = (AgentInfoDAO) ApplicationContext.getInstance().getBean("agentInfoDAO");
		}
		return dao;
	}
	
	public void setAgentInfoDAO(AgentInfoDAO dao) {
		this.dao = dao;
	}
	
	public AgentInfo[] getAllAgentInfo() {
		return getAgentInfoDAO().getAllAgentInfo();
	}
	
	public AgentInfo[] getAllAgentInfo(int startIndex, int limit) {
		return getAgentInfoDAO().getAllAgentInfo(startIndex, limit);
	}
	
	public AgentInfo getAgentInfo(long id){
		return getAgentInfoDAO().getAgentInfo(id);
	}

	public AgentInfo[] getAgentInfos(long[] ids){
		return getAgentInfoDAO().getAgentInfos(ids);
	}
	
	public long addAgentInfo(AgentInfo agentInfo){
		return getAgentInfoDAO().addAgentInfo(agentInfo);
	}	
	
	public long[] addAgentInfos(AgentInfo[] agentInfos){
		return getAgentInfoDAO().addAgentInfos(agentInfos);
	}
	
	public int deleteAgentInfo(long id){
		return getAgentInfoDAO().deleteAgentInfo(id);
	}
	
	public int deleteAgentInfos(long[] ids){
		return getAgentInfoDAO().deleteAgentInfos(ids);
	}
	
	public long saveAgentInfo(AgentInfo agentInfo){
		long ret = getAgentInfoDAO().saveAgentInfo(agentInfo);
		if (ret == 0) {
			ret = getAgentInfoDAO().addAgentInfo(agentInfo);
		} else {
			ret = agentInfo.getId();
		}
		return ret;
	}
	
	public int updateAgentInfo(AgentInfo agentInfo){
		return getAgentInfoDAO().saveAgentInfo(agentInfo);		
	}
	
	public int[] updateAgentInfos(AgentInfo[] agentInfos){
		return getAgentInfoDAO().saveAgentInfos(agentInfos);		
	}
	
	public int count(){
		return getAgentInfoDAO().count();
	}
	
}

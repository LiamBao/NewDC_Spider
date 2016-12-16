//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.AgentInfo;

public interface AgentInfoBaseDAO {

	public AgentInfo[] getAllAgentInfo();

	public AgentInfo[] getAllAgentInfo(int startIndex, int limit);

	public AgentInfo getAgentInfo(long id);

	public AgentInfo[] getAgentInfos(long[] id);
	
	public long addAgentInfo(AgentInfo agentInfo);
	
	public long[] addAgentInfos(AgentInfo[] agentInfos);
	
	public int deleteAgentInfo(long id);
	
	public int deleteAgentInfos(long[] id);
	
	public int saveAgentInfo(AgentInfo agentInfo);
	
	public int[] saveAgentInfos(final AgentInfo[] agentInfos);
	
	public int count();
	
}

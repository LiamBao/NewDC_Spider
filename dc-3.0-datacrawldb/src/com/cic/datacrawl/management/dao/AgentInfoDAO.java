package com.cic.datacrawl.management.dao;

import com.cic.datacrawl.management.dao.base.*;
import com.cic.datacrawl.management.entity.AgentInfo;

public interface AgentInfoDAO extends AgentInfoBaseDAO {
	AgentInfo[] queryByIP(String ip, int startIndex, int limit);

	AgentInfo[] queryByIPAndEnable(String ip, byte enable, int startIndex, int limit);

	AgentInfo[] queryByEnable(byte enable, int startIndex, int limit);

	void changeEnableFlag(long[] ids, byte enable);

	void changeGroupIds(long[] ids, long groupId);

	AgentInfo[] queryByGroupIds(long[] groupId);
}

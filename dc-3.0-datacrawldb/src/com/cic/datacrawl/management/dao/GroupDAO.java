package com.cic.datacrawl.management.dao;

import com.cic.datacrawl.management.dao.base.*;
import com.cic.datacrawl.management.entity.Group;

public interface GroupDAO extends GroupBaseDAO {

	void cleanSiteCount(long[] groupId);

	void changeAgentCount(long groupId, int count);

	void changeSiteCount(long groupId, int count);

	Group[] queryByName(String name, int start, int limit);

	void cleanAgentCount(long[] groupId);

	int calcSumSiteCount(long[] groupId);

	int calcSumAgentCount(long[] groupId);

	Group[] getAllGroupsAndCalcSitesAndAgents(String groupName);
}

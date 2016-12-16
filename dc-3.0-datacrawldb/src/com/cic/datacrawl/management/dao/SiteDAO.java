package com.cic.datacrawl.management.dao;

import com.cic.datacrawl.management.dao.base.*;
import com.cic.datacrawl.management.entity.Site;

public interface SiteDAO extends SiteBaseDAO{

	int changeInfoStatus(long siteId,int infoStatus);
	int changeInfoStatus(long[] siteIds,int infoStatus);

	Site[] getAllNotDeletedSite(int startIndex, int pageSize);
	void changeEnableFlag(long[] ids, byte enable);
	void changeGroupIds(long[] ids, long groupId);
	Site[] queryByGroupIds(long[] groupId);
}


package com.cic.datacrawl.management.manager;

import com.cic.datacrawl.management.entity.Site;
import com.cic.datacrawl.management.manager.base.SiteBaseManager;

public class SiteManager extends SiteBaseManager {
	public static final byte INFO_STATUS_NOT_READY = 0;
	public static final byte INFO_STATUS_READY = 1;
	public static final byte INFO_STATUS_DISABLE_READY = 2;
	public static final byte INFO_STATUS_DISABLE_NOT_READY = 3;
	public static final byte INFO_STATUS_DELETED = 10;

	public void finishedAddSite(long siteId) {
		getSiteDAO().changeInfoStatus(siteId, INFO_STATUS_READY);
	}

	public void changeGroupIds(long[] ids, long groupId) {
		getSiteDAO().changeGroupIds(ids, groupId);
	}

	public Site[] queryByGroupIds(long[] groupId) {
		return getSiteDAO().queryByGroupIds(groupId);
	}

}

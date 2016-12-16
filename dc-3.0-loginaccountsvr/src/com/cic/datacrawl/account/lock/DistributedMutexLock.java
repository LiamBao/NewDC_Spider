package com.cic.datacrawl.account.lock;

import java.util.Calendar;
import java.util.Date;

import com.cicdata.iwmdata.base.client.cache.MemcacheManagerForGwhalin;

public class DistributedMutexLock {
	
	/**
	 * 给站点ID列表加锁
	 * @param lockTime 锁定时间
	 */
	public void lockSiteIdList(int lockTime) {
		String cacheKey = "ACCOUNT_SITE_ID_LIST_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, lockTime);
		Date expireTime = calendar.getTime();
		
		while(!cacheClient.add(cacheKey, 0, expireTime)) {
			try {
				Thread.sleep(100);
				calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, lockTime);
				expireTime = calendar.getTime();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * 给站点ID列表解锁
	 */
	public void unlockSiteIdList() {
		String cacheKey = "ACCOUNT_SITE_ID_LIST_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		cacheClient.remove(cacheKey);
	}
	
	
	/**
	 * 给站点账号列表加锁
	 * @param siteId 站点ID
	 * @param lockTime 锁定时间
	 */
	public void lockSiteCookieList(int siteId, int lockTime) {
		String cacheKey = "ACCOUNT_SITE_" + siteId +"_COOKIE_LIST_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, lockTime);
		Date expireTime = calendar.getTime();
		
		while(!cacheClient.add(cacheKey, 0, expireTime)) {
			try {
				Thread.sleep(100);
				calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, lockTime);
				expireTime = calendar.getTime();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * 给站点账号列表解锁
	 * @param siteId 站点ID
	 */
	public void unlockSiteCookieList(int siteId) {
		String cacheKey = "ACCOUNT_SITE_" + siteId +"_COOKIE_LIST_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		cacheClient.remove(cacheKey);
	}
	
	/**
	 * 给站点线路账号列表加锁
	 * @param siteId 站点ID
	 * @param agentSubgroupId 子网络ID
	 * @param lockTime
	 */
	public void lockSiteSubGroupCookieList(int siteId,int agentSubgroupId,int lockTime) {
		String cacheKey = "ACCOUNT_SITE_" + siteId + "_SUBGROUPID_" + agentSubgroupId + "_COOKIE_LIST_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, lockTime);
		Date expireTime = calendar.getTime();
		
		while(!cacheClient.add(cacheKey, 0, expireTime)) {
			try {
				Thread.sleep(100);
				calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, lockTime);
				expireTime = calendar.getTime();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * 给站点账号列表解锁
	 * @param siteId 站点ID
	 * @param agentSubgroupId 子网络ID
	 */
	public void unlockSiteSubGroupCookieList(int siteId,int agentSubgroupId) {
		String cacheKey = "ACCOUNT_SITE_" + siteId + "_SUBGROUPID_" + agentSubgroupId + "_COOKIE_LIST_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		cacheClient.remove(cacheKey);
	}
	
	/**
	 * 给待清理站点账号列表加锁
	 * @param siteId 站点ID
	 * @param lockTime
	 */
	public void lockWaitClearSiteCookieList(int siteId,int lockTime) {
		String cacheKey = "ACCOUNT_WAIT_CLEAR_SITE_" + siteId + "_COOKIE_LIST_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, lockTime);
		Date expireTime = calendar.getTime();
		
		while(!cacheClient.add(cacheKey, 0, expireTime)) {
			try {
				Thread.sleep(100);
				calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, lockTime);
				expireTime = calendar.getTime();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * 给待清理站点账号列表解锁
	 * @param siteId 站点ID
	 */
	public void unlockWaitClearSiteCookieList(int siteId) {
		String cacheKey = "ACCOUNT_WAIT_CLEAR_SITE_" + siteId + "_COOKIE_LIST_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		cacheClient.remove(cacheKey);
	}
	
	
	/**
	 * 给站点最大账号数加锁
	 * @param siteId 站点ID
	 * @param lockTime
	 */
	public void lockSiteMaxAccountNum(int siteId,int lockTime) {
		String cacheKey = "ACCOUNT_SITE_" + siteId + "_MAX_ACCOUNT_NUM_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, lockTime);
		Date expireTime = calendar.getTime();
		
		while(!cacheClient.add(cacheKey, 0, expireTime)) {
			try {
				Thread.sleep(100);
				calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, lockTime);
				expireTime = calendar.getTime();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * 给站点最大账号数解锁
	 * @param siteId 站点ID
	 */
	public void unlockSiteMaxAccountNum(int siteId) {
		String cacheKey = "ACCOUNT_SITE_" + siteId + "_MAX_ACCOUNT_NUM_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		cacheClient.remove(cacheKey);
	}
}

package com.cic.datacrawl.account.anew;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.cic.datacrawl.account.bean.SiteAccountInfo;
import com.cic.datacrawl.account.lock.DistributedMutexLock;
import com.cic.datacrawl.account.rpc.protocol.ResTaskRunnerReqAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerChangeAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerReqAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerReturnAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerUpdateAccountCookieWritable;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.initialize.InitializerRegister;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;
import com.cicdata.iwmdata.base.client.cache.MemcacheManagerForGwhalin;

public class AccountServerDaemon {

	private static final Logger log = Logger.getLogger(AccountServerDaemon.class);
	private static AccountServerDaemon cs;
	private static String serverAddress;
	private static int numHandlers;
	public static int agentTimeOut;
	public static int cacheEnableTime;
	public static int lockHoldTime = 10;
	// 站点账号时间限制
	public static long siteAccountTimeOut ;
	// 待清理站点账号时间限制
	public static long waitClearAccountTimeOut;

	// 站点线路账号列表失效时间
	public static int siteSubgroupTimeOut;

	
	public static void setSiteSubgroupTimeOut(int siteSubgroupTimeOut) {
		AccountServerDaemon.siteSubgroupTimeOut = siteSubgroupTimeOut;
	}


	public static void setSiteAccountTimeOut(long siteAccountTimeOut) {
		AccountServerDaemon.siteAccountTimeOut = siteAccountTimeOut;
	}

	
	public static void setWaitClearAccountTimeOut(long waitClearAccountTimeOut) {
		AccountServerDaemon.waitClearAccountTimeOut = waitClearAccountTimeOut;
	}

	public static int getNumHandlers() {
		return numHandlers;
	}

	public static void setNumHandlers(int numHandlers) {
		AccountServerDaemon.numHandlers = numHandlers;
	}

	public static int getAgentTimeOut() {
		return agentTimeOut;
	}

	public static void setAgentTimeOut(int agentTimeOut) {
		AccountServerDaemon.agentTimeOut = agentTimeOut;
	}

	public static int getCacheEnableTime() {
		return cacheEnableTime;
	}

	public static void setCacheEnableTime(int cacheEnableTime) {
		AccountServerDaemon.cacheEnableTime = cacheEnableTime;
	}

	public static int getLockHoldTime() {
		return lockHoldTime;
	}

	public static void setLockHoldTime(int lockHoldTime) {
		AccountServerDaemon.lockHoldTime = lockHoldTime;
	}

	public synchronized static AccountServerDaemon getInstance() {
		if (cs == null) {
			System.out.println("a new instance");
			cs = new AccountServerDaemon();
		}
		return cs;
	}

	private AccountServerDaemon() {
	}
	
	
	/**
	 * set status for site account
	 * @param accountId
	 * @param status
	 */
	public void setSiteAccountStatus(int accountId, int status) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			ps = conn.prepareStatement("UPDATE T_SITE_ACCOUNT SET STATUS = ? WHERE ID = ?");
			ps.setInt(1, status);
			ps.setInt(2, accountId);
			ps.executeUpdate();
		} catch (Exception e) {
			log.error("Set status=" + status + " for accountId= " + accountId + " failed !",e);
			e.printStackTrace();
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 从数据库获取所有的siteId
	 * @return
	 */
	public  List<Integer> getSiteIdFromDB(){
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<Integer> list = new ArrayList<Integer>();
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT DISTINCT SITE_ID FROM T_SITE_ACCOUNT WHERE STATUS = 1 ");
			while (rs.next()) {
				list.add(rs.getInt("SITE_ID"));
			}
		} catch (Exception e) {
			log.error("cannot get site_id",e);
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
		
	}
	
	/**
	 *从数据库加载账号信息 
	 * @return
	 */
	public List<SiteAccountInfo> getSiteAccountInfoFromDB(){
		List<SiteAccountInfo> list = new ArrayList<SiteAccountInfo>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			st = conn.createStatement();
			
			rs = st.executeQuery("SELECT A.ID AS AID,A.USER_NAME,A.PASS_WORD,A.COOKIE,A.AUTO_LOGIN FROM T_SITE_ACCOUNT A WHERE A.STATUS = 1 ORDER BY A.SITE_ID ");
			while (rs.next()) {
				SiteAccountInfo info = new SiteAccountInfo();
				info.setAccountId(rs.getInt("AID"));
				int isAuto = rs.getInt("AUTO_LOGIN");
				info.setIsAutoLogin(isAuto);
				// 是自动登录
				if(isAuto == 1){
					info.setUserName(rs.getString("USER_NAME"));
					info.setPasswd(rs.getString("PASS_WORD"));
					info.setCookie("");
				}else{
					info.setUserName("");
					info.setPasswd("");
					info.setCookie(rs.getString("COOKIE"));
				}
				list.add(info);
				//log.info("add a new account to the list ");
			}
		} catch (Exception e) {
			log.error("get account information  failed !",e);
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	
	/**
	 * 初始化后，将DB中的站点账号信息加载到cache中
	 */
	public  void addSiteAccountInfoToCache(){
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			st = conn.createStatement();
			List<Integer> sites = new ArrayList<Integer>();
			List<Integer> cacheSites = getSiteIdListFromCache(false);
			rs = st.executeQuery("SELECT DISTINCT SITE_ID FROM T_SITE_ACCOUNT WHERE STATUS = 1");
			while (rs.next()) {
				sites.add(rs.getInt("SITE_ID"));
			}
			addOrUpdateSiteIdList(sites);
			
			updateSite(cacheSites, sites);
			
			for(int siteId:sites){
				// 删除cache中站点的账号信息
				delSiteCookieList(siteId);
				// 保存从数据库中拿到的账号信息
				List<SiteAccountInfo> list = new ArrayList<SiteAccountInfo>();
				rs = st.executeQuery("SELECT A.ID AS AID,A.USER_NAME,A.PASS_WORD,A.COOKIE,B.MAX_ACCOUNT_NUM,A.AUTO_LOGIN FROM T_SITE_ACCOUNT A,T_SITE B WHERE A.STATUS = 1 AND A.SITE_ID = B.ID AND A.SITE_ID = " + siteId + " ORDER BY A.ID");
				while (rs.next()) {
					SiteAccountInfo info = new SiteAccountInfo();
					info.setAccountId(rs.getInt("AID"));
					int isAuto = rs.getInt("AUTO_LOGIN");
					info.setIsAutoLogin(isAuto);
					if(isAuto == 1){
						info.setUserName(rs.getString("USER_NAME"));
						info.setPasswd(rs.getString("PASS_WORD"));
						info.setCookie("");
					}else{
						info.setUserName("");
						info.setPasswd("");
						info.setCookie(rs.getString("COOKIE"));
					}
					list.add(info);
					//log.info("add a new account to the list");
					// 保存站点账号最大共享数到cache中
					addOrUpdateSiteMaxAccountNum(siteId, rs.getInt("MAX_ACCOUNT_NUM"));
				}
				
				// 将最新信息添加到cache中
				addOrUpdateSiteCookieList(siteId, list);
				
				/*
				// 数据库中此站点账号信息被删除或者没有
				if(list.size() == 0){
					log.info("there is no data in database! the siteId:" + siteId);
					delSiteCookieList(siteId);
					continue;
				}
				
				//cache中已经有的账号信息
				List<SiteAccountInfo> accountInfos = getSiteCookieListFromCache(siteId,false);
				if(accountInfos == null || accountInfos.isEmpty()){
					log.info("can not find account informations from cache");
					addOrUpdateSiteCookieList(siteId, list);
				}else{
					// 标记属性是否改变
					boolean isChange = false;
					for(SiteAccountInfo info : list){
						isChange = false;
						SiteAccountInfo accountInfo = findSiteAccountInfoFromListByAccountId(accountInfos, info.getAccountId());
						// 如果原始cache中没有此账号信息，则添加到cache中
						if(accountInfo == null){
							//log.info("add a new account to the  cache");
							isChange = true;
							accountInfos.add(info);
						}else{
							if(info.getIsAutoLogin() != accountInfo.getIsAutoLogin()){
								accountInfo.setIsAutoLogin(info.getIsAutoLogin());
								isChange = true;
							}
							
							if(!info.getUserName().equals(accountInfo.getUserName())){
								accountInfo.setUserName(info.getUserName());
								isChange = true;
							}
							
							if(!info.getPasswd().equals(accountInfo.getPasswd())){
								accountInfo.setPasswd(info.getPasswd());
								isChange = true;
							}
							
							if(!info.getCookie().equals(accountInfo.getCookie())){
								accountInfo.setCookie(info.getCookie());
								isChange = true;
							}
						}
						if(isChange)
						{
							addOrUpdateSiteCookieList(siteId, accountInfos);
						}
					}
					
					updateSiteCookieList(siteId, accountInfos, list);
					
				}*/
			}
			
		} catch (Exception e) {
			log.error(" Add accounts  to the cache  failed!",e);
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 对比cache中的账号信息和数据库中的账号信息，更新cache
	 * @param siteId 站点ID
	 * @param accountInfos cache中的信息
	 * @param list 数据库中的信息
	 */
	public void updateSiteCookieList(int siteId,List<SiteAccountInfo> accountInfos,List<SiteAccountInfo> list){
		// 数据库中的数据被删除的话，cache中数据也需要被删除
		List<SiteAccountInfo> ids = new ArrayList<SiteAccountInfo>();
		int count = 0;
		for(SiteAccountInfo cacheInfo : accountInfos){
			count = 0;
			for(SiteAccountInfo dbInfo : list){
				// 数据库中有此账号
				if(cacheInfo.getAccountId() == dbInfo.getAccountId()){
					break;
				}else{
					count++;
				}
			}
			// 数据库中查询到的有效账号没有此账号，此账号失效
			if(count == list.size()){
				ids.add(cacheInfo);
			}
		}
		if(ids.size() > 0){
			for(SiteAccountInfo info : ids){
				accountInfos.remove(info);
			}
			addOrUpdateSiteCookieList(siteId, accountInfos);
		}
	}
	/**
	 * 对比cache中的站点ID和数据库中的站点ID，将数据中没有的site id对应的站点账号列表删除
	 * 
	 * @param cacheInfo
	 * @param dbInfo
	 */
	public void updateSite(List<Integer> cacheInfo,List<Integer> dbInfo){
		log.info("cacheInfo:" + cacheInfo);
		log.info("dbInfo:" + dbInfo);
		if(cacheInfo == null || dbInfo == null){
			return;
		}
		for(int dbid : cacheInfo){
			if(!dbInfo.contains(dbid)){
				delSiteCookieList(dbid);
			}
		}
	}
	
	/**
	 * 获取站点id列表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getSiteIdListFromCache(boolean loadDB){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		List<Integer> siteIdList = null;
		String siteIdListKey = "ACCOUNT_SITE_ID_LIST";
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		log.info("Add mutexLock for siteIdList");
		mutexLock.lockSiteIdList(lockHoldTime);
		Object siteIdListObj = cacheClient.get(siteIdListKey);
		log.info("Delete mutexLock for siteIdList");
		mutexLock.unlockSiteIdList();
		if(siteIdListObj != null){
			siteIdList = (List<Integer>)siteIdListObj;
		}else{
			if(loadDB){
				siteIdList = getSiteIdFromDB();
				// 取得的数据添加至cache
				addOrUpdateSiteIdList(siteIdList);
				/*log.info("Add mutexLock for siteIdList");
				mutexLock.lockSiteIdList(lockHoldTime);
				siteIdListObj = cacheClient.get(siteIdListKey);
				log.info("Delete mutexLock for siteIdList");
				mutexLock.unlockSiteIdList();
				if(siteIdListObj != null){
					siteIdList = (List<Integer>)siteIdListObj;
				}*/
			}
		}
		return siteIdList;
	}
	
	
	/**
	 * 获取待清理站点账号列表
	 * 
	 * @param siteId 站点ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SiteAccountInfo> getWaitClearSiteCookieListFromCache(int siteId){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		List<SiteAccountInfo> waitClearSiteCookieList = null;
		String waitClearSiteCookieListKey = "ACCOUNT_WAIT_CLEAR_SITE_" + siteId + "_" + "COOKIE_LIST";
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		log.info("Add mutexLock for WaitClearSiteCookieList which site_id is " + siteId);
		mutexLock.lockWaitClearSiteCookieList(siteId, lockHoldTime);
		Object siteCookieListObj = cacheClient.get(waitClearSiteCookieListKey);
		log.info("Delete mutexLock for WaitClearSiteCookieList which site_id is  " + siteId);
		mutexLock.unlockWaitClearSiteCookieList(siteId);
		if(siteCookieListObj != null){
			waitClearSiteCookieList = (List<SiteAccountInfo>)siteCookieListObj;
		}
		return waitClearSiteCookieList;
	}
	
	/**
	 * 获取站点账号列表
	 * 
	 * @param siteId 站点ID
	 * @param loadDB 若cache中没有找到信息，是否加载数据库
	 * 			true：加载
	 * 			false：不加载
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SiteAccountInfo> getSiteCookieListFromCache(int siteId,boolean loadDB){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		List<SiteAccountInfo> siteCookieList = null;
		String siteCookieListKey = "ACCOUNT_SITE_" + siteId + "_" + "COOKIE_LIST";
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		log.info("Add mutexLock for SiteCookieList which site_id is  " + siteId);
		mutexLock.lockSiteCookieList(siteId, lockHoldTime);
		Object siteCookieListObj = cacheClient.get(siteCookieListKey);
		log.info("Delete mutexLock for SiteCookieList which site_id is " + siteId);
		mutexLock.unlockSiteCookieList(siteId);
		if(siteCookieListObj != null){
			siteCookieList = (List<SiteAccountInfo>)siteCookieListObj;
		}else{
			if(loadDB){
				//log.info("load data from database...");
				addSiteAccountInfoToCache();
				log.info("Add mutexLock for SiteCookieList which site_id is  " + siteId);
				mutexLock.lockSiteCookieList(siteId, lockHoldTime);
				siteCookieListObj = cacheClient.get(siteCookieListKey);
				log.info("Delete mutexLock for SiteCookieList which site_id is " + siteId);
				mutexLock.unlockSiteCookieList(siteId);
				if(siteCookieListObj != null){
					siteCookieList = (List<SiteAccountInfo>)siteCookieListObj;
				}
			}
		}
		
		return siteCookieList;
	}
	
	/**
	 * 获取站点线路账号列表
	 * 
	 * @param siteId 站点ID
	 * @param agentSubgroupId 线路ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SiteAccountInfo> getSiteSubGroupIdCookieListFromCache(int siteId,int agentSubgroupId){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		List<SiteAccountInfo> siteSubGroupIdCookieList = null;
		String siteSubGroupIdCookieListKey = "ACCOUNT_SITE_" + siteId + "_SUBGROUPID_" + agentSubgroupId + "_COOKIE_LIST";
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		log.info("Add mutexLock for siteSubGroupIdCookieList which site_id is  " + siteId + " and agentSubgroupId is " + agentSubgroupId);
		mutexLock.lockSiteSubGroupCookieList(siteId, agentSubgroupId, lockHoldTime);
		Object siteSubGroupCookieListObj = cacheClient.get(siteSubGroupIdCookieListKey); 
		log.info("Delete mutexLock for siteSubGroupIdCookieList which site_id is  " + siteId + " and agentSubgroupId is " + agentSubgroupId);
		mutexLock.unlockSiteSubGroupCookieList(siteId, agentSubgroupId);
		if(siteSubGroupCookieListObj != null){
			siteSubGroupIdCookieList = (List<SiteAccountInfo>)siteSubGroupCookieListObj;
		}
		return siteSubGroupIdCookieList;
	}
	
	/**
	 * 更新站点ID列表
	 * @param siteIdList 账号ID列表
	 */
	public void addOrUpdateSiteIdList(List<Integer> siteIdList){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		String siteIdListKey = "ACCOUNT_SITE_ID_LIST";
		log.info("Add mutexLock for siteIdList");
		mutexLock.lockSiteIdList(lockHoldTime);
		cacheClient.addOrUpdate(siteIdListKey,siteIdList);
		log.info("Delete mutexLock for siteIdList");
		mutexLock.unlockSiteIdList();
	}
	
	/**
	 * 更新站点账号列表
	 * @param siteId 站点ID
	 * @param siteCookieList 账号列表
	 */
	public void addOrUpdateSiteCookieList(int siteId,List<SiteAccountInfo> siteCookieList){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		String siteCookieListKey = "ACCOUNT_SITE_" + siteId + "_" + "COOKIE_LIST";
		log.info("Add mutexLock for SiteCookieList which site_id is " + siteId);
		mutexLock.lockSiteCookieList(siteId, lockHoldTime);
		cacheClient.addOrUpdate(siteCookieListKey, siteCookieList);
		log.info("Delete mutexLock for SiteCookieList which site_id is " + siteId);
		mutexLock.unlockSiteCookieList(siteId);
	}
	
	/**
	 * 删除站点账号列表 
	 */
	public void delSiteCookieList(int siteId){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		String siteCookieListKey = "ACCOUNT_SITE_" + siteId + "_" + "COOKIE_LIST";
		String siteMaxAccountNumKey = "ACCOUNT_SITE_" + siteId + "_MAX_ACCOUNT_NUM";
		log.info("Add mutexLock for SiteCookieList which site_id is " + siteId);
		mutexLock.lockSiteCookieList(siteId, lockHoldTime);
		cacheClient.remove(siteCookieListKey);
		cacheClient.remove(siteMaxAccountNumKey);
		log.info("Delete mutexLock for SiteCookieList which site_id is " + siteId);
		mutexLock.unlockSiteCookieList(siteId);
	}
	
	/**
	 * 更新站点账号列表
	 * @param siteId 站点ID
	 * @param siteCookieList 账号列表
	 */
	public void addOrUpdateWaitClearSiteCookieList(int siteId,List<SiteAccountInfo> waitClearSiteCookieList){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		String waitClearSiteCookieListKey = "ACCOUNT_WAIT_CLEAR_SITE_" + siteId + "_" + "COOKIE_LIST";
		log.info("Add mutexLock for WaitClearSiteCookieList which site_id is " + siteId);
		mutexLock.lockWaitClearSiteCookieList(siteId, lockHoldTime);
		cacheClient.addOrUpdate(waitClearSiteCookieListKey, waitClearSiteCookieList);
		log.info("Delete mutexLock for WaitClearSiteCookieList which site_id is " + siteId);
		mutexLock.unlockWaitClearSiteCookieList(siteId);
	}
	
	/**
	 * 更新站点线路账号列表
	 * @param siteId 站点ID
	 * @param agentSubgroupId 线路ID
	 * @param siteSubGroupIdCookieList 账号列表
	 */
	public void addOrUpdateSiteSubGroupCookieList(int siteId,int agentSubgroupId,List<SiteAccountInfo> siteSubGroupIdCookieList){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		String siteSubGroupIdCookieListKey = "ACCOUNT_SITE_" + siteId + "_SUBGROUPID_" + agentSubgroupId + "_COOKIE_LIST";
		log.info("Add mutexLock for SiteSubGroupCookieList which site_id is  " + siteId + " and agentSubgroupId is " + agentSubgroupId);
		mutexLock.lockSiteSubGroupCookieList(siteId, agentSubgroupId, lockHoldTime);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MILLISECOND, siteSubgroupTimeOut);
		Date expireTime = calendar.getTime();
		cacheClient.addOrUpdate(siteSubGroupIdCookieListKey, siteSubGroupIdCookieList, expireTime);
		log.info("Del mutexLock for SiteSubGroupCookieList which site_id is  " + siteId + " and agentSubgroupId is " + agentSubgroupId);
		mutexLock.unlockSiteSubGroupCookieList(siteId, agentSubgroupId);
		
	}
	/**
	 * 更新站点最大账号个数
	 * @param siteId 站点ID
	 * @param num 账号个数
	 */
	public void addOrUpdateSiteMaxAccountNum(int siteId,int num){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		String siteMaxAccountNumKey = "ACCOUNT_SITE_" + siteId + "_MAX_ACCOUNT_NUM";
		log.info("Add mutexLock for SiteMaxAccountNum which site_id is " + siteId);
		mutexLock.lockSiteMaxAccountNum(siteId, lockHoldTime);
		cacheClient.addOrUpdate(siteMaxAccountNumKey, num);
		log.info("Delete mutexLock for SiteMaxAccountNum which site_id is " + siteId);
		mutexLock.unlockSiteMaxAccountNum(siteId);
	}
	
	/**
	 * 获取站点最大账号个数
	 * @param siteId 站点ID
	 */
	public int getSiteMaxAccountNum(int siteId){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		String siteMaxAccountNumKey = "ACCOUNT_SITE_" + siteId + "_MAX_ACCOUNT_NUM";
		log.info("Add mutexLock for SiteMaxAccountNum which site_id is " + siteId);
		mutexLock.lockSiteMaxAccountNum(siteId, lockHoldTime);
		Object siteMaxAccountNum = cacheClient.get(siteMaxAccountNumKey);
		log.info("Delete mutexLock for SiteMaxAccountNum which site_id is  " + siteId);
		mutexLock.unlockSiteMaxAccountNum(siteId);
		if(siteMaxAccountNum != null){
			return (Integer)siteMaxAccountNum;
		}
		return 0;
	}
	
	
	/**
	 * 通过请求账号协议，获取分配的账号并返回给客户端
	 * s
	 * @param writable 请求账号协议
	 * @return
	 * @throws Exception 
	 */
	public ResTaskRunnerReqAccountWritable getAccountByWritable(TaskRunnerReqAccountWritable writable) throws Exception{
		ResTaskRunnerReqAccountWritable reqAccountWritable = new ResTaskRunnerReqAccountWritable();
		// 挑选的账号
		SiteAccountInfo selectAccount = null;
		try {
			
			//从cache中获取站点所有账号列表
			List<SiteAccountInfo> siteCookieList = getSiteCookieListFromCache(writable.siteId,true);
			//从cache中获取站点对应线路的所有账号列表
			List<SiteAccountInfo> siteSubGroupIdCookieList = getSiteSubGroupIdCookieListFromCache(writable.siteId, writable.agentSubgroupId);
			
			if(siteCookieList == null || siteCookieList.isEmpty()){
				// 数据库中没有账号信息
				log.info("there is not data in the database");
				return reqAccountWritable;
			}
			
			// 账号共享
			if(writable.isShare == 1){
				//log.info("the request protocol attribute isShare is  " + writable.isShare );
				if(siteSubGroupIdCookieList == null){
					//说明第一次分配账号或者cache中站点线路账号列表数据丢失
					//log.info("the siteSubGroupIdCookieList is null  ");
					siteSubGroupIdCookieList = new ArrayList<SiteAccountInfo>();
				}
				//线路有效Cookie账号数
				int validAccountNum = siteSubGroupIdCookieList.size();
				//log.info("the effective account number is " + validAccountNum);
				int siteMaxAccountNum = getSiteMaxAccountNum(writable.siteId);
				// 线路有效Cookie账号数少于规定最大账号个数
				if(siteMaxAccountNum > validAccountNum){
					//log.info("the effective account number is small than the max site account number ");
					
					//从站点账号列表中挑选一个账号
					selectAccount = selectAccountFromSiteCookieList(siteCookieList,true);
					
					// 挑选成功
					if(selectAccount != null){
						// 非自动登录
						// 添加到siteSubGroupIdCookieList
						if(selectAccount.getIsAutoLogin() == 0)
						{
							selectAccount.setIsUsed(1);
							// 选择到的账号为非自动登录，只需要账号，密码，不需要回传cookie，直接添加到站点线路账号列表中
							addSiteAccountInfoToSiteSubGroupIdList(writable.siteId, writable.agentSubgroupId, selectAccount.getAccountId(), selectAccount.getCookie());
						}
						addOrUpdateSiteCookieList(writable.siteId, siteCookieList);
					}else{
						// 挑选失败 	账号均被使用
						// 从siteSubGroupCookieList中挑选一个账号
						selectAccount = getSiteAccountInfoFromSiteSubGroupIdCookieList(writable.siteId, writable.agentSubgroupId);
					}
				}else{
					// 线路有效Cookie账号数大于等于规定最大账号个数 	或者没有取到最大账号个数
					// 从siteSubGroupCookieList中挑选一个账号
					selectAccount = getSiteAccountInfoFromSiteSubGroupIdCookieList(writable.siteId, writable.agentSubgroupId);
				}
				
			}
			
			if(writable.isShare == 0){
				//账号不共享
				//从cache中获取站点所有账号列表，选择一个账号（username,passwd），设置为被使用状态，
				//更新cache，将账号发送给下载器
				selectAccount = selectAccountFromSiteCookieList(siteCookieList,false);
				if(selectAccount != null){
					addOrUpdateSiteCookieList(writable.siteId, siteCookieList);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Allocate an account failed!",e);
			throw e;
		}
		if(selectAccount == null){
			log.info("Allocate an account failed! " + writable.toString());
		}else{
			reqAccountWritable = setAccountValue(reqAccountWritable, selectAccount);
			log.info("Allocate an account success!");
		}
		
		return reqAccountWritable;
	}
	
	/**
	 * 从siteSubGroupCookieList中挑选一个账号
	 * @param siteId 站点ID
	 * @param agentSubgroupId 线路ID
	 * @return
	 */
	public SiteAccountInfo getSiteAccountInfoFromSiteSubGroupIdCookieList(int siteId,int agentSubgroupId){
		SiteAccountInfo selectAccount = null;
		//从cache中获取站点所有账号列表
		List<SiteAccountInfo> siteCookieList = getSiteCookieListFromCache(siteId,true);
		//从cache中获取站点对应线路的所有账号列表
		List<SiteAccountInfo> siteSubGroupIdCookieList = getSiteSubGroupIdCookieListFromCache(siteId, agentSubgroupId);
		//selectAccount = selectAccountFromSiteSubGroupCookieList(siteSubGroupIdCookieList,siteCookieList);
		// 选择一个账号
		if(siteSubGroupIdCookieList != null && !siteSubGroupIdCookieList.isEmpty()){
		
			// 记录站点线路账号列表的无效账号
			List<SiteAccountInfo> list = new ArrayList<SiteAccountInfo>();
		
			Collections.sort(siteSubGroupIdCookieList, getComparator(0, "shareNum"));
			for(SiteAccountInfo info : siteSubGroupIdCookieList){
				// 账号存在站点账号列表中
				SiteAccountInfo account = findSiteAccountInfoFromListByAccountId(siteCookieList, info.getAccountId());
				if(account != null){
					info.setCookie(account.getCookie());
					info.setPasswd(account.getPasswd());
					info.setUserName(account.getUserName());
					info.setIsAutoLogin(account.getIsAutoLogin());
					selectAccount = info;
					
					removeSiteAccountFromWaitClearSiteCookieList(siteId, selectAccount.getAccountId());
					account.setIsUsed(1);
					account.setAllocateTime(System.currentTimeMillis());
					addOrUpdateSiteCookieList(siteId, siteCookieList);
					
					break;
				}else{
					// 说明这个账号在数据库中已经被删除了
					list.add(info);
				}
			}
			
			for(SiteAccountInfo info : list){
				siteSubGroupIdCookieList.remove(info);
			}
			addOrUpdateSiteSubGroupCookieList(siteId, agentSubgroupId, siteSubGroupIdCookieList);
		}
		
		/*{
			removeSiteAccountFromWaitClearSiteCookieList(siteId, selectAccount.getAccountId());
			addOrUpdateSiteSubGroupCookieList(siteId, agentSubgroupId, siteSubGroupIdCookieList);
			// 更新站点账号列表中的账号分配时间
			if(siteCookieList != null && siteCookieList.size() > 0){
				for(SiteAccountInfo info : siteCookieList){
					if(info.getAccountId() == selectAccount.getAccountId()){
						info.setIsUsed(1);
						info.setAllocateTime(System.currentTimeMillis());
						break;
					}
				}
				addOrUpdateSiteCookieList(siteId, siteCookieList);
			}
		}else*/
		
		if(selectAccount == null){
			// 站点线路账号列表为空而且站点账号列表中账号均被使用，说明站点线路账号列表有问题(cache丢失)
			// 此时查看待清理站点列表
			List<SiteAccountInfo> waitClearSiteCookieList = getWaitClearSiteCookieListFromCache(siteId);
			
			//selectAccount = selectAccountFromWaitClearSiteCookieList(waitClearSiteCookieList);
			
			if(waitClearSiteCookieList != null && waitClearSiteCookieList.size() > 0){
				// 记录站点线路账号列表的无效账号
				List<SiteAccountInfo> list = new ArrayList<SiteAccountInfo>();
			
				Collections.sort(waitClearSiteCookieList, getComparator(1, "allocateTime"));
				for(SiteAccountInfo info : waitClearSiteCookieList){
					// 账号存在站点账号列表中
					SiteAccountInfo account = findSiteAccountInfoFromListByAccountId(siteCookieList, info.getAccountId());
					if(account != null){
						info.setCookie(account.getCookie());
						info.setPasswd(account.getPasswd());
						info.setUserName(account.getUserName());
						info.setIsAutoLogin(account.getIsAutoLogin());
						selectAccount = info;
						
						account.setIsUsed(1);
						account.setAllocateTime(System.currentTimeMillis());
						addOrUpdateSiteCookieList(siteId, siteCookieList);
						break;
					}else{
						// 说明这个账号在数据库中已经被删除了
						list.add(info);
					}
				}
				
				removeSiteAccountFromWaitClearSiteCookieList(siteId, selectAccount.getAccountId());
				
				for(SiteAccountInfo info : list){
					waitClearSiteCookieList.remove(info);
				}
				
				addOrUpdateWaitClearSiteCookieList(siteId, waitClearSiteCookieList);
			}
			
			
			/*if(selectAccount != null)
			{
				List<SiteAccountInfo> list = new ArrayList<SiteAccountInfo>();
				
				SiteAccountInfo account = findSiteAccountInfoFromListByAccountId(siteCookieList, selectAccount.getAccountId());
				// 标记站点账号列表中账号被使用，分配时间为当前时间
				if(siteCookieList != null && siteCookieList.size() > 0){
					for(SiteAccountInfo info : siteCookieList){
						if(info.getAccountId() == selectAccount.getAccountId()){
							info.setIsUsed(1);
							info.setAllocateTime(System.currentTimeMillis());
							break;
						}else{
							list.add(info);
						}
					}
					
					waitClearSiteCookieList.remove(selectAccount);
					addOrUpdateWaitClearSiteCookieList(siteId, waitClearSiteCookieList);
					addOrUpdateSiteCookieList(siteId, siteCookieList);
				}
			}*/
		}
		
		if(selectAccount != null && selectAccount.getIsAutoLogin() == 0)
		{
			addSiteAccountInfoToSiteSubGroupIdList(siteId, agentSubgroupId, selectAccount.getAccountId(), selectAccount.getCookie());
		}
		
		return selectAccount;
	}
	
	
	/**
	 * 从待清理站点账号列表中删除账号信息
	 * @param siteId 站点ID
	 * @param accountId 账号ID
	 */
	public void removeSiteAccountFromWaitClearSiteCookieList(int siteId,int accountId){
		List<SiteAccountInfo> waitClearSiteCookieList = getWaitClearSiteCookieListFromCache(siteId);
		if(waitClearSiteCookieList == null || waitClearSiteCookieList.isEmpty()){
			return;
		}
		SiteAccountInfo info = findSiteAccountInfoFromListByAccountId(waitClearSiteCookieList, accountId);
		if(info != null){
			waitClearSiteCookieList.remove(info);
			addOrUpdateWaitClearSiteCookieList(siteId, waitClearSiteCookieList);
		}
	}
	
	/**
	 * 从站点账号列表中删除账号信息
	 * @param siteId 站点ID
	 * @param accountId 账号ID
	 */
	public void removeSiteAccountFromSiteCookieList(int siteId,int accountId){
		List<SiteAccountInfo> siteCookieList = getSiteCookieListFromCache(siteId,true);
		if(siteCookieList == null || siteCookieList.isEmpty()){
			return;
		}
		SiteAccountInfo info = findSiteAccountInfoFromListByAccountId(siteCookieList, accountId);
		if(info != null){
			siteCookieList.remove(info);
			addOrUpdateSiteCookieList(siteId, siteCookieList);
		}
	}
	
	/**
	 * 从站点线路账号列表中删除账号信息
	 * @param siteId 站点ID
	 * @param accountId 账号ID
	 */
	public void removeSiteAccountFromSiteSubGroupIdCookieList(int siteId,int agentSubgroupId,int accountId){
		List<SiteAccountInfo> siteSubGroupIdCookieList = getSiteSubGroupIdCookieListFromCache(siteId, agentSubgroupId);
		if(siteSubGroupIdCookieList == null || siteSubGroupIdCookieList.isEmpty()){
			return;
		}
		SiteAccountInfo info = findSiteAccountInfoFromListByAccountId(siteSubGroupIdCookieList, accountId);
		if(info != null){
			siteSubGroupIdCookieList.remove(info);
			addOrUpdateSiteSubGroupCookieList(siteId,agentSubgroupId, siteSubGroupIdCookieList);
		}
	}
	
	/**
	 * 将取到的账号信息复制到回复协议中
	 * 
	 * @param reqAccountWritable 回复协议
	 * @param selectAccount 账号信息
	 * @return
	 */
	private ResTaskRunnerReqAccountWritable setAccountValue(ResTaskRunnerReqAccountWritable reqAccountWritable,SiteAccountInfo selectAccount){
		reqAccountWritable.userName = selectAccount.getUserName() == null ? "":selectAccount.getUserName();
		reqAccountWritable.passwd = selectAccount.getPasswd() == null ? "":selectAccount.getPasswd();
		reqAccountWritable.cookie = selectAccount.getCookie() == null ? "":selectAccount.getCookie();
		reqAccountWritable.isAutoLogin = selectAccount.getIsAutoLogin();
		reqAccountWritable.accountId = selectAccount.getAccountId();
		return reqAccountWritable;
	}
	
	
	/**
	 * 从siteCookieList中挑选一个未被使用的账号
	 * @param list 站点账号列表
	 * @param isShare 账号如果不共享，分配后设置为被使用中
	 */
	public  SiteAccountInfo selectAccountFromSiteCookieList(List<SiteAccountInfo> list,boolean isShare){
		if(list == null || list.isEmpty() ){
			return null;
		}
		log.info("select an account from list ");
		try {
			for(SiteAccountInfo info : list){
				// 有效的且未被使用
				if(info.getStatus() == 1 && info.getIsUsed() == 0){
					if(!isShare)
					{
						info.setIsUsed(1);
					}
					// 设置站点账号分配时间
					info.setAllocateTime(System.currentTimeMillis());
					return info;
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("select an account  failed!",e);
			return null;
		}

	}
	
	/**
	 * 从siteCookieList中挑选一个被使用的账号
	 * @param list 站点账号列表
	 * @param isUser 0：未使用 	1：使用中
	 */
	public  SiteAccountInfo selectAccountFromWaitClearSiteCookieList(List<SiteAccountInfo> list){
		
		if(list == null || list.isEmpty() ){
			return null;
		}
		log.info("Begin select an account ....");
		try {
			Collections.sort(list, getComparator(1, "allocateTime"));
			SiteAccountInfo info = list.get(0);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("select an account  failed!",e);
			return null;
		}

	}
	
	/**
	 * 从siteCookieList中挑选一个被使用的账号
	 * @param list 站点账号列表
	 * @param isUser 0：未使用 	1：使用中
	 */
	public  SiteAccountInfo selectAccountFromSiteCookieList(List<SiteAccountInfo> list,int isUser){
		if(list == null || list.isEmpty() ){
			return null;
		}
		log.info("Begin select an account ....");
		
		try {
			Collections.sort(list, getComparator(1, "allocateTime"));
			for(SiteAccountInfo info : list){
				// 有效的且被使用
				if(info.getStatus() == 1 && info.getIsUsed() == isUser){
					// 设置站点账号分配时间
					info.setAllocateTime(System.currentTimeMillis());
					return info;
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("select an account  failed!" ,e);
			return null;
		}

	}
	
	/**
	 * 从siteSubGroupCookieList中挑选一个分享次数最少的账号
	 * @param siteSubGroupIdCookieList 站点线路账号列表
	 * @param siteCookieList 站点账号列表
	 */
	public  SiteAccountInfo selectAccountFromSiteSubGroupCookieList(List<SiteAccountInfo> siteSubGroupIdCookieList,List<SiteAccountInfo> siteCookieList){
		
		log.info("Begin select an account ....");
		
		if(siteSubGroupIdCookieList == null || siteSubGroupIdCookieList.isEmpty() ){
			return null;
		}
		
		
		try {
			Collections.sort(siteSubGroupIdCookieList, getComparator(0, "shareNum"));
			for(SiteAccountInfo info : siteSubGroupIdCookieList){
				// 账号存在站点账号列表中
				SiteAccountInfo account = findSiteAccountInfoFromListByAccountId(siteCookieList, info.getAccountId());
				if(account != null){
					// 账号有效
					if(info.getStatus() == 1){
						info.setCookie(account.getCookie());
						info.setPasswd(account.getPasswd());
						info.setUserName(account.getUserName());
						info.setIsAutoLogin(account.getIsAutoLogin());
						return info;
					}
				}
			}
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("select an account  failed!",e);
			return null;
		}

	}
	/**
	 * 对List进行排序
	 * 
	 * @param desc 0：升序 	1：降序
	 * @param sortVal  排序字段
	 * @return
	 */
	private Comparator<SiteAccountInfo> getComparator(final int desc,final String sortVal){
		Comparator<SiteAccountInfo> comparator = new Comparator<SiteAccountInfo>() {
			public int compare(SiteAccountInfo s1, SiteAccountInfo s2) {
				// 按照分享次数排序
				if(sortVal.equals("shareNum")){
					if (s1.getShareNum() != s2.getShareNum()) {
						if(desc == 0)
						{
							return s1.getShareNum() - s2.getShareNum();
						}else{
							return s2.getShareNum() - s1.getShareNum();
						}
					} 
				}
				if(sortVal.equals("allocateTime")){
					if (s1.getAllocateTime() != s2.getAllocateTime()) {
						if(desc == 0)
						{
							return (int)(s1.getAllocateTime() - s2.getAllocateTime());
						}else{
							return (int)(s2.getAllocateTime() - s1.getAllocateTime());
						}
					} 
				}
				return 0;
			}
		};
		
		return comparator;
	}
	
	/**
	 * 
	 * 从集合中选择一个账号
	 * 
	 * @param list 账号集合
	 * @param accountId 账号ID
	 * @return
	 */
	public SiteAccountInfo findSiteAccountInfoFromListByAccountId(List<SiteAccountInfo> list, int accountId){
		if(list == null || list.isEmpty()){
			return null;
		}
		
		for(SiteAccountInfo info : list){
			if(info.getAccountId() == accountId){
				return info;
			}
		}
		return null;
	}
	
	/**
	 * 更换账号
	 * @param writable 更换账号请求协议
	 * @return
	 */
	public ResTaskRunnerReqAccountWritable changeAccountByTaskRunnerChangeAccountWritable(
			TaskRunnerChangeAccountWritable writable) {
		// 归还账号
		/*TaskRunnerReturnAccountWritable returnAccountWritable = new TaskRunnerReturnAccountWritable();
		returnAccountWritable.accountId = writable.accountId;
		returnAccountWritable.agentSubgroupId = writable.agentSubgroupId;
		returnAccountWritable.isAutoLogin = writable.isAutoLogin;
		returnAccountWritable.isShare = writable.oldAccountIsShare;
		returnAccountWritable.siteId = writable.siteId;
		returnAccountWritable.status = writable.status;
		FeedBackWritable backWritable = this.recoverAccountByWritable(returnAccountWritable);
		
		if(backWritable.code == CodeStatus.succCode){
			// 申请一个新账号
			TaskRunnerReqAccountWritable reqAccountWritable = new TaskRunnerReqAccountWritable();
			reqAccountWritable.agentSubgroupId = writable.agentSubgroupId;
			reqAccountWritable.isShare = writable.isShare;
			reqAccountWritable.siteId = writable.siteId;
			ResTaskRunnerReqAccountWritable newAccount = this.getAccountByWritable(reqAccountWritable);
			return newAccount;
		}
		*/
		return new ResTaskRunnerReqAccountWritable();
	}
	
	
	/**
	 * 账号归还
	 * @param writable 账号归还协议
	 * @return
	 */
	public FeedBackWritable recoverAccountByWritable(TaskRunnerReturnAccountWritable writable)throws Exception {
		FeedBackWritable backWritable = null;
		int code = -1;
		String errorMessage = "";
		String objectStr = "";
		int siteId = writable.siteId;
		int agentSubgroupId = writable.agentSubgroupId;
		int accountId = writable.accountId;
		//从cache中获取站点所有账号列表
		List<SiteAccountInfo> siteCookieList = getSiteCookieListFromCache(siteId,true);
		// 归还账户
		SiteAccountInfo recoverAccount = null;
		try {
			// 账号共享
			if(writable.isShare == 1){
				//从cache中获取站点对应线路的所有账号列表
				List<SiteAccountInfo> siteSubGroupIdCookieList = getSiteSubGroupIdCookieListFromCache(siteId, agentSubgroupId);
				recoverAccount = findSiteAccountInfoFromListByAccountId(siteSubGroupIdCookieList, accountId);
				// 1-账号有效 2-cookie失效 3-账号登录失败
				if(writable.status == 1){
					if(recoverAccount != null){
						int shareNum = -1;
						//在站点对应线路的所有账号列表找,找到更新分享数
						shareNum = recoverAccount.getShareNum();
						recoverAccount.setShareNum(--shareNum);
						if(shareNum == 0){
							//将user对应的账号加入到清理账号列表，更新待清理账号列表到cache
							addSiteAccountInfoToWaitClearSiteCookieList(siteId, recoverAccount);
							// 账号线路列表中删除此账号信息
							siteSubGroupIdCookieList.remove(recoverAccount);
						}
						// 更新站点线路账号信息	
						addOrUpdateSiteSubGroupCookieList(siteId, agentSubgroupId, siteSubGroupIdCookieList);
					}
				}
				
				if(writable.status == 2){
					if(recoverAccount != null)
					{
						// 从站点线路账号列表中删除失效账号
						siteSubGroupIdCookieList.remove(recoverAccount);
						addOrUpdateSiteSubGroupCookieList(siteId, agentSubgroupId, siteSubGroupIdCookieList);
						// 非自动登录
						if(recoverAccount.getIsAutoLogin() == 0)
						{
							// 从站点账号列表中删除账号
							removeSiteAccountFromSiteCookieList(siteId, accountId);
							// 更新数据库
							setSiteAccountStatus(accountId, 2);
						}else{
							// 自动登录
							// 将站点账号列表设置为未使用
							for(SiteAccountInfo info :siteCookieList){
								if(info.getAccountId() == accountId){
									info.setIsUsed(0);
									info.setAllocateTime(0);
									break;
								}
							}
							addOrUpdateSiteCookieList(siteId, siteCookieList);
						}
					}
				}
				// 共享账号不用name和pwd,所以不存在账号失效
				if(writable.status == 3){
					log.error("recover  account is failed !  accountId is " + writable.accountId + " and  isShare is " + writable.isShare + " and status is " + writable.status);
				}
			}
			
			// 账号不共享
			if(writable.isShare == 0){
				//从cache中获取站点所有账号列表，设置user对应账号的状态为未使用更新cache
				recoverAccount = findSiteAccountInfoFromListByAccountId(siteCookieList, accountId);
				if(writable.status == 1){
					if(recoverAccount != null)
					{
						// 被使用中
						if(recoverAccount.getIsUsed() == 1){
							recoverAccount.setIsUsed(0);
							recoverAccount.setAllocateTime(0);
							addOrUpdateSiteCookieList(siteId, siteCookieList);
						}
					}
				}
				
				if(writable.status == 2){
					log.error("recover account is failed ! accountId is " + writable.accountId + " and isShare is " + writable.isShare + " and status is " + writable.status);
				}
				
				if(writable.status == 3){
					if(recoverAccount != null)
					{
						siteCookieList.remove(recoverAccount);
						addOrUpdateSiteCookieList(siteId, siteCookieList);
					}
					setSiteAccountStatus(accountId, 3);
				}
			}
			code = CodeStatus.succCode;
		} catch (Exception e) {
			code = CodeStatus.failCode;
			errorMessage = "Recover the Account failed!";
			e.printStackTrace();
			log.error("Recover the Account failed!",e);
			throw e;
		}
		
		backWritable = new FeedBackWritable(code, errorMessage, objectStr);
		return backWritable;
	}
	
	
	
	
	/**
	 * 将账号添加到待清理站点账号列表
	 * 
	 * @param siteId 站点ID
	 * @param info 账号信息
	 */
	public void addSiteAccountInfoToWaitClearSiteCookieList(int siteId,SiteAccountInfo info){
		
		List<SiteAccountInfo> waitClearSiteCookieList = getWaitClearSiteCookieListFromCache(siteId);
		info.setEnterClearListTime(System.currentTimeMillis());
		if(waitClearSiteCookieList != null){
			SiteAccountInfo account = findSiteAccountInfoFromListByAccountId(waitClearSiteCookieList, info.getAccountId());
			if(account == null){
				waitClearSiteCookieList.add(info);
			}
		}else{
			waitClearSiteCookieList = new ArrayList<SiteAccountInfo>();
			waitClearSiteCookieList.add(info);
		}
		
		addOrUpdateWaitClearSiteCookieList(siteId, waitClearSiteCookieList);
	}
	
	
	/**
	 * 更新账户cookie
	 * @param writable 账号协议
	 * @return
	 */
	public FeedBackWritable updateAccountCookieByWritable(TaskRunnerUpdateAccountCookieWritable writable){
		FeedBackWritable backWritable = null;
		int code = CodeStatus.succCode;
		String errorMessage = "";
		String objectStr = "";
		
		try {
			//Server将账号的Cookie添加对站点对应线路账号列表中，并标识站点列表中该账号为被使用状态，更新cache
			int siteId = writable.siteId;
			int agentSubgroupId = writable.agentSubgroupId;
			
			if(addSiteAccountInfoToSiteSubGroupIdList(siteId, agentSubgroupId, writable.accountId,writable.cookie)){
				List<SiteAccountInfo> siteCookieList = getSiteCookieListFromCache(siteId,true);
				if(siteCookieList != null){
					
					for(SiteAccountInfo info : siteCookieList){
						if(info.getAccountId() == writable.accountId){
							if(info.getIsUsed() == 0){
								info.setIsUsed(1);
								info.setAllocateTime(System.currentTimeMillis());
							}
							break;
						}
					}
					addOrUpdateSiteCookieList(siteId, siteCookieList);
				}else{
					code = CodeStatus.failCode;
					errorMessage = "the siteCookieList is null";
				}
			}else{
				code = CodeStatus.failCode;
				errorMessage = "add the account into the SiteSubGroupIdList failed";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code = CodeStatus.failCode;
			errorMessage = "update the account cookie failed";
		}
		
		log.error(errorMessage);
		backWritable = new FeedBackWritable(code, errorMessage, objectStr);
		
		return backWritable;
	}
	/**
	 * 将账号添加到SiteSubGroupIdList中
	 * 
	 * @param siteId 站点ID
	 * @param agentSubgroupId 线路ID
	 * @param accountId 账号ID
	 * @param cookie 账号cookie
	 * @return
	 */
	public boolean addSiteAccountInfoToSiteSubGroupIdList(int siteId,int agentSubgroupId,int accountId,String cookie){
		List<SiteAccountInfo> siteCookieList = getSiteCookieListFromCache(siteId,true);
		List<SiteAccountInfo> siteSubGroupIdCookieList = getSiteSubGroupIdCookieListFromCache(siteId, agentSubgroupId);
		try {
			
			if(siteSubGroupIdCookieList == null){
				siteSubGroupIdCookieList = new ArrayList<SiteAccountInfo>();
			}
			
			// 标记siteSubGroupIdList中是否有此账号
			boolean flag = false;
			for(SiteAccountInfo info : siteSubGroupIdCookieList){
				if(info.getAccountId() == accountId){
					info.setCookie(cookie);
					int shareNum = info.getShareNum();
					info.setShareNum(++shareNum);
					flag = true;
					break;
				}
			}
			
			// 如果没有账号，添加账号到siteSubGroupIdList中
			if(!flag){
				SiteAccountInfo info = findSiteAccountInfoFromListByAccountId(siteCookieList, accountId);
				info.setCookie(cookie);
				int shareNum = info.getShareNum();
				info.setShareNum(++shareNum);
				siteSubGroupIdCookieList.add(info);
			}
			
			addOrUpdateSiteSubGroupCookieList(siteId, agentSubgroupId, siteSubGroupIdCookieList);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("add an account into the siteSubgroupIdList failed!",e);
			return false;
		}
		
	}
	
	/**
	 * 扫描站点账号列表发现最近分配时间超过2小时（时长根据需要设置）的账号，则设置其状态为未使用
	 */
	public void scanSiteCookieList(){
		List<Integer> sites = getSiteIdListFromCache(true);
		if(sites == null || sites.isEmpty()){
			return;
		}
		
		for(int siteId : sites){
			List<SiteAccountInfo> siteCookieList = getSiteCookieListFromCache(siteId,false);
			if(siteCookieList != null)
			{
				for(SiteAccountInfo info : siteCookieList){
					long currentTime = System.currentTimeMillis();
					if(info.getIsUsed() == 1){
						long allocateTime = info.getAllocateTime();
						if(currentTime - allocateTime >= siteAccountTimeOut){
							info.setIsUsed(0);
							info.setAllocateTime(0);
						}
					}
				}
				addOrUpdateSiteCookieList(siteId, siteCookieList);
			}
		}
	}
	
	/**
	 *  定时扫描待清理账号列表，如果发现账号加入队列时间超过1小时（时长根据需要设置），
	 *  则从列表中删除该账号，并设置站点账号列表中该账号状态为未使用，并更新到cache中
	 */
	public void scanWaitClearSiteCookieList(){
		List<Integer> sites = getSiteIdListFromCache(true);
		if(sites == null || sites.isEmpty()){
			return;
		}
		
		List<SiteAccountInfo> deleAccounts = new ArrayList<SiteAccountInfo>();
		
		for(int siteId : sites){
			deleAccounts.clear();
			List<SiteAccountInfo> waitClearSiteCookieList = getWaitClearSiteCookieListFromCache(siteId);
			List<SiteAccountInfo> siteCookieList = getSiteCookieListFromCache(siteId,true);
			if(waitClearSiteCookieList != null)
			{
				for(SiteAccountInfo info : waitClearSiteCookieList){
					long currentTime = System.currentTimeMillis();
					long allocateTime = info.getEnterClearListTime();
					if(currentTime - allocateTime >= waitClearAccountTimeOut){
						deleAccounts.add(info);
					}
				}
			}
			
			if(deleAccounts.size() > 0){
				for(SiteAccountInfo info : deleAccounts){
					waitClearSiteCookieList.remove(info);
					// 还原站点账号列表中的信息
					if(siteCookieList != null){
						for(SiteAccountInfo info2 : siteCookieList){
							if(info2.getAccountId() == info.getAccountId()){
								info2.setIsUsed(0);
								info2.setAllocateTime(0);
								break;
							}
						}
					}
				}
				addOrUpdateSiteCookieList(siteId, siteCookieList);
				addOrUpdateWaitClearSiteCookieList(siteId, waitClearSiteCookieList);
			}
		}
		
	}
	
	public void getSiteAccountInfo(int siteId){
		System.out.println("站点账号列表开始----------------");
		List<SiteAccountInfo> list = getSiteCookieListFromCache(siteId,false);
		System.out.println("site " + siteId + "的cache账号信息：");
		if(list != null && list.size() > 0){
			for(SiteAccountInfo info : list){
				System.out.println(info.toString());
			}
		}else{
			System.out.println("此站点没有账号信息");
		}
		System.out.println("站点账号列表结束----------------");
	}
	
	public void getSiteSubgroupAccountInfo(int siteId,int subGroupId){
		System.out.println("站点线路账号列表开始----------------");
		List<SiteAccountInfo> list = getSiteSubGroupIdCookieListFromCache(siteId, subGroupId);
		System.out.println("site " + siteId + ",subgroupID " + subGroupId + " 的cache账号信息：");
		if(list != null && list.size() > 0){
			for(SiteAccountInfo info : list){
				System.out.println(info.toString());
			}
		}else{
			System.out.println("此站点线路没有账号信息");
		}
		System.out.println("站点线路账号列表结束----------------");
	}
	
	public void getWaitClearAccountInfo(int siteId){
		System.out.println("待清理站点账号列表开始----------------");
		List<SiteAccountInfo> list = getWaitClearSiteCookieListFromCache(siteId);
		System.out.println("site " + siteId + " 的待清理cache账号信息：");
		if(list != null && list.size() > 0){
			for(SiteAccountInfo info : list){
				System.out.println(info.toString());
			}
		}else{
			System.out.println("此站点没有待清理账号信息");
		}
		System.out.println("待清理站点账号列表结束----------------");
	}
	
	/**
	 * 获取所有的cache信息
	 * 
	 * @param subGroupId 线路ID
	 * 测试用
	 */
	public  void getAllCacheInfo(int subGroupId){
		List<Integer> sites = getSiteIdListFromCache(true);
		System.out.println();
		System.out.println();
		System.out.println("站点账号列表开始----------------");
		if(sites == null){
			return;
		}
		for(int siteId:sites){
			
			List<SiteAccountInfo> list = getSiteCookieListFromCache(siteId,true);
			
			System.out.println("site " + siteId + "的cache账号信息：");
			if(list != null && list.size() > 0){
				for(SiteAccountInfo info : list){
					System.out.println(info.toString());
				}
			}else{
				System.out.println("此站点没有账号信息");
			}
			
		}
		System.out.println("站点账号列表结束----------------");
		System.out.println();
		System.out.println();
		System.out.println("站点线路账号列表开始----------------");
		for(int siteId:sites){
			List<SiteAccountInfo> list = getSiteSubGroupIdCookieListFromCache(siteId, subGroupId);
			System.out.println("site " + siteId + ",subgroupID " + subGroupId + " 的cache账号信息：");
			if(list != null && list.size() > 0){
				for(SiteAccountInfo info : list){
					System.out.println(info.toString());
				}
			}else{
				System.out.println("此站点线路没有账号信息");
			}
			
		}
		System.out.println("站点线路账号列表结束----------------");
		System.out.println();
		System.out.println();
		System.out.println("待清理站点账号列表开始----------------");
		for(int siteId:sites){
			List<SiteAccountInfo> list = getWaitClearSiteCookieListFromCache(siteId);
			System.out.println("site " + siteId + " 的待清理cache账号信息：");
			if(list != null && list.size() > 0){
				for(SiteAccountInfo info : list){
					System.out.println(info.toString());
				}
			}else{
				System.out.println("此站点没有待清理账号信息");
			}
		}
		System.out.println("待清理站点账号列表结束----------------");
		System.out.println();
		System.out.println();
	}
	/**
	 * 清除所有的cache信息
	 * 
	 */
	public  void clearAllCache(int agentSubgroupId){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		String head = "ACCOUNT_";
		List<Integer> sites = getSiteIdListFromCache(true);
		
		if(sites == null){
			return;
		}
		for(int siteId : sites)
		{
			String waitClearSiteCookieListKey = head + "WAIT_CLEAR_SITE_" + siteId + "_" + "COOKIE_LIST";
			String siteCookieListKey = head + "SITE_" + siteId + "_" + "COOKIE_LIST";
			String siteSubGroupIdCookieListKey = head + "SITE_" + siteId + "_SUBGROUPID_" + agentSubgroupId + "_COOKIE_LIST";
			cacheClient.remove(waitClearSiteCookieListKey);
			cacheClient.remove(siteCookieListKey);
			cacheClient.remove(siteSubGroupIdCookieListKey);
		}
		String siteIdListKey = head + "SITE_ID_LIST";
		cacheClient.remove(siteIdListKey);
	}
	
	
	/**
	 * 清除所有的cache信息
	 * 
	 */
	public  void clearAllCache(int siteId,int agentSubgroupId){
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		String head = "ACCOUNT_";
		
			String waitClearSiteCookieListKey = head + "WAIT_CLEAR_SITE_" + siteId + "_" + "COOKIE_LIST";
			String siteCookieListKey = head + "SITE_" + siteId + "_" + "COOKIE_LIST";
			String siteSubGroupIdCookieListKey = head + "SITE_" + siteId + "_SUBGROUPID_" + agentSubgroupId + "_COOKIE_LIST";
			cacheClient.remove(waitClearSiteCookieListKey);
			cacheClient.remove(siteCookieListKey);
			cacheClient.remove(siteSubGroupIdCookieListKey);
		
		String siteIdListKey = head + "SITE_ID_LIST";
		cacheClient.remove(siteIdListKey);
	}
	
	
	
	public static void start_up(String[] args) throws Exception {
		log.info("init config ...");
		// 获取配置文件路径
		String path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";
		ApplicationContext.initialiaze(path, true);
		InitializerRegister.getInstance().execute();
		Configuration conf = new Configuration();
		conf.addResource(new Path("conf/conf.xml"));
		// cache失效时间
		int cacheEnableTime = conf.getInt("account.center.cache.enable.time", 600);
		// cache锁超时时间
		int lockHoldTime = conf.getInt("account.center.lock.hold.time", 10);
		// 数据库
		int numHandlers = conf.getInt("data.collection.num.handlers", 150);
		// 扫描站点账号列表时间间隔
		int scanSiteAccountInterval = conf.getInt("scan.site.cookie.interval", 600000);
		// 扫描待清理账号列表时间间隔
		int scanWaitclearSiteAccountInterval = conf.getInt("scan.waitclear.cookie.interval", 600000);
		// 扫描数据库时间间隔
		int scanDatabaseInterval = conf.getInt("scan.database.interval", 600000);
		
		// 站点列表账号超时时间
		int siteAccountTimeOut = conf.getInt("site.account.timeout", 7200000);
		// 站点线路账号列表失效时间
		int siteSubgroupTimeOut = conf.getInt("site.subgroup.account.timeout", 7200000);
		// 待清理列表账户超时时间
		int waitClearAccountTimeOut = conf.getInt("waitclear.site.account.timeout", 3600000);
		
		AccountServerDaemon.setSiteSubgroupTimeOut(siteSubgroupTimeOut);
		AccountServerDaemon.setCacheEnableTime(cacheEnableTime);
		AccountServerDaemon.setLockHoldTime(lockHoldTime);
		AccountServerDaemon.setNumHandlers(numHandlers);
		AccountServerDaemon.setSiteAccountTimeOut(siteAccountTimeOut);
		AccountServerDaemon.setWaitClearAccountTimeOut(waitClearAccountTimeOut);
		// 启动服务端的监听服务线程
		serverAddress = conf.get("account.center.address", "0.0.0.0");
		int port = conf.getInt("account.center.port", 16003);
		
		AccountDaemonListener serverListener = new AccountDaemonListener(new Configuration(), serverAddress, port, numHandlers);
		serverListener.start();
		
		ScanSiteCookieListThread scanSiteCookieListThread = new ScanSiteCookieListThread(scanSiteAccountInterval);
		scanSiteCookieListThread.start();
		
		ScanWaitClearSiteCookieListThread waitClearSiteCookieListThread = new ScanWaitClearSiteCookieListThread(scanWaitclearSiteAccountInterval);
		waitClearSiteCookieListThread.start();
		
		ScanDatabaseThread databaseThread = new ScanDatabaseThread(scanDatabaseInterval);
		databaseThread.start();
		
		log.info("Account Server start success !");
	}

	public static void main(String[] args) {
		try {
			start_up(args);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// 测试用
	public static  void testAllocateAccount(int siteId,int subGroupId,int share){
		TaskRunnerReqAccountWritable writable = new TaskRunnerReqAccountWritable();
		writable.siteId = siteId;
		writable.agentSubgroupId = subGroupId;
		writable.isShare = share;
		
		try {
			ResTaskRunnerReqAccountWritable s = AccountServerDaemon.getInstance().getAccountByWritable(writable);
			System.out.println("得到的账号信息：" + s.toString());
			// 自动登录需要客户端回传cookie
			// 0：非自动登录 1：自动登录
			if(writable.isShare == 1 && s.accountId != 0 && s.isAutoLogin == 1){
				TaskRunnerUpdateAccountCookieWritable writable2 = new TaskRunnerUpdateAccountCookieWritable();
				writable2.accountId = s.accountId;
				writable2.agentSubgroupId = writable.agentSubgroupId;
				writable2.cookie = "55998899555";
				//writable2.isAutoLogin = s.isAutoLogin;
				writable2.siteId = writable.siteId;
				///writable2.status = 1;
				AccountServerDaemon.getInstance().updateAccountCookieByWritable(writable2);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 测试用
	public static void returnAccount(int accountId,int siteId,int subGroupId){
		
		TaskRunnerReturnAccountWritable writable3 = new TaskRunnerReturnAccountWritable();
		writable3.accountId = accountId;
		writable3.agentSubgroupId = subGroupId;
		writable3.siteId = siteId;
		writable3.isAutoLogin = 0;
		writable3.isShare = 1;
		writable3.status = 2;
		try {
			AccountServerDaemon.getInstance().recoverAccountByWritable(writable3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}

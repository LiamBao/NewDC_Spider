package com.cic.datacrawl.account.bean;

import java.io.Serializable;

/**
 * 
 * 站点账号信息
 * 
 * @author charles.chen
 *
 */
public class SiteAccountInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int isAutoLogin;//0：非自动登录 1：自动登录
	private int accountId;
	private String userName;
	private String passwd;
	private String cookie;
	/**
	 * 记录账号是否被使用
	 */
	private int isUsed = 0;// 0：未使用 	1：使用中
	/**
	 * 记录账号是否失效
	 */
	private int status = 1;// 1-账号有效   2-cookie失效  3-账号登录失败
	/**
	 * 账号分享次数
	 */
	private int shareNum = 0;
	/**
	 * 账号所属分组ID
	 */
	private int agentSubgroupId;
	/**
	 * 账号分配时间
	 */
	private long allocateTime = 0;
	/**
	 * 账号进入待清理站点列表时间
	 */
	private long enterClearListTime = 0;
 
	
	
	public long getEnterClearListTime() {
		return enterClearListTime;
	}

	public void setEnterClearListTime(long enterClearListTime) {
		this.enterClearListTime = enterClearListTime;
	}

	public int getAgentSubgroupId() {
		return agentSubgroupId;
	}

	public void setAgentSubgroupId(int agentSubgroupId) {
		this.agentSubgroupId = agentSubgroupId;
	}

	public long getAllocateTime() {
		return allocateTime;
	}

	public void setAllocateTime(long allocateTime) {
		this.allocateTime = allocateTime;
	}

	public int getIsAutoLogin() {
		return isAutoLogin;
	}

	public void setIsAutoLogin(int isAutoLogin) {
		this.isAutoLogin = isAutoLogin;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	
	
	public int getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(int isUsed) {
		this.isUsed = isUsed;
	}

	

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getShareNum() {
		return shareNum;
	}

	public void setShareNum(int shareNum) {
		this.shareNum = shareNum;
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("ResLoginAccount: [isAutoLogin: " + isAutoLogin + ",accountId: " + accountId + 
				",userName: " + userName + ",passwd: " + passwd + 
				",isUsed: " + isUsed + ",status: " + status + ",shareNum: " + shareNum + 
				",agentSubgroupId: " + agentSubgroupId + ",allocateTime: " + allocateTime + 
				",enterClearListTime: " + enterClearListTime + ",cookie: " + cookie + "]");  
        return strBuf.toString(); 
	}
}

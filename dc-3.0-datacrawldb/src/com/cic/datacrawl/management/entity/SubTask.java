package com.cic.datacrawl.management.entity;

import java.io.Serializable;

public class SubTask implements Serializable{
	private static final long serialVersionUID = 4566669661055123683L;
	
	private int agentId;
	private java.lang.String agentIp;
	private byte agentType;
	private int taskId;
	private long subTaskId;
	private java.lang.String subtaskKey; 
	private java.lang.String scriptFile; 
	private java.lang.String scriptMain;
	private int downPageCount;
	private int scrapeCount;
	private int agentGroupId; 
	private int siteId;
	private int turnPageWaitTime;
	private byte useSnapShot;
	private int errorCode;
	private java.lang.String errorMsg; 
	private java.lang.String errorUrl; 
	private int batchId;
	private long createTime; 
	private long startTime; 
	private int costTime;
	private int exeTime;
	private int effectiveTimeRate;
	private int projectId;
	private java.lang.String keyWord;
	private java.lang.String forumId;
	private java.lang.String threadId;
	
	public SubTask() {
	}
	
	public SubTask(SubTaskEntity entity) {
		this.setAgentType(entity.getAgentType());
		this.setTaskId(entity.getTaskId());
		this.setSubTaskId(entity.getSubTaskId());
		this.setSubtaskKey(entity.getSubtaskKey());
		this.setScriptFile(entity.getScriptFile());
		this.setScriptMain(entity.getScriptMain());
		this.setSiteId(entity.getSiteId());
		this.setTurnPageWaitTime(entity.getTurnPageWaitTime());
		this.setUseSnapShot(entity.getUseSnapShot());
		this.setBatchId(entity.getBatchId());
		this.setCreateTime(entity.getCreateTime());
		this.setStartTime(entity.getStartTime());
		this.setProjectId(entity.getProjectId());
		this.setKeyWord(entity.getKeyWord());
		this.setForumId(entity.getForumId());
		this.setThreadId(entity.getThreadId());
	}
	
	public int getAgentId() {
		return agentId;
	}
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	public java.lang.String getAgentIp() {
		return agentIp;
	}
	public void setAgentIp(java.lang.String agentIp) {
		this.agentIp = agentIp;
	}
	public byte getAgentType() {
		return agentType;
	}
	public void setAgentType(byte agentType) {
		this.agentType = agentType;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public long getSubTaskId() {
		return subTaskId;
	}
	public void setSubTaskId(long subTaskId) {
		this.subTaskId = subTaskId;
	}
	public java.lang.String getSubtaskKey() {
		return subtaskKey;
	}
	public void setSubtaskKey(java.lang.String subtaskKey) {
		this.subtaskKey = subtaskKey;
	}
	public java.lang.String getScriptFile() {
		return scriptFile;
	}
	public void setScriptFile(java.lang.String scriptFile) {
		this.scriptFile = scriptFile;
	}
	public java.lang.String getScriptMain() {
		return scriptMain;
	}
	public void setScriptMain(java.lang.String scriptMain) {
		this.scriptMain = scriptMain;
	}
	public int getDownPageCount() {
		return downPageCount;
	}
	public void setDownPageCount(int downPageCount) {
		this.downPageCount = downPageCount;
	}
	public int getScrapeCount() {
		return scrapeCount;
	}
	public void setScrapeCount(int scrapeCount) {
		this.scrapeCount = scrapeCount;
	}
	public int getAgentGroupId() {
		return agentGroupId;
	}
	public void setAgentGroupId(int agentGroupId) {
		this.agentGroupId = agentGroupId;
	}
	public int getSiteId() {
		return siteId;
	}
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	public int getTurnPageWaitTime() {
		return turnPageWaitTime;
	}
	public void setTurnPageWaitTime(int turnPageWaitTime) {
		this.turnPageWaitTime = turnPageWaitTime;
	}
	public byte getUseSnapShot() {
		return useSnapShot;
	}
	public void setUseSnapShot(byte useSnapShot) {
		this.useSnapShot = useSnapShot;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public java.lang.String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(java.lang.String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public java.lang.String getErrorUrl() {
		return errorUrl;
	}
	public void setErrorUrl(java.lang.String errorUrl) {
		this.errorUrl = errorUrl;
	}
	public int getBatchId() {
		return batchId;
	}
	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public int getCostTime() {
		return costTime;
	}
	public void setCostTime(int costTime) {
		this.costTime = costTime;
	}
	public int getExeTime() {
		return exeTime;
	}

	public void setExeTime(int exeTime) {
		this.exeTime = exeTime;
	}

	public int getEffectiveTimeRate() {
		return this.effectiveTimeRate;
	}
	
	public void setEffectiveTimeRate(int effectiveTimeRate) {
		this.effectiveTimeRate = effectiveTimeRate;
	}
	
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public java.lang.String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(java.lang.String keyWord) {
		this.keyWord = keyWord;
	}
	public java.lang.String getForumId() {
		return forumId;
	}
	public void setForumId(java.lang.String forumId) {
		this.forumId = forumId;
	}
	public java.lang.String getThreadId() {
		return threadId;
	}
	public void setThreadId(java.lang.String threadId) {
		this.threadId = threadId;
	}
}

package com.cic.datacollection.bean;

public class SubTaskInfo {
	private byte agentType;
	private int taskId;
	private long subTaskId;
	private String subTaskKey;
	private String scriptFile;
	private String scriptMain;
	private int siteId;
	private int batchId;
	private byte useSnapShot;
	private int turnPageWaitTime;
	private long createTime;
	private long startTime;
	private int projectId;
	private String keyWord;
	private String forumId;
	private String threadId;
	
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
	public String getSubTaskKey() {
		return subTaskKey;
	}
	public void setSubTaskKey(String subTaskKey) {
		this.subTaskKey = subTaskKey;
	}
	public String getScriptFile() {
		return scriptFile;
	}
	public void setScriptFile(String scriptFile) {
		this.scriptFile = scriptFile;
	}
	public String getScriptMain() {
		return scriptMain;
	}
	public void setScriptMain(String scriptMain) {
		this.scriptMain = scriptMain;
	}
	public int getSiteId() {
		return siteId;
	}
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	public int getBatchId() {
		return batchId;
	}
	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}
	public byte getUseSnapShot() {
		return useSnapShot;
	}
	public void setUseSnapShot(byte useSnapShot) {
		this.useSnapShot = useSnapShot;
	}
	public int getTurnPageWaitTime() {
		return turnPageWaitTime;
	}
	public void setTurnPageWaitTime(int turnPageWaitTime) {
		this.turnPageWaitTime = turnPageWaitTime;
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
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public String getForumId() {
		return forumId;
	}
	public void setForumId(String forumId) {
		this.forumId = forumId;
	}
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
}

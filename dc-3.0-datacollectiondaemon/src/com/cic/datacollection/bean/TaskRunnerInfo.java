package com.cic.datacollection.bean;

public class TaskRunnerInfo {
	private int agentId;
	private String agentAddress;
	private byte agentType;
	private int taskId;
	private long subTaskId;
	private String subTaskKey;
	private String scriptFile;
	private String scriptMain;
	private int scrapeCount;
	private int agentGroupId;
	private int siteId;
	private int errorCode;
	private String errorMsg;
	private String errorUrl;
	private int batchId;
	private byte useSnapShot;
	private int turnPageWaitTime;
	private long createTime;
	private long startTime;
	private int costTime;
	private int projectId;
	private String keyWord;
	private String forumId;
	private String threadId;
	private long heartBeatTime;
	private boolean infoFlag;
	
	public TaskRunnerInfo() {
	}
	
	public TaskRunnerInfo(TaskRunnerInfo other) {
		setAgentId(other.getAgentId());
		setAgentAddress(other.getAgentAddress());
		setAgentType(other.getAgentType());
		setTaskId(other.getTaskId());
		setSubTaskId(other.getSubTaskId());
		setSubTaskKey(other.getSubTaskKey());
		setScriptFile(other.getScriptFile());
		setScriptMain(other.getScriptMain());
		setScrapeCount(other.getScrapeCount());
		setAgentGroupId(other.getAgentGroupId());
		setSiteId(other.getSiteId());
		setErrorCode(other.getErrorCode());
		setErrorMsg(other.getErrorMsg());
		setErrorUrl(other.getErrorUrl());
		setBatchId(other.getBatchId());
		setUseSnapShot(other.getUseSnapShot());
		setTurnPageWaitTime(other.getTurnPageWaitTime());
		setCreateTime(other.getCreateTime());
		setStartTime(other.getStartTime());
		setCostTime(other.getCostTime());
		setProjectId(other.getProjectId());
		setKeyWord(other.getKeyWord());
		setForumId(other.getForumId());
		setThreadId(other.getThreadId());
		setHeartBeatTime(other.getHeartBeatTime());
		setInfoFlag(other.infoFlag);
	}
	
	public int getAgentId() {
		return agentId;
	}
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	public String getAgentAddress() {
		return agentAddress;
	}
	public void setAgentAddress(String agentAddress) {
		this.agentAddress = agentAddress;
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
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getErrorUrl() {
		return errorUrl;
	}
	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
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
	public int getCostTime() {
		return costTime;
	}
	public void setCostTime(int costTime) {
		this.costTime = costTime;
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
	public long getHeartBeatTime() {
		return heartBeatTime;
	}
	public void setHeartBeatTime(long heartBeatTime) {
		this.heartBeatTime = heartBeatTime;
	}
	public boolean isInfoFlag() {
		return infoFlag;
	}
	public void setInfoFlag(boolean infoFlag) {
		this.infoFlag = infoFlag;
	}
}

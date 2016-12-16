package com.cic.datacollection.mina.protocol;

import java.io.Serializable;

import com.cic.datacollection.mina.MinaRequestProtocol;

public class AgentFinishedReportProtocol extends MinaRequestProtocol implements Serializable {
	private static final long serialVersionUID = 1000021775550327390L;
	public int agentId;
	public String agentAddress;
	public byte agentType;
	public int taskId;
	public long subTaskId;
	public String subTaskKey;
	public String scriptFile;
	public String scriptMain;
	public int scrapeCount;
	public int agentGroupId;
	public int siteId;
	public int errorCode;
	public String errorMsg;
	public String errorUrl;
	public int batchId;
	public long createTime;
	public long startTime;
	public int exeTime;
	public int effectiveTimeRate;
	public int projectId;
	public String keyWord;
	public String forumId;
	public String threadId;
	public boolean infoFlag;  //标识上报的信息是否完整（如果Agent进程重启，会丢失记录的subTask信息，TaskRunner上报后，再上报到ControlServer，需要由Server补充subTask的信息）

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
	public int getExeTime() {
		return exeTime;
	}
	public void setExeTime(int exeTime) {
		this.exeTime = exeTime;
	}
	public int getEffectiveTimeRate() {
		return effectiveTimeRate;
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
	public boolean isInfoFlag() {
		return infoFlag;
	}
	public void setInfoFlag(boolean infoFlag) {
		this.infoFlag = infoFlag;
	}
	
	public AgentFinishedReportProtocol(){
		
	}
	
	public AgentFinishedReportProtocol(String protocolName) {
		super.setProtocolName(protocolName);
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("AgentFinishedReport: [agentId: " + agentId + ",agentAddress: " + agentAddress + ",agentType: " + agentType + ",taskId: " + taskId + ",subTaskId: " + subTaskId + ",subTaskKey: " + subTaskKey + ",scriptFile: " + scriptFile + ",scriptMain: " + scriptMain + ",scrapeCount: " + scrapeCount + ",agentGroupId: " + agentGroupId + ",siteId: " + siteId  + ",errorCode: " + errorCode + ",errorMsg: " + errorMsg + ",errorUrl: " + errorUrl + ",batchId: " + batchId + ",createTime: " + createTime + ",startTime: " + startTime + ",exeTime: " + exeTime + ",effectiveTimeRate: " + effectiveTimeRate +  ",projectId: " + projectId + ",keyWord: " + keyWord + ",forumId: " + forumId + ",threadId: " + threadId + ",infoFlag: " + infoFlag + "]");  
        return strBuf.toString(); 
	}
}

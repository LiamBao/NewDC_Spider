package com.cic.datacollection.mina.protocol;

import java.io.Serializable;

import com.cic.datacollection.mina.MinaResponseProtocol;

public class ResGetSubTaskProtocol extends MinaResponseProtocol implements Serializable {
	private static final long serialVersionUID = 8323563503679045625L;
	public int code;
	public String errorMessage;
	private byte agentType;
	private int taskId;
	private long subTaskId;
	private String subTaskKey;
	private String scriptFile;
	private String scriptMain;
	private int agentGroupId;
	private int siteId;
	private int batchId;
	private long createTime;
	private long startTime;
	private int projectId;
	private String keyWord;
	private String forumId;
	private String threadId;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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
	
	public ResGetSubTaskProtocol() {	
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("ResGetSubTask: [code: " + code + ",errorMessage: " + errorMessage + ",agentType: " + ",taskId: " + taskId + ",subTaskId: " + subTaskId + ",subTaskKey: " + subTaskKey + ",scriptFile: " + scriptFile + ",scriptMain: " + scriptMain + ",agentGroupId: " + agentGroupId + ",siteId: " + siteId + ",batchId: " + batchId + ",createTime: " + createTime + ",startTime: " + startTime + ",projectId: " + projectId + ",keyWord: " + keyWord + ",forumId: " + forumId + ",threadId: " + threadId + "]");  
        return strBuf.toString(); 
	}
}

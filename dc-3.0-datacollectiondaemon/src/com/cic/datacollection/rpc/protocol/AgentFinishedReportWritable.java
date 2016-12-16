package com.cic.datacollection.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * 子任务执行结果上报协议
 * @author johnney.bu
 *
 */
public class AgentFinishedReportWritable implements Writable {
	public int agentId;
	public String agentAddress;
	public byte agentType;
	public int taskId;
	public long subTaskId;
	public String subTaskKey;
	public String scriptFile;
	public String scriptMain;
	public int downPageCount;
	public int scrapeCount;
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
	public byte useSnapShot;
	public int turnPageWaitTime;
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
		if(agentAddress == null) {
			this.agentAddress = "";
		} else {
			this.agentAddress = agentAddress;
		}
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
		if(subTaskKey == null) {
			this.subTaskKey = "";
		} else {
	 		this.subTaskKey = subTaskKey;
		}
	}

	public String getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(String scriptFile) {
		if(scriptFile == null) {
			this.scriptFile = "";
		} else {
			this.scriptFile = scriptFile;
		}
	}

	public String getScriptMain() {
		return scriptMain;
	}

	public void setScriptMain(String scriptMain) {
		if(scriptMain == null) {
			this.scriptMain = "";
		} else {
			this.scriptMain = scriptMain;
		}
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
		if(errorMsg == null) {
			this.errorMsg = "";
		} else {
			this.errorMsg = errorMsg;
		}
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		if(errorUrl == null) {
			this.errorUrl = "";
		} else {
			this.errorUrl = errorUrl;
		}
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
		if(keyWord == null) {
			this.keyWord = "";
		} else {
			this.keyWord = keyWord;
		}
	}

	public String getForumId() {
		return forumId;
	}

	public void setForumId(String forumId) {
		if(forumId == null) {
			this.forumId = "";
		} else {
			this.forumId = forumId;
		}
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		if(threadId == null){
			this.threadId = "";
		} else {
			this.threadId = threadId;
		}
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

	public boolean getInfoFlag() {
		return infoFlag;
	}

	public void setInfoFlag(boolean infoFlag) {
		this.infoFlag = infoFlag;
	}

	public AgentFinishedReportWritable(){
	}
	
	@Override
	public void readFields(DataInput out) throws IOException {
		agentId = out.readInt();
		agentAddress = out.readUTF();
		agentType = out.readByte();
		taskId = out.readInt();
		subTaskId = out.readLong();
		subTaskKey = out.readUTF();
		scriptFile = out.readUTF();
		scriptMain = out.readUTF();
		downPageCount = out.readInt();
		scrapeCount = out.readInt();
		siteId = out.readInt();
		errorCode = out.readInt();
		errorMsg = out.readUTF();
		errorUrl = out.readUTF();
		batchId = out.readInt();
		createTime = out.readLong();
		startTime = out.readLong();
		exeTime = out.readInt();
		effectiveTimeRate = out.readInt();
		projectId = out.readInt();
		keyWord = out.readUTF();
		forumId = out.readUTF();
		threadId = out.readUTF();
		useSnapShot = out.readByte();
		turnPageWaitTime = out.readInt();
		infoFlag = out.readBoolean();
	}
	
	@Override
	public void write(DataOutput in) throws IOException {
		in.writeInt(agentId);
		in.writeUTF(agentAddress);
		in.writeByte(agentType);
		in.writeInt(taskId);
		in.writeLong(subTaskId);
		in.writeUTF(subTaskKey);
		in.writeUTF(scriptFile);
		in.writeUTF(scriptMain);
		in.writeInt(downPageCount);
		in.writeInt(scrapeCount);
		in.writeInt(siteId);
		in.writeInt(errorCode);
		in.writeUTF(errorMsg);
		in.writeUTF(errorUrl);
		in.writeInt(batchId);
		in.writeLong(createTime);
		in.writeLong(startTime);
		in.writeInt(exeTime);
		in.writeInt(effectiveTimeRate);
		in.writeInt(projectId);
		in.writeUTF(keyWord);
		in.writeUTF(forumId);
		in.writeUTF(threadId);
		in.writeByte(useSnapShot);
		in.writeInt(turnPageWaitTime);
		in.writeBoolean(infoFlag);
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("AgentFinishedReport: [agentId: " + agentId + ",agentAddress: " + agentAddress + ",agentType: " + agentType + ",taskId: " + taskId + ",subTaskId: " + subTaskId + ",subTaskKey: " + subTaskKey + ",scriptFile: " + scriptFile + ",scriptMain: " + scriptMain + ",downPageCount: " + downPageCount + ",scrapeCount: " + scrapeCount + ",siteId: " + siteId  + ",errorCode: " + errorCode + ",errorMsg: " + errorMsg + ",errorUrl: " + errorUrl + ",batchId: " + batchId + ",createTime: " + createTime + ",startTime: " + startTime + ",exeTime: " + exeTime + ",effectiveTimeRate: " + effectiveTimeRate +  ",projectId: " + projectId + ",keyWord: " + keyWord + ",forumId: " + forumId + ",threadId: " + threadId + ",useSpanShot: " + useSnapShot + ",turnPageWaitTime: " + turnPageWaitTime + ",infoFlag: " + infoFlag + "]");  
        return strBuf.toString(); 
	}
}

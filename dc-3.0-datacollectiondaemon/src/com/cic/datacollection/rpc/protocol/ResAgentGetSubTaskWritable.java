package com.cic.datacollection.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * Agent获取任务响应协议：Control Server 给Agent返回的子任务内容
 * @author johnney.bu
 *
 */
public class ResAgentGetSubTaskWritable implements Writable {
	
	public int code;
	public String errorMessage;
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
	
	public ResAgentGetSubTaskWritable(){
	}
	
	public ResAgentGetSubTaskWritable(int code, String errorMessage, byte agentType, int taskId, long subTaskId, String subTaskKey, String scriptFile, String scriptMain, int siteId, int batchId, byte useSnapShot, int turnPageWaitTime, long createTime, long startTime, int projectId, String keyWord, String forumId, String threadId){
		this.code = code;
		if(errorMessage == null) {
			this.errorMessage = "";
		} else {
			this.errorMessage = errorMessage;
		}
		this.agentType = agentType;
		this.taskId = taskId;
		this.subTaskId = subTaskId;
		if(subTaskKey == null) {
			this.subTaskKey = "";
		} else {
			this.subTaskKey = subTaskKey;
		}
		if(scriptFile == null){
			this.scriptFile = "";
		} else {
			this.scriptFile = scriptFile;
		}
		if(scriptMain == null) {
			this.scriptMain = "";
		} else {
			this.scriptMain = scriptMain;
		}
		this.siteId = siteId;
		this.batchId = batchId;
		this.useSnapShot = useSnapShot;
		this.turnPageWaitTime = turnPageWaitTime;
		this.createTime = createTime;
		this.startTime = startTime;
		this.projectId = projectId;
		if(keyWord == null) {
			this.keyWord = "";
		} else {
			this.keyWord = keyWord;
		}
		if(forumId == null) {
			this.forumId = "";
		} else {
			this.forumId = forumId;
		}
		if(threadId == null){
			this.threadId = "";
		} else {
			this.threadId = threadId;
		}
	}
	
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
		if(errorMessage == null) {
			this.errorMessage = "";
		} else {
			this.errorMessage = errorMessage;
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
		if(scriptFile == null){
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

	@Override
	public void readFields(DataInput out) throws IOException {
		code = out.readInt();
		errorMessage = out.readUTF();
		agentType = out.readByte();
		taskId = out.readInt();
		subTaskId = out.readLong();
		subTaskKey = out.readUTF();
		scriptFile = out.readUTF();
		scriptMain = out.readUTF();
		siteId = out.readInt();
		batchId = out.readInt();
		useSnapShot = out.readByte();
		turnPageWaitTime = out.readInt();
		createTime = out.readLong();
		startTime = out.readLong();
		projectId = out.readInt();
		keyWord = out.readUTF();
		forumId = out.readUTF();
		threadId = out.readUTF();
	}
	
	@Override
	public void write(DataOutput in) throws IOException {
		in.writeInt(code);
		in.writeUTF(errorMessage);
		in.writeByte(agentType);
		in.writeInt(taskId);
		in.writeLong(subTaskId);
		in.writeUTF(subTaskKey);
		in.writeUTF(scriptFile);
		in.writeUTF(scriptMain);
		in.writeInt(siteId);
		in.writeInt(batchId);
		in.writeByte(useSnapShot);
		in.writeInt(turnPageWaitTime);
		in.writeLong(createTime);
		in.writeLong(startTime);
		in.writeInt(projectId);
		in.writeUTF(keyWord);
		in.writeUTF(forumId);
		in.writeUTF(threadId);
	}

	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("ResGetSubTask: [code: " + code + ",errorMessage: " + errorMessage + ",agentType: " + ",taskId: " + taskId + ",subTaskId: " + subTaskId + ",subTaskKey: " + subTaskKey + ",scriptFile: " + scriptFile + ",scriptMain: " + scriptMain + ",siteId: " + siteId + ",batchId: " + batchId + ",useSnapShot: " + useSnapShot + ",turnPageWaitTime: " + turnPageWaitTime + ",createTime: " + createTime + ",startTime: " + startTime + ",projectId: " + projectId + ",keyWord: " + keyWord + ",forumId: " + forumId + ",threadId: " + threadId + "]");  
        return strBuf.toString(); 
	}
}

package com.cic.datacollection.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class TaskRunnerFinishedReportWritable implements Writable {
	public long subTaskId;
	public int errCode;
	public String errMsg;
	public String errUrl;
	public int downPageCount;
	public int scrapeCount;
	public long startTime;
	public int exeTime;
	public int effectiveTimeRate;
	
	public long getSubTaskId() {
		return subTaskId;
	}

	public void setSubTaskId(long subTaskId) {
		this.subTaskId = subTaskId;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		if(errMsg == null){
			this.errMsg = "";
		} else {
			this.errMsg = errMsg;
		}
	}

	public String getErrUrl() {
		return errUrl;
	}

	public void setErrUrl(String errUrl) {
		if(errUrl == null){
			this.errUrl = "";
		} else {
			this.errUrl = errUrl;	
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

	public TaskRunnerFinishedReportWritable() {
	}
	
	@Override
	public void readFields(DataInput out) throws IOException {
		subTaskId = out.readLong();
		errCode = out.readInt();
		errMsg = out.readUTF();
		errUrl = out.readUTF();
		downPageCount = out.readInt();
		scrapeCount = out.readInt();
		startTime = out.readLong();
		exeTime = out.readInt();
		effectiveTimeRate = out.readInt();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeLong(subTaskId);
		in.writeInt(errCode);
		in.writeUTF(errMsg);
		in.writeUTF(errUrl);
		in.writeInt(downPageCount);
		in.writeInt(scrapeCount);
		in.writeLong(startTime);
		in.writeInt(exeTime);
		in.writeInt(effectiveTimeRate);
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("TaskRunnerFinishedReport: [subTaskId: " + subTaskId + ",errCode: " + errCode + ",errMsg: " + errMsg + 
				",errUrl: " + errUrl + ",downPageCount: " + downPageCount + ",scrapeCount: " + scrapeCount + ",startTime: " + startTime + ",exeTime: " + exeTime + 
				",effectiveTimeRate: " + effectiveTimeRate + "]");  
        return strBuf.toString(); 
	}
}

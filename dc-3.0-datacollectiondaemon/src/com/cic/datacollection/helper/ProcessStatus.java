package com.cic.datacollection.helper;

public class ProcessStatus {
	
	private int status; //1表示正在执行过程中，2表示执行成功完成，3表示执行失败完成
	
	private long updateTime;
	
	public ProcessStatus(int status, long updateTime){
		this.status = status;
		this.updateTime = updateTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	
	

}

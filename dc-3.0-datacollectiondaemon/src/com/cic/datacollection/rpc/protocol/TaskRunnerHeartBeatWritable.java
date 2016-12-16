package com.cic.datacollection.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * TaskRunner心跳协议 
 * @author johnney.bu
 *
 */
public class TaskRunnerHeartBeatWritable implements Writable {
	public long subTaskId;
	
	public long getSubTaskId() {
		return subTaskId;
	}

	public void setSubTaskId(long subTaskId) {
		this.subTaskId = subTaskId;
	}

	public TaskRunnerHeartBeatWritable(long subTaskId) {
		this.subTaskId = subTaskId;
	}
	
	public TaskRunnerHeartBeatWritable() {
	}
	
	@Override
	public void readFields(DataInput out) throws IOException {
		subTaskId = out.readLong();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeLong(subTaskId);
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("TaskRunnerHeartBeat: [subTaskId: " + subTaskId + "]");  
        return strBuf.toString(); 
	}
}

package com.cic.datacollection.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class AgentHeartBeatWritable implements Writable {
	public int agentId;
	
	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public AgentHeartBeatWritable(){
	}
	
	public AgentHeartBeatWritable(int agentId) {
		this.agentId = agentId;
	}
	
	@Override
	public void readFields(DataInput out) throws IOException {
		agentId = out.readInt();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeInt(agentId);
	}

	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("AgentHeartBeat: [agentId: " + agentId + "]");  
        return strBuf.toString(); 
	}
}

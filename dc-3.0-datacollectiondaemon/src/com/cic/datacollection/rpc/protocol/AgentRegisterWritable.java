package com.cic.datacollection.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;

/**
 * Agent想Control Server 注册协议
 * @author johnney.bu
 *
 */
public class AgentRegisterWritable implements Writable{
	
	public String agentAddress;
	public int agentPort;
	
	public AgentRegisterWritable(){
		
	}
		
	public AgentRegisterWritable(String agentAddress, int agentPort){
		if(agentAddress == null) {
			this.agentAddress = "";
		} else {
			this.agentAddress = agentAddress;
		}
		this.agentPort = agentPort;
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

	public int getAgentPort() {
		return agentPort;
	}

	public void setAgentPort(int agentPort) {
		this.agentPort = agentPort;
	}

	@Override
	public void readFields(DataInput out) throws IOException {
		agentAddress = out.readUTF();
		agentPort = out.readInt();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeUTF(agentAddress);
		in.writeInt(agentPort);
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("AgentRegister: [agentAddress: " + agentAddress + ",agentPort: " + agentPort + "]");  
        return strBuf.toString(); 
	}
}

package com.cic.datacollection.mina.protocol;

import java.io.Serializable;

import com.cic.datacollection.mina.MinaRequestProtocol;

public class AgentCheckConsistencyProtocol extends MinaRequestProtocol implements Serializable {
	private static final long serialVersionUID = 964014912347546318L;
	public int agentId;
	
	public int getAgentId() {
		return agentId;
	}
	
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	public AgentCheckConsistencyProtocol(){
	}
	
	public AgentCheckConsistencyProtocol(String protocalName) {
		super.setProtocolName(protocalName);
	}
	
	public AgentCheckConsistencyProtocol(String protocalName, int agentId) {
		super.setProtocolName(protocalName);
		this.agentId = agentId;
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("AgentCheckConsistency: [agentId: " + agentId + "]");  
        return strBuf.toString(); 
	}
}

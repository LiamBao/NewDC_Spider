package com.cic.datacollection.mina.protocol;

import java.io.Serializable;

import com.cic.datacollection.mina.MinaRequestProtocol;

public class AgentHeartBeatProtocol extends MinaRequestProtocol implements Serializable {
	private static final long serialVersionUID = 871548185765877687L;
	public int agentId;
	
	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	
	public AgentHeartBeatProtocol(){
	}
	
	public AgentHeartBeatProtocol(String protocolName){
		super.setProtocolName(protocolName);
	}
	
	public AgentHeartBeatProtocol(String protocolName, int agentId) {
		super.setProtocolName(protocolName);
		this.agentId = agentId;
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("AgentHeartBeat: [agentId: " + agentId + "]");  
        return strBuf.toString(); 
	}
}

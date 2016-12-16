package com.cic.datacollection.mina.protocol;

import java.io.Serializable;

import com.cic.datacollection.mina.MinaRequestProtocol;

public class AgentGetSubTaskProtocol extends MinaRequestProtocol implements Serializable {
	private static final long serialVersionUID = -8382215884270045614L;
	private int agentId;
	
	public int getAgentId() {
		return agentId;
	}
	
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	
	public AgentGetSubTaskProtocol(){
	}
	
	public AgentGetSubTaskProtocol(String protocalName) {
		super.setProtocolName(protocalName);
	}
	
	public AgentGetSubTaskProtocol(String protocalName, int agentId) {
		super.setProtocolName(protocalName);
		this.agentId = agentId;
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("AgentGetSubTask: [agentId: " + agentId + "]");  
        return strBuf.toString(); 
	}
}

package com.cic.datacollection.mina.protocol;

import com.cic.datacollection.mina.MinaRequestProtocol;

public class AgentRegisterProtocol extends MinaRequestProtocol {
	private static final long serialVersionUID = -7178464146781332237L;
	public String agentAddress;
	public int agentPort;
	
	public String getAgentAddress() {
		return agentAddress;
	}

	public void setAgentAddress(String agentAddress) {
		this.agentAddress = agentAddress;
	}

	public int getAgentPort() {
		return agentPort;
	}

	public void setAgentPort(int agentPort) {
		this.agentPort = agentPort;
	}
	
	public AgentRegisterProtocol(){
		
	}
		
	public AgentRegisterProtocol(String agentAddress, int agentPort){
		this.agentAddress = agentAddress;
		this.agentPort = agentPort;
	}
	
	public AgentRegisterProtocol(String protocolName, String agentAddress, int agentPort){
		super.setProtocolName(protocolName);
		this.agentAddress = agentAddress;
		this.agentPort = agentPort;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();  
        sb.append("AgentRegister: [address: " + agentAddress  + ", port: " + agentPort + "]");  
        return sb.toString(); 
	}
}

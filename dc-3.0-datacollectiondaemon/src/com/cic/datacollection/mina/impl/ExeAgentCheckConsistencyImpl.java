package com.cic.datacollection.mina.impl;

import java.io.Serializable;

import com.cic.datacollection.mina.MinaExecuteInterface;
import com.cic.datacollection.mina.protocol.AgentCheckConsistencyProtocol;
import com.cic.datacollection.mina.protocol.ResCheckConsistencyProtocol;
import com.cic.datacollection.anew.MinaServerDaemon;

public class ExeAgentCheckConsistencyImpl implements MinaExecuteInterface {

	@Override
	public Serializable execute(Object message) {
		AgentCheckConsistencyProtocol agentInfo = (AgentCheckConsistencyProtocol)message;
		
		ResCheckConsistencyProtocol feedBack =  null;
		
		MinaServerDaemon server = MinaServerDaemon.getInstance();
		
		feedBack = server.getAgentSubTaskIds(agentInfo.getAgentId());
		
		return feedBack;
	}

}

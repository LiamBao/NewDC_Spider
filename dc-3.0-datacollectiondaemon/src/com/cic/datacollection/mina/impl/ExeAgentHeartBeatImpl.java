package com.cic.datacollection.mina.impl;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.cic.datacollection.anew.MinaServerDaemon;
import com.cic.datacollection.mina.MinaExecuteInterface;
import com.cic.datacollection.mina.protocol.AgentHeartBeatProtocol;
import com.cic.datacollection.mina.protocol.FeedBackProtocol;
import com.cic.datacrawl.core.rpc.CodeStatus;

public class ExeAgentHeartBeatImpl implements MinaExecuteInterface {
	private static final Logger log = Logger.getLogger(ExeAgentHeartBeatImpl.class);
	
	@Override
	public Serializable execute(Object message) {
		AgentHeartBeatProtocol agentHeartBeat = (AgentHeartBeatProtocol)message;
		
		int agentId = agentHeartBeat.getAgentId();
		log.info("Accept HeartBeat from AgentId : " + agentId);
		
		int agentMaxInstanceNum = MinaServerDaemon.getInstance().updateAgentHeartBeatTime(agentId);
		
		String strMsg = "" + agentMaxInstanceNum;
		FeedBackProtocol feedBack = new FeedBackProtocol(CodeStatus.succCode, "", strMsg);
		
		log.info("Set HeartBeat for AgentId: " + agentId + " success");
		
		return feedBack;
	}

}

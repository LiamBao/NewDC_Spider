package com.cic.datacollection.rpc.impl;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacollection.anew.RpcServerDaemon;
import com.cic.datacollection.rpc.protocol.AgentHeartBeatWritable;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;

public class AgentTimerReportExeImpl implements ExecuteInterface {
	private static final Logger log = Logger.getLogger(AgentTimerReportExeImpl.class);

	@Override
	public Writable execute(Writable args) {
		AgentHeartBeatWritable agentHeartBeat = (AgentHeartBeatWritable)args;
		
		int agentId = agentHeartBeat.getAgentId();
		log.info("Accept HeartBeat from AgentId : " + agentId);
		
		String strMsg = RpcServerDaemon.getInstance().updateAgentHeartBeatTime(agentId);
		
		FeedBackWritable feedBack = new FeedBackWritable(CodeStatus.succCode, "", strMsg);
		
		log.info("Set HeartBeat for AgentId: " + agentId + " success");
		
		return feedBack;
	}

}

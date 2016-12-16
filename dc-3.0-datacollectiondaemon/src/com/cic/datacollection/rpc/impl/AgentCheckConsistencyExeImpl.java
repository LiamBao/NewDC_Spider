package com.cic.datacollection.rpc.impl;

import org.apache.hadoop.io.Writable;

import com.cic.datacollection.anew.RpcServerDaemon;
import com.cic.datacollection.rpc.protocol.AgentCheckConsistencyWritable;
import com.cic.datacollection.rpc.protocol.ResAgentCheckConsistencyWritable;
import com.cic.datacrawl.core.rpc.ExecuteInterface;

public class AgentCheckConsistencyExeImpl implements ExecuteInterface {

	@Override
	public Writable execute(Writable args) {
		AgentCheckConsistencyWritable agentInfo = (AgentCheckConsistencyWritable)args;
		
		ResAgentCheckConsistencyWritable feedBack =  null;
		
		RpcServerDaemon server = RpcServerDaemon.getInstance();
		
		feedBack = server.getAgentSubTaskIds(agentInfo.getAgentId());
		
		return feedBack;
	}

}

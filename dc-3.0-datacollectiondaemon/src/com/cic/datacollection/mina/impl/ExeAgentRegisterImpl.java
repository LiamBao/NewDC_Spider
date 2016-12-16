package com.cic.datacollection.mina.impl;

import java.io.Serializable;

import com.cic.datacollection.anew.MinaServerDaemon;
import com.cic.datacollection.mina.MinaExecuteInterface;
import com.cic.datacollection.mina.protocol.AgentRegisterProtocol;
import com.cic.datacollection.mina.protocol.FeedBackProtocol;
import com.cic.datacrawl.core.rpc.CodeStatus;

public class ExeAgentRegisterImpl implements MinaExecuteInterface {

	@Override
	public Serializable execute(Object message) {
		//get the arguments
		AgentRegisterProtocol registerInfo = (AgentRegisterProtocol)message;
		String address = registerInfo.agentAddress;
		int port = registerInfo.agentPort;

		int agentId = 0;
		int maxInstance = 0;
		int code =-1;
		String errorMessage="";
		String strAgentInfo="";
		
		//register the agent
		agentId = MinaServerDaemon.getInstance().registerAgent(address, port);
		if (agentId > 0){
			code =CodeStatus.succCode;
			maxInstance = MinaServerDaemon.getInstance().getAgentMaxInstance(agentId);
			strAgentInfo = agentId + "_" + maxInstance;
		}else{
			code = CodeStatus.failCode;
			errorMessage="注册的过程中出错";
		}
		
		FeedBackProtocol feedBack = new FeedBackProtocol(code, errorMessage, strAgentInfo);
		
		return feedBack;
	}

}

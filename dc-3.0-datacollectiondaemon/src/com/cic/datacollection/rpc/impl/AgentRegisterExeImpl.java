package com.cic.datacollection.rpc.impl;

import org.apache.hadoop.io.Writable;

import com.cic.datacollection.anew.RpcServerDaemon;
import com.cic.datacollection.rpc.protocol.AgentRegisterWritable;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;

public class AgentRegisterExeImpl implements ExecuteInterface{

	@Override
	public Writable execute(Writable args) {
		//get the arguments
		AgentRegisterWritable registerInfo = (AgentRegisterWritable)args;
		String address = registerInfo.agentAddress;
		int port = registerInfo.agentPort;

		int agentId = 0;
		String instanceNumPageSnapShot = "2_0_0";  //maxInstance_useSnapshot_agentSubgroupId
		int code =-1;
		String errorMessage="";
		String strAgentInfo="";
		
		//register the agent
		agentId = RpcServerDaemon.getInstance().registerAgent(address, port);
		if (agentId > 0){
			code =CodeStatus.succCode;
			instanceNumPageSnapShot = RpcServerDaemon.getInstance().getAgentMaxInstance(agentId);
			strAgentInfo = agentId + "_" + instanceNumPageSnapShot;
		}else{
			code = CodeStatus.failCode;
			errorMessage="注册的过程中出错";
		}
		
		FeedBackWritable feedBack = new FeedBackWritable(code,errorMessage, strAgentInfo);
		return feedBack;
	}

}

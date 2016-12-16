package com.cic.datacollection.mina.impl;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.cic.datacollection.anew.MinaServerDaemon;
import com.cic.datacollection.bean.SubTaskInfo;
import com.cic.datacollection.mina.MinaExecuteInterface;
import com.cic.datacollection.mina.protocol.AgentGetSubTaskProtocol;
import com.cic.datacollection.mina.protocol.ResGetSubTaskProtocol;
import com.cic.datacrawl.core.rpc.CodeStatus;

public class ExeAgentGetSubTaskImpl implements MinaExecuteInterface {
	private static final Logger log = Logger.getLogger(ExeAgentGetSubTaskImpl.class);
	
	@Override
	public Serializable execute(Object message) {
		AgentGetSubTaskProtocol getTaskInfo = (AgentGetSubTaskProtocol)message;
		int agentId = getTaskInfo.getAgentId();
		
		log.info("Accept a get task request from agentId: " + agentId);
		
		int code =-1;
		String errorMessage="";
		
		MinaServerDaemon server = MinaServerDaemon.getInstance();
		SubTaskInfo subTaskInfo = null;
		
		log.info("Get subtaskInfo for agentId: " + agentId);
		subTaskInfo = server.allocateSubTaskForAgent(agentId);
		if(subTaskInfo != null) {
			code = CodeStatus.succCode;
		} else {
			code = CodeStatus.failCode;
			errorMessage = "There is no available task for AgentId: " + agentId;
			log.error(errorMessage);
		}

		
		ResGetSubTaskProtocol subTaskPackage = new ResGetSubTaskProtocol();
		if(code == CodeStatus.succCode) {
			subTaskPackage.setAgentType(subTaskInfo.getAgentType());
			subTaskPackage.setTaskId(subTaskInfo.getTaskId());
			subTaskPackage.setSubTaskId(subTaskInfo.getSubTaskId());
			subTaskPackage.setSubTaskKey(subTaskInfo.getSubTaskKey());
			subTaskPackage.setScriptFile(subTaskInfo.getScriptFile());
			subTaskPackage.setScriptMain(subTaskInfo.getScriptMain());
			subTaskPackage.setSiteId(subTaskInfo.getSiteId());
			subTaskPackage.setBatchId(subTaskInfo.getBatchId());
			subTaskPackage.setCreateTime(subTaskInfo.getCreateTime());
			subTaskPackage.setStartTime(System.currentTimeMillis());
			subTaskPackage.setProjectId(subTaskInfo.getProjectId());
			subTaskPackage.setKeyWord(subTaskInfo.getKeyWord());
			subTaskPackage.setForumId(subTaskInfo.getForumId());
			subTaskPackage.setThreadId(subTaskInfo.getThreadId());
		} else {
			subTaskPackage.setAgentType((byte)0);
			subTaskPackage.setTaskId(0);
			subTaskPackage.setSubTaskId(0);
			subTaskPackage.setSubTaskKey("");
			subTaskPackage.setScriptFile("");
			subTaskPackage.setScriptMain("");
			subTaskPackage.setAgentGroupId(0);
			subTaskPackage.setSiteId(0);
			subTaskPackage.setBatchId(0);
			subTaskPackage.setCreateTime(0);
			subTaskPackage.setStartTime(System.currentTimeMillis());
			subTaskPackage.setProjectId(0);
			subTaskPackage.setKeyWord("");
			subTaskPackage.setForumId("");
			subTaskPackage.setThreadId("");
		}
		
		subTaskPackage.setCode(code);
		subTaskPackage.setErrorMessage(errorMessage);			
		
		log.info("Send subTaskId: " + subTaskPackage.getSubTaskId());

		return subTaskPackage;		
	}

}

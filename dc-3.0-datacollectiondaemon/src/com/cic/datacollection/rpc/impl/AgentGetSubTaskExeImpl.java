package com.cic.datacollection.rpc.impl;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacollection.anew.RpcServerDaemon;
import com.cic.datacollection.bean.SubTaskInfo;
import com.cic.datacollection.rpc.protocol.AgentGetSubTaskWritable;
import com.cic.datacollection.rpc.protocol.ResAgentGetSubTaskWritable;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
/**
 * 处理Agent获取任务的请求
 * @author johnney.bu
 *
 */
public class AgentGetSubTaskExeImpl implements ExecuteInterface {
	private static final Logger log = Logger.getLogger(AgentGetSubTaskExeImpl.class);

	@Override
	public Writable execute(Writable args){
		AgentGetSubTaskWritable getTaskInfo = (AgentGetSubTaskWritable)args;
		int agentId = getTaskInfo.getAgentId();
		
		log.info("Accept a get task request from agentId: " + agentId);
		
		int code =-1;
		String errorMessage="";
		
		RpcServerDaemon server = RpcServerDaemon.getInstance();
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

		
		ResAgentGetSubTaskWritable subTaskWritable = new ResAgentGetSubTaskWritable();
		if(code == CodeStatus.succCode) {
			subTaskWritable.setAgentType(subTaskInfo.getAgentType());
			subTaskWritable.setTaskId(subTaskInfo.getTaskId());
			subTaskWritable.setSubTaskId(subTaskInfo.getSubTaskId());
			subTaskWritable.setSubTaskKey(subTaskInfo.getSubTaskKey());
			subTaskWritable.setScriptFile(subTaskInfo.getScriptFile());
			subTaskWritable.setScriptMain(subTaskInfo.getScriptMain());
			subTaskWritable.setSiteId(subTaskInfo.getSiteId());
			subTaskWritable.setBatchId(subTaskInfo.getBatchId());
			subTaskWritable.setUseSnapShot(subTaskInfo.getUseSnapShot());
			subTaskWritable.setTurnPageWaitTime(subTaskInfo.getTurnPageWaitTime());
			subTaskWritable.setCreateTime(subTaskInfo.getCreateTime());
			subTaskWritable.setStartTime(System.currentTimeMillis());
			subTaskWritable.setProjectId(subTaskInfo.getProjectId());
			subTaskWritable.setKeyWord(subTaskInfo.getKeyWord());
			subTaskWritable.setForumId(subTaskInfo.getForumId());
			subTaskWritable.setThreadId(subTaskInfo.getThreadId());
		} else {
			subTaskWritable.setAgentType((byte)0);
			subTaskWritable.setTaskId(0);
			subTaskWritable.setSubTaskId(0);
			subTaskWritable.setSubTaskKey("");
			subTaskWritable.setScriptFile("");
			subTaskWritable.setScriptMain("");
			subTaskWritable.setSiteId(0);
			subTaskWritable.setBatchId(0);
			subTaskWritable.setUseSnapShot((byte) 0);
			subTaskWritable.setTurnPageWaitTime(0);
			subTaskWritable.setCreateTime(0);
			subTaskWritable.setStartTime(System.currentTimeMillis());
			subTaskWritable.setProjectId(0);
			subTaskWritable.setKeyWord("");
			subTaskWritable.setForumId("");
			subTaskWritable.setThreadId("");
		}
		
		subTaskWritable.setCode(code);
		subTaskWritable.setErrorMessage(errorMessage);			
		
		log.info("Send subTaskId: " + subTaskWritable.getSubTaskId());

		return subTaskWritable;		
	}

}

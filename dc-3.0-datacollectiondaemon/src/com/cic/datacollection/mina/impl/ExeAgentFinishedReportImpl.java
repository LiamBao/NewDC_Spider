package com.cic.datacollection.mina.impl;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.cic.datacollection.anew.MinaServerDaemon;
import com.cic.datacollection.mina.MinaExecuteInterface;
import com.cic.datacollection.mina.protocol.AgentFinishedReportProtocol;
import com.cic.datacollection.mina.protocol.FeedBackProtocol;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.management.manager.SubTaskManager;

public class ExeAgentFinishedReportImpl implements MinaExecuteInterface {
	private static final Logger log = Logger.getLogger(ExeAgentFinishedReportImpl.class);

	@Override
	public Serializable execute(Object message) {
		int code = CodeStatus.succCode;
		String errMsg = "";
		
		MinaServerDaemon server = MinaServerDaemon.getInstance();
		AgentFinishedReportProtocol subTaskStatus = (AgentFinishedReportProtocol)message;
		log.info("Accept AgentFinishedReportExeImpl, subTaskId: " + subTaskStatus.getSubTaskId());
		try {
			//如果不存在，表示该任务已经做完成处理
			if(subTaskStatus.getErrorCode() == SubTaskManager.TASKRUNNER_STATUS_CODE_NOT_EXIST) {
				if(!server.subTaskIsExist(subTaskStatus.getAgentId(), subTaskStatus.getSubTaskId())) {
					return new FeedBackProtocol(code, errMsg, "");
				}
			}
			code = server.processSubTaskFinishReport(subTaskStatus);
		} catch(Exception e){
			errMsg = "addSubTaskInfoToFQ failed for subTaskId: " + subTaskStatus.getSubTaskId();
			log.error(errMsg);
			log.error(subTaskStatus.toString());
			e.printStackTrace();
		}
		
		return new FeedBackProtocol(code, errMsg, "");
	}

}

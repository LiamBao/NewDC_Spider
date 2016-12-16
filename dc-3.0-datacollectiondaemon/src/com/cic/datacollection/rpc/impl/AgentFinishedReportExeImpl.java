package com.cic.datacollection.rpc.impl;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacollection.anew.RpcServerDaemon;
import com.cic.datacollection.rpc.protocol.AgentFinishedReportWritable;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;
import com.cic.datacrawl.management.manager.SubTaskManager;

/**
 * 处理Agent上报的子任务执行结果
 * @author johnney.bu
 *
 */
public class AgentFinishedReportExeImpl  implements ExecuteInterface{
	private static final Logger log = Logger.getLogger(AgentFinishedReportExeImpl.class);

	@Override
	public Writable execute(Writable args) {
		int code = CodeStatus.succCode;
		String errMsg = "";
		
		RpcServerDaemon server = RpcServerDaemon.getInstance();
		AgentFinishedReportWritable subTaskStatus = (AgentFinishedReportWritable)args;
		log.info("Accept AgentFinishedReportExeImpl, subTaskId: " + subTaskStatus.getSubTaskId());
		try {
			//如果不存在，表示该任务已经做完成处理
			if(subTaskStatus.getErrorCode() == SubTaskManager.TASKRUNNER_STATUS_CODE_NOT_EXIST) {
				if(!server.subTaskIsExist(subTaskStatus.getAgentId(), subTaskStatus.getSubTaskId())) {
					return new FeedBackWritable(code, errMsg, "");
				}
			}
			code = server.processSubTaskFinishReport(subTaskStatus);
		} catch(Exception e){
			errMsg = "addSubTaskInfoToFQ failed for subTaskId: " + subTaskStatus.getSubTaskId();
			log.error(errMsg);
			log.error(subTaskStatus.toString());
			e.printStackTrace();
		}
		
		return new FeedBackWritable(code, errMsg, "");
	}

}

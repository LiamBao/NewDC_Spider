package com.cic.datacollection.rpc.impl;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacollection.anew.RpcAgentDaemon;
import com.cic.datacollection.rpc.protocol.TaskRunnerFinishedReportWritable;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;

/**
 * 处理TaskRunner上报的任务执行结果
 * @author johnney.bu
 *
 */
public class TaskRunnerFinishedReportExeImpl implements ExecuteInterface {
	private static final Logger log = Logger.getLogger(TaskRunnerFinishedReportExeImpl.class);

	@Override
	public Writable execute(Writable args) {
		TaskRunnerFinishedReportWritable subTaskStatus = (TaskRunnerFinishedReportWritable)args;
		RpcAgentDaemon agent = RpcAgentDaemon.getInstance();

		int code = CodeStatus.succCode;
		String errMsg = "";
		
		log.info("TaskRunner finished for subTaskId: " + subTaskStatus.getSubTaskId());
		
		if(!agent.reportSubTaskStatus(subTaskStatus.getSubTaskId(), subTaskStatus.getErrCode(),
				subTaskStatus.getErrMsg(), subTaskStatus.getErrUrl(), subTaskStatus.getDownPageCount(), subTaskStatus.getScrapeCount(),
				subTaskStatus.getStartTime(),subTaskStatus.getExeTime(), subTaskStatus.getEffectiveTimeRate())) {
			code = CodeStatus.failCode;
			errMsg = "Send FinishReport to ControlServer for SubTaskId " + subTaskStatus.getSubTaskId() + " failed !";
			log.error(errMsg);
			log.error(subTaskStatus.toString());
		} else {
			// agent.delTaskRunnerInfo(subTaskStatus.getSubTaskId());
		}
		agent.delTaskRunnerInfo(subTaskStatus.getSubTaskId());
		log.info("End invoke finished report for subTaskId: " + subTaskStatus.getSubTaskId());
		
		return new FeedBackWritable(code, errMsg, "");
	}
}

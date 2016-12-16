package com.cic.datacollection.rpc.impl;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacollection.anew.RpcAgentDaemon;
import com.cic.datacollection.rpc.protocol.TaskRunnerHeartBeatWritable;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;

/**
 * 处理TaskRunner上报的心跳
 * @author johnney.bu
 *
 */
public class TaskRunnerTimerReportExeImpl implements ExecuteInterface {

	private static final Logger log = Logger.getLogger(TaskRunnerTimerReportExeImpl.class);
	
	@Override
	public Writable execute(Writable args) {
		int code = CodeStatus.succCode;
		
		TaskRunnerHeartBeatWritable taskStatus = (TaskRunnerHeartBeatWritable)args;
		long subTaskId = taskStatus.getSubTaskId();
		log.info("TaskRunner HeartBeat subTaskId:" + subTaskId);
		RpcAgentDaemon.getInstance().updateTaskRunnerHeartBeat(subTaskId);

		log.info("end invoke TaskRunnerTimerReportExeImpl for subtaskId:" + subTaskId);
		return new FeedBackWritable(code,"", "");
	}

}

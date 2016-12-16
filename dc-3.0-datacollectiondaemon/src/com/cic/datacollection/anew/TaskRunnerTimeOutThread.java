package com.cic.datacollection.anew;

import java.util.List;

import org.apache.log4j.Logger;

import com.cic.datacollection.bean.TaskRunnerInfo;
import com.cic.datacrawl.management.manager.SubTaskManager;

/**
 * TaskRunner超时检查线程
 * @author johnney.bu
 *
 */
public class TaskRunnerTimeOutThread extends Thread {
	private static final Logger log = Logger.getLogger(TaskRunnerTimeOutThread.class);
	
	private int timeOutWait;
	
	TaskRunnerTimeOutThread(int taskRunnerTimeOut) {
		this.timeOutWait = taskRunnerTimeOut;
	}
	
	@Override
	public void run(){
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(true) {
			try {
				Thread.sleep(timeOutWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			log.info("Check TaskRunner TimeOut...");
			long curTime = System.currentTimeMillis();
			RpcAgentDaemon agent = RpcAgentDaemon.getInstance();
			List<TaskRunnerInfo> timeOutTaskRunnerList = agent.CheckTaskRunnerTimeOut(curTime);
			log.info("TimeOut TaskRunner Num: " + timeOutTaskRunnerList.size());
			if(!timeOutTaskRunnerList.isEmpty()) {
				for(TaskRunnerInfo taskRunnerInfo : timeOutTaskRunnerList) {
					//暂时定TaskRunner知道的错误码1标识执行超时
					int exeTime = (int)(System.currentTimeMillis()-taskRunnerInfo.getStartTime());
					if(!agent.reportSubTaskStatus(taskRunnerInfo.getSubTaskId(), SubTaskManager.TASKRUNNER_STATUS_CODE_TIME_OUT, "TimeOut: Executed more than " + timeOutWait + "ms", "", 0, 0, taskRunnerInfo.getStartTime(), exeTime, 0)) {
						log.error("Send TimeOut-TaskRunner for subTaskId: " + taskRunnerInfo.getSubTaskId() + " failed !");
					}
				}
			}
		}
	}

}

package com.cic.datacrawl.runner;

import org.apache.log4j.Logger;

public class TaskRunnerHeartBeatReportThread implements Runnable {

	private static final Logger LOG = Logger.getLogger(TaskRunnerHeartBeatReportThread.class);
	
	private int heartBeatWaitTime;

	public TaskRunnerHeartBeatReportThread(int waitTime) {
		this.heartBeatWaitTime = waitTime;
	}

	private boolean isRunning = true;

	public void stop() {
		isRunning = false;
	}

	@Override
	public void run() {
		TaskRunner taskRunner = TaskRunner.getInstance();
		
		while (isRunning) {
			while(!taskRunner.sendHeartBeat()) {
				LOG.error("Send HeartBeat to Agent failed, try again...");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			LOG.info("Send HeartBeat success");
			
			try {
				LOG.info("sleep " + heartBeatWaitTime);
				Thread.sleep(heartBeatWaitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

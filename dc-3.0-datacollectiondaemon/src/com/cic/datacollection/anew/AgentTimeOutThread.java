package com.cic.datacollection.anew;

import org.apache.log4j.Logger;

import com.cic.datacollection.lock.DistributedMutexLock;

/**
 * Agent超时检查线程
 * @author johnney.bu
 *
 */
public class AgentTimeOutThread extends Thread{
	private static final Logger log = Logger.getLogger(AgentTimeOutThread.class);
	
	private int waitTime;
	
	public AgentTimeOutThread(int waitTime) {
		this.waitTime = waitTime;
	}
	
	private boolean getTimeOutCheckLock() {
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		return mutexLock.lockAgentTimeOutCheck(waitTime);
	}
	
	private void delTimeOutCheckLock() {
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		mutexLock.unlockAgentTimeOutCheck();
	}
	
	
	@Override
	public void run() {
		while(true) {
			
			try {
				log.info("Agent TimeOut Checker sleep " + waitTime + " ms...");
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(getTimeOutCheckLock()) {
				int num = RpcServerDaemon.getInstance().processAgentTimeOut();
				log.error("TimeOut Agent Num: " + num);
				delTimeOutCheckLock();
			}
		}
	}
}

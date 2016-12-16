package com.cic.datacollection.anew;

public class AgentHeartBeatReportThread extends Thread {
	private int heartBeatTime;


	public int getHeartBeatTime() {
		return heartBeatTime;
	}

	public void setHeartBeatTime(int heartBeatTime) {
		this.heartBeatTime = heartBeatTime;
	}

	public AgentHeartBeatReportThread(int heartBeatTime) {
		this.heartBeatTime = heartBeatTime;
	}

	@Override
	public void run() {
		while(true) {
			RpcAgentDaemon.getInstance().sendHeartBeat();
			
			try {
				Thread.sleep(heartBeatTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

package com.cic.datacrawl.core.system;

public class TaskRunnerConf {
	private String snapShotIp;
	private int snapShotPort;
	private String loginAccountIp;
	private int loginAccountPort;
	
	public String getSnapShotIp() {
		return snapShotIp;
	}
	public void setSnapShotIp(String snapShotIp) {
		this.snapShotIp = snapShotIp;
	}
	public int getSnapShotPort() {
		return snapShotPort;
	}
	public void setSnapShotPort(int snapShotPort) {
		this.snapShotPort = snapShotPort;
	}
	public String getLoginAccountIp() {
		return loginAccountIp;
	}
	public void setLoginAccountIp(String loginAccountIp) {
		this.loginAccountIp = loginAccountIp;
	}
	public int getLoginAccountPort() {
		return loginAccountPort;
	}
	public void setLoginAccountPort(int loginAccountPort) {
		this.loginAccountPort = loginAccountPort;
	}
}

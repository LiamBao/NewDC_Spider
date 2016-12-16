package com.cic.datacrawl.core.rpc;

public enum ServiceName {

	AGENT_REGISTER("agentRegister"),
	AGENT_REQUEST_TASK("agentRequestTask"),
	AGENT_GET_SUBTASKIDS("agentGetSubTaskIds"),
	AGENT_TASK_FINISH_REPORT("agentTaskFinishedReport"),
	AGENT_HEART_BEAT("agentHeartBeat"),
	TASK_RUNNER_HEART_BEAT("taskRunnerTimerReport"),
	TASK_RUNNER_FINISH_REPORT("taskRunnerFinishedReport"),
	TASK_RUNNER_GET_SNAPSHOT("taskRunnerGetSnapShot"),
	TASK_RUNNER_REQUEST_ACCOUNT("taskRunnerRequestAccount"),
	TASK_RUNNER_RETURN_ACCOUNT("taskRunnerReturnAccount"),
	TASK_RUNNER_CHANGE_ACCOUNT("taskRunnerChangeAccount"),
	TASK_RUNNER_UPDATE_ACCOUNT("taskRunnerUpdateAccount"),
	TASK_RUNNER_REPORT_RECORD("taskRunnerReportRecord"),
	AGENT_TRANSFER_RECORD("agentTransferRecord");
	
	private String name;

	ServiceName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}

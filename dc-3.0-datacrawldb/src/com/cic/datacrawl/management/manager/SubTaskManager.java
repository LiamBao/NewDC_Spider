package com.cic.datacrawl.management.manager;

public class SubTaskManager{
	
	public static final int SCRIPT_STATUS_CODE_FINISHED = 0;
	public static final int SCRIPT_STATUS_CODE_ERROR_TIMEOUT = 801;
	public static final int SCRIPT_STATUS_CODE_ERROR_ARGUMRNT = 802;
	public static final int SCRIPT_STATUS_CODE_ERROR_DEAD_CYCLE_SCRIPT = 803;
	public static final int SCRIPT_STATUS_CODE_ERROR_SAVE = 804;
	public static final int SCRIPT_STATUS_CODE_ERROR_UNKNOW = 800;
	public static final int SCRIPT_STATUS_CODE_TIME_OUT = 999;  //TaskRunner TimeOut
	public static final int TASKRUNNER_STATUS_CODE_TIME_OUT = 700;
	public static final int AGENT_STATUS_CODE_TIME_OUT = 701;
	public static final int TASKRUNNER_STATUS_CODE_NOT_EXIST = 702;
	
	// 等待中
	public static final byte STATUS_PADDING = 0;
	// 运行中
	public static final byte STATUS_RUNNING = 1;
	// 完成
	public static final byte STATUS_FINISHED = 2;
	// 运行时发生错误
	public static final byte STATUS_ERROR = 3;
	// 手动停止
	public static final byte STATUS_MANUAL_STOP = 4;

	public static final byte FLAG_SEND_ERROR_UNDEFINE = 0;
	public static final byte FLAG_SEND_ERROR_NOT_SEND = 1;
	public static final byte FLAG_SEND_ERROR_SEND = 2;

	private byte status = STATUS_FINISHED;

	/**
	 * @return the status
	 */
	
	public void setStatus(byte status) {
		this.status = status;
	}
	
	public byte getStatus() {
		return status;
	}

}

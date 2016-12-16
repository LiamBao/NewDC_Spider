package com.cic.datacrawl.core;


public class ProcessMonitor {
	private static int scriptStatus;
	private static long changeScriptStatusTime;
	private static String scriptErrorMessage;
	private static String scriptErrorURL;
	

	public static String getScriptErrorURL() {
		return scriptErrorURL;
	}

	public static void setScriptErrorURL(String scriptErrorURL) {
		ProcessMonitor.scriptErrorURL = scriptErrorURL;
	}

	public static String getScriptErrorMessage() {
		return scriptErrorMessage;
	}

	public static void setScriptErrorMessage(String scriptErrorMessage) {
		ProcessMonitor.scriptErrorMessage = scriptErrorMessage;
	}

	public static long getChangeScriptStatusTime() {
		return changeScriptStatusTime;
	}

	public static void setChangeScriptStatusTime(long changeScriptStatusTime) {
		ProcessMonitor.changeScriptStatusTime = changeScriptStatusTime;
	}

	public static int getScriptStatus() {
		return scriptStatus;
	}

	public static void setScriptStatus(int scriptStatus) {
		ProcessMonitor.scriptStatus = scriptStatus;
	}
	
	public static void failed(int errorCode, String errorMessage){
		setScriptStatus(errorCode);
		setScriptErrorMessage(errorMessage);
		setChangeScriptStatusTime(System.currentTimeMillis());
	}
	
	public static void failed(int errorCode, String errorMessage, String errUrl){
		setScriptStatus(errorCode);
		setScriptErrorMessage(errorMessage);
		setScriptErrorURL(errUrl);
		setChangeScriptStatusTime(System.currentTimeMillis());
	}
	
	public static void finished(){
		failed(0, null);
	}
}

package com.cic.datacrawl.core.rhino.debugger;

public interface ScriptExecuter {
	public String getExecuterName();
	public String getScriptName();
	public void enter();
	public void exit();
	public void stop();
	public void waitForExecute();
}

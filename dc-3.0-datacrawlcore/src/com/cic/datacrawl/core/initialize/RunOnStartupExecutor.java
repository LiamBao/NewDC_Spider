package com.cic.datacrawl.core.initialize;

public interface RunOnStartupExecutor {
	public void execute(String scriptpath) throws Exception;
}

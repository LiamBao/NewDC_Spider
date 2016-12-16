package com.cic.datacrawl.core.system;

public interface SystemInterface {
	public static final String MODE_EDIT = "Script Editor";
	public static final String MODE_EXECUTE = "Task Runner";

	void exit(int exitCode);

	String getMode();
}

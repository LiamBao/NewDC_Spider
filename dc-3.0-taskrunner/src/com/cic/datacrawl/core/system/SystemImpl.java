package com.cic.datacrawl.core.system;

public class SystemImpl implements SystemInterface {

	@Override
	public void exit(int exitCode) {
		System.exit(0);
	}

	@Override
	public String getMode() {
		return MODE_EXECUTE;
	}

}

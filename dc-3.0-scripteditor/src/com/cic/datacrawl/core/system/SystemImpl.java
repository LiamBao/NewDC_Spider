package com.cic.datacrawl.core.system;

import com.cic.datacrawl.ui.SwingGui;

public class SystemImpl implements SystemInterface{

	@Override
	public void exit(int exitCode) {
		SwingGui.getInstance().stopCurrentThread();
	}

	@Override
	public String getMode() {
		return MODE_EDIT;
	}

}

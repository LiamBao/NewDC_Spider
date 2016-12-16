package com.cic.datacrawl.core.initialize;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class RunOnStartup {
	private static final Logger logger = Logger.getLogger(RunOnStartup.class);
	private List<String> runOnStartupScriptPathList = new ArrayList<String>();

	/**
	 * @param runOnStartupScriptPathList
	 *            the runOnStartupScriptPathList to set
	 */
	public void setRunOnStartupScriptPathList(
			List<String> runOnStartupScriptPathList) {
		this.runOnStartupScriptPathList = runOnStartupScriptPathList;
	}

	public void execute(final RunOnStartupExecutor executor) {
		if (executor == null)
			throw new NullPointerException();
		if (runOnStartupScriptPathList != null) {
			for (int i = 0; i < runOnStartupScriptPathList.size(); ++i) {
				try {
					executor.execute(runOnStartupScriptPathList.get(i));
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

}

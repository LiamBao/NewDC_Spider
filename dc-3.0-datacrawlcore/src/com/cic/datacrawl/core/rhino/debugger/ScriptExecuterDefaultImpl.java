package com.cic.datacrawl.core.rhino.debugger;

import org.mozilla.javascript.Context;

public class ScriptExecuterDefaultImpl implements ScriptExecuter {
	private String scriptName;

	/**
	 * @return the scriptName
	 */
	public String getScriptName() {
		return scriptName;
	}

	/**
	 * @param scriptName
	 *            the scriptName to set
	 */
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	@Override
	public void enter() {
		ScriptExecuterManager.getInstance().changeToStartStatus(this);
	}

	@Override
	public void exit() {
		ScriptExecuterManager.getInstance().changeToFinishedStatus(this);
	}

	private String name;

	@Override
	public String getExecuterName() {
		if (name == null)
			name = ScriptExecuterManager.getInstance().buildExecuterName(this);

		return name;
	}

	@Override
	public void waitForExecute() {
		while (!ScriptExecuterManager.getInstance().canExecute()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void stop() {
		Context.exit();
	}
}

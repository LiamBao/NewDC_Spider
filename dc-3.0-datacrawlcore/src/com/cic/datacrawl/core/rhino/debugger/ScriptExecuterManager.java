package com.cic.datacrawl.core.rhino.debugger;

import java.util.HashMap;
import java.util.Iterator;

public class ScriptExecuterManager {
	private static ScriptExecuterManager manager = new ScriptExecuterManager();

	public static ScriptExecuterManager getInstance() {
		return manager;
	}

	private int executeThreadNumber = 1;

	private HashMap<String, ScriptExecuter> currentExecutorMap = new HashMap<String, ScriptExecuter>();

	private long proxyNameIndex = 0;

	public synchronized String buildExecuterName(ScriptExecuter scriptExecuter) {
		return scriptExecuter.getScriptName() + "_" + (++proxyNameIndex);
	}

	public boolean canExecute() {
		if (currentExecutorMap.size() < executeThreadNumber) {
			return true;
		}
		return false;
	}

	public ScriptExecuter[] getCurrentExecuters() {
		ScriptExecuter[] ret = null;
		synchronized (currentExecutorMap) {
			ret = new ScriptExecuter[currentExecutorMap.size()];
			Iterator<String> keyIterator = currentExecutorMap.keySet()
					.iterator();
			int i = 0;
			while (keyIterator.hasNext()) {
				ret[i++] = currentExecutorMap.get(keyIterator.next());
			}
		}
		return ret;
	}

	public ScriptExecuter getCurrentExecuter() {
		ScriptExecuter[] scriptExecuters = getCurrentExecuters();
		if (scriptExecuters.length == 0)
			return null;

		return scriptExecuters[0];
	}

	public void changeToStartStatus(ScriptExecuter executer) {
		synchronized (currentExecutorMap) {
			if (!currentExecutorMap.containsKey(executer.getExecuterName())) {
				currentExecutorMap.put(executer.getExecuterName(), executer);
			}
		}
	}

	public void changeToFinishedStatus(ScriptExecuter executer) {
		synchronized (currentExecutorMap) {
			if (currentExecutorMap.containsKey(executer.getExecuterName())) {
				currentExecutorMap.remove(executer.getExecuterName());
			}
		}
	}

}

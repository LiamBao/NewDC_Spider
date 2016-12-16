package com.cic.datacrawl.core.config;

import java.util.ArrayList;

public class AutoSaveConfiguration implements Runnable {

	private long sleepSecond = 3600;
	private ArrayList<ConfigurationSaveRunner> saveRunnerList = new ArrayList<ConfigurationSaveRunner>();

	/**
	 * @return the sleepSecond
	 */
	public long getSleepSecond() {
		return sleepSecond;
	}

	/**
	 * @param sleepSecond the sleepSecond to set
	 */
	public void setSleepSecond(long sleepSecond) {
		this.sleepSecond = sleepSecond;
	}

	/**
	 * @param mainGui the mainGui to set
	 */
	public synchronized void registerSaveRunner(ConfigurationSaveRunner saveRunner) {
		if(saveRunner != null && !saveRunnerList.contains(saveRunner)){
			saveRunnerList.add(saveRunner);
		}
	}

	@Override
	public void run() {
		long waitingtime = sleepSecond * 1000;
		while (waitingtime > 0) {
			try {
				Thread.sleep(waitingtime);
			} catch (InterruptedException e) {
			}
			synchronized (saveRunnerList) {
				for(int i=0;i<saveRunnerList.size();++i){
					saveRunnerList.get(i).saveConfiguration();
				}
			}
			waitingtime = sleepSecond * 1000;
		}
	}
}

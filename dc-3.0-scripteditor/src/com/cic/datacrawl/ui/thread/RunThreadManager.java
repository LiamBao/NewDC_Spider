package com.cic.datacrawl.ui.thread;

import java.util.HashMap;
import java.util.Map;

public class RunThreadManager {
	private Map<String, Thread> threadMap = new HashMap<String, Thread>();

	private static RunThreadManager instance;

	public static RunThreadManager getInstance() {
		if (instance == null)
			instance = new RunThreadManager();

		return instance;
	}

	public String addThread(String key, Thread thread) {
		synchronized (threadMap) {
			threadMap.put(key, thread);
		}
		return key;
	}

	public void removeThread(String key) {
		synchronized (threadMap) {
			threadMap.remove(key);
		}
	}

	public void stopThread(String key) {
		Thread t = null;
		synchronized (threadMap) {
			if (threadMap.containsKey(key)) {
				t = threadMap.get(key);
			}
		}
		while (t != null && (t.isAlive() || !t.isInterrupted())) {
			t.interrupt();
			if (t != null && (!t.isAlive() || t.isInterrupted())) {
				synchronized (threadMap) {
					if (threadMap.containsKey(key)) {
						threadMap.remove(key);
					}
				}
			}else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}

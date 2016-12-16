package com.cic.datacrawl.runner;

import java.util.Hashtable;
import java.util.Map;
import org.apache.log4j.Logger;
import com.cic.datacrawl.core.ProcessMonitor;
import com.cic.datacrawl.management.manager.SubTaskManager;

public class DeadCycleDetecter {
	
	private static final Logger LOG = Logger.getLogger(DeadCycleDetecter.class);
	private static DeadCycleDetecter instance = new DeadCycleDetecter();

	private DeadCycleDetecter() {
	}

	public static DeadCycleDetecter getInstance() {
		return instance;
	}

	private Map<String, VisitRecord> recordMap = new Hashtable<String, VisitRecord>();

	public void visit(String url) throws Exception {
		if (url == null || url.trim().length() == 0)
			return;

		VisitRecord record = null;
		if (recordMap.containsKey(url)) {
			record = recordMap.get(url);
			record.addVisit();
		} else {
			record = new VisitRecord(url);
		}
		if (record.isDeadCycle()) {
			LOG.error("Site:" + url + " visited " + record.visitCount + " times, average wait time is "
					+ record.getAverageVisitWaitTime() + "ms. There may be dead circulation in script.");

			ProcessMonitor.setScriptStatus(SubTaskManager.SCRIPT_STATUS_CODE_ERROR_DEAD_CYCLE_SCRIPT);
			ProcessMonitor.setChangeScriptStatusTime(System.currentTimeMillis());
			throw new Exception("May be it is a deadcycle script.");
		}
	}

	private class VisitRecord {
		private String url;
		private long minVisitSplit;
		private long minVisitCount;
		private long visitCount = 0;
		private long visitSplitTimeCount = 0;
		private long lastVisitTime;

		public VisitRecord(String url) {
			if (url == null)
				return;

			long currentTime = System.currentTimeMillis();
			if (url.trim().equalsIgnoreCase(this.url)) {
				addVisit();
			} else {
				this.url = url;
				visitCount = 1;
				visitSplitTimeCount = 0;
			}
			lastVisitTime = currentTime;
		}

		public void addVisit() {
			++visitCount;
			visitSplitTimeCount += (System.currentTimeMillis() - lastVisitTime);
		}

		public long getAverageVisitWaitTime() {
			return visitSplitTimeCount / visitCount;
		}

		public boolean isDeadCycle() {
			long averageVisitSplit = getAverageVisitWaitTime();
			return averageVisitSplit < minVisitSplit && averageVisitSplit > 0 && visitCount > minVisitCount;
		}
	}
}

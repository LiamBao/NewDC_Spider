package com.cic.datacrawl.management.manager;

import java.util.ArrayList;

import com.cic.datacrawl.management.entity.Task;
import com.cic.datacrawl.management.manager.base.TaskBaseManager;

public class TaskManager extends TaskBaseManager {
	public static final byte FLAG_ENABLED = 1;
	public static final byte FLAG_DISABLED = 0;
	public static final byte FLAG_BOTH = Byte.MIN_VALUE;
	public static final byte FLAG_SPLIT_NEED = 1;
	public static final byte FLAG_SPLIT_NOT_NEED = 0;

	// public DefaultEntity[] querySplitNumByKey() {
	// return getTaskDAO().querySplitNumByKey();
	// }
	
	public int initTaskFinishedFlag(int taskId) {
		return getTaskDAO().initTaskFinishedFlag(taskId);
	}
	
	public void changeTaskEnableFlag(long[] siteIds, byte enableFlag){
		getTaskDAO().changeTaskEnableFlag(siteIds, enableFlag);
	}

	public Task[] queryByEnableFlagInFinishedSite(final byte enableFlag, int taskGroupId, int rownum){
		 return getTaskDAO().queryByEnableFlagInFinishedSite(enableFlag, taskGroupId, rownum);
	}

	public int makeTimeoutFlag(int taskGroupId) {
		ArrayList<Task> retList = new ArrayList<Task>();
		Task[] tasks = queryByEnableFlag(FLAG_ENABLED, taskGroupId);
		if (tasks != null) {
			long currentTime = System.currentTimeMillis();
			for (int i = 0; i < tasks.length; ++i) {
				if (tasks[i].getDueCheckFlag()>0 && tasks[i].getDueTime() != null && tasks[i].getDueTime().getTime() < currentTime) {
					retList.add(tasks[i]);
				}
			}
		}
		long[] ret = new long[retList.size()];
		for (int i = 0; i < retList.size(); ++i) {
			ret[i] = retList.get(i).getId();
		}

		if(ret.length > 0) {
			return getTaskDAO().changeTaskEnableFlag(ret, FLAG_DISABLED);
		} else {
			return 0;
		}
	}

	public Task[] queryAllNeedSendDueAlarmTask(int taskGroupId) {
		Task[] tasks = queryByEnableFlag(FLAG_ENABLED, taskGroupId);
		if (tasks != null && tasks.length > 0) {
			long currentTime = System.currentTimeMillis();
			ArrayList<Task> list = new ArrayList<Task>();
			for (int i = 0; i < tasks.length; ++i) {
				if (tasks[i].getDueTime().getTime() < (currentTime + tasks[i].getDueAlarmBefore() * 24 * 3600000)) {

					list.add(tasks[i]);
				}
			}
			tasks = new Task[list.size()];
			list.toArray(tasks);
		}
		return tasks;
	}

}

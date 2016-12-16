package com.cic.datacrawl.management.dao;

import com.cic.datacrawl.core.entity.DefaultEntity;
import com.cic.datacrawl.management.dao.base.SubTaskBaseDAO;
import com.cic.datacrawl.management.entity.SubTask;

public interface SubTaskDAO extends SubTaskBaseDAO {

	public SubTask[] getAllExceptionTask();

	public int changeTaskStatus(long id, String key, int status);

	public SubTask[] getSubTasks();

	public DefaultEntity[] querySplitNumByKey(String[] keys);

	public int changeSendStatus(long[] ids, byte flagSendErrorSend);

	public SubTask[] queryAllNotFinishedTaskByTask(long taskId);

	public SubTask[] queryAllNotFinishedTaskByTask(long[] taskIds);
	
	public SubTask[] queryAllRecentFinishedTaskByTask(long[] taskIds);
	public SubTask[] queryAllRecentFinishedTaskByTask(long taskId);

	public int deleteBySiteId(long[] siteIds);

	public int changeTaskStatus(long[] id, byte status);

	public SubTask[] querySubtaskBySiteId(long[] siteIds);

	DefaultEntity[] querySplitNumByTaskIds(Long[] taskIds);
}

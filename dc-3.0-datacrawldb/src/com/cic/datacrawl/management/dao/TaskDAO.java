package com.cic.datacrawl.management.dao;

import com.cic.datacrawl.management.dao.base.*;
import com.cic.datacrawl.management.entity.Task;

public interface TaskDAO extends TaskBaseDAO{

	int changeTaskEnableFlag(long[] ids, byte enableFlag);

	//TODO:

	Task[] queryByEnableFlagInFinishedSite(byte enableFlag, final int taskGroupId, int rownum);
	
	int initTaskFinishedFlag(final int id);
}


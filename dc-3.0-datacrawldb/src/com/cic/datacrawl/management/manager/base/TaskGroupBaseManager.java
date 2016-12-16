package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.TaskGroupDAO;

public abstract class TaskGroupBaseManager {
	
	protected TaskGroupDAO dao;
	protected TaskGroupDAO getTaskGroupDAO() {		
		if(dao == null){
			dao = (TaskGroupDAO) ApplicationContext.getInstance().getBean("taskGroupDAO");
		}
		return dao;
	}
	
	public void setTaskGroupDAO(TaskGroupDAO dao) {
		this.dao = dao;
	}

	public void registerSpliter (final int taskGroupId, final String lanIP) {
		getTaskGroupDAO().registerSpliter(taskGroupId, lanIP);
	}
	
	public void finishedSpliter (final int taskGroupId, final byte errorFlag, final String exception) {
		getTaskGroupDAO().finishedSpliter(taskGroupId, errorFlag, exception);
	}
}

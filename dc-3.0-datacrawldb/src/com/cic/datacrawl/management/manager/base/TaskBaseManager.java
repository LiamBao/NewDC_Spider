//DON'T MODIFY ME
package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.management.entity.Task;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.TaskDAO;

public abstract class TaskBaseManager{

	protected TaskDAO dao;
	protected TaskDAO getTaskDAO() {		
		if(dao == null){
			dao = (TaskDAO) ApplicationContext.getInstance().getBean("taskDAO");
		}
		return dao;
	}
	
	public void setTaskDAO(TaskDAO dao) {
		this.dao = dao;
	}
	
	public Task getTask(int id){
		return getTaskDAO().getTask(id);
	}

	public Task[] getTasks(int[] ids){
		return getTaskDAO().getTasks(ids);
	}
	
	public long addTask(Task task){
		return getTaskDAO().addTask(task);
	}	
	
	public long[] addTasks(Task[] tasks){
		return getTaskDAO().addTasks(tasks);
	}
	
	public int deleteTask(int id){
		return getTaskDAO().deleteTask(id);
	}
	
	public int deleteTasks(int[] ids){
		return getTaskDAO().deleteTasks(ids);
	}
	
	public long saveTask(Task task){
		long ret = getTaskDAO().saveTask(task);
		if (ret == 0) {
			ret = getTaskDAO().addTask(task);
		} else {
			ret = task.getId();
		}
		return ret;
	}
	
	public int updateTask(Task task){
		return getTaskDAO().saveTask(task);		
	}
	
	public int[] updateTasks(Task[] tasks){
		return getTaskDAO().saveTasks(tasks);		
	}
	
	public int count(){
		return getTaskDAO().count();
	}
	
	public Task[] queryByEnableFlag(final byte enableFlag, final int taskGroupId){
		 return getTaskDAO().queryByEnableFlag(enableFlag, taskGroupId);
	}
	public Task[] queryBySiteId(final long siteId, final int taskGroupId){
		 return getTaskDAO().queryBySiteId(siteId, taskGroupId);
	}
}

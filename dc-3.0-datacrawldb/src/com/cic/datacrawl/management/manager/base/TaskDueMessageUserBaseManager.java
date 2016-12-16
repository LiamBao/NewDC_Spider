//DON'T MODIFY ME
package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.management.entity.TaskDueMessageUser;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.TaskDueMessageUserDAO;

public abstract class TaskDueMessageUserBaseManager{

	protected TaskDueMessageUserDAO dao;
	protected TaskDueMessageUserDAO getTaskDueMessageUserDAO() {		
		if(dao == null){
			dao = (TaskDueMessageUserDAO) ApplicationContext.getInstance().getBean("taskDueMessageUserDAO");
		}
		return dao;
	}
	
	public void setTaskDueMessageUserDAO(TaskDueMessageUserDAO dao) {
		this.dao = dao;
	}
	
	public TaskDueMessageUser[] getAllTaskDueMessageUser() {
		return getTaskDueMessageUserDAO().getAllTaskDueMessageUser();
	}
	
	public TaskDueMessageUser[] getAllTaskDueMessageUser(int startIndex, int limit) {
		return getTaskDueMessageUserDAO().getAllTaskDueMessageUser(startIndex, limit);
	}
	
	public TaskDueMessageUser getTaskDueMessageUser(long id){
		return getTaskDueMessageUserDAO().getTaskDueMessageUser(id);
	}

	public TaskDueMessageUser[] getTaskDueMessageUsers(long[] ids){
		return getTaskDueMessageUserDAO().getTaskDueMessageUsers(ids);
	}
	
	public long addTaskDueMessageUser(TaskDueMessageUser taskDueMessageUser){
		return getTaskDueMessageUserDAO().addTaskDueMessageUser(taskDueMessageUser);
	}	
	
	public long[] addTaskDueMessageUsers(TaskDueMessageUser[] taskDueMessageUsers){
		return getTaskDueMessageUserDAO().addTaskDueMessageUsers(taskDueMessageUsers);
	}
	
	public int deleteTaskDueMessageUser(long id){
		return getTaskDueMessageUserDAO().deleteTaskDueMessageUser(id);
	}
	
	public int deleteTaskDueMessageUsers(long[] ids){
		return getTaskDueMessageUserDAO().deleteTaskDueMessageUsers(ids);
	}
	
	public long saveTaskDueMessageUser(TaskDueMessageUser taskDueMessageUser){
		long ret = getTaskDueMessageUserDAO().saveTaskDueMessageUser(taskDueMessageUser);
		if (ret == 0) {
			ret = getTaskDueMessageUserDAO().addTaskDueMessageUser(taskDueMessageUser);
		} else {
			ret = taskDueMessageUser.getId();
		}
		return ret;
	}
	
	public int updateTaskDueMessageUser(TaskDueMessageUser taskDueMessageUser){
		return getTaskDueMessageUserDAO().saveTaskDueMessageUser(taskDueMessageUser);		
	}
	
	public int[] updateTaskDueMessageUsers(TaskDueMessageUser[] taskDueMessageUsers){
		return getTaskDueMessageUserDAO().saveTaskDueMessageUsers(taskDueMessageUsers);		
	}
	
	public int count(){
		return getTaskDueMessageUserDAO().count();
	}
	
}

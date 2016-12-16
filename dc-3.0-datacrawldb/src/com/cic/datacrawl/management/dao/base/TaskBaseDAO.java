//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.Task;

public interface TaskBaseDAO {

	public Task getTask(int id);

	public Task[] getTasks(int[] ids);
	
	public long addTask(Task task);
	
	public long[] addTasks(Task[] tasks);
	
	public int deleteTask(int id);
	
	public int deleteTasks(int[] ids);
	
	public int saveTask(Task task);
	
	public int[] saveTasks(final Task[] tasks);
	
	public int count();
	
	public Task[] queryByEnableFlag(final byte enableFlag, final int taskGroupId);
	public Task[] queryBySiteId(final long siteId, final int taskGroupId);
}

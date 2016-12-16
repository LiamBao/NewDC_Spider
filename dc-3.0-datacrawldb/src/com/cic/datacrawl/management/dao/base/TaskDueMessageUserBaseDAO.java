//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.TaskDueMessageUser;

public interface TaskDueMessageUserBaseDAO {

	public TaskDueMessageUser[] getAllTaskDueMessageUser();

	public TaskDueMessageUser[] getAllTaskDueMessageUser(int startIndex, int limit);

	public TaskDueMessageUser getTaskDueMessageUser(long id);

	public TaskDueMessageUser[] getTaskDueMessageUsers(long[] id);
	
	public long addTaskDueMessageUser(TaskDueMessageUser taskDueMessageUser);
	
	public long[] addTaskDueMessageUsers(TaskDueMessageUser[] taskDueMessageUsers);
	
	public int deleteTaskDueMessageUser(long id);
	
	public int deleteTaskDueMessageUsers(long[] id);
	
	public int saveTaskDueMessageUser(TaskDueMessageUser taskDueMessageUser);
	
	public int[] saveTaskDueMessageUsers(final TaskDueMessageUser[] taskDueMessageUsers);
	
	public int count();
	
}

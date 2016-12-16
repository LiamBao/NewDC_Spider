//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import java.text.DateFormat;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.DateUtil;

/**
 * 
 * TaskDueMessageUser: 用来记录信息通知关联表的信息
 */
public class TaskDueMessageUser extends BaseEntity {
	private static final TaskDueMessageUser DEFAULT_ENTITY = new TaskDueMessageUser();

	/**
	 *	Create an default TaskDueMessageUser Entity.
	 */
	public TaskDueMessageUser() {
	}
	/**
	 * Create an TaskDueMessageUser Entity.
	 * @param taskId. Type: long. 
	 * @param alarmEmail. Type: java.lang.String. 
	 */
	public TaskDueMessageUser(
		long taskId, 
				java.lang.String alarmEmail
		) {
		
		setTaskId(taskId);
		setAlarmEmail(alarmEmail);
	}


	public long getId(){
		return getLong("id");
	}
	
	public void setId(long id){
		set("id", id);
	}
	/**
	 * Get TaskId Value.<br>
	 * @return TaskId type: long
	 */
	public long getTaskId() {
		return getLong("taskId");
	}	
	/**
	 * Set 名称 Value
	 * @param 名称 type: long
	 */
	public void setTaskId(long taskId) {
		set("taskId", taskId);
	}	/**
	 * Get AlarmEmail Value.<br>
	 * @return AlarmEmail type: java.lang.String
	 */
	public java.lang.String getAlarmEmail() {
		return getString("alarmEmail");
	}	
	/**
	 * Set 邮件地址 Value
	 * @param 邮件地址 type: java.lang.String
	 */
	public void setAlarmEmail(java.lang.String alarmEmail) {
		setString("alarmEmail", alarmEmail, 50);
	}	
	@Override
	public String getTheEntityName() {
		return "t_task_due_message_user";
	}
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id", "taskId", "alarmEmail"};
	}
	
	@Override
	protected String[] initCompareColumns() {
		return new String[]{"id"};
	}
	
	@Override
	public BaseEntity getDefaultEmptyBean() {
		return DEFAULT_ENTITY;
	}
}

package com.cic.datacrawl.control.cache;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cic.datacrawl.management.entity.SubTask;
import com.cicdata.iwmdata.base.client.cache.MemcacheManagerForGwhalin;

public class TaskSpliterCache {

	/**
	 * 对TaskSpliterGroupId加锁
	 * @param taskSpliterGroupId
	 * @return 加锁是否成功
	 */
	public boolean lockTaskSpliter(int taskSpliterGroupId, int lockWaitTime) {
		String cacheKey = "TASKSPLITER_GROUP_LOCK_" + taskSpliterGroupId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, lockWaitTime);
		Date expireTime = calendar.getTime();
		
		Integer flag = 0;
		return cacheClient.add(cacheKey, flag, expireTime);
	}
	
	/**
	 * 对TaskSpliterGroupId解锁
	 * @param taskSpliterGroupId
	 * @return 解锁是否成功
	 */
	public boolean unlockTaskSpliter(int taskSpliterGroupId) {
		String cacheKey = "TASKSPLITER_GROUP_LOCK_" + taskSpliterGroupId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		return cacheClient.remove(cacheKey);
	}
	
	public boolean lockTaskId(int taskId, int lockWaitTime) {
		String cacheKey = "TASK_LOCK_" + taskId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, lockWaitTime);
		Date expireTime = calendar.getTime();
		
		Integer flag = 0;
		while(!cacheClient.add(cacheKey, flag, expireTime)) {
			try {
				Thread.sleep(50);
				
				calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, lockWaitTime);
				expireTime = calendar.getTime();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	public boolean unlockTaskId(int taskId) {
		String cacheKey = "TASK_LOCK_" + taskId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		return cacheClient.remove(cacheKey);
	}
	
	public int getSubTaskNum(int taskId) {
		List<Long> subTaskIds = getSubTaskIds(taskId);
		
		if(null != subTaskIds) {
			return subTaskIds.size();
		} else {
			return 0;
		}
	}
	
	/**
	 * Cache中存储的TaskId对应的subTaskId列表，其中subTaskId是由subTask_key计算MD5，取前8个字节转为long的结果
	 * @param taskId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getSubTaskIds(int taskId) {
		String cacheKey = "TASK_SUBTASK_" + taskId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		return (List<Long>)cacheClient.get(cacheKey);
	}
	
	/**
	 * 超时设置只是为测试使用，在进入生产环境，要把超时逻辑删除
	 * @param taskId
	 * @param taskIds
	 * @return
	 */
	public boolean addSubTaskIds(int taskId, List<Long> taskIds) {
		String cacheKey = "TASK_SUBTASK_" + taskId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.HOUR, 17);
//		
//		return cacheClient.addOrUpdate(cacheKey, taskIds, calendar.getTime());
		return cacheClient.addOrUpdate(cacheKey, taskIds);
	}
	
	/**
	 * 超时设置只是为测试使用，在进入生产环境，要把超时逻辑删除
	 * @param subTaskId
	 * @param subTask
	 * @return
	 */
	public boolean addSubTaskInfo(long subTaskId, SubTask subTask) {
		String cacheKey = "SUBTASK_" + subTaskId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();

//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.HOUR, 17);
//		
//		return cacheClient.addOrUpdate(cacheKey, subTask, calendar.getTime());
		return cacheClient.addOrUpdate(cacheKey, subTask);
	}
}

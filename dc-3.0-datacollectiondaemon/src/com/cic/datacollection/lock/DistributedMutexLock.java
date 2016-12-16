package com.cic.datacollection.lock;

import java.util.Calendar;
import java.util.Date;


import com.cicdata.iwmdata.base.client.cache.MemcacheManagerForGwhalin;

public class DistributedMutexLock {
	
	public void lockAgentSubGroup(int agentSubGroupId) {
		String cacheKey = "AGENT_SUBGROUP_LOCK_" + agentSubGroupId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		while(!cacheClient.add(cacheKey, 0)) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void lockAgentSubGroup(int agentSubGroupId, int lockTime) {
		String cacheKey = "AGENT_SUBGROUP_LOCK_" + agentSubGroupId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, lockTime);
		Date expireTime = calendar.getTime();
		
//		System.out.println("=================================================================" + expireTime.getTime() + "  " + System.currentTimeMillis());
		while(!cacheClient.add(cacheKey, 0, expireTime)) {
			try {
				Thread.sleep(100);
				
				calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, lockTime);
				expireTime = calendar.getTime();
//				System.out.println("=================================================================" + expireTime.getTime() + "  " + System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void unlockAgentSubGroup(int agentSubGroupId) {
		String cacheKey = "AGENT_SUBGROUP_LOCK_" + agentSubGroupId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		cacheClient.remove(cacheKey);
	}
	
	public void lockTask(int taskId) {
		String cacheKey = "TASK_LOCK_" + taskId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		while(!cacheClient.add(cacheKey, 0)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void lockTask(int taskId, int lockTime) {
		String cacheKey = "TASK_LOCK_" + taskId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, lockTime);
		Date expireTime = calendar.getTime();
		
		while(!cacheClient.add(cacheKey, 0, expireTime)) {
			try {
				Thread.sleep(50);
				
				calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, lockTime);
				expireTime = calendar.getTime();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void unlockTask(int taskId) {
		String cacheKey = "TASK_LOCK_" + taskId;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		cacheClient.remove(cacheKey);
	}
	
	public boolean lockAgentTimeOutCheck(int lockTime) {
		String cacheKey = "AGENT_TIMEOUT_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, lockTime);
		Date expireTime = calendar.getTime();
		
		return cacheClient.add(cacheKey, 0, expireTime);
	}
	
	public boolean unlockAgentTimeOutCheck() {
		String cacheKey = "AGENT_TIMEOUT_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		return cacheClient.remove(cacheKey);
	}
	
	public boolean lockTaskRunnerTimeOutCheck() {
		String cacheKey = "TASKRUNNER_TIMEOUT_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		return cacheClient.add(cacheKey, 0);
	}
	
	public boolean unlockTaskRunnerTimeOutCheck() {
		String cacheKey = "TASKRUNNER_TIMEOUT_LOCK";
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		return cacheClient.remove(cacheKey);
	}
	
}

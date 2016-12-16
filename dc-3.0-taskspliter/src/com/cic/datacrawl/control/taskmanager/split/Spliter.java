package com.cic.datacrawl.control.taskmanager.split;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.SpringConfiguration;
import com.cic.datacrawl.core.db.transaction.TransactionExecuter;
import com.cic.datacrawl.core.db.transaction.TransactionService;
import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.control.cache.TaskSpliterCache;
import com.cic.datacrawl.management.entity.BatchInfo;
import com.cic.datacrawl.management.entity.SubTask;
import com.cic.datacrawl.management.entity.SubTaskEntity;
import com.cic.datacrawl.management.entity.Task;
import com.cic.datacrawl.management.manager.BatchInfoManager;
import com.cic.datacrawl.management.manager.TaskManager;

/**
 * 拆分抽象类
 * 
 */
public abstract class Spliter {

	private static final Logger LOG = Logger.getLogger(Spliter.class);
	private String splitKey;
	private ArrayList<Integer> taskIdList;
	private HashMap<Integer, Task> taskMap;
	private int pageSize = 500;
	private int taskGroupId = 0;

	// private ExitSplitThread exitThread = new ExitSplitThread();

	public int getTaskGroupId() {
		return taskGroupId;
	}

	public void setTaskGroupId(int taskGroupId) {
		this.taskGroupId = taskGroupId;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	protected String getSplitKey() {
		if (splitKey == null) {
			splitKey = StringUtil.buildRandomString(10);
		}
		return splitKey;
	}

	protected long currentTime;

	/**
	 * 传过来的参数taskDefines是通过select * from t_task where is_enable=1得到的
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private Task[] filterTasksByTime(Task[] taskDefines) throws Exception {
		ArrayList<Task> taskList = new ArrayList<Task>();

		LOG.info("Get " + taskList.size()
				+ " Tasks from t_task by queryByEnableFlagInFinishedSite");

		if (taskDefines != null && taskDefines.length > 0) {
			LOG.info("共需要拆分task共 " + taskDefines.length + " 个");
			taskIdList = new ArrayList<Integer>();
			taskMap = new HashMap<Integer, Task>();
			Timestamp today = new Timestamp(System.currentTimeMillis());
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			long todayTime = today.getTime();
			today.setNanos(0);
			for (int i = 0; i < taskDefines.length; i++) {
				taskMap.put(taskDefines[i].getId(), taskDefines[i]);
				taskIdList.add(taskDefines[i].getId());
				taskList.add(taskDefines[i]);
			}
		}
		Task[] ret = new Task[taskList.size()];
		taskList.toArray(ret);
		return ret;
	}

	private Task[] filterTaskBySubTask(Task[] taskDefines) throws Exception {
		ArrayList<Task> taskList = new ArrayList<Task>();
		
		if (taskDefines != null && taskDefines.length > 0) {
			TaskSpliterCache taskSpliterCache = new TaskSpliterCache();
			for(int i=0; i<taskDefines.length; ++i) {
				Task task = taskDefines[i];
				// 从cache中查找任务的子任务个数
				int subTaskNum = taskSpliterCache.getSubTaskNum(task.getId());
				LOG.info(subTaskNum);
				if(subTaskNum==0 || task.getLastSplitNum()==0 || subTaskNum < task.getLastSplitNum()) {
					taskList.add(task);
				} else {
					LOG.info("TaskId : " + task.getId() + " delayed last batch");
				}
			}
		}
		return taskList.toArray(new Task[taskList.size()]);
	}

	protected void splitSingleTask(Task task, BatchInfo batchInfo) throws Exception {
		int subtaskCount = 0;
		int lastSplitNum = 0;
		try {
			//注意每次只处理指定最大量的item，要更新对应的状态--是否处理过，处理完后，要把状态重置，以便下一轮可以继续处理
			int cnt = count(task);
			LOG.info("task item do_count: " + cnt);
			do {
				LOG.info("TaskId: " + task.getId() + " keywords " + cnt);
				String errorMessage = null;
				SubTaskEntity[] records = null;
				try {
					records = convert(task);
				} catch (Throwable e) {
					errorMessage = e.getMessage();
					task.setLastSplitStatus((byte)1);
					
					if (errorMessage.length() > 250) {
						task.setLastSplitMsg(errorMessage.substring(0, 250));
					} else {
						task.setLastSplitMsg(errorMessage);
					}
				}
				
				LOG.info("split " + records.length + " subTasks for taskId: " + task.getId());
				try {
					// 更新任务定义数据
					if (records != null && records.length > 0) {
						//记录拆分出的子任务总数
						subtaskCount += records.length;
						// 遍历records ，设置batchId
						long createTime = System.currentTimeMillis();
						for (int k = 0; k < records.length; k++) {
							records[k].setTaskId(task.getId());
							records[k].setBatchId(batchInfo.getId());
							records[k].setCreateTime(createTime);
							records[k].setAgentType(task.getAgentType());
							records[k].setUseSnapShot(task.getUseSnapShot());
							records[k].setTurnPageWaitTime(task.getTurnPageWaitTime());
							long subTaskId = strLongMd5(records[k].getSubtaskKey());
							if(subTaskId > 0) {
								records[k].setSubTaskId(subTaskId);
							}
						}
						if (records != null && records.length > 0) {
							lastSplitNum += addSubTaskToCache(task.getId(), records);
						}
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				updateTaskSource(task);
				cnt = count(task);
			} while (cnt > 0);
			
			this.splitSubtaskCount = getSubTaskNum(task.getId());
			
			task.setLastSplitMsg("add new: " + lastSplitNum);
			task.setLastSplitNum(this.splitSubtaskCount);
			task.setSubTaskNum(subtaskCount);
			
			batchInfo.setSplitCount(subtaskCount);
			((BatchInfoManager) ApplicationContext.getInstance().getBean(
					"batchInfoManager")).saveBatchInfo(batchInfo);
			
			LOG.info("Split " + lastSplitNum + " subTasks for taskId: " + task.getId());
			if(lastSplitNum > 0) {
				LOG.info("Init finished_flag as 0 for taskId : " + task.getId());
				int flag =((TaskManager)ApplicationContext.getInstance().getBean("taskManager")).initTaskFinishedFlag(task.getId());
				LOG.info("Init finished_flag result: " + flag);
			}
			
		} catch (Throwable e) {
			LOG.error(e.getMessage());
			LOG.error(e.getLocalizedMessage());
		}
	}
	
	private int getSubTaskNum(int taskId) {
		TaskSpliterCache taskSpliterCache = new TaskSpliterCache();
		List<Long> subTaskIds = taskSpliterCache.getSubTaskIds(taskId);
		if(null == subTaskIds) {
			return 0;
		} else {
			return subTaskIds.size();
		}
	}
	
	private int addSubTaskToCache(int taskId, SubTaskEntity[] records) {
		TaskSpliterCache taskSpliterCache = new TaskSpliterCache();
		
		//lock
		taskSpliterCache.lockTaskId(taskId, 1800);

		List<Long> subTaskIds = taskSpliterCache.getSubTaskIds(taskId);
		if(null == subTaskIds) {
			subTaskIds = new ArrayList<Long>();
		}
		
		Set<Long> subTaskIdSet = new HashSet<Long>();
		for(Long id : subTaskIds) {
			subTaskIdSet.add(id);
		}
		
		int addNum = 0;
		long subTaskId = 0;
		for(SubTaskEntity record : records) {
			subTaskId = record.getSubTaskId();
			if(!subTaskIdSet.contains(subTaskId)) {
				SubTask subTask = new SubTask(record);
				LOG.info(addNum + " SubTask: " + record);
				taskSpliterCache.addSubTaskInfo(subTaskId, subTask);
				subTaskIdSet.add(subTaskId);
				subTaskIds.add(subTaskId);
				++addNum;
			}
		}
		
/*		List<Long> subTaskIdList = new ArrayList<Long>();
		for(Long id : subTaskIdSet) {
			subTaskIdList.add(id);
		}*/
		
		taskSpliterCache.addSubTaskIds(taskId, subTaskIds);
		
		//unlock
		taskSpliterCache.unlockTaskId(taskId);
		
		return addNum;
	}
	
	private int splitSubtaskCount = 0;
	private boolean noException = true;

	/**
	 * 拆分任务
	 * 
	 * @throws Exception
	 */
	private void splitNeedSplitTask() throws Exception {
		TaskManager taskManager = (TaskManager) ApplicationContext
				.getInstance().getBean("taskManager");
		String splitMaxTask = ((SpringConfiguration) ApplicationContext
				.getInstance().getBean("config")).getSplitMaxTask();
		
		Task[] needSplitTaskDefines = null;
		Task[] needSplitTaskDefinesByTime = null;

		// 按时间获取需要拆分的任务
		needSplitTaskDefinesByTime = filterTasksByTime(taskManager
				.queryByEnableFlagInFinishedSite(TaskManager.FLAG_ENABLED,
						this.taskGroupId, Integer.parseInt(splitMaxTask)));
		LOG.info(needSplitTaskDefinesByTime.length + " tasks found by time.");
		needSplitTaskDefines = filterTaskBySubTask(needSplitTaskDefinesByTime);

		LOG.info("There are " + needSplitTaskDefines.length + " tasks need split.");
		if (needSplitTaskDefinesByTime != null
				&& needSplitTaskDefinesByTime.length > 0) {
			splitKey = StringUtil.buildRandomString(10);
			LOG.info("获得需要拆分的任务定义数组 " + needSplitTaskDefinesByTime.length);
			LOG.info("Build splitKey: '" + splitKey + "'.");

			for (int i = 0; i < needSplitTaskDefinesByTime.length; i++) {
				needSplitTaskDefinesByTime[i].setLastSplitKey(splitKey);
				needSplitTaskDefinesByTime[i].setLastSplitTime(new Timestamp(currentTime));
				needSplitTaskDefinesByTime[i].setLastSplitMsg("");
			}
			if (needSplitTaskDefines != null && needSplitTaskDefines.length > 0) {
				ArrayList<Task> taskList = new ArrayList<Task>();
				for (int i = 0; i < needSplitTaskDefines.length; i++) {
					this.splitSubtaskCount = 0;
					long startTime = System.currentTimeMillis();
					
					Task task = needSplitTaskDefines[i];
					LOG.info("Start split task(" + task.getId() + "): "
							+ task.getName());
					BatchInfo batchInfo = new BatchInfo(splitKey,
							needSplitTaskDefines[i].getId(), 0, new Timestamp(
									currentTime));

					// Insert Batch Object
					int batchId = ((BatchInfoManager) ApplicationContext
							.getInstance().getBean("batchInfoManager"))
							.addBatchInfo(batchInfo);
					batchInfo.setId(batchId);
					
					final Task finalTask = task;
					final BatchInfo finalBatchInfo = batchInfo;

					TransactionService transactionService = (TransactionService) ApplicationContext
							.getInstance().getBean("transactionService");
					transactionService.execute(new TransactionExecuter() {

						@Override
						public void execute() throws Exception {
							try {
								noException = true;
								splitSingleTask(finalTask, finalBatchInfo);
							} catch (Exception e) {
								noException = false;
								throw e;
							}
						}

						@Override
						public Object getResult() {
							return null;
						}
					});

					long finishTime = System.currentTimeMillis();
					task.setLastSplitCostTime((int)(finishTime-startTime)/1000);
					
					if (noException) {
						taskList.add(task);
					}

					Calendar calendar = Calendar.getInstance();
					Timestamp nextStartTime = task.getStartTime();
					if (nextStartTime == null) {
						nextStartTime = new Timestamp(currentTime);
					}

					long loop = 1;
					long nextStartTimeNS = 0;
					do {
						nextStartTimeNS = nextStartTime.getTime() + loop
								* task.getSplitWaitTime();
						++loop;
					} while (nextStartTimeNS < System.currentTimeMillis());

					LOG.info("Split CurStartTime: " + nextStartTime);
					nextStartTime = new Timestamp(nextStartTimeNS);
					LOG.info("Split NextStartTime: " + nextStartTime);
					
					calendar.setTime(nextStartTime);
					if (calendar.get(Calendar.YEAR) > 9000) {
						calendar.set(Calendar.YEAR, 9000);
					}
					nextStartTime = new Timestamp(calendar.getTimeInMillis());
					task.setStartTime(nextStartTime);
					LOG.info("Split task(" + needSplitTaskDefines[i].getId()
							+ "): " + needSplitTaskDefines[i].getName()
							+ " finished, total split " + splitSubtaskCount
							+ " subtasks.");
					
					task.setFinishFlag((byte)0);
				}
				if (taskList.size() > 0) {
					LOG.info("Modify the task defines");
					Task[] tasks = new Task[taskList.size()];
					taskList.toArray(tasks);
					taskManager.updateTasks(tasks);
				} else {
					LOG.info("NO task need define.");
				}
			}
		}
	}
	
	/**
	 * 拆分任务入口
	 * 
	 * @throws Exception
	 */
	public void doSplit() throws Exception {
		// Thread t = new Thread(exitThread);
		// t.start();
		currentTime = System.currentTimeMillis();
		TaskManager taskManager = (TaskManager) ApplicationContext
				.getInstance().getBean("taskManager");
		taskManager.makeTimeoutFlag(this.taskGroupId);// 将超过最后日期的任务标记为disable
		
		splitNeedSplitTask();// 拆分任务
	}

	/**
	 * 计算字符串的MD5，返回MD5前8个字节转换的数字，用于将subTaskKey转换为subTaskId
	 * @param orgStr
	 * @return
	 */
	public long strLongMd5(String orgStr)
	{
	    byte[] retByte;
	    try 
	    {
	        retByte = MessageDigest.getInstance("MD5").digest(orgStr.getBytes());
	        long num = md5ToLong(retByte);
	        System.out.println(num + "  " + orgStr);
	        
	        return num;
	    } 
	    catch (NoSuchAlgorithmException e) 
	    {
	        e.printStackTrace();
	    }
	    
	    return 0;
	}
	
	public long md5ToLong(byte[] md5Bytes) { 
        long num = 0; 
        long bytes[] = new long[8];
        
        for(int i=0; i<8; i++) {
        	if(i == 7) {
        		bytes[i] = md5Bytes[i] & 0x7f;
        	} else {
        		bytes[i] = md5Bytes[i] & 0xff;
        	}
        }
        
        for(int i=0; i<8; i++) {
        	bytes[i] <<= (i * 8);
        	num |= bytes[i];
        }
 
        return num;
    } 
	

	protected abstract SubTaskEntity[] convert(Task defines) throws Throwable;

	protected abstract int count(Task defines) throws Throwable;

	protected abstract void updateTaskSource(Task defines) throws Throwable;

}

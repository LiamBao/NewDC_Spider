package com.cic.datacollection.anew;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.cic.datacollection.anew.AgentTimeOutThread;
import com.cic.datacollection.anew.ServerDaemonListener;
import com.cic.datacollection.bean.SubTaskInfo;
import com.cic.datacollection.lock.DistributedMutexLock;
import com.cic.datacollection.rpc.protocol.AgentFinishedReportWritable;
import com.cic.datacollection.rpc.protocol.ResAgentCheckConsistencyWritable;
import com.cic.datacollection.util.IPUtil;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.initialize.InitializerRegister;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.management.entity.SubTask;
import com.cic.datacrawl.management.manager.SubTaskManager;
import com.cicdata.iwmdata.base.client.cache.MemcacheManagerForGwhalin;

public class RpcServerDaemon {

	private static final Logger log = Logger.getLogger(RpcServerDaemon.class);
	private static RpcServerDaemon cs;
	private static String serverAddress;
	private static int numHandlers;
	public static int agentTimeOut;
	public static int cacheEnableTime;
	public static int lockHoldTime;

	public static int getNumHandlers() {
		return numHandlers;
	}

	public static void setNumHandlers(int numHandlers) {
		RpcServerDaemon.numHandlers = numHandlers;
	}

	public static int getAgentTimeOut() {
		return agentTimeOut;
	}

	public static void setAgentTimeOut(int agentTimeOut) {
		RpcServerDaemon.agentTimeOut = agentTimeOut;
	}

	public static int getCacheEnableTime() {
		return cacheEnableTime;
	}

	public static void setCacheEnableTime(int cacheEnableTime) {
		RpcServerDaemon.cacheEnableTime = cacheEnableTime;
	}

	public static int getLockHoldTime() {
		return lockHoldTime;
	}

	public static void setLockHoldTime(int lockHoldTime) {
		RpcServerDaemon.lockHoldTime = lockHoldTime;
	}

	public synchronized static RpcServerDaemon getInstance() {
		if (cs == null) {
			System.out.println("a new instance");
			cs = new RpcServerDaemon();
		}
		return cs;
	}

	private RpcServerDaemon() {
		//serverAddress = IPUtil.getHostIP();
	}
	
	/**
	 * add agent info to DB
	 * @param ip
	 * @param port
	 * @return
	 */
	private int initAgentInfoToDB(String ip, int port) {
		int agentId = 0;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		ResultSet resultSet = null;
		PreparedStatement ps = null;
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT ID FROM T_AGENT_INFO WHERE IP='" + ip + "'");
			if (rs.next()) {
				ps = conn.prepareStatement("UPDATE T_AGENT_INFO SET ENABLE_FLAG=1,REGISTER_TIME=NOW(),UPDATE_TIME=NOW(),PORT=? WHERE IP=?");
				ps.setInt(1, port);
				ps.setString(2, ip);
				if (ps.executeUpdate() == 1) {
					agentId = rs.getInt("ID");
				}
			} else {
				ps = conn.prepareStatement("INSERT INTO T_AGENT_INFO(ID,IP,PORT,AGENT_SUBGROUP_ID,ENABLE_FLAG,MAX_INSTANCE,REGISTER_TIME,UPDATE_TIME,UPDATE_USER) VALUES (DEFAULT,?,?,DEFAULT,1,DEFAULT,NOW(),NOW(),DEFAULT)");
				ps.setString(1, ip);
				ps.setInt(2, port);
				if (ps.executeUpdate() == 1) {
					resultSet = st.executeQuery("SELECT ID FROM T_AGENT_INFO WHERE IP='" + ip + "'");
					if(resultSet.next()) {
						agentId = resultSet.getInt("ID");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Register Agent to DB failed: ", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if(resultSet != null) {
				try {
					resultSet.close();
				} catch (Exception e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
		return agentId;
	}
	
	/**
	 * set status for agentId
	 * @param agentId
	 * @param agentStatus
	 */
	public void setAgentStatus(int agentId, byte agentStatus) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			ps = conn.prepareStatement("UPDATE T_AGENT_INFO SET ENABLE_FLAG=? WHERE ID=?");
			ps.setByte(1, agentStatus);
			ps.setInt(2, agentId);
			ps.executeUpdate();
		} catch (Exception e) {
			log.error("Set status=" + agentStatus + " for agentId: " + agentId + " failed !");
			e.printStackTrace();
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 从数据库获取Agent最多可以执行的子任务个数
	 * @param agentId
	 * @return maxInstance_closePageSnapShot
	 */
	public String getAgentMaxInstance(int agentId) {
		Connection conn = null;
		Statement st = null;
		ResultSet resultSet = null;
		String resultStr = "2_0_0";
		
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			st = conn.createStatement();
			resultSet = st.executeQuery("SELECT MAX_INSTANCE, CLOSE_PAGE_SNAPSHOT, AGENT_SUBGROUP_ID FROM T_AGENT_INFO WHERE ID=" + agentId);
			if (resultSet.next()) {
				int maxInstance = resultSet.getInt("MAX_INSTANCE");
				int closePageSnapShot = resultSet.getByte("CLOSE_PAGE_SNAPSHOT");
				int agentSubgroupId = resultSet.getInt("AGENT_SUBGROUP_ID");
				resultStr = maxInstance + "_" + closePageSnapShot + "_" + agentSubgroupId;
			}
		} catch (Exception e) {
			log.error("Get MaxInstance for AgentID: " + agentId + " failed!");
			e.printStackTrace();
		} finally {
			if(resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return resultStr;
	}
	
	/**
	 * 获取Agent所在的线路分组
	 * @param agentId
	 * @return
	 */
	private int getAgentSubGroupId(int agentId){
		int agentSubGroupId = 0;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT AGENT_SUBGROUP_ID as agentSubGroupId FROM T_AGENT_INFO WHERE ID=" + agentId);
			if(rs.next()) {
				agentSubGroupId = rs.getInt("agentSubGroupId");
			}
		} catch (Exception e) {
			log.error("Get agentSubGroupId for agentId: " + agentId + " failed!");
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(st != null) {
					st.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (Exception e1) {
				log.error(e1.getMessage());
				e1.printStackTrace();
			}
		}
		
		return agentSubGroupId;
	}
	
	/**
	 * 获取一条线路可以执行的所有TaskId列表，并按优先级从高到低的顺序
	 * @param agentSubGroupId
	 * @return
	 */
	private List<Integer> getAvailableTaskIds(int agentSubGroupId){
		List<Integer> taskIds = new ArrayList<Integer>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT a.ID as taskId, a.PRIORITY as priority FROM T_TASK as a, T_AGENT_SUBGROUP as b WHERE b.ID=" + agentSubGroupId + " and a.FINISHED_FLAG=0 and a.AGENT_GROUP_ID = b.AGENT_GROUP_ID ORDER BY priority DESC");
			while(rs.next()) {
				int taskId = rs.getInt("taskId");
				taskIds.add(taskId);
			}
		} catch (Exception e) {
			log.error("getAvailableTaskIds SQL failed");
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(st != null) {
					st.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return taskIds;
	}
	
	/**
	 * 设置Task完成标识字段：表示所有的子任务都已经执行完成
	 * @param taskId
	 * @return
	 */
	private boolean updateTaskFinishedStatus(int taskId) {
		boolean flag = false;
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			ps = conn.prepareStatement("UPDATE T_TASK SET FINISHED_FLAG=1 WHERE ID=?");
			ps.setInt(1, taskId);
			if(ps.executeUpdate() == 1) {
				flag = true;
			}
		} catch (Exception e) {
			log.error("updateTaskFinishedStatus for taskId: " + taskId + " failed");
			e.printStackTrace();
		} finally {
			try {
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return flag;
	}
	
	/**
	 * 把执行完（超时）的子任务插入到DB中，供运维分析
	 * @param subTask
	 * @return
	 */
	public boolean insertSubTaskIntoDB(SubTask subTask) {
		boolean flag = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {	
			String sql = "INSERT INTO T_SUBTASK(ID,SUBTASK_ID,SUBTASK_KEY,TASK_ID,TASK_BATCH_ID,AGENT_ID,AGENT_IP,AGENT_TYPE,AGENT_SUBGROUP_ID,SITE_ID,PROJECT_ID,KEYWORD,FORUMID,THREADID,USE_SNAPSHOT,TURN_PAGE_WAITTIME,SCRIPT_FILE,SCRIPT_MAIN,CREATE_TIME,START_TIME,FINISH_TIME,PARSE_ITEM_NUM,DOWN_PAGE_NUM,COST_TIME,EXE_TIME,EFFECTIVE_TIME_RATE,ERR_CODE,ERR_MSG,ERR_URL) "
					+ "VALUES (DEFAULT,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			ps = conn.prepareStatement(sql);
			int index = 1;
			ps.setLong(index++, subTask.getSubTaskId());
			ps.setString(index++, subTask.getSubtaskKey());
			ps.setInt(index++, subTask.getTaskId());
			ps.setInt(index++, subTask.getBatchId());
			ps.setInt(index++, subTask.getAgentId());
			ps.setString(index++, subTask.getAgentIp());
			ps.setByte(index++, subTask.getAgentType());
			ps.setInt(index++, subTask.getAgentGroupId());
			ps.setInt(index++, subTask.getSiteId());
			ps.setInt(index++, subTask.getProjectId());
			ps.setString(index++, subTask.getKeyWord());
			ps.setString(index++, subTask.getForumId());
			ps.setString(index++, subTask.getThreadId());
			ps.setByte(index++, subTask.getUseSnapShot());
			ps.setInt(index++, subTask.getTurnPageWaitTime());
			ps.setString(index++, subTask.getScriptFile());
			ps.setString(index++, subTask.getScriptMain());
			ps.setTimestamp(index++, new java.sql.Timestamp(subTask.getCreateTime()));
			ps.setTimestamp(index++, new java.sql.Timestamp(subTask.getStartTime()));
			ps.setTimestamp(index++, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setInt(index++, subTask.getScrapeCount());
			ps.setInt(index++, subTask.getDownPageCount());
			ps.setInt(index++, subTask.getCostTime());
			ps.setInt(index++, subTask.getExeTime());
			ps.setInt(index++, subTask.getEffectiveTimeRate());
			ps.setInt(index++, subTask.getErrorCode());
			ps.setString(index++, subTask.getErrorMsg());
			ps.setString(index++, subTask.getErrorUrl());
			
			if(ps.executeUpdate() == 1) {
				flag = true;
			} else {
				log.error("Insert SubTask into DB failed for subTaskId: " + subTask.getSubTaskId());
			}
		} catch (SQLException e) {
			log.error("Insert SubTask into DB failed for subTaskId: " + subTask.getSubTaskId());
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
			
		return flag;
	}
	
	/**
	 * 从DB中获取所有有效Agent的ID列表，用于超时检查
	 * @return
	 */
	private List<Integer> getActiveAgents() {
		List<Integer> agentIds = new ArrayList<Integer>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT ID FROM T_AGENT_INFO WHERE ENABLE_FLAG=1");
			while(rs.next()) {
				agentIds.add(rs.getInt("ID"));
			}
		} catch (Exception e) {
			log.error("Get Active Agent from t_agent_info failed");
			log.equals(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(st != null) {
					st.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (Exception e1) {
				log.error(e1.getMessage());
				e1.printStackTrace();
			}
		}
		
		return agentIds;
	}
	
	private int addTaskMaxInstanceToCacheFromDB(int taskId) {
		int maxInstance = 0;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = ((DataSource) ApplicationContext.getInstance().getBean("dataSource")).getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT MAX_INSTANCE FROM T_TASK WHERE ID=" + taskId);
			if(rs.next()) {
				maxInstance = rs.getInt("MAX_INSTANCE");
			}
		} catch (Exception e) {
			log.error("Get maxInstance for taskId: " + taskId + " failed!");
			log.equals(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(st != null) {
					st.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (Exception e1) {
				log.error(e1.getMessage());
				e1.printStackTrace();
			}
		}
		
		if(maxInstance > 0) {
			MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
			String taskMaxInstanceKey = "TASK_MAXINSTANCE_" + taskId;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, cacheEnableTime);
			cacheClient.addOrUpdate(taskMaxInstanceKey, maxInstance, calendar.getTime());
		}
		
		return maxInstance;
	}
	
	/**
	 * Agent register
	 * 更新数据库成功后，要更新其心跳时间
	 * @param ip
	 * @param port
	 * @return
	 */
	public int registerAgent(String ip, int port) {
		int agentId = initAgentInfoToDB(ip, port);
		if(agentId >0) {
			MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
			String heartBeatKey = "AGENT_HEARTBEAT_" + agentId;
			long heartBeatTime = System.currentTimeMillis();
			cacheClient.addOrUpdate(heartBeatKey, heartBeatTime);
		}
		
		return agentId;
	}
	
	/**
	 * 从给定的TaskId列表中挑选一个可以执行的子任务 
	 * 挑选到可执行的子任务后，就直接修改相关信息（不考虑agent的恢复，避免锁时间太长，影响系统效率）
	 * @param agentId
	 * @param agentSubGroupId
	 * @param taskIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SubTaskInfo getAnAvailableSubTask (int agentId, int agentSubGroupId, List<Integer> taskIds) {
		SubTaskInfo subTaskInfo = null;
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		
		for(Integer taskId : taskIds) {
			log.info("Add mutexLock for TaskId: " + taskId);
			mutexLock.lockTask(taskId, lockHoldTime);
			
			//select an available task
			String taskMaxInstanceKey = "TASK_MAXINSTANCE_" + taskId;
			String curTaskInstanceKey = "TASK_" + taskId + "_LINE_" + agentSubGroupId;
			Integer taskMaxInstanceNum = (Integer)cacheClient.get(taskMaxInstanceKey);
			if(null == taskMaxInstanceNum) {
				taskMaxInstanceNum = addTaskMaxInstanceToCacheFromDB(taskId);
			}
			log.info("MaxInstanceNum for TaskId : " + taskId + " is " + taskMaxInstanceNum);
			Integer taskCurInstanceNum = (Integer)cacheClient.get(curTaskInstanceKey);
			if(null != taskCurInstanceNum && taskCurInstanceNum >= taskMaxInstanceNum) {
				log.warn("taskCurInstanceNum >= taskMaxInstanceNum : " + taskCurInstanceNum + ">=" + taskMaxInstanceNum);
				log.info("Del mutexLock for TaskId: " + taskId);
				mutexLock.unlockTask(taskId);
				continue;
			}
			
			String taskSubTaskCacheKey = "TASK_SUBTASK_" + taskId;
			List<Long> subTaskIds = (List<Long>)cacheClient.get(taskSubTaskCacheKey);
			if(subTaskIds == null || subTaskIds.isEmpty()) {
				updateTaskFinishedStatus(taskId);
				log.info("Del mutexLock for TaskId: " + taskId);
				mutexLock.unlockTask(taskId);
				if(subTaskIds != null) {
					log.error("Get SubTaskIds for taskId: " + taskId + " from Cache failed");
					cacheClient.remove(taskSubTaskCacheKey);
				}
				
				continue;
			} else {
				log.info("TaskId: " + taskId + " 's all subTaskIds num: " + subTaskIds.size());
				for(int i=0; i<subTaskIds.size();) {
					//select an available subTask
					long subTaskId = subTaskIds.get(i);
					String subTaskCacheKey = "SUBTASK_" + subTaskId;
					SubTask subTask = (SubTask)cacheClient.get(subTaskCacheKey);
					if(subTask == null) {
						log.error("Get SubTaskInfo from Cache for " + subTaskCacheKey + " failed");
						subTaskIds.remove(i);
					} else {
						subTaskInfo = new SubTaskInfo();
						subTaskInfo.setAgentType(subTask.getAgentType());
						subTaskInfo.setTaskId(subTask.getTaskId());
						subTaskInfo.setSubTaskId(subTask.getSubTaskId());
						subTaskInfo.setSubTaskKey(subTask.getSubtaskKey());
						subTaskInfo.setScriptFile(subTask.getScriptFile());
						subTaskInfo.setScriptMain(subTask.getScriptMain());
						subTaskInfo.setSiteId(subTask.getSiteId());
						subTaskInfo.setBatchId(subTask.getBatchId());
						subTaskInfo.setUseSnapShot(subTask.getUseSnapShot());
						subTaskInfo.setTurnPageWaitTime(subTask.getTurnPageWaitTime());
						subTaskInfo.setCreateTime(subTask.getCreateTime());
						subTaskInfo.setStartTime(subTask.getStartTime());
						subTaskInfo.setProjectId(subTask.getProjectId());
						subTaskInfo.setKeyWord(subTask.getKeyWord());
						subTaskInfo.setForumId(subTask.getForumId());
						subTaskInfo.setThreadId(subTask.getThreadId());
						
						subTaskIds.remove(i);
						
						break;
					}
				}
				
				if(subTaskIds.isEmpty()) {
					cacheClient.remove(taskSubTaskCacheKey);
					updateTaskFinishedStatus(taskId);
				} else {
					cacheClient.addOrUpdate(taskSubTaskCacheKey, subTaskIds);
				}
				
				log.info("Del mutexLock for taskId: " + taskId);
				mutexLock.unlockTask(taskId);
				
				if(null != subTaskInfo) {
					break;
				} 
			}
		}
		
		return subTaskInfo;
	}
	
	/**
	 * 成功分配到子任务，修改Agent当前执行的subTaskId列表及task当前在该线路上的instance个数
	 * @param agentSubGroupId
	 * @param agentId
	 * @param subTaskId
	 * @param taskId
	 */
	private void modifyAgentTaskInstanceNum(int agentSubGroupId, int agentId, long subTaskId, int taskId) {
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		
		String agentSubtaskKey = "AGENT_SUBTASK_" + agentId;
		@SuppressWarnings("unchecked")
		List<Long> agentSubTaskIds = (List<Long>)cacheClient.get(agentSubtaskKey);
		if(agentSubTaskIds == null) {
			agentSubTaskIds = new ArrayList<Long>();
		}
		agentSubTaskIds.add(subTaskId);
		cacheClient.addOrUpdate(agentSubtaskKey, agentSubTaskIds);
		
		String curTaskInstanceKey = "TASK_" + taskId + "_LINE_" + agentSubGroupId;
		Integer taskCurInstanceNum = (Integer)cacheClient.get(curTaskInstanceKey);
		
		if(null == taskCurInstanceNum) {
			taskCurInstanceNum = new Integer(1);
		} else {
			++taskCurInstanceNum;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 3600);
		Date expireTime = calendar.getTime();
		
		cacheClient.addOrUpdate(curTaskInstanceKey, taskCurInstanceNum, expireTime);
		log.info(curTaskInstanceKey + " Instance num: " + taskCurInstanceNum);
	}
	
	/**
	 * 获取一个AgentId可以执行的子任务
	 * @param agentId
	 * @return
	 */
	public SubTaskInfo allocateSubTaskForAgent(int agentId){
		SubTaskInfo subTaskInfo = null;
		
		int agentSubGroupId = getAgentSubGroupId(agentId);
		log.info("Get agentSubGroupId for agentId : " + agentId + " is " + agentSubGroupId);
		if(agentSubGroupId > 0) {
			DistributedMutexLock mutexLock = new DistributedMutexLock();
			//agentSubGroup 加锁，在Agent上报子任务启动状态的消息后解锁或锁超时
			log.info("Add mutexLock for AgentSubGroupId: " + agentSubGroupId);
			mutexLock.lockAgentSubGroup(agentSubGroupId, lockHoldTime);
			log.info("Add mutexLock for AgentSubGroupId: " + agentSubGroupId + " success");
			
			List <Integer> taskIds = getAvailableTaskIds(agentSubGroupId);
			log.info("getAvailableTaskIds " + taskIds.toString());
			if(!taskIds.isEmpty()) {
				subTaskInfo = getAnAvailableSubTask(agentId, agentSubGroupId, taskIds);
				if(subTaskInfo != null) {
					modifyAgentTaskInstanceNum(agentSubGroupId, agentId, subTaskInfo.getSubTaskId(), subTaskInfo.getTaskId());
				}
			} else {
				log.info("There is no available taskId for agentId: " + agentId);
			}
			
			log.info("Del mutexLock for AgentSubGroupId: " + agentSubGroupId);
			mutexLock.unlockAgentSubGroup(agentSubGroupId);
		} else {
			log.error("aentSubGroupId for agentId: " + agentId + " is " + agentSubGroupId);
		}
		
		return subTaskInfo;
	}
	
	/**
	 * 获取agentId当前正在处理的所有subTaskId列表
	 * @param agentId
	 * @return
	 */
	public ResAgentCheckConsistencyWritable getAgentSubTaskIds(int agentId) {
		ResAgentCheckConsistencyWritable feedBack = new ResAgentCheckConsistencyWritable();
		
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
			
		String agentSubtaskKey = "AGENT_SUBTASK_" + agentId;
		@SuppressWarnings("unchecked")
		List<Long> agentSubTaskIds = (List<Long>)cacheClient.get(agentSubtaskKey);
		if(agentSubTaskIds == null) {
			agentSubTaskIds = new ArrayList<Long>();
		}
		
		feedBack.setSubTaskIds(agentSubTaskIds);

		feedBack.setCode(CodeStatus.succCode);
		
		return feedBack;
	}
	
	/**
	 * 对Agent上报的子任务信息，补充不完整的部分字段
	 * @param subTaskStatus
	 * @return
	 */
	public SubTask convertReportInfoToSubTask(AgentFinishedReportWritable subTaskStatus) {
		SubTask subTask = null;
		long subTaskId = subTaskStatus.getSubTaskId();
		if(!subTaskStatus.infoFlag) {
			MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
			String subTaskKey = "SUBTASK_" + subTaskId;
			subTask = (SubTask)cacheClient.get(subTaskKey);
			if(subTask == null) {
				log.warn("Cannot get subTask from cache for subTaskId: " + subTaskId);
				subTask = new SubTask();
			}
		} else {
			subTask = new SubTask();
			subTask.setAgentType(subTaskStatus.getAgentType());
			subTask.setTaskId(subTaskStatus.getTaskId());
			subTask.setSubtaskKey(subTaskStatus.getSubTaskKey());
			subTask.setScriptFile(subTaskStatus.getScriptFile());
			subTask.setScriptMain(subTaskStatus.getScriptMain());
			subTask.setSiteId(subTaskStatus.getSiteId());
			subTask.setBatchId(subTaskStatus.getBatchId());
			subTask.setCreateTime(subTaskStatus.getCreateTime());
			subTask.setProjectId(subTaskStatus.getProjectId());
			subTask.setKeyWord(subTaskStatus.getKeyWord());
			subTask.setForumId(subTaskStatus.getForumId());
			subTask.setThreadId(subTaskStatus.getThreadId());
			subTask.setUseSnapShot(subTaskStatus.getUseSnapShot());
			subTask.setTurnPageWaitTime(subTaskStatus.getTurnPageWaitTime());
		}
		
		subTask.setSubTaskId(subTaskStatus.getSubTaskId());
		subTask.setAgentId(subTaskStatus.getAgentId());
		subTask.setAgentIp(subTaskStatus.getAgentAddress());
		subTask.setErrorCode(subTaskStatus.getErrorCode());
		subTask.setErrorMsg(subTaskStatus.getErrorMsg());
		subTask.setErrorUrl(subTaskStatus.getErrorUrl());
		subTask.setDownPageCount(subTaskStatus.getDownPageCount());
		subTask.setScrapeCount(subTaskStatus.getScrapeCount());
		subTask.setStartTime(subTaskStatus.getStartTime());
		subTask.setCostTime((int)(System.currentTimeMillis()-subTaskStatus.getStartTime()));
		subTask.setExeTime(subTaskStatus.getExeTime());
		subTask.setEffectiveTimeRate(subTaskStatus.getEffectiveTimeRate());
				
		return subTask;
	}
	
	/**
	 * 将Agent上报的子任务插入的DB
	 * @param subTask
	 * @return
	 */
	public boolean addSubTaskInfoToDB(SubTask subTask) {
		boolean flag = false;
		flag = insertSubTaskIntoDB(subTask);
		if(!flag) {
			log.error("ERROR--Insert SubTask : " + subTask.toString());
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			flag = insertSubTaskIntoDB(subTask);
		}
		
		return flag;
	}
	
	/**
	 * 判断subTaskId对应的信息是否被删除
	 * @param subTaskId
	 * @return
	 */
	public boolean subTaskIsExist(int agentId, long subTaskId) {
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		String agentSubTaskKey = "AGENT_SUBTASK_" + agentId;
		log.info("Cache get agentSubTaskKey : " + agentSubTaskKey);
		@SuppressWarnings("unchecked")
		List<Long> subTaskIds = (List<Long>)cacheClient.get(agentSubTaskKey);
		if(subTaskIds != null && subTaskIds.contains(subTaskId)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 处理Agent上报的子任务信息
	 * @param subTaskStatus
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int processSubTaskFinishReport(AgentFinishedReportWritable subTaskStatus) {
		int code = CodeStatus.succCode;
		
		SubTask subTask = convertReportInfoToSubTask(subTaskStatus);
		
		int agentSubGroupId = getAgentSubGroupId(subTask.getAgentId());
		subTask.setAgentGroupId(agentSubGroupId);
		
		if(!addSubTaskInfoToDB(subTask)) {
			code = CodeStatus.sqlErrCode;
			//return code;
			log.info("ERROR--Insert SubTask : " + subTask.toString());
		}
		code = CodeStatus.succCode;
		
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		long subTaskId = subTask.getSubTaskId();
		String subTaskCacheKey = "SUBTASK_" + subTaskId;
		boolean rmSubTaskFlag = cacheClient.remove(subTaskCacheKey);
		
		log.info("Get agentSubGroupId: " + agentSubGroupId);
		DistributedMutexLock mutexLock = new DistributedMutexLock();
		if(agentSubGroupId > 0) {
			log.info("Add mutexLock for AgentSubGroupId: " + agentSubGroupId);
			mutexLock.lockAgentSubGroup(agentSubGroupId, lockHoldTime);
			if(rmSubTaskFlag){
				String curTaskInstanceKey = "TASK_" + subTask.getTaskId() + "_LINE_" + agentSubGroupId;
				Integer curInstanceNum = (Integer)cacheClient.get(curTaskInstanceKey);
				if(null == curInstanceNum || curInstanceNum < 1) {
					log.error("Invalid value for cacheKey : " + curTaskInstanceKey);
				} else {
					--curInstanceNum;
					
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.SECOND, 3600);
					Date expireTime = calendar.getTime();
					
					cacheClient.addOrUpdate(curTaskInstanceKey, curInstanceNum, expireTime);
					log.info(curTaskInstanceKey + " Instance num: " + curInstanceNum);
				}
			} else {
				log.error("processSubTaskFinishReport: cannot delete SubTask from cache for subTaskId: " + subTaskId);
			}
			
			String agentSubTaskKey = "AGENT_SUBTASK_" + subTask.getAgentId();
			log.info("Cache get agentSubTaskKey : " + agentSubTaskKey);
			List<Long> subTaskIds = (List<Long>)cacheClient.get(agentSubTaskKey);
			if(null == subTaskIds || subTaskIds.isEmpty()) {
				log.error("Invalid value for cacheKey : " + agentSubTaskKey);
			} else {
				subTaskIds.remove(new Long(subTaskId));
				if(subTaskIds.isEmpty()) {
					cacheClient.remove(agentSubTaskKey);
				} else {
					cacheClient.addOrUpdate(agentSubTaskKey, subTaskIds);
				}
			}
			
			log.info("Del mutexLock for AgentSubGroupId: " + agentSubGroupId);
			mutexLock.unlockAgentSubGroup(agentSubGroupId);
		}
		
		return code;
	}
	
	/**
	 * 更新Agent的心跳时间，并返回该Agent最多同时并行的Instance个数
	 * @param agentId
	 * @return
	 */
	public String updateAgentHeartBeatTime(int agentId) {
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		String heartBeatKey = "AGENT_HEARTBEAT_" + agentId;
		Long lastHeartBeatTime = (Long)cacheClient.get(heartBeatKey);
		if(null == lastHeartBeatTime) {
			//设置Agent为可用状态
			byte agentStatus = 1;
			setAgentStatus(agentId, agentStatus);
		}
		
		long heartBeatTime = System.currentTimeMillis();
		cacheClient.addOrUpdate(heartBeatKey, heartBeatTime);
		
		return getAgentMaxInstance(agentId);
	}

	/**
	 * Agent心跳是否超时
	 * @param agentId
	 * @param curTime
	 * @return
	 */
	private int calcAgentIntervalTimeOut(int agentId, long curTime) {
		MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
		String heartBeatKey = "AGENT_HEARTBEAT_" + agentId;
		Long heartBeatTime = (Long)cacheClient.get(heartBeatKey); 
		int intervalTime = 0;
		if(null == heartBeatTime || heartBeatTime == 0) {
			log.error("Cannot get heartbeat time for agentId: "+agentId+" from cache");
			intervalTime = agentTimeOut + 1;
		} else {
			intervalTime = (int)(curTime-heartBeatTime);
		}
		
		return intervalTime;
	}
	
	/**
	 * 处理超时的agent信息
	 * @param agentId
	 */
	@SuppressWarnings("unchecked")
	private void processTimeOutAgent(int agentId, int intervalTime) {
		log.info("Begin processTimeOutAgent for AgentId: " + agentId);
		int agentSubGroupId = getAgentSubGroupId(agentId);
		if(agentSubGroupId > 0) {
			MemcacheManagerForGwhalin cacheClient = MemcacheManagerForGwhalin.getInstance();
			DistributedMutexLock mutexLock = new DistributedMutexLock();
			
			log.info("Add mutexLock for AgentSubGroupId: " + agentSubGroupId);
			mutexLock.lockAgentSubGroup(agentSubGroupId, lockHoldTime);
			String agentSubTaskCacheKey = "AGENT_SUBTASK_" + agentId;
			List<Long> subTaskIds = (List<Long>)cacheClient.get(agentSubTaskCacheKey);
			if(null != subTaskIds && !subTaskIds.isEmpty()) {
				for(Long subTaskId : subTaskIds) {
					String subTaskCacheKey = "SUBTASK_" + subTaskId;
					SubTask subTask = (SubTask)cacheClient.get(subTaskCacheKey);
					if(null == subTask) {
						log.error("processTimeOutAgent: cannot get SubTask from cache for subTaskId: " + subTaskId);
						continue;
					}
					
					subTask.setCostTime(intervalTime);
					subTask.setExeTime(intervalTime);
					subTask.setAgentId(agentId);
					subTask.setAgentGroupId(agentSubGroupId);
					subTask.setErrorCode(SubTaskManager.AGENT_STATUS_CODE_TIME_OUT);
					subTask.setErrorMsg("Agent TimeOut");
					insertSubTaskIntoDB(subTask);
					if(cacheClient.remove(subTaskCacheKey)) {
						String curTaskInstanceCacheKey = "TASK_" + subTask.getTaskId() + "_LINE_" + agentSubGroupId;
						Integer curInstanceNum = (Integer)cacheClient.get(curTaskInstanceCacheKey);
						if(null == curInstanceNum || curInstanceNum < 1) {
							log.error("Invalid value: " + curInstanceNum + " for cacheKey : " + curTaskInstanceCacheKey);
						} else {
							--curInstanceNum;
							cacheClient.addOrUpdate(curTaskInstanceCacheKey, curInstanceNum);
							log.info(curTaskInstanceCacheKey + " Instance num: " + curInstanceNum);
						}
					}
				}
			}
			
			log.info("Del mutexLock for AgentSubGroupId: " + agentSubGroupId);
			mutexLock.unlockAgentSubGroup(agentSubGroupId);
			
			//clear AgentInfo from Cache
			String heartBeatCacheKey = "AGENT_HEARTBEAT_" + agentId;
			cacheClient.remove(heartBeatCacheKey);
			cacheClient.remove(agentSubTaskCacheKey);
			
			byte agentStatus = 0;
			setAgentStatus(agentId, agentStatus);
		} else {
			byte agentStatus = 0;
			setAgentStatus(agentId, agentStatus);
		}
		
		log.info("End processTimeOutAgent for AgentId: " + agentId);
	}
	
	/**
	 * Agent超时检查及处理
	 * @return
	 */
	public int processAgentTimeOut(){
		int timeOutNum = 0;
		List<Integer> activeAgentIds = getActiveAgents();
		if(activeAgentIds.isEmpty()) {
			log.info("SQL Error Or No active agent for timeOut check");
			return timeOutNum;
		}
		
		log.info("Active AgentIds: " + activeAgentIds.toString());
		
		long curTime = System.currentTimeMillis();
		for(Integer agentId : activeAgentIds) {
			log.info("TimeOut check agentId: " + agentId);
			int intervalTime = calcAgentIntervalTimeOut(agentId, curTime);
			if(intervalTime > agentTimeOut) {
				log.error("TimeOut Agent: " + agentId);
				++timeOutNum;
				processTimeOutAgent(agentId, intervalTime);
			}
			log.info("Finished TimeOut check agentId: " + agentId);
		}
		
		return timeOutNum;
	}
	
	@SuppressWarnings("static-access")
	public static void start_up(String[] args) throws Exception {
		// 获取配置文件路径
		String path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";
		ApplicationContext.initialiaze(path, true);
		InitializerRegister.getInstance().execute();
		RpcServerDaemon cs = RpcServerDaemon.getInstance();
		Configuration conf = new Configuration();
		conf.addResource(new Path("conf/conf.xml"));
		int cacheEnableTime = conf.getInt("control.center.cache.enable.time", 600);
		int lockHoldTime = conf.getInt("control.center.lock.hold.time", 10);
		int numHandlers = conf.getInt("data.collection.num.handlers", 150);
		cs.setCacheEnableTime(cacheEnableTime);
		cs.setLockHoldTime(lockHoldTime);
		cs.setNumHandlers(numHandlers);

		// 启动服务端的监听服务线程
		String localHost = IPUtil.getHostIP();
		serverAddress = conf.get("control.center.address", localHost);
		int port = conf.getInt("control.center.port", 16000);
		ServerDaemonListener serverListener = new ServerDaemonListener(new Configuration(), serverAddress, port, numHandlers);
		serverListener.start();
		
		// 超时检测线程（//时间全部换算成ms，配置中默认为s）
		int agentTimeOut = conf.getInt("agent.timeout.time", 300) * 1000;
		cs.setAgentTimeOut(agentTimeOut);
		AgentTimeOutThread timeOutThread = new AgentTimeOutThread(agentTimeOut);
		timeOutThread.start();
		
		log.info("Control Scheduler start success !");
	}

	public static void main(String[] args) {
		try {
			start_up(args);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}

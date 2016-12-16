package com.cic.datacollection.anew;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.cic.datacollection.bean.TaskRunnerInfo;
import com.cic.datacollection.mina.ClientIoHandlerAdapter;
import com.cic.datacollection.mina.MinaClient;
import com.cic.datacollection.mina.protocol.AgentCheckConsistencyProtocol;
import com.cic.datacollection.mina.protocol.AgentFinishedReportProtocol;
import com.cic.datacollection.mina.protocol.AgentGetSubTaskProtocol;
import com.cic.datacollection.mina.protocol.AgentHeartBeatProtocol;
import com.cic.datacollection.mina.protocol.AgentRegisterProtocol;
import com.cic.datacollection.mina.protocol.FeedBackProtocol;
import com.cic.datacollection.mina.protocol.ResCheckConsistencyProtocol;
import com.cic.datacollection.mina.protocol.ResGetSubTaskProtocol;
import com.cic.datacollection.util.IPUtil;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ServiceName;
import com.cic.datacrawl.management.manager.SubTaskManager;

public class MinaAgentDaemon {

	private static final Logger log = Logger.getLogger(MinaAgentDaemon.class);
	private static MinaAgentDaemon agent = new MinaAgentDaemon();
	private static String serverDaemonAddress;
	private static int serverDaemonPort;

	private static MinaClient heartBeatClient;
	private static MinaClient taskManagerClient;
	
	private static int agentId;
	private static String address;
	private static int port;
	
	private static int maxTaskRunnerNum;
	private static int curTaskRunnerNum;
	private static int taskRunnerTimeOut;
	//记录TaskRunner执行的subTask信息以及心跳时间，subTaskId为key
	private static Map<Long, TaskRunnerInfo> taskRunnerInfoMap;
	
	public static MinaAgentDaemon getInstance() {
		return agent;
	}

	public static int getServerDaemonPort() {
		return serverDaemonPort;
	}

	public static void setServerDaemonPort(int serverDaemonPort) {
		MinaAgentDaemon.serverDaemonPort = serverDaemonPort;
	}

	public static MinaClient getHeartBeatClient() {
		return heartBeatClient;
	}

	public static void setHeartBeatClient(MinaClient heartBeatClient) {
		MinaAgentDaemon.heartBeatClient = heartBeatClient;
	}

	public static MinaClient getTaskManagerClient() {
		return taskManagerClient;
	}

	public static void setTaskManagerClient(MinaClient taskManagerClient) {
		MinaAgentDaemon.taskManagerClient = taskManagerClient;
	}

	public static MinaClient getFinishedReportClient() {
		MinaClient agentClient = new MinaClient(serverDaemonAddress, serverDaemonPort, new ClientIoHandlerAdapter());
		agentClient.connect();
		
		return agentClient;
	}

	private MinaAgentDaemon() {
		address = IPUtil.getHostIP();
		taskRunnerInfoMap = new HashMap<Long, TaskRunnerInfo>();
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		MinaAgentDaemon.port = port;
	}

	public static String getAddress() {
		return address;
	}

	public static void setAddress(String address) {
		MinaAgentDaemon.address = address;
	}

	public static int getAgentId() {
		return agentId;
	}
	
	public static void setAgentId(int agentId) {
		MinaAgentDaemon.agentId = agentId;
	}
	
	public static int getMaxTaskRunnerNum() {
		return maxTaskRunnerNum;
	}
	
	public static void setMaxTaskRunnerNum(int maxTaskRunnerNum) {
		MinaAgentDaemon.maxTaskRunnerNum = maxTaskRunnerNum;
	}
	
	public static int getCurTaskRunnerNum() {
		return curTaskRunnerNum;
	}
	
	public static void setCurTaskRunnerNum(int curTaskRunnerNum) {
		MinaAgentDaemon.curTaskRunnerNum = curTaskRunnerNum;
	}
	
	public static int getTaskRunnerTimeOut() {
		return taskRunnerTimeOut;
	}
	
	public static void setTaskRunnerTimeOut(int taskRunnerTimeOut) {
		MinaAgentDaemon.taskRunnerTimeOut = taskRunnerTimeOut;
	}
	
	public String getServerDaemonAddress() {
		return serverDaemonAddress;
	}

	public void setServerDaemonAddress(String serverDaemonAddress) {
		MinaAgentDaemon.serverDaemonAddress = serverDaemonAddress;
	}

	/**
	 * 判断给定的subTaskId是否已经启动TaskRunner
	 * @param subTaskId
	 * @return
	 */
	public boolean taskRunnerIsExist(long subTaskId) {
		synchronized (taskRunnerInfoMap) {
			return taskRunnerInfoMap.containsKey(subTaskId);
		}
	}
	/**
	 * 启动一个新的taskRunner
	 * @param taskRunnerInfo
	 * @return
	 */
	public synchronized boolean addTaskRunnerInfo(TaskRunnerInfo taskRunnerInfo) {
		boolean flag = false;
		synchronized (taskRunnerInfoMap) {
			if(!taskRunnerInfoMap.containsKey(taskRunnerInfo.getSubTaskId())) {
				TaskRunnerInfo newTaskRunner = new TaskRunnerInfo(taskRunnerInfo);
				taskRunnerInfoMap.put(newTaskRunner.getSubTaskId(), newTaskRunner);
				curTaskRunnerNum = taskRunnerInfoMap.size();
				flag = true;
			}
		}
		
		return flag;
	}

	/**
	 * 执行subTaskId的TaskRunner完成
	 * @param subTaskId
	 * @return
	 */
	public synchronized boolean delTaskRunnerInfo(long subTaskId) {
		boolean flag = false;
		synchronized (taskRunnerInfoMap) {
			if(taskRunnerInfoMap.containsKey(subTaskId)){
				taskRunnerInfoMap.remove(subTaskId);
				curTaskRunnerNum = taskRunnerInfoMap.size();
				flag = true;
			}
		}
		
		return flag;
	}
	
	/**
	 * 从Map中提取Agent记录的子任务信息
	 * @param subTaskId
	 * @return
	 */
	public synchronized TaskRunnerInfo getTaskRunnerInfo(long subTaskId) {
		synchronized (taskRunnerInfoMap) {
			if(taskRunnerInfoMap.containsKey(subTaskId)) {
				return taskRunnerInfoMap.get(subTaskId);
			} else {
				return null;
			}
		}
	}
	
	/**
	 * 更新执行subTaskId的TaskRunner的心跳时间
	 * @param subTaskId
	 * @return
	 */
	public void updateTaskRunnerHeartBeat(long subTaskId) {
		synchronized (taskRunnerInfoMap) {
			if(taskRunnerInfoMap.containsKey(subTaskId)) {
				taskRunnerInfoMap.get(subTaskId).setHeartBeatTime(System.currentTimeMillis());
			} else {
				TaskRunnerInfo taskRunnerInfo = new TaskRunnerInfo();
				taskRunnerInfo.setSubTaskId(subTaskId);
				taskRunnerInfo.setAgentId(getAgentId());
				taskRunnerInfo.setAgentAddress(getAddress());
				taskRunnerInfo.setHeartBeatTime(System.currentTimeMillis());
				taskRunnerInfo.setStartTime(System.currentTimeMillis());
				taskRunnerInfo.setInfoFlag(false);
				taskRunnerInfoMap.put(subTaskId, taskRunnerInfo);
			}
		}
	}
	
	/**
	 * 检查是否有超时的TaskRunner
	 * @param curTime
	 * @return
	 */
	public List<TaskRunnerInfo> CheckTaskRunnerTimeOut(long curTime) {
		List<TaskRunnerInfo> taskRunnerList = new ArrayList<TaskRunnerInfo>();
		
		synchronized (taskRunnerInfoMap) {
			Set<Long> subTaskIdSet = taskRunnerInfoMap.keySet();
			for(Iterator<Long> iter=subTaskIdSet.iterator(); iter.hasNext();) {
				long subTaskId = iter.next();
				log.info(subTaskId + " TaskRunner TimeOut Check: curTime - taskRunnerInfoMap.get(subTaskId).getHeartBeatTime(): " + (curTime - taskRunnerInfoMap.get(subTaskId).getHeartBeatTime()));
				if(curTime - taskRunnerInfoMap.get(subTaskId).getHeartBeatTime() > taskRunnerTimeOut)
				{
					taskRunnerList.add(taskRunnerInfoMap.get(subTaskId));
				}
			}
			
			if(!taskRunnerList.isEmpty()) {
				for(TaskRunnerInfo taskRunner : taskRunnerList) {
					taskRunnerInfoMap.remove(taskRunner.getSubTaskId());
				}
				
				curTaskRunnerNum = taskRunnerInfoMap.size();
			}
		}
		
		return taskRunnerList;
	}
	
	/**
	 * 判断是否需要从Control Scheduler获取新的任务
	 * @return
	 */
	public boolean needGetNewTask() {
		synchronized (taskRunnerInfoMap) {
			log.info("curTaskRunnerNum: " + curTaskRunnerNum + " < maxTaskRunnerNum: " + maxTaskRunnerNum);
			return curTaskRunnerNum < maxTaskRunnerNum;
		}
	}
	
	/**
	 * 向ControlServer发送请求获取任务
	 * @return
	 */
	public TaskRunnerInfo getTaskFromControlServer() {
		log.info("Request Task from Control Server...");
		TaskRunnerInfo taskRunnerInfo = null;
		AgentGetSubTaskProtocol agentInfo = new AgentGetSubTaskProtocol(ServiceName.AGENT_REQUEST_TASK.getName(), agentId);
		log.info("New subTaskInfo");

		ResGetSubTaskProtocol feedBack = (ResGetSubTaskProtocol) taskManagerClient.executeRemoteService(agentInfo);
		if(feedBack != null) {
			if(feedBack.getCode() == CodeStatus.succCode) {
				taskRunnerInfo = new TaskRunnerInfo();
				taskRunnerInfo.setAgentAddress(address);
				taskRunnerInfo.setAgentId(agentId);
				taskRunnerInfo.setAgentType(feedBack.getAgentType());
				taskRunnerInfo.setTaskId(feedBack.getTaskId());
				taskRunnerInfo.setSubTaskId(feedBack.getSubTaskId());
				taskRunnerInfo.setSubTaskKey(feedBack.getSubTaskKey());
				taskRunnerInfo.setScriptFile(feedBack.getScriptFile());
				taskRunnerInfo.setScriptMain(feedBack.getScriptMain());
				taskRunnerInfo.setAgentGroupId(feedBack.getAgentGroupId());
				taskRunnerInfo.setSiteId(feedBack.getSiteId());
				taskRunnerInfo.setBatchId(feedBack.getBatchId());
				taskRunnerInfo.setCreateTime(feedBack.getCreateTime());
				taskRunnerInfo.setStartTime(feedBack.getStartTime());
				taskRunnerInfo.setProjectId(feedBack.getProjectId());
				taskRunnerInfo.setKeyWord(feedBack.getKeyWord());
				taskRunnerInfo.setForumId(feedBack.getForumId());
				taskRunnerInfo.setThreadId(feedBack.getThreadId());
				taskRunnerInfo.setInfoFlag(true);
			}
		} else {
			log.error("NetError: GetTaskFromControlServer failed !");
			log.error(agentInfo);
		}
		
		return taskRunnerInfo;
	}
	
	/**
	 * 对比Shceduler记录的subTaskId是否在本机都存在
	 * @param num
	 * @param subTaskIds
	 */
	private void subTaskConsistencyCheck(int num, long[] subTaskIds) {
		log.info("Begin Check-consistent for subTaskids");
		synchronized (taskRunnerInfoMap) {
			if(num > curTaskRunnerNum) {
				for(int i=0; i< num; ++i) {
					if(!taskRunnerInfoMap.containsKey(subTaskIds[i])) {
						log.info("TaskRunner not exist for subTaskId:" + subTaskIds[i]);
						reportSubTaskStatus(subTaskIds[i], SubTaskManager.TASKRUNNER_STATUS_CODE_NOT_EXIST, "SubTask Not Exist", "", 0, System.currentTimeMillis(), 0);
					}
				}
			}
		}
		log.info("End Check-consistent for subTaskids");
	}
	
	/**
	 * 从Scheduler获取该Agent当前正在执行的subTaskId列表，与本机记录的subTaskId对比
	 * 如果本机不存在，则按该subTaskId不存在的状态上报给Scheduler
	 */
	public void checkSubTaskConsistency() {
		AgentCheckConsistencyProtocol agentInfo = new AgentCheckConsistencyProtocol(ServiceName.AGENT_GET_SUBTASKIDS.getName(), agentId);
		
		ResCheckConsistencyProtocol feedBack = (ResCheckConsistencyProtocol) taskManagerClient.executeRemoteService(agentInfo);
		if(feedBack != null) {
			log.info("Agent get subTaskIds Status: " + feedBack.getCode());
			if(feedBack.getNum() > 0) {
				subTaskConsistencyCheck(feedBack.getNum(), feedBack.getSubTaskIds());
			}
		} else {
			log.error("NetError: Send GetSubTaskIdsRequest to ControlServer failed");
			log.error(agentInfo);
		}
	}
	
	/**
	 * 上报TaskRunner执行结果信息
	 * @param subTaskId
	 * @param errCode
	 * @param errMsg
	 * @param errUrl
	 * @param scrapeCount
	 * @param costTime
	 * @return
	 */
	public boolean reportSubTaskStatus(long subTaskId, int errCode, String errMsg, String errUrl, int scrapeCount, long startTime, int exeTime) {
		boolean flag = false;
		AgentFinishedReportProtocol subTaskStatus = new AgentFinishedReportProtocol();
		subTaskStatus.setProtocolName(ServiceName.AGENT_TASK_FINISH_REPORT.getName());
		TaskRunnerInfo taskRunnerInfo = getTaskRunnerInfo(subTaskId);
		if(taskRunnerInfo == null || !taskRunnerInfo.isInfoFlag()) {
			subTaskStatus.setSubTaskId(subTaskId);
			subTaskStatus.setAgentId(getAgentId());
			subTaskStatus.setAgentAddress(getAddress());
			subTaskStatus.setStartTime(startTime);
			subTaskStatus.setInfoFlag(false);
			subTaskStatus.setSubTaskKey("");
			subTaskStatus.setScriptFile("");
			subTaskStatus.setScriptMain("");
			subTaskStatus.setKeyWord("");
			subTaskStatus.setForumId("");
			subTaskStatus.setThreadId("");
		} else {
			subTaskStatus.setAgentId(taskRunnerInfo.getAgentId());
			subTaskStatus.setAgentAddress(taskRunnerInfo.getAgentAddress());
			subTaskStatus.setAgentType(taskRunnerInfo.getAgentType());
			subTaskStatus.setTaskId(taskRunnerInfo.getTaskId());
			subTaskStatus.setSubTaskId(taskRunnerInfo.getSubTaskId());
			subTaskStatus.setSubTaskKey(taskRunnerInfo.getSubTaskKey());
			subTaskStatus.setScriptFile(taskRunnerInfo.getScriptFile());
			subTaskStatus.setScriptMain(taskRunnerInfo.getScriptMain());
			subTaskStatus.setAgentGroupId(taskRunnerInfo.getAgentGroupId());
			subTaskStatus.setSiteId(taskRunnerInfo.getSiteId());
			subTaskStatus.setBatchId(taskRunnerInfo.getBatchId());
			subTaskStatus.setCreateTime(taskRunnerInfo.getCreateTime());
			subTaskStatus.setStartTime(taskRunnerInfo.getStartTime());
			subTaskStatus.setProjectId(taskRunnerInfo.getProjectId());
			subTaskStatus.setKeyWord(taskRunnerInfo.getKeyWord());
			subTaskStatus.setForumId(taskRunnerInfo.getForumId());
			subTaskStatus.setThreadId(taskRunnerInfo.getThreadId());
			subTaskStatus.setInfoFlag(taskRunnerInfo.isInfoFlag());
		}
		
		subTaskStatus.setErrorCode(errCode);
		subTaskStatus.setErrorMsg(errMsg);
		subTaskStatus.setErrorUrl(errUrl);
		subTaskStatus.setScrapeCount(scrapeCount);
		subTaskStatus.setExeTime(exeTime);
		
		log.info("Begin report subTask status for subTaskId:" + subTaskStatus.getSubTaskId());
		MinaClient agentClient = getFinishedReportClient();

		FeedBackProtocol feedBack = (FeedBackProtocol) agentClient.executeRemoteService(subTaskStatus);
		if(feedBack != null) {
			if(feedBack.getStatusCode() == CodeStatus.succCode) {
				flag = true;
			}
		} else {
			log.error("NetError: Send FinishReport to ControlServer failed for subTaskId: " + subTaskId);
			log.error(subTaskStatus);
		}

		log.info("End report subTask status for subTaskId:" + subTaskStatus.getSubTaskId());
		
		agentClient.close();
		
		return flag;
	}
	
	/**
	 * 发送心跳到Control Server；并获取该Agent最多同时并行的Instance个数
	 * @return
	 */
	public boolean sendHeartBeat() {
		boolean flag = false;
		log.info("Send HeartBeat...");
		AgentHeartBeatProtocol heartBeatPackage = new AgentHeartBeatProtocol(ServiceName.AGENT_HEART_BEAT.getName(), agentId);
		
		FeedBackProtocol feedBack = (FeedBackProtocol) heartBeatClient.executeRemoteService(heartBeatPackage);
		if(feedBack != null) {
			if(feedBack.getStatusCode() == CodeStatus.succCode) {
				flag = true;
				maxTaskRunnerNum = Integer.parseInt(feedBack.getObjectStr().trim());
			}
			log.info("Send HeartBeat success; " + feedBack.toString());
		} else {
			log.error("NetError: Send HeartBeat to ControlServer failed");
			log.error(heartBeatPackage);
		}
		
		return flag;
	}
	
	@SuppressWarnings("static-access")
	private static void start(String[] args) throws Exception {
		MinaAgentDaemon agent = MinaAgentDaemon.getInstance();		
		String path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";
		ApplicationContext.initialiaze(path, true);
		Configuration conf = new Configuration();
		conf.addResource(new Path("conf/conf.xml"));
		
		// 设置ServerDaemon的信息
		String centerAddress = conf.get("control.center.address", "localhost");
		agent.setServerDaemonAddress(centerAddress);
		int centerPort = conf.getInt("control.center.port", 16000);
		int agentPort = conf.getInt("agent.listener.port", 16001);
		
		agent.setServerDaemonPort(centerPort);
		agent.setPort(agentPort);
		
		MinaClient taskManagerClient = new MinaClient(centerAddress, centerPort, new ClientIoHandlerAdapter());
		taskManagerClient.connect();
		
		MinaClient heartBeatClient = new MinaClient(centerAddress, centerPort, new ClientIoHandlerAdapter());
		heartBeatClient.connect();
		
		agent.setTaskManagerClient(taskManagerClient);
		agent.setHeartBeatClient(heartBeatClient);
		
		//向Control Scheduler 发送注册信息，返回该Agent对应的AgentId,MaxInstance
		log.info("Send register info to Control Server...");
		AgentRegisterProtocol registerInfo = new AgentRegisterProtocol(ServiceName.AGENT_REGISTER.getName(), agent.getAddress(), agent.getPort());
		log.info("Send Protocol name: " + registerInfo.getProtocolName());
		FeedBackProtocol feedBack = (FeedBackProtocol) agent.getTaskManagerClient().executeRemoteService(registerInfo);// 向Server提交注册信息
		if (feedBack != null) {
			if( feedBack.getStatusCode() == CodeStatus.succCode) {
				String [] strs = feedBack.getObjectStr().trim().split("_");
				agent.setAgentId(Integer.parseInt(strs[0]));
				agent.setMaxTaskRunnerNum(Integer.parseInt(strs[1]));
				log.info("Register Success, AgentId: " + agent.getAgentId() + " ,MaxTaskRunnerNum: " + agent.getMaxTaskRunnerNum());
			} else {
				log.info(feedBack.getErrorMessage());
				System.exit(0);
			}
		} else{				
			System.exit(0);
		}
		
		// 启动监听
		AgentDaemonListener agentListener = new AgentDaemonListener(new Configuration(), agent.getAddress(), agentPort, 100);
		agentListener.start();

		try {
			//等待90秒，等待所有的taskRunner上报仍在执行的subTask信息,这个sleep时间要根据taskRunner上报心跳的间隔设置
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//设置当前正在执行的TaskRunner个数
		synchronized (taskRunnerInfoMap) {
			agent.setCurTaskRunnerNum(taskRunnerInfoMap.size());
		}
		
		//时间全部换算成ms，配置中默认为s
		int heartBeatTime = conf.getInt("agent.heartbeat.time", 60)*1000;
		AgentHeartBeatReportThread heartBeatReportThread = new AgentHeartBeatReportThread(heartBeatTime);
		heartBeatReportThread.start();
		
		int taskRunnerTimeOut = conf.getInt("task.runner.timeout.time", 300) * 1000;
		agent.setTaskRunnerTimeOut(taskRunnerTimeOut);
		
		AgentTaskManagerThread taskManagerThread = new AgentTaskManagerThread(taskRunnerTimeOut);
		taskManagerThread.start();
		
		TaskRunnerTimeOutThread taskRunnerTimeOutThread = new TaskRunnerTimeOutThread(taskRunnerTimeOut);
		taskRunnerTimeOutThread.start();
		
		log.info("Start Agent Success !");
	}

	public static void main(String[] args) {
		try {
			start(args);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}

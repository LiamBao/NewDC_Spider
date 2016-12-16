package com.cic.datacollection.anew;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacollection.bean.TaskRunnerInfo;
import com.cic.datacollection.rpc.protocol.AgentCheckConsistencyWritable;
import com.cic.datacollection.rpc.protocol.AgentFinishedReportWritable;
import com.cic.datacollection.rpc.protocol.AgentGetSubTaskWritable;
import com.cic.datacollection.rpc.protocol.AgentHeartBeatWritable;
import com.cic.datacollection.rpc.protocol.AgentRegisterWritable;
import com.cic.datacollection.rpc.protocol.ResAgentCheckConsistencyWritable;
import com.cic.datacollection.rpc.protocol.ResAgentGetSubTaskWritable;
import com.cic.datacollection.util.IPUtil;
import com.cic.datacollection.util.LockFile;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.rpc.ClientImpl;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ServiceName;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;
import com.cic.datacrawl.core.rpc.protocol.RecordCollectWritable;
import com.cic.datacrawl.core.util.FileUtils;
import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.management.manager.SubTaskManager;

public class RpcAgentDaemon {

	private static final Logger log = Logger.getLogger(RpcAgentDaemon.class);
	private static RpcAgentDaemon agent = new RpcAgentDaemon();
	private static String serverDaemonAddress;
	private static int serverDaemonPort;
	private static ClientImpl agentClient;
	private static ClientImpl newDCSvrAgentClient;
	private static int agentId;
	private static int agentSubgroupId;
	private static String address;
	private static int port;
	private static int handlerNum;
	
	private static int closePageSnapShot;
	private static int maxTaskRunnerNum;
	private static int curTaskRunnerNum;
	private static int taskRunnerTimeOut;
	//记录TaskRunner执行的subTask信息以及心跳时间，subTaskId为key
	private static Map<Long, TaskRunnerInfo> taskRunnerInfoMap;
	
	// 
	private static int holdTime;
	// 文件最大个数
	public static int maxFoldFileNum;
	// 扫描本地时间间隔
	private static int scanFailRecordsInterval;
	// 保存的文件类型
	public static String fileType = ".txt";
	// 失败记录保存路径
	public static String saveFailRecordsPath;
	// 文件最大容量（M）
	private static int maxFileSize = 10;

	public static ClientImpl getNewDCSvrAgentClient() {
		return newDCSvrAgentClient;
	}

	public static void setNewDCSvrAgentClient(ClientImpl newDCSvrAgentClient) {
		RpcAgentDaemon.newDCSvrAgentClient = newDCSvrAgentClient;
	}

	public static int getHoldTime() {
		return holdTime;
	}

	public static void setHoldTime(int holdTime) {
		RpcAgentDaemon.holdTime = holdTime;
	}

	public static RpcAgentDaemon getInstance() {
		return agent;
	}

	public static int getServerDaemonPort() {
		return serverDaemonPort;
	}

	public static void setServerDaemonPort(int serverDaemonPort) {
		RpcAgentDaemon.serverDaemonPort = serverDaemonPort;
	}

	public ClientImpl getAgentClient() {
		return agentClient;
	}

	public void setAgentClient(ClientImpl agentClient) {
		RpcAgentDaemon.agentClient = agentClient;
	}

	private RpcAgentDaemon() {
		address = IPUtil.getHostIP();
		taskRunnerInfoMap = new HashMap<Long, TaskRunnerInfo>();
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		RpcAgentDaemon.port = port;
	}

	public static int getHandlerNum() {
		return handlerNum;
	}

	public static void setHandlerNum(int handlerNum) {
		RpcAgentDaemon.handlerNum = handlerNum;
	}

	public static int getClosePageSnapShot() {
		return closePageSnapShot;
	}

	public static void setClosePageSnapShot(int closePageSnapShot) {
		RpcAgentDaemon.closePageSnapShot = closePageSnapShot;
	}

	public static String getAddress() {
		return address;
	}

	public static void setAddress(String address) {
		RpcAgentDaemon.address = address;
	}

	public static int getAgentId() {
		return agentId;
	}
	
	public static void setAgentId(int agentId) {
		RpcAgentDaemon.agentId = agentId;
	}
	
	public static int getAgentSubgroupId() {
		return agentSubgroupId;
	}

	public static void setAgentSubgroupId(int agentSubgroupId) {
		RpcAgentDaemon.agentSubgroupId = agentSubgroupId;
	}

	public static int getMaxTaskRunnerNum() {
		return maxTaskRunnerNum;
	}
	
	public static void setMaxTaskRunnerNum(int maxTaskRunnerNum) {
		RpcAgentDaemon.maxTaskRunnerNum = maxTaskRunnerNum;
	}
	
	public static int getCurTaskRunnerNum() {
		return curTaskRunnerNum;
	}
	
	public static void setCurTaskRunnerNum(int curTaskRunnerNum) {
		RpcAgentDaemon.curTaskRunnerNum = curTaskRunnerNum;
	}
	
	public static int getTaskRunnerTimeOut() {
		return taskRunnerTimeOut;
	}
	
	public static void setTaskRunnerTimeOut(int taskRunnerTimeOut) {
		RpcAgentDaemon.taskRunnerTimeOut = taskRunnerTimeOut;
	}
	
	public String getServerDaemonAddress() {
		return serverDaemonAddress;
	}

	public void setServerDaemonAddress(String serverDaemonAddress) {
		RpcAgentDaemon.serverDaemonAddress = serverDaemonAddress;
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
		log.info("addTaskRunnerInfo taskRunnerInfoMap");
		synchronized (taskRunnerInfoMap) {
			log.info("addTaskRunnerInfo taskRunnerInfoMap synchronized");
			if(!taskRunnerInfoMap.containsKey(taskRunnerInfo.getSubTaskId())) {
				TaskRunnerInfo newTaskRunner = new TaskRunnerInfo(taskRunnerInfo);
				taskRunnerInfoMap.put(newTaskRunner.getSubTaskId(), newTaskRunner);
				curTaskRunnerNum = taskRunnerInfoMap.size();
				flag = true;
			}
			log.info("addTaskRunnerInfo taskRunnerInfoMap out");
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
		log.info("delTaskRunnerInfo taskRunnerInfoMap ,taskRunnerInfoMap size:" + curTaskRunnerNum);
		synchronized (taskRunnerInfoMap) {
			log.info("delTaskRunnerInfo taskRunnerInfoMap synchronized for subTaskId:" + subTaskId);
			if(taskRunnerInfoMap.containsKey(subTaskId)){
				TaskRunnerInfo info = taskRunnerInfoMap.remove(subTaskId);
				log.info("del task info:" + info.toString());
				curTaskRunnerNum = taskRunnerInfoMap.size();
				flag = true;
			}
			
			log.info("delTaskRunnerInfo taskRunnerInfoMap out,taskRunnerInfoMap size:" + curTaskRunnerNum);
		}
		
		return flag;
	}
	
	/**
	 * 从Map中提取Agent记录的子任务信息
	 * @param subTaskId
	 * @return
	 */
	public synchronized TaskRunnerInfo getTaskRunnerInfo(long subTaskId) {
		log.info("getTaskRunnerInfo taskRunnerInfoMap for subTaskId: " + subTaskId);
		synchronized (taskRunnerInfoMap) {
			log.info("getTaskRunnerInfo taskRunnerInfoMap synchronized for subTaskId: " + subTaskId);
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
		log.info("updateTaskRunnerHeartBeat taskRunnerInfoMap");
		synchronized (taskRunnerInfoMap) {
			log.info("updateTaskRunnerHeartBeat taskRunnerInfoMap synchronized");
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
		log.info("CheckTaskRunnerTimeOut taskRunnerInfoMap");
		synchronized (taskRunnerInfoMap) {
			log.info("CheckTaskRunnerTimeOut taskRunnerInfoMap synchronized");
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
			
			log.info("CheckTaskRunnerTimeOut taskRunnerInfoMap out");
		}
		
		return taskRunnerList;
	}
	
	/**
	 * 判断是否需要从Control Scheduler获取新的任务
	 * @return
	 */
	public boolean needGetNewTask() {
		log.info("needGetNewTask taskRunnerInfoMap");
		//如果agentSubgroupId为无效，则不请求新任务
		if(agentSubgroupId < 1){
			return false;
		}
		synchronized (taskRunnerInfoMap) {
			log.info("needGetNewTask taskRunnerInfoMap synchronized");
			log.info("curTaskRunnerNum: " + curTaskRunnerNum + " , maxTaskRunnerNum: " + maxTaskRunnerNum);
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
		Writable agentInfo = new AgentGetSubTaskWritable(agentId);
		log.info("New subTaskInfo");

		ResAgentGetSubTaskWritable feedBack = (ResAgentGetSubTaskWritable) agentClient.execute_proxy(ServiceName.AGENT_REQUEST_TASK.getName(), agentInfo);
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
				taskRunnerInfo.setSiteId(feedBack.getSiteId());
				taskRunnerInfo.setBatchId(feedBack.getBatchId());
				taskRunnerInfo.setUseSnapShot(feedBack.getUseSnapShot());
				taskRunnerInfo.setTurnPageWaitTime(feedBack.getTurnPageWaitTime());
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
		List<Long> notExistSubTaskIds = new ArrayList<Long>();
		
		log.info("subTaskConsistencyCheck taskRunnerInfoMap");
		synchronized (taskRunnerInfoMap) {
			log.info("subTaskConsistencyCheck taskRunnerInfoMap synchronized");
			if(num > curTaskRunnerNum) {
				for(int i=0; i< num; ++i) {
					if(!taskRunnerInfoMap.containsKey(subTaskIds[i])) {
						log.info("TaskRunner not exist for subTaskId:" + subTaskIds[i]);
						notExistSubTaskIds.add(subTaskIds[i]);
					}
				}
			}
		}
		
		if(!notExistSubTaskIds.isEmpty()) {
			for(Long subTaskId : notExistSubTaskIds) {
				reportSubTaskStatus(subTaskId, SubTaskManager.TASKRUNNER_STATUS_CODE_NOT_EXIST, "SubTask Not Exist", "", 0, 0, System.currentTimeMillis(), 0, 0);
			}
		}
		
		log.info("End Check-consistent for subTaskids");
	}
	
	/**
	 * 从Scheduler获取该Agent当前正在执行的subTaskId列表，与本机记录的subTaskId对比
	 * 如果本机不存在，则按该subTaskId不存在的状态上报给Scheduler
	 */
	public void checkSubTaskConsistency() {
		Writable agentInfo = new AgentCheckConsistencyWritable(agentId);
		
		ResAgentCheckConsistencyWritable feedBack = (ResAgentCheckConsistencyWritable) agentClient.execute_proxy(ServiceName.AGENT_GET_SUBTASKIDS.getName(), agentInfo);
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
	public boolean reportSubTaskStatus(long subTaskId, int errCode, String errMsg, String errUrl, int downPageCount, int scrapeCount, long startTime, int exeTime, int effectiveTimeRate) {
		boolean flag = false;
		AgentFinishedReportWritable subTaskStatus = new AgentFinishedReportWritable();
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
			subTaskStatus.setSiteId(taskRunnerInfo.getSiteId());
			subTaskStatus.setBatchId(taskRunnerInfo.getBatchId());
			subTaskStatus.setCreateTime(taskRunnerInfo.getCreateTime());
			subTaskStatus.setStartTime(taskRunnerInfo.getStartTime());
			subTaskStatus.setProjectId(taskRunnerInfo.getProjectId());
			subTaskStatus.setKeyWord(taskRunnerInfo.getKeyWord());
			subTaskStatus.setForumId(taskRunnerInfo.getForumId());
			subTaskStatus.setThreadId(taskRunnerInfo.getThreadId());
			subTaskStatus.setUseSnapShot(taskRunnerInfo.getUseSnapShot());
			subTaskStatus.setTurnPageWaitTime(taskRunnerInfo.getTurnPageWaitTime());
			subTaskStatus.setInfoFlag(taskRunnerInfo.isInfoFlag());
		}
		
		subTaskStatus.setErrorCode(errCode);
		subTaskStatus.setErrorMsg(errMsg);
		subTaskStatus.setErrorUrl(errUrl);
		subTaskStatus.setDownPageCount(downPageCount);
		subTaskStatus.setScrapeCount(scrapeCount);
		subTaskStatus.setExeTime(exeTime);
		subTaskStatus.setEffectiveTimeRate(effectiveTimeRate);
		
		log.info("Begin report subTask status for subTaskId:" + subTaskStatus.getSubTaskId());
		FeedBackWritable feedBack = (FeedBackWritable) agentClient.execute_proxy(ServiceName.AGENT_TASK_FINISH_REPORT.getName(), subTaskStatus);
		if(feedBack != null) {
			log.info("Finished report status: " + feedBack.getCode());
			if(feedBack.getCode() == CodeStatus.succCode) {
				flag = true;
			}
		} else {
			log.error("NetError: Send FinishReport to ControlServer failed for subTaskId: " + subTaskId);
			log.error(subTaskStatus);
		}
		
		log.info("End report subTask status for subTaskId:" + subTaskStatus.getSubTaskId());
		
		return flag;
	}
	
	/**
	 * 发送心跳到Control Server；并获取该Agent最多同时并行的Instance个数以及是否关闭网页快照
	 * @return
	 */
	public boolean sendHeartBeat() {
		boolean flag = false;
		log.info("Send HeartBeat...");
		AgentHeartBeatWritable heartBeatWritable = new AgentHeartBeatWritable(agentId);
		
		FeedBackWritable feedBack = (FeedBackWritable) agentClient.execute_proxy(ServiceName.AGENT_HEART_BEAT.getName(), heartBeatWritable);
		if(feedBack != null) {
			if(feedBack.getCode() == CodeStatus.succCode) {
				flag = true;
				String [] strs = feedBack.getObjectStr().trim().split("_");
				maxTaskRunnerNum = Integer.parseInt(strs[0]);
				closePageSnapShot = Integer.parseInt(strs[1]);
				agentSubgroupId = Integer.parseInt(strs[2]);
			}
			log.info("Send HeartBeat success; " + feedBack.toString());
		} else {
			log.error("NetError: Send HeartBeat to ControlServer failed");
			log.error(heartBeatWritable);
		}
		
		return flag;
	}
	
	@SuppressWarnings("static-access")
	private static void start(String[] args) throws Exception {
		RpcAgentDaemon agent = RpcAgentDaemon.getInstance();		
		String path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";
		ApplicationContext.initialiaze(path, true);
		Configuration conf = new Configuration();
		conf.addResource(new Path("conf/conf.xml"));
		
		// 设置RpcServerDaemon的信息
		serverDaemonAddress = conf.get("control.center.address", "0.0.0.0");
		agent.setServerDaemonAddress(serverDaemonAddress);
		// RpcServerPort
		int centerPort = conf.getInt("control.center.port", 16000);
		// 接收RpcServer传输数据的Port
		int agentPort = conf.getInt("agent.listener.port", 16001);
		int numHandlers = conf.getInt("data.collection.num.handlers", 150);
		agent.setHandlerNum(numHandlers);
		agent.setPort(agentPort);
		// 监听RpcServer服务
		agent.setAgentClient(new ClientImpl(agent.getServerDaemonAddress(), centerPort));
		
		int holdTime = conf.getInt("control.center.lock.hold.time", 10);
		agent.setHoldTime(holdTime);
		
		int startDataCollectionAgent = conf.getInt("start.data.collection.agent", 0);
		if(startDataCollectionAgent == 1){
			// NewDataCollectionSvr IP
			String newDCSvrDaemonAddress = conf.get("newdcsvr.center.address", "0.0.0.0");
			// NewDataCollectionSvr Port
			int newDCSvrDaemonPort = conf.getInt("newdcsvr.center.port", 16010);
			// 接收NewDCSvr传输数据的Port
			//int newDCSvrListenerPort = conf.getInt("agent.newdcsvr.listener.port", 16011);
			// 监听NewDCSvr服务
			log.info("connect the server [ip: " + newDCSvrDaemonAddress+ ",port:" + newDCSvrDaemonPort +"]");
			// ClientImpl 中serverInterface只是一个代理，只有当真正传送协议的时候才会连接服务
			agent.setNewDCSvrAgentClient(new ClientImpl(newDCSvrDaemonAddress, newDCSvrDaemonPort));
			// log.info("connect the server is success?  " + getNewDCSvrAgentClient().isConnected());
			// 扫描本地文件时间间隔
			scanFailRecordsInterval = conf.getInt("scan.failrecords.interval", 60000);
			// 文件夹最大文件数
			maxFoldFileNum = conf.getInt("max.foldfile.num", 50);
			// 文件最大容量（M）
			maxFileSize = conf.getInt("max.file.size", 10);
			// 失败记录保存文件夹
			saveFailRecordsPath = conf.get("save.failrecords.path", new File(Config.INSTALL_PATH).getParent());
		}
		
		/**/
		//向Control Scheduler 发送注册信息，返回该Agent对应的AgentId,MaxInstance
		log.info("Send register info to Control Server...");
		Writable registerInfo = new AgentRegisterWritable(agent.getAddress(), agent.getPort());
		
		FeedBackWritable feedBack = (FeedBackWritable) agent.getAgentClient().execute_proxy(ServiceName.AGENT_REGISTER.getName(), registerInfo);// 向Server提交注册信息
		if (feedBack != null) {
			if( feedBack.getCode() == CodeStatus.succCode) {
				String [] strs = feedBack.getObjectStr().trim().split("_");
				agent.setAgentId(Integer.parseInt(strs[0]));
				agent.setMaxTaskRunnerNum(Integer.parseInt(strs[1]));
				agent.setClosePageSnapShot(Integer.parseInt(strs[2]));
				agent.setAgentSubgroupId(Integer.parseInt(strs[3]));
				log.info("Register Success, AgentId: " + agent.getAgentId() + " ,MaxTaskRunnerNum: " + agent.getMaxTaskRunnerNum());
			} else {
				log.info(feedBack.getErrorMessage());
				System.exit(0);
			}
		} else{	
			System.exit(0);
		}
		 
		
		// 启动监听agentPort
		AgentDaemonListener agentListener = new AgentDaemonListener(new Configuration(), agent.getAddress(), agent.getPort(), agent.getHandlerNum());
		agentListener.start();
		/*	*/
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
		
		TaskRunnerTimeOutThread taskRunnerTimeOutThread = new TaskRunnerTimeOutThread(taskRunnerTimeOut);
		taskRunnerTimeOutThread.start();
	
		
		AgentTaskManagerThread taskManagerThread = new AgentTaskManagerThread(taskRunnerTimeOut);
		taskManagerThread.start();
		
		
		if(startDataCollectionAgent == 1){
			// 检查文件线程启动
			FailRecordsUploadThread checkAgentRecordCollectFilesThread = new FailRecordsUploadThread(scanFailRecordsInterval);
			checkAgentRecordCollectFilesThread.start();
		}
		
		
		log.info("Start Agent Success !");
	}


	/**
	 * 客户端向服务器发送协议
	 * @param recordCollectWritable 打包协议
	 * @param isWriteToFile 失败后是否写入文件 
	 * 			true：写(TaskRunner发送过来的，失败写本地)		
	 * 			false：不写(扫描本地文件的时候，失败不写本地)
	 * @return
	 */
	public Writable sendRecordsToRecordCollectServer(RecordCollectWritable recordCollectWritable,boolean isWriteToFile){
		// 服务端返回信息
		int code = CodeStatus.succCode;
		String errorMessage = "";
		String strAgentInfo = "";
		FeedBackWritable writable = null;
		
		try {
			log.info("agent send data to server");
			writable = (FeedBackWritable)newDCSvrAgentClient.execute_proxy(ServiceName.AGENT_TRANSFER_RECORD.getName(), recordCollectWritable);
			// Agent向Server发送协议，如果发送失败则写到本地
			if(writable == null){
				code = CodeStatus.netErrCode;
				errorMessage = "NetError: send record to collect server failed";
				log.error(errorMessage);
				log.debug("isWriteToFile:" + isWriteToFile);
				if(isWriteToFile)
				{
					saveToFile(recordCollectWritable.businessName,recordCollectWritable.entityTypeName,recordCollectWritable.data,true);
				}
			}else{
				log.info("agent send data to server success");
				log.info("ErrorCode:" + writable.code + ",ErrorMessage:" + writable.errorMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("sendRecordsToRecordCollectServer failed",e);
		}
		
		writable = new FeedBackWritable(code,errorMessage,strAgentInfo);
		
		return writable;
	}
	
	/**
	 *  将数据保存到本地
	 * @param bussionName 业务名称
	 * @param entityTypeName 实体名称
	 * @param data 数据
	 * @param reName 文件大小超过规定是否重命名
	 */
	public  void saveToFile(String bussionName,String entityTypeName,String data,boolean reName){
		// 主文件夹 + 以业务名称命名的文件夹
		String filePath = saveFailRecordsPath + File.separator + bussionName;
		File file = new File(filePath);
		if(!file.exists()){
			log.info("create new folder:" + filePath);
			file.mkdirs();
		}
		// 遍历文件夹下的文件
		File[] files = FileUtils.listAllFiles(file, RpcAgentDaemon.fileType, false); 
		int length = 0;
		if(files != null){
			length = files.length;
		}
		// 检测目录中的文件个数是否超过最大个数
		if(length > maxFoldFileNum){
			log.info("set curTaskRunnerNum : 10000 ");
			curTaskRunnerNum = 10000;
		}
		
		String fileName = filePath  + File.separator + entityTypeName + ".txt";
		log.info("file name:" + fileName);
		long maxLength = maxFileSize*1024*1024;
		try {
			log.info("save fail records to file");
			File f = saveFile(fileName, data,true);
			log.info("save fail records to file success!");
			if(reName && f.length() > maxLength){
				log.info(f.getName() + "'s size is bigger than " + maxFileSize + "MB");
				String DATETIMESTAMP_FORMAT = "yyyyMMddHHmmss";
				SimpleDateFormat format = new SimpleDateFormat(DATETIMESTAMP_FORMAT);
				String time = format.format(new Date());
				String newFileName = filePath  + File.separator + "[" + entityTypeName + "]_" + time + ".txt";
				boolean flag = f.renameTo(new File(newFileName));
				log.info(f.getName() + " rename to " + newFileName + " :" + flag);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error("fail records save to local file failed!",e);
		}
	}

	/**
	 * 写文件
	 * @param fileName 文件名称
	 * @param text 写入内容
	 * @param append 追加到文件后面
	 * @return
	 * @throws IOException
	 */
	public static File saveFile(String fileName, String text ,boolean append) throws IOException {

		File f = new File(fileName);
		if (!f.exists())
		{
			f.createNewFile();
		}
		
		LockFile file = new LockFile(fileName);
		ReentrantReadWriteLock lock = file.getLock();
		log.info("add file write lock");
		lock.writeLock().lock();

		BufferedReader r = new BufferedReader(new StringReader(text));
		BufferedWriter w = new BufferedWriter(new FileWriter(f,append));
		try {
			String line = null;
			while ((line = r.readLine()) != null) {
				w.write(StringUtil.fromDefaultToUTF8(line));
				w.write("\n");
			}
			w.flush();
		}catch(IOException e){
			e.printStackTrace();
			throw e;
		}
		finally {
			w.close();
			r.close();
			log.info("delete file write lock");
			lock.writeLock().unlock();
		}
		return f;
	}
	
	/**
	 * 匹配文件名
	 * @param fileName
	 * @return
	 */
	public static boolean isMatch(String fileName){
		Pattern p = Pattern.compile("_\\d{14}");  
        Matcher m = p.matcher(fileName);
        return m.find();
	}
	
	public static void main(String[] args) {
		try {
			start(args);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
}

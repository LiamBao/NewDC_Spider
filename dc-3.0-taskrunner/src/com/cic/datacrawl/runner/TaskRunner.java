package com.cic.datacrawl.runner;

import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.cic.datacollection.rpc.protocol.TaskRunnerFinishedReportWritable;
import com.cic.datacollection.rpc.protocol.TaskRunnerHeartBeatWritable;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.ProcessMonitor;
import com.cic.datacrawl.core.StatusCode;
import com.cic.datacrawl.core.browser.RhinoBrowser;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.entity.EntitySaveManager;
import com.cic.datacrawl.core.initialize.InitializerRegister;
import com.cic.datacrawl.core.initialize.RunOnStartup;
import com.cic.datacrawl.core.initialize.RunOnStartupExecutor;
import com.cic.datacrawl.core.jsfunction.RhinoStandardFunction;
import com.cic.datacrawl.core.rhino.RhinoContext;
import com.cic.datacrawl.core.rhino.shell.Global;
import com.cic.datacrawl.core.rpc.ClientImpl;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ServiceName;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;
import com.cic.datacrawl.core.system.TaskRunnerConf;
import com.cic.datacrawl.core.util.FileUtils;
import com.cic.datacrawl.management.manager.SubTaskManager;

public class TaskRunner {

	private static final Logger LOG = Logger.getLogger(TaskRunner.class);
	private static int port;
	private static String address;
	public static long subTaskId;
	public static long startTime;
	public static long jsStartTime;
	public static int totalElementCount = 0;
	public static int downPageCount = 0;
	public static int heartBeatWaitTime;
	
	public static ClientImpl rpcClient;
	
	private static TaskRunner instance = new TaskRunner();

	private TaskRunner() {
	}

	public static TaskRunner getInstance() {
		if (instance == null) {
			instance = new TaskRunner();
		}
		return instance;
	}

	public static int getHeartBeatWaitTime() {
		return heartBeatWaitTime;
	}

	public static void setHeartBeatWaitTime(int heartBeatWaitTime) {
		TaskRunner.heartBeatWaitTime = heartBeatWaitTime;
	}

	public static ClientImpl getRpcClient() {
		return rpcClient;
	}

	public static void setRpcClient(ClientImpl rpcClient) {
		TaskRunner.rpcClient = rpcClient;
	}

	public static void closeRpcClient() {
		TaskRunner.rpcClient.close();
	}
	
	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		TaskRunner.port = port;
	}

	public static String getAddress() {
		return address;
	}

	public static void setAddress(String address) {
		TaskRunner.address = address;
	}

	public static long getSubTaskId() {
		return subTaskId;
	}

	public static void setSubTaskId(long subTaskId) {
		TaskRunner.subTaskId = subTaskId;
	}

	public static long getStartTime() {
		return startTime;
	}

	public static void setStartTime(long startTime) {
		TaskRunner.startTime = startTime;
	}

	public static long getJsStartTime() {
		return jsStartTime;
	}

	public static void setJsStartTime(long jsStartTime) {
		TaskRunner.jsStartTime = jsStartTime;
	}

	private static boolean remoteEnabled = false;

	public static boolean isRemoteEnabled() {
		return remoteEnabled;
	}

	public static void setRemoteEnabled(boolean remoteEnabled) {
		TaskRunner.remoteEnabled = remoteEnabled;
	}

	public static void main(String[] args) {
		LOG.info("抓取开始");
		try {
			TaskRunner.setStartTime(System.currentTimeMillis());
			start(args);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		LOG.info("抓取结束");
		System.exit(0);
	}

	public static void start(String[] args) throws Exception {
		if (System.getProperties().get("os.name").toString().toLowerCase().indexOf("linux") >= 0)
			System.setProperty("sun.awt.xembedserver", "true");
		LOG.debug(args);
		String path = null;
		boolean reflash = true;
		File scriptFile = null;
		String executeJSString = null;
		boolean useSnapShot = false;
		boolean closeSnapShot = false;
		byte agentType = 0;
		int turnPageWaitTime = 0;
		int siteId = 0;
		int agentSubgroupId = 0;
		long subtaskId = 0; // 子任务id号
		String address = "localhost";
		int port = 16001;
		int parseFlag = 0;
		String errorMessage = "null";
		// parse the arguments
		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("-h")) {
				System.out.println("Command Format: \n" + "\t%JAVA_HOME%\\BIN\\JAVA -jar homepageCatcher.jar "
						+ "[-d config path]\n" + "\t%JAVA_HOME%\\BIN\\JAVA -jar homepageCatcher.jar "
						+ "[-d config path_1;path_2;....;path_n]\n");
				System.exit(0);
			}
			// 加载自定义配置
			int index = ArrayUtils.indexOf(args, "-d");
			if (index >= 0) {
				try {
					path = args[index + 1];
					File f = new File(path);
					if (!f.exists()) {
						LOG.warn("Invalid path of configuration. " + "Using default configuration.");
					}
				} catch (Throwable e) {
					LOG.warn("Invalid path of configuration. " + "Using default configuration.");
				}
			}
			// 脚本文件
			index = ArrayUtils.indexOf(args, "-f");
			if (index >= 0) {
				try {
					String scriptFilePath = java.net.URLDecoder.decode(args[index + 1], "utf-8");
					scriptFile = new File(scriptFilePath);
					if (!scriptFile.exists() || !scriptFile.isFile()) {
						parseFlag = 1;
						LOG.error("Invalid script." + scriptFilePath);
						errorMessage = "Invalid script." + scriptFilePath;
					}
				} catch (Throwable e) {
					LOG.error("Script is undefined.");
					parseFlag = 1;
				}
			}
			// 脚本文件主函数
			index = ArrayUtils.indexOf(args, "-m");
			if (index >= 0) {
				try {
					executeJSString = java.net.URLDecoder.decode(args[index + 1], "utf-8");
					if (executeJSString.trim().startsWith("-")) {
						parseFlag = 1;
						LOG.warn("Invalid Main Function Name.");
						errorMessage = "Invalid Main Function Name.";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if (LOG.isDebugEnabled())
						LOG.debug("Invalid Main Function Name.");
				}
			}

			// 机器IP
			index = ArrayUtils.indexOf(args, "-a");
			if (index >= 0) {
				try {
					address = args[index + 1];
					if (address.trim().startsWith("-")) {
						parseFlag = 1;
						address = null;
						LOG.warn("Invalid address.");
						errorMessage = "Invalid address.";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if (LOG.isDebugEnabled())
						LOG.debug("Invalid address.");
				}
			}
			
			// subTaskId
			index = ArrayUtils.indexOf(args, "-i");
			if (index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						subtaskId = Long.parseLong(temp.trim());
						TaskRunner.setSubTaskId(subtaskId);
					} catch (NumberFormatException e) {
						parseFlag = 1;
						LOG.warn("Invalid taskRecordId.");
						errorMessage = "Invalid taskRecordId.";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if (LOG.isDebugEnabled())
						LOG.debug("Invalid Main Function Name.");
				}
			}

			// agent监听端口
			index = ArrayUtils.indexOf(args, "-p");
			if (index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						port = Integer.parseInt(temp.trim());
					} catch (NumberFormatException e) {
						parseFlag = 1;
						LOG.warn("Invalid port.");
						errorMessage = "Invalid port.";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if (LOG.isDebugEnabled())
						LOG.debug("Invalid port.");
				}
			}

			// 任务超时时间
			index = ArrayUtils.indexOf(args, "-t");
			if (index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						int waitTime = Integer.parseInt(temp.trim());
						TaskRunner.setHeartBeatWaitTime(((waitTime-1)/3000)*1000);
					} catch (NumberFormatException e) {
						parseFlag = 1;
						LOG.warn("Invalid HeartBeatWaitTime.");
						errorMessage = "Invalid HeartBeatWaitTime.";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if (LOG.isDebugEnabled())
						LOG.debug("Invalid HeartBeatWaitTime.");
				}
			}
			
			// agentType
			index = ArrayUtils.indexOf(args, "-c");
			if(index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						agentType = (byte)Integer.parseInt(temp.trim());
					} catch (NumberFormatException e1) {
						parseFlag = 1;
						LOG.warn("Invalid agentType value");
						errorMessage = "Invalid agentType value";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if(LOG.isDebugEnabled()) {
						LOG.debug("Invalid agentType value.");
					}
				}
			}
			
			//closeSnapShot
			index = ArrayUtils.indexOf(args, "-n");
			if(index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						int tmp = Integer.parseInt(temp.trim());
						if(tmp == 0) {
							closeSnapShot = false;
						} else {
							closeSnapShot = true;
						}
					} catch (NumberFormatException e1) {
						parseFlag = 1;
						LOG.warn("Invalid closeSnapShot value");
						errorMessage = "Invalid closeSnapShot value";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if(LOG.isDebugEnabled()) {
						LOG.debug("Invalid useSnapShot value.");
					}
				}
			}
			// useSnapShot
			index = ArrayUtils.indexOf(args, "-k");
			if(index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						int tmp = (byte)Integer.parseInt(temp.trim());
						if(tmp == 0) {
							useSnapShot = false;
						} else {
							useSnapShot = true;
						}
					} catch (NumberFormatException e1) {
						parseFlag = 1;
						LOG.warn("Invalid useSnapShot value");
						errorMessage = "Invalid useSnapShot value";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if(LOG.isDebugEnabled()) {
						LOG.debug("Invalid useSnapShot value.");
					}
				}
			}
			
			//turnPageWaitTime 翻页等待时间
			index = ArrayUtils.indexOf(args, "-w");
			if(index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						turnPageWaitTime = Integer.parseInt(temp.trim());
					} catch (NumberFormatException e1) {
						parseFlag = 1;
						LOG.warn("Invalid turnPageWaitTime value");
						errorMessage = "Invalid turnPageWaitTime value";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if(LOG.isDebugEnabled()) {
						LOG.debug("Invalid turnPageWaitTime value.");
					}
				}
			}
			
			// siteId
			index = ArrayUtils.indexOf(args, "-s");
			if(index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						siteId = Integer.parseInt(temp.trim());
					} catch (NumberFormatException e1) {
						parseFlag = 1;
						LOG.warn("Invalid siteId value");
						errorMessage = "Invalid siteId value";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if(LOG.isDebugEnabled()) {
						LOG.debug("Invalid siteId value.");
					}
				}
			}
			
			// agentSubGroupId
			index = ArrayUtils.indexOf(args, "-g");
			if(index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						agentSubgroupId = Integer.parseInt(temp.trim());
					} catch (NumberFormatException e1) {
						parseFlag = 1;
						LOG.warn("Invalid agentSubgroupId value");
						errorMessage = "Invalid agentSubgroupId value";
					}
				} catch (Throwable e) {
					parseFlag = 1;
					if(LOG.isDebugEnabled()) {
						LOG.debug("Invalid agentSubgroupId value.");
					}
				}
			}
		}

		int errorCode = -1;
		String errorUrl = "null";
		if (parseFlag == 1) {
			errorCode = SubTaskManager.SCRIPT_STATUS_CODE_ERROR_ARGUMRNT;// 表示在参数的时候出现问题
		} else {
			TaskRunner.setRpcClient(new ClientImpl(address, port));
			TaskRunner.setRemoteEnabled(true);
			
			TaskRunnerHeartBeatReportThread heartBeatThread = null;
			try {
				TaskRunner.setJsStartTime(System.currentTimeMillis());
				LOG.info(scriptFile);
				System.out.println(scriptFile);
				LOG.info(executeJSString);
				if (path == null || path.trim().length() == 0)
					path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";
				LOG.info("Config Path: \"" + path + "\"");
				if (isRemoteEnabled()) {
					heartBeatThread = new TaskRunnerHeartBeatReportThread(TaskRunner.getHeartBeatWaitTime());
					new Thread(heartBeatThread).start();
				}
				// 启动IOC容器
				// 启动配置管理程序
				// 装载默认配置文件
				ApplicationContext.initialiaze(path, reflash);
				InitializerRegister.getInstance().execute();
				System.setProperty("Task.Record.Id", "" + subtaskId);
				LOG.info("Execute in command line: \"" + (scriptFile == null ? "" : (scriptFile.getAbsoluteFile() + "."))
						+ ((executeJSString == null || executeJSString.trim().length() == 0) ? "" : executeJSString) + "\"");
				String jsFolder = FileUtils.getParentAbsolutePath(scriptFile);
				if (LOG.isDebugEnabled()) {
					LOG.debug("scriptFile: " + scriptFile);
					LOG.debug("JSFolder: " + jsFolder);
				}
				if (jsFolder != null && jsFolder.trim().length() > 0) {
					Config.setJSFolder(jsFolder);
				}
				startup(scriptFile, executeJSString, siteId, agentSubgroupId, agentType, closeSnapShot, useSnapShot, turnPageWaitTime);
			} catch (Throwable t) {
				LOG.error(t.getMessage(), t);
			} finally {
				LOG.debug("Script execute finished.");
			}
			
			errorCode = ProcessMonitor.getScriptStatus();
			if(ProcessMonitor.getScriptErrorMessage() == null) {
				errorMessage = "null";
			} else {
				errorMessage = ProcessMonitor.getScriptErrorMessage();
			}
			if(ProcessMonitor.getScriptErrorURL() == null) {
				errorUrl = "null";
			} else {
				errorUrl = ProcessMonitor.getScriptErrorURL();
			}
			
			if (isRemoteEnabled()) {
				heartBeatThread.stop();
			}
		}

		if (isRemoteEnabled()) {
			// 当任务执行完成时，向Agent上报状态，直到成功上报为止
			LOG.info("totalElementCount=" + totalElementCount);
			
			long curTime = System.currentTimeMillis();
			int exeTime = (int)(curTime - TaskRunner.getStartTime());
			int effectiveTime = (int)(curTime - TaskRunner.getJsStartTime());
			int effectiveTimeRate = (effectiveTime * 100)/exeTime;
			
			boolean flag = ReportFinished(subtaskId, errorCode, totalElementCount, downPageCount,
					errorMessage, errorUrl, startTime, exeTime, effectiveTimeRate);
			if(flag) {
				// 当有其他线程在执行中时候，可能会将整个进程退出而导致其他线程没有正确完成。
				LOG.info("Finished report for subTaskId: " + subTaskId + " success");
			} else {
				LOG.error("Finished report for subTaskId: " + subTaskId + " failed");
			}
		}
		
		TaskRunner.closeRpcClient();
	}

	public boolean sendHeartBeat() {
		boolean flag = false;
		LOG.info("Send HeartBeat...");
		TaskRunnerHeartBeatWritable heartBeatWritable = new TaskRunnerHeartBeatWritable(subTaskId);
		
		synchronized (rpcClient){
			FeedBackWritable feedBack = (FeedBackWritable) rpcClient.execute_proxy(ServiceName.TASK_RUNNER_HEART_BEAT.getName(), heartBeatWritable);
			if(feedBack != null) {
				if(feedBack.getCode() == CodeStatus.succCode) {
					flag = true;
				}
				LOG.info("Send HeartBeat success; " + feedBack.toString());
			} else {
				LOG.error("NetError: Send HeartBeat to Agent failed");
			}
		}
		
		return flag;
	}

	
	private static boolean ReportFinished(long subTaskId, int errCode, int scrapeCount, int downPageCount, String errMsg, String errUrl, long startTime, int exeTime, int effectiveTimeRate){
		TaskRunnerFinishedReportWritable taskStatus = new TaskRunnerFinishedReportWritable();
		taskStatus.setSubTaskId(subTaskId);
		taskStatus.setErrCode(errCode);
		taskStatus.setErrMsg(errMsg);
		taskStatus.setErrUrl(errUrl);
		taskStatus.setScrapeCount(scrapeCount);
		taskStatus.setDownPageCount(downPageCount);
		taskStatus.setStartTime(startTime);
		taskStatus.setExeTime(exeTime);
		taskStatus.setEffectiveTimeRate(effectiveTimeRate);
		
		boolean flag = false;
		int tryNum = 0;
		while(tryNum < 3) {
			synchronized (rpcClient){
				try{
					FeedBackWritable feedBack = (FeedBackWritable) rpcClient.execute_proxy(ServiceName.TASK_RUNNER_FINISH_REPORT.getName(), taskStatus);
					if(feedBack != null) {
						if(feedBack.getCode() == CodeStatus.succCode) {
							flag = true;
						}
					} else {
						LOG.error("NetError: Send Finished Report to Agent failed");
						LOG.error(taskStatus);
					}
				} catch (Exception e) {
					LOG.error(e.getMessage());
					flag = false;
				}
			}
			
			if(flag) {
				break;
			} else {
				++tryNum;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		return flag;
	}

	private static void startup(File scriptFile, String executeJSString, int siteId, int agentSubgroupId, byte agentType, boolean closeSnapShot, boolean useSnapShot, int turnPageWaitTime) throws IOException {
		String scriptBody = null;
		if (scriptFile != null)
			scriptBody = FileUtils.readFile(scriptFile, "UTF-8");
		
		TaskRunnerConf confBean = (TaskRunnerConf)ApplicationContext.getInstance().getBean("taskRunnerConf");
		
		String snapShotIp = confBean.getSnapShotIp();
		int snapShotPort = confBean.getSnapShotPort();
		String loginAccountIp = confBean.getLoginAccountIp();
		int loginAccountPort = confBean.getLoginAccountPort();
		
		LOG.info("vip params:: agentType:"+agentType+", closeSnapShot:"+closeSnapShot+", useSnapShot:"+useSnapShot+", siteId:"+siteId+","
				+ " agentSubgroupId:"+agentSubgroupId+", snapShotIp:"+snapShotIp+", snapShotPort:"+snapShotPort+", loginAccountIp:"+loginAccountIp+","
				+ " loginAccountPort:"+loginAccountPort+", turnPageWaitTime:"+turnPageWaitTime);
		DownLoader downloader = new DownLoader();
		downloader.init(agentType, closeSnapShot, useSnapShot, siteId, agentSubgroupId, snapShotIp, snapShotPort, loginAccountIp, loginAccountPort, turnPageWaitTime);
		
		RhinoContext rhinoContext = new RhinoContext(Global.getInstance());
		RhinoStandardFunction.rhinoContext = rhinoContext;
		//注册java对象，供javascript使用
		rhinoContext.putJavaObject("downloader", downloader, RhinoBrowser.class);
		if (scriptFile != null)
			rhinoContext.putJavaObject("currentScriptFile", scriptFile.getAbsolutePath(), String.class);
		if (executeJSString != null)
			rhinoContext.putJavaObject("currentScriptString", executeJSString, String.class);
		final RhinoContext finalRhinoContext = rhinoContext;
		((RunOnStartup) ApplicationContext.getInstance().getBean("runOnStartup")).execute(new RunOnStartupExecutor() {
			@Override
			public void execute(String scriptPath) throws Exception {
				String script = FileUtils.readFile(scriptPath, "UTF-8");
				finalRhinoContext.execute(script);
			}
		});
		try {
			if (scriptBody != null && scriptBody.trim().length() > 0) {
				rhinoContext.execute(scriptBody, scriptFile.getAbsolutePath());
			}

			if (executeJSString != null && executeJSString.length() > 0) {
				rhinoContext.execute(executeJSString);
				LOG.info("Execute " + executeJSString + " finished");
			}
			try {
				((EntitySaveManager) ApplicationContext.getInstance().getBean("saveManager")).commit();
				totalElementCount = ((EntitySaveManager) ApplicationContext.getInstance().getBean("saveManager"))
						.getTotalElementCount();
				
				downPageCount = downloader.getDownPageNum();
				LOG.info("Execute forceCommit finished");
			} catch (Exception e) {
				ProcessMonitor.failed(StatusCode.STATUS_TASKRUNNER_COMMIT_FAILED, e.getMessage(), downloader.getUrl());
				throw e;
			}
		} catch (Throwable t) {
			for (Throwable t1 = t; t1 != null; t1 = t1.getCause()) {
				LOG.error(t1.getMessage());
			}
		} finally {
			downloader.clear();
		}
	}
}

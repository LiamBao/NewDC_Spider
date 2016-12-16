package com.cic.datacrawl.runner;

import java.io.File;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.cic.datacollection.rpc.protocol.TaskRunnerFinishedReportWritable;
import com.cic.datacollection.rpc.protocol.TaskRunnerHeartBeatWritable;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.initialize.InitializerRegister;
import com.cic.datacrawl.core.loginaccount.LoginAccountManager;
import com.cic.datacrawl.core.rpc.ClientImpl;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ServiceName;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;

public class TaskRunnerManual {

	private static final Logger LOG = Logger.getLogger(TaskRunnerManual.class);
	public static TaskRunnerManual instance = new TaskRunnerManual();
	public static ClientImpl rpcClient;
	private static long subTaskId;
	private static String address;
	private static int port;
	private static long startTime;
	private static long jsStartTime;
	private static int waitTime;
	private static File scriptFile;
	private static String scriptMain;
	private static byte closeSnapShot;
	private static byte useSnapShot;
	private static byte agentType;
	private static int turnPageWaitTime;
	
	private TaskRunnerManual() {
	}
	
	public static TaskRunnerManual getInstance() {
		if (instance == null) {
			instance = new TaskRunnerManual();
		}
		return instance;
	}
	
	public static ClientImpl getRpcClient() {
		return rpcClient;
	}

	public static void setRpcClient(ClientImpl rpcClient) {
		TaskRunnerManual.rpcClient = rpcClient;
	}

	public static long getSubTaskId() {
		return subTaskId;
	}

	public static void setSubTaskId(long subTaskId) {
		TaskRunnerManual.subTaskId = subTaskId;
	}

	public static String getAddress() {
		return address;
	}

	public static void setAddress(String address) {
		TaskRunnerManual.address = address;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		TaskRunnerManual.port = port;
	}

	public static long getStartTime() {
		return startTime;
	}

	public static void setStartTime(long startTime) {
		TaskRunnerManual.startTime = startTime;
	}

	public static long getJsStartTime() {
		return jsStartTime;
	}

	public static void setJsStartTime(long jsStartTime) {
		TaskRunnerManual.jsStartTime = jsStartTime;
	}

	public static int getWaitTime() {
		return waitTime;
	}

	public static void setWaitTime(int waitTime) {
		TaskRunnerManual.waitTime = waitTime;
	}

	public static File getScriptFile() {
		return scriptFile;
	}

	public static void setScriptFile(File scriptFile) {
		TaskRunnerManual.scriptFile = scriptFile;
	}

	public static String getScriptMain() {
		return scriptMain;
	}

	public static void setScriptMain(String scriptMain) {
		TaskRunnerManual.scriptMain = scriptMain;
	}

	public static Logger getLog() {
		return LOG;
	}

	private static boolean parseArgs(String[] args) {
		boolean parseFlag = true;
		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("-h")) {
				System.out.println("Command Format: \n" + "\t%JAVA_HOME%\\BIN\\JAVA -jar homepageCatcher.jar "
						+ "[-d config path]\n" + "\t%JAVA_HOME%\\BIN\\JAVA -jar homepageCatcher.jar "
						+ "[-d config path_1;path_2;....;path_n]\n");
				
				return false;
			}
			
			String errorMessage = null;
			int index = ArrayUtils.indexOf(args, "-f");
			if (index >= 0) {
				try {
					String scriptFilePath = java.net.URLDecoder.decode(args[index + 1], "utf-8");
					scriptFile = new File(scriptFilePath);
//					if (!scriptFile.exists() || !scriptFile.isFile()) {
//						parseFlag = false;
//						LOG.error("Invalid script.");
//						errorMessage = "Invalid script.";
//					}
				} catch (Throwable e) {
					LOG.error("Script is undefined.");
					parseFlag = false;
				}
			}
			index = ArrayUtils.indexOf(args, "-s");
			if (index >= 0) {
				try {
					scriptMain = java.net.URLDecoder.decode(args[index + 1], "utf-8");
//					if (scriptMain.trim().startsWith("-")) {
//						parseFlag = false;
//						LOG.warn("Invalid Main Function Name.");
//						errorMessage = "Invalid Main Function Name.";
//					}
				} catch (Throwable e) {
					parseFlag = false;
					if (LOG.isDebugEnabled())
						LOG.debug("Invalid Main Function Name.");
				}
			}

			index = ArrayUtils.indexOf(args, "-a");
			if (index >= 0) {
				try {
					address = args[index + 1];
					if (address.trim().startsWith("-")) {
						parseFlag = false;
						address = null;
						LOG.warn("Invalid address.");
						errorMessage = "Invalid address.";
					}
				} catch (Throwable e) {
					parseFlag = false;
					if (LOG.isDebugEnabled())
						LOG.debug("Invalid address.");
				}
			}
			index = ArrayUtils.indexOf(args, "-i");
			if (index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						subTaskId = Long.parseLong(temp.trim());
						LOG.info("Start TaskRunner: " + subTaskId);
					} catch (Exception e) {
						parseFlag = false;
						LOG.warn("Invalid taskRecordId.");
						errorMessage = "Invalid taskRecordId.";
					}
				} catch (Throwable e) {
					parseFlag = false;
					if (LOG.isDebugEnabled())
						LOG.debug("Invalid Main Function Name.");
				}
			}

			index = ArrayUtils.indexOf(args, "-p");
			if (index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						port = Integer.parseInt(temp.trim());
					} catch (Exception e) {
						parseFlag = false;
						LOG.warn("Invalid port.");
						errorMessage = "Invalid port.";
					}
				} catch (Throwable e) {
					parseFlag = false;
					if (LOG.isDebugEnabled())
						LOG.debug("Invalid port.");
				}
			}

			index = ArrayUtils.indexOf(args, "-t");
			if (index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						int waitTime = Integer.parseInt(temp.trim());
						TaskRunnerManual.setWaitTime(((waitTime-1)/3000)*1000);
					} catch (Exception e) {
						parseFlag = false;
						LOG.warn("Invalid HeartBeatWaitTime.");
						errorMessage = "Invalid HeartBeatWaitTime.";
					}
				} catch (Throwable e) {
					parseFlag = false;
					if (LOG.isDebugEnabled())
						LOG.debug("Invalid HeartBeatWaitTime.");
				}
			}
			
			index = ArrayUtils.indexOf(args, "-c");
			if(index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						agentType = (byte)Integer.parseInt(temp.trim());
					} catch (NumberFormatException e1) {
						parseFlag = true;
						LOG.warn("Invalid agentType value");
						errorMessage = "Invalid agentType value";
					}
				} catch (Throwable e) {
					parseFlag = true;
					if(LOG.isDebugEnabled()) {
						LOG.debug("Invalid agentType value.");
					}
				}
			}
			
			index = ArrayUtils.indexOf(args, "-n");
			if(index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						closeSnapShot = (byte)Integer.parseInt(temp.trim());
					} catch (NumberFormatException e1) {
						parseFlag = true;
						LOG.warn("Invalid closeSnapShot value");
						errorMessage = "Invalid closeSnapShot value";
					}
				} catch (Throwable e) {
					parseFlag = true;
					if(LOG.isDebugEnabled()) {
						LOG.debug("Invalid closeSnapShot value.");
					}
				}
			}
			
			index = ArrayUtils.indexOf(args, "-k");
			if(index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						useSnapShot = (byte)Integer.parseInt(temp.trim());
					} catch (NumberFormatException e1) {
						parseFlag = true;
						LOG.warn("Invalid useSnapShot value");
						errorMessage = "Invalid useSnapShot value";
					}
				} catch (Throwable e) {
					parseFlag = true;
					if(LOG.isDebugEnabled()) {
						LOG.debug("Invalid useSnapShot value.");
					}
				}
			}
			
			index = ArrayUtils.indexOf(args, "-w");
			if(index >= 0) {
				try {
					String temp = args[index + 1];
					try {
						turnPageWaitTime = Integer.parseInt(temp.trim());
					} catch (NumberFormatException e1) {
						parseFlag = true;
						LOG.warn("Invalid turnPageWaitTime value");
						errorMessage = "Invalid turnPageWaitTime value";
					}
				} catch (Throwable e) {
					parseFlag = true;
					if(LOG.isDebugEnabled()) {
						LOG.debug("Invalid turnPageWaitTime value.");
					}
				}
			}
			
			LOG.info(errorMessage);
		}
		
		return parseFlag;
	}
	
	private static void startUp() {
		int randTime = 10000;
		
		LOG.info("Begin Execute TaskRunner: " + subTaskId);
		
		LOG.info(address + ":" + port + " , " + subTaskId + " , " + waitTime + " , "+ scriptFile.getPath() + " , " + scriptMain);
		
		String path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";
		LOG.info("Config Path: \"" + path + "\"");

		// 启动IOC容器
		// 启动配置管理程序
		// 装载默认配置文件
		ApplicationContext.initialiaze(path, true);
		InitializerRegister.getInstance().execute();
		LOG.info("Load IOC and configfile for TaskRunner: " + subTaskId);
		
		LoginAccountManager loginAccountManager = new LoginAccountManager();
		loginAccountManager.init(1, 1, "192.168.0.242", 16003);
		if(loginAccountManager.allocateAccount()){
			System.out.println(loginAccountManager.toString());
			loginAccountManager.returnAccount();
		}
		
/*		TaskRunnerManual.setRpcClient(new ClientImpl(address, port));
		LOG.info("Create RPC Client for TaskRunner: " + subTaskId);
		
		TaskRunnerManual.setJsStartTime(System.currentTimeMillis());
		
		TaskRunnerHeartBeatReportThread heartBeatThread = null;
		heartBeatThread = new TaskRunnerHeartBeatReportThread(waitTime);
		new Thread(heartBeatThread).start();
		
		LOG.info("Running TaskRunner: " + subTaskId);
		
		try {
			Thread.sleep(randTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		heartBeatThread.stop();

		LOG.info("End Execute TaskRunner: " + subTaskId);
		long curTime = System.currentTimeMillis();
		int exeTime = (int)(curTime - TaskRunnerManual.getStartTime());
		int effectiveTime = (int)(curTime - TaskRunnerManual.getJsStartTime());
		int effectiveTimeRate = (effectiveTime * 100)/exeTime;
		if(!ReportFinished(subTaskId, 0, 10, 100,"", "", startTime, exeTime, effectiveTimeRate)) {
			LOG.error("Send Finished Report failed");
		}
*/
		
		LOG.info("Finished TaskRunner : " + subTaskId);
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
	
	private static boolean ReportFinished(long subTaskId, int errCode, int downPageCount, int scrapeCount,String errMsg, String errUrl, long startTime, int exeTime, int effectiveTimeRate){
		TaskRunnerFinishedReportWritable taskStatus = new TaskRunnerFinishedReportWritable();
		taskStatus.setSubTaskId(subTaskId);
		taskStatus.setErrCode(errCode);
		taskStatus.setErrMsg(errMsg);
		taskStatus.setErrUrl(errUrl);
		taskStatus.setDownPageCount(downPageCount);
		taskStatus.setScrapeCount(scrapeCount);
		taskStatus.setStartTime(startTime);
		taskStatus.setExeTime(exeTime);
		taskStatus.setEffectiveTimeRate(effectiveTimeRate);
		
		boolean flag = false;
		int tryNum = 0;
		LOG.info("Send finished report to agent for subTaskId: " + subTaskId);
		while(tryNum < 3) {
			try {
				synchronized (rpcClient){
					FeedBackWritable feedBack = (FeedBackWritable) rpcClient.execute_proxy(ServiceName.TASK_RUNNER_FINISH_REPORT.getName(), taskStatus);
					if(feedBack != null) {
						if(feedBack.getCode() == CodeStatus.succCode) {
							flag = true;
						}
					} else {
						LOG.error("NetError: Send Finished Report to Agent failed");
						LOG.error(taskStatus);
					}
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				flag = false;
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
	
	public static void main(String[] args) {
		TaskRunnerManual.setStartTime(System.currentTimeMillis());
		if(!parseArgs(args)) {
			System.exit(-1);
		}
		try {
			startUp();
		} catch (Throwable e) {
			LOG.error("TaskRunnerManual is error", e);
			e.printStackTrace();
		}
		LOG.info("exit");
		System.exit(0);
	}
}

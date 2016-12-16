package com.cic.datacrawl.control.taskmanager;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import com.cic.datacrawl.control.taskmanager.split.Spliter;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.initialize.InitializerRegister;
import com.cic.datacrawl.control.cache.TaskSpliterCache;
import com.cic.datacrawl.management.manager.TaskGroupManager;

public class TaskSpliter {

	private static final Logger LOG = Logger.getLogger(TaskSpliter.class);
	
	public static void main(String[] args) {
		LOG.info("TaskSpliter register to T_SPLITER_GROUP");
		
		LOG.info("TaskSpliter start==============================================");		
		try{
			start(args);
		}catch(Exception e){
			LOG.error(e.getMessage(), e);
		}
		LOG.info("TaskSpliter finished===========================================");		
	
		System.exit(0);
	}
	
	
	/**
	 * 拆分的具体逻辑
	 * @param args
	 * @throws Exception
	 */
	public static int start(String[] args) throws Exception{
		if (System.getProperties().get("os.name").toString().toLowerCase().indexOf("linux") >= 0){
			System.setProperty("sun.awt.xembedserver", "true");
		}

		// 初始化Spring配置
		String path = null;
		int taskGroupId = 0;
		if (args != null && args.length > 1) {
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
			index = ArrayUtils.indexOf(args, "-g");
			if(index >= 0){
				taskGroupId = Integer.parseInt(args[index + 1]);
			} else {
				LOG.error("Command Format: \n"
						+ "-g taskGroupId\n");
				return -1;   //输入参数中必须指明taskGroupId
			}
		} else {
			LOG.error("Command Format: \n"
					+ "-g taskGroupId\n");
			return -1;   //输入参数中必须指明taskGroupId
		}
		
		if (path == null || path.trim().length() == 0){
			path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";
		}
		LOG.info("Start Split TaskGroup : " + taskGroupId);
		LOG.debug("Config Path: \"" + path + "\"");
		ApplicationContext.initialiaze(path, true);
		InitializerRegister.getInstance().execute();
		
		//互斥判断
		if(!lockTaskSpliter(taskGroupId, 7200)) {
			LOG.error("Add Mutex-Lock for TaskGroup: " + taskGroupId + " failed");
			LOG.error("Another TaskSpliter is running for TaskGroup: " + taskGroupId);
			return -1;
		} else {
			LOG.info("Add Mutex-Lock for TaskGroup: " + taskGroupId + " success");
		}
			
		try{
			//Spliter register
			registerSpliter(taskGroupId);
			
			Spliter spliter = (Spliter) ApplicationContext.getInstance().getBean("spliter");
			spliter.setTaskGroupId(taskGroupId);
			spliter.doSplit();//拆分任务
			
			finishedSpliter(taskGroupId, (byte) 0, new String("Success"));
		} catch (Exception e) {
			finishedSpliter(taskGroupId, (byte) 1, e.getMessage());
			LOG.warn(e.getMessage());
		}
		
		//清除互斥锁
		if(unlockTaskSpliter(taskGroupId)) {
			LOG.info("Del Mutex-Lock for taskGroup: " + taskGroupId + "  success");
		} else {
			LOG.error("Del Mutex-Lock for taskGroup: " + taskGroupId + "  failed");
		}
		
		return 0;
	}
	
	/**
	 * 加互斥锁
	 * @param taskSpliterGroupId
	 * @return
	 */
	public static boolean lockTaskSpliter(int taskSpliterGroupId, int lockWaitTime) {		
		return new TaskSpliterCache().lockTaskSpliter(taskSpliterGroupId, lockWaitTime);
	}
	
	/**
	 * 清除互斥锁
	 * @param taskSpliterGroupId
	 * @return
	 */
	public static boolean unlockTaskSpliter(int taskSpliterGroupId) {		
		return new TaskSpliterCache().unlockTaskSpliter(taskSpliterGroupId);
	}
	
	/**
	 * @desc register the Spliter to T_SPLITER_GROUP with lanIP & taskGroupId
	 * @param taskGroupId
	 * @throws UnknownHostException
	 */
	
	public static void registerSpliter (int taskGroupId) { 
	    String lanIP = new String("127.0.0.1");
	    String tmpIP = null;
	    try {   
	    	Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();   
	        while (netInterfaces.hasMoreElements()) {   
	            NetworkInterface netInterface = netInterfaces.nextElement();   
	            if(netInterface.getName().equalsIgnoreCase("eth0")) {
		            Enumeration<InetAddress> ips = netInterface.getInetAddresses();   
		            while(ips.hasMoreElements()) {   
		            	tmpIP = ips.nextElement().getHostAddress();
		            	if(tmpIP.matches("[0-9]+.[0-9]+.[0-9]+.[0-9]+")) {
		            		lanIP = tmpIP;
		            		break;
		            	}
		            } 
		            break;
	            }
	        }
	    } catch (Exception e) {   
	        LOG.warn(e.getMessage()); 
	    }  
		
		TaskGroupManager taskGroupManager = (TaskGroupManager) ApplicationContext.getInstance().getBean("taskGroupManager");
		taskGroupManager.registerSpliter(taskGroupId, lanIP);
	}
	
	public static void finishedSpliter (int taskGroupId, byte errorFlag, String exception) {
		TaskGroupManager taskGroupManager = (TaskGroupManager) ApplicationContext.getInstance().getBean("taskGroupManager");
		taskGroupManager.finishedSpliter(taskGroupId, errorFlag, exception);
	}

}

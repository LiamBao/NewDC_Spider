package com.cic.datacollection.anew;

import org.apache.log4j.Logger;

import com.cic.datacollection.bean.TaskRunnerInfo;

public class AgentTaskManagerThread extends Thread{
	private static final Logger log = Logger.getLogger(AgentTaskManagerThread.class);
	private int taskRunnerTimeOut;
	
	public AgentTaskManagerThread(int taskRunnerTimeOut) {
		this.taskRunnerTimeOut = taskRunnerTimeOut;
	}
	
	@SuppressWarnings("static-access")
	private boolean startTaskRunner(TaskRunnerInfo taskRunnerInfo) {
		boolean flag = false;
		RpcAgentDaemon agent = RpcAgentDaemon.getInstance();
		String address = agent.getAddress();
		int port = agent.getPort();
		int closeSnapShot = agent.getClosePageSnapShot();
		int siteId = taskRunnerInfo.getSiteId();
		long subtaskId = taskRunnerInfo.getSubTaskId();// get subtaskId
		byte agentType = taskRunnerInfo.getAgentType();
		byte useSnapShot = closeSnapShot==0?taskRunnerInfo.getUseSnapShot():0;
		int turnPageWaitTime = taskRunnerInfo.getTurnPageWaitTime();
		int agentSubGroupId = agent.getAgentSubgroupId();
		
		String taskScriptFile = "";
		String taskScriptMain = "";
		try {
			taskScriptFile = java.net.URLEncoder.encode(taskRunnerInfo.getScriptFile(), "utf-8");
			taskScriptMain = java.net.URLEncoder.encode(taskRunnerInfo.getScriptMain(), "utf-8");
		} catch (Exception e) {
			log.error("URL ENCODE ERROR " ,e);
			taskScriptFile = taskRunnerInfo.getScriptFile();
			taskScriptMain = taskRunnerInfo.getScriptMain();
		}
		long time = System.currentTimeMillis();
		ProcessBuilder processBuilder = new ProcessBuilder("/home/dc_opr/NewDCEngine/DataCrawl/AgentDaemon/DCTaskRunner.sh","-i",""+subtaskId,"-a",address,"-p",""+port,"-t",""+taskRunnerTimeOut,"-f",""+taskScriptFile,"-m",""+taskScriptMain,"-c",""+agentType,"-n",""+closeSnapShot,"-k",""+useSnapShot,"-w",""+turnPageWaitTime,"-s",""+siteId,"-g",""+agentSubGroupId);
		log.info("Execute Command："+"/home/dc_opr/NewDCEngine/DataCrawl/AgentDaemon/DCTaskRunner.sh"+" -i "+subtaskId+" -a "+address+" -p "+port+" -t "+taskRunnerTimeOut+" -f "+taskScriptFile+" -m "+taskScriptMain+" -c "+agentType+" -n "+closeSnapShot+" -k "+useSnapShot+" -w "+turnPageWaitTime + " -s "+siteId + " -g "+agentSubGroupId);
		Process process = null;
		try {
			process = processBuilder.start();
			log.info("Start TaskRunner at StartTime:" + time + " for subtaskId:" + subtaskId);
			process.waitFor();
			log.info("Start TaskRunner for SubtaskId: " + subtaskId + " success, CostTime: " + (System.currentTimeMillis()-time));
			flag = true;
		} catch (Exception e1) {
			log.error("Start TaskRunner for SubtaskId: " + subtaskId + " failed, CostTime: " + (System.currentTimeMillis()-time));
			log.error("/home/dc_opr/NewDCEngine/DataCrawl/AgentDaemon/DCTaskRunner.sh"+" -i "+subtaskId+" -a "+address+" -p "+port+" -t "+taskRunnerTimeOut+" -f "+taskScriptFile+" -m "+taskScriptMain+" -c "+agentType+" -k "+useSnapShot+" -w "+turnPageWaitTime+" -s "+siteId+" -g "+agentSubGroupId, e1);
		}finally{
			if(process != null){
				process.destroy();
			}
		}
		
		return flag;
	}
	
	@Override
	public void run(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		RpcAgentDaemon agent = RpcAgentDaemon.getInstance();
		long lastCheckTime = System.currentTimeMillis();
		
		while(true) {
			if(agent.needGetNewTask()) {
				TaskRunnerInfo taskRunnerInfo = agent.getTaskFromControlServer();
				if(null == taskRunnerInfo) {
					//没有可以分配给该Agent的任务
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					log.info("Get TaskRunner: " + taskRunnerInfo.getSubTaskId() + " from Scheduler");
					if(agent.taskRunnerIsExist(taskRunnerInfo.getSubTaskId())) {
						log.info("The TaskRunner for subTaskId " + taskRunnerInfo.getSubTaskId() + " is exist");
					} else {
						int tryNum = 1;
						boolean flag = startTaskRunner(taskRunnerInfo);
						while(tryNum < 2 && !flag) {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							++tryNum;
							flag = startTaskRunner(taskRunnerInfo);
						}
						if(flag) {
							taskRunnerInfo.setHeartBeatTime(System.currentTimeMillis());
							agent.addTaskRunnerInfo(taskRunnerInfo);
						}
					}
				}
				
				//两次任务请求间隔2s
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(System.currentTimeMillis() - lastCheckTime > 10000) {
				agent.checkSubTaskConsistency();
				lastCheckTime = System.currentTimeMillis();
			}
		}
	}
	
}

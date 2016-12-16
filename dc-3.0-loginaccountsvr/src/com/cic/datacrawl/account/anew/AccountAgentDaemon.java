package com.cic.datacrawl.account.anew;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.cic.datacrawl.account.rpc.protocol.ResTaskRunnerReqAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerReqAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerReturnAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerUpdateAccountCookieWritable;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.rpc.ClientImpl;
import com.cic.datacrawl.core.rpc.ServiceName;


/**
 * 记录收集Agent
 * 
 * @author charles.chen
 *
 */
public class AccountAgentDaemon {
	
	private static final Logger log = Logger.getLogger(AccountAgentDaemon.class);
	private static AccountAgentDaemon agent = new AccountAgentDaemon();
	private static String serverDaemonAddress;
	private static int serverDaemonPort;
	
	private static ClientImpl agentClient;
	
	private static String address;
	
	
	
	public static String getServerDaemonAddress() {
		return serverDaemonAddress;
	}
	public static void setServerDaemonAddress(String serverDaemonAddress) {
		AccountAgentDaemon.serverDaemonAddress = serverDaemonAddress;
	}
	public static AccountAgentDaemon getInstance() {
		return agent;
	}
	
	
	
	public static AccountAgentDaemon getAgent() {
		return agent;
	}
	public static void setAgent(AccountAgentDaemon agent) {
		AccountAgentDaemon.agent = agent;
	}
	public static int getServerDaemonPort() {
		return serverDaemonPort;
	}
	public static void setServerDaemonPort(int serverDaemonPort) {
		AccountAgentDaemon.serverDaemonPort = serverDaemonPort;
	}
	public static String getAddress() {
		return address;
	}
	public static void setAddress(String address) {
		AccountAgentDaemon.address = address;
	}
	public static ClientImpl getAgentClient() {
		return agentClient;
	}
	public static void setAgentClient(ClientImpl agentClient) {
		AccountAgentDaemon.agentClient = agentClient;
	}
	
	@SuppressWarnings("static-access")
	private static void start(String[] args) throws Exception {
		AccountAgentDaemon agent = AccountAgentDaemon.getInstance();		
		String path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";
		ApplicationContext.initialiaze(path, true);
		Configuration conf = new Configuration();
		conf.addResource(new Path("conf/conf.xml"));
		// 设置ServerDaemon的信息
		String centerAddress = conf.get("account.center.address", "0.0.0.0");
		agent.setServerDaemonAddress(centerAddress);
		agent.setAddress(centerAddress);
		int centerPort = conf.getInt("account.center.port", 16003);
		
		// 建立和服务器的连接
		agent.setAgentClient(new ClientImpl(agent.getServerDaemonAddress(), centerPort));
		log.info("Start Agent Success !");
	}
	
	static HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
	static List<String> list = new ArrayList<String>();
	public static void testAllocateAccount(int siteId,int subGroupId,int share){
		TaskRunnerReqAccountWritable writable = new TaskRunnerReqAccountWritable();
		writable.siteId = siteId;
		writable.agentSubgroupId = subGroupId;
		writable.isShare = share;
		
		ResTaskRunnerReqAccountWritable s = (ResTaskRunnerReqAccountWritable)agentClient.execute_proxy(ServiceName.TASK_RUNNER_REQUEST_ACCOUNT.getName(), writable);
		
		System.out.println("得到的账号信息：" + s.toString());
		Set<Integer> keys = map.keySet();
		int count = 0;
		if(keys.contains(s.accountId)){
			count = map.get(s.accountId);
		}
		map.put(s.accountId, ++count);
		
		if(s.accountId != 0){
			list.add(s.toString());
		}
		
		if(writable.isShare == 1 && s.accountId != 0 && s.isAutoLogin == 1){
			TaskRunnerUpdateAccountCookieWritable writable2 = new TaskRunnerUpdateAccountCookieWritable();
			writable2.accountId = s.accountId;
			writable2.agentSubgroupId = writable.agentSubgroupId;
			writable2.cookie = "2014年5月8日10:11:29";
			//writable2.isAutoLogin = s.isAutoLogin;
			writable2.siteId = writable.siteId;
			///writable2.status = 1;
			
			agentClient.execute_proxy(ServiceName.TASK_RUNNER_UPDATE_ACCOUNT.getName(), writable2);
		}
		
	}
		
	
	public static void returnAccount(int accountId,int siteId,int subGroupId,int isShare,int status){
		
		TaskRunnerReturnAccountWritable writable3 = new TaskRunnerReturnAccountWritable();
		writable3.accountId = accountId;
		writable3.agentSubgroupId = subGroupId;
		writable3.siteId = siteId;
		writable3.isShare = isShare;
		writable3.status = status;
		agentClient.execute_proxy(ServiceName.TASK_RUNNER_RETURN_ACCOUNT.getName(), writable3);
		
	}
	
	public static void main(String[] args) {
		try {
			start(args);
			
			
//				AccountServerDaemon.getInstance().clearAllCache(i,30);
//				AccountServerDaemon.getInstance().clearAllCache(1,20);
//				AccountServerDaemon.getInstance().clearAllCache(1,30);
//				AccountServerDaemon.getInstance().clearAllCache(1,40);
			
			
			/*AccountServerDaemon.getInstance().getSiteAccountInfo(1);
			AccountServerDaemon.getInstance().getSiteSubgroupAccountInfo(1, 10);
			AccountServerDaemon.getInstance().getSiteSubgroupAccountInfo(1, 20);
			AccountServerDaemon.getInstance().getSiteSubgroupAccountInfo(1, 30);
			AccountServerDaemon.getInstance().getSiteSubgroupAccountInfo(1, 40);
			AccountServerDaemon.getInstance().getWaitClearAccountInfo(1);
			*/
			
			//AccountServerDaemon.getInstance().addSiteAccountInfoToCache();
			
			int site = 27;
			
			getInfo(site);

			/*site = 3;
			getInfo(site);
			
			site = 4;
			getInfo(site);

			
			site = 9;
			getInfo(site);
			*/
			
			//returnAccount(6, site, 10, 1, 2);
			//returnAccount(7, site, 10, 1, 1);
			//returnAccount(6, site, 10, 1, 1);
			//testAllocateAccount(site,10,1);
			//testAllocateAccount(site,10,1);
			
			//AccountServerDaemon.getInstance().clearAllCache(10);
			//getInfo(site);
			
			//System.out.println(list.size());
			//System.out.println(map.toString());
			
			//System.exit(0);
			
			
			/*
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(true){
						AccountServerDaemon server = AccountServerDaemon.getInstance();
						server.addSiteAccountInfoToCache();
						getInfo(9);
						try {
							log.info("database  Checker sleep " + 10000 + " ms...");
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			
			*/
			
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public static void getInfo(int site){
		AccountServerDaemon.getInstance().getSiteAccountInfo(site);
		AccountServerDaemon.getInstance().getSiteSubgroupAccountInfo(site, 10);
		AccountServerDaemon.getInstance().getSiteSubgroupAccountInfo(site, 20);
		AccountServerDaemon.getInstance().getSiteSubgroupAccountInfo(site, 30);
		AccountServerDaemon.getInstance().getSiteSubgroupAccountInfo(site, 40);
		AccountServerDaemon.getInstance().getWaitClearAccountInfo(site);
	}
	
}

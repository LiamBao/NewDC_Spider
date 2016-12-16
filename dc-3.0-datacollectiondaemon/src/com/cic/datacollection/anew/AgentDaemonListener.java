package com.cic.datacollection.anew;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.log4j.Logger;

import com.cic.datacrawl.core.rpc.ServerImpl;


public class AgentDaemonListener extends Thread{
	
	private static final Logger log = Logger.getLogger(AgentDaemonListener.class);
	public Configuration conf;
	public String address;
	public int port;
	public int handlerNum;
	
	public AgentDaemonListener(Configuration conf, String address, int port, int handlerNum){
		this.conf = conf;
		this.address = address;
		this.port = port;
		this.handlerNum = handlerNum;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run(){
		
		ServerImpl Agent = new ServerImpl();
		try{
			org.apache.hadoop.ipc.Server server = RPC.getServer(Agent, address, port, handlerNum, false, conf);
			server.start();
			server.join();
		}catch (Exception e) {
			log.error("监听AgentDaemonListener出错：",e);
		}
	}

}

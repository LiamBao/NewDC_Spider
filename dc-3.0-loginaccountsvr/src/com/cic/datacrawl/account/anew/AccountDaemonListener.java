package com.cic.datacrawl.account.anew;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.log4j.Logger;

import com.cic.datacrawl.core.rpc.ServerImpl;

public class AccountDaemonListener extends Thread{
	
	private static final Logger log = Logger.getLogger(AccountDaemonListener.class);
	
	private Configuration conf;
	public String address;
	public int port;
	public int numHandlers;
	
	public AccountDaemonListener(Configuration conf, String address, int port, int numHandlers){
		this.conf = conf;
		this.address = address;
		this.port = port;
		this.numHandlers = numHandlers;
	}
		
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		ServerImpl controlCenter = new ServerImpl();
		try{
			org.apache.hadoop.ipc.Server server = RPC.getServer(controlCenter, address, port, numHandlers, false, conf);
			server.start();
			server.join();
		}catch(IOException e){
			log.error("IO error", e);
			System.exit(-1);
		} catch (InterruptedException e) {
			log.error("Interrupted", e);
			System.exit(-1);
		}
	}

}

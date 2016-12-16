package com.cic.datacrawl.account.anew;

import org.apache.log4j.Logger;


/**
 * 扫描数据库
 * @author charles.chen
 *
 */
public class ScanDatabaseThread extends Thread{
	private static final Logger log = Logger.getLogger(ScanDatabaseThread.class);
	
	private int waitTime;
	
	public ScanDatabaseThread(int waitTime) {
		this.waitTime = waitTime;
	}
	
	
	@Override
	public void run() {
		while(true) {
			AccountServerDaemon server = AccountServerDaemon.getInstance();
			server.addSiteAccountInfoToCache();
			
			try {
				log.info("database  Checker sleep " + waitTime + " ms...");
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
	}
}

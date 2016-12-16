package com.cic.datacrawl.account.anew;

import org.apache.log4j.Logger;


/**
 * 扫描站点账号列表线程
 * @author charles.chen
 *
 */
public class ScanSiteCookieListThread extends Thread{
	private static final Logger log = Logger.getLogger(ScanSiteCookieListThread.class);
	
	private int waitTime;
	
	public ScanSiteCookieListThread(int waitTime) {
		this.waitTime = waitTime;
	}
	
	
	@Override
	public void run() {
		while(true) {
			AccountServerDaemon server = AccountServerDaemon.getInstance();
			server.scanSiteCookieList();
			
			try {
				log.info("SiteCookieList  Checker sleep " + waitTime + " ms...");
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
	}
}

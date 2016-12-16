package com.cic.datacrawl.account.anew;

import org.apache.log4j.Logger;


/**
 * 扫描待清理站点账号列表线程
 * @author charles.chen
 *
 */
public class ScanWaitClearSiteCookieListThread extends Thread{
	private static final Logger log = Logger.getLogger(ScanWaitClearSiteCookieListThread.class);
	
	private int waitTime;
	
	public ScanWaitClearSiteCookieListThread(int waitTime) {
		this.waitTime = waitTime;
	}
	
	
	
	
	@Override
	public void run() {
		while(true) {
			AccountServerDaemon server = AccountServerDaemon.getInstance();
			server.scanWaitClearSiteCookieList();
			try {
				log.info("WaitClearSiteCookieList  Checker sleep " + waitTime + " ms...");
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
	}
}

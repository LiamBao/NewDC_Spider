package com.cic.datacollection.anew;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.cic.datacollection.util.LockFile;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;
import com.cic.datacrawl.core.rpc.protocol.RecordCollectWritable;
import com.cic.datacrawl.core.util.FileUtils;

/**
 * 
 * 扫描本地，将上传失败的记录信息重新传到server
 * 
 * @author charles.chen
 *
 */
public class FailRecordsUploadThread extends Thread{
	private static final Logger log = Logger.getLogger(FailRecordsUploadThread.class);
	
	private int waitTime;
	
	public FailRecordsUploadThread(int waitTime) {
		this.waitTime = waitTime;
	}
	
	private RpcAgentDaemon agent = RpcAgentDaemon.getInstance();
	private FeedBackWritable writable = null;
	@Override
	public void run() {
		while(true) {
			
			if(!RpcAgentDaemon.getNewDCSvrAgentClient().isConnected()){
				log.info("retry to connect server...");
				RpcAgentDaemon.getNewDCSvrAgentClient().retryToConnectServer();
			}
			
			if(!RpcAgentDaemon.getNewDCSvrAgentClient().isConnected()){
             	log.warn("the server is shut down!");
            }else{
				File fold = new File(RpcAgentDaemon.saveFailRecordsPath);
				if(!fold.exists()){
					log.warn("can not find failRecords folder!");
				}else{
					// 递归查找所有文件
					File[] files = FileUtils.listAllFiles(fold, RpcAgentDaemon.fileType, true);
					int count = 0;
					if(files != null && files.length > 0){
						count = files.length;
						log.debug("failRecords files size:" + count);
						FileUtils.orderByDate(files,false);
						for(File file:files){
							
							if(!file.exists()){
								log.warn("file :" + file.getName() + " is not exists");
								continue;
							}
							
							LockFile lockFile = new LockFile(file.getPath());
							ReentrantReadWriteLock lock = lockFile.getLock();
							log.info("add file read lock");
							lock.readLock().lock();
							
							String fileName = file.getName();
							int index = fileName.lastIndexOf(".");
							String entry = fileName.substring(0, index);
							String entityTypeName = "";
							
							String businessName = file.getParentFile().getName();
							// 保存上传失败的记录
				            List<String> failRecord = new ArrayList<String>();
				            // 记录文件有多少行
				            int lineCount = 0;
				            
							try {
								boolean match = RpcAgentDaemon.isMatch(fileName);
								// 不是重命名的文件
								if(!match)
								{
									entityTypeName = entry;
								}else{
									// 是重命名的文件
									String ss = entry.split("]")[0].substring(1);
									entityTypeName = ss;
								}
								
						        BufferedReader reader = new BufferedReader(new FileReader(file));
					            String tempString = null;
					            
					            // 一次读入一行，直到读入null为文件结束
					            while ((tempString = reader.readLine()) != null) {
					                if(!tempString.equals("")){
					                	lineCount++;
						                RecordCollectWritable recordCollectWritable = new RecordCollectWritable(entityTypeName,tempString,businessName);
						                writable = (FeedBackWritable)agent.sendRecordsToRecordCollectServer(recordCollectWritable,false);
						                if(writable == null || writable.code != CodeStatus.succCode){
						                	failRecord.add(tempString);
											log.error(" send records to server failed!");
										}
					                }
					            }
					            reader.close();
							} catch (IOException e) {
								e.printStackTrace();
								log.error("scan local files failed!",e);
							}finally{
								// 是否将失败的记录写到文件
								boolean isWrite = false;
								// 有记录上传至服务器，删除文件，讲上传失败的记录再次写入文件
					            if(failRecord.isEmpty() || failRecord.size() < lineCount){
					            	 boolean flag = file.delete();
							         log.info(file.getName() + " is delete:" + flag);
							         // 文件个数减一
							         count--;
							         isWrite = true;
					            }
					            log.info("delete file read lock");
					            lock.readLock().unlock();
					            
					            if(isWrite){
					            	log.info("write failed records to file,failRecord size:" + failRecord.size());
					            	 // 将失败记录重新写入文件中
						            for(String strs:failRecord)
							        {
						            	agent.saveToFile(businessName, entry, strs, false);
							        }
					            }
							}
						}
					}
					
					
					if(count < RpcAgentDaemon.maxFoldFileNum)
					{
						if(RpcAgentDaemon.getCurTaskRunnerNum() > 100){
							log.info("set curTaskRunnerNum : 0");
							RpcAgentDaemon.setCurTaskRunnerNum(0);
						}
					}
				}
            }
			
			try {
				log.info("file checker  sleep " + waitTime + " ms...");
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error("thread is interrupted!",e);
			}
			
		}
	}
	
}

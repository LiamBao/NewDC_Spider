package com.cic.datacrawl.crawler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.cic.datacollection.rpc.protocol.ResTaskRunnerReqSnapshotWritable;
import com.cic.datacollection.rpc.protocol.TaskRunnerReqSnapshotWritable;
import com.cic.datacrawl.core.GlobalConstants;
import com.cic.datacrawl.core.StatusCode;
import com.cic.datacrawl.core.entity.SnapshotBean;
import com.cic.datacrawl.core.rpc.ClientImpl;
import com.cic.datacrawl.core.rpc.ServiceName;
import com.cic.datacrawl.crawler.WebCrawler;
import com.cicdata.iwmdata.base.fq.client.FileMQCustomClient;
import com.cicdata.iwmdata.base.fq.exception.FileMQClientException;

/**
 * 与snapShotService的接口：saveSnapShot、getSnapShot
 * @author johnney.bu
 * 文本类型：HTML （HttpClient: HTML, Browser: XML）
 * 文本对应的URL
 * 采集时间
 * 文本内容
 * 页面所属的站点：Host
 */

public class SnapShotCrawler extends WebCrawler {
	private static final Logger LOG = Logger.getLogger(SnapShotCrawler.class);
	
	private static String fqName = "DATACRAWL_WEBPAGE_SNAPSHOT";
	private int siteId;
	private ClientImpl rpcClient;
	
	public SnapShotCrawler() {
	}
	
	public void init(int siteId, String snapShotIp, int snapShotPort) {
		this.siteId = siteId;
		this.rpcClient = new ClientImpl(snapShotIp, snapShotPort);
	}

	public void closeSnapShot() {
		this.rpcClient.close();
	}
	
	public void saveSnapShot(String baseUrl, byte contentType, String content) {
		SnapshotBean bean = new SnapshotBean();
		bean.setBaseUrl(baseUrl);
		bean.setContentType(contentType);
		bean.setSiteId(siteId);
		bean.setCreateTime(System.currentTimeMillis());
		bean.setContent(content);
		
		saveToFq(bean);
	}
	
	private boolean saveToFq(SnapshotBean snapShotBean) {
		try {
			FileMQCustomClient fqClient = FileMQCustomClient.getInstance(GlobalConstants.Snapshot_fq_conf);
			
			List<String> beans = new ArrayList<String>();
			beans.add(snapShotBean.toString());
			
			boolean flag = false;
			for(int tryTimes=0; tryTimes<3; ++tryTimes) {
				if(!fqClient.createFileMQ(fqName, beans)) {
					if(tryTimes == 2) {
						LOG.error("SaveSnapShotToFQ failed after retry 2 times");
						break;
					}
					try {
						Thread.sleep(500*(tryTimes+1));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					flag = true;
					break;
				}
			}
			return flag;
		} catch (FileMQClientException e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
//	private boolean saveToFq(SnapshotBean snapShotBean) {
//		FileMQClient fqClient = FileMQClient.getInstance();
//		List<String> beans = new ArrayList<String>();
//		beans.add(snapShotBean.toString());
//		
//		boolean flag = false;
//		for(int tryTimes=0; tryTimes<3; ++tryTimes) {
//			if(!fqClient.createFileMQ(fqName, beans)) {
//				if(tryTimes == 2) {
//					LOG.error("SaveSnapShotToFQ failed after retry 2 times");
//					break;
//				}
//				try {
//					Thread.sleep(500*(tryTimes+1));
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			} else {
//				flag = true;
//				break;
//			}
//		}
//		return flag;
//	}
	
	//HttpClient 下载的内容存html；Browser 下载的内容存XML
	public void downLoadPage(String url) {
		errorCode = StatusCode.STATUS_SUCC;
		SnapshotBean snapShotBean = getSnapShot(siteId, url);
		if(snapShotBean == null) {
			errorCode = StatusCode.STATUS_SNAPSHOT_NOT_EXIST;
			return;
		}
		
		if(snapShotBean.getContentType() == 1) {
			try {
				this.html = snapShotBean.getContent().getBytes(charset);
				parsePageToDocument();
				if(errorCode == StatusCode.STATUS_SUCC) {
					convertPageToXml();
				}
			} catch (UnsupportedEncodingException e) {
				errorCode = StatusCode.STATUS_STRING_TO_BYTEARRAY_EXCEPTION;
				e.printStackTrace();
			}
		} else if(snapShotBean.getContentType() == 2) {
			this.xmlContent = snapShotBean.getContent();
		}		
	}
	
	private SnapshotBean getSnapShot(int siteId, String url) {
		SnapshotBean bean = null;
		TaskRunnerReqSnapshotWritable reqWritable = new TaskRunnerReqSnapshotWritable(siteId, url);
		
		try {
			ResTaskRunnerReqSnapshotWritable feedBack =  (ResTaskRunnerReqSnapshotWritable)rpcClient.execute_proxy(ServiceName.TASK_RUNNER_GET_SNAPSHOT.getName(), reqWritable);
			if(feedBack != null && feedBack.getCode()>0) {
				bean = new SnapshotBean();
				bean.setContentType(feedBack.getContentType());
				bean.setCreateTime(feedBack.getCreateTime());
				bean.setBaseUrl(feedBack.getBaseUrl());
				bean.setContent(feedBack.getContent());
				bean.setSiteId(feedBack.getSiteId());
			} else {
				LOG.error("NetError: Get Snapshot from snapshotService failed");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		
		return bean;
	}
}

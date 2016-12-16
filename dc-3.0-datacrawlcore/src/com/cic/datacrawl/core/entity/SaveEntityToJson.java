package com.cic.datacrawl.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.GlobalConstants;
import com.cicdata.iwmdata.base.fq.client.FileMQCustomClient;
import com.cicdata.iwmdata.base.fq.exception.FileMQClientException;

/**
 * 将采集的记录转成JSON后存到FQ
 * @author johnney.bu
 *
 */
public class SaveEntityToJson extends EntitySaveManager {
	public static final Logger LOG = Logger.getLogger(SaveEntityToJson.class);
	
	private static String fqNamePreFix = "DATACRAWL";
	private FileMQCustomClient fqClient; 
	private Map<String, List<String>> entityJsons;		//存储各种类型(POST/THREAD/SERA/THREADFAILED/FORUMFAILED)的JSON数据
	private String businessName;
	
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public SaveEntityToJson() {
		super();
		initFQConf();
	}
	
	private boolean initFQConf() {
		String[] recordConf = new String[]{"classpath:fq/record/applicationContext-fq-init.xml","classpath:fq/record/applicationContext-wsClient-fq.xml","classpath:fq/record/applicationContext-fq-task.xml"};
		
		boolean flag = false;
		
		try {
			FileMQCustomClient.initCustomClient(GlobalConstants.Record_fq_conf, recordConf);
			flag = true;
		} catch (FileMQClientException e) {
			LOG.error("Init FQ Conf failed");
			e.printStackTrace();
		}
		
		return flag;
	}
	
	private boolean saveToFq(String entityTypeName, List<String> jsons) {
		try {
			fqClient = FileMQCustomClient.getInstance(GlobalConstants.Record_fq_conf);

			boolean flag = false;
			String fqName = fqNamePreFix + "_" + businessName.toUpperCase() + "_" + entityTypeName.toUpperCase();
			for(int tryTimes=0; tryTimes<3; ++tryTimes) {
				if(!fqClient.createFileMQ(fqName, jsons)) {
					LOG.error("Send record to FQ failed: " + (tryTimes+1));
					if(tryTimes == 2) {
						break;
					}
					try {
						Thread.sleep(100*(tryTimes+1));
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
	
//	private boolean saveToFq(String entityTypeName, List<String> jsons) {
//		LOG.info("Begin Init FQ Client");
//		fqClient = FileMQClient.getInstance();
//		LOG.info("Init FQ Client Success");
//
//		boolean flag = false;
//		String fqName = fqNamePreFix + "_" + businessName.toUpperCase() + "_" + entityTypeName.toUpperCase();
//		for(int tryTimes=0; tryTimes<3; ++tryTimes) {
//			if(!fqClient.createFileMQ(fqName, jsons)) {
//				LOG.error("Send record to FQ failed: " + (tryTimes+1));
//				if(tryTimes == 2) {
//					break;
//				}
//				try {
//					Thread.sleep(1000*(tryTimes+1));
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			} else {
//				flag = true;
//				break;
//			}
//		}
//		
//		return flag;
//	}
	
	@Override
	public void save(BaseEntity entity) {
		if (entity == null) {
			return;
		}
				
		if(entityJsons == null) {
			entityJsons = new HashMap<String, List<String>>();
		}
		
		synchronized(entityJsons) {
			String entityTypeName = entity.getTheEntityName().toLowerCase();
			List <String> jsonList = entityJsons.get(entityTypeName);
			if(jsonList == null) {
				jsonList = new ArrayList<String>();
				entityJsons.put(entityTypeName, jsonList);
			}
			String entityJson = entity.toJSONString();
			
			LOG.info(entityJson);
			
			jsonList.add(entityJson);
			++totalElementCount;
	
			if (jsonList.size() >= getBufferSize()) {
				try {
					if(saveToFq(entityTypeName, jsonList)) {
						jsonList.clear();
					}
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public void save(BaseEntity[] entities) {
		if (entities != null) {
			for (int i = 0; i < entities.length; ++i) {
				save(entities[i]);
			}
		}
	}

	@Override
	public void commit() throws Exception {
		if(entityJsons != null) {
			synchronized(entityJsons) {
				Set<String> keySet = entityJsons.keySet();
				Iterator<String> iter = keySet.iterator();
				while(iter.hasNext()) {
					String entityTypeName = iter.next();
					List<String> jsonList = entityJsons.get(entityTypeName);
					if(jsonList != null && !jsonList.isEmpty()) {
						saveToFq(entityTypeName, jsonList);
						jsonList.clear();
					}
				}
				
				clean();
			}
		}
	}

	@Override
	public void clean() {
		entityJsons.clear();
	}
}

package com.cic.datacrawl.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacrawl.core.rpc.ClientImpl;
import com.cic.datacrawl.core.rpc.ServiceName;
import com.cic.datacrawl.core.rpc.protocol.RecordCollectWritable;
import com.cic.datacrawl.core.util.IPUtil;

/**
 * 
 * 将采集的数据发送到CollectServer
 * 
 * @author charles.chen
 *
 */
public class SaveEntityToCollectServer extends EntitySaveManager {
	public static final Logger LOG = Logger.getLogger(SaveEntityToCollectServer.class);
	
	private Map<String, List<String>> entityJsons;//存储各种类型(POST/THREAD/SERA/THREADFAILED/FORUMFAILED)的JSON数据
	private String agentIp;
	private int port;
	private ClientImpl agent;
	private int length = 0;
	

	private String businessName;
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getAgentIp() {
		return agentIp;
	}

	public void setAgentIp(String agentIp) {
		this.agentIp = agentIp;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


	public SaveEntityToCollectServer() {
		super();
	}
	
	
	public Map<String, List<String>> getEntityJsons() {
		return entityJsons;
	}

	public void setEntityJsons(Map<String, List<String>> entityJsons) {
		this.entityJsons = entityJsons;
	}

	/**
	 * 
	 * 将数据打包发送到CollectAgent
	 * 
	 * 
	 * @param entityTypeName 实体名称
	 * @param strRecords 实体数据
	 * @return
	 */
	private boolean sendToCollectAgent(String entityTypeName, String strRecords) {
		LOG.info("strRecords  size:" + strRecords.length());
		boolean flag = false;
		RecordCollectWritable writable = new RecordCollectWritable(entityTypeName, strRecords, businessName);
		
		if(agent == null){
			agentIp = IPUtil.getHostIP();
			agent = new ClientImpl(agentIp, port);
		}
		
		for(int count = 0; count < 3; ++count){
			LOG.info(" send records to CollectAgent ..  " + (count + 1) + " time ");
			Writable back = agent.execute_proxy(ServiceName.TASK_RUNNER_REPORT_RECORD.getName(), writable);
			if(back == null){
				if(count == 2)
				{
					LOG.error("NetError:agent is closed.");
					break;
				}
				LOG.error("send records to collect agent failed! try again..");
				try {
					Thread.sleep(1000);
					LOG.info("sleep 1 second");
				} catch (InterruptedException e) {
					e.printStackTrace();
					LOG.error("thread is interrupted!",e);
				}
			}else{
				LOG.info("send records to collect agent success");
				flag = true;
				break;
			}
		}
		return flag;
	}
	

	
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
			
			length += entityJson.length(); 
			
			if(length >= DEFUALT_BUFFERED_LENGTH){
				try {
					String strRecords = JSONArray.fromObject(jsonList).toString();
					// 去掉所有的换行符
					//strRecords = strRecords.replaceAll("\n", "");
					sendToCollectAgent(entityTypeName, strRecords);
					jsonList.clear();
					length = 0;
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error("send to collect agent failed",e);
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
						String strRecords = JSONArray.fromObject(jsonList).toString();
						// 去掉所有的换行符
						//strRecords = strRecords.replaceAll("\n", "");
						sendToCollectAgent(entityTypeName, strRecords);
						jsonList.clear();
						length=0;
					}
				}
				clean();
			}
		}
	}

	@Override
	public void clean() {
		entityJsons.clear();
		length = 0;
		if(agent != null){
			agent.close();
		}
	}
	
	
}

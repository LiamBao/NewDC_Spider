//DON'T MODIFY ME
package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.management.entity.BatchInfo;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.BatchInfoDAO;

public abstract class BatchInfoBaseManager{

	protected BatchInfoDAO dao;
	protected BatchInfoDAO getBatchInfoDAO() {		
		if(dao == null){
			dao = (BatchInfoDAO) ApplicationContext.getInstance().getBean("batchInfoDAO");
		}
		return dao;
	}
	
	public void setBatchInfoDAO(BatchInfoDAO dao) {
		this.dao = dao;
	}
	
	public BatchInfo[] getAllBatchInfo() {
		return getBatchInfoDAO().getAllBatchInfo();
	}
	
	public BatchInfo[] getAllBatchInfo(int startIndex, int limit) {
		return getBatchInfoDAO().getAllBatchInfo(startIndex, limit);
	}
	
	public BatchInfo getBatchInfo(int id){
		return getBatchInfoDAO().getBatchInfo(id);
	}

	public BatchInfo[] getBatchInfos(int[] ids){
		return getBatchInfoDAO().getBatchInfos(ids);
	}
	
	public int addBatchInfo(BatchInfo batchInfo){
		return getBatchInfoDAO().addBatchInfo(batchInfo);
	}	
	
	public int[] addBatchInfos(BatchInfo[] batchInfos){
		return getBatchInfoDAO().addBatchInfos(batchInfos);
	}
	
	public int deleteBatchInfo(int id){
		return getBatchInfoDAO().deleteBatchInfo(id);
	}
	
	public int deleteBatchInfos(int[] ids){
		return getBatchInfoDAO().deleteBatchInfos(ids);
	}
	
	public long saveBatchInfo(BatchInfo batchInfo){
		long ret = getBatchInfoDAO().saveBatchInfo(batchInfo);
		if (ret == 0) {
			ret = getBatchInfoDAO().addBatchInfo(batchInfo);
		} else {
			ret = batchInfo.getId();
		}
		return ret;
	}
	
	public int updateBatchInfo(BatchInfo batchInfo){
		return getBatchInfoDAO().saveBatchInfo(batchInfo);		
	}
	
	public int[] updateBatchInfos(BatchInfo[] batchInfos){
		return getBatchInfoDAO().saveBatchInfos(batchInfos);		
	}
	
	public int count(){
		return getBatchInfoDAO().count();
	}
	
	public BatchInfo[] queryByTaskId(final int taskId ){
		 return getBatchInfoDAO().queryByTaskId(taskId );
	}
	public long countByTaskId(final int taskId ){
		return getBatchInfoDAO().countByTaskId(taskId);
	}
}

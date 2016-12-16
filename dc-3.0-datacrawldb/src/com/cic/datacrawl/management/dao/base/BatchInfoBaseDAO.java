//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.BatchInfo;

public interface BatchInfoBaseDAO {

	public BatchInfo[] getAllBatchInfo();

	public BatchInfo[] getAllBatchInfo(int startIndex, int limit);

	public BatchInfo getBatchInfo(int id);

	public BatchInfo[] getBatchInfos(int[] ids);
	
	public int addBatchInfo(BatchInfo batchInfo);
	
	public int[] addBatchInfos(BatchInfo[] batchInfos);
	
	public int deleteBatchInfo(int id);
	
	public int deleteBatchInfos(int[] ids);
	
	public int saveBatchInfo(BatchInfo batchInfo);
	
	public int[] saveBatchInfos(final BatchInfo[] batchInfos);
	
	public int count();
	
	public BatchInfo[] queryByTaskId(final int taskId );
	public int countByTaskId(final int taskId);
}

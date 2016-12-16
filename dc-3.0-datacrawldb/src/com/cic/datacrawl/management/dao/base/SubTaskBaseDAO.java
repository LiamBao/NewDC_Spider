//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.SubTask;

public interface SubTaskBaseDAO {

	public SubTask[] getAllSubTask();

	public SubTask[] getAllSubTask(int startIndex, int limit);

	public SubTask getSubTask(long id);

	public SubTask[] getSubTasks(long[] id);
	
	public long addSubTask(SubTask subTask) throws Exception;
	
	public long[] addSubTasks(SubTask[] subTasks) throws Exception;
	
	public int deleteSubTask(long id);
	
	public int deleteSubTasks(long[] id);
	
	public int saveSubTask(SubTask subTask);
	
	public int[] saveSubTasks(final SubTask[] subTasks);
	
	public int count();
	
	public SubTask[] queryByTaskId(final long taskId );
	public SubTask[] queryByIP(final java.lang.String agentLanIpv4 );
	public SubTask[] queryByIPStatus(final java.lang.String agentLanIpv4, final byte status );
	public SubTask[] queryByStatus(final byte status );
	public SubTask queryByIPStatus(final java.lang.String agentLanIpv4, final byte status, final long taskId);
	public long countByIPStatus(final java.lang.String agentLanIpv4, final byte status);
	public long countByStatus(final byte status);
	public long countByBatchId(final long batchId, final byte status, final long taskId);
}

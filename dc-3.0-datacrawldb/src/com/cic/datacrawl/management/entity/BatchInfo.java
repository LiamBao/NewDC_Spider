//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import java.text.DateFormat;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.DateUtil;

/**
 * 
 * BatchInfo: 拆分批号
 */
public class BatchInfo extends BaseEntity {
	private static final BatchInfo DEFAULT_ENTITY = new BatchInfo();

	/**
	 *	Create an default BatchInfo Entity.
	 */
	public BatchInfo() {
	}
	/**
	 * Create an BatchInfo Entity.
	 * @param batchName. Type: java.lang.String. 
	 * @param taskId. Type: long. 
	 * @param splitCount. Type: int. 
	 * @param splitTime. Type: java.sql.Timestamp. 任务拆分的时间
	 */
	public BatchInfo(
		java.lang.String batchName, 
				int taskId, 
				int splitCount, 
				java.sql.Timestamp splitTime
		) {
		
		setBatchName(batchName);
		setTaskId(taskId);
		setSplitCount(splitCount);
		setSplitTime(splitTime);
	}


	public int getId(){
		return getInt("id");
	}
	
	public void setId(int id){
		set("id", id);
	}
	/**
	 * Get BatchName Value.<br>
	 * @return BatchName type: java.lang.String
	 */
	public java.lang.String getBatchName() {
		return getString("batchName");
	}	
	/**
	 * Set 用来记录拆分批次号 Value
	 * @param 用来记录拆分批次号 type: java.lang.String
	 */
	public void setBatchName(java.lang.String batchName) {
		setString("batchName", batchName, 10);
	}	/**
	 * Get TaskId Value.<br>
	 * @return TaskId type: long
	 */
	public int getTaskId() {
		return getInt("taskId");
	}	
	/**
	 * Set TaskId Value
	 * @param TaskId type: long
	 */
	public void setTaskId(int taskId) {
		set("taskId", taskId);
	}	/**
	 * Get SplitCount Value.<br>
	 * @return SplitCount type: int
	 */
	public int getSplitCount() {
		return getInt("splitCount");
	}	
	/**
	 * Set SplitCount Value
	 * @param SplitCount type: int
	 */
	public void setSplitCount(int splitCount) {
		set("splitCount", splitCount);
	}	/**
	 * Get SplitTime Value.<br>
	 * 任务拆分的时间
	 * @return SplitTime type: java.sql.Timestamp
	 */
	public java.sql.Timestamp getSplitTime() {
		java.sql.Timestamp ret = getTimestamp("splitTime");
		return ret == null ? new java.sql.Timestamp(System.currentTimeMillis()) : ret;
	}
	
	public String getSplitTimeString(){
		return DateUtil.formatTimestamp(getSplitTime());
	}
	
	public String getSplitTimeString(DateFormat format){
		return format.format(getSplitTime());
	}
	
	
	public String getSplitTimeString(String format){
		try{
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
			return formatter.format(getSplitTime());
		} catch (Throwable t) {
			return getSplitTimeString();
		}
	}
	
	/**
	 * Set 创建时间 Value
	 * @param 创建时间 type: java.sql.Timestamp
	 */
	public void setSplitTime(java.sql.Timestamp splitTime) {
		set("splitTime", splitTime);
	}
	
	public void setSplitTime(String splitTime) {
		setSplitTime(DateUtil.format(splitTime));
	}
	
	@Override
	public String getTheEntityName() {
		return "t_batch";
	}
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id", "batchName", "taskId", "splitCount", "splitTime"};
	}
	
	@Override
	protected String[] initCompareColumns() {
		return new String[]{"id"};
	}
	
	@Override
	public BaseEntity getDefaultEmptyBean() {
		return DEFAULT_ENTITY;
	}
}

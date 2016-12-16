//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import java.text.DateFormat;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.DateUtil;

/**
 * 
 * AgentInfo: 用来记录子任务的信息
 */
public class AgentInfo extends BaseEntity {
	private static final AgentInfo DEFAULT_ENTITY = new AgentInfo();

	/**
	 *	Create an default AgentInfo Entity.
	 */
	public AgentInfo() {
	}
	/**
	 * Create an AgentInfo Entity.
	 * @param lanIpv4. Type: java.lang.String. 
	 * @param wanIpv4. Type: java.lang.String. 
	 * @param port. Type: int. 
	 * @param maxProcessNum. Type: int. 
	 * @param processNum. Type: int. 
	 * @param waitDtsFileCount. Type: int. 
	 * @param enable. Type: byte. 标志Agent的状态：0. 停用，1. 启用
	 * @param available. Type: byte. 标志Agent是否可用的状态：0. 不可用，1. 可用
	 * @param groupId. Type: long. 
	 * @param registerTime. Type: java.sql.Timestamp. 
	 * @param lastAccessTime. Type: java.sql.Timestamp. 
	 * @param lastDTSTime. Type: java.sql.Timestamp. 
	 */
	public AgentInfo(
		java.lang.String lanIpv4, 
				java.lang.String wanIpv4, 
				int port, 
				int maxProcessNum, 
				int processNum, 
				int waitDtsFileCount, 
				byte enable, 
				byte available, 
				long groupId, 
				java.sql.Timestamp registerTime, 
				java.sql.Timestamp lastAccessTime, 
				java.sql.Timestamp lastDTSTime
		) {
		
		setLanIpv4(lanIpv4);
		setWanIpv4(wanIpv4);
		setPort(port);
		setMaxProcessNum(maxProcessNum);
		setProcessNum(processNum);
		setWaitDtsFileCount(waitDtsFileCount);
		setEnable(enable);
		setAvailable(available);
		setGroupId(groupId);
		setRegisterTime(registerTime);
		setLastAccessTime(lastAccessTime);
		setLastDTSTime(lastDTSTime);
	}


	public long getId(){
		return getLong("id");
	}
	
	public void setId(long id){
		set("id", id);
	}
	/**
	 * Get LanIpv4 Value.<br>
	 * @return LanIpv4 type: java.lang.String
	 */
	public java.lang.String getLanIpv4() {
		return getString("lanIpv4");
	}	
	/**
	 * Set Agent局域地址 Value
	 * @param Agent局域地址 type: java.lang.String
	 */
	public void setLanIpv4(java.lang.String lanIpv4) {
		setString("lanIpv4", lanIpv4, 15);
	}	/**
	 * Get WanIpv4 Value.<br>
	 * @return WanIpv4 type: java.lang.String
	 */
	public java.lang.String getWanIpv4() {
		return getString("wanIpv4");
	}	
	/**
	 * Set Agent外网地址 Value
	 * @param Agent外网地址 type: java.lang.String
	 */
	public void setWanIpv4(java.lang.String wanIpv4) {
		setString("wanIpv4", wanIpv4, 15);
	}	/**
	 * Get Port Value.<br>
	 * @return Port type: int
	 */
	public int getPort() {
		return getInt("port");
	}	
	/**
	 * Set Agent端口号 Value
	 * @param Agent端口号 type: int
	 */
	public void setPort(int port) {
		set("port", port);
	}	/**
	 * Get MaxProcessNum Value.<br>
	 * @return MaxProcessNum type: int
	 */
	public int getMaxProcessNum() {
		return getInt("maxProcessNum");
	}	
	/**
	 * Set 最大任务数 Value
	 * @param 最大任务数 type: int
	 */
	public void setMaxProcessNum(int maxProcessNum) {
		set("maxProcessNum", maxProcessNum);
	}	/**
	 * Get ProcessNum Value.<br>
	 * @return ProcessNum type: int
	 */
	public int getProcessNum() {
		return getInt("processNum");
	}	
	/**
	 * Set 当前运行任务数 Value
	 * @param 当前运行任务数 type: int
	 */
	public void setProcessNum(int processNum) {
		set("processNum", processNum);
	}	/**
	 * Get WaitDtsFileCount Value.<br>
	 * @return WaitDtsFileCount type: int
	 */
	public int getWaitDtsFileCount() {
		return getInt("waitDtsFileCount");
	}	
	/**
	 * Set 未 DTS 的文件数量 Value
	 * @param 未 DTS 的文件数量 type: int
	 */
	public void setWaitDtsFileCount(int waitDtsFileCount) {
		set("waitDtsFileCount", waitDtsFileCount);
	}	/**
	 * Get Enable Value.<br>
	 * 标志Agent的状态：0. 停用，1. 启用
	 * @return Enable type: byte
	 */
	public byte getEnable() {
		byte ret = getByte("enable");
		return ret == Byte.MIN_VALUE ? 0 : ret;
	}	
	/**
	 * Set 启用的状态标志 Value
	 * @param 启用的状态标志 type: byte
	 */
	public void setEnable(byte enable) {
		set("enable", enable);
	}	/**
	 * Get Available Value.<br>
	 * 标志Agent是否可用的状态：0. 不可用，1. 可用
	 * @return Available type: byte
	 */
	public byte getAvailable() {
		byte ret = getByte("available");
		return ret == Byte.MIN_VALUE ? 0 : ret;
	}	
	/**
	 * Set 不可用的状态标志 Value
	 * @param 不可用的状态标志 type: byte
	 */
	public void setAvailable(byte available) {
		set("available", available);
	}	/**
	 * Get GroupId Value.<br>
	 * @return GroupId type: long
	 */
	public long getGroupId() {
		return getLong("groupId");
	}	
	/**
	 * Set Group ID Value
	 * @param Group ID type: long
	 */
	public void setGroupId(long groupId) {
		set("groupId", groupId);
	}	/**
	 * Get RegisterTime Value.<br>
	 * @return RegisterTime type: java.sql.Timestamp
	 */
	public java.sql.Timestamp getRegisterTime() {
		return getTimestamp("registerTime");
	}
	
	public String getRegisterTimeString(){
		return DateUtil.formatTimestamp(getRegisterTime());
	}
	
	public String getRegisterTimeString(DateFormat format){
		return format.format(getRegisterTime());
	}
	
	
	public String getRegisterTimeString(String format){
		try{
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
			return formatter.format(getRegisterTime());
		} catch (Throwable t) {
			return getRegisterTimeString();
		}
	}
	
	/**
	 * Set 注册时间 Value
	 * @param 注册时间 type: java.sql.Timestamp
	 */
	public void setRegisterTime(java.sql.Timestamp registerTime) {
		set("registerTime", registerTime);
	}
	
	public void setRegisterTime(String registerTime) {
		setRegisterTime(DateUtil.format(registerTime));
	}
	/**
	 * Get LastAccessTime Value.<br>
	 * @return LastAccessTime type: java.sql.Timestamp
	 */
	public java.sql.Timestamp getLastAccessTime() {
		return getTimestamp("lastAccessTime");
	}
	
	public String getLastAccessTimeString(){
		return DateUtil.formatTimestamp(getLastAccessTime());
	}
	
	public String getLastAccessTimeString(DateFormat format){
		return format.format(getLastAccessTime());
	}
	
	
	public String getLastAccessTimeString(String format){
		try{
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
			return formatter.format(getLastAccessTime());
		} catch (Throwable t) {
			return getLastAccessTimeString();
		}
	}
	
	/**
	 * Set 最后任务接收时间 Value
	 * @param 最后任务接收时间 type: java.sql.Timestamp
	 */
	public void setLastAccessTime(java.sql.Timestamp lastAccessTime) {
		set("lastAccessTime", lastAccessTime);
	}
	
	public void setLastAccessTime(String lastAccessTime) {
		setLastAccessTime(DateUtil.format(lastAccessTime));
	}
	/**
	 * Get LastDTSTime Value.<br>
	 * @return LastDTSTime type: java.sql.Timestamp
	 */
	public java.sql.Timestamp getLastDTSTime() {
		return getTimestamp("lastDTSTime");
	}
	
	public String getLastDTSTimeString(){
		return DateUtil.formatTimestamp(getLastDTSTime());
	}
	
	public String getLastDTSTimeString(DateFormat format){
		return format.format(getLastDTSTime());
	}
	
	
	public String getLastDTSTimeString(String format){
		try{
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
			return formatter.format(getLastDTSTime());
		} catch (Throwable t) {
			return getLastDTSTimeString();
		}
	}
	
	/**
	 * Set 最后DTS时间 Value
	 * @param 最后DTS时间 type: java.sql.Timestamp
	 */
	public void setLastDTSTime(java.sql.Timestamp lastDTSTime) {
		set("lastDTSTime", lastDTSTime);
	}
	
	public void setLastDTSTime(String lastDTSTime) {
		setLastDTSTime(DateUtil.format(lastDTSTime));
	}
	
	@Override
	public String getTheEntityName() {
		return "t_agent_info";
	}
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id", "lanIpv4", "wanIpv4", "port", "maxProcessNum", "processNum", "waitDtsFileCount", "enable", "available", "groupId", "registerTime", "lastAccessTime", "lastDTSTime"};
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

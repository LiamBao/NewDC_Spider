//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import java.text.DateFormat;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.DateUtil;

/**
 * 
 * Task: 用来定义新的任务，包括该任务的描述信息，使用哪个脚本，脚本调用方式，运行间隔
 */
public class Task extends BaseEntity {
	private static final Task DEFAULT_ENTITY = new Task();

	/**
	 *	Create an default Task Entity.
	 */
	public Task() {
	}
	/**
	 * Create an Task Entity.
	 * @param id. Type: int. TaskId
	 * @param siteId. Type: int. 可以用来描述该任务的简短文字，长度为25个汉字，或者50个英文数字符号
	 * @param name. Type: java.lang.String. 可以用来描述该任务的简短文字，长度为25个汉字，或者50个英文数字符号
	 * @param remark. Type: java.lang.String. 用来描述任务的详细信息，长度为200个汉字，或者400个英文数字符号
	 * @param agentType. Type: byte. 采集客户端类型 1-httpclient 2-browser
	 * @param agentGroupId. Type: int. 任务执行的机器分组 
	 * @param spliterGroupId. Type: int. 任务分组ID，标识该由哪个分组的拆分程序处理
	 * @param priority. Type: int. 任务优先级，值越大，表明优先级越高
	 * @param splitFlag. Type: byte. 标志当前任务的是否启用：0. 未启用，1. 启用
	 * @param sendFlag. Type: byte. 标志当前任务的是否启用：0. 未启用，1. 启用
	 * @param useSnapShot. Type: byte. 是否使用网页快照： 0. 不使用，1. 使用
	 * @param finishFlag. Type: byte. 标志当前任务的是否所有的子任务都执行完成：0. 未完成，1. 执行完成
	 * @param dueCheckFlag. Type: byte. 预警提醒，0为不提醒，1为提醒
	 * @param dueTime. Type: java.sql.Timestamp. 如果超过指定时间，则任务将不再会被任务拆分程序拆分	
	 * @param dueAlarmBefore. Type: int. 根据用户指定的天数，在任务到期前给用户发送提醒邮件
	 * @param splitFile. Type: java.lang.String. 用来执行该任务的脚本，保存的信息为配置文件中定义的脚本目录下的相对路径，长度为100个英文数字符号
	 * @param splitMain. Type: java.lang.String. 执行脚本中的主函数及参数名称，长度为300个英文数字符号，例如：execute(&quot;http://www.163.com&quot;)
	 * @param scriptFile. Type: java.lang.String. 记录当前任务执行使用的脚本名称，此属性从任务定义表中带入
	 * @param splitWaitTime. Type: long. 两次拆分操作的间隔等待时间，单位是毫秒
	 * @param startTime. Type: java.sql.Timestamp. 
	 * @param lastSplitTime. Type: java.sql.Timestamp. 
	 * @param lastSplitKey. Type: java.lang.String. 拆分批号由随机字符串产生
	 * @param lastSplitNum. Type: int. 上次拆分出的子任务数
	 * @param subTaskNum. Type: int. 拆分出的子任务总数
	 * @param lastSplitMsg. Type: java.lang.String. 拆分异常消息
	 */
	public Task(
			int siteId, 
			java.lang.String name, 
			java.lang.String remark,
			byte agentType,
			int agentGroupId,
			int spliterGroupId,
			int priority,
			byte splitFlag,
			byte sendFlag,
			byte useSnapShot,
			byte finishFlag,
			byte dueCheckFlag,
			java.sql.Timestamp dueTime,
			int dueAlarmBefore,
			java.lang.String splitFile, 
			java.lang.String splitMain, 
			java.lang.String scriptFile,
			int turnPageWaitTime,
			long splitWaitTime, 
			java.sql.Timestamp startTime, 
			java.sql.Timestamp lastSplitTime,
			int lastSplitCostTime,
			java.lang.String lastSplitKey, 
			int lastSplitNum,
			int subTaskNum,
			byte lastSplitStatus,
			java.lang.String lastSplitMsg
		) {
		
		setSiteId(siteId);
		setName(name);
		setRemark(remark);
		setAgentType(agentType);
		setAgentGroupId(agentGroupId);
		setSpliterGroupId(spliterGroupId);
		setPriority(priority);
		setSplitFlag(splitFlag);
		setSendFlag(sendFlag);
		setUseSnapShot(useSnapShot);
		setFinishFlag(finishFlag);
		setDueCheckFlag(dueCheckFlag);
		setDueTime(dueTime);
		setDueAlarmBefore(dueAlarmBefore);
		setSplitFile(splitFile);
		setSplitMain(splitMain);
		setScriptFile(scriptFile);
		setTurnPageWaitTime(turnPageWaitTime);
		setSplitWaitTime(splitWaitTime);	
		setStartTime(startTime);
		setLastSplitTime(lastSplitTime);
		setLastSplitCostTime(lastSplitCostTime);
		setLastSplitKey(lastSplitKey);
		setLastSplitNum(lastSplitNum);
		setSubTaskNum(subTaskNum);
		setLastSplitStatus(lastSplitStatus);
		setLastSplitMsg(lastSplitMsg);
	}


	public int getId(){
		return getInt("id");
	}
	
	public void setId(int id){
		set("id", id);
	}
	/**
	 * Get SiteId Value.<br>
	 * 可以用来描述该任务的简短文字，长度为25个汉字，或者50个英文数字符号
	 * @return SiteId type: long
	 */
	public int getSiteId() {
		return getInt("siteId");
	}	
	/**
	 * Set 任务名称 Value
	 * @param 任务名称 type: long
	 */
	public void setSiteId(int siteId) {
		set("siteId", siteId);
	}	
	
	/**
	 * Get Name Value.<br>
	 * 可以用来描述该任务的简短文字，长度为25个汉字，或者50个英文数字符号
	 * @return Name type: java.lang.String
	 */
	public java.lang.String getName() {
		return getString("name");
	}	
	/**
	 * Set 任务名称 Value
	 * @param 任务名称 type: java.lang.String
	 */
	public void setName(java.lang.String name) {
		setString("name", name, 50);
	}	/**
	 * Get Description Value.<br>
	 * 用来描述任务的详细信息，长度为200个汉字，或者400个英文数字符号
	 * @return Description type: java.lang.String
	 */
	public java.lang.String getRemark() {
		return getString("remark");
	}	
	/**
	 * Set 任务描述 Value
	 * @param 任务描述 type: java.lang.String
	 */
	public void setRemark(java.lang.String remark) {
		setString("remark", remark, 400);
	}	
	
	public byte getAgentType() {
		return getByte("agentType");
	}
	
	public void setAgentType(byte agentType) {
		set("agentType", agentType);
	}
	
	public int getAgentGroupId() {
		return getInt("agentGroupId");
	}
	
	public void setAgentGroupId(int agentGroupId) {
		set("agentGroupId", agentGroupId);
	}
	
	public int getSpliterGroupId() {
		return getInt("spliterGroupId");
	}
	
	public void setSpliterGroupId(int spliterGroupId) {
		set("spliterGroupId", spliterGroupId);
	}
	
	/**
	 * Get Priority Value.<br>
	 * 任务优先级，值越大，表明优先级越高
	 * @return Priority type: int
	 */
	public int getPriority() {
		int ret = getInt("priority");
		return ret == Integer.MIN_VALUE ? 0 : ret;
	}	
	/**
	 * Set 任务优先级 Value
	 * @param 任务优先级 type: int
	 */
	public void setPriority(int priority) {
		set("priority", priority);
	}
	
	public byte getSplitFlag() {
		return getByte("splitFlag");
	}
	
	public void setSplitFlag(byte splitFlag) {
		set("splitFlag", splitFlag);
	}
	
	public byte getSendFlag() {
		return getByte("sendFlag");
	}
	
	public void setSendFlag(byte sendFlag) {
		set("sendFlag", sendFlag);
	}
	
	public byte getUseSnapShot() {
		return getByte("useSnapShot");
	}
	
	public void setUseSnapShot(byte useSnapShot) {
		set("useSnapShot", useSnapShot);
	}
	
	public byte getFinishFlag() {
		return getByte("finishFlag");
	}
	
	public void setFinishFlag(byte finishFlag) {
		set("finishFlag", finishFlag);
	}
	
	/**
	 * Get DueCheckFlag Value.<br>
	 * 初始时候为true。为false时不发送任务到期提醒邮件
	 * @return DueCheckFlag type: boolean
	 */
	public byte getDueCheckFlag() {
		return getByte("dueCheckFlag");
	}	
	/**
	 * Set 是否发送到期提醒邮件 Value
	 * @param 是否发送到期提醒邮件 type: boolean
	 */
	public void setDueCheckFlag(byte dueCheckFlag) {
		set("dueCheckFlag", dueCheckFlag);
	}
	
	/**
	 * Get DueTime Value.<br>
	 * 如果超过指定时间，则任务将不再会被任务拆分程序拆分
	 * @return DueTime type: java.sql.Timestamp
	 */
	public java.sql.Timestamp getDueTime() {
		return getTimestamp("dueTime");
	}
	
	public String getDueTimeString(){
		return DateUtil.formatTimestamp(getDueTime());
	}
	
	public String getDueTimeString(DateFormat format){
		return format.format(getDueTime());
	}
	
	
	public String getDueTimeString(String format){
		try{
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
			return formatter.format(getDueTime());
		} catch (Throwable t) {
			return getDueTimeString();
		}
	}
	
	/**
	 * Set 任务到期时间 Value
	 * @param 任务到期时间 type: java.sql.Timestamp
	 */
	public void setDueTime(java.sql.Timestamp dueTime) {
		set("dueTime", dueTime);
	}
	
	public void setDueTime(String dueTime) {
		setDueTime(DateUtil.format(dueTime));
	}
	/**
	 * Get DueAlarmBefore Value.<br>
	 * 根据用户指定的天数，在任务到期前给用户发送提醒邮件
	 * @return DueAlarmBefore type: int
	 */
	public int getDueAlarmBefore() {
		return getInt("dueAlarmBefore");
	}	
	/**
	 * Set 任务到期前提醒 Value
	 * @param 任务到期前提醒 type: int
	 */
	public void setDueAlarmBefore(int dueAlarmBefore) {
		set("dueAlarmBefore", dueAlarmBefore);
	}
	
	/**
	 * Get SplitFile Value.<br>
	 * 用来执行该任务的脚本，保存的信息为配置文件中定义的脚本目录下的相对路径，长度为100个英文数字符号
	 * @return SplitFile type: java.lang.String
	 */
	public java.lang.String getSplitFile() {
		return getString("splitFile");
	}	
	/**
	 * Set 拆分脚本名称 Value
	 * @param 拆分脚本名称 type: java.lang.String
	 */
	public void setSplitFile(java.lang.String splitFile) {
		setString("splitFile", splitFile, 100);
	}	/**
	 * Get SplitMain Value.<br>
	 * 执行脚本中的主函数及参数名称，长度为300个英文数字符号，例如：execute(&quot;http://www.163.com&quot;)
	 * @return SplitMain type: java.lang.String
	 */
	public java.lang.String getSplitMain() {
		return getString("splitMain");
	}	
	/**
	 * Set 拆分脚本执行函数 Value
	 * @param 拆分脚本执行函数 type: java.lang.String
	 */
	public void setSplitMain(java.lang.String splitMain) {
		setString("splitMain", splitMain, 300);
	}	/**
	 * Get ScriptFile Value.<br>
	 * 记录当前任务执行使用的脚本名称，此属性从任务定义表中带入
	 * @return ScriptFile type: java.lang.String
	 */
	public java.lang.String getScriptFile() {
		return getString("scriptFile");
	}	
	/**
	 * Set 运行脚本名称 Value
	 * @param 运行脚本名称 type: java.lang.String
	 */
	public void setScriptFile(java.lang.String scriptFile) {
		setString("scriptFile", scriptFile, 100);
	}
	
	public int getTurnPageWaitTime(){
		int ret = getInt("turnPageWaitTime");
		return ret<0 ? 0 : ret;
	}
	
	public void setTurnPageWaitTime(int turnPageWaitTime) {
		set("turnPageWaitTime", turnPageWaitTime);
	}
	
	/**
	 * Get SplitWaitTime Value.<br>
	 * 两次拆分操作的间隔等待时间，单位是毫秒
	 * @return SplitWaitTime type: long
	 */
	public long getSplitWaitTime() {
		long ret = getLong("splitWaitTime");
		return ret<0 ? 0 : ret;
	}	
	/**
	 * Set 拆分等待时间 Value
	 * @param 拆分等待时间 type: int
	 */
	public void setSplitWaitTime(long splitWaitTime) {
		set("splitWaitTime", splitWaitTime);
	}		
	
	/**
	 * Get StartTime Value.<br>
	 * @return StartTime type: java.sql.Timestamp
	 */
	public java.sql.Timestamp getStartTime() {
		return getTimestamp("startTime");
	}
	
	public String getStartTimeString(){
		return DateUtil.formatTimestamp(getStartTime());
	}
	
	public String getStartTimeString(DateFormat format){
		return format.format(getStartTime());
	}
	
	
	public String getStartTimeString(String format){
		try{
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
			return formatter.format(getStartTime());
		} catch (Throwable t) {
			return getStartTimeString();
		}
	}
	
	/**
	 * Set 启动时间 Value
	 * @param 启动时间 type: java.sql.Timestamp
	 */
	public void setStartTime(java.sql.Timestamp startTime) {
		set("startTime", startTime);
	}
	
	public void setStartTime(String startTime) {
		setStartTime(DateUtil.format(startTime));
	}
	
	/**
	 * Get LastSplitTime Value.<br>
	 * @return LastSplitTime type: java.sql.Timestamp
	 */
	public java.sql.Timestamp getLastSplitTime() {
		return getTimestamp("lastSplitTime");
	}
	
	public String getLastSplitTimeString(){
		return DateUtil.formatTimestamp(getLastSplitTime());
	}
	
	public String getLastSplitTimeString(DateFormat format){
		return format.format(getLastSplitTime());
	}
	
	
	public String getLastSplitTimeString(String format){
		try{
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
			return formatter.format(getLastSplitTime());
		} catch (Throwable t) {
			return getLastSplitTimeString();
		}
	}
	
	/**
	 * Set 最后一次拆分时间 Value
	 * @param 最后一次拆分时间 type: java.sql.Timestamp
	 */
	public void setLastSplitTime(java.sql.Timestamp lastSplitTime) {
		set("lastSplitTime", lastSplitTime);
	}
	
	public void setLastSplitTime(String lastSplitTime) {
		setLastSplitTime(DateUtil.format(lastSplitTime));
	}
	
	public int getLastSplitCostTime() {
		return getInt("lastSplitCostTime");
	}
	
	public void setLastSplitCostTime(int lastSplitCostTime) {
		set("lastSplitCostTime", lastSplitCostTime);
	}
	
	/**
	 * Get LastSplitKey Value.<br>
	 * 拆分批号由随机字符串产生
	 * @return LastSplitKey type: java.lang.String
	 */
	public java.lang.String getLastSplitKey() {
		return getString("lastSplitKey");
	}	
	/**
	 * Set 最后一次拆分批号 Value
	 * @param 最后一次拆分批号 type: java.lang.String
	 */
	public void setLastSplitKey(java.lang.String lastSplitKey) {
		setString("lastSplitKey", lastSplitKey, 10);
	}	/**
	 * Get LastSplitCount Value.<br>
	 * 标志当前任务的拆分情况：0. 无需拆分，1. 需要拆分
	 * @return LastSplitCount type: int
	 */
	public int getLastSplitNum() {
		int ret = getInt("lastSplitNum");
		return ret < 0 ? 0 : ret;
	}	
	/**
	 * Set 最后一次拆分的数量 Value
	 * @param 最后一次拆分的数量 type: int
	 */
	public void setLastSplitNum(int lastSplitNum) {
		set("lastSplitNum", lastSplitNum);
	}
	
	public int getSubTaskNum() {
		return getInt("subTaskNum");
	}
	
	public void setSubTaskNum(int subTaskNum) {
		set("subTaskNum", subTaskNum);
	}
	
	public byte getLastSplitStatus() {
		return getByte("lastSplitStatus");
	}
	
	public void setLastSplitStatus(byte lastSplitStatus) {
		set("lastSplitStatus", lastSplitStatus);
	}
	
	public String getLastSplitMsg() {
		return getString("lastSplitMsg");
	}
	
	public void setLastSplitMsg(String lastSplitMsg) {
		setString("lastSplitMsg", lastSplitMsg, 512);
	}

	@Override
	public String getTheEntityName() {
		return "t_task";
	}
	
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id","siteId","name","remark","agentType","agentGroupId","spliterGroupId","priority","splitFlag","sendFlag","useSnapShot","finishFlag","dueCheckFlag","dueTime","dueAlarmBefore","splitFile","splitMain","scriptFile","turnPageWaitTime","splitWaitTime","startTime","lastSplitTime","lastSplitKey","lastSplitNum","splitStatus","lastSplitMsg"};
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

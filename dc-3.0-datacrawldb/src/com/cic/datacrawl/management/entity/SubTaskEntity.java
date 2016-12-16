//DON'T MODIFY ME
package com.cic.datacrawl.management.entity;

import com.cic.datacrawl.core.entity.BaseEntity;

/**
 * 
 * SubTask: 用来保存被切分后的任务，包括该任务的描述信息，使用哪个脚本，脚本调用方式，运行间隔，及其执行状况
 */
public class SubTaskEntity extends BaseEntity {
	private static final SubTaskEntity DEFAULT_ENTITY = new SubTaskEntity();

	/**
	 *	Create an default SubTask Entity.
	 */
	public SubTaskEntity() {
	}
	/**
	 * Create an SubTask Entity.
	 * @param agentType. Type: java.lang.String. 下载网页引擎类型，1-httpClient, 2-browser
	 * @param taskId. Type: int. 关联到SubTaskDefine中的某一个被拆分任务的定义，该任务定义可能会被删除
	 * @param subTaskId. Type: long. 由subTaskKey经过md5计算，取前8个字节转换得到的值 
	 * @param subtaskKey. Type: java.lang.String. 记录当前任务的key，此属性从拆分脚本中带入
	 * @param scriptFile. Type: java.lang.String. 记录当前任务执行使用的脚本名称，此属性从任务定义表中带入
	 * @param scriptMain. Type: java.lang.String. 记录当前任务执行使用的主函数及参数，此属性从任务定义表中带入
	 * @param siteId. Type: int.
	 * @param turnPageWaitTime. Type: int. 翻页等待时间
	 * @param useSnapShot. Type: byte. 是否使用快照 
	 * @param batchId. Type: int. 用来关联拆分批次号的主键
	 * @param createTime. Type: long. 任务拆分的时间
	 * @param startTime. Type: long. 任务开始的时间
	 * @param projectId 项目ID
	 * @param keyWord. Type: java.lang.String. 搜索的关键字
	 * @param forumId. java.lang.String. 
	 * @param threadId. java.lang.String.
	 */
	public SubTaskEntity(
			byte agentType,
			int taskId,
			long subTaskId,
			java.lang.String subtaskKey, 
			java.lang.String scriptFile, 
			java.lang.String scriptMain, 
			int siteId,
			int turnPageWaitTime,
			byte useSnapShot,
			int batchId, 
			long createTime, 
			long startTime,
			int projectId,
			java.lang.String keyWord,
			java.lang.String forumId,
			java.lang.String threadId
		) {
		
		setAgentType(agentType);
		setTaskId(taskId);
		setSubTaskId(subTaskId);
		setSubtaskKey(subtaskKey);
		setScriptFile(scriptFile);
		setScriptMain(scriptMain);
		setSiteId(siteId);
		setTurnPageWaitTime(turnPageWaitTime);
		setUseSnapShot(useSnapShot);
		setBatchId(batchId);
		setCreateTime(createTime);
		setStartTime(startTime);
		setProjectId(projectId);
		setKeyWord(keyWord);
		setForumId(forumId);
		setThreadId(threadId);
	}
	
	public long getId(){
		return getLong("id");
	}
	
	public void setId(long id){
		set("id", id);
	}
	
	public byte getAgentType() {
		return getByte("agentType");
	}
	
	public void setAgentType(byte agentType) {
		set("agentType", agentType);
	}
	
	/**
	 * Get TaskId Value.<br>
	 * 关联到SubTaskDefine中的某一个被拆分任务的定义，该任务定义可能会被删除
	 * @return TaskId type: long
	 */
	public int getTaskId() {
		return getInt("taskId");
	}	
	/**
	 * Set 任务id Value
	 * @param 任务id type: long
	 */
	public void setTaskId(int taskId) {
		set("taskId", taskId);
	}	
	/**
	 * Get SubtaskKey Value.<br>
	 * 记录当前任务的key，此属性从拆分脚本中带入
	 * @return SubtaskKey type: java.lang.String
	 */
	
	public long getSubTaskId() {
		return getLong("subTaskId");
	}
	
	public void setSubTaskId(long subTaskId) {
		set("subTaskId", subTaskId);
	}
	
	public java.lang.String getSubtaskKey() {
		return getString("subtaskKey");
	}	
	/**
	 * Set Key Value
	 * @param Key type: java.lang.String
	 */
	public void setSubtaskKey(java.lang.String subtaskKey) {
		setString("subtaskKey", subtaskKey, 255);
	}	
	/**
	 * Get ScriptFile Value.<br>
	 * 记录当前任务执行使用的脚本名称，此属性从任务定义表中带入
	 * @return ScriptFile type: java.lang.String
	 */
	public java.lang.String getScriptFile() {
		return getString("scriptFile");
	}	
	/**
	 * Set 脚本名称 Value
	 * @param 脚本名称 type: java.lang.String
	 */
	public void setScriptFile(java.lang.String scriptFile) {
		setString("scriptFile", scriptFile, 128);
	}	
	/**
	 * Get ScriptMain Value.<br>
	 * 记录当前任务执行使用的主函数及参数，此属性从任务定义表中带入
	 * @return ScriptMain type: java.lang.String
	 */
	public java.lang.String getScriptMain() {
		return getString("scriptMain");
	}	
	/**
	 * Set 脚本运行函数 Value
	 * @param 脚本运行函数 type: java.lang.String
	 */
	public void setScriptMain(java.lang.String scriptMain) {
		setString("scriptMain", scriptMain, 65536);
	}	
	/**
	 * Get SiteId Value.<br>
	 * @return SiteId type: int
	 */
	public int getSiteId() {
		int ret = getInt("siteId");
		return ret<0 ? 0 : ret;
	}	
	/**
	 * Set 网站ID Value
	 * @param 网站ID type: int
	 */
	public void setSiteId(int siteId) {
		set("siteId", siteId);
	}
	
	public int getTurnPageWaitTime() {
		int ret = getInt("turnPageWaitTime");
		return ret<0 ? 0 : ret;
	}
	            
	public void setTurnPageWaitTime(int turnPageWaitTime) {
		set("turnPageWaitTime", turnPageWaitTime);
	}
	
	public byte getUseSnapShot() {
		return getByte("useSnapShot");
	}
	
	public void setUseSnapShot(byte useSnapShot) {
		set("useSnapShot", useSnapShot);
	}
	
	/**
	 * Get BatchId Value.<br>
	 * 用来关联拆分批次号的主键
	 * @return BatchId type: long
	 */
	public int getBatchId() {
		int ret = getInt("batchId");
		return ret<-1 ? -1 : ret;
	}	
	/**
	 * Set BatchId Value
	 * @param BatchId type: long
	 */
	public void setBatchId(int batchId) {
		set("batchId", batchId);
	}	
	/**
	 * Get CreateTime Value.<br>
	 * 任务拆分的时间
	 * @return CreateTime type: java.sql.Timestamp
	 */
	public long getCreateTime() {
		return getLong("createTime");
	}
	/**
	 * Set 创建时间 Value
	 * @param 创建时间 type: long
	 */
	public void setCreateTime(long createTime) {
		set("createTime", createTime);
	}

	/**
	 * Get StartTime Value.<br>
	 * 任务开始的时间
	 * @return StartTime type: long
	 */
	public long getStartTime() {
		return getLong("startTime");
	}
	/**
	 * Set 开始运行时间 Value
	 * @param 开始运行时间 type: long
	 */
	public void setStartTime(long startTime) {
		set("startTime", startTime);
	}

	public int getProjectId() {
		return getInt("projectId");
	}
	
	public void setProjectId(int projectId) {
		set("projectId", projectId);
	}
	
	public String getKeyWord() {
		return getString("keyWord");
	}
	
	public void setKeyWord(String keyWord) {
		setString("keyWord", keyWord, 256);
	}
	
	public String getForumId() {
		return getString("forumId");
	}
	
	public void setForumId(String forumId) {
		setString("forumId", forumId, 256);
	}
	
	public String getThreadId() {
		return getString("threadId");
	}
	
	public void setThreadId(String threadId) {
		setString("threadId", threadId, 256);
	}
	
	@Override
	public String getTheEntityName() {
		return "t_subtask";
	}
	
	@Override
	protected String[] initColumns() {
		return new String[]{"id", "agentType", "taskId", "subTaskId", "subtaskKey", "scriptFile", "scriptMain", "siteId", "turnPageWaitTime", "useSnapShot", "batchId", "createTime", "startTime", "projectId", "keyWord", "forumId", "threadId"};
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

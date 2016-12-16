//DON'T MODIFY ME
package com.cic.datacrawl.management;

import com.cic.datacrawl.management.entity.*;
import com.cic.datacrawl.core.initialize.InitializeAble;
import com.cic.datacrawl.core.rhino.RhinoContextGlobalRegister;

public class DcmanagementEntityFactory extends InitializeAble {
	private static final DcmanagementEntityFactory FACTORY = new DcmanagementEntityFactory();
	
	public static final DcmanagementEntityFactory getInstance() {
		return FACTORY;
	}
	
	protected DcmanagementEntityFactory() {
	}	
	
	/**
	 *	Create an default Site Entity.
	 */
	public Site buildDefaultSite() {
		return new Site();
	}
	
	/**
	 * Create an Site Entity.
	 * @param name. Type: java.lang.String. 
	 * @param description. Type: java.lang.String. 
	 * @param domain. Type: java.lang.String. 
	 * @param url. Type: java.lang.String. 
	 * @param infoStatus. Type: int. 记录进程的状态：0. 只有网站信息，1. 完成
	 * @param maxInstance. Type: int. 
	 * @param processInstance. Type: int. 
	 * @param bddId. Type: long. 0
	 * @param type. Type: java.lang.String. 
	 * @param qaId. Type: long. 
	 * @param qa. Type: java.lang.String. 
	 * @param rw. Type: java.lang.String. 
	 * @param rwId. Type: long. 
	 * @param groupId. Type: long. 
	 */
	public Site buildSite(java.lang.String name, 
			java.lang.String domain, 
			java.lang.String url, 
			byte infoStatus,
			java.lang.String itemType,  
			java.lang.String qa, 
			java.lang.String rw, 
			java.lang.String remark,
			byte deleteFlag
		) {
		
		return new Site(name, 
			domain, 
			url, 
			infoStatus, 
			itemType,  
			qa, 
			rw, 
			remark,
			deleteFlag
		);
	}
	
	/**
	 *	Create an default BatchInfo Entity.
	 */
	public BatchInfo buildDefaultBatchInfo() {
		return new BatchInfo();
	}
	
	/**
	 * Create an BatchInfo Entity.
	 * @param batchName. Type: java.lang.String. 
	 * @param taskId. Type: long. 
	 * @param splitCount. Type: int. 
	 * @param splitTime. Type: java.sql.Timestamp. 任务拆分的时间
	 */
	public BatchInfo buildBatchInfo(java.lang.String batchName, 
		int taskId, 
		int splitCount, 
		java.sql.Timestamp splitTime
		) {
		
		return new BatchInfo(batchName, 
		taskId, 
		splitCount, 
		splitTime
		);
	}
	
	/**
	 *	Create an default SubTask Entity.
	 */
	public SubTaskEntity buildDefaultSubTask() {
		return new SubTaskEntity();
	}
	
	/**
	 * Create an SubTask Entity.
	 * @param agentId. Type: long. 
	 * @param agentLanIpv4. Type: java.lang.String. 
	 * @param agentWanIpv4. Type: java.lang.String. 
	 * @param taskId. Type: long. 关联到SubTaskDefine中的某一个被拆分任务的定义，该任务定义可能会被删除
	 * @param subtaskKey. Type: java.lang.String. 记录当前任务的key，此属性从拆分脚本中带入
	 * @param scriptFile. Type: java.lang.String. 记录当前任务执行使用的脚本名称，此属性从任务定义表中带入
	 * @param scriptMain. Type: java.lang.String. 记录当前任务执行使用的主函数及参数，此属性从任务定义表中带入
	 * @param scrapeCount. Type: byte. 
	 * @param status. Type: byte. 记录进程的状态：0. 等待，1. 进行中，2. 完成，3. 错误
	 * @param groupId. Type: long. 
	 * @param siteId. Type: int. 
	 * @param errorCode. Type: int. 记录进程的状态：0. 正常，1，2，3，4等错误号由DC定义
	 * @param errorMessage. Type: java.lang.String. 记录进程的错误信息。
	 * @param errorUrl. Type: java.lang.String. 记录进程出错的URL。
	 * @param errorSentFlag. Type: byte. 错误信息发送标志：0. 初始，1. 未发送，2. 已发送
	 * @param sort. Type: int. 0
	 * @param bddId. Type: long. 0
	 * @param timeout. Type: long. 从任务定义中复制的任务超时时间
	 * @param batchId. Type: long. 用来关联拆分批次号的主键
	 * @param createDayId. Type: long. 任务拆分的时间
	 * @param createTime. Type: java.sql.Timestamp. 任务拆分的时间
	 * @param startTime. Type: java.sql.Timestamp. 任务开始的时间
	 * @param lastUpdateTime. Type: java.sql.Timestamp. 在任务执行过程中，用来记录最后一次修改
	 */
	public SubTaskEntity buildSubTask(
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
		
		return new SubTaskEntity( 
			agentType,
			taskId,
			subTaskId,
			subtaskKey, 
			scriptFile, 
			scriptMain, 
			siteId,
			turnPageWaitTime,
			useSnapShot,
			batchId, 
			createTime, 
			startTime, 
			projectId,
			keyWord,
			forumId,
			threadId
		);
	}
	
	/**
	 *	Create an default Task Entity.
	 */
	public Task buildDefaultTask() {
		return new Task();
	}
	
	/**
	 * Create an Task Entity.
	 * @param siteId. Type: long. 可以用来描述该任务的简短文字，长度为25个汉字，或者50个英文数字符号
	 * @param name. Type: java.lang.String. 可以用来描述该任务的简短文字，长度为25个汉字，或者50个英文数字符号
	 * @param description. Type: java.lang.String. 用来描述任务的详细信息，长度为200个汉字，或者400个英文数字符号
	 * @param splitType. Type: byte. 用来执行该任务的脚本，保存的信息为配置文件中定义的脚本目录下的相对路径，长度为100个英文数字符号
	 * @param splitFile. Type: java.lang.String. 用来执行该任务的脚本，保存的信息为配置文件中定义的脚本目录下的相对路径，长度为100个英文数字符号
	 * @param splitMain. Type: java.lang.String. 执行脚本中的主函数及参数名称，长度为300个英文数字符号，例如：execute(&quot;http://www.163.com&quot;)
	 * @param scriptFile. Type: java.lang.String. 记录当前任务执行使用的脚本名称，此属性从任务定义表中带入
	 * @param dueTime. Type: java.sql.Timestamp. 如果超过指定时间，则任务将不再会被任务拆分程序拆分
	 * @param dueCheckFlag. Type: boolean. 初始时候为true。为false时不发送任务到期提醒邮件
	 * @param dueAlarmBefore. Type: int. 根据用户指定的天数，在任务到期前给用户发送提醒邮件
	 * @param timeout. Type: long. 单位：微秒，该值小于1800000（30分钟）的情况下，为未定义超时时间。不然，每个Task开始执行以后，如果超过指定时间，则任务进程停止
	 * @param lastSplitStatus. Type: int. 标志当前任务最后一次拆分是否错误：0. 正确，1. 出错
	 * @param lastSplitMsg. Type: java.lang.String. 当last_split_status =1时候，记录的拆分错误信息
	 * @param enableFlag. Type: byte. 标志当前任务的是否启用：0. 未启用，1. 启用
	 * @param enableSendFlag. Type: byte. 标志当前任务的是否启用：0. 未启用，1. 启用
	 * @param useSnapShot. Type: byte. 是否使用网页快照
	 * @param splitWaitTime. Type: long. 两次拆分操作的间隔等待时间，单位是毫秒
	 * @param priority. Type: int. 任务优先级，值越大，表明优先级越高
	 * @param startTime. Type: java.sql.Timestamp. 
	 * @param isEnableStartTime. Type: int. 
	 * @param lastSplitTime. Type: java.sql.Timestamp. 
	 * @param lastSplitKey. Type: java.lang.String. 拆分批号由随机字符串产生
	 * @param lastSplitCount. Type: int. 标志当前任务的拆分情况：0. 无需拆分，1. 需要拆分
	 * @param groupId. Type: long. 
	 * @param bddId. Type: long. 0
	 */
	public Task buildTask(int siteId, 
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
		
		return new Task(siteId, 
			name, 
			remark,
			agentType,
			agentGroupId,
			spliterGroupId,
			priority,
			splitFlag,
			sendFlag,
			useSnapShot,
			finishFlag,
			dueCheckFlag,
			dueTime,
			dueAlarmBefore,
			splitFile, 
			splitMain, 
			scriptFile,
			turnPageWaitTime,
			splitWaitTime, 
			startTime, 
			lastSplitTime,
			lastSplitCostTime,
			lastSplitKey, 
			lastSplitNum,
			subTaskNum,
			lastSplitStatus,
			lastSplitMsg
		);
	}
	
	/**
	 *	Create an default AgentInfo Entity.
	 */
	public AgentInfo buildDefaultAgentInfo() {
		return new AgentInfo();
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
	public AgentInfo buildAgentInfo(java.lang.String lanIpv4, 
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
		
		return new AgentInfo(lanIpv4, 
		wanIpv4, 
		port, 
		maxProcessNum, 
		processNum, 
		waitDtsFileCount, 
		enable, 
		available, 
		groupId, 
		registerTime, 
		lastAccessTime, 
		lastDTSTime
		);
	}
	
	/**
	 *	Create an default User Entity.
	 */
	public User buildDefaultUser() {
		return new User();
	}
	
	/**
	 * Create an User Entity.
	 * @param name. Type: java.lang.String. 
	 * @param email. Type: java.lang.String. 
	 */
	public User buildUser(java.lang.String name, 
		java.lang.String email
		) {
		
		return new User(name, 
		email
		);
	}
	
	/**
	 *	Create an default Dict Entity.
	 */
	public Dict buildDefaultDict() {
		return new Dict();
	}
	
	/**
	 * Create an Dict Entity.
	 * @param type. Type: java.lang.String. 
	 * @param value. Type: java.lang.String. 
	 * @param text. Type: java.lang.String. 
	 */
	public Dict buildDict(java.lang.String type, 
		java.lang.String value, 
		java.lang.String text
		) {
		
		return new Dict(type, 
		value, 
		text
		);
	}
	
	/**
	 *	Create an default Configuration Entity.
	 */
	public Configuration buildDefaultConfiguration() {
		return new Configuration();
	}
	
	/**
	 * Create an Configuration Entity.
	 * @param name. Type: java.lang.String. 
	 * @param value. Type: java.lang.String. 
	 */
	public Configuration buildConfiguration(java.lang.String name, 
		java.lang.String value
		) {
		
		return new Configuration(name, 
		value
		);
	}
	
	/**
	 *	Create an default TaskDueMessageUser Entity.
	 */
	public TaskDueMessageUser buildDefaultTaskDueMessageUser() {
		return new TaskDueMessageUser();
	}
	
	/**
	 * Create an TaskDueMessageUser Entity.
	 * @param taskId. Type: long. 
	 * @param alarmEmail. Type: java.lang.String. 
	 */
	public TaskDueMessageUser buildTaskDueMessageUser(long taskId, 
		java.lang.String alarmEmail
		) {
		
		return new TaskDueMessageUser(taskId, 
		alarmEmail
		);
	}
	
	/**
	 *	Create an default Account Entity.
	 */
	public Account buildDefaultAccount() {
		return new Account();
	}
	
	/**
	 * Create an Account Entity.
	 * @param siteId. Type: long. 
	 * @param account. Type: java.lang.String. 
	 * @param password. Type: java.lang.String. 
	 * @param lastGetTime. Type: java.sql.Timestamp. 
	 * @param lastGetKey. Type: java.lang.String. 
	 * @param invalid. Type: byte. 
	 */
	public Account buildAccount(long siteId, 
		java.lang.String account, 
		java.lang.String password, 
		java.sql.Timestamp lastGetTime, 
		java.lang.String lastGetKey, 
		byte invalid
		) {
		
		return new Account(siteId, 
		account, 
		password, 
		lastGetTime, 
		lastGetKey, 
		invalid
		);
	}
	
	/**
	 *	Create an default Group Entity.
	 */
	public Group buildDefaultGroup() {
		return new Group();
	}
	
	/**
	 * Create an Group Entity.
	 * @param name. Type: java.lang.String. 
	 * @param siteCount. Type: int. 
	 * @param agentCount. Type: int. 
	 */
	public Group buildGroup(java.lang.String name, 
		int siteCount, 
		int agentCount
		) {
		
		return new Group(name, 
		siteCount, 
		agentCount
		);
	}
	
	@Override
	public void execute(){
		RhinoContextGlobalRegister.getInstance().registParameter(
				"dcmanagementEntityFactory", 
				FACTORY);
	}
	
}

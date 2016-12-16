//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.cic.datacrawl.management.dao.base.TaskBaseDAO;
import com.cic.datacrawl.management.entity.Task;

public class TaskBaseDAOImpl extends JdbcDaoSupport implements TaskBaseDAO{
	
	public int count() {
		String sql = "select count(*) from t_task";
		return getJdbcTemplate().queryForInt(sql);
	}
		
	public Task getTask(int id){
		String sql = "SELECT * FROM T_TASK WHERE ID="+id;
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		if (processor.result.size() > 0){
			return (Task) processor.result.get(0);
		} else {
			return null;
		}
	}

	public Task[] getTasks(final int[] ids){
		if (ids.length == 0) {
			return new Task[0]; 
		}		
		StringBuffer sql = new StringBuffer("SELECT * FROM T_TASK WHERE ID IN (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(") order by id");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), processor);
        return (Task[])processor.result.toArray(new Task[0]);
	}

	public long addTask(final Task task){
		if (task == null) {
			throw new IllegalArgumentException("添加的Task为null");
		}
		String sql = "INSERT INTO T_TASK (SITE_ID, NAME, REMARK, AGENT_TYPE, AGENT_GROUP_ID, SPLITER_GROUP_ID, PRIORITY, SPLIT_FLAG, SEND_FLAG, FINISHED_FLAG, DUE_CHECK_FLAG, DUE_TIME, DUE_ALARM_BEFORE, SPLIT_FILE, SPLIT_MAIN, SCRIPT_FILE, SPLIT_WAIT_TIME, START_TIME, LAST_SPLIT_TIME, LAST_SPLIT_KEY, LAST_SPLIT_NUM, SPLIT_COST_TIME, LAST_SPLIT_STATUS, LAST_SPLIT_MSG)"
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
            	int index = 1;
            	ps.setInt(index++, task.getSiteId());
				ps.setString(index++, task.getName());
				ps.setString(index++, task.getRemark());
				ps.setByte(index++, task.getAgentType());
				ps.setInt(index++, task.getAgentGroupId());
				ps.setInt(index++, task.getSpliterGroupId());
				ps.setInt(index++, task.getPriority());
				ps.setByte(index++, task.getSplitFlag());
				ps.setByte(index++, task.getSendFlag());
				ps.setByte(index++, task.getFinishFlag());
				ps.setByte(index++, task.getDueCheckFlag());
				ps.setTimestamp(index++, task.getDueTime());
				ps.setInt(index++, task.getDueAlarmBefore());
				ps.setString(index++, task.getSplitFile());
				ps.setString(index++, task.getSplitMain());
				ps.setString(index++, task.getScriptFile());
				ps.setLong(index++, task.getSplitWaitTime());
				ps.setTimestamp(index++, task.getStartTime());
				ps.setTimestamp(index++, task.getLastSplitTime());
				ps.setString(index++, task.getLastSplitKey());
				ps.setInt(index++, task.getLastSplitNum());
				ps.setInt(index++, task.getLastSplitCostTime());
				ps.setByte(index++, task.getLastSplitStatus());
				ps.setString(index++, task.getLastSplitMsg());
            }
        });
        
		return theJdbcTemplate.queryForLong("SELECT LAST_INSERT_ID()");
	}

	public long[] addTasks(final Task[] tasks){
		if (tasks == null || tasks.length == 0) {
			throw new IllegalArgumentException("添加的Task为null");
		}
		
		long[] ret = new long[tasks.length];
		for (int i = 0; i < tasks.length; ++i) {
			ret[i] = addTask(tasks[i]);
		}
		return ret;
	}

	public int deleteTask(int id){
		String sql = "DELETE FROM T_TASK WHERE ID=" + id;
		return getJdbcTemplate().update(sql.toString());
	}
	
	public int deleteTasks(int[] ids){
		if (ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer("DELETE FROM T_TASK WHERE ID IN (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");
		return getJdbcTemplate().update(sql.toString());
	}
	
	public int saveTask(final Task task){
		String sql = "UPDATE T_TASK SET SITE_ID=?, NAME=?, REMARK=?, AGENT_TYPE=?, AGENT_GROUP_ID=?, SPLITER_GROUP_ID=?, PRIORITY=?, SPLIT_FLAG=?, SEND_FLAG=?, FINISHED_FLAG=?, DUE_CHECK_FLAG=?, DUE_TIME=?, DUE_ALARM_BEFORE=?, SPLIT_FILE=?, SPLIT_MAIN=?, SCRIPT_FILE=?, SPLIT_WAIT_TIME=?, START_TIME=?, LAST_SPLIT_TIME=?, LAST_SPLIT_KEY=?, LAST_SPLIT_NUM=? SPLIT_COST_TIME=?, LAST_SPLIT_STATUS=?, LAST_SPLIT_MSG=? WHERE ID=?";
		return getJdbcTemplate().update(sql, new PreparedStatementSetter(){
			public void setValues(PreparedStatement ps) throws SQLException{
				int index = 1;
				ps.setInt(index++, task.getSiteId());
				ps.setString(index++, task.getName());
				ps.setString(index++, task.getRemark());
				ps.setByte(index++, task.getAgentType());
				ps.setInt(index++, task.getAgentGroupId());
				ps.setInt(index++, task.getSpliterGroupId());
				ps.setInt(index++, task.getPriority());
				ps.setByte(index++, task.getSplitFlag());
				ps.setByte(index++, task.getSendFlag());
				ps.setByte(index++, task.getFinishFlag());
				ps.setByte(index++, task.getDueCheckFlag());
				ps.setTimestamp(index++, task.getDueTime());
				ps.setInt(index++, task.getDueAlarmBefore());
				ps.setString(index++, task.getSplitFile());
				ps.setString(index++, task.getSplitMain());
				ps.setString(index++, task.getScriptFile());
				ps.setLong(index++, task.getSplitWaitTime());
				ps.setTimestamp(index++, task.getStartTime());
				ps.setTimestamp(index++, task.getLastSplitTime());
				ps.setString(index++, task.getLastSplitKey());
				ps.setInt(index++, task.getLastSplitNum());
				ps.setInt(index++, task.getLastSplitCostTime());
				ps.setByte(index++, task.getLastSplitStatus());
				ps.setString(index++, task.getLastSplitMsg());
				ps.setInt(index++, task.getId());
			}
		});
	}
	
	public int[] saveTasks(final Task[] tasks){
		if (tasks == null || tasks.length == 0) {
			throw new IllegalArgumentException("更新的Task为null");
		}
		String sql = "UPDATE T_TASK SET SITE_ID=?, NAME=?, REMARK=?, AGENT_TYPE=?, AGENT_GROUP_ID=?, SPLITER_GROUP_ID=?, PRIORITY=?, SPLIT_FLAG=?, SEND_FLAG=?, FINISHED_FLAG=?, DUE_CHECK_FLAG=?, DUE_TIME=?, DUE_ALARM_BEFORE=?, SPLIT_FILE=?, SPLIT_MAIN=?, SCRIPT_FILE=?, SPLIT_WAIT_TIME=?, START_TIME=?, LAST_SPLIT_TIME=?, LAST_SPLIT_KEY=?, LAST_SPLIT_NUM=?, SPLIT_COST_TIME=?, LAST_SPLIT_STATUS=?, LAST_SPLIT_MSG=? WHERE ID=?";

		return getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
            	int index = 1;
            	ps.setInt(index++, tasks[i].getSiteId());
				ps.setString(index++, tasks[i].getName());
				ps.setString(index++, tasks[i].getRemark());
				ps.setByte(index++, tasks[i].getAgentType());
				ps.setInt(index++, tasks[i].getAgentGroupId());
				ps.setInt(index++, tasks[i].getSpliterGroupId());
				ps.setInt(index++, tasks[i].getPriority());
				ps.setByte(index++, tasks[i].getSplitFlag());
				ps.setByte(index++, tasks[i].getSendFlag());
				ps.setByte(index++, tasks[i].getFinishFlag());
				ps.setByte(index++, tasks[i].getDueCheckFlag());
				ps.setTimestamp(index++, tasks[i].getDueTime());
				ps.setInt(index++, tasks[i].getDueAlarmBefore());
				ps.setString(index++, tasks[i].getSplitFile());
				ps.setString(index++, tasks[i].getSplitMain());
				ps.setString(index++, tasks[i].getScriptFile());
				ps.setLong(index++, tasks[i].getSplitWaitTime());
				ps.setTimestamp(index++, tasks[i].getStartTime());
				ps.setTimestamp(index++, tasks[i].getLastSplitTime());
				ps.setString(index++, tasks[i].getLastSplitKey());
				ps.setInt(index++, tasks[i].getLastSplitNum());
				ps.setInt(index++, tasks[i].getLastSplitCostTime());
				ps.setByte(index++, tasks[i].getLastSplitStatus());
				ps.setString(index++, tasks[i].getLastSplitMsg());
				ps.setInt(index++, tasks[i].getId());
            }
            
			public int getBatchSize() {
				return tasks.length;
			}
        });
	}
	
	public Task[] queryByEnableFlag (final byte enableFlag, final int taskGroupId) {
				
		
		StringBuffer sql = new StringBuffer("SELECT * FROM T_TASK WHERE SPLIT_FLAG=? and SPLITER_GROUP_ID=?");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter (){
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setByte(1, enableFlag);
				ps.setInt(2, taskGroupId);
			}
		}
		,processor);
        return (Task[])processor.result.toArray(new Task[0]);
	}
	public Task[] queryBySiteId (final long siteId, final int taskGroupId) {
				
		StringBuffer sql = new StringBuffer("SELECT * FROM T_TASK where SITE_ID=? and SPLITER_GROUP_ID=? ");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter (){
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, siteId);
				ps.setInt(2, taskGroupId);
			}
		}
		,processor);
        return (Task[])processor.result.toArray(new Task[0]);
	}
	
	protected class CountProcess implements RowCallbackHandler {
		public CountProcess() {
		}
		
		private long count = 0;
		
		public long getCount() {
			return count;
		}
		
		@Override
		public void processRow(ResultSet rs) throws SQLException {
			count = rs.getLong("count");
		}
	}
	
	protected class IDProcessor implements RowCallbackHandler {
		public IDProcessor () {
		}
		public List<Long> result = new ArrayList<Long>();
		public void processRow(ResultSet rs) throws SQLException {
        	result.add(rs.getLong("id"));
		}
	};
	
	protected class EntityProcessor implements RowCallbackHandler {
		public EntityProcessor () {
		}
		public List<Task> result = new ArrayList<Task>();
		public void processRow(ResultSet rs) throws SQLException {
			Task task = new Task();
			task.setId(rs.getInt("ID"));
			task.setSiteId(rs.getInt("SITE_ID"));
        	task.setName(rs.getString("NAME"));
        	task.setRemark(rs.getString("REMARK"));
        	task.setAgentType(rs.getByte("AGENT_TYPE"));
        	task.setAgentGroupId(rs.getInt("AGENT_GROUP_ID"));
        	task.setSpliterGroupId(rs.getInt("SPLITER_GROUP_ID"));
        	task.setPriority(rs.getInt("PRIORITY"));
        	task.setSplitFlag(rs.getByte("SPLIT_FLAG"));
        	task.setSendFlag(rs.getByte("SEND_FLAG"));
        	task.setFinishFlag(rs.getByte("FINISHED_FLAG"));
        	task.setDueCheckFlag(rs.getByte("DUE_CHECK_FLAG"));
        	
        	java.sql.Timestamp dueTime = rs.getTimestamp("DUE_TIME");
        	if (dueTime != null) {
        		task.setDueTime(dueTime);
        	}
        	
        	task.setDueAlarmBefore(rs.getInt("DUE_ALARM_BEFORE"));
        	task.setSplitFile(rs.getString("SPLIT_FILE"));
        	task.setSplitMain(rs.getString("SPLIT_MAIN"));
        	task.setScriptFile(rs.getString("SCRIPT_FILE"));
        	task.setTurnPageWaitTime(rs.getInt("TURN_PAGE_WAITTIME"));
        	task.setSplitWaitTime(rs.getLong("SPLIT_WAIT_TIME"));
        	
        	java.sql.Timestamp startTime = rs.getTimestamp("START_TIME");
        	if (startTime != null) {
        		task.setStartTime(startTime);
        	}
        	
        	java.sql.Timestamp lastSplitTime = rs.getTimestamp("LAST_SPLIT_TIME");
        	if (lastSplitTime != null) {
        		task.setLastSplitTime(lastSplitTime);
        	}

        	task.setLastSplitKey(rs.getString("LAST_SPLIT_KEY"));
        	task.setLastSplitNum(rs.getInt("LAST_SPLIT_NUM"));
        	task.setLastSplitCostTime(rs.getInt("SPLIT_COST_TIME"));
        	task.setLastSplitStatus(rs.getByte("LAST_SPLIT_STATUS"));
        	task.setLastSplitMsg(rs.getString("LAST_SPLIT_MSG"));
        	
        	result.add(task);
		}
	}
}

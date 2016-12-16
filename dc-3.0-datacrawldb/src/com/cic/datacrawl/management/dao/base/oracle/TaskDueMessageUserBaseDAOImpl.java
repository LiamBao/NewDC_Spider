//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base.oracle;

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

import com.cic.datacrawl.management.dao.base.TaskDueMessageUserBaseDAO;
import com.cic.datacrawl.management.entity.TaskDueMessageUser;
import com.cic.datacrawl.management.utils.DBUtils;

public class TaskDueMessageUserBaseDAOImpl extends JdbcDaoSupport implements
		TaskDueMessageUserBaseDAO {

	public int count() {
		String sql = "select count(*) from t_task_due_message_user";
		return getJdbcTemplate().queryForInt(sql);
	}

	public TaskDueMessageUser[] getAllTaskDueMessageUser() {
		return getAllTaskDueMessageUser(0, -1);
	}

	public TaskDueMessageUser[] getAllTaskDueMessageUser(int startIndex,
			int limit) {
		String sql = "select id, task_id, alarm_email from t_task_due_message_user order by id";
		String paginationSQL = DBUtils.buildPageSQL(startIndex, limit, sql);
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(paginationSQL, processor);
		return (TaskDueMessageUser[]) processor.result
				.toArray(new TaskDueMessageUser[0]);
	}

	public TaskDueMessageUser getTaskDueMessageUser(long id) {
		String sql = "select id, task_id, alarm_email from t_task_due_message_user where id="
				+ id;
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		if (processor.result.size() > 0) {
			return (TaskDueMessageUser) processor.result.get(0);
		} else {
			return null;
		}
	}

	public TaskDueMessageUser[] getTaskDueMessageUsers(final long[] ids) {
		if (ids.length == 0) {
			return new TaskDueMessageUser[0];
		}
		StringBuffer sql = new StringBuffer(
				"select id, task_id, alarm_email from t_task_due_message_user where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(") order by id");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), processor);
		return (TaskDueMessageUser[]) processor.result
				.toArray(new TaskDueMessageUser[0]);
	}

	public long addTaskDueMessageUser(
			final TaskDueMessageUser taskDueMessageUser) {
		if (taskDueMessageUser == null) {
			throw new IllegalArgumentException("添加的TaskDueMessageUser为null");
		}

		String sqlSeq = "select S_T_TASK_DUE_MESSAGE_USER.nextval from dual";
		final long id = getJdbcTemplate().queryForLong(sqlSeq);

		String sql = "insert into t_task_due_message_user (id,task_id, alarm_email) "
				+ "values(?,?,?)";

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, id);
				ps.setLong(index++, taskDueMessageUser.getTaskId());
				ps.setString(index++, taskDueMessageUser.getAlarmEmail());
			}
		});

		return id;
	}

	public long[] addTaskDueMessageUsers(
			final TaskDueMessageUser[] taskDueMessageUsers) {
		if (taskDueMessageUsers == null || taskDueMessageUsers.length == 0) {
			throw new IllegalArgumentException("添加的TaskDueMessageUser为null");
		}

		long[] ret = new long[taskDueMessageUsers.length];
		for (int i = 0; i < taskDueMessageUsers.length; ++i) {
			ret[i] = addTaskDueMessageUser(taskDueMessageUsers[i]);
		}
		return ret;
	}

	public int deleteTaskDueMessageUser(long id) {
		String sql = "delete from t_task_due_message_user where id=?";
		return getJdbcTemplate().update(sql.toString(),
				new Object[] { new Long(id) },
				new int[] { java.sql.Types.BIGINT });
	}

	public int deleteTaskDueMessageUsers(long[] ids) {
		if (ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer(
				"delete from t_task_due_message_user where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");
		return getJdbcTemplate().update(sql.toString());
	}

	public int saveTaskDueMessageUser(
			final TaskDueMessageUser taskDueMessageUser) {
		String sql = "update t_task_due_message_user set task_id=?, alarm_email=? where id = ?";
		return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, taskDueMessageUser.getTaskId());
				ps.setString(index++, taskDueMessageUser.getAlarmEmail());
				ps.setLong(index++, taskDueMessageUser.getId());
			}
		});
	}

	public int[] saveTaskDueMessageUsers(
			final TaskDueMessageUser[] taskDueMessageUsers) {
		if (taskDueMessageUsers == null || taskDueMessageUsers.length == 0) {
			throw new IllegalArgumentException("更新的TaskDueMessageUser为null");
		}
		String sql = "update t_task_due_message_user set task_id=?, alarm_email=? where id = ?";

		return getJdbcTemplate().batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						int index = 1;
						ps.setLong(index++, taskDueMessageUsers[i].getTaskId());
						ps.setString(index++,
								taskDueMessageUsers[i].getAlarmEmail());
						ps.setLong(index++, taskDueMessageUsers[i].getId());
					}

					public int getBatchSize() {
						return taskDueMessageUsers.length;
					}
				});
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
		public IDProcessor() {
		}

		public List<Long> result = new ArrayList<Long>();

		public void processRow(ResultSet rs) throws SQLException {
			result.add(rs.getLong("id"));
		}
	};

	protected class EntityProcessor implements RowCallbackHandler {
		public EntityProcessor() {
		}

		public List<TaskDueMessageUser> result = new ArrayList<TaskDueMessageUser>();

		public void processRow(ResultSet rs) throws SQLException {
			TaskDueMessageUser taskDueMessageUser = new TaskDueMessageUser();
			taskDueMessageUser.setId(rs.getLong("id"));
			taskDueMessageUser.setTaskId(rs.getLong("task_id"));
			taskDueMessageUser.setAlarmEmail(rs.getString("alarm_email"));
			result.add(taskDueMessageUser);
		}
	};
}

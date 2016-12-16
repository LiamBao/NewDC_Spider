package com.cic.datacrawl.management.dao.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementSetter;

import com.cic.datacrawl.management.dao.TaskDAO;
import com.cic.datacrawl.management.dao.base.mysql.TaskBaseDAOImpl;
import com.cic.datacrawl.management.entity.Task;
import com.cic.datacrawl.management.manager.SiteManager;

public class TaskDAOImpl extends TaskBaseDAOImpl implements TaskDAO {
	@Override
	public int changeTaskEnableFlag(long[] ids, final byte enableFlag) {
		if (ids == null || ids.length == 0)
			return 0;

		StringBuilder sql = new StringBuilder(
				"UPDATE T_TASK SET SPLIT_FLAG=? WHERE ID IN(");

		for (int i = 0; i < ids.length; ++i) {
			if (i > 0)
				sql.append(", ");

			sql.append(ids[i]);
		}

		sql.append(")");
		return getJdbcTemplate().update(sql.toString(),
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setByte(1, enableFlag);
					}
				});
	}

	@Override
	public Task[] queryByEnableFlagInFinishedSite(final byte enableFlag, final int taskGroupId, final int rownum) {

		StringBuffer sql = new StringBuffer(
				"SELECT * FROM T_TASK WHERE SPLIT_FLAG=? AND SPLITER_GROUP_ID=? AND (START_TIME IS NULL OR START_TIME<=NOW()) AND (DUE_CHECK_FLAG=0 OR DUE_TIME>NOW()) AND SITE_ID IN (SELECT ID FROM T_SITE WHERE INFO_STATUS=?) LIMIT ?");
		
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setByte(1, enableFlag);
				ps.setInt(2, taskGroupId);
				ps.setByte(3, SiteManager.INFO_STATUS_READY);
				ps.setInt(4, rownum);
			}
		}, processor);
		
		return (Task[]) processor.result.toArray(new Task[0]);
	}

	@Override
	public int initTaskFinishedFlag(final int id) {
		String sql = "UPDATE T_TASK SET FINISHED_FLAG=0 WHERE ID=? and FINISHED_FLAG=1";
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++" + sql);
		return getJdbcTemplate().update(sql.toString(),
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setInt(1, id);
					}
				});
	};
}

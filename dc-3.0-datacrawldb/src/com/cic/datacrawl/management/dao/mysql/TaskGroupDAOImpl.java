package com.cic.datacrawl.management.dao.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.cic.datacrawl.management.dao.TaskGroupDAO;

public class TaskGroupDAOImpl extends JdbcDaoSupport implements TaskGroupDAO{
	
	public void registerSpliter (final int taskGroupId, final String lanIP) {
		StringBuffer sql = new StringBuffer(
				"UPDATE T_SPLITER_GROUP SET SPLITER_IP=?, START_TIME=NOW() WHERE ID=?");
		
		getJdbcTemplate().update(sql.toString(),
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, lanIP);
						ps.setInt(2, taskGroupId);
					}
				});
	}
	
	public void finishedSpliter (final int taskGroupId, final byte errorFlag, final String exception) {
		StringBuffer sql = new StringBuffer(
				"UPDATE T_SPLITER_GROUP SET FINISH_TIME=NOW(), ERROR_FLAG=?, ERROR_LOG=? WHERE ID=?");
		
		getJdbcTemplate().update(sql.toString(),
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setByte(1, errorFlag);
						ps.setString(2, exception);
						ps.setInt(3, taskGroupId);
					}
				});
	}
	
}

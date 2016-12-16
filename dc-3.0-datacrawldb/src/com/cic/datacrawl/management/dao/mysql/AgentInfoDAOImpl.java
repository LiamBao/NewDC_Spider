package com.cic.datacrawl.management.dao.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.cic.datacrawl.management.dao.AgentInfoDAO;
import com.cic.datacrawl.management.dao.base.mysql.AgentInfoBaseDAOImpl;
import com.cic.datacrawl.management.entity.AgentInfo;

public class AgentInfoDAOImpl extends AgentInfoBaseDAOImpl implements AgentInfoDAO {

	@Override
	public AgentInfo[] queryByEnable(byte enable, int startIndex, int limit) {
		String sql = "select id, lan_ipv4, wan_ipv4, port, max_process_num, process_num, waiting_dts_file_count, is_enable, is_available, group_id, register_time, last_access_time from t_agent_info "
						+ "where is_enable = "
						+ enable
						+ " order by id";
		if (limit > 0) {
			sql = sql + "limit " + limit;
			if (startIndex > 0) {
				sql = sql + "," + startIndex;
			}
		}
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		return (AgentInfo[]) processor.result.toArray(new AgentInfo[0]);
	}

	@Override
	public AgentInfo[] queryByIP(String ip, int startIndex, int limit) {
		String sql = "select id, lan_ipv4, wan_ipv4, port, max_process_num, process_num, waiting_dts_file_count, is_enable, is_available, group_id, register_time, last_access_time, last_dts_time from t_agent_info";
		if (ip != null && ip.trim().length() > 0) {
			sql = sql + " where lan_ipv4 like '%" + ip + "%'";
		}
		sql = sql + " order by id";
		if (limit > 0) {
			sql = sql + "limit " + limit;
			if (startIndex > 0) {
				sql = sql + "," + startIndex;
			}
		}
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		return (AgentInfo[]) processor.result.toArray(new AgentInfo[0]);
	}

	@Override
	public AgentInfo[] queryByIPAndEnable(String ip, byte enable, int startIndex, int limit) {
		String sql = "select id, lan_ipv4, wan_ipv4, port, max_process_num, process_num, waiting_dts_file_count, is_enable, is_available, group_id, register_time, last_access_time from t_agent_info "
						+ "where is_enable = "
						+ enable
						+ " and lan_ipv4 like '%"
						+ ip
						+ "%' order by id";
		if (limit > 0) {
			sql = sql + "limit " + limit;
			if (startIndex > 0) {
				sql = sql + "," + startIndex;
			}
		}
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		return (AgentInfo[]) processor.result.toArray(new AgentInfo[0]);
	}

	// TODO:

	@Override
	public void changeEnableFlag(long[] ids, final byte enable) {
		StringBuilder sql = new StringBuilder("update t_agent_info set is_enable=? where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setByte(1, enable);
			}
		});
	}

	@Override
	public void changeGroupIds(long[] ids, final long groupId) {
		StringBuilder sql = new StringBuilder("update t_agent_info set group_id=? where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, groupId);
			}
		});
	}

	@Override
	public AgentInfo[] queryByGroupIds(long[] groupIds) {
		if (groupIds.length == 0) {
			return new AgentInfo[0];
		}
		StringBuffer sql = new StringBuffer(
				"select id, lan_ipv4, wan_ipv4, port, max_process_num, process_num, waiting_dts_file_count, is_enable, is_available, group_id, register_time, last_access_time from t_agent_info where group_id in (");
		for (int i = 0; i < groupIds.length - 1; i++) {
			sql.append(groupIds[i]).append(",");
		}
		sql.append(groupIds[groupIds.length - 1]).append(")");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), processor);
		return (AgentInfo[]) processor.result.toArray(new AgentInfo[0]);
	}
}

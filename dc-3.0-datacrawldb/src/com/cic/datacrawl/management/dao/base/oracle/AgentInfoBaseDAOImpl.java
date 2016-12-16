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

import com.cic.datacrawl.management.dao.base.AgentInfoBaseDAO;
import com.cic.datacrawl.management.entity.AgentInfo;
import com.cic.datacrawl.management.utils.DBUtils;

public class AgentInfoBaseDAOImpl extends JdbcDaoSupport implements
		AgentInfoBaseDAO {

	public int count() {
		String sql = "select count(1) from t_agent_info";
		return getJdbcTemplate().queryForInt(sql);
	}

	public AgentInfo[] getAllAgentInfo() {
		return getAllAgentInfo(0, -1);
	}

	public AgentInfo[] getAllAgentInfo(int startIndex, int limit) {
		String sql = "select id, lan_ipv4, wan_ipv4, port, max_process_num, process_num, waiting_dts_file_count, is_enable, is_available, group_id, register_time, last_access_time, last_dts_time from t_agent_info order by id";

		String paginationSQL = DBUtils.buildPageSQL(startIndex, limit, sql);
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(paginationSQL, processor);
		return (AgentInfo[]) processor.result.toArray(new AgentInfo[0]);
	}

	public AgentInfo getAgentInfo(long id) {
		String sql = "select id, lan_ipv4, wan_ipv4, port, max_process_num, process_num, waiting_dts_file_count, is_enable, is_available, group_id, register_time, last_access_time, last_dts_time from t_agent_info where id="
				+ id;
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		if (processor.result.size() > 0) {
			return (AgentInfo) processor.result.get(0);
		} else {
			return null;
		}
	}

	public AgentInfo[] getAgentInfos(final long[] ids) {
		if (ids.length == 0) {
			return new AgentInfo[0];
		}
		StringBuffer sql = new StringBuffer(
				"select id, lan_ipv4, wan_ipv4, port, max_process_num, process_num, waiting_dts_file_count, is_enable, is_available, group_id, register_time, last_access_time, last_dts_time from t_agent_info where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(") order by id");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), processor);
		return (AgentInfo[]) processor.result.toArray(new AgentInfo[0]);
	}

	public long addAgentInfo(final AgentInfo agentInfo) {
		if (agentInfo == null) {
			throw new IllegalArgumentException("添加的AgentInfo为null");
		}
		
		String sqlSeq = "select S_T_AGENT_INFO.nextval from dual";
		final long id = getJdbcTemplate().queryForLong(sqlSeq);
		
		String sql = "insert into t_agent_info (id,lan_ipv4, wan_ipv4, port, max_process_num, process_num, waiting_dts_file_count, is_enable, is_available, group_id, register_time, last_access_time, last_dts_time) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, id);
				ps.setString(index++, agentInfo.getLanIpv4());
				ps.setString(index++, agentInfo.getWanIpv4());
				ps.setInt(index++, agentInfo.getPort());
				ps.setInt(index++, agentInfo.getMaxProcessNum());
				ps.setInt(index++, agentInfo.getProcessNum());
				ps.setInt(index++, agentInfo.getWaitDtsFileCount());
				ps.setInt(index++, 1);
				ps.setByte(index++, agentInfo.getAvailable());
				ps.setLong(index++, agentInfo.getGroupId());
				ps.setTimestamp(index++, agentInfo.getRegisterTime());
				ps.setTimestamp(index++, agentInfo.getLastAccessTime());
				ps.setTimestamp(index++, agentInfo.getLastDTSTime());
			}
		});

		return id;
	}

	public long[] addAgentInfos(final AgentInfo[] agentInfos) {
		if (agentInfos == null || agentInfos.length == 0) {
			throw new IllegalArgumentException("添加的AgentInfo为null");
		}

		long[] ret = new long[agentInfos.length];
		for (int i = 0; i < agentInfos.length; ++i) {
			ret[i] = addAgentInfo(agentInfos[i]);
		}
		return ret;
	}

	public int deleteAgentInfo(long id) {
		String sql = "delete from t_agent_info where id=?";
		return getJdbcTemplate().update(sql.toString(),
				new Object[] { new Long(id) },
				new int[] { java.sql.Types.BIGINT });
	}

	public int deleteAgentInfos(long[] ids) {
		if (ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer(
				"delete from t_agent_info where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");
		return getJdbcTemplate().update(sql.toString());
	}

	public int saveAgentInfo(final AgentInfo agentInfo) {
		String sql = "update t_agent_info set lan_ipv4=?, wan_ipv4=?, port=?, max_process_num=?, process_num=?, waiting_dts_file_count=?, is_enable=?, is_available=?, group_id=?, register_time=?, last_access_time=?, last_dts_time=? where id = ?";
		return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, agentInfo.getLanIpv4());
				ps.setString(index++, agentInfo.getWanIpv4());
				ps.setInt(index++, agentInfo.getPort());
				ps.setInt(index++, agentInfo.getMaxProcessNum());
				ps.setInt(index++, agentInfo.getProcessNum());
				ps.setInt(index++, agentInfo.getWaitDtsFileCount());
				ps.setByte(index++, agentInfo.getEnable());
				ps.setByte(index++, agentInfo.getAvailable());
				ps.setLong(index++, agentInfo.getGroupId());
				ps.setTimestamp(index++, agentInfo.getRegisterTime());
				ps.setTimestamp(index++, agentInfo.getLastAccessTime());
				ps.setTimestamp(index++, agentInfo.getLastDTSTime());
				ps.setLong(index++, agentInfo.getId());
			}
		});
	}

	public int[] saveAgentInfos(final AgentInfo[] agentInfos) {
		if (agentInfos == null || agentInfos.length == 0) {
			throw new IllegalArgumentException("更新的AgentInfo为null");
		}
		String sql = "update t_agent_info set lan_ipv4=?, wan_ipv4=?, port=?, max_process_num=?, process_num=?, waiting_dts_file_count=?, is_enable=?, is_available=?, group_id=?, register_time=?, last_access_time=?, last_dts_time=? where id = ?";

		return getJdbcTemplate().batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						int index = 1;
						ps.setString(index++, agentInfos[i].getLanIpv4());
						ps.setString(index++, agentInfos[i].getWanIpv4());
						ps.setInt(index++, agentInfos[i].getPort());
						ps.setInt(index++, agentInfos[i].getMaxProcessNum());
						ps.setInt(index++, agentInfos[i].getProcessNum());
						ps.setInt(index++, agentInfos[i].getWaitDtsFileCount());
						ps.setByte(index++, agentInfos[i].getEnable());
						ps.setByte(index++, agentInfos[i].getAvailable());
						ps.setLong(index++, agentInfos[i].getGroupId());
						ps.setTimestamp(index++,
								agentInfos[i].getRegisterTime());
						ps.setTimestamp(index++,
								agentInfos[i].getLastAccessTime());
						ps.setTimestamp(index++, agentInfos[i].getLastDTSTime());
						ps.setLong(index++, agentInfos[i].getId());
					}

					public int getBatchSize() {
						return agentInfos.length;
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

		public List<AgentInfo> result = new ArrayList<AgentInfo>();

		public void processRow(ResultSet rs) throws SQLException {
			AgentInfo agentInfo = new AgentInfo();
			agentInfo.setId(rs.getLong("id"));
			agentInfo.setLanIpv4(rs.getString("lan_ipv4"));
			agentInfo.setWanIpv4(rs.getString("wan_ipv4"));
			agentInfo.setPort(rs.getInt("port"));
			agentInfo.setMaxProcessNum(rs.getInt("max_process_num"));
			agentInfo.setProcessNum(rs.getInt("process_num"));
			agentInfo.setWaitDtsFileCount(rs.getInt("waiting_dts_file_count"));
			agentInfo.setEnable(rs.getByte("is_enable"));
			agentInfo.setAvailable(rs.getByte("is_available"));
			agentInfo.setGroupId(rs.getLong("group_id"));
			java.sql.Timestamp registerTime = rs.getTimestamp("register_time");
			if (registerTime != null) {
				agentInfo.setRegisterTime(registerTime);
			}
			java.sql.Timestamp lastAccessTime = rs
					.getTimestamp("last_access_time");
			if (lastAccessTime != null) {
				agentInfo.setLastAccessTime(lastAccessTime);
			}
			java.sql.Timestamp lastDTSTime = rs.getTimestamp("last_dts_time");
			if (lastDTSTime != null) {
				agentInfo.setLastDTSTime(lastDTSTime);
			}
			result.add(agentInfo);
		}
	};
}

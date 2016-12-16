package com.cic.datacrawl.management.dao.oracle;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementSetter;

import com.cic.datacrawl.management.dao.GroupDAO;
import com.cic.datacrawl.management.dao.base.oracle.GroupBaseDAOImpl;
import com.cic.datacrawl.management.entity.Group;
import com.cic.datacrawl.management.utils.DBUtils;

public class GroupDAOImpl extends GroupBaseDAOImpl implements GroupDAO {

	@Override
	public void changeAgentCount(final long groupId, final int count) {
		String sql = "update t_group set agent_count=? where id = ?";
		getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setInt(index++, count);
				ps.setLong(index++, groupId);
			}
		});
	}

	@Override
	public void changeSiteCount(final long groupId, final int count) {
		String sql = "update t_group set site_count=? where id = ?";
		getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setInt(index++, count);
				ps.setLong(index++, groupId);
			}
		});
	}

	@Override
	public void cleanAgentCount(long[] groupIds) {
		if (groupIds == null || groupIds.length == 0)
			return;
		StringBuilder sql = new StringBuilder(
				"update t_group set site_count=0 where id in (");
		for (int i = 0; i < groupIds.length - 1; i++) {
			sql.append(groupIds[i]).append(",");
		}
		sql.append(groupIds[groupIds.length - 1]).append(")");
		getJdbcTemplate().update(sql.toString());
	}

	@Override
	public void cleanSiteCount(long[] groupIds) {
		if (groupIds == null || groupIds.length == 0)
			return;
		StringBuilder sql = new StringBuilder(
				"update t_group set site_count=0 where id in (");
		for (int i = 0; i < groupIds.length - 1; i++) {
			sql.append(groupIds[i]).append(",");
		}
		sql.append(groupIds[groupIds.length - 1]).append(")");
		getJdbcTemplate().update(sql.toString());
	}

	@Override
	public Group[] queryByName(String name, int startIndex, int limit) {
		String sql = "select id, group_name, site_count, agent_count from t_group where group_name like '%"
				+ name + "%' order by id";
		String paginationSQL = DBUtils.buildPageSQL(startIndex, limit, sql);

		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(paginationSQL, processor);
		return (Group[]) processor.result.toArray(new Group[0]);
	}

	@Override
	public int calcSumSiteCount(long[] groupId) {
		if (groupId == null || groupId.length == 0)
			return 0;
		StringBuilder sql = new StringBuilder(
				"select sum(site_count) from t_group where  id in (");
		for (int i = 0; i < groupId.length - 1; i++) {
			sql.append(groupId[i]).append(",");
		}
		sql.append(groupId[groupId.length - 1]).append(")");

		return getJdbcTemplate().queryForInt(sql.toString());
	}

	@Override
	public int calcSumAgentCount(long[] groupId) {
		if (groupId == null || groupId.length == 0)
			return 0;
		StringBuilder sql = new StringBuilder(
				"select sum(agent_count) from t_group where  id in (");
		for (int i = 0; i < groupId.length - 1; i++) {
			sql.append(groupId[i]).append(",");
		}
		sql.append(groupId[groupId.length - 1]).append(")");

		return getJdbcTemplate().queryForInt(sql.toString());
	}

	@Override
	public Group[] getAllGroupsAndCalcSitesAndAgents(String groupName) {
		String sql = "select id, group_name, site.site_count, agent.agent_count from t_group l"
				+ "eft join (select group_id, count(0) as site_count from t_site where info_status = 1 group by group_id) as site on site.group_id = id "
				+ "left join (select group_id, count(0) as agent_count from t_agent_info group by group_id) as agent on agent.group_id = id";
		if (groupName != null && groupName.trim().length() > 0)
			sql = sql + " where group_name like '%" + groupName + "%'";
		sql = sql + " order by id";

		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		return (Group[]) processor.result.toArray(new Group[0]);
	}

}

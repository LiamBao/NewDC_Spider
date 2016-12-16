//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.cic.datacrawl.management.dao.base.GroupBaseDAO;
import com.cic.datacrawl.management.entity.Group;

public class GroupBaseDAOImpl extends JdbcDaoSupport implements GroupBaseDAO{
	
	private static final Logger log = Logger.getLogger(GroupBaseDAOImpl.class);
	
	public int count() {
		String sql = "select count(*) from t_group";
		log.info("SQL:"+ sql);
		return getJdbcTemplate().queryForInt(sql);
	}
	
	public Group[] getAllGroup() {
		return getAllGroup(0, -1);
	}
	
	public Group[] getAllGroup(int startIndex, int limit) {
		String sql = "select id, group_name, site_count, agent_count from t_group order by id";
		if(limit >0){
			sql = sql + "limit " + limit;
			if(startIndex > 0){
				sql = sql+ ","+ startIndex; 
			}		
		}
		EntityProcessor processor = new EntityProcessor();
		log.info("SQL:"+ sql);
		getJdbcTemplate().query(sql, processor);
		return (Group[])processor.result.toArray(new Group[0]);
	}

	
	public Group getGroup(long id){
		String sql = "select id, group_name, site_count, agent_count from t_group where id="+id;
		EntityProcessor processor = new EntityProcessor();
		log.info("SQL:"+ sql);
		getJdbcTemplate().query(sql, processor);
		if (processor.result.size() > 0){
			return (Group) processor.result.get(0);
		} else {
			return null;
		}
	}

	public Group[] getGroups(final long[] ids){
		if (ids.length == 0) {
			return new Group[0]; 
		}		
		StringBuffer sql = new StringBuffer("select id, group_name, site_count, agent_count from t_group where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(") order by id");
		EntityProcessor processor = new EntityProcessor();
		log.info("SQL:"+ sql);
		getJdbcTemplate().query(sql.toString(), processor);
        return (Group[])processor.result.toArray(new Group[0]);
	}

	public long addGroup(final Group group){
		if (group == null) {
			throw new IllegalArgumentException("添加的Group为null");
		}
		String sql = "insert into t_group (group_name, site_count, agent_count) "
			+ "values(?,?,?)";

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		log.info("SQL:"+ sql);
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
            	int index = 1;
				ps.setString(index++, group.getName());
				ps.setInt(index++, group.getSiteCount());
				ps.setInt(index++, group.getAgentCount());
            }
        });
        
		return theJdbcTemplate.queryForLong("SELECT LAST_INSERT_ID()");
	}

	public long[] addGroups(final Group[] groups){
		if (groups == null || groups.length == 0) {
			throw new IllegalArgumentException("添加的Group为null");
		}
		
		long[] ret = new long[groups.length];
		for (int i = 0; i < groups.length; ++i) {
			ret[i] = addGroup(groups[i]);
		}
		return ret;
	}

	public int deleteGroup(long id){
		String sql = "delete from t_group where id=?";
		log.info("SQL:"+ sql);
		return getJdbcTemplate().update(sql.toString(), new Object[]{new Long(id)}, new int[]{java.sql.Types.BIGINT});
	}
	
	public int deleteGroups(long[] ids){
		if (ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer("delete from t_group where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");
		log.info("SQL:"+ sql);
		return getJdbcTemplate().update(sql.toString());
	}
	
	public int saveGroup(final Group group){
		String sql = "update t_group set group_name=?, site_count=?, agent_count=? where id = ?";
		log.info("SQL:"+ sql);
		return getJdbcTemplate().update(sql, new PreparedStatementSetter(){
			public void setValues(PreparedStatement ps) throws SQLException{
				int index = 1;
				ps.setString(index++, group.getName());
				ps.setInt(index++, group.getSiteCount());
				ps.setInt(index++, group.getAgentCount());
				ps.setLong(index++, group.getId());
			}
		});
	}
	
	public int[] saveGroups(final Group[] groups){
		if (groups == null || groups.length == 0) {
			throw new IllegalArgumentException("更新的Group为null");
		}
		String sql = "update t_group set group_name=?, site_count=?, agent_count=? where id = ?";
		log.info("SQL:"+ sql);
		return getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
            	int index = 1;
				ps.setString(index++, groups[i].getName());
				ps.setInt(index++, groups[i].getSiteCount());
				ps.setInt(index++, groups[i].getAgentCount());
            	ps.setLong(index++, groups[i].getId());
            }
            
			public int getBatchSize() {
				return groups.length;
			}
        });
	}
	
	public Group queryByName (final java.lang.String name) {
				
		StringBuffer sql = new StringBuffer("select id, group_name, site_count, agent_count from t_group where group_name=? ");
		EntityProcessor processor = new EntityProcessor();
		log.info("SQL:"+ sql);
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter (){
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, name);
			}
		}
		, processor);
		
        if (processor.result.size()>0){
			return (Group) processor.result.get(0);
		} else {
			return null;
		}
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
		public List<Group> result = new ArrayList<Group>();
		public void processRow(ResultSet rs) throws SQLException {
			Group group = new Group();
        	group.setId(rs.getLong("id"));
        	group.setName(rs.getString("group_name"));
        	group.setSiteCount(rs.getInt("site_count"));
        	group.setAgentCount(rs.getInt("agent_count"));
        	result.add(group);
		}
	};
}

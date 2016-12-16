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

import com.cic.datacrawl.management.dao.base.UserBaseDAO;
import com.cic.datacrawl.management.entity.User;

public class UserBaseDAOImpl extends JdbcDaoSupport implements UserBaseDAO{
	
	public int count() {
		String sql = "select count(*) from t_user";
		return getJdbcTemplate().queryForInt(sql);
	}
	
	public User[] getAllUser() {
		return getAllUser(0, -1);
	}
	
	public User[] getAllUser(int startIndex, int limit) {
		String sql = "select id, name, email from t_user order by id";
		if(limit >0){
			sql = sql + "limit " + limit;
			if(startIndex > 0){
				sql = sql+ ","+ startIndex; 
			}		
		}
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		return (User[])processor.result.toArray(new User[0]);
	}

	
	public User getUser(long id){
		String sql = "select id, name, email from t_user where id="+id;
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		if (processor.result.size() > 0){
			return (User) processor.result.get(0);
		} else {
			return null;
		}
	}

	public User[] getUsers(final long[] ids){
		if (ids.length == 0) {
			return new User[0]; 
		}		
		StringBuffer sql = new StringBuffer("select id, name, email from t_user where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(") order by id");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), processor);
        return (User[])processor.result.toArray(new User[0]);
	}

	public long addUser(final User user){
		if (user == null) {
			throw new IllegalArgumentException("添加的User为null");
		}
		String sql = "insert into t_user (name, email) "
			+ "values(?,?)";

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
            	int index = 1;
				ps.setString(index++, user.getName());
				ps.setString(index++, user.getEmail());
            }
        });
        
		return theJdbcTemplate.queryForLong("SELECT LAST_INSERT_ID()");
	}

	public long[] addUsers(final User[] users){
		if (users == null || users.length == 0) {
			throw new IllegalArgumentException("添加的User为null");
		}
		
		long[] ret = new long[users.length];
		for (int i = 0; i < users.length; ++i) {
			ret[i] = addUser(users[i]);
		}
		return ret;
	}

	public int deleteUser(long id){
		String sql = "delete from t_user where id=?";
		return getJdbcTemplate().update(sql.toString(), new Object[]{new Long(id)}, new int[]{java.sql.Types.BIGINT});
	}
	
	public int deleteUsers(long[] ids){
		if (ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer("delete from t_user where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");
		return getJdbcTemplate().update(sql.toString());
	}
	
	public int saveUser(final User user){
		String sql = "update t_user set name=?, email=? where id = ?";
		return getJdbcTemplate().update(sql, new PreparedStatementSetter(){
			public void setValues(PreparedStatement ps) throws SQLException{
				int index = 1;
				ps.setString(index++, user.getName());
				ps.setString(index++, user.getEmail());
				ps.setLong(index++, user.getId());
			}
		});
	}
	
	public int[] saveUsers(final User[] users){
		if (users == null || users.length == 0) {
			throw new IllegalArgumentException("更新的User为null");
		}
		String sql = "update t_user set name=?, email=? where id = ?";

		return getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
            	int index = 1;
				ps.setString(index++, users[i].getName());
				ps.setString(index++, users[i].getEmail());
            	ps.setLong(index++, users[i].getId());
            }
            
			public int getBatchSize() {
				return users.length;
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
		public List<User> result = new ArrayList<User>();
		public void processRow(ResultSet rs) throws SQLException {
			User user = new User();
        	user.setId(rs.getLong("id"));
        	user.setName(rs.getString("name"));
        	user.setEmail(rs.getString("email"));
        	result.add(user);
		}
	};
}

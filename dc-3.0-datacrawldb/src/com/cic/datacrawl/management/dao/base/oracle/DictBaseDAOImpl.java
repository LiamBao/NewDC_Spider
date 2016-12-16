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

import com.cic.datacrawl.management.dao.base.DictBaseDAO;
import com.cic.datacrawl.management.entity.Dict;
import com.cic.datacrawl.management.utils.DBUtils;

public class DictBaseDAOImpl extends JdbcDaoSupport implements DictBaseDAO{
	
	public int count() {
		String sql = "select count(*) from t_dict";
		return getJdbcTemplate().queryForInt(sql);
	}
	
	public Dict[] getAllDict() {
		return getAllDict(0, -1);
	}
	
	public Dict[] getAllDict(int startIndex, int limit) {
		String sql = "select id, type, value, text from t_dict order by id";
		String paginationSQL = DBUtils.buildPageSQL(startIndex, limit, sql);
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(paginationSQL, processor);
		return (Dict[])processor.result.toArray(new Dict[0]);
	}

	
	public Dict getDict(long id){
		String sql = "select id, type, value, text from t_dict where id="+id;
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		if (processor.result.size() > 0){
			return (Dict) processor.result.get(0);
		} else {
			return null;
		}
	}

	public Dict[] getDicts(final long[] ids){
		if (ids.length == 0) {
			return new Dict[0]; 
		}		
		StringBuffer sql = new StringBuffer("select id, type, value, text from t_dict where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(") order by id");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), processor);
        return (Dict[])processor.result.toArray(new Dict[0]);
	}

	public long addDict(final Dict dict){
		if (dict == null) {
			throw new IllegalArgumentException("添加的Dict为null");
		}
		
		String sqlSeq = "select S_T_DICT.nextval from dual";
		final long id = getJdbcTemplate().queryForLong(sqlSeq);
		
		
		String sql = "insert into t_dict (id,type, value, text) "
			+ "values(?,?,?,?)";

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
            	int index = 1;
            	ps.setLong(index++, id);
				ps.setString(index++, dict.getType());
				ps.setString(index++, dict.getValue());
				ps.setString(index++, dict.getText());
            }
        });
        
		return id;
	}

	public long[] addDicts(final Dict[] dicts){
		if (dicts == null || dicts.length == 0) {
			throw new IllegalArgumentException("添加的Dict为null");
		}
		
		long[] ret = new long[dicts.length];
		for (int i = 0; i < dicts.length; ++i) {
			ret[i] = addDict(dicts[i]);
		}
		return ret;
	}

	public int deleteDict(long id){
		String sql = "delete from t_dict where id=?";
		return getJdbcTemplate().update(sql.toString(), new Object[]{new Long(id)}, new int[]{java.sql.Types.BIGINT});
	}
	
	public int deleteDicts(long[] ids){
		if (ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer("delete from t_dict where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");
		return getJdbcTemplate().update(sql.toString());
	}
	
	public int saveDict(final Dict dict){
		String sql = "update t_dict set type=?, value=?, text=? where id = ?";
		return getJdbcTemplate().update(sql, new PreparedStatementSetter(){
			public void setValues(PreparedStatement ps) throws SQLException{
				int index = 1;
				ps.setString(index++, dict.getType());
				ps.setString(index++, dict.getValue());
				ps.setString(index++, dict.getText());
				ps.setLong(index++, dict.getId());
			}
		});
	}
	
	public int[] saveDicts(final Dict[] dicts){
		if (dicts == null || dicts.length == 0) {
			throw new IllegalArgumentException("更新的Dict为null");
		}
		String sql = "update t_dict set type=?, value=?, text=? where id = ?";

		return getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
            	int index = 1;
				ps.setString(index++, dicts[i].getType());
				ps.setString(index++, dicts[i].getValue());
				ps.setString(index++, dicts[i].getText());
            	ps.setLong(index++, dicts[i].getId());
            }
            
			public int getBatchSize() {
				return dicts.length;
			}
        });
	}
	
	public Dict[] queryByType (final java.lang.String type) {
				
		StringBuffer sql = new StringBuffer("select id, type, value, text from t_dict where type=? ");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter (){
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, type);
			}
		}
		,processor);
        return (Dict[])processor.result.toArray(new Dict[0]);
	}
	public Dict queryByTypeValue (final java.lang.String type, final java.lang.String value) {
				
		StringBuffer sql = new StringBuffer("select id, type, value, text from t_dict where type=? AND value=? ");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter (){
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, type);
				ps.setString(2, value);
			}
		}
		, processor);
		
        if (processor.result.size()>0){
			return (Dict) processor.result.get(0);
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
		public List<Dict> result = new ArrayList<Dict>();
		public void processRow(ResultSet rs) throws SQLException {
			Dict dict = new Dict();
        	dict.setId(rs.getLong("id"));
        	dict.setType(rs.getString("type"));
        	dict.setValue(rs.getString("value"));
        	dict.setText(rs.getString("text"));
        	result.add(dict);
		}
	};
}

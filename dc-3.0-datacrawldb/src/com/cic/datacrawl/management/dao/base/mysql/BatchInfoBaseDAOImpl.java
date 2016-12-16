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

import com.cic.datacrawl.management.dao.base.BatchInfoBaseDAO;
import com.cic.datacrawl.management.entity.BatchInfo;

public class BatchInfoBaseDAOImpl extends JdbcDaoSupport implements BatchInfoBaseDAO{
	
	private static final Logger log = Logger.getLogger(BatchInfoBaseDAOImpl.class);
	
	public int count() {
		String sql = "select count(*) from t_batch";
		log.info("SQL:"+ sql);
		return getJdbcTemplate().queryForInt(sql);
	}
	
	public BatchInfo[] getAllBatchInfo() {
		return getAllBatchInfo(0, -1);
	}
	
	public BatchInfo[] getAllBatchInfo(int startIndex, int limit) {
		String sql = "select id, batch_name, task_id, split_count, split_time from t_batch order by id";
		if(limit >0){
			sql = sql + "limit " + limit;
			if(startIndex > 0){
				sql = sql+ ","+ startIndex; 
			}		
		}
		EntityProcessor processor = new EntityProcessor();
		log.info("SQL:"+ sql);
		getJdbcTemplate().query(sql, processor);
		return (BatchInfo[])processor.result.toArray(new BatchInfo[0]);
	}

	
	public BatchInfo getBatchInfo(int id){
		String sql = "select id, batch_name, task_id, split_count, split_time from t_batch where id="+id;
		EntityProcessor processor = new EntityProcessor();
		log.info("SQL:"+ sql);
		getJdbcTemplate().query(sql, processor);
		if (processor.result.size() > 0){
			return (BatchInfo) processor.result.get(0);
		} else {
			return null;
		}
	}

	public BatchInfo[] getBatchInfos(final int[] ids){
		if (ids.length == 0) {
			return new BatchInfo[0]; 
		}		
		StringBuffer sql = new StringBuffer("select id, batch_name, task_id, split_count, split_time from t_batch where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(") order by id");
		EntityProcessor processor = new EntityProcessor();
		log.info("SQL:"+ sql);
		getJdbcTemplate().query(sql.toString(), processor);
        return (BatchInfo[])processor.result.toArray(new BatchInfo[0]);
	}

	public int addBatchInfo(final BatchInfo batchInfo){
		if (batchInfo == null) {
			throw new IllegalArgumentException("添加的BatchInfo为null");
		}

		String sql = "INSERT INTO T_TASK_BATCH (ID, BATCH_NAME, TASK_ID, SPLIT_COUNT, SPLIT_TIME) values (DEFAULT,?,?,?,?)";
		
		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		log.info("SQL:"+ sql);
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
            	int index = 1;
				ps.setString(index++, batchInfo.getBatchName());
				ps.setInt(index++, batchInfo.getTaskId());
				ps.setInt(index++, batchInfo.getSplitCount());
				ps.setTimestamp(index++, batchInfo.getSplitTime());
            }
        });
        
		return theJdbcTemplate.queryForInt("SELECT LAST_INSERT_ID()");
	}

	public int[] addBatchInfos(final BatchInfo[] batchInfos){
		if (batchInfos == null || batchInfos.length == 0) {
			throw new IllegalArgumentException("添加的BatchInfo为null");
		}
		
		int[] ret = new int[batchInfos.length];
		for (int i = 0; i < batchInfos.length; ++i) {
			ret[i] = addBatchInfo(batchInfos[i]);
		}
		return ret;
	}

	public int deleteBatchInfo(int id){
		String sql = "delete from t_batch where id=?";
		log.info("SQL:"+ sql);
		return getJdbcTemplate().update(sql.toString(), new Object[]{new Long(id)}, new int[]{java.sql.Types.BIGINT});
	}
	
	public int deleteBatchInfos(int[] ids){
		if (ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer("delete from t_batch where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");
		log.info("SQL:"+ sql);
		return getJdbcTemplate().update(sql.toString());
	}
	
	public int saveBatchInfo(final BatchInfo batchInfo){
		String sql = "UPDATE T_TASK_BATCH SET BATCH_NAME=?, TASK_ID=?, SPLIT_COUNT=?, SPLIT_TIME=? WHERE ID=?";
		log.info("SQL:"+ sql);
		return getJdbcTemplate().update(sql, new PreparedStatementSetter(){
			public void setValues(PreparedStatement ps) throws SQLException{
				int index = 1;
				ps.setString(index++, batchInfo.getBatchName());
				ps.setInt(index++, batchInfo.getTaskId());
				ps.setInt(index++, batchInfo.getSplitCount());
				ps.setTimestamp(index++, batchInfo.getSplitTime());
				ps.setInt(index++, batchInfo.getId());
			}
		});
	}
	
	public int[] saveBatchInfos(final BatchInfo[] batchInfos){
		if (batchInfos == null || batchInfos.length == 0) {
			throw new IllegalArgumentException("更新的BatchInfo为null");
		}
		String sql = "update t_batch set batch_name=?, task_id=?, split_count=?, split_time=? where id = ?";
		log.info("SQL:"+ sql);
		return getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
            	int index = 1;
				ps.setString(index++, batchInfos[i].getBatchName());
				ps.setInt(index++, batchInfos[i].getTaskId());
				ps.setInt(index++, batchInfos[i].getSplitCount());
				ps.setTimestamp(index++, batchInfos[i].getSplitTime());
            	ps.setInt(index++, batchInfos[i].getId());
            }
            
			public int getBatchSize() {
				return batchInfos.length;
			}
        });
	}
	
	public BatchInfo[] queryByTaskId (final int taskId) {
				
		StringBuffer sql = new StringBuffer("select id, batch_name, task_id, split_count, split_time from t_batch where task_id=? ");
		String orderBy = "";
		orderBy = "";
		if (orderBy!= null && orderBy.length() > 0) {
			sql.append(" order by ").append(orderBy);
		}
		
		EntityProcessor processor = new EntityProcessor();
		log.info("SQL:"+ sql);
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter (){
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, taskId);
			}
		}
		,processor);
        return (BatchInfo[])processor.result.toArray(new BatchInfo[0]);
	}
	public int countByTaskId (final int taskId) {
		StringBuffer sql = new StringBuffer("select count(*) as count from t_batch where task_id=? ");
		CountProcess processor = new CountProcess();
		log.info("SQL:"+ sql);
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter () {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, taskId);
			}
		}
		, processor);
		
        return processor.getCount();
		
	}
	
	protected class CountProcess implements RowCallbackHandler {
		public CountProcess() {
		}
		
		private int count = 0;
		
		public int getCount() {
			return count;
		}
		
		@Override
		public void processRow(ResultSet rs) throws SQLException {
			count = rs.getInt("count");
		}
	}
	
	protected class IDProcessor implements RowCallbackHandler {
		public IDProcessor () {
		}
		public List<Integer> result = new ArrayList<Integer>();
		public void processRow(ResultSet rs) throws SQLException {
        	result.add(rs.getInt("id"));
		}
	};
	
	protected class EntityProcessor implements RowCallbackHandler {
		public EntityProcessor () {
		}
		public List<BatchInfo> result = new ArrayList<BatchInfo>();
		public void processRow(ResultSet rs) throws SQLException {
			BatchInfo batchInfo = new BatchInfo();
        	batchInfo.setId(rs.getInt("ID"));
        	batchInfo.setBatchName(rs.getString("BATCH_NAME"));
        	batchInfo.setTaskId(rs.getInt("TASK_ID"));
        	batchInfo.setSplitCount(rs.getInt("SPLIT_COUNT"));
        	java.sql.Timestamp splitTime = rs.getTimestamp("SPLIT_TIME");
        	if (splitTime != null) {
        		batchInfo.setSplitTime(splitTime);
        	}
        	result.add(batchInfo);
		}
	}
}

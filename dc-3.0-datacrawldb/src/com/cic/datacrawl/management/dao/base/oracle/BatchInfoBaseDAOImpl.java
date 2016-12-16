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

import com.cic.datacrawl.management.dao.base.BatchInfoBaseDAO;
import com.cic.datacrawl.management.entity.BatchInfo;
import com.cic.datacrawl.management.utils.DBUtils;

public class BatchInfoBaseDAOImpl extends JdbcDaoSupport implements
		BatchInfoBaseDAO {

	public int count() {
		String sql = "SELECT COUNT(0) FROM T_TASK_BATCH";
		return getJdbcTemplate().queryForInt(sql);
	}

	public BatchInfo[] getAllBatchInfo() {
		return getAllBatchInfo(0, -1);
	}

	public BatchInfo[] getAllBatchInfo(int startIndex, int limit) {
		String sql = "select * from T_TASK_BATCH order by ID";
		String paginationSQL = DBUtils.buildPageSQL(startIndex, limit, sql);
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(paginationSQL, processor);
		return (BatchInfo[]) processor.result.toArray(new BatchInfo[0]);
	}

	public BatchInfo getBatchInfo(int id) {
		String sql = "select * from T_TASK_BATCH where ID="
				+ id;
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		if (processor.result.size() > 0) {
			return (BatchInfo) processor.result.get(0);
		} else {
			return null;
		}
	}

	public BatchInfo[] getBatchInfos(final int[] ids) {
		if (ids.length == 0) {
			return new BatchInfo[0];
		}
		StringBuffer sql = new StringBuffer(
				"select * from T_TASK_BATCH where ID in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(") order by ID");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), processor);
		return (BatchInfo[]) processor.result.toArray(new BatchInfo[0]);
	}

	public int addBatchInfo(final BatchInfo batchInfo) {
		if (batchInfo == null) {
			throw new IllegalArgumentException("添加的BatchInfo为null");
		}

		String sql = "insert into T_TASK_BATCH (ID,BATCH_NAME,TASK_ID,SPLIT_COUNT,SPLIT_TIME)"
				+ " values(default,?,?,?,?)";

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, batchInfo.getBatchName());
				ps.setInt(index++, batchInfo.getTaskId());
				ps.setInt(index++, batchInfo.getSplitCount());
				ps.setTimestamp(index++, batchInfo.getSplitTime());
			}
		});

		String sqlSeq = "select ID from T_TASK_BATCH WHERE TASK_ID="+batchInfo.getTaskId() + " and BATCH_NAME=\"" + batchInfo.getBatchName() + "\"";
		final int id = getJdbcTemplate().queryForInt(sqlSeq);
		
		return id;
	}

	public int[] addBatchInfos(final BatchInfo[] batchInfos) {
		if (batchInfos == null || batchInfos.length == 0) {
			throw new IllegalArgumentException("添加的BatchInfo为null");
		}

		int[] ret = new int[batchInfos.length];
		for (int i = 0; i < batchInfos.length; ++i) {
			ret[i] = addBatchInfo(batchInfos[i]);
		}
		return ret;
	}

	public int deleteBatchInfo(int id) {
		String sql = "DELETE FROM T_TASK_BATCH WHERE ID=?";
		return getJdbcTemplate().update(sql.toString(),
				new Object[] {id},
				new int[] { java.sql.Types.INTEGER });
	}

	public int deleteBatchInfos(int[] ids) {
		if (ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer("DELETE FROM T_TASK_BATCH where ID in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");
		return getJdbcTemplate().update(sql.toString());
	}

	public int saveBatchInfo(final BatchInfo batchInfo) {
		String sql = "update T_TASK_BATCH set BATCH_NAME=?, TASK_ID=?, SPLIT_COUNT=?, SPLIT_TIME=? where ID = ?";
		return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, batchInfo.getBatchName());
				ps.setInt(index++, batchInfo.getTaskId());
				ps.setInt(index++, batchInfo.getSplitCount());
				ps.setTimestamp(index++, batchInfo.getSplitTime());
				ps.setInt(index++, batchInfo.getId());
			}
		});
	}

	public int[] saveBatchInfos(final BatchInfo[] batchInfos) {
		if (batchInfos == null || batchInfos.length == 0) {
			throw new IllegalArgumentException("更新的BatchInfo为null");
		}
		String sql = "update T_TASK_BATCH set BATCH_NAME=?, TASK_ID=?, SPLIT_COUNT=?, SPLIT_TIME=? where ID = ?";

		return getJdbcTemplate().batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
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

	public BatchInfo[] queryByTaskId(final int taskId) {

		StringBuffer sql = new StringBuffer(
				"select * from T_TASK_BATCH where TASK_ID=? ");
		String orderBy = "";
		if (orderBy != null && orderBy.length() > 0) {
			sql.append(" order by ").append(orderBy);
		}

		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, taskId);
			}
		}, processor);
		return (BatchInfo[]) processor.result.toArray(new BatchInfo[0]);
	}

	public int countByTaskId(final int taskId) {
		StringBuffer sql = new StringBuffer(
				"select count(0) as count from T_TASK_BATCH where TASK_ID=? ");
		CountProcess processor = new CountProcess();
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, taskId);
			}
		}, processor);

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
		public IDProcessor() {
		}

		public List<Integer> result = new ArrayList<Integer>();

		public void processRow(ResultSet rs) throws SQLException {
			result.add(rs.getInt("ID"));
		}
	};

	protected class EntityProcessor implements RowCallbackHandler {
		public EntityProcessor() {
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
	};
}

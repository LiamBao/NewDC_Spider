package com.cic.datacrawl.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cic.datacrawl.core.util.SQLUtil;

public class DbConnection {
	private static final Logger LOG = Logger.getLogger(DbConnection.class);

	public DbConnection(String driverClassName, String url, String username, String password) {
		dataSource = new BasicDataSource();
		dataSource.setInitialSize(0);
		dataSource.setMaxActive(5);
		dataSource.setMaxIdle(1);
		dataSource.setMinIdle(0);

		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		try {
			Connection connection = dataSource.getConnection();
			connection.close();
		} catch (SQLException e) {
			LOG.error(e.getMessage());
			throw new RuntimeException(e);
		}
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	protected BasicDataSource dataSource;
	protected JdbcTemplate jdbcTemplate;

	public void close() {
		try {
			dataSource.close();
		} catch (SQLException e) {
		}
	}

	public int queryForInt(String sql) {
		try {
			return jdbcTemplate.queryForInt(sql);
		} catch (IncorrectResultSizeDataAccessException e) {
			return 0;
		}
	}

	public int queryForInt(String sql, Object... args) {
		try {
			return jdbcTemplate.queryForInt(sql, args);
		} catch (IncorrectResultSizeDataAccessException e) {
			return 0;
		}
	}

	// // 支持空结果集. 结果集为空时,返回0
	// public int queryForInt(String sql, Object... args) {
	// ArrayList<Integer> rs = (ArrayList<Integer>) jdbcTemplate.queryForList(
	// sql, args, Integer.class);
	// if (rs.isEmpty())
	// return 0;
	// else
	// return rs.get(0).intValue();
	// }

	public byte[] queryForBinary(String sql, Object... args) {
		return (byte[]) jdbcTemplate.queryForObject(sql, args, byte[].class);
	}

	public String queryForString(String sql) {
		return (String) jdbcTemplate.queryForObject(sql, String.class);
	}

	public String queryForString(String sql, Object... args) {
		return (String) jdbcTemplate.queryForObject(sql, args, String.class);
	}

	public int update(String sql) {
		return jdbcTemplate.update(sql);
	}

	public int[] batchUpdate(String sql, final BatchPreparedStatementSetterValue[][] valueArrays) {
		int[] ret = new int[0];
		if (sql == null || sql.trim().length() == 0 || valueArrays == null || valueArrays.length == 0)
			return ret;

		if(LOG.isDebugEnabled())
		LOG.debug("Enter batchUpdate(" + sql + ", valueArrays)");

		if (valueArrays != null) {
			ArrayList<Integer> l = new ArrayList<Integer>();
			Integer validReturnValue = new Integer(Integer.MIN_VALUE);
			Integer invalidReturnValue = new Integer(0);

			boolean hasValidValue = false;

			for (int i = 0; i < valueArrays.length; ++i) {
				if (valueArrays[i] != null) {
					l.add(validReturnValue);
					hasValidValue = true;
				} else {
					l.add(invalidReturnValue);
				}
			}

			if (hasValidValue) {
				LOG.debug("Execute batchUpdate ---------- sql: " + sql);
				try {
					ret = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

						@Override
						public void setValues(PreparedStatement ps, int index) throws SQLException {

							BatchPreparedStatementSetterValue[] values = valueArrays[index];
							if (values != null) {
								for (int i = 0; i < values.length; ++i) {
									SQLUtil.setValue(ps, values[i].getType(), values[i].getValue(), i + 1);
								}
							}
						}

						@Override
						public int getBatchSize() {
							return valueArrays.length;
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				for (int i = 0; i < ret.length; ++i) {
					for (int j = 0; j < l.size(); ++j) {
						if (l.get(j) == validReturnValue) {
							l.set(j, new Integer(ret[i]));
							break;
						}
					}
				}
			}
			ret = new int[l.size()];
			for (int i = 0; i < l.size(); ++i) {
				ret[i] = l.get(i).intValue();
			}
		}
		LOG.debug("Exit batchUpdate(" + sql + ", valueArrays)");
		return ret;
	}

	public int[] batchUpdate(String[] sql) {
		return jdbcTemplate.batchUpdate(sql);
	}

	public int update(String sql, Object... args) {
		return jdbcTemplate.update(sql, args);
	}

	public DbResultSet query(String sql) {
		LOG.info("SQL:"+ sql);
		return (DbResultSet) jdbcTemplate.query(sql, DbResultSet.newResultSetExtractor());
	}

	public DbResultSet query(String sql, Object... args) {
		return (DbResultSet) jdbcTemplate.query(sql, args, DbResultSet.newResultSetExtractor());
	}

	protected ConnectionWrapper getConnectionWrapper() {
		throw new RuntimeException("this driver doesn't support this function.");
	}

	/**
	 * 复制数据,并执行一些后续操作(主要用于类似DTS的相关处理)
	 * 
	 * @param srcSQL
	 * @param destDbConn
	 * @param destSQL
	 * @param mergesSQL
	 */
	public void copyTableData(String srcSQL, DbConnection destDbConn, String destSQL, String... mergesSQL) {
		ConnectionWrapper destDbConnW = destDbConn.getConnectionWrapper();
		try {
			ConnectionWrapper srcDbConnW = this.getConnectionWrapper();
			try {
				ConnectionWrapper.copyTableData(srcDbConnW, srcSQL, destDbConnW, destSQL);
			} catch (SQLException e) {
				throw new RuntimeException("copyTableData error.", e);
			} finally {
				srcDbConnW.close();
			}

			for (int i = 0; i < mergesSQL.length; i++) {
				try {
					destDbConnW.execute(mergesSQL[i]);
				} catch (SQLException e) {
					throw new RuntimeException("ExecSQL error: " + mergesSQL[i], e);
				}
			}
		} finally {
			destDbConnW.close();
		}
	}

	public void executes(String... executesSQL) {
		ConnectionWrapper srcDbConnW = this.getConnectionWrapper();
		try {
			for (int i = 0; i < executesSQL.length; i++) {
				try {
					srcDbConnW.execute(executesSQL[i]);
				} catch (SQLException e) {
					throw new RuntimeException("ExecSQL error: " + executesSQL[i], e);
				}
			}
		} finally {
			srcDbConnW.close();
		}
	}

	/**
	 * ID型字段值生成(主要用于类似DTS的相关处理)
	 * 
	 * @param srcTableName
	 * @param uidFieldName
	 * @param idFieldName
	 * @param uidDbConn
	 * @param uidTableName
	 */
	public void genIdValues(String srcTableName, String uidFieldName, String idFieldName,
			DbConnection uidDbConn, String uidTableName) {
		ConnectionWrapper uidDbConnW = uidDbConn.getConnectionWrapper();
		try {
			ConnectionWrapper localDbConnW = this.getConnectionWrapper();
			try {
				String sqlSrcUid = "select distinct " + uidFieldName + " as uid from " + srcTableName;
				LOG.info("SQL:"+ sqlSrcUid);
				String uidTempTable = ConnectionWrapper.copyTableData(localDbConnW, sqlSrcUid, uidDbConnW,
																		"#tttgenid1");

				String sqlInsUid = "insert ignore into "
									+ uidTableName
									+ " (uid) select uid from "
									+ uidTempTable;
				LOG.info("SQL:"+ sqlInsUid);
				uidDbConnW.execute(sqlInsUid);

				String sqlGenedUid = "select T.uid, G.id from "
										+ uidTempTable
										+ " T inner joinn "
										+ uidTableName
										+ " G on T.uid=G.uid";
				LOG.info("SQL:"+ sqlGenedUid);
				String uidLocalTemp = ConnectionWrapper.copyTableData(uidDbConnW, sqlGenedUid, localDbConnW,
																		"#tttgenid2");

				String sqlUpdId = "update "
									+ srcTableName
									+ " T inner join "
									+ uidLocalTemp
									+ " G  on T."
									+ uidFieldName
									+ "=G.uid"
									+ " set T."
									+ idFieldName
									+ "=G.id from "
									+ srcTableName;
				LOG.info("SQL:"+ sqlUpdId);
				localDbConnW.execute(sqlUpdId);
				
			} catch (SQLException e) {
				throw new RuntimeException("genIdValues error.", e);
			} finally {
				localDbConnW.close();
			}
		} finally {
			uidDbConnW.close();
		}
	}

}

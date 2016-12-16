package com.cic.datacrawl.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

public abstract class ConnectionWrapper {

	private static final Logger LOG = Logger.getLogger(ConnectionWrapper.class);
	private BasicDataSource dataSource;
	protected Connection jdbcConnection;

	public ConnectionWrapper(BasicDataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcConnection = DataSourceUtils.getConnection(dataSource);
	}

	// 表数据复制支持 - 获取sql计算结果
	public ConnectionWrapper_RsMeta openResultSet(String sql, boolean loadMeta) throws SQLException {
		Statement statement = jdbcConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
																ResultSet.CONCUR_READ_ONLY);
		LOG.info("SQL:"+ sql);
		ResultSet rs = statement.executeQuery(sql);
		return new ConnectionWrapper_RsMeta(statement, rs, loadMeta);
	}

	public void execute(String sql) throws SQLException {
		Statement statement = jdbcConnection.createStatement();
		try {
			statement.execute(sql);
		} finally {
			statement.close();
		}
	}

	public void close() {
		DataSourceUtils.releaseConnection(jdbcConnection, dataSource);
	}

	// 表数据复制支持 - 根据指定meta信息,自动建表. 返回表在sql中的表示名称(临时表一般需附加前缀)
	public abstract String createTable(String tableName, ConnectionWrapper_RsMeta rsMeta, boolean isTempTable)
			throws SQLException;

	public abstract boolean hasTable(String tableName) throws SQLException;

	// 表数据复制
	public static String copyTableData(ConnectionWrapper srcDbConn, String srcSQL,
			ConnectionWrapper destDbConn, String destSQL) throws SQLException {

		// System.out.println("copy table:"+srcSQL);
		// 读取源数据,并分析源数据表结构
		String tsReadSQL;
		if (srcSQL.indexOf(' ') < 0)
			tsReadSQL = "select * from " + srcSQL;
		else
			tsReadSQL = srcSQL;
		String newTempTableName = null;
		LOG.info("SQL:"+ tsReadSQL);
		ConnectionWrapper_RsMeta rsMeta = srcDbConn.openResultSet(tsReadSQL, true);
		try {
			String tsWriteSQL;
			if (destSQL.indexOf(' ') < 0) {
				StringBuffer tsFieldsName = new StringBuffer();
				StringBuffer tsFieldsValue = new StringBuffer();
				for (int i = 0; i < rsMeta.columnCount; i++) {
					if (i > 0) {
						tsFieldsName.append(',');
						tsFieldsValue.append(',');
					}
					tsFieldsName.append(rsMeta.columnName[i]);
					tsFieldsValue.append('?');
				}

				String insertTableName;
				if (destSQL.startsWith("#")) {
					// 根据结果表结构，自动新建临时表
					insertTableName = destDbConn.createTable(destSQL.substring(1), rsMeta, true);
					newTempTableName = insertTableName;
				} else if (!destDbConn.hasTable(destSQL)) {
					insertTableName = destDbConn.createTable(destSQL, rsMeta, false);
					newTempTableName = insertTableName;
				} else {
					insertTableName = destSQL;
				}
				tsWriteSQL = "insert into "
								+ insertTableName
								+ " ("
								+ tsFieldsName
								+ ") values ("
								+ tsFieldsValue
								+ ")";
			} else {
				tsWriteSQL = destSQL;
			}
			if (LOG.isDebugEnabled())
				LOG.debug("sql: " + tsWriteSQL);
			
			PreparedStatement pstmtInsert = destDbConn.jdbcConnection.prepareStatement(tsWriteSQL);
			try {
				int thisBatchRows = 0;
				ResultSet rs = rsMeta.rsData;
				while (rs.next()) {
					for (int i = 1; i <= rsMeta.columnCount; i++) {
						int n = i - 1;
						switch (rsMeta.columnType[n]) {
						case java.sql.Types.INTEGER:
						case java.sql.Types.SMALLINT:
						case java.sql.Types.TINYINT:
							int valInt = rs.getInt(i);
							if (rs.wasNull())
								pstmtInsert.setNull(i, rsMeta.columnType[n]);
							else
								pstmtInsert.setInt(i, valInt);
							break;
						// 转DECIMAL为long 类型
						case java.sql.Types.DECIMAL:
						case java.sql.Types.BIGINT:
							long valLong = rs.getLong(i);
							if (rs.wasNull())
								pstmtInsert.setNull(i, rsMeta.columnType[n]);
							else
								pstmtInsert.setLong(i, valLong);
							break;
						case java.sql.Types.DOUBLE:
						case java.sql.Types.FLOAT:
							double valDouble = rs.getDouble(i);
							if (rs.wasNull())
								pstmtInsert.setNull(i, rsMeta.columnType[n]);
							else
								pstmtInsert.setDouble(i, valDouble);
							break;
						case java.sql.Types.DATE:
							java.sql.Date valDate = rs.getDate(i);
							if (rs.wasNull())
								pstmtInsert.setNull(i, rsMeta.columnType[n]);
							else
								pstmtInsert.setDate(i, valDate);
							break;
						case java.sql.Types.TIME:
							java.sql.Time valTime = rs.getTime(i);
							if (rs.wasNull())
								pstmtInsert.setNull(i, rsMeta.columnType[n]);
							else
								pstmtInsert.setTime(i, valTime);
							break;
						case java.sql.Types.TIMESTAMP:
							java.sql.Timestamp valTimestamp = rs.getTimestamp(i);
							if (rs.wasNull())
								pstmtInsert.setNull(i, rsMeta.columnType[n]);
							else
								pstmtInsert.setTimestamp(i, valTimestamp);
							break;

						case java.sql.Types.CHAR:
						case java.sql.Types.VARCHAR:
						case java.sql.Types.CLOB:
						case java.sql.Types.LONGVARCHAR:
							String valString = rs.getString(i);
							if (rs.wasNull())
								pstmtInsert.setNull(i, rsMeta.columnType[n]);
							else
								pstmtInsert.setString(i, valString);
							break;
						case java.sql.Types.BINARY:
						case java.sql.Types.VARBINARY:
							byte[] valBinary = rs.getBytes(i);
							if (rs.wasNull())
								pstmtInsert.setNull(i, rsMeta.columnType[n]);
							else
								pstmtInsert.setBytes(i, valBinary);
							break;

						default:
							throw new SQLException("Unsupport field datatype "
													+ rsMeta.columnType[n]
													+ " for '"
													+ rsMeta.columnName[n]
													+ "'");
						}
					}

					pstmtInsert.addBatch();
					thisBatchRows++;
					if (thisBatchRows == COPY_BATCH_ROW_COUNT) {
						pstmtInsert.executeBatch();
						pstmtInsert.clearBatch();
						thisBatchRows = 0;
					}
				}
				if (thisBatchRows > 0)
					pstmtInsert.executeBatch();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				// 关闭 statment
				try {
					pstmtInsert.close();

				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// 关闭 statment 和resulet
			try {
				rsMeta.close();
				rsMeta = null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return newTempTableName;
	}

	final static int COPY_BATCH_ROW_COUNT = 5000;

}

package com.cic.datacrawl.core.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

public class ConnectionWrapper_MySQL extends ConnectionWrapper {
	
	private static final Logger log = Logger.getLogger(ConnectionWrapper_MySQL.class);

  public ConnectionWrapper_MySQL(BasicDataSource dataSource) {
    super(dataSource);
  }
  
  @Override
  /* 根据指定meta信息,自动建表. 返回表在sql中的表示名称(临时表一般需附加前缀) */
  public String createTable(String tableName, ConnectionWrapper_RsMeta rsMeta,
          boolean isTempTable) throws SQLException {
      StringBuffer sql = new StringBuffer();
      if (isTempTable)
          sql.append("CREATE TEMPORARY TABLE ");
      else
          sql.append("CREATE TABLE ");
      sql.append(tableName);
      sql.append(" (");
      for (int i = 0; i < rsMeta.columnCount; i++) {
          if (i > 0)
              sql.append(',');

          sql.append(rsMeta.columnName[i]);
          sql.append(' ');
          switch (rsMeta.columnType[i]) {
          case java.sql.Types.INTEGER:
          case java.sql.Types.SMALLINT:
          case java.sql.Types.TINYINT:
              sql.append(" int");
              break;
          case java.sql.Types.BIGINT:
              sql.append(" bigint");
              break;
          case java.sql.Types.DOUBLE:
          case java.sql.Types.FLOAT:
              sql.append(" double");
              break;
          case java.sql.Types.DATE:
              sql.append(" date");
              break;
          case java.sql.Types.TIME:
              sql.append(" time");
              break;
          case java.sql.Types.TIMESTAMP:
              sql.append(" datetime");
              break;

          case java.sql.Types.CHAR:
              sql.append(" char(" + rsMeta.columnPrecision[i] + ")");
              break;
          case java.sql.Types.VARCHAR:
              sql.append(" varchar(" + rsMeta.columnPrecision[i] + ")");
              break;
          case java.sql.Types.BINARY:
              sql.append(" binary(" + rsMeta.columnPrecision[i] + ")");
          case java.sql.Types.VARBINARY:
              sql.append(" varbinary(" + rsMeta.columnPrecision[i] + ")");
              break;

          case java.sql.Types.CLOB:
          case java.sql.Types.LONGVARCHAR:
              sql.append(" text");
              break;

          default:
              throw new SQLException("Unsupport field datatype "
                      + rsMeta.columnType[i] + " for '"
                      + rsMeta.columnName[i] + "'");
          }
          if (!rsMeta.columnNullable[i])
              sql.append(" not null");
      }
      sql.append(" )");
      execute(sql.toString());
      return tableName;
  }

  @Override
  public boolean hasTable(String tableName) throws SQLException {
    String tsSql = "show tables like '" + tableName + "'";
    boolean bHas = false;
    Statement statement = jdbcConnection.createStatement();
    try {
        ResultSet rs = statement.executeQuery(tsSql);
        try {
            bHas = rs.next();
        } catch(Exception e){
        	  log.error("SQL:"+ tsSql,e);
        }finally {
            rs.close();
        }
    } catch(Exception e){
    	  log.error("SQL:"+ tsSql,e);
      }finally {
        statement.close();
    }
    return bHas;
  }

  @Override
  public ConnectionWrapper_RsMeta openResultSet(String sql, boolean loadMeta) throws SQLException {
  /*
   * 经测试, MySQL的JDBC驱动, 必须设置.enableStreamingResults(),否则大数据量时会内存溢出。
   * 且驱动应该用mysql-connector-java-3.1.14-bin.jar，驱动5.0.x版容易TCP读取错误.
   * 当设置enableStreamingResults()时， 通过getPrecision()获得字段属性就会报错： ...... No
   * statements may be issued when any streaming result sets are
   * open.........
   */

  Statement statement = jdbcConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
  if (! loadMeta) {
      // ((com.mysql.jdbc.Statement)stmt).enableStreamingResults();
      statement.setFetchSize(Integer.MIN_VALUE);
      log.info("SQL:"+ sql);
      ResultSet rs = statement.executeQuery(sql);
      return new ConnectionWrapper_RsMeta(statement, rs, false);
  } else {
      String ts = sql.trim().toLowerCase();
      log.info("SQL:"+ sql);
      if (ts.startsWith("select ") && (ts.indexOf(" limit ") < 0)) {
          ResultSet rs0 = statement.executeQuery(sql + " limit 0");
          ConnectionWrapper_RsMeta result = new ConnectionWrapper_RsMeta(statement, rs0, true);
          rs0.close();

          statement.setFetchSize(Integer.MIN_VALUE);
          ResultSet rs = statement.executeQuery(sql);
          result.setDataResultSet(rs);
          return result;
      } else {
    	  log.info("SQL:"+ sql);
          ResultSet rs = statement.executeQuery(sql);
          return new ConnectionWrapper_RsMeta(statement, rs, true);
      }
  }
  }

}

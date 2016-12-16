package com.cic.datacrawl.core.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

public class ConnectionWrapper_MSSQL extends ConnectionWrapper {
	
	private static final Logger log = Logger.getLogger(ConnectionWrapper_MSSQL.class);

  public ConnectionWrapper_MSSQL(BasicDataSource dataSource) {
    super(dataSource);
  }
  
  @Override
  /* 根据指定meta信息,自动建表. 返回表在sql中的表示名称(临时表一般需附加前缀) */
  public String createTable(String tableName, ConnectionWrapper_RsMeta rsMeta,
          boolean isTempTable) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("CREATE TABLE ");
    if (isTempTable)
        sql.append('#');
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
    return (isTempTable ? "#" : "") + tableName;
  }

  @Override
  public boolean hasTable(String tableName) throws SQLException {
    String tsSql = "select * from sysobjects where id = object_id(N'["
      + tableName + "]') and OBJECTPROPERTY(id, N'IsUserTable') = 1";
    boolean bHas = false;
    Statement statement = jdbcConnection.createStatement();
    try {
    	log.info("SQL:"+ tsSql);
      ResultSet rs = statement.executeQuery(tsSql);
      try {
          bHas = rs.next();
      } catch(Exception e){
    	  log.error("SQL:"+ tsSql,e);
      }
      finally {
          rs.close();
      }
    }catch(Exception e){
  	  log.error("SQL:"+ tsSql,e);
    } finally {
      statement.close();
    }
    return bHas;
  }

}

package com.cic.datacrawl.core.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/* ResultSetMetaData 中的主要信息 */
public class ConnectionWrapper_RsMeta {
  public ConnectionWrapper_RsMeta(Statement st, ResultSet rs, boolean loadMeta) throws SQLException {
    this.st = st;
    this.rsData = rs;
    if (loadMeta) this.loadResultSetMeta(rs);
  }
  
  public Statement st;
  public ResultSet rsData;
  
  public void setDataResultSet(ResultSet rs) {
    this.rsData = rs;
  }
  
  public void close() throws SQLException {
    rsData.close();
    st.close();
  }

  public int columnCount;
  public String[] columnName;
  public int[] columnType;
  public int[] columnPrecision;
  public boolean[] columnNullable;
  
  private void loadResultSetMeta(ResultSet rs) throws SQLException {
    ResultSetMetaData rsMeta = rs.getMetaData();
    columnCount = rsMeta.getColumnCount();
    columnName = new String[columnCount];
    columnType = new int[columnCount];
    columnPrecision = new int[columnCount];
    columnNullable = new boolean[columnCount];
    for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
      int n = i - 1;
      columnName[n] = rsMeta.getColumnLabel(i);
      columnType[n] = rsMeta.getColumnType(i);
      columnNullable[n] = (rsMeta.isNullable(i) != ResultSetMetaData.columnNoNulls);
      switch (columnType[n]) {
        case java.sql.Types.CHAR:
        case java.sql.Types.VARCHAR:
        case java.sql.Types.BINARY:
        case java.sql.Types.VARBINARY:
        case java.sql.Types.DECIMAL:
          columnPrecision[n] = rsMeta.getPrecision(i);
      }
    }
  }
  
}

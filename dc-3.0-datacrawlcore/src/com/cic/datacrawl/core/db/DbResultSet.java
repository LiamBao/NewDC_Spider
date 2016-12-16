package com.cic.datacrawl.core.db;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.mozilla.javascript.NativeArray;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;

public class DbResultSet {

  private DbResultSet(DbFieldInfo[] fields) {
    this.fields = fields;
    
    fieldsIndex = new HashMap<String, Integer>();
    for (int i=0; i < fields.length; i++)
      fieldsIndex.put(fields[i].name, i);
    
    jsFields = new NativeArray(fields);
  }
  
  protected DbFieldInfo[] fields;
  protected HashMap<String, Integer> fieldsIndex;
  protected Object[][] rows;
  private NativeArray jsFields, jsRows;
  
  private void setRows(ArrayList<Object[]> list) {
    this.rows = new Object[list.size()][];
    list.toArray(rows);
    
    DbResultRow[] drows = new DbResultRow[rows.length];
    for (int i=0; i < rows.length; i++)
      drows[i] = new DbResultRow(this, i);
    jsRows = new NativeArray(drows);
  }
  
  public int getFieldCount() {
    return fields.length;
  }
  
  public int getRowCount() {
    return rows.length;
  }
  
  public NativeArray getFields() {
    return jsFields;
  }
  
  public NativeArray getRows() {
    return jsRows;
  }
                              

  public static DbFieldInfo[] extractMetaData(ResultSetMetaData rsMeta) {
    try {
      DbFieldInfo[] fields = new DbFieldInfo[rsMeta.getColumnCount()];
      for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
        int n = i - 1;
        DbFieldInfo field = new DbFieldInfo();
        fields[n] = field;
        field.name = rsMeta.getColumnLabel(i);
        field.sqlType = rsMeta.getColumnType(i);
        field.sqlNullable = (rsMeta.isNullable(i) != ResultSetMetaData.columnNoNulls);

        field.sqlTypeName = rsMeta.getColumnTypeName(i);
        switch (field.sqlType) {
          case java.sql.Types.CHAR:
          case java.sql.Types.VARCHAR:
          case java.sql.Types.BINARY:
          case java.sql.Types.VARBINARY:
          case java.sql.Types.DECIMAL:
            field.sqlTypeName += "("+rsMeta.getPrecision(i)+")";
        }
      }
      return fields;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static ResultSetExtractor newResultSetExtractor() {
    return new JsResultSetExtractor(null);
  }
  
  public static ResultSetExtractor newResultSetExtractor(DbFieldInfo[] fields) {
    return new JsResultSetExtractor(fields);
  }
  
  static class JsResultSetExtractor implements ResultSetExtractor {
    public JsResultSetExtractor(DbFieldInfo[] fields) {
      this.fields = fields;
    }
    private DbFieldInfo[] fields;
    
    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
      if (fields == null)
        fields = extractMetaData(rs.getMetaData());
      DbResultSet rowSet = new DbResultSet(fields);
      
      ArrayList<Object[]> rows = new ArrayList<Object[]>();
      while (rs.next()) {
        Object[] row = new Object[fields.length];
        for (int i=0; i < fields.length; i++)
          row[i] = JdbcUtils.getResultSetValue(rs, 1+i);
        rows.add(row);
      }
      rowSet.setRows(rows);
      
      return rowSet;
    }
  }

}

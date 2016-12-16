package com.cic.datacrawl.core.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativeObjectUtil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

public class DbWriter {
	private static final Logger log = Logger.getLogger(DbWriter.class);
	
	
  // 创建writer
  public DbWriter(DbConnection dbConnection, String tableName, String fields) {
    this(dbConnection, tableName, fields, 1);
  }
  
  public DbWriter(DbConnection dbConnection, String tableName, String fields, int bufSize) {
    this.init(dbConnection, tableName, fields.split(","), bufSize);
  }
  
  
  public void write(Object obj) {
    if (obj instanceof NativeObject) // 输出单条结果
      writeItem((NativeObject)obj);
    else if (obj instanceof NativeArray) {// 输出多条结果
      NativeArray array = (NativeArray) obj;
      int jsLength = (int) array.getLength();
      for (int i=0; i < jsLength; i++)
        writeItem((NativeObject) array.get(i, array));
    }
    else throw new RuntimeException("Must be NativeObject/NativeArray");
  }
  
  private void writeItem(NativeObject item) {
    buffer.add(NativeObjectUtil.jsArray2Map(item));
    if (buffer.size() >= bufSize) {
      flashBuffer();
    }
  }
  
 
  // 关闭(将缓冲区全部写入数据库)
  public void close() {
    flashBuffer();
  }
  
  private void init(DbConnection dbConnection, String tableName, String[] fields, int bufSize) {
    this.fields = fields;
    this.bufSize = bufSize;
    this.jdbcTemplate = new JdbcTemplate(dbConnection.dataSource);
    
    StringBuilder tsFields = new StringBuilder();
    StringBuilder tsValues = new StringBuilder();
    for (int i=0; i < fields.length; i++) {
      if (i > 0) {
        tsFields.append(',');
        tsValues.append(',');
      }
      tsFields.append(fields[i]);
      tsValues.append('?');
    }
    this.insertSql = "insert into " + tableName + "("+ tsFields.toString() +") values ("
      + tsValues.toString() +")";

    ConnectionWrapper connWrap = dbConnection.getConnectionWrapper();
    try {
      String tsSql = "select "+ tsFields.toString() +" from " +tableName +" where 1=2";
      log.info("SQL:"+ tsSql);
      ConnectionWrapper_RsMeta rsMeta = connWrap.openResultSet(tsSql, true);
      this.fieldsTypes = rsMeta.columnType;
      rsMeta.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    finally {
      connWrap.close();
    }
  }
  
  private String[] fields;
  private int bufSize;
  private JdbcTemplate jdbcTemplate;
  private String insertSql;
  private int[] fieldsTypes;
  
  private ArrayList<HashMap<String, Object>> buffer = new ArrayList<HashMap<String, Object>>(500);
  
  
  private BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {
    @Override
    public int getBatchSize() {
      return buffer.size();
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
      HashMap<String, Object> item = buffer.get(i);
      for (int n=1; n <= fields.length; n++) {
        int fi = n-1;
        Object value = item.get(fields[fi]);
        if (value == null) {
          ps.setNull(n, fieldsTypes[fi]);
          continue;
        }
        switch (fieldsTypes[fi]) {
        case java.sql.Types.INTEGER:
        case java.sql.Types.SMALLINT:
        case java.sql.Types.TINYINT:
            int valInt;
            if (value instanceof Integer)
              valInt = ((Integer)value).intValue();
            else if (value instanceof Long)
              valInt = ((Long)value).intValue();
            else if (value instanceof Double)
              valInt = ((Double)value).intValue();
            else if (value instanceof String)
              valInt = Integer.parseInt((String)value);
            else
              throw new RuntimeException("Cannot be cast int from Class= "+value.getClass().getName()+"  value=" + value);
            ps.setInt(n, valInt);
            break;
        //转DECIMAL为long 类型
        case java.sql.Types.DECIMAL:                            
        case java.sql.Types.BIGINT:
            long valLong = ((Long)value).longValue();
            ps.setLong(n, valLong);
            break;
        case java.sql.Types.DOUBLE:
        case java.sql.Types.FLOAT:
            double valDouble = ((Double)value).doubleValue();
            ps.setDouble(n, valDouble);
            break;
        case java.sql.Types.DATE:
            java.sql.Date valDate = (java.sql.Date)value;
            ps.setDate(n, valDate);
            break;
        case java.sql.Types.TIME:
            java.sql.Time valTime = (java.sql.Time)value;
            ps.setTime(n, valTime);
            break;
        case java.sql.Types.TIMESTAMP:
            java.sql.Timestamp valTimestamp = (java.sql.Timestamp)value;
            ps.setTimestamp(n, valTimestamp);
            break;

        case java.sql.Types.CHAR:
        case java.sql.Types.VARCHAR:
        case java.sql.Types.CLOB:
        case java.sql.Types.LONGVARCHAR:
            String valString = (String)value;
            ps.setString(n, valString);
            break;
        case java.sql.Types.BINARY:
        case java.sql.Types.VARBINARY:
        case java.sql.Types.LONGVARBINARY:
            byte[] valBinary = (byte[])value;
            ps.setBytes(n, valBinary);
            break;

        default:
            throw new SQLException("Unsupport field datatype "
                    + fieldsTypes[fi] + " for '"
                    + fields[fi] + "'");
        }
      }
    }
    
  };

  private void flashBuffer() {
    jdbcTemplate.batchUpdate(insertSql, bpss);
    buffer.clear();
  }
}

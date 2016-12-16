package com.cic.datacrawl.core.db;

public class DbFieldInfo {
  protected String name;
  protected int sqlType;
  protected String sqlTypeName;
  protected boolean sqlNullable;
  
  public String getName() {
    return name;
  }
  public int getSqlType() {
    return sqlType;
  }
  public String getSqlTypeName() {
    return sqlTypeName;
  }
  public boolean isSqlNullable() {
    return sqlNullable;
  }

}

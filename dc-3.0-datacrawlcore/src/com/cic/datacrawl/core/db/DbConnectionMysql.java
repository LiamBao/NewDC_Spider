package com.cic.datacrawl.core.db;

public class DbConnectionMysql extends DbConnection {
  public DbConnectionMysql(String hostname, String dbName, String username, String password) {
    super("com.mysql.jdbc.Driver", "jdbc:mysql://"+hostname+":3306/"+dbName+"?characterEncoding=UTF8", username, password);
  }
  
  @Override
  protected ConnectionWrapper getConnectionWrapper() {
    return new ConnectionWrapper_MySQL(dataSource);
  }
}

package com.cic.datacrawl.core.db;

public class DbConnectionOracle extends DbConnection {
  public DbConnectionOracle(String hostname, String dbName, String username, String password) {
    super("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@"+hostname+":1521:"+dbName, username, password);
  }
  
  @Override
  protected ConnectionWrapper getConnectionWrapper() {
    return new ConnectionWrapper_MySQL(dataSource);
  }
}

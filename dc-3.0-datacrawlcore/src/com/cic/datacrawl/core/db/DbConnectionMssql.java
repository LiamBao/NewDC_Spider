package com.cic.datacrawl.core.db;

public class DbConnectionMssql extends DbConnection {
  public DbConnectionMssql(String hostname, String dbName, String username, String password) {
    super("net.sourceforge.jtds.jdbc.Driver", "jdbc:jtds:sqlserver://" + hostname + ":1433;DatabaseName=" + dbName, username, password);
  }
  public DbConnectionMssql(String hostname, String dbName, String username, String password, String instanceName) {
    super("net.sourceforge.jtds.jdbc.Driver", "jdbc:jtds:sqlserver://" + hostname + ":1433;DatabaseName=" + dbName
        + (instanceName==null ? "" : (";instance="+instanceName+";namedPipe=true;")), username, password);
  }
  
  @Override
  protected ConnectionWrapper getConnectionWrapper() {
    return new ConnectionWrapper_MSSQL(dataSource);
  }
}

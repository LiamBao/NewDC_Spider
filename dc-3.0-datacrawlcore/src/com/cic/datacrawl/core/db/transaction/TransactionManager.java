package com.cic.datacrawl.core.db.transaction;

public interface TransactionManager {
	public Object getManager();

	public TransactionStatus buildStatus();

	public void commit(TransactionStatus status);

	public void rollback(TransactionStatus status);

}

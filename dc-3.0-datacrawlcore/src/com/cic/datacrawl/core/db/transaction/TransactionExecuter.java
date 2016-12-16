package com.cic.datacrawl.core.db.transaction;

public interface TransactionExecuter {
	public void execute() throws Exception;
	public Object getResult();
}

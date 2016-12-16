package com.cic.datacrawl.core.db.transaction;

public interface TransactionService {
	public void execute(final TransactionExecuter executer);
	
	public TransactionManager getManager();
}

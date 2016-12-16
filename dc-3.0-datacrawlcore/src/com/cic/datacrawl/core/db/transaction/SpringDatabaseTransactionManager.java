package com.cic.datacrawl.core.db.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.cic.datacrawl.core.util.StringUtil;

public class SpringDatabaseTransactionManager implements TransactionManager {
	private PlatformTransactionManager transactionManager;

	public SpringDatabaseTransactionManager(PlatformTransactionManager manager) {
		transactionManager = manager;
	}

	@Override
	public TransactionStatus buildStatus() {
		if (transactionManager == null)
			return null;
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		// explicitly setting the transaction name is something that can only be
		// done programmatically
		def.setName(StringUtil.buildRandomString(5));
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		return new TransactionStatus(transactionManager.getTransaction(def));
	}

	@Override
	public void commit(TransactionStatus status) {
		if (transactionManager == null)
			return;
		transactionManager
				.commit((org.springframework.transaction.TransactionStatus) status
						.getStatus());
	}

	@Override
	public void rollback(TransactionStatus status) {
		if (transactionManager == null)
			return;

		transactionManager
				.rollback((org.springframework.transaction.TransactionStatus) status
						.getStatus());
	}

	@Override
	public Object getManager() {
		return transactionManager;
	}

}

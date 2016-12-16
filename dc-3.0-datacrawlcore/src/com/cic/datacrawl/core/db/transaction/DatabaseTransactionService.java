package com.cic.datacrawl.core.db.transaction;

import org.apache.log4j.Logger;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class DatabaseTransactionService implements TransactionService {
	private static final Logger LOGGER = Logger
			.getLogger(DatabaseTransactionService.class);
	private TransactionTemplate transactionTemplate;

	// /**
	// * @param transactionManager
	// * the transactionManager to set
	// */
	// public void setSpringTransactionManager(
	// PlatformTransactionManager transactionManager) {
	// Assert.notNull(transactionManager,
	// "The 'transactionManager' argument must not be null.");
	// this.transactionTemplate = new TransactionTemplate(transactionManager);
	//
	// // the transaction settings can be set here explicitly if so desired
	// this.transactionTemplate
	// .setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
	// this.transactionTemplate.setTimeout(30); // 30 seconds
	// }

	/**
	 * @param transactionTemplate
	 *            the transactionTemplate to set
	 */
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public void execute(final TransactionExecuter executer) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					org.springframework.transaction.TransactionStatus status) {
				try {
					executer.execute();
				} catch (Exception e) {
					LOGGER.warn(e.getMessage(), e);
					status.setRollbackOnly();
				}
			}
		});
	}

	@Override
	public TransactionManager getManager() {
		return new SpringDatabaseTransactionManager(transactionTemplate
				.getTransactionManager());
	}

}

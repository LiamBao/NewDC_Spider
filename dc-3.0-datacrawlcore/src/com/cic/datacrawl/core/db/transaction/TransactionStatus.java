package com.cic.datacrawl.core.db.transaction;

public class TransactionStatus {
	private Object status;

	public TransactionStatus(Object status) {
		super();
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public Object getStatus() {
		return status;
	}

}

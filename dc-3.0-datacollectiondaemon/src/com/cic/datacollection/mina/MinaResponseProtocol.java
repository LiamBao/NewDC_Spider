package com.cic.datacollection.mina;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MinaResponseProtocol implements Serializable {
	public int statusCode;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}

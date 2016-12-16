package com.cic.datacollection.mina;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MinaRequestProtocol implements Serializable {

	public String protocolName;

	public String getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}
	
}

package com.cic.datacollection.mina.protocol;

import com.cic.datacollection.mina.MinaResponseProtocol;

public class FeedBackProtocol extends MinaResponseProtocol {
	private static final long serialVersionUID = -3047094511037072155L;
	public String errorMessage;
	public String objectStr;
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getObjectStr() {
		return objectStr;
	}
	
	public void setObjectStr(String objectStr) {
		this.objectStr = objectStr;
	}
	
	public FeedBackProtocol() {
	}
	
	public FeedBackProtocol(int errCode, String errMsg, String objStr) {
		super.setStatusCode(errCode);
		this.errorMessage = errMsg;
		this.objectStr = objStr;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();  
        sb.append("FeedBack: [status: " + statusCode  + ", errMsg: " + errorMessage + ", objStr:" + objectStr + "]");  
        return sb.toString(); 
	}
}

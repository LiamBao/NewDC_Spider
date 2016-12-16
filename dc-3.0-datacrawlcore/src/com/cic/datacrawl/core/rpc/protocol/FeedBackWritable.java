package com.cic.datacrawl.core.rpc.protocol;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * 通用的响应协议
 * @author johnney.bu
 *
 */
public class FeedBackWritable implements Writable{
	
	public int code;
	public String errorMessage;
	public String objectStr;
	
	public FeedBackWritable(){
		
	}
	
	public FeedBackWritable(int code, String errorMessage, String objectStr){
		this.code = code;
		if(errorMessage == null){
			this.errorMessage = "";
		} else {
			this.errorMessage = errorMessage;
		}
		if(objectStr == null) {
			this.objectStr = "";
		} else {
			this.objectStr = objectStr;
		}
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		if(errorMessage == null){
			this.errorMessage = "";
		} else {
			this.errorMessage = errorMessage;
		}
	}

	public String getObjectStr() {
		return objectStr;
	}

	public void setObjectStr(String objectStr) {
		if(objectStr == null) {
			this.objectStr = "";
		} else {
			this.objectStr = objectStr;
		}
	}

	@Override
	public void readFields(DataInput out) throws IOException {
		// TODO Auto-generated method stub
		code = out.readInt();
		errorMessage = out.readUTF();
		objectStr = out.readUTF();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		// TODO Auto-generated method stub
		in.writeInt(code);
		in.writeUTF(errorMessage);
		in.writeUTF(objectStr);
		
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();  
        sb.append("FeedBack: [code: " + code  + ", errMsg: " + errorMessage + ", objectStr:" + objectStr + "]");  
        return sb.toString(); 
	}
}

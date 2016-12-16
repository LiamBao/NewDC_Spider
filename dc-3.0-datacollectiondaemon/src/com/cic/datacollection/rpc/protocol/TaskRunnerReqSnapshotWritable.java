package com.cic.datacollection.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class TaskRunnerReqSnapshotWritable implements Writable {
	public int siteId;
	public String url;
	
	public TaskRunnerReqSnapshotWritable() {
	}
	
	public TaskRunnerReqSnapshotWritable(int siteId, String url) {
		this.siteId = siteId;
		this.url = url;
	}
	
	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void readFields(DataInput out) throws IOException {
		siteId = out.readInt();
		url = out.readUTF();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeInt(siteId);
		in.writeUTF(url);
	}

	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("ReqSnapShot: [siteId: " + siteId + ",url: " + url + "]");  
        return strBuf.toString(); 
	}
}

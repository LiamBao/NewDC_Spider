package com.cic.datacollection.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class ResTaskRunnerReqSnapshotWritable implements Writable {
	public int code;			//标识
	public byte contentType;		//1:html  2:xml
	public long createTime;		//快照创建时间
	public int siteId;				//站点ID
	public String baseUrl;			//网页URL
	public String content;			//网页内容
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public ResTaskRunnerReqSnapshotWritable(){
	}
	
	public byte getContentType() {
		return contentType;
	}

	public void setContentType(byte contentType) {
		this.contentType = contentType;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public void readFields(DataInput out) throws IOException {
		code = out.readInt();
		contentType = out.readByte();
		createTime = out.readLong();
		siteId = out.readInt();
		baseUrl = out.readUTF();
		content = out.readUTF();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeInt(code);
		in.writeByte(contentType);
		in.writeLong(createTime);
		in.writeInt(siteId);
		in.writeUTF(baseUrl);
		in.writeUTF(content);
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("ResReqSnapShot: [code: " + code + ",contentType: " + contentType + ",createTime: " + createTime + ",siteId: " + siteId + ",baseUrl: " + baseUrl + ",content: " + content + "]");  
        return strBuf.toString(); 
	}
}

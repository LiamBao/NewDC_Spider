package com.cic.datacrawl.core.entity;

import com.cicdata.datacollection.storeservice.beans.json.JSONObject;

public class SnapshotBean {
	private byte contentType;		//1:html  2:xml
	private long createTime;		//快照创建时间
	private int siteId;				//站点ID
	private String baseUrl;			//网页URL
	private String content;			//网页内容
	
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
	
	public String toString(){
		net.sf.json.JSONObject jsonString = net.sf.json.JSONObject.fromObject(this);
		String content=jsonString.toString();
		return content;
	}
	
	public static SnapshotBean parseFromJson(String strJson) {
		try {
			SnapshotBean bean = null;
			JSONObject json = new JSONObject(strJson);
			if (json != null) {
				bean = new SnapshotBean();
				bean.setContentType((byte)(json.getInt("contentType")));
				bean.setCreateTime(json.getLong("createTime"));
				bean.setSiteId(json.getInt("siteId"));
				bean.setBaseUrl(json.getString("baseUrl"));
				bean.setContent(json.getString("content"));
			}
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

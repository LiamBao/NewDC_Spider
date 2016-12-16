package com.cic.datacollection.bean;

import com.cic.datacrawl.core.entity.BaseEntity;

/**
 * 该类对FeedBackWritable进行了解析，并处理后，返回给用户
 * FeedBackWritable是通讯机制返回出的信息
 */
public class FeedBack extends BaseEntity{
	
	private static final long serialVersionUID = -7184033774786067941L;
	private String name;
	public FeedBack(String name){
		this.name = name;
	}
	
	public int getCode() {
		return getInt("CODE");
	}

	public void setCode(int code) {
		set("CODE",code);
	}

	public String getErrorMessage() {
		return getString("ERRORMESSAGE");
	}

	public void setErrorMessage(String errorMessage) {
		set("ERRORMESSAGE", errorMessage);
	}

	@Override
	public BaseEntity getDefaultEmptyBean() {
		return null;
	}

	@Override
	public String getTheEntityName() {
		return name;
	}

	@Override
	protected String[] initColumns() {
		return null;
	}

	@Override
	protected String[] initCompareColumns() {
		return null;
	}
	
}

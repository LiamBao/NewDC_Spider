package com.cic.datacrawl.core.db.mongodb;

import java.util.List;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cicdata.datacollection.storeservice.beans.ws.UpdateBean;

public interface ISaveDataMongoDB {
	
	public void save(String tableName, BaseEntity[] entities) throws Exception;
	
	public void save(String tableName, BaseEntity[] entities,boolean ifTE) throws Exception;
	
	public int[] updateMongodb(String tableName,List<UpdateBean> beans)throws Exception;
	
	public BaseEntity[] find(String tableName,String query) throws Exception;
	
	public BaseEntity[] find(String tableName,String query, int maxResult) throws Exception;
	
	public BaseEntity[] find(String tableName,String query, int maxResult,String sort) throws Exception;
	
	public BaseEntity[] find(String tableName, String query, int fristResult,int maxResult,String sortStr) throws Exception;
	
	public long count(String tableName,String query)throws Exception ;

	public long[] ifExist(String tableName,BaseEntity[] entities) throws Exception;
	
	public long ifExist(String tableName,BaseEntity entity) throws Exception;
	
}

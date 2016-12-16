package com.cic.datacrawl.core.db.mongodb;
import java.util.List;

import jxl.common.Logger;

import com.cic.datacrawl.core.db.DbConnectionMongoDB;
import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.ArrayUtil;
import com.cicdata.datacollection.storeservice.beans.ws.UpdateBean;

public class SaveDataMongoDBImpl implements ISaveDataMongoDB {
	public SaveDataMongoDBImpl(){
		
	}
	private static final Logger LOG = Logger.getLogger(SaveDataMongoDBImpl.class);
	private DbConnectionMongoDB dbConnectionMongoDB ;

	private String dcStoreAddress;
	
	
	public void setDcStoreAddress(String dcStoreAddress) {
		this.dcStoreAddress = dcStoreAddress;
		dbConnectionMongoDB = new DbConnectionMongoDB(dcStoreAddress);
	}

	@Override
	public void save(String tableName, BaseEntity[] entities) throws Exception {
		save(tableName, entities,false);
	}
	
	@Override
	public void save(String tableName, BaseEntity[] entities, boolean ifTE)	throws Exception {
		if(ArrayUtil.isEmpty(entities))return;
		dbConnectionMongoDB.insert(tableName, entities,ifTE);
	}

	@Override
	public int[] updateMongodb(String tableName, List<UpdateBean> beans) throws Exception {
		return dbConnectionMongoDB.updateMongodb(tableName, beans);
	}
	
	@Override
	public BaseEntity[] find(String tableName, String query) throws Exception {
		return find(tableName, query,-1);
	}

	@Override
	public BaseEntity[] find(String tableName, String query, int maxResult) throws Exception {
		return find(tableName, query, maxResult, null);
	}

	@Override
	public BaseEntity[] find(String tableName, String query, int maxResult,	String sort) throws Exception {
		return find(tableName, query, 0, maxResult, sort);
	}

	@Override
	public BaseEntity[] find(String tableName, String query, int fristResult,int maxResult, String sort) throws Exception {
		return dbConnectionMongoDB.query(tableName, query, fristResult, maxResult, sort);
	}
	
	public DbConnectionMongoDB getDbConnectionMongoDB() {
		return dbConnectionMongoDB;
	}

	public void setDbConnectionMongoDB(DbConnectionMongoDB dbConnectionMongoDB) {
		this.dbConnectionMongoDB = dbConnectionMongoDB;
	}

	@Override
	public long count(String tableName, String query) throws Exception {
		return this.dbConnectionMongoDB.count(tableName, query);
	}

	@Override
	public long[] ifExist(String tableName, BaseEntity[] querys) throws Exception {
		long[] flags =  new long[querys.length];
		int index = 0;
		for(BaseEntity query:querys){
			long res = this.ifExist(tableName, query);
			flags[index] = res;
			index++;
		}
		return flags;
	}
	
	@Override
	public long ifExist(String tableName, BaseEntity entity) throws Exception {
		long ifExist = this.dbConnectionMongoDB.ifExist(tableName, entity);
		LOG.info("got count size:" + ifExist);
		return ifExist;
	}


}
